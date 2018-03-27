package technopark.diploma.arquest;

import technopark.diploma.arquest.core.ar.collision.shape.Shape;
import technopark.diploma.arquest.core.ar.collision.shape.Sphere;
import technopark.diploma.arquest.core.ar.drawable.IDrawable;
import technopark.diploma.arquest.core.ar.drawable.TextureDrawable;
import technopark.diploma.arquest.core.game.Item;
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
                        return new Sphere(
                                json.getAsJsonObject().get("radius").getAsFloat()
                        );
                    }
                }
        );
        gsonBuilder.registerTypeAdapter(
                IDrawable.class,
                new JsonDeserializer<IDrawable>() {
                    @Override
                    public IDrawable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        JsonObject jsonObject = json.getAsJsonObject();
                        return new TextureDrawable(
                                jsonObject.get("modelName").getAsString(),
                                jsonObject.get("textureName").getAsString()
                        );
                    }
                }
        );
        gsonBuilder.registerTypeAdapter(
                Item.class,
                new JsonDeserializer<Item>() {
                    @Override
                    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        JsonObject jsonObject = json.getAsJsonObject();
                        Item item = new Item(
                                jsonObject.get("id").getAsInt(),
                                jsonObject.get("name").getAsString(),
                                jsonObject.get("description").getAsString(),
                                jsonObject.get("modelName").getAsString(),
                                jsonObject.get("textureName").getAsString()
                        );
                        item.getGeom().setScale(jsonObject.get("geom").getAsJsonObject().get("scale").getAsFloat());
                        return item;
                    }
                }
        );
        gson = gsonBuilder.create();
    }

    @Provides
    @Singleton
    public Gson getGson() {
        return gson;
    }
}
