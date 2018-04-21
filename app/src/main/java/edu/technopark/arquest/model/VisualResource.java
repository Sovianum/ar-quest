package edu.technopark.arquest.model;

import com.viro.core.Object3D;

public class VisualResource {
    private String modelUri;
    private String diffuseUri;
    private Object3D.Type type;

    public VisualResource(Object3D.Type type) {
        this.type = type;
    }

    public String getDiffuseUri() {
        return diffuseUri;
    }

    public VisualResource setTextureUri(String diffuseUri) {
        this.diffuseUri = diffuseUri;
        return this;
    }

    public String getModelUri() {
        return modelUri;
    }

    public VisualResource setModelUri(String modelUri) {
        this.modelUri = modelUri;
        return this;
    }

    public Object3D.Type getType() {
        return type;
    }

    public VisualResource setType(Object3D.Type type) {
        this.type = type;
        return this;
    }
}
