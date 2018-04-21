package edu.technopark.arquest.core.ar.drawable;

import edu.technopark.arquest.core.ar.enabled.Enabled;

public class TextureDrawable extends Enabled implements IDrawable {
    private String textureName;
    private String modelName;

    public TextureDrawable(String modelName, String textureName) {
        this.textureName = textureName;
        this.modelName = modelName;
    }

    @Override
    public String getTextureName() {
        return textureName;
    }

    @Override
    public String getModelName() {
        return modelName;
    }
}
