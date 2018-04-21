package edu.technopark.arquest;

import android.app.Application;

import edu.technopark.arquest.network.NetworkModule;
import edu.technopark.arquest.network.ServerInfo;

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
                hintModule(new HintModule()).
                build();
    }
}
