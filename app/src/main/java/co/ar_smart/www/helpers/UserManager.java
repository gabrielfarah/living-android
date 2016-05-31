package co.ar_smart.www.helpers;

import android.util.Log;

import java.io.IOException;

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

    /**
     * This static methods update a user object in the server given its updated representation
     *
     * @param u         the updated user instance
     * @param API_TOKEN the api token for doing the request
     * @param callback  the callback for implementing ui responses
     */
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

    /**
     * This static method returns the current user information from the server
     * @param API_TOKEN the api token for doing the request
     * @param callback the callback for implementing ui responses
     */
    public static void getUser(String API_TOKEN, final UserCallbackInterface callback) {
        UserService userClient = RetrofitServiceGenerator.createService(UserService.class, API_TOKEN);
        Call<User> call = userClient.getUser();
        Log.d("OkHttp", String.format("Sending request %s ", call.request().toString()));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User ru = response.body();
                    callback.onSuccessCallback(ru);
                } else {
                    try {
                        System.out.print(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
     * This interface defines the callbacks for the states of the user methods
     */
    public interface UserCallbackInterface {
        /**
         * This method will be called if the request failed. The main cause may be the lack of internet connection.
         */
        void onFailureCallback();

        /**
         * This method returns a user once the request was a success
         * @param user the response user
         */
        void onSuccessCallback(User user);

        /**
         * This method will be called if was a problem with the response
         */
        void onUnsuccessfulCallback();
    }

    /**
     * the methods available for a user
     */
    public interface UserService {
        /**
         * this method will edit a user
         * @param user the updated instance of the user
         * @return the edited user response
         */
        @PATCH("profile/")
        Call<User> editUser(@Body User user);

        /**
         * this method returns the current user information stored
         * @return the information of the user doing the request
         */
        @GET("profile/")
        Call<User> getUser();
    }
}
