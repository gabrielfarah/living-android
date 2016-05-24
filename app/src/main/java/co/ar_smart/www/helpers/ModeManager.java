package co.ar_smart.www.helpers;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.pojos.Command;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.Mode;
import co.ar_smart.www.pojos.hue.HueEndpoint;
import co.ar_smart.www.pojos.sonos.SonosEndpoint;
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
public class ModeManager {

    /**
     * This method will get all the guests for a given Hub
     *
     * @param hub_id    the id of the hub to add the guest to
     * @param API_TOKEN the JWT token of the user doing the request
     * @param callback  the callback interface to implements the UI responses
     */
    public static void getModes(int hub_id, String API_TOKEN, final ModeCallbackInterface callback) {
        ModeService guestClient = RetrofitServiceGenerator.createService(ModeService.class, API_TOKEN);
        Call<List<Mode>> call = guestClient.getModes(hub_id);
        Log.d("OkHttp", String.format("Sending request %s ", call.request().toString()));
        call.enqueue(new Callback<List<Mode>>() {
            @Override
            public void onResponse(Call<List<Mode>> call, Response<List<Mode>> response) {
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
            public void onFailure(Call<List<Mode>> call, Throwable t) {
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
    public static void removeMode(int hub_id, int guest_id, String API_TOKEN, final ModeCallbackInterface callback) {
        ModeService guestClient = RetrofitServiceGenerator.createService(ModeService.class, API_TOKEN);
        Call call = guestClient.deleteMode(hub_id, guest_id);
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
     *
     * @param hub_id    the id of the hub to add the guest to
     * @param email     the email of the new guest to be added
     * @param API_TOKEN the JWT token of the user doing the request
     * @param callback  the callback interface to implements the UI responses
     */
    public static void addMode(int hub_id, Mode email, String API_TOKEN, final ModeCallbackInterface callback) {
        ModeService guestClient = RetrofitServiceGenerator.createService(ModeService.class, API_TOKEN);
        Call call = guestClient.addMode(hub_id, email);
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

    public static ArrayList<Mode> getDefaultModes(ArrayList<Endpoint> endpoint_devices) {
        ArrayList<Mode> response = new ArrayList<>();
        Mode mode_all_off = new Mode(0, "Turn Off All");
        Mode mode_all_on = new Mode(1, "Turn On All");
        ArrayList<Command> off_commands = new ArrayList<>();
        ArrayList<Command> on_commands = new ArrayList<>();
        for (Endpoint e : endpoint_devices) {
            Log.d("meto modos1", e.toString());
            switch (e.getUi_class_command()) {
                case "ui-sonos":
                    Log.d("meto modos1", "sonos");
                    SonosEndpoint sonosEndpoint = new SonosEndpoint(e);
                    off_commands.add(sonosEndpoint.getTurnOffCommand());
                    on_commands.add(sonosEndpoint.getTurnOnCommand());
                    break;
                case "ui-lock":
                    if (e.getEndpoint_type().equalsIgnoreCase("zwave")) {
                        //TODO
                    } else {
                        //TODO
                    }
                    break;
                case "ui-hue":
                    Log.d("meto modos1", "hue");
                    HueEndpoint hueEndpoint = new HueEndpoint(e);
                    off_commands.add(hueEndpoint.getTurnOffCommand());
                    on_commands.add(hueEndpoint.getTurnOnCommand());
                    break;
                default:
                    AnalyticsApplication.getInstance().trackEvent("Device Image", "DoNotExist", "The device in hub:" + e.getHub() + " named:" + e.getName() + " the image does not correspong. image:" + e.getImage());
            }
        }
        mode_all_off.setPayload(off_commands);
        mode_all_on.setPayload(on_commands);
        response.add(mode_all_off);
        response.add(mode_all_on);
        return response;
    }

    /**
     * This interface defines the callbacks for the states of the getApiToken method
     */
    public interface ModeCallbackInterface {
        /**
         * This method will be called if the request failed. The main cause may be the lack of internet connection.
         */
        void onFailureCallback();

        /**
         * This method will return a list of guests from the server in case the request was successful.
         *
         * @param guest The list of guests accounts for the hub
         */
        void onSuccessCallback(List<Mode> guest);

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
    public interface ModeService {
        /**
         * This method will retrieve all the guests given a valid hub id
         *
         * @param hub_id the hub id to obtain the guets from
         * @return a list of guests
         */
        @GET("hubs/{hub_id}/guests/")
        Call<List<Mode>> getModes(@Path("hub_id") int hub_id);

        /**
         * This method will create a new guest in the hub
         *
         * @param hub_id the hub id into which the guest will be created
         * @param email  the email of the user to be invited into te hub
         * @return a JSON containing teh response of the post method
         */
        @POST("hubs/{hub_id}/guests/")
        Call<ResponseBody> addMode(@Path("hub_id") int hub_id, @Body Mode email);

        /**
         * This method will remove a guest user from the hub
         *
         * @param hub_id   the hub id into which the guest will be removed
         * @param guest_id the id of the guest to be removed
         * @return a JSON containing teh response of the delete method
         */
        @DELETE("hubs/{hub_id}/guests/{guest_id}/")
        Call<ResponseBody> deleteMode(@Path("hub_id") int hub_id, @Path("guest_id") int guest_id);
    }
}
