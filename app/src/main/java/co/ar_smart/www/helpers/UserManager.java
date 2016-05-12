package co.ar_smart.www.helpers;

import android.util.Log;

import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.pojos.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;

/**
 * Created by Gabriel on 5/11/2016.
 */
public class UserManager {

    public static void updateUser(User u, String API_TOKEN, final UserCallbackInterface callback) {
        UserService userClient = RetrofitServiceGenerator.createService(UserService.class, API_TOKEN);
        Call<User> call = userClient.editUser(u);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    callback.onSuccessCallback(response.body());
                } else {
                    Log.d("DEBUGGG", response.toString());
                    callback.onUnsuccessfulCallback();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                AnalyticsApplication.getInstance().trackException(new Exception(t));
                callback.onFailureCallback();
            }
        });
    }

    public static void getUser(String API_TOKEN, final UserCallbackInterface callback) {
        UserService userClient = RetrofitServiceGenerator.createService(UserService.class, API_TOKEN);
        Call<User> call = userClient.getUser();
        Log.d("OkHttp", String.format("Sending request %s ", call.request().toString()));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    callback.onSuccessCallback(response.body());
                } else {
                    Log.d("DEBUGGG", response.toString());
                    callback.onUnsuccessfulCallback();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                AnalyticsApplication.getInstance().trackException(new Exception(t));
                callback.onFailureCallback();
            }
        });
    }

    /**
     * This interface defines the callbacks for the states of the getApiToken method
     */
    public interface UserCallbackInterface {
        /**
         * This method will be called if the request failed. The main cause may be the lack of internet connection.
         */
        void onFailureCallback();

        /**
         * This method will contain the API token obtained from the server in case the request was successful.
         *
         * @param user The user obtained from the server
         */
        void onSuccessCallback(User user);

        /**
         * This method will be called if the credentials provided were not correct.
         */
        void onUnsuccessfulCallback();
    }

    public interface UserService {
        @PATCH("profile/")
        Call<User> editUser(@Body User user);

        @GET("profile/")
        Call<User> getUser();
    }
}
