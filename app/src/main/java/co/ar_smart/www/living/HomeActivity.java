package co.ar_smart.www.living;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.ar_smart.www.adapters.GridDevicesAdapter;
import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.controllers.SonosControllerActivity;
import co.ar_smart.www.controllers.ZwaveLockControllerActivity;
import co.ar_smart.www.endpoints.ManagementEndpointsActivity;
import co.ar_smart.www.helpers.JWTManager;
import co.ar_smart.www.helpers.RetrofitServiceGenerator;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.Hub;
import co.ar_smart.www.register.LivingLocalConfigurationActivity;
import co.ar_smart.www.user.ManagementUserActivity;
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
    private List<String> devices = new ArrayList<>();
    /**
     * The list view of the side panel menu
     */
    private ListView navList;
    /**
     * Main layout of this activity (conatains both content and nav menu)
     */
    private DrawerLayout mDrawerLayout;
    /**
     * The adapter for the navigation menu
     */
    private ArrayAdapter<String> navAdapter;
    /**
     * Toggle for the nav menu
     */
    private ActionBarDrawerToggle navMenuToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);

        navList = (ListView) findViewById(R.id.homeNavigationList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_home);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        addNavMenuItems();
        setupDrawer();

        loadPreferredHub();
    }

    /**
     * This method creates the left navigation menu and set the actions for each element
     */
    private void addNavMenuItems() {
        final String[] options = {"Devices", "Scenes", "Rooms", "Guests", "My Account"};
        navAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options);
        navList.setAdapter(navAdapter);

        navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Toast.makeText(HomeActivity.this, options[position], Toast.LENGTH_SHORT).show();
                        openDevicesActivity();
                        break;
                    case 1:
                        Toast.makeText(HomeActivity.this, options[position], Toast.LENGTH_SHORT).show();
                        openScenesActivity();
                        break;
                    case 2:
                        Toast.makeText(HomeActivity.this, options[position], Toast.LENGTH_SHORT).show();
                        openRoomsActivity();
                        break;
                    case 3:
                        Toast.makeText(HomeActivity.this, options[position], Toast.LENGTH_SHORT).show();
                        openGuestsActivity();
                        break;
                    case 4:
                        Toast.makeText(HomeActivity.this, options[position], Toast.LENGTH_SHORT).show();
                        openAccountActivity();
                        break;
                }
            }
        });
    }

    /**
     * This method opens the devices manager activity (from which the user can do the devices CRUD)
     */
    private void openDevicesActivity() {
        //TODO
    }

    /**
     * This method opens the scenes manager activity (from which the user can do the scenes CRUD)
     */
    private void openScenesActivity() {
        //TODO
    }

    /**
     * This method opens the rooms manager activity (from which the user can do the rooms CRUD)
     */
    private void openRoomsActivity() {
        //TODO
    }

    /**
     * This method opens the guests manager activity (from which the user can do the guests CRUD)
     */
    private void openGuestsActivity() {
        //TODO
    }

    /**
     * This method opens the user manager activity (from which the user can do his own CRUD)
     */
    private void openAccountActivity() {
        Intent intent = new Intent(this, ManagementUserActivity.class);
        startActivity(intent);
    }

    /**
     * This method setup the nav menu toggle mechanism
     * changes the activity title and sets listeners
     */
    private void setupDrawer() {
        navMenuToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.nav_menu_open, R.string.nav_menu_close) {
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getString(R.string.label_home_activity_title));
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getString(R.string.label_nav_bar_title));
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        navMenuToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(navMenuToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        navMenuToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        navMenuToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Get the feed icon and add the click action + change its color to white
        getMenuInflater().inflate(R.menu.home_menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item != null) {
                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(HomeActivity.this, "FEED", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
            }
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(ContextCompat.getColor(this, R.color.blanco), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.notification_feed_button) {
            return true;
        }
        // Activate the navigation drawer toggle
        if (navMenuToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            JWTManager.getApiToken(EMAIL, PASSWORD, new JWTManager.JWTCallbackInterface() {
                @Override
                public void onFailureCallback() {
                    showNoInternetMessage();
                }

                @Override
                public void onSuccessCallback(String nToken) {
                    API_TOKEN = nToken;
                }

                @Override
                public void onUnsuccessfulCallback() {
                    successfulLogout();
                }

                @Override
                public void onExceptionCallback() {
                    // Nothing exceptional to do in this case.
                }
            });
        }
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
                        Log.d("DEVICE:", endpoint.getName());
                        Log.d("COMMAND:", endpoint.getEndpoint_classes().get(0).getCommands().get(0).toString());
                    }
                    // If user got no endpoints redirect to management activity. set grid layout otherwise.
                    if (response.body().isEmpty()) {
                        openManagementDevicesActivity();
                    } else {
                        setGridLayout(response.body());
                    }
                } else {
                    // error response, no access to resource?
                    // if the user no longer has access to the endpoints (because he got uninvited) ask for select new hub.
                    getHubs();
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
     * This methods opens the device manager activity
     */
    private void openManagementDevicesActivity() {
        Intent intent = new Intent(this, ManagementEndpointsActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        startActivity(intent);
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
                    // If the user got hubs he can select one to use. If he do not then send it to register one activity.
                    if (!response.body().isEmpty()) {
                        showSelectHubDialog(response.body());
                    } else {
                        openRegisterHubActivity();
                    }
                } else {
                    AnalyticsApplication.getInstance().trackEvent("Weird Event", "NoAccessToHubs", "The user do not have access to the hubs? token:" + API_TOKEN);
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
     * This method opens the register new hub activity
     */
    private void openRegisterHubActivity() {
        Intent intent = new Intent(this, LivingLocalConfigurationActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        startActivity(intent);
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
        editor.apply();
    }

    /**
     * This method created the grid layout of the home activity. It will take the list of endpoints and will output it into the UI
     * @param listaEndpoints The list of devices (endpoints) the user has access in this hub
     */
    private void setGridLayout(final List<Endpoint> listaEndpoints){
        final GridView homeMainGridView = (GridView) findViewById(R.id.gridView);
        if (homeMainGridView != null) {
            homeMainGridView.setAdapter(new GridDevicesAdapter(HomeActivity.this, listaEndpoints));
            homeMainGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Toast.makeText(HomeActivity.this, listaEndpoints.get(position).getName(),
                            Toast.LENGTH_SHORT).show();
                    processEndpointClick(listaEndpoints.get(position));
                }
            });
        }
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
            default:
                AnalyticsApplication.getInstance().trackEvent("Device Image", "DoNotExist", "The device in hub:" + endpoint.getHub() + " named:" + endpoint.getName() + " the image does not correspong. image:" + endpoint.getImage());
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
