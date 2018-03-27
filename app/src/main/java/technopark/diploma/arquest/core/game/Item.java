package technopark.diploma.arquest.core.game;

import technopark.diploma.arquest.core.ar.SceneObject;
import technopark.diploma.arquest.core.ar.drawable.IDrawable;
import technopark.diploma.arquest.core.ar.identifiable.Identifiable;

public class Item extends SceneObject implements IDrawable {
    public static final int VOID_ID = -1;
    public static final Item VOID = new Item(VOID_ID, "void", "", "", "");

    private final int id;
    private final String name;
    private final String description;
    private final String modelName;
    private final String textureName;

    public Item(int id, String name, String description, String modelName, String textureName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.modelName = modelName;
        this.textureName = textureName;

        setDrawable(this);
        setIdentifiable(new Identifiable(name, id));
        setEnabled(false);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    @Override
    public String getTextureName() {
        return textureName;
    }
}