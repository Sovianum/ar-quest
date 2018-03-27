package technopark.diploma.arquest;

import android.app.Application;
import android.content.Context;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.util.Collection;

import technopark.diploma.arquest.common.CollectionUtils;
import technopark.diploma.arquest.network.NetworkModule;
import technopark.diploma.arquest.network.ServerInfo;
import technopark.diploma.arquest.storage.fs.FileModule;

public class App extends Application {

    private static AppComponent appComponent;

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.
                builder().
                gameModule(new GameModule()).
                networkModule(new NetworkModule(ServerInfo.BACKEND_URL)).
                contextModule(new ContextModule(this)).
                commonModule(new CommonModule()).
                fileModule(new FileModule(this)).
                build();
    }

    public static void copyAssets(Context context, int questID) throws IOException {
        final FileModule fileModule = new FileModule(context);

        Collection<String> names = CollectionUtils.listOf(
                "andy.obj", "andy.png",
                "andy_shadow.obj", "andy_shadow.png",
                "banana.obj", "banana.jpg",
                "bigmax.obj", "bigmax.jpg",
                "house.obj", "house.jpg",
                "rose.obj", "rose.jpg",
                "trigrid.png"
        );
        for (String name : names) {
            fileModule.getQuestDir(questID).saveAsset(ByteStreams.toByteArray(context.getAssets().open(name)), name);
        }
    }
}
