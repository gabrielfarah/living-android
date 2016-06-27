package co.ar_smart.www.helpers;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import co.ar_smart.www.analytics.AnalyticsApplication;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static co.ar_smart.www.helpers.Constants.JSON;
import static co.ar_smart.www.helpers.Constants.LOGIN_URL;

/**
 * This helper class, implements the methods responsable to validate the expiracy of a JWT using the HS256 encription algorithm.
 * Created by Gabriel on 4/27/2016.
 */
public class JWTManager {

    private static OkHttpClient client = new OkHttpClient();

    /**
     * This method validate if a date passed as parameter if after in time than the actual date of the device.
     * @param jDate string timestamp of a unix epoch
     * @return true if jDate is later in time than current time, false otherwise.
     */
    private static boolean validateDate(String jDate){
        double var = Double.parseDouble(jDate);
        Date expiration_time =new Date((long)var*1000);
        return expiration_time.after(new Date());
    }

    /**
     * This method validates the expiration date of a JWT in string format.
     * The JWT must be generated using the HS256 algorithm and in BASE64 encoding.
     * @param oJWT the string representation of a JWT to be validated given the exp date contained within.
     * @return true if the JWT haven't expired, false otherwise.
     */
    public static boolean validateJWT(String oJWT){
        try {
            String[] jwtTokenValues = oJWT.split("\\.");
            if (jwtTokenValues.length>2) {
                String value = new String(Base64.decode(jwtTokenValues[1].getBytes(), Base64.DEFAULT));
                JSONObject jsonPayload = new JSONObject(value);
                String exp = jsonPayload.getString("exp");
                //OK, now we validate the expiration date
                return validateDate(exp);
            }else {
                return false;
            }
        } catch (JSONException e) {
            Log.d("Error", e.getMessage());
            return false;
        }
    }

    /**
     * This method tries obtains a new api token given an email and password field.
     * If it fails it will notify the user
     *
     * @param email    the user email obtained from the user input
     * @param password the user password obtained from the user input
     * @param callback the interface for managing the request actions on each UI screens independently if needed
     */
    public static void getApiToken(String email, String password, final JWTCallbackInterface callback) {
        String json = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AnalyticsApplication.getInstance().trackException(e);
                callback.onFailureCallback();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                response.body().close();
                if (!response.isSuccessful()) {
                    callback.onUnsuccessfulCallback();
                } else {
                    try {
                        JSONObject jObject = new JSONObject(jsonData);
                        callback.onSuccessCallback(jObject.getString("token"));
                    } catch (JSONException e) {
                        AnalyticsApplication.getInstance().trackException(e);
                        callback.onExceptionCallback();
                    }
                }
            }
        });
    }

    /**
     * This interface defines the callbacks for the states of the getApiToken method
     */
    public interface JWTCallbackInterface {
        /**
         * This method will be called if the request failed. The main cause may be the lack of internet connection.
         */
        void onFailureCallback();

        /**
         * This method will contain the API token obtained from the server in case the request was successful.
         *
         * @param nToken The token obtained from the server
         */
        void onSuccessCallback(String nToken);

        /**
         * This method will be called if the credentials provided were not correct.
         */
        void onUnsuccessfulCallback();

        /**
         * This method will be called if there was an error parsing the JSON response from the server.
         */
        void onExceptionCallback();
    }

}
