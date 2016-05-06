package co.ar_smart.www.living;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.helpers.JWTManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static co.ar_smart.www.helpers.Constants.*;

/**
 * This activity is responsible for loading the saved token/auth parameters and try to enter directly into the home activity.
 * In case the token expired, it will try to gain a new one automatically.
 * If none of the above works, then it will redirect to the login activity.
 * Created by Gabriel on 4/27/2016.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * The user email
     */
    private String EMAIL = "";
    /**
     * The user password
     */
    private String PASSWORD = "";
    /**
     * The backend auth token
     */
    private String API_TOKEN = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadCredentials();
    }

    /**
     * This method loads the user credentials and the api token from the shared preferences.
     * If the token is valid, then it will go directly to the home activity.
     * If the token expired, then it will try to obtain a new token using the loaded credentials.
     * If all failed, then it will redirect to the login activity.
     */
    private void loadCredentials() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        // Get values using keys
        EMAIL = settings.getString(PREF_EMAIL, DEFAULT_EMAIL);
        PASSWORD = settings.getString(PREF_PASSWORD, DEFAULT_PASSWORD);
        API_TOKEN = settings.getString(PREF_JWT, DEFAULT_JWT);
        if (JWTManager.validateJWT(API_TOKEN)) {
            openHomeActivity();
        } else if (!(EMAIL.isEmpty() && PASSWORD.isEmpty())) {
            getApiToken(EMAIL, PASSWORD);
        }else{
            openLoginRegisterActivity();
        }
    }

    /**
     * This method tries obtains a new api token given an email and password field.
     * If it fails it will redirect the user to the login activity
     * @param email the user email obtained from the shared preferences
     * @param password the user password obtained from the shared preferences
     */
    private void getApiToken(String email, String password) {
        String json = "{\"email\":\""+email+"\",\"password\":\""+password+"\"}";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                AnalyticsApplication.getInstance().trackException(e);
                openLoginRegisterActivity();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                response.body().close();
                if (!response.isSuccessful()) {
                    displayMessage("Please login");
                    openLoginRegisterActivity();
                } else {
                    try {
                        JSONObject jObject = new JSONObject(jsonData);
                        API_TOKEN = jObject.getString("token");
                        openHomeActivity();
                    } catch (JSONException e) {
                        AnalyticsApplication.getInstance().trackException(e);
                        openLoginRegisterActivity();
                    }
                }
            }
        });
    }

    /**
     * This method display a dialog message in the UI thread given a message.
     * @param message The message sent to be displayed in the main UI
     */
    private void displayMessage(final String message){
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * This method opens the home activity and sends to it the valid api token obtained from the shred preferences
     */
    public void openHomeActivity() {
        super.onResume();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        startActivity(intent);
    }

    /**
     * This method opens the login activity for the user to input an email and password fields
     */
    public void openLoginActivity() {
        super.onResume();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * This method opens the login/register activity for the user login or create a new account
     */
    public void openLoginRegisterActivity() {
        super.onResume();
        Intent intent = new Intent(this, LoginRegisterActivity.class);
        startActivity(intent);
    }

    /**
     * This method toggles visible/invisible the circular progress bar.
     */
    private void toggleProgress() {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.progress);
                if(mProgressBar.getVisibility() == View.INVISIBLE) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                else{
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        loadCredentials();
    }
}
