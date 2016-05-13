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
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Gabriel on 5/11/2016.
 */
public class GuestManager {

    public static void getGuests(int hub_id, String API_TOKEN, final GuestCallbackInterface callback) {
        GuestService userClient = RetrofitServiceGenerator.createService(GuestService.class, API_TOKEN);
        Call<List<Guest>> call = userClient.getGuests(hub_id);
        Log.d("OkHttp", String.format("Sending request %s ", call.request().toString()));
        call.enqueue(new Callback<List<Guest>>() {
            @Override
            public void onResponse(Call<List<Guest>> call, Response<List<Guest>> response) {
                if (response.isSuccessful()) {
                    Log.d("DEBUGGG", response.message().toString() + " - " + response.code() + " - " + response.body());
                    callback.onSuccessCallback(response.body());
                } else {
                    Log.d("DEBUGGG", response.message().toString() + " - " + response.code());
                    try {
                        Log.d("DEBUGGG", response.errorBody().string().toString());
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

    public static void removeGuest(int hub_id, int guest_id, String API_TOKEN, final GuestCallbackInterface callback) {
        GuestService userClient = RetrofitServiceGenerator.createService(GuestService.class, API_TOKEN);
        Call call = userClient.deleteGuest(hub_id, guest_id);
        Log.d("OkHttp", String.format("Sending request %s ", call.request().toString()));
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    callback.onSuccessCallback();
                } else {
                    Log.d("DEBUGGG", response.message().toString() + " - " + response.code());
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
         * This method will contain the API token obtained from the server in case the request was successful.
         *
         * @param guest The user obtained from the server
         */
        void onSuccessCallback(List<Guest> guest);

        /**
         * This method will contain the API token obtained from the server in case the request was successful.
         */
        void onSuccessCallback();

        /**
         * This method will be called if the credentials provided were not correct.
         */
        void onUnsuccessfulCallback();
    }

    public interface GuestService {
        @GET("hubs/{hub_id}/guests/")
        Call<List<Guest>> getGuests(@Path("hub_id") int hub_id);

        @DELETE("hubs/{hub_id}/guests/{guest_id}/")
        Call<ResponseBody> deleteGuest(@Path("hub_id") int hub_id, @Path("guest_id") int guest_id);
    }
}
