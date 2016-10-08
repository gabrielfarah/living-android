package co.ar_smart.www.register;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.helpers.JWTManager;
import co.ar_smart.www.living.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static co.ar_smart.www.helpers.Constants.DEFAULT_EMAIL;
import static co.ar_smart.www.helpers.Constants.DEFAULT_PASSWORD;
import static co.ar_smart.www.helpers.Constants.HUB_REGISTER_URL;
import static co.ar_smart.www.helpers.Constants.JSON;
import static co.ar_smart.www.helpers.Constants.PREFS_NAME;
import static co.ar_smart.www.helpers.Constants.PREF_EMAIL;
import static co.ar_smart.www.helpers.Constants.PREF_PASSWORD;

public class RegisterHubActivity extends AppCompatActivity
{
    /**
     * Hub serial
     */
    private String hubSerial;
    /**
     * Edit text for HubSerial
     */
    private EditText edtHubSerial;
    /**
     * Hub name
     */
    private String hubName;
    /**
     * Path of background image
     */
    private String backgroungPath;
    /**
     * Hub Latitude
     */
    private double hubLatitude;
    /**
     * Hub Longitude
     */
    private double hubLongitude;
    /**
     * Hub area radius
     */
    private int hubRadius;
    /**
     * Current Context
     */
    private Context mContext;
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_hub);
        mContext = this;
        Intent previusIntent = getIntent();
        // Get values from extras
        hubName = previusIntent.getStringExtra("hubName");
        backgroungPath = previusIntent.getStringExtra("backgroundPath");
        hubLatitude = previusIntent.getDoubleExtra("hubLatitude", Constants.DEFAULT_LATITUDE);
        hubLongitude = previusIntent.getDoubleExtra("hubLongitude", Constants.DEFAULT_LONGITUDE);
        hubRadius = (int) previusIntent.getDoubleExtra("hubRadius", Constants.DEFAULT_RADIUS);
        edtHubSerial = (EditText) findViewById(R.id.edtHubSerial);
        //Assign action for button
        Button btn_co_continue = (Button) findViewById(R.id.btn_co_continue);
        if (btn_co_continue != null)
        {
            btn_co_continue.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (edtHubSerial.getText().toString().equals("")) {
                        Toast.makeText(mContext, R.string.error_hub_serial_empty, Toast.LENGTH_SHORT).show();
                    } else {
                        hubSerial = edtHubSerial.getText().toString().trim();
                        getToken();
                    }
                }
            });
        }
    }

    /**
     * Get API_TOKEN for current user
     */
    private void getToken()
    {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        // Get values using keys
        String EMAIL = settings.getString(PREF_EMAIL, DEFAULT_EMAIL);
        String PASSWORD = settings.getString(PREF_PASSWORD, DEFAULT_PASSWORD);
        JWTManager.getApiToken(EMAIL, PASSWORD, new JWTManager.JWTCallbackInterface()
        {
            @Override
            public void onFailureCallback() {
                Constants.showNoInternetMessage(getApplicationContext());
            }

            @Override
            public void onSuccessCallback(String nToken)
            {
                registerHub(nToken);
            }

            @Override
            public void onUnsuccessfulCallback()
            {
                // Nothing exceptional to do in this case.
            }

            @Override
            public void onExceptionCallback()
            {
                // Nothing exceptional to do in this case.
            }
        });
    }

    /**
     * Call service for register the hub with given values
     * @param nToken - API_TOKEN for current user
     */
    private void registerHub(String nToken)
    {
        try {
            final String token = "JWT  " + nToken;
            JSONObject json = new JSONObject();
            json.put("custom_name", hubName);
            json.put("serial", hubSerial);
            json.put("latitude", hubLatitude);
            json.put("longitude", hubLongitude);
            json.put("radius", hubRadius);

            RequestBody body = RequestBody.create(JSON, json.toString());
            Request request = new Request.Builder()
                    .url(HUB_REGISTER_URL)
                    .header("Accept", "application/json")
                    .header("Authorization", token)
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    AnalyticsApplication.getInstance().trackException(e);
                    displayMessage(getResources().getString(R.string.toast_login_failure));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException
                {
                    String body = response.body().string();
                    response.body().close();
                    if (!response.isSuccessful())
                    {
                        displayMessage(getResources().getString(R.string.toast_hub_registration_failure));
                    }
                    else
                    {
                        connectOutlet();
                    }
                }
            });
        }
        catch (Exception e)
        {
            displayMessage(getResources().getString(R.string.create_user_error));
        }
    }

    /**
     * Method to advance to next activity
     */
    private void connectOutlet()
    {
        Intent i = new Intent(mContext, ConnectOutletActivity.class);
        startActivity(i);
    }


    /**
     * This method display a dialog message in the UI thread given a message.
     * @param message The message sent to be displayed in the main UI
     */
    private void displayMessage(final String message){
        RegisterHubActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(RegisterHubActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
