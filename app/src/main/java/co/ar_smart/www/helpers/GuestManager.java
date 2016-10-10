package co.ar_smart.www.helpers;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.pojos.Guest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * This class implements all the methods to manage the guests in a Hub
 * Created by Gabriel on 5/11/2016.
 */
public class GuestManager {

    /**
     * This method will get all the guests for a given Hub
     *
     * @param hub_id    the id of the hub to add the guest to
     * @param API_TOKEN the JWT token of the user doing the request
     * @param callback  the callback interface to implements the UI responses
     */
    public static void getGuests(int hub_id, String API_TOKEN, final GuestCallbackInterface callback) {
        GuestService guestClient = RetrofitServiceGenerator.createService(GuestService.class, API_TOKEN);
        Call<List<Guest>> call = guestClient.getGuests(hub_id);
        Log.d("OkHttp", String.format("Sending request %s ", call.request().toString()));
        call.enqueue(new Callback<List<Guest>>() {
            @Override
            public void onResponse(Call<List<Guest>> call, Response<List<Guest>> response) {
                if (response.isSuccessful()) {
                    Log.d("DEBUGGG", response.message() + " - " + response.code() + " - " + response.body());
                    callback.onSuccessCallback(response.body());
                } else {
                    Log.d("DEBUGGG", response.message() + " - " + response.code());
                    try {
                        Log.d("DEBUGGG", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    callback.onUnsuccessfulCallback();
                }
            }

            @Override
            public void onFailure(Call<List<Guest>> call, Throwable t) {
                AnalyticsApplication.getInstance().trackException(new Exception(t));
                callback.onFailureCallback();
            }
        });
    }

    /**
     * This method will remove a guest from a Hub
     *
     * @param hub_id    the id of the hub to add the guest to
     * @param guest_id  the id of the guest to be removed
     * @param API_TOKEN the JWT token of the user doing the request
     * @param callback  the callback interface to implements the UI responses
     */
    public static void removeGuest(int hub_id, int guest_id, String API_TOKEN, final GuestCallbackInterface callback) {
        GuestService guestClient = RetrofitServiceGenerator.createService(GuestService.class, API_TOKEN);
        Call<ResponseBody> call = guestClient.deleteGuest(hub_id, guest_id);
        Log.d("OkHttp", String.format("Sending request %s ", call.request().toString()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    callback.onSuccessCallback();
                } else {
                    Log.d("DEBUGGG", response.message() + " - " + response.code());
                    callback.onUnsuccessfulCallback();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                AnalyticsApplication.getInstance().trackException(new Exception(t));
                callback.onFailureCallback();
            }
        });
    }

    /**
     * this method will add a new guest into a Hub
     * @param hub_id the id of the hub to add the guest to
     * @param email the email of the new guest to be added
     * @param API_TOKEN the JWT token of the user doing the request
     * @param callback the callback interface to implements the UI responses
     */
    public static void addGuest(int hub_id, Guest email, String API_TOKEN, final GuestCallbackInterface callback) {
        GuestService guestClient = RetrofitServiceGenerator.createService(GuestService.class, API_TOKEN);
        Call call = guestClient.addGuest(hub_id, email);
        Log.d("OkHttp", String.format("Sending request %s ", call.request().toString()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    callback.onSuccessCallback();
                } else {
                    try {
                        Log.d("DEBUGGG", response.message() + " - " + response.code() + " " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    callback.onUnsuccessfulCallback();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                AnalyticsApplication.getInstance().trackException(new Exception(t));
                callback.onFailureCallback();
            }
        });
    }

    /**
     * This interface defines the callbacks for the states of the getApiToken method
     */
    public interface GuestCallbackInterface {
        /**
         * This method will be called if the request failed. The main cause may be the lack of internet connection.
         */
        void onFailureCallback();

        /**
         * This method will return a list of guests from the server in case the request was successful.
         *
         * @param guest The list of guests accounts for the hub
         */
        void onSuccessCallback(List<Guest> guest);

        /**
         * This method will be called in case the request was successful.
         */
        void onSuccessCallback();

        /**
         * This method will be called if the user doing the request is not the hub admin
         */
        void onUnsuccessfulCallback();
    }

    /**
     * the interface describing the API urls and their response types
     */
    public interface GuestService {
        /**
         * This method will retrieve all the guests given a valid hub id
         * @param hub_id the hub id to obtain the guets from
         * @return a list of guests
         */
        @GET("hubs/{hub_id}/guests/")
        Call<List<Guest>> getGuests(@Path("hub_id") int hub_id);

        /**
         * This method will create a new guest in the hub
         * @param hub_id the hub id into which the guest will be created
         * @param email the email of the user to be invited into te hub
         * @return a JSON containing teh response of the post method
         */
        @POST("hubs/{hub_id}/guests/")
        Call<ResponseBody> addGuest(@Path("hub_id") int hub_id, @Body Guest email);

        /**
         * This method will remove a guest user from the hub
         * @param hub_id the hub id into which the guest will be removed
         * @param guest_id the id of the guest to be removed
         * @return a JSON containing teh response of the delete method
         */
        @DELETE("hubs/{hub_id}/guests/{guest_id}/")
        Call<ResponseBody> deleteGuest(@Path("hub_id") int hub_id, @Path("guest_id") int guest_id);
    }
}
