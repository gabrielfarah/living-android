package co.ar_smart.www.helpers;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import co.ar_smart.www.analytics.AnalyticsApplication;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static co.ar_smart.www.helpers.Constants.BASE_URL;
import static co.ar_smart.www.helpers.Constants.JSON;

/**
 * This class implements static methods for setting and getting commands to and from the API server
 * Created by Gabriel on 5/15/2016.
 */
public class CommandManager {

    private static OkHttpClient client = new OkHttpClient();

    /**
     * This method will send a command to the server to be executed. The commands sent thu this method needs a response eg. ask the value of a sensor or the volume of a music player.
     *
     * @param API_TOKEN the JWT api token of the user using the request
     * @param hub_id    the id of the hub to sent the request to
     * @param json      the string representation of a json command
     * @param callback  the callback interface for implementing UI responses to events
     */
    public static void sendCommandWithResult(String API_TOKEN, int hub_id, String json, final CommandWithResultsCallbackInterface callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(BASE_URL + "hubs/" + hub_id + "/command/get/")
                .header("Authorization", "JWT " + API_TOKEN)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AnalyticsApplication.getInstance().trackException(e);
                e.printStackTrace();
                callback.onFailureCallback();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                response.body().close();
                if (!response.isSuccessful()) {
                    Log.d("UNSEUCCESS", response.body().string());
                    callback.onUnsuccessfulCallback();
                } else {
                    try {
                        JSONObject jObject = new JSONObject(jsonData);
                        // Gets the task id from the URL field in the response and the timeout for that task id
                        String pollingUrl = jObject.getString("url").split("/v1/")[1];
                        int timeout = Integer.parseInt(jObject.getString("timeout"));
                        callback.onSuccessCallback(pollingUrl, timeout);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AnalyticsApplication.getInstance().trackException(e);
                    }
                }
            }
        });
    }

    /**
     * This method should be called next to sendCommandWithResult and is used for polling the async response on the server
     *
     * @param API_TOKEN  the JWT api token of the user using the request
     * @param pollingURL the url obtained in the response of the sendCommandWithResult method
     * @param callback   the callback interface for implementing UI responses to events
     */
    public static void getCommandResult(String API_TOKEN, String pollingURL, final ResponseCallbackInterface callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + pollingURL)
                .header("Authorization", "JWT " + API_TOKEN)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AnalyticsApplication.getInstance().trackException(e);
                //e.printStackTrace();
                callback.onFailureCallback();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                response.body().close();
                if (!response.isSuccessful()) {
                    Log.d("getresponse unsuccess", jsonData);
                    callback.onUnsuccessfulCallback();
                } else {
                    try {
                        JSONObject jObject = new JSONObject(jsonData);
                        callback.onSuccessCallback(jObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AnalyticsApplication.getInstance().trackException(e);
                    }
                }
            }
        });
    }

    /**
     * this method should be called when we only need to send commands that sets values on the hub without needing a response. eg. play a song, turn a light on/off etc.
     * @param API_TOKEN the JWT api token of the user using the request
     * @param hub_id the id of the hub to sent the request to
     * @param json the string representation of a json command
     * @param callback the callback interface for implementing UI responses to events
     */
    public static void sendCommandWithoutResult(String API_TOKEN, int hub_id, String json, final ResponseCallbackInterface callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(BASE_URL + "hubs/" + hub_id + "/command/set/")
                .header("Authorization", "JWT " + API_TOKEN)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AnalyticsApplication.getInstance().trackException(e);
                e.printStackTrace();
                callback.onFailureCallback();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                response.body().close();
                if (!response.isSuccessful()) {
                    Log.d("unsuccessful", jsonData.toString());
                    callback.onUnsuccessfulCallback();
                } else {
                    try {
                        JSONObject jObject = new JSONObject(jsonData);
                        callback.onSuccessCallback(jObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AnalyticsApplication.getInstance().trackException(e);
                    }
                }
            }
        });
    }

    public static String getFormattedCommand(String command) {
        return "[" + command + "]";
    }


    /**
     * This interface implements the callbacks for the sendCommandWithResult method
     */
    public interface CommandWithResultsCallbackInterface {
        /**
         * This methods will be called once the app could not perform the request. eg.  if the mobile device dont have internet
         */
        void onFailureCallback();

        /**
         * This method will be called if the request to sendCommandWithResult was successfully called
         * @param pollingUrl the polling URL to poll the hub response
         * @param timeout the timeout until the request will be valid in seconds
         */
        void onSuccessCallback(String pollingUrl, int timeout);

        /**
         * This method will be called if the request to sendCommandWithResult was performed but it presented errors in the request
         */
        void onUnsuccessfulCallback();
    }

    /**
     * This interface implements the callbacks for the sendCommandWithoutResult method
     */
    public interface ResponseCallbackInterface {
        /**
         * This methods will be called once the app could not perform the request. eg.  if the mobile device dont have internet
         */
        void onFailureCallback();

        /**
         * This method will be called if the request to sendCommandWithoutResult was successfully called
         * @param jObject the JSON response object from the server
         */
        void onSuccessCallback(JSONObject jObject);

        /**
         * This method will be called if the request to sendCommandWithResult was performed but it presented errors in the request
         */
        void onUnsuccessfulCallback();
    }
}
