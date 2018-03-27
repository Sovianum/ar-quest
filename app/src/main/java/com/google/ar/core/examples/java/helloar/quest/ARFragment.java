package com.google.ar.core.examples.java.helloar.quest;


import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.examples.java.helloar.App;
import com.google.ar.core.examples.java.helloar.DisplayRotationHelper;
import com.google.ar.core.examples.java.helloar.GameModule;
import com.google.ar.core.examples.java.helloar.PermissionHelper;
import com.google.ar.core.examples.java.helloar.R;
import com.google.ar.core.examples.java.helloar.common.CollectionUtils;
import com.google.ar.core.examples.java.helloar.common.ContinuousAction;
import com.google.ar.core.examples.java.helloar.core.ar.SceneObject;
import com.google.ar.core.examples.java.helloar.core.ar.geom.Geom;
import com.google.ar.core.examples.java.helloar.core.game.InteractionArgument;
import com.google.ar.core.examples.java.helloar.core.game.InteractionResult;
import com.google.ar.core.examples.java.helloar.core.game.InteractiveObject;
import com.google.ar.core.examples.java.helloar.core.game.Item;
import com.google.ar.core.examples.java.helloar.core.game.Place;
import com.google.ar.core.examples.java.helloar.core.game.slot.Slot;
import com.google.ar.core.examples.java.helloar.quest.game.DeferredClickListener;
import com.google.ar.core.examples.java.helloar.quest.game.InteractionResultHandler;
import com.google.ar.core.examples.java.helloar.quest.game.RendererHelper;
import com.google.ar.core.examples.java.helloar.rendering.BackgroundRenderer;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ARFragment extends Fragment implements GLSurfaceView.Renderer   {
    public static final String TAG = ARFragment.class.getSimpleName();

    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    @BindView(R.id.surfaceview)
    GLSurfaceView surfaceView;

    @BindView(R.id.collision_txt)
    TextView collisionText;

    @BindView(R.id.inventory_btn)
    Button toInventoryBtn;

    @BindView(R.id.journal_btn)
    Button toJournalBtn;

    @BindView(R.id.interact_btn)
    Button interactBtn;

    @Inject
    GameModule gameModule;

    ContinuousAction snackbarAction = new ContinuousAction(
            new Runnable() {
                @Override
                public void run() {
                    ARFragment.this.showSnackbarMessage(getString(R.string.direct_camera_to_floor_str), false);
                    ARFragment.this.hideButtons();
                }
            },
            new Runnable() {
                @Override
                public void run() {
                    ARFragment.this.hideSnackbarMessage();
                    ARFragment.this.showButtons();
                }
            }
    );

    private boolean installRequested;

    private Session session;
    private Snackbar messageSnackbar;
    private DisplayRotationHelper displayRotationHelper;

    private final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    private RendererHelper rendererHelper;

    private View.OnClickListener toInventoryOnClickListener;

    private InteractionResultHandler interactionResultHandler;
    private List<SceneObject> collidedObjects = new ArrayList<>();
    private DeferredClickListener interactor = new DeferredClickListener() {
        private boolean needActualize = false;

        @Override
        public void actualize() {
            if (needActualize) {
                gameModule.getScene().getCollisions(gameModule.getPlayer().getCollider(), collidedObjects);
                if (interact() == null) {   // user intended to release item
                    gameModule.getPlayer().release();
                }

                collidedObjects.clear();
                needActualize = false;
            }
        }

        @Override
        public void onClick(View v) {
            needActualize = true;
        }
    };

    private View.OnClickListener toJournalOnClickListener;

    public ARFragment() {
        super();
        App.getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_ar, container, false);
        ButterKnife.bind(this, view);

        displayRotationHelper = new DisplayRotationHelper(getActivity());

        toInventoryBtn.setOnClickListener(toInventoryOnClickListener);
        toJournalBtn.setOnClickListener(toJournalOnClickListener);

        toInventoryBtn.setOnClickListener(toInventoryOnClickListener);
        interactBtn.setOnClickListener(interactor);

        // Set up renderer.
        surfaceView.setPreserveEGLContextOnPause(true);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        surfaceView.setRenderer(this);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        installRequested = false;
        interactionResultHandler = new InteractionResultHandler();

        snackbarAction.startIfNotRunning();


        return view;
    }

    public void setDecorations(Place place) {
        rendererHelper = new RendererHelper(gameModule.getScene());
        if (place != null) {
            gameModule.setCurrentPlace(place);
        }
    }

    public void setToInventoryOnClickListener(View.OnClickListener listener) {
        toInventoryOnClickListener = listener;
        if (toInventoryBtn != null) {
            toInventoryBtn.setOnClickListener(toInventoryOnClickListener);
        }
    }

    public void setToJournalOnClickListener(View.OnClickListener listener) {
        toJournalOnClickListener = listener;
        if (toJournalBtn != null) {
            toJournalBtn.setOnClickListener(toJournalOnClickListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // ARCore requires camera permissions to operate. If we did not yet obtain runtime
        // permission on Android M and above, now is a good time to ask the user for it.
        if (PermissionHelper.hasPermissions(getActivity())) {
            if (session != null) {
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
        }

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
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!PermissionHelper.hasPermissions(getActivity())) {
            Toast.makeText(getActivity(), "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                    .show();
            getActivity().finish();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        // Create the texture and pass it to ARCore session to be filled during update().
        backgroundRenderer.createOnGlThread(/*context=*/ getActivity());

        try {
            Place place = gameModule.getCurrentPlace();
            if (place == null) {
                return;
            }
            rendererHelper.allocateRenderers(getActivity(), place);
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
                if (!gameModule.getScene().isLoaded()) {
                    snackbarAction.startIfNotRunning();
                    Pose planeOrigin = getPlaneOrigin(frame);
                    if (planeOrigin != null) {
                        Place place = gameModule.getCurrentPlace();
                        if (place != null) {
                            gameModule.getScene().load(place.getAll(), planeOrigin);
                        }
                    }
                } else {
                    snackbarAction.stopIfRunning();
                    gameModule.getScene().update(session);
                }
            }

            // Draw background.
            backgroundRenderer.draw(frame);

            // If not tracking, don't draw 3d objects.
            if (camera.getTrackingState() == TrackingState.PAUSED) {
                return;
            }

            if (gameModule.getScene().isLoaded()) { // prevent drawing before scene loaded
                update(frame, camera);
            }
            interactor.actualize();

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
                            Activity activity = getActivity();
                            if (activity != null) {
                                activity.finish();
                            }
                        }
                    });
        }
        messageSnackbar.show();
    }

    private void hideSnackbarMessage() {
        if (messageSnackbar != null) {
            messageSnackbar.dismiss();
        }
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

    private void update(Frame frame, Camera camera) {
        Place place = gameModule.getCurrentPlace();
        if (place == null) {
            return;
        }
        rendererHelper.renderScene(frame, camera, place);
        gameModule.getPlayer().update(camera.getPose());
        Collection<SceneObject> collided = gameModule.getScene().getCollisions(gameModule.getPlayer().getCollider());
        final InteractiveObject closest = getClosestInteractive(collided);

        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Button btn = ARFragment.this.interactBtn;
                    if (closest != null) {
                        btn.setText(R.string.interact_str);
                        btn.setEnabled(true);
                    } else if (ARFragment.this.gameModule.getPlayer().getItem() != null &&
                            ARFragment.this.gameModule.getPlayer().getItem().getId() != Item.VOID_ID) {
                        btn.setText(R.string.release_str);
                        btn.setEnabled(true);
                    } else {
                        btn.setText(R.string.interact_str);
                        btn.setEnabled(false);
                    }

                }
            });
        }

        Item item = gameModule.getPlayer().getItem();
        if (item != null) {
            rendererHelper.renderObject(frame, camera, item);
        }
    }

    private Pose getPlaneOrigin(Frame frame) {
        Anchor origin = null;

        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }
        WindowManager manager = activity.getWindowManager();

        Display display = manager.getDefaultDisplay();
        android.graphics.Point size = new android.graphics.Point();
        display.getSize(size);

        for (HitResult hit : frame.hitTest(size.x / 2, size.y / 2)) { // todo check if correct screen position
            Trackable trackable = hit.getTrackable();

            if (trackable instanceof Plane) {
                origin = hit.createAnchor();
                break;
            }
        }
        if (origin != null) {
            return origin.getPose();
        }
        return null;
    }

    private void showButtons() {
        toInventoryBtn.setAlpha(1);
        toJournalBtn.setAlpha(1);
        interactBtn.setAlpha(1);
    }

    private void hideButtons() {
        toInventoryBtn.setAlpha(0);
        toJournalBtn.setAlpha(0);
        interactBtn.setAlpha(0);
    }

    private Collection<InteractionResult> interact() {
        final Activity activity = getActivity();
        if (activity == null) {
            return null;
        }

        collidedObjects.sort(new Comparator<SceneObject>() {
            @Override
            public int compare(SceneObject o1, SceneObject o2) {
                float d1 = Geom.distance(o1.getGeom(), gameModule.getPlayer().getGeom());
                float d2 = Geom.distance(o2.getGeom(), gameModule.getPlayer().getGeom());

                return Float.compare(d1, d2);
            }
        });

        InteractiveObject closestObject = getClosestInteractive(collidedObjects);
        if (closestObject == null) {
            return null;
        }

        InteractionArgument arg = new InteractionArgument(
                null,
                CollectionUtils.singleItemList(new Slot.RepeatedItem(gameModule.getPlayer().getItem()))
        );
        Collection<InteractionResult> results = closestObject.interact(arg);

        for (InteractionResult result : results) {
            interactionResultHandler.onInteractionResult(result, activity);
        }

        return results;
    }

    private InteractiveObject getClosestInteractive(Collection<SceneObject> objects) {
        Place place = gameModule.getCurrentPlace();
        if (place == null) {
            return null;
        }
        for (SceneObject object : objects) {
            InteractiveObject got = place.getInteractiveObject(object.getIdentifiable().getId());
            if (got != null) {
                return got;
            }
        }
        return null;
    }
}
