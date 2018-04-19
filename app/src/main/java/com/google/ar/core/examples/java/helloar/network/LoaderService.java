package com.google.ar.core.examples.java.helloar.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface LoaderService {

    @POST("api/v1/auth/register")
    Call<ServerResponse<String>> registerUser(@Body RequestBody user);

    @POST("api/v1/auth/login")
    Call<ServerResponse<String>> loginUser(@Body RequestBody user);

    @GET("video/mp4/720/big_buck_bunny_720p_10mb.mp4") //stubs
    @Streaming
    Call<ResponseBody> downloadFile();

    @GET("api/v1/{questId}")
    @Streaming
    Call<ResponseBody> downloadQuest(@Path("questId") int questId);



    //@GET("api/v1/user/self")
    //Call<ServerResponse<User>> getSelfInfo(@Header(ServerInfo.AUTH_HEADER) String token);


}
