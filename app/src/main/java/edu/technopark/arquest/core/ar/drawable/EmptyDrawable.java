package edu.technopark.arquest.core.ar.drawable;

import edu.technopark.arquest.core.ar.enabled.Enabled;

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
