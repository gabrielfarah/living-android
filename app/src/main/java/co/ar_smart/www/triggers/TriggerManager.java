package co.ar_smart.www.triggers;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.helpers.RetrofitServiceGenerator;
import co.ar_smart.www.pojos.Mode;
import co.ar_smart.www.pojos.Trigger;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Manager for actions that involve triggers
 * Created by caev03 on 31/05/2016.
 */
public class TriggerManager
{
    /**
     * @param hub_id
     * @param endpoint_id
     * @param trigger
     * @param API_TOKEN
     * @param callback
     */
    public static void addMode(int hub_id, int endpoint_id, Trigger trigger, String API_TOKEN, final TriggerCallbackInterface callback)
    {
        Log.d("Hub_id",hub_id+"");
        Log.d("endpoint_id", endpoint_id+"");
        Log.d("Api_token",API_TOKEN);
        TriggerService triggerClient = RetrofitServiceGenerator.createService(TriggerService.class, API_TOKEN);
        Call call = triggerClient.addTrigger(hub_id, endpoint_id, trigger);
        call.enqueue(new Callback<ResponseBody>()
        {
            @Override
            public void onResponse(Call call, Response response)
            {
                if (response.isSuccessful())
                {
                    callback.onSuccessCallback();
                }
                else
                {
                    try
                    {
                        String errorMessage = response.message() + " - " + response.code() + " " + response.errorBody().string();
                        Log.d("DEBUGGG", errorMessage);
                        AnalyticsApplication.getInstance().trackEvent("ERROR", "CREATEMODE", errorMessage);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    callback.onUnsuccessfulCallback();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t)
            {
                AnalyticsApplication.getInstance().trackException(new Exception(t));
                callback.onFailureCallback();
            }
        });
    }

    /**
     * the interface describing the API urls and their response types
     */
    public interface TriggerService
    {

        /**
         * This method will create a new trigger in the hub
         *
         * @param hub_id the hub id into which the trigger will be created
         * @return a JSON containing teh response of the post method
         */
        @POST("hubs/{hub_id}/endpoints/{endpoint_id}/triggers/")
        Call<ResponseBody> addTrigger(@Path("hub_id") int hub_id, @Path("endpoint_id") int endpoint_id, @Body Trigger trigger);
    }

    /**
     * This interface defines the callbacks for the states of the getApiToken method
     */
    public interface TriggerCallbackInterface
    {
        /**
         * This method will be called if the request failed. The main cause may be the lack of internet connection.
         */
        void onFailureCallback();

        /**
         * This method will return a list of modess from the server in case the request was successful.
         *
         * @param modes The list of modess accounts for the hub
         */
        void onSuccessCallback(List<Mode> modes);

        /**
         * This method will be called in case the request was successful.
         */
        void onSuccessCallback();

        /**
         * This method will be called if the user doing the request is not the hub admin
         */
        void onUnsuccessfulCallback();
    }
}
