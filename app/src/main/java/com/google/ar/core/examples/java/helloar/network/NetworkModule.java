package com.google.ar.core.examples.java.helloar.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.ar.core.examples.java.helloar.ListenerHandler;
import com.google.ar.core.examples.java.helloar.NetworkError;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {
    public interface OnDataGetListener<T> {
        void onSuccess(final T items);

        void onError(final Exception error);
    }

    private String token;
    private String baseUrl;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private final LoaderService service;

    public NetworkModule(String baseUrl) {
        this.baseUrl = baseUrl;
        okHttpClient = new OkHttpClient().newBuilder()
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        service = retrofit.create(LoaderService.class);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return okHttpClient;
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit() {
        return retrofit;
    }

    @Provides
    @Singleton
    NetworkModule provideNetworkModule() {
        return new NetworkModule(baseUrl);
    }

    public void setToken(String token) {
        this.token = token;
    }

    private <T> void handleDefaultSuccess(
            Response<ServerResponse<T>> response,
            ListenerHandler<OnDataGetListener<T>> handler,
            int expectedCode
    ) throws NetworkError {
        final ServerResponse<T> body = response.body();
        if (body == null) {
            throw new NetworkError(response.code());
        }
        if (body.getData() == null || response.code() != expectedCode) {
            throw new NetworkError(body.getErrMsg(), response.code());
        }
        invokeSuccess(handler, body.getData());
    }

    private <T> void
    invokeSuccess(final ListenerHandler<OnDataGetListener<T>> handler, final T payload) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnDataGetListener<T> listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null in invokeSuccess");
                    listener.onSuccess(payload);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    private <T> void
    invokeError(final ListenerHandler<OnDataGetListener<T>> handler, final Exception error) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnDataGetListener<T> listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null in invokeError");
                    listener.onError(error);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }
}
