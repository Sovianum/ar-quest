package technopark.diploma.arquest.quest.game;

import android.content.Context;

import com.google.ar.core.Camera;
import com.google.ar.core.Frame;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import technopark.diploma.arquest.GameModule;
import technopark.diploma.arquest.core.ar.Scene;
import technopark.diploma.arquest.core.ar.SceneObject;
import technopark.diploma.arquest.core.ar.drawable.TextureDrawable;
import technopark.diploma.arquest.core.game.Place;
import technopark.diploma.arquest.rendering.ObjectRenderer;
import technopark.diploma.arquest.storage.fs.FileModule;
import technopark.diploma.arquest.storage.fs.QuestDir;

public class RendererHelper {
    private final float[] anchorMatrix = new float[16];
    private final float[] viewmtx = new float[16];
    private final float[] projmtx = new float[16];
    private Map<String, ObjectRenderer> renderers;
    private Scene scene;
    private GameModule gameModule;
    private FileModule fileModule;
    private float lightIntensity;

    public RendererHelper(GameModule gameModule, FileModule fileModule) {
        this.scene = gameModule.getScene();
        this.gameModule = gameModule;
        this.fileModule = fileModule;
    }

    public Map<String, ObjectRenderer> getRenderers() {
        return renderers;
    }

    public void setRenderers(Map<String, ObjectRenderer> renderers) {
        this.renderers = renderers;
    }

    public void renderObject(Frame frame, Camera camera, SceneObject sceneObject) {
        initInner(frame, camera);
        renderInner(sceneObject);
    }

    public void renderScene(Frame frame, Camera camera, Place place) {
        initInner(frame, camera);
        for (SceneObject sceneObject : place.getAll()) {
            renderInner(sceneObject);
        }
    }

    public void allocateRenderers(Context context, Place place) throws IOException {
        renderers = new HashMap<>();
        for (SceneObject sceneObject : place.getAll()) {
            String name = sceneObject.getIdentifiable().getName();
            if (!renderers.containsKey(name)) {
                TextureDrawable drawable = sceneObject.getDrawable();
                ObjectRenderer objectRenderer = new ObjectRenderer();
                objectRenderer.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
                QuestDir questDir = fileModule.getQuestDir(gameModule.getCurrentQuest().getId());
                objectRenderer.createOnGlThread(
                        context,
                        questDir.loadObjAsset(drawable.getModelName()),
                        questDir.loadBitmapAsset(drawable.getTextureName())
                );
                renderers.put(name, objectRenderer);
            }
        }
    }

    private void initInner(Frame frame, Camera camera) {
        // Get projection matrix.
        camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f);

        // Get camera matrix and draw.
        camera.getViewMatrix(viewmtx, 0);

        // Compute lighting from average intensity of the image.
        lightIntensity = frame.getLightEstimate().getPixelIntensity();
    }

    private void renderInner(SceneObject sceneObject) {
        if (sceneObject == null || !sceneObject.isEnabled()) {
            return;
        }
        ObjectRenderer renderer = renderers.get(sceneObject.getIdentifiable().getName());
        if (renderer == null) {
            return;
        }
        sceneObject.getGeom().getPose().toMatrix(anchorMatrix, 0);
        renderer.updateModelMatrix(anchorMatrix, sceneObject.getGeom().getScale());
        renderer.draw(viewmtx, projmtx, lightIntensity);
    }
}
