package technopark.diploma.arquest.core.ar.drawable;

import technopark.diploma.arquest.core.ar.enabled.Enabled;

public class TextureDrawable extends Enabled {
    private String textureName;
    private String modelName;

    public TextureDrawable(String modelName, String textureName) {
        this.textureName = textureName;
        this.modelName = modelName;
    }

    public String getTextureName() {
        return textureName;
    }

    public String getModelName() {
        return modelName;
    }
}
