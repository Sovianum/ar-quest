package technopark.diploma.arquest.network;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoaderService {

    @POST("api/v1/auth/register")
    Call<ServerResponse<String>> registerUser(@Body RequestBody user);

    @POST("api/v1/auth/login")
    Call<ServerResponse<String>> loginUser(@Body RequestBody user);

    //@GET("api/v1/user/self")
    //Call<ServerResponse<User>> getSelfInfo(@Header(ServerInfo.AUTH_HEADER) String token);


}
