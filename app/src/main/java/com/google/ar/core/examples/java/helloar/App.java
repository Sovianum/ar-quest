package com.google.ar.core.examples.java.helloar;

import android.app.Application;

import com.google.ar.core.examples.java.helloar.network.NetworkModule;
import com.google.ar.core.examples.java.helloar.network.ServerInfo;
import com.google.ar.core.examples.java.helloar.storage.fs.FileModule;

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
