package com.google.ar.core.examples.java.helloar.quest;


import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.examples.java.helloar.DisplayRotationHelper;
import com.google.ar.core.examples.java.helloar.HelloArActivity;
import com.google.ar.core.examples.java.helloar.PermissionHelper;
import com.google.ar.core.examples.java.helloar.R;
import com.google.ar.core.examples.java.helloar.rendering.BackgroundRenderer;
import com.google.ar.core.examples.java.helloar.rendering.ObjectRenderer;
import com.google.ar.core.examples.java.helloar.rendering.PlaneRenderer;
import com.google.ar.core.examples.java.helloar.rendering.PointCloudRenderer;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ARFragment extends Fragment implements GLSurfaceView.Renderer   {
    private static final String TAG = HelloArActivity.class.getSimpleName();

    public static final float THRESHOLD_DISTANCE = 0.1f;
    public static final float SPHERE_RADIUS = 1f;
    public static final int ANDROID_CNT = 3;
    private static final float DEFAULT_TARGET_SCALE = 1f;
    private static final float TRIGGER_TARGET_SCALE = 2f;

    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private GLSurfaceView surfaceView;
    private Button toggleBtn;
    private Button grabBtn;
    private Button releaseBtn;

    private boolean installRequested;

    private Session session;
    private GestureDetector gestureDetector;
    private Snackbar messageSnackbar;
    private DisplayRotationHelper displayRotationHelper;

    private final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    private final ObjectRenderer virtualObject = new ObjectRenderer();
    private final ObjectRenderer virtualObjectShadow = new ObjectRenderer();
    private final PlaneRenderer planeRenderer = new PlaneRenderer();
    private final PointCloudRenderer pointCloud = new PointCloudRenderer();

    // Temporary matrix allocated here to reduce number of allocations for each frame.
    private final float[] anchorMatrix = new float[16];

    // Tap handling and UI.
    private final ArrayBlockingQueue<MotionEvent> queuedSingleTaps = new ArrayBlockingQueue<>(16);

    private Anchor cameraAnchor = null;
    private int grabId = -1;
    private boolean needShow = false;

    private List<Anchor> randomAnchors = new ArrayList<>(ANDROID_CNT);
    private List<Float> targetScales = new ArrayList<>(ANDROID_CNT);
    private int anchorCnt = 0;
    private Anchor sphereOrigin = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        for (int i = 0; i != ANDROID_CNT; ++i) {
            randomAnchors.add(null);
            targetScales.add(DEFAULT_TARGET_SCALE);
        }

        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_ar, container, false);

        surfaceView = view.findViewById(R.id.surfaceview);
        displayRotationHelper = new DisplayRotationHelper(/*context=*/ getActivity());

        toggleBtn = view.findViewById(R.id.toggle_btn);
        toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                needShow = !needShow;
            }
        });

        grabBtn = view.findViewById(R.id.grab_btn);
        grabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grab();
            }
        });

        releaseBtn = view.findViewById(R.id.release_btn);
        releaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                release();
            }
        });

        gestureDetector =
                new GestureDetector(
                        getActivity(),
                        new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onSingleTapUp(MotionEvent e) {
                                onSingleTap(e);
                                return true;
                            }

                            @Override
                            public boolean onDown(MotionEvent e) {
                                return true;
                            }
                        });

        surfaceView.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return gestureDetector.onTouchEvent(event);
                    }
                });

        // Set up renderer.
        surfaceView.setPreserveEGLContextOnPause(true);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        surfaceView.setRenderer(this);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        installRequested = false;

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();


        // ARCore requires camera permissions to operate. If we did not yet obtain runtime
        // permission on Android M and above, now is a good time to ask the user for it.
        if (PermissionHelper.hasPermissions(getActivity())) {
            if (session != null) {
                showLoadingMessage();
                // Note that order matters - see the note in onPause(), the reverse applies here.
                session.resume();
            }
            surfaceView.onResume();
            displayRotationHelper.onResume();
        } else {
            PermissionHelper.requestPermissions(getActivity());
        }

        if (session == null) {
            Exception exception = null;
            String message = null;
            try {
                switch (ArCoreApk.getInstance().requestInstall(getActivity(), !installRequested)) {
                    case INSTALL_REQUESTED:
                        installRequested = true;
                        return;
                    case INSTALLED:
                        break;
                }

                // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                // permission on Android M and above, now is a good time to ask the user for it.
                if (!PermissionHelper.hasPermissions(getActivity())) {
                    PermissionHelper.requestPermissions(getActivity());
                    return;
                }

                session = new Session(/* context= */ getActivity());
            } catch (UnavailableArcoreNotInstalledException
                    | UnavailableUserDeclinedInstallationException e) {
                message = "Please install ARCore";
                exception = e;
            } catch (UnavailableApkTooOldException e) {
                message = "Please update ARCore";
                exception = e;
            } catch (UnavailableSdkTooOldException e) {
                message = "Please update this app";
                exception = e;
            } catch (Exception e) {
                message = "This device does not support AR";
                exception = e;
            }

            if (message != null) {
                showSnackbarMessage(message, true);
                Log.e(TAG, "Exception creating session", exception);
                return;
            }

            // Create default config and check if supported.
            Config config = new Config(session);
            if (!session.isSupported(config)) {
                showSnackbarMessage("This device does not support AR", true);
            }
            session.configure(config);
        }

//        showLoadingMessage();
        // Note that order matters - see the note in onPause(), the reverse applies here.
        session.resume();
        surfaceView.onResume();
        displayRotationHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (session != null) {
            // Note that the order matters - GLSurfaceView is paused first so that it does not try
            // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
            // still call session.update() and get a SessionPausedException.
            displayRotationHelper.onPause();
            surfaceView.onPause();
            session.pause();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!PermissionHelper.hasPermissions(getActivity())) {
            Toast.makeText(getActivity(), "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                    .show();
            getActivity().finish();
        }
    }

    private void onSingleTap(MotionEvent e) {
        // Queue tap if there is space. Tap is lost if queue is full.
        queuedSingleTaps.offer(e);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        // Create the texture and pass it to ARCore session to be filled during update().
        backgroundRenderer.createOnGlThread(/*context=*/ getActivity());

        // Prepare the other rendering objects.
        try {
            virtualObject.createOnGlThread(/*context=*/ getActivity(), "andy.obj", "andy.png");
            virtualObject.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);

            virtualObjectShadow.createOnGlThread(/*context=*/ getActivity(), "andy_shadow.obj", "andy_shadow.png");
            virtualObjectShadow.setBlendMode(ObjectRenderer.BlendMode.Shadow);
            virtualObjectShadow.setMaterialProperties(1.0f, 0.0f, 0.0f, 1.0f);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read obj file");
        }
        try {
            planeRenderer.createOnGlThread(/*context=*/ getActivity(), "trigrid.png");
        } catch (IOException e) {
            Log.e(TAG, "Failed to read plane texture");
        }
        pointCloud.createOnGlThread(/*context=*/ getActivity());
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        displayRotationHelper.onSurfaceChanged(width, height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        // Clear screen to notify driver it should not load any pixels from previous frame.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (session == null) {
            return;
        }
        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        displayRotationHelper.updateSessionIfNeeded(session);

        try {
            session.setCameraTextureName(backgroundRenderer.getTextureId());

            // Obtain the current frame from ARSession. When the configuration is set to
            // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
            // camera framerate.
            Frame frame = session.update();
            Camera camera = frame.getCamera();

            if (camera.getTrackingState() == TrackingState.TRACKING) {
                checkAllCollisions();
                if (cameraAnchor != null) {
                    cameraAnchor.detach();
                }
                cameraAnchor = getCameraFloatingPoint(session, frame);
                if (grabId >= 0) {
                    Anchor a = randomAnchors.get(grabId);
                    a.detach();
                    randomAnchors.set(grabId, cameraAnchor);
                }

                if (sphereOrigin == null) {
                    sphereOrigin = session.createAnchor(camera.getPose());
                    for (int i = 0; i != ANDROID_CNT; ++i) {
                        Pose pose = sphereOrigin.getPose().compose(getRandomOffsetPose()).extractTranslation();
                        setNextAnchor(session.createAnchor(pose));
                    }
                }
            }

            // Draw background.
            backgroundRenderer.draw(frame);

            // If not tracking, don't draw 3d objects.
            if (camera.getTrackingState() == TrackingState.PAUSED) {
                return;
            }

            // Get projection matrix.
            float[] projmtx = new float[16];
            camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f);

            // Get camera matrix and draw.
            float[] viewmtx = new float[16];
            camera.getViewMatrix(viewmtx, 0);

            // Compute lighting from average intensity of the image.
            final float lightIntensity = frame.getLightEstimate().getPixelIntensity();

//            // Check if we detected at least one plane. If so, hide the loading message.
//            if (messageSnackbar != null) {
//                for (Plane plane : session.getAllTrackables(Plane.class)) {
//                    if (plane.getType() == com.google.ar.core.Plane.Type.HORIZONTAL_UPWARD_FACING
//                            && plane.getTrackingState() == TrackingState.TRACKING) {
//                        hideLoadingMessage();
//                        break;
//                    }
//                }
//            }

            // Visualize anchors created by touch.
            for (int i = 0; i != randomAnchors.size(); ++i) {
                Anchor anchor = randomAnchors.get(i);
                if (anchor == null || anchor.getTrackingState() != TrackingState.TRACKING) {
                    continue;
                }
                // Get the current pose of an Anchor in world space. The Anchor pose is updated
                // during calls to session.update() as ARCore refines its estimate of the world.
                anchor.getPose().toMatrix(anchorMatrix, 0);

                // Update and draw the model and its shadow.
                virtualObject.updateModelMatrix(anchorMatrix, targetScales.get(i));
                virtualObjectShadow.updateModelMatrix(anchorMatrix, targetScales.get(i));
                virtualObject.draw(viewmtx, projmtx, lightIntensity);
                virtualObjectShadow.draw(viewmtx, projmtx, lightIntensity);
            }

            float scaleFactor = 0.3f;
            if (cameraAnchor != null && needShow) {
                cameraAnchor.getPose().toMatrix(anchorMatrix, 0);

                virtualObject.updateModelMatrix(anchorMatrix, scaleFactor);
                virtualObjectShadow.updateModelMatrix(anchorMatrix, scaleFactor);
                virtualObject.draw(viewmtx, projmtx, lightIntensity);
                virtualObjectShadow.draw(viewmtx, projmtx, lightIntensity);
            }

        } catch (Throwable t) {
            // Avoid crashing the application due to unhandled exceptions.
            Log.e(TAG, "Exception on the OpenGL thread", t);
        }
    }

    private void setNextAnchor(Anchor anchor) {
        int index = anchorCnt++ % ANDROID_CNT;
        Anchor oldAnchor = randomAnchors.get(index);
        if (oldAnchor != null) {
            oldAnchor.detach();
        }
        randomAnchors.set(index, anchor);
        targetScales.set(index, DEFAULT_TARGET_SCALE);
    }

    private void checkAllCollisions() {
        if (!needShow) {
            return;
        }
        for (int i = 0; i != randomAnchors.size(); i++) {
            Anchor anchor = randomAnchors.get(i);
            if (anchor == null) {
                return;
            }

            if (detectCollision(cameraAnchor, anchor)) {
                targetScales.set(i, TRIGGER_TARGET_SCALE);
            } else {
                targetScales.set(i, 1f);
            }
        }
    }

    private void grab() {
        if (grabId >= 0) {
            return;
        }
        for (int i = 0; i != ANDROID_CNT; ++i) {
            Anchor a = randomAnchors.get(i);
            if (detectCollision(cameraAnchor, a)) {
                grabId = i;
                Toast.makeText(getActivity(), "Grabbed", Toast.LENGTH_SHORT).show();
            }
        }
        Toast.makeText(getActivity(), "You are to far", Toast.LENGTH_SHORT).show();
    }

    private void release() {
        if (grabId < 0) {
            return;
        }
        randomAnchors.set(grabId, session.createAnchor(cameraAnchor.getPose()));
        grabId = -1;
        Toast.makeText(getActivity(), "Released", Toast.LENGTH_SHORT).show();
    }

    private boolean detectCollision(Anchor anchor1, Anchor anchor2) {
        float distance = getDistance(anchor1, anchor2);
        return distance < THRESHOLD_DISTANCE;
    }

    private float getDistance(Anchor anchor1, Anchor anchor2) {
        float[] v1 = anchor1.getPose().transformPoint(new float[]{0, 0, 0});
        float[] v2 = anchor2.getPose().transformPoint(new float[]{0 ,0, 0});

        float dist = 0;
        for (int i = 0; i != v1.length; ++i) {
            float d = v2[i] - v1[i];
            dist += d * d;
        }
        return (float) Math.sqrt(dist);
    }

    private Anchor getCameraFloatingPoint(Session session, Frame frame) {
        Camera camera = frame.getCamera();
        Pose pose = camera.getPose().compose(Pose.makeTranslation(0f, 0f, -1f)).extractTranslation();
        return session.createAnchor(pose);
    }

    private Pose getRandomOffsetPose() {
        return Pose.makeTranslation(
                SPHERE_RADIUS * (float) (Math.random() - 0.5),
                SPHERE_RADIUS * (float) (Math.random() - 0.5),
                SPHERE_RADIUS * (float) (Math.random() - 0.5)
        );
    }

    private void showSnackbarMessage(String message, boolean finishOnDismiss) {
        messageSnackbar =
                Snackbar.make(
                        getActivity().findViewById(android.R.id.content),
                        message,
                        Snackbar.LENGTH_INDEFINITE);
        messageSnackbar.getView().setBackgroundColor(0xbf323232);
        if (finishOnDismiss) {
            messageSnackbar.setAction(
                    "Dismiss",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            messageSnackbar.dismiss();
                        }
                    });
            messageSnackbar.addCallback(
                    new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            getActivity().finish();
                        }
                    });
        }
        messageSnackbar.show();
    }

    private void showLoadingMessage() {
        getActivity().runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        showSnackbarMessage("Searching for surfaces...", false);
                    }
                });
    }

    private void hideLoadingMessage() {
        getActivity().runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (messageSnackbar != null) {
                            messageSnackbar.dismiss();
                        }
                        messageSnackbar = null;
                    }
                });
    }
}
