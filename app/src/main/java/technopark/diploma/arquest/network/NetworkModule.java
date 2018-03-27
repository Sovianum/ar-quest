package technopark.diploma.arquest.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import technopark.diploma.arquest.ListenerHandler;
import technopark.diploma.arquest.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.net.HttpURLConnection.HTTP_OK;

@Module
public class NetworkModule {
    public interface OnDataGetListener<T> {
        void onSuccess(final T items);

        void onError(final Exception error);
    }

    private String token;
    private String baseUrl;

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    public static final String TEXT_PLAIN = "text/plain";
    private static final Gson GSON = new GsonBuilder().create();
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

    public ListenerHandler<OnDataGetListener<ServerResponse<String>>>
    loginUser(final OnDataGetListener<ServerResponse<String>> listener, final User user) {
        final ListenerHandler<OnDataGetListener<ServerResponse<String>>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final String strRequestBody = GSON.toJson(user);
                    RequestBody requestBody =
                            RequestBody.create(MediaType.parse(TEXT_PLAIN), strRequestBody);
                    final Response<ServerResponse<String>> response = service.loginUser(requestBody).execute();

                    final ServerResponse<String> body = response.body();
                    if (body == null) {
                        throw new NetworkError(response.code());
                    }
                    if (body.getData() == null || response.code() != HTTP_OK) {
                        throw new NetworkError(body.getErrMsg(), response.code());
                    }
                    invokeSuccess(handler, body);
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    public ListenerHandler<OnDataGetListener<ServerResponse<String>>>
    registerUser(final OnDataGetListener<ServerResponse<String>> listener, final User user) {
        final ListenerHandler<OnDataGetListener<ServerResponse<String>>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final String strRequestBody = GSON.toJson(user);
                    RequestBody requestBody =
                            RequestBody.create(MediaType.parse(TEXT_PLAIN), strRequestBody);
                    final Response<ServerResponse<String>> response = service.registerUser(requestBody).execute();

                    final ServerResponse<String> body = response.body();
                    if (body == null) {
                        throw new NetworkError(response.code());
                    }
                    if (body.getData() == null || response.code() != HTTP_OK) {
                        throw new NetworkError(body.getErrMsg(), response.code());
                    }
                    invokeSuccess(handler, body);
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
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
