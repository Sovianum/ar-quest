package com.google.ar.core.examples.java.helloar.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.ar.core.examples.java.helloar.ListenerHandler;
import com.google.ar.core.examples.java.helloar.NetworkError;
import com.google.ar.core.examples.java.helloar.storage.CheckpointsStorage;
import com.google.ar.core.examples.java.helloar.storage.Inventories;
import com.google.ar.core.examples.java.helloar.storage.Journals;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {
    public static final String TEXT_PLAIN = "text/plain";

    private static final Api INSTANCE = new Api();
    private static final Gson GSON = new GsonBuilder().create();
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final LoaderService service;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private String token;
    private static Journals journals;
    private static Inventories inventories;
    private static CheckpointsStorage checkpointsStorage;
    private static Integer currentQuestId;

    private Api() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerInfo.BACKEND_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        service = retrofit.create(LoaderService.class);

        journals = new Journals();
        inventories = new Inventories();
        checkpointsStorage = new CheckpointsStorage();
    }

    public static Journals getJournals() {
        return Api.journals;
    }

    public static Inventories getInventories() {
        return Api.inventories;
    }

    public static CheckpointsStorage getCheckpointsStorage() {
        return Api.checkpointsStorage;
    }

    public static void setCurrentQuestId(Integer id) {
        Api.currentQuestId = id;
    }

    public static Integer getCurrentQuestId() {
        return Api.currentQuestId;
    }

    public void
    setToken(String token) {
        this.token = token;
    }

    public static Api
    getInstance() {
        return INSTANCE;
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

    public interface OnDataGetListener<T> {
        void onSuccess(final T items);

        void onError(final Exception error);
    }
}
