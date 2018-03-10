package com.google.ar.core.examples.java.helloar.core.ar.drawable;

import com.google.ar.core.examples.java.helloar.core.ar.enabled.IEnabled;

public interface IDrawable extends IEnabled {
    String getTextureName();
    String getModelName();
}
