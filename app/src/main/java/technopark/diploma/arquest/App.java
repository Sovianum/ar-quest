package technopark.diploma.arquest;

import android.app.Application;

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
}
