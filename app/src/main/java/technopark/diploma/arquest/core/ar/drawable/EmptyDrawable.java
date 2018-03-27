package technopark.diploma.arquest.core.ar.drawable;

import technopark.diploma.arquest.core.ar.enabled.Enabled;

public class EmptyDrawable extends Enabled implements IDrawable {
    @Override
    public String getTextureName() {
        return null;
    }

    @Override
    public String getModelName() {
        return null;
    }
}
