package edu.technopark.arquest.quest;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.viro.core.AsyncObject3DListener;
import com.viro.core.Material;
import com.viro.core.Object3D;
import com.viro.core.Texture;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import edu.technopark.arquest.App;
import edu.technopark.arquest.game.Identifiable3D;
import edu.technopark.arquest.game.Place;
import edu.technopark.arquest.model.VisualResource;

@Module

public class AssetModule {
    @Inject
    Context context;

    @Provides
    @Singleton
    public AssetModule provideAssetModule() {
        App.getAppComponent().inject(this);
        return this;
    }

    public static final String TAG = AssetModule.class.getSimpleName();

    public void loadPlace(Place place) {
        for (Identifiable3D identifiable3D : place.getAll()) {
            loadModel(identifiable3D, identifiable3D.getVisualResource());
        }
    }

    public void loadModel(Object3D object3D, VisualResource visualResource) {
        Object3D.Type type = visualResource.getType();
        switch (type) {
            case FBX:
               loadFBX(object3D, visualResource);
                return;
            case OBJ:
                loadOBJ(object3D, visualResource);
        }
    }

    private void loadOBJ(Object3D object3D, final VisualResource visualResource) {
        if (object3D == null || visualResource == null) {
            return;
        }
        object3D.loadModel(Uri.parse(visualResource.getModelUri()), visualResource.getType(), new AsyncObject3DListener() {
            @Override
            public void onObject3DLoaded(final Object3D object, final Object3D.Type type) {
                // When the model is loaded, set the texture associated with this OBJ
                String diffuse = visualResource.getDiffuseUri();
                if (diffuse == null || diffuse.equals("")) return;

                Bitmap bitmap = getBitmapFromAsset(diffuse);
                if (bitmap == null) {
                    return;
                }
                Texture objectTexture = new Texture(bitmap, Texture.Format.RGBA8, false, false);
                Material material = new Material();
                material.setDiffuseTexture(objectTexture);
                object.getGeometry().setMaterials(Collections.singletonList(material));
            }

            @Override
            public void onObject3DFailed(String s) {
                Log.e(TAG, "Unable to load model Error: " + s);
            }
        });
    }

    private void loadFBX(Object3D object3D, VisualResource visualResource) {
        object3D.loadModel(Uri.parse(visualResource.getModelUri()), visualResource.getType(), new AsyncObject3DListener() {
            @Override
            public void onObject3DLoaded(final Object3D object, final Object3D.Type type) {}

            @Override
            public void onObject3DFailed(String s) {
                Log.e(TAG, "Unable to load model Error: " + s);
            }
        });
    }

    private Bitmap getBitmapFromAsset(String assetName) {
        AssetManager assetManager = context.getResources().getAssets();
        InputStream imageStream;
        try {
            imageStream = assetManager.open(assetName);
        } catch (IOException exception) {
            Log.w(TAG, "Unable to find image [" + assetName + "] in assets! Error: "
                    + exception.getMessage());
            return null;
        }
        return BitmapFactory.decodeStream(imageStream);
    }
}
