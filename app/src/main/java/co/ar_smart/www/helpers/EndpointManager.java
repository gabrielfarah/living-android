package co.ar_smart.www.helpers;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import co.ar_smart.www.analytics.AnalyticsApplication;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static co.ar_smart.www.helpers.Constants.BASE_URL;
import static co.ar_smart.www.helpers.Constants.JSON;

/**
 * Created by Gabriel on 8/4/2016.
 */
public class EndpointManager {

    private static OkHttpClient client = new OkHttpClient();

    public static void sendAddEndpointCommand(String API_TOKEN, int hub_id, String url, final CommandManager.CommandWithResultsCallbackInterface callback) {
        RequestBody body = RequestBody.create(JSON, "[]");
        Request request = new Request.Builder()
                .url(BASE_URL + "hubs/" + hub_id + url)
                .header("Authorization", "JWT " + API_TOKEN)
                .post(body)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                AnalyticsApplication.getInstance().trackException(e);
                e.printStackTrace();
                callback.onFailureCallback();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String jsonData = response.body().string();
                response.body().close();
                if (!response.isSuccessful()) {
                    //for (int i = 0; i <jsonData.toString().length(); i++){}
                    Log.d("UNSUCCESS", call.request().toString());
                    Log.d("UNSUCCESS:", jsonData.toString());
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
}
