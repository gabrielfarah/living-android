package co.ar_smart.www.living;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.ar_smart.www.adapters.GridDevicesAdapter;
import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.controllers.ZwaveLockControllerActivity;
import co.ar_smart.www.controllers.SonosControllerActivity;
import co.ar_smart.www.helpers.JWTManager;
import co.ar_smart.www.helpers.RetrofitServiceGenerator;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.Hub;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

import static co.ar_smart.www.helpers.Constants.DEFAULT_EMAIL;
import static co.ar_smart.www.helpers.Constants.DEFAULT_HUB;
import static co.ar_smart.www.helpers.Constants.DEFAULT_PASSWORD;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;
import static co.ar_smart.www.helpers.Constants.JSON;
import static co.ar_smart.www.helpers.Constants.LOGIN_URL;
import static co.ar_smart.www.helpers.Constants.PREFS_NAME;
import static co.ar_smart.www.helpers.Constants.PREF_EMAIL;
import static co.ar_smart.www.helpers.Constants.PREF_HUB;
import static co.ar_smart.www.helpers.Constants.PREF_JWT;
import static co.ar_smart.www.helpers.Constants.PREF_PASSWORD;

/**
 * This activity implements the main screen of the Living application.
 * Created by Gabriel on 4/27/2016.
 */
public class HomeActivity extends AppCompatActivity {

    /**
     * The backend auth token
     */
    private String API_TOKEN = "";
    /**
     * The ID of the hub the user wants to use in this session.
     */
    private int PREFERRED_HUB_ID = -1;
    /**
     * The flag that should be set true if handler should stop
     */
    private boolean backgroundStopHandlerFlag = false;
    /**
     * This handler will be used to update the states of all the devices every 5 seconds
     */
    private final Handler backgroundPollingHandler = new Handler();
    /**
     * The list of devices (endpoints) a particular hub has
     */
    private List<String> devices = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        Button loginButton = (Button) findViewById(R.id.logoutButton);
        if (loginButton != null) {
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnalyticsApplication.getInstance().trackEvent("User Action", "Logout", "The user logged out");
                    successfulLogout();
                }
            });
        }
        loadPreferredHub();
    }

    /**
     * This method will load the preferred hub the user selected the last time (if any).
     */
    private void loadPreferredHub(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        // Get values using keys
        PREFERRED_HUB_ID = Integer.parseInt(settings.getString(PREF_HUB, DEFAULT_HUB));
        if (PREFERRED_HUB_ID == -1)
            getHubs();
        else
            getEndpoints();
    }

    /**
     * This method will clear the saved credentials of the user in the shared preferences.
     * It will also redirect the user to the login activity.
     */
    private void successfulLogout(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(PREF_EMAIL);
        editor.remove(PREF_PASSWORD);
        editor.remove(PREF_JWT);
        editor.remove(PREF_HUB);
        editor.apply();
        openLoginActivity();
    }

    /**
     * This method will open the login activity.
     */
    public void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Since other users (using the app or manually) may have changed the states of the devices (endpoints)
     * we must poll the states of the endpoints and update the UI so the user knows what's the state of
     * every device.
     * This method will automatically poll every 5 seconds
     */
    private void loadEndpointStates(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // while the handler is not stoped create a new request every time delta
                if (!backgroundStopHandlerFlag) {
                    // TODO pollStates();
                    backgroundPollingHandler.postDelayed(this, 5000);
                }
            }
        };
        // start it with:
        backgroundPollingHandler.post(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        // If user paused this activity and the token expired while idled, then we must automatically
        // get a new one when this activity resumes.
        if (!JWTManager.validateJWT(API_TOKEN)){
            SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                    Context.MODE_PRIVATE);
            // Get values using keys
            String EMAIL = settings.getString(PREF_EMAIL, DEFAULT_EMAIL);
            String PASSWORD = settings.getString(PREF_PASSWORD, DEFAULT_PASSWORD);
            getApiToken(EMAIL, PASSWORD);
        }
    }

    /**
     * This method tries obtains a new api token given an email and password field.
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
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override public void onFailure(okhttp3.Call call, IOException e) {
                // If we dont have internet active
                showNoInternetMessage();
                AnalyticsApplication.getInstance().trackException(e);
                //finish();
                //startActivity(getIntent());
            }

            @Override public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String jsonData = response.body().string();
                response.body().close();
                if (!response.isSuccessful()) {
                    successfulLogout();
                } else {
                    try {
                        JSONObject jObject = new JSONObject(jsonData);
                        API_TOKEN = jObject.getString("token");
                    } catch (JSONException e) {
                        AnalyticsApplication.getInstance().trackException(e);
                    }
                }
            }
        });
    }

    /**
     * This method will try to obtain all the devices (endpoints) for a given hub of the user.
     */
    private void getEndpoints(){
        HomeClient livingHomeClient = RetrofitServiceGenerator.createService(HomeClient.class, API_TOKEN);
        Call<List<Endpoint>> call = livingHomeClient.endpoints(""+PREFERRED_HUB_ID);
        //Log.d("OkHttp", String.format("Sending request %s ",call.request().toString()));
        call.enqueue(new Callback<List<Endpoint>>() {
            @Override
            public void onResponse(Call<List<Endpoint>> call, Response<List<Endpoint>> response) {
                if (response.isSuccessful()) {
                    for (Endpoint endpoint : response.body()) {
                        if (!devices.contains(endpoint.getName()))
                            devices.add(endpoint.getName());
                    }
                    //TODO what if the user got no endpoints?
                    setGridLayout(response.body());
                } else {
                    // error response, no access to resource?
                    //TODO if the device no longer has access to the endpoints (because he got ininvited) ask for select new hub or manage acordingly.
                    Log.d("Error", response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Endpoint>> call, Throwable t) {
                // something went completely south (like no internet connection)
                showNoInternetMessage();
                Log.d("Error", t.getMessage());
                AnalyticsApplication.getInstance().trackException(new Exception(t));
            }
        });
    }

    /**
     * This method will try to obtain all the Living hubs the user owns/is invited.
     */
    private void getHubs(){
        HomeClient livingHomeClient = RetrofitServiceGenerator.createService(HomeClient.class, API_TOKEN);
        // Create a call instance for looking up Retrofit contributors.
        Call<List<Hub>> call = livingHomeClient.hubs();
        //Log.d("OkHttp", String.format("Sending request %s ",call.request().toString()));
        call.enqueue(new Callback<List<Hub>>() {
            @Override
            public void onResponse(Call<List<Hub>> call, Response<List<Hub>> response) {
                if (response.isSuccessful()) {
                    //TODO what happens if the user got no hub?
                    showSelectHubDialog(response.body());
                } else {
                    // error response, no access to resource?
                    Log.d("Error", response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Hub>> call, Throwable t) {
                // something went completely south (like no internet connection)
                showNoInternetMessage();
                Log.d("Error", t.getMessage());
                AnalyticsApplication.getInstance().trackException(new Exception(t));
            }
        });
    }

    /**
     * If the user have not selected a preferred hub to use, this method will be called and a pupop
     * will be displayed asking for one.
     * @param hubs the list of all available hubs of the user.
     */
    private void showSelectHubDialog(final List<Hub> hubs){
        String[] temp = new String[hubs.size()];
        for (int i=0;i<hubs.size();i++)
            temp[i] = hubs.get(i).getCustom_name();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a home");
        builder.setItems(temp, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PREFERRED_HUB_ID = hubs.get(which).getId();
                getEndpoints();
                savePreferredHub();
            }
        });
        builder.show();
    }

    /**
     * This method will save the user preferred hub for using until he change it or do logout.
     * Since every user can own multiple hubs, is necessary to pick at least one for this session.
     * The user can change the hub or this will be deleted on logout.
     */
    private void savePreferredHub(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_HUB, String.valueOf(PREFERRED_HUB_ID));
        editor.commit();
    }

    /**
     * This method created the grid layout of the home activity. It will take the list of endpoints and will output it into the UI
     * @param listaEndpoints The list of devices (endpoints) the user has access in this hub
     */
    private void setGridLayout(final List<Endpoint> listaEndpoints){
        //ArrayAdapter<String> ssidAdapter = new ArrayAdapter<String>(HomeActivity.this, android.R.layout.simple_dropdown_item_1line, devices);
        final GridView homeMainGridView = (GridView) findViewById(R.id.gridView);
        homeMainGridView.setAdapter(new GridDevicesAdapter(HomeActivity.this, listaEndpoints));
        homeMainGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(HomeActivity.this, listaEndpoints.get(position).getName(),
                        Toast.LENGTH_SHORT).show();
                processEndpointClick(listaEndpoints.get(position));
            }
        });

    }

    /**
     * This method will show a no internet error message to the user
     */
    private void showNoInternetMessage() {
        Toast.makeText(HomeActivity.this, getResources().getString(R.string.toast_missing_internet),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is in charge of opening the corresponding controller for each device depending the UI parameter of the device
     * @param endpoint the endpoint clicked by the user in the app UI
     */
    private void processEndpointClick(Endpoint endpoint){
        switch (endpoint.getUi_class_command()){
            case "ui-sonos":
                openSONOSController(endpoint);
                break;
            case "ui-lock":
                if (endpoint.getEndpoint_type().equalsIgnoreCase("zwave")) {
                    openZwaveLockController(endpoint);
                }else{
                    //TODO
                }
                break;
        }
    }

    /**
     * This method open the activity that handles a SONOS sound player
     * @param sonos the endpoint representing a wifi sonos system
     */
    private void openSONOSController(Endpoint sonos){
        Intent intent = new Intent(this, SonosControllerActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putExtra(EXTRA_OBJECT, sonos);
        startActivity(intent);
    }

    /**
     * This method open the activity that handles a simple door lock device (No keypad)
     * @param lock the endpoint representing a z-wave door lock
     */
    private void openZwaveLockController(Endpoint lock){
        Intent intent = new Intent(this, ZwaveLockControllerActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putExtra(EXTRA_OBJECT, lock);
        startActivity(intent);
    }

    /**
     * This interface implements a Retrofit interface for the Home Activity
     */
    public interface HomeClient {
        /**
         * This function get all the endpoints inside a hub given a hub id.
         * @param hub_id The ID of the hub from which to get the endpoints
         * @return A list containing all the endpoints
         */
        @GET("hubs/{hub_id}/endpoints/")
        Call<List<Endpoint>> endpoints(
                @Path("hub_id") String hub_id
        );

        /**
         * This function obtains all the hubs for the current user
         * @return A list of all the hubs the user is available to query
         */
        @GET("hubs/")
        Call<List<Hub>> hubs();

        /**
         * This function obtains a particular hub given a valid hub ID
         * @param hub_id The ID of the hub to get
         * @return The hub matching the hub ID
         */
        @GET("hubs/{hub_id}/")
        Call<Hub> hub(
                @Path("hub_id") String hub_id
        );
    }
}
