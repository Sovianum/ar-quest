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
import android.widget.TextView;
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
import com.google.ar.core.examples.java.helloar.PermissionHelper;
import com.google.ar.core.examples.java.helloar.R;
import com.google.ar.core.examples.java.helloar.core.ar.Scene;
import com.google.ar.core.examples.java.helloar.core.ar.SceneObject;
import com.google.ar.core.examples.java.helloar.core.ar.collision.Collider;
import com.google.ar.core.examples.java.helloar.core.ar.collision.shape.Sphere;
import com.google.ar.core.examples.java.helloar.core.ar.drawable.IDrawable;
import com.google.ar.core.examples.java.helloar.core.game.InteractiveObject;
import com.google.ar.core.examples.java.helloar.core.game.Place;
import com.google.ar.core.examples.java.helloar.core.game.Player;
import com.google.ar.core.examples.java.helloar.core.game.journal.Journal;
import com.google.ar.core.examples.java.helloar.core.game.map.RoadMap;
import com.google.ar.core.examples.java.helloar.core.game.slot.Slot;
import com.google.ar.core.examples.java.helloar.quest.game.QuestService;
import com.google.ar.core.examples.java.helloar.rendering.BackgroundRenderer;
import com.google.ar.core.examples.java.helloar.rendering.ObjectRenderer;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ARFragment extends Fragment implements GLSurfaceView.Renderer   {
    private static final String TAG = ARFragment.class.getSimpleName();

    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private GLSurfaceView surfaceView;
    private Button toggleBtn;
    private Button toInventoryBtn;
    private Button toJournalBtn;
    private Button releaseBtn;
    private Button toQuestFragmentBtn;
    private TextView collisionText;

    private boolean installRequested;

    private Session session;
    private GestureDetector gestureDetector;
    private Snackbar messageSnackbar;
    private DisplayRotationHelper displayRotationHelper;

    private final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();

    // Temporary matrix allocated here to reduce number of allocations for each frame.
    private final float[] anchorMatrix = new float[16];

    // Tap handling and UI.
    private final ArrayBlockingQueue<MotionEvent> queuedSingleTaps = new ArrayBlockingQueue<>(16);

    private Scene scene;
    private Map<String, ObjectRenderer> renderers;
    private Place place;
    private InteractiveObject andy;
    private InteractiveObject rose;
    private InteractiveObject banana;

    private View.OnClickListener toInventoryOnClickListener;
    private View.OnClickListener toJouranlOnClickListener;
    private Player player;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_ar, container, false);

        surfaceView = view.findViewById(R.id.surfaceview);
        displayRotationHelper = new DisplayRotationHelper(/*context=*/ getActivity());

        collisionText = view.findViewById(R.id.collision_txt);
        toInventoryBtn = view.findViewById(R.id.inventory_btn);
        toInventoryBtn.setOnClickListener(toInventoryOnClickListener);
        toJournalBtn = view.findViewById(R.id.journal_btn);
        toJournalBtn.setOnClickListener(toJouranlOnClickListener);
        releaseBtn = view.findViewById(R.id.release_btn);

        // Set up tap listener.
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

        player = new Player(new Slot(0, "inv", false), new RoadMap(), new Journal<String>());
        player.setCollider(new Collider(new Sphere(0.05f)));

        return view;
    }

    public void setToInventoryOnClickListener(View.OnClickListener lister) {
        this.toInventoryOnClickListener = lister;
        if (toInventoryBtn != null) {
            toInventoryBtn.setOnClickListener(toInventoryOnClickListener);
        }
    }

    public void setToJournalOnClickListener(View.OnClickListener lister) {
        this.toJouranlOnClickListener = lister;
        if (toJournalBtn != null) {
            toJournalBtn.setOnClickListener(toJouranlOnClickListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // ARCore requires camera permissions to operate. If we did not yet obtain runtime
        // permission on Android M and above, now is a good time to ask the user for it.
        if (PermissionHelper.hasPermissions(getActivity())) {
            if (session != null) {
//                showLoadingMessage();
                // Note that order matters - see the note in onPause(), the reverse applies here.
                session.resume();
            }
            surfaceView.onResume();
            displayRotationHelper.onResume();
        } else {
            PermissionHelper.requestPermissions(getActivity());
        }

        if (session == null) {
            configureSession();
            scene = new Scene();
        }

        //showLoadingMessage();
        // Note that order matters - see the note in onPause(), the reverse applies here.
        session.resume();
        surfaceView.onResume();
        displayRotationHelper.onResume();

        place = QuestService.getDemoPlace();
        andy = place.getAccessibleInteractiveObjects().get(1);
        rose = place.getAccessibleInteractiveObjects().get(2);
        banana = place.getAccessibleInteractiveObjects().get(3);

        scene.load(place.getAll(), Pose.makeTranslation(0, 0, -1));
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

        try {
            allocateRenderers();
        } catch (IOException e) {
            Log.e(TAG, "Failed to read obj file");
        }
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
                scene.update(session);
            }

            // Draw background.
            backgroundRenderer.draw(frame);

            // If not tracking, don't draw 3d objects.
            if (camera.getTrackingState() == TrackingState.PAUSED) {
                return;
            }

            renderScene(frame, camera);
            updatePlayer(camera);
            Collection<SceneObject> collided = scene.getCollisions(player.getCollider());
            StringBuilder s = new StringBuilder("Camera: " + camera.getPose().toString() + "\n");
            s.append("Andy: " + scene.getAnchorMap().get(andy.getIdentifiable().getSceneID()).getPose().toString() + " " + getColliderRadius(andy) + "\n");
            s.append("Rose: " + scene.getAnchorMap().get(rose.getIdentifiable().getSceneID()).getPose().toString() + " " + getColliderRadius(rose) + "\n");
            s.append("Banana: " + scene.getAnchorMap().get(banana.getIdentifiable().getSceneID()).getPose().toString() + " " + getColliderRadius(banana) + "\n");
            s.append("Collided: ");
            for (SceneObject sceneObject : collided) {
                s.append(sceneObject.getIdentifiable().getName()).append(" ");
            }

            final String str = s.toString();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    collisionText.setText(str);
                }
            });

        } catch (Throwable t) {
            // Avoid crashing the application due to unhandled exceptions.
            Log.e(TAG, "Exception on the OpenGL thread", t);
        }
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

    private void configureSession() {
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

    private void allocateRenderers() throws IOException {
        renderers = new HashMap<>();
        for (SceneObject sceneObject : place.getAll()) {
            String name = sceneObject.getIdentifiable().getName();
            if (!renderers.containsKey(name)) {
                IDrawable drawable = sceneObject.getDrawable();
                ObjectRenderer objectRenderer = new ObjectRenderer();
                objectRenderer.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
                objectRenderer.createOnGlThread(getActivity(), drawable.getModelName(), drawable.getTextureName());
                renderers.put(name, objectRenderer);
            }
        }
    }

    private void renderScene(Frame frame, Camera camera) {
        // Get projection matrix.
        float[] projmtx = new float[16];
        camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f);

        // Get camera matrix and draw.
        float[] viewmtx = new float[16];
        camera.getViewMatrix(viewmtx, 0);

        // Compute lighting from average intensity of the image.
        final float lightIntensity = frame.getLightEstimate().getPixelIntensity();

        ObjectRenderer renderer;
        Anchor anchor;

        for (SceneObject sceneObject : place.getAll()) {
            if (!sceneObject.isEnabled()) {
                continue;
            }
            renderer = renderers.get(sceneObject.getIdentifiable().getName());
            if (renderer == null) {
                continue;
            }
            anchor = scene.getAnchorMap().get(sceneObject.getIdentifiable().getSceneID());
            if (anchor == null) {
                continue;
            }
            anchor.getPose().toMatrix(anchorMatrix, 0);
            renderer.updateModelMatrix(anchorMatrix, sceneObject.getGeom().getScale());
            renderer.draw(viewmtx, projmtx, lightIntensity);
        }
    }

    private void updatePlayer(Camera camera) {
        player.getGeom().applyGlobal(camera.getPose());
    }

    private static String getColliderRadius(SceneObject sceneObject) {
        return String.valueOf(
                ((Sphere) sceneObject.getCollider().getShape()).getRadius()
        );
    }
}
