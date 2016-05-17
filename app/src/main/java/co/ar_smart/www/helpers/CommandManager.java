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
 * Created by Gabriel on 5/15/2016.
 */
public class CommandManager {


    public static void sendCommandWithResult(String API_TOKEN, int hub_id, String json, final CommandWithResultsCallbackInterface callback) {
        OkHttpClient client = new OkHttpClient();
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
                Log.d("FAILURE", e.toString());
                callback.onFailureCallback();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                response.body().close();
                if (!response.isSuccessful()) {
                    Log.d("UNSUCCESS 1", jsonData + " - " + response.message() + " - " + response.code());
                    callback.onUnsuccessfulCallback();
                } else {
                    try {
                        JSONObject jObject = new JSONObject(jsonData);
                        String pollingUrl = jObject.getString("url").split("/v1/")[1];
                        int timeout = Integer.parseInt(jObject.getString("timeout"));
                        callback.onSuccessCallback(pollingUrl, timeout);
                    } catch (JSONException e) {
                        Log.d("EXCEPTION", e.toString());
                        e.printStackTrace();
                        AnalyticsApplication.getInstance().trackException(e);
                    }
                }
            }
        });
    }

    public static void getCommandResult(String API_TOKEN, String pollingURL, final ResponseCallbackInterface callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASE_URL + pollingURL)
                .header("Authorization", "JWT " + API_TOKEN)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AnalyticsApplication.getInstance().trackException(e);
                e.printStackTrace();
                Log.d("FAILURE", e.toString());
                callback.onFailureCallback();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                response.body().close();
                if (!response.isSuccessful()) {
                    Log.d("UNSUCCESS 2", jsonData + " - " + response.message() + " - " + response.code());
                    callback.onUnsuccessfulCallback();
                } else {
                    try {
                        JSONObject jObject = new JSONObject(jsonData);
                        callback.onSuccessCallback(jObject);
                    } catch (JSONException e) {
                        Log.d("EXCEPTION", e.toString());
                        AnalyticsApplication.getInstance().trackException(e);
                    }
                }
            }
        });
    }

    public static void sendCommandWithoutResult(String API_TOKEN, int hub_id, String json, final ResponseCallbackInterface callback) {
        OkHttpClient client = new OkHttpClient();
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
                Log.d("FAILURE", e.toString());
                callback.onFailureCallback();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                response.body().close();
                if (!response.isSuccessful()) {
                    Log.d("UNSUCCESS 3", jsonData + " - " + response.message() + " - " + response.code());
                    callback.onUnsuccessfulCallback();
                } else {
                    try {
                        JSONObject jObject = new JSONObject(jsonData);
                        callback.onSuccessCallback(jObject);
                    } catch (JSONException e) {
                        Log.d("EXCEPTION", e.toString());
                        e.printStackTrace();
                        AnalyticsApplication.getInstance().trackException(e);
                    }
                }
            }
        });
    }


    public interface CommandWithResultsCallbackInterface {
        void onFailureCallback();

        void onSuccessCallback(String pollingUrl, int timeout);

        void onUnsuccessfulCallback();
    }

    public interface ResponseCallbackInterface {
        void onFailureCallback();

        void onSuccessCallback(JSONObject jObject);

        void onUnsuccessfulCallback();
    }
}
