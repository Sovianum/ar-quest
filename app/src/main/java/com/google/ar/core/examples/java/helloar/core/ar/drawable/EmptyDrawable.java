package com.google.ar.core.examples.java.helloar.core.ar.drawable;

import com.google.ar.core.examples.java.helloar.core.ar.enabled.Enabled;

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
