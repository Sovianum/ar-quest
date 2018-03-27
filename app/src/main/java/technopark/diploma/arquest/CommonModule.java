package technopark.diploma.arquest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import technopark.diploma.arquest.core.ar.collision.shape.Shape;
import technopark.diploma.arquest.core.ar.collision.shape.Sphere;
import technopark.diploma.arquest.core.game.InteractiveObject;
import technopark.diploma.arquest.core.game.Item;

@Module
public class CommonModule {
    private Gson gson;

    public CommonModule() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(
                Shape.class,
                new JsonDeserializer<Shape>() {
                    @Override
                    public Shape deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        try {
                            return new Sphere(
                                    json.getAsJsonObject().get("radius").getAsFloat()
                            );
                        } catch (NullPointerException e){
                            return new Sphere(0);
                        }
                    }
                }
        );
        // todo make smth more generic
        final Gson defaultGson = gsonBuilder.create();

        gsonBuilder.registerTypeAdapter(
                Item.class,
                new JsonDeserializer<Item>() {
                    @Override
                    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        Item item = defaultGson.fromJson(json, Item.class);
                        JsonObject jsonObject = json.getAsJsonObject();
                        item.getGeom().setScale(jsonObject.get("geom").getAsJsonObject().get("scale").getAsFloat());
                        return item;
                    }
                }
        );
        gsonBuilder.registerTypeAdapter(
                InteractiveObject.class,
                new JsonDeserializer<InteractiveObject>() {
                    @Override
                    public InteractiveObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        InteractiveObject interactiveObject = defaultGson.fromJson(json, InteractiveObject.class);
                        interactiveObject.setAction(interactiveObject.getActionFromStates());
                        return interactiveObject;
                    }
                }
        );
        gson = gsonBuilder.create();
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        return gson;
    }
}
