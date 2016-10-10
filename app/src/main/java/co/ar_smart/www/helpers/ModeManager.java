package co.ar_smart.www.helpers;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.interfaces.ICommandClass;
import co.ar_smart.www.pojos.Command;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.Mode;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * This class implements all the methods to manage the modess in a Hub
 * Created by Gabriel on 5/11/2016.
 */
public class ModeManager {

    private static ModeService modesClient;

    public static void getModes(int hub_id, String API_TOKEN, final ModeCallbackInterfaceGet callback) {
        modesClient = RetrofitServiceGenerator.createService(ModeService.class, API_TOKEN);
        Call<List<Mode>> call = modesClient.getModes(hub_id);
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


    public static void removeMode(int hub_id, int modes_id, String API_TOKEN, final ModeCallbackInterfaceDelete callback) {
        modesClient = RetrofitServiceGenerator.createService(ModeService.class, API_TOKEN);
        Call<ResponseBody> call = modesClient.deleteMode(hub_id, modes_id);
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


    public static void addMode(int hub_id, Mode email, String API_TOKEN, final ModeCallbackInterface callback) {
        modesClient = RetrofitServiceGenerator.createService(ModeService.class, API_TOKEN);
        Call<Mode> call = modesClient.addMode(hub_id, email);
        Log.d("OkHttp", String.format("Sending request %s ", call.request().toString()));
        call.enqueue(new Callback<Mode>() {
            @Override
            public void onResponse(Call<Mode> call, Response<Mode> response) {
                if (response.isSuccessful()) {
                    callback.onSuccessCallback(response.body());
                } else {
                    try {
                        String errorMessage = response.message() + " - " + response.code() + " " + response.errorBody().string();
                        Log.d("DEBUGGG", errorMessage);
                        AnalyticsApplication.getInstance().trackEvent("ERROR", "CREATEMODE", errorMessage);
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
        Mode mode_all_off = new Mode(-2, "Turn off all devices");
        Mode mode_all_on = new Mode(-1, "Turn on all devices");
        ArrayList<Command> off_commands = new ArrayList<>();
        ArrayList<Command> on_commands = new ArrayList<>();
        HashMap<String, ICommandClass> map = Constants.getUiMapClasses();
        ICommandClass te;
        for (Endpoint e : endpoint_devices) {
            Log.d("meto modos1", e.toString());
            te = map.get(e.getUi_class_command());
            if (te != null) {
                te.setEndpoint(e);
                off_commands.add(te.getTurnOffCommand());
                on_commands.add(te.getTurnOnCommand());
            } else {
                AnalyticsApplication.getInstance().trackEvent("Device UI Class", "DoNotExist", "The device in hub:" + e.getHub() + " named:" + e.getName() + " the ui class does not correspond. UI:" + e.getUi_class_command());
            }
        }
        mode_all_off.setPayload(off_commands);
        mode_all_on.setPayload(on_commands);
        response.add(mode_all_off);
        response.add(mode_all_on);
        return response;
    }

    public static void editMode(int hub_id, Mode modo, String API_TOKEN, final ModeCallbackInterface callback) {
        modesClient = RetrofitServiceGenerator.createService(ModeService.class, API_TOKEN);
        Call<Mode> call = modesClient.editMode(hub_id, modo.getId(), modo);
        Log.d("EDIT", "ID: " + modo.getId() + " " + call.request().toString());
        call.enqueue(new Callback<Mode>() {
            @Override
            public void onResponse(Call<Mode> call, Response<Mode> response) {
                if (response.isSuccessful()) {
                    callback.onSuccessCallback(response.body());
                } else {
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

    public interface ModeCallbackInterface {
        /**
         * This method will be called if the request failed. The main cause may be the lack of internet connection.
         */
        void onFailureCallback();

        /**
         * This method will be called if the user doing the request is not the hub admin
         */
        void onUnsuccessfulCallback();

        void onSuccessCallback(Mode body);
    }

    public interface ModeCallbackInterfaceGet {
        /**
         * This method will be called if the request failed. The main cause may be the lack of internet connection.
         */
        void onFailureCallback();

        void onSuccessCallback(List<Mode> modes);

        void onUnsuccessfulCallback();
    }

    public interface ModeCallbackInterfaceDelete {
        /**
         * This method will be called if the request failed. The main cause may be the lack of internet connection.
         */
        void onFailureCallback();
        void onSuccessCallback();
        void onUnsuccessfulCallback();
    }

    /**
     * the interface describing the API urls and their response types
     */
    public interface ModeService {
        @GET("hubs/{hub_id}/modes/")
        Call<List<Mode>> getModes(@Path("hub_id") int hub_id);

        @POST("hubs/{hub_id}/modes/")
        Call<Mode> addMode(@Path("hub_id") int hub_id, @Body Mode email);

        @DELETE("hubs/{hub_id}/modes/{modes_id}/")
        Call<ResponseBody> deleteMode(@Path("hub_id") int hub_id, @Path("modes_id") int modes_id);

        @PUT("hubs/{hub_id}/modes/{mode_id}/")
        Call<Mode> editMode(@Path("hub_id") int hub_id, @Path("mode_id") int mode_id, @Body Mode mode);
    }
}
