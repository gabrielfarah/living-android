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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.ar_smart.www.actions.ActionActivity;
import co.ar_smart.www.adapters.HomeGridDevicesAdapter;
import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.controllers.SonosControllerActivity;
import co.ar_smart.www.controllers.TriggerMainController;
import co.ar_smart.www.controllers.ZwaveLockControllerActivity;
import co.ar_smart.www.controllers.hue.HueControllerActivity;
import co.ar_smart.www.endpoints.EditRoomActivity;
import co.ar_smart.www.endpoints.ManagementEndpointsActivity;
import co.ar_smart.www.helpers.CommandManager;
import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.helpers.JWTManager;
import co.ar_smart.www.helpers.ModeManager;
import co.ar_smart.www.helpers.RetrofitServiceGenerator;
import co.ar_smart.www.helpers.UserManager;
import co.ar_smart.www.interfaces.IHomeClient;
import co.ar_smart.www.modes.ModeManagementActivity;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.EndpointState;
import co.ar_smart.www.pojos.Hub;
import co.ar_smart.www.pojos.Mode;
import co.ar_smart.www.pojos.User;
import co.ar_smart.www.pojos.zwave_binary.ZwaveBinaryEndpoint;
import co.ar_smart.www.pojos.zwave_level.ZwaveLevelEndpoint;
import co.ar_smart.www.register.CreatedUserActivity;
import co.ar_smart.www.settings.SettingsActivity;
import co.ar_smart.www.user.GuestManagementActivity;
import co.ar_smart.www.user.ManagementUserActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static co.ar_smart.www.helpers.Constants.DEFAULT_EMAIL;
import static co.ar_smart.www.helpers.Constants.DEFAULT_HUB;
import static co.ar_smart.www.helpers.Constants.DEFAULT_PASSWORD;
import static co.ar_smart.www.helpers.Constants.EXTRA_ADDITIONAL_OBJECT;
import static co.ar_smart.www.helpers.Constants.EXTRA_BOOLEAN;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE_PREF_HUB;
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
     * This handler will be used to update the states of all the devices every 5 seconds
     */
    private final Handler backgroundPollingHandler = new Handler();
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
    /**
     * The current user logged in
     */
    private User currentUSer;
    /**
     * The list of modes (scenes) a particular hub has
     */
    private ArrayList<Mode> modes = new ArrayList<>();
    /**
     * The list of devices (endpoints) a particular hub has
     */
    private ArrayList<Endpoint> endpoint_devices = new ArrayList<>();
    private boolean endpointsPolledsuccessfully = false;
    private boolean modesPolledSuccesfully = false;
    private boolean hubsPolledSuccesfully = false;
    private Date endpointStatesTimeoutDate;
    private String endpointStatesPollingURL;
    private boolean stopHandlerFlag = false;
    private Handler pollingResponseHandler = new Handler();
    private ArrayList<Hub> hubs = new ArrayList<>();
    private ArrayList<EndpointIcons> endpointIcons = new ArrayList<>();
    private HomeGridDevicesAdapter gridAdapter = new HomeGridDevicesAdapter(HomeActivity.this, endpoint_devices);
    private Runnable runnableEndpointStatesResponse;
    private Runnable runnableEndpointStates;
    private Context mContext;
    private boolean doubleBackToExitPressedOnce = false;
    private Button devicesButton;
    private Button scenesButton;
    private Button roomsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        mContext = this;

        navList = (ListView) findViewById(R.id.homeNavigationList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_home);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        devicesButton = (Button) findViewById(R.id.devices_home_button);
        scenesButton = (Button) findViewById(R.id.scenes_home_button);
        roomsButton = (Button) findViewById(R.id.rooms_home_button);
        devicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                devicesButton.setSelected(true);
                scenesButton.setSelected(false);
                roomsButton.setSelected(false);
            }
        });
        scenesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                devicesButton.setSelected(false);
                scenesButton.setSelected(true);
                roomsButton.setSelected(false);
            }
        });
        roomsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                devicesButton.setSelected(false);
                scenesButton.setSelected(false);
                roomsButton.setSelected(true);
            }
        });

        addNavMenuItems();
        setupDrawer();

        loadPreferredHub();
        performPushNotificationRegistration();
        loadEndpointStates();
    }

    /**
     * This method will syncronize the user push notification token in the backend
     */
    private void performPushNotificationRegistration() {
        OneSignal.startInit(this).init();
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(final String userId, String registrationId) {
                if ((userId != null)) {
                    if (!userId.isEmpty()) {
                        UserManager.getUser(API_TOKEN, new UserManager.UserCallbackInterface() {
                            @Override
                            public void onFailureCallback() {
                            }

                            @Override
                            public void onSuccessCallback(User user) {
                                currentUSer = user;
                                currentUSer.setPush_token(userId);
                                Log.d("DEBUGGG", currentUSer.toString());
                            }

                            @Override
                            public void onUnsuccessfulCallback() {
                            }
                        });
                    }
                }
            }
        });
        if (currentUSer != null) {
            UserManager.updateUser(currentUSer, API_TOKEN, new UserManager.UserCallbackInterface() {
                @Override
                public void onFailureCallback() {
                }

                @Override
                public void onSuccessCallback(User user) {
                }

                @Override
                public void onUnsuccessfulCallback() {
                    Log.d("No paso", "fuck");
                }
            });
        }
    }

    /**
     * This method creates the left navigation menu and set the actions for each element
     */
    private void addNavMenuItems() {
        final String[] options = {getResources().getString(R.string.nav_label_devices),
                getResources().getString(R.string.nav_label_scenes),
                getResources().getString(R.string.nav_label_rooms),
                getResources().getString(R.string.nav_label_guests),
                getResources().getString(R.string.nav_label_account),
                getResources().getString(R.string.nav_label_settings)};
        navAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options);
        navList.setAdapter(navAdapter);

        navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        openDevicesActivity();
                        break;
                    case 1:
                        openScenesActivity();
                        break;
                    case 2:
                        openRoomsActivity();
                        break;
                    case 3:
                        openGuestsActivity();
                        break;
                    case 4:
                        openAccountActivity();
                        break;
                    case 5:
                        openSettingsActivity();
                        break;
                }
            }
        });
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_OBJECT, hubs);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putExtra(EXTRA_MESSAGE_PREF_HUB, PREFERRED_HUB_ID);
        startActivity(intent);
    }

    private void stopPolling() {
        backgroundStopHandlerFlag = true;
        stopHandlerFlag = true;
    }

    /**
     * This method opens the devices manager activity (from which the user can do the devices CRUD)
     */
    private void openDevicesActivity() {
        Intent i = new Intent(HomeActivity.this, ManagementEndpointsActivity.class);
        i.putExtra(EXTRA_MESSAGE, API_TOKEN);
        startActivity(i);
    }

    /**
     * This method opens the scenes manager activity (from which the user can do the scenes CRUD)
     */
    private void openScenesActivity() {
        Intent intent = new Intent(this, ModeManagementActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putExtra(EXTRA_MESSAGE_PREF_HUB, PREFERRED_HUB_ID);
        intent.putParcelableArrayListExtra(EXTRA_OBJECT, modes);
        intent.putParcelableArrayListExtra(EXTRA_ADDITIONAL_OBJECT, endpoint_devices);
        startActivity(intent);
    }

    /**
     * This method opens the rooms manager activity (from which the user can do the rooms CRUD)
     */
    private void openRoomsActivity() {
        Intent intent = new Intent(this, EditRoomActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putExtra(EXTRA_MESSAGE_PREF_HUB, PREFERRED_HUB_ID);
        startActivity(intent);
    }

    /**
     * This method opens the guests manager activity (from which the user can do the guests CRUD)
     */
    private void openGuestsActivity() {
        Intent intent = new Intent(this, GuestManagementActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putExtra(EXTRA_MESSAGE_PREF_HUB, PREFERRED_HUB_ID);
        startActivity(intent);
    }

    /**
     * This method opens the user manager activity (from which the user can do his own CRUD)
     */
    private void openAccountActivity() {
        Intent intent = new Intent(this, ManagementUserActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putExtra(EXTRA_OBJECT, currentUSer);
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
                        openActionsActivity();
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

    /**
     * This method opens the actions activity (Log/Feed) of the recent things that happened at the house
     */
    private void openActionsActivity() {
        Intent intent = new Intent(this, ActionActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putExtra(EXTRA_MESSAGE_PREF_HUB, PREFERRED_HUB_ID);
        startActivity(intent);
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
        return navMenuToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


    /**
     * This method will load the preferred hub the user selected the last time (if any).
     */
    private void loadPreferredHub() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        // Get values using keys
        PREFERRED_HUB_ID = Integer.parseInt(settings.getString(PREF_HUB, DEFAULT_HUB));
        if (PREFERRED_HUB_ID == -1) {
            getHubs();
        } else {
            getEndpoints();
        }
    }

    /**
     * This method will clear the saved credentials of the user in the shared preferences.
     * It will also redirect the user to the login activity.
     */
    private void successfulLogout() {
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
    private void loadEndpointStates() {
        runnableEndpointStates = new Runnable() {
            @Override
            public void run() {
                // while the handler is not stopped create a new request every time delta
                if (!backgroundStopHandlerFlag) {
                    if (endpointsPolledsuccessfully) {
                        stopHandlerFlag = false;
                        getEndpointStates();
                    }
                    backgroundPollingHandler.postDelayed(this, 10000);
                }
            }
        };
        // start it with:
        backgroundPollingHandler.post(runnableEndpointStates);
    }

    private void getEndpointStates() {
        /**
         *[{"node":6,"state":[0,1],"mainCC":30,"sensor":0,"sleep_cap":1, "active":true},{"node":7,"state":[0,1],"mainCC":30,"sensor":0,"sleep_cap":1, "active":true},]
         *String json = "[{\"type\":\"zwave\",\"function\":\"zwave_get_all_status\",\"parameters\":{}}]";
         *
         * {"type":"zwave","function":"zwave_get_all_status","parameters":{}},
         */
        String command = "[{\"type\":\"wifi\",\"action\":\"wifi_get_all_status\",\"parameters\":{}}]";
        CommandManager.sendCommandWithResult(API_TOKEN, PREFERRED_HUB_ID, command, new CommandManager.CommandWithResultsCallbackInterface() {
            @Override
            public void onFailureCallback() {

            }

            @Override
            public void onSuccessCallback(String pollingUrl, int timeout) {
                endpointStatesTimeoutDate = Constants.calculateTimeout(timeout);
                endpointStatesPollingURL = pollingUrl;
                loadAsyncEndpointStatesResponse();
            }

            @Override
            public void onUnsuccessfulCallback() {

            }
        });
    }

    /**
     * This method will poll the server response every 2 seconds until is stopped by the flag or the timeout expires
     */
    private void loadAsyncEndpointStatesResponse() {
        runnableEndpointStatesResponse = new Runnable() {
            @Override
            public void run() {
                // while the handler is not stoped create a new request every time delta
                if (!stopHandlerFlag && endpointStatesTimeoutDate.after(new Date())) {
                    processEndpointStatesResponse();
                    pollingResponseHandler.postDelayed(this, 2000);
                }
            }
        };
        // start it with:
        pollingResponseHandler.post(runnableEndpointStatesResponse);
    }

    private void processEndpointStatesResponse() {
        CommandManager.getCommandResult(API_TOKEN, endpointStatesPollingURL, new CommandManager.ResponseCallbackInterface() {
            @Override
            public void onFailureCallback() {
                stopHandlerFlag = true;
            }

            @Override
            public void onSuccessCallback(JSONObject jObject) {
                Log.d("Response", jObject.toString());
                try {
                    if (jObject.has("status")) {
                        if (!jObject.getString("status").equalsIgnoreCase("processing")) {
                            stopHandlerFlag = true;
                            JSONArray states = jObject.getJSONArray("response");
                            Type listType = new TypeToken<List<EndpointState>>() {
                            }.getType();
                            ArrayList<EndpointState> endpointStates = new Gson().fromJson(states.toString(), listType);
                            Log.d("ENDPOINT STATES", endpointStates.toString());
                            updateEndpointStates(endpointStates);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onUnsuccessfulCallback() {
                stopHandlerFlag = true;
            }
        });
    }

    private void updateEndpointStates(ArrayList<EndpointState> endpointStates) {
        for (int i = 0; i < endpoint_devices.size(); i++) {
            for (int j = 0; j < endpointStates.size(); j++) {
                EndpointState es = endpointStates.get(j);
                Endpoint e = endpoint_devices.get(i);
                if (es.getUid() != null && e.getUid() != null && es.getUid().equalsIgnoreCase(e.getUid())) {
                    e.setState(es.getMainState());
                    e.setActive(es.isActive());

                }
                if (es.getNode() == e.getNode()) {
                    e.setState(es.getMainState());
                    e.setActive(es.isActive());
                }
                endpoint_devices.set(i, e);
                updateUIWithStates();
            }
        }
    }

    private void updateUIWithStates() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gridAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        if (PREFERRED_HUB_ID == -1) {
            PREFERRED_HUB_ID = Integer.parseInt(settings.getString(PREF_HUB, DEFAULT_HUB));
        }
        // If user paused this activity and the token expired while idled, then we must automatically
        // get a new one when this activity resumes.
        if (!JWTManager.validateJWT(API_TOKEN)) {
            // Get values using keys
            String EMAIL = settings.getString(PREF_EMAIL, DEFAULT_EMAIL);
            String PASSWORD = settings.getString(PREF_PASSWORD, DEFAULT_PASSWORD);
            JWTManager.getApiToken(EMAIL, PASSWORD, new JWTManager.JWTCallbackInterface() {
                @Override
                public void onFailureCallback() {
                    Constants.showNoInternetMessage(getApplicationContext());
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
        backgroundStopHandlerFlag = false;
        stopHandlerFlag = false;
        loadEndpointStates();
    }

    @Override
    public void onPause() {
        super.onPause();
        backgroundPollingHandler.removeCallbacks(runnableEndpointStates);
        pollingResponseHandler.removeCallbacks(runnableEndpointStatesResponse);
        stopPolling();
    }

    /**
     * This method will try to obtain all the devices (endpoints) for a given hub of the user.
     */
    private void getEndpoints() {
        IHomeClient livingIHomeClient = RetrofitServiceGenerator.createService(IHomeClient.class, API_TOKEN);
        Call<List<Endpoint>> call = livingIHomeClient.endpoints("" + PREFERRED_HUB_ID);
        //Log.d("OkHttp", String.format("Sending request %s ",call.request().toString()));
        call.enqueue(new Callback<List<Endpoint>>() {
            @Override
            public void onResponse(Call<List<Endpoint>> call, Response<List<Endpoint>> response) {
                if (response.isSuccessful()) {
                    for (Endpoint endpoint : response.body()) {
                        if (!devices.contains(endpoint.getName())) {
                            devices.add(endpoint.getName());
                            endpoint_devices.add(endpoint);
                            Log.d("DEVICE:", endpoint.getName());
                        }
                        //Log.d("COMMAND:", endpoint.getEndpoint_classes().get(0).getCommands().get(0).toString());
                    }
                    getModes();
                    // If user got no endpoints redirect to management activity. set grid layout otherwise.
                    if (response.body().isEmpty()) {
                        openManagementDevicesActivity();
                    } else {
                        setGridLayout(response.body());
                        endpointsPolledsuccessfully = true;
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
                Constants.showNoInternetMessage(getApplicationContext());
                t.printStackTrace();
                AnalyticsApplication.getInstance().trackException(new Exception(t));
            }
        });
    }

    /**
     * This method will try to obtain all the devices (endpoints) for a given hub of the user.
     */
    private void getModes() {
        IHomeClient livingIHomeClient = RetrofitServiceGenerator.createService(IHomeClient.class, API_TOKEN);
        Call<List<Mode>> call = livingIHomeClient.modes("" + PREFERRED_HUB_ID);
        //Log.d("OkHttp", String.format("Sending request %s ",call.request().toString()));
        call.enqueue(new Callback<List<Mode>>() {
            @Override
            public void onResponse(Call<List<Mode>> call, Response<List<Mode>> response) {
                if (response.isSuccessful()) {
                    for (Mode mode : response.body()) {
                        if (!modes.contains(mode))
                            modes.add(mode);
                    }
                    if (!endpoint_devices.isEmpty()) {
                        ArrayList<Mode> defaultmodes = ModeManager.getDefaultModes(endpoint_devices);
                        if (!modes.containsAll(defaultmodes)) {
                            modes.addAll(defaultmodes);
                            Log.d("MODE:", modes.toString());
                        }
                    }
                    modesPolledSuccesfully = true;
                }
            }

            @Override
            public void onFailure(Call<List<Mode>> call, Throwable t) {
                // something went completely south (like no internet connection)
                Constants.showNoInternetMessage(getApplicationContext());
                try {
                    Log.d("Error", call.request().toString());
                    Log.d("Error", call.request().body().contentLength()+"");
                } catch (Exception e) {
//                    e.printStackTrace();
                }
                t.printStackTrace();
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
    private void getHubs() {
        IHomeClient livingIHomeClient = RetrofitServiceGenerator.createService(IHomeClient.class, API_TOKEN);
        // Create a call instance for looking up Retrofit contributors.
        Call<List<Hub>> call = livingIHomeClient.hubs();
        //Log.d("OkHttp", String.format("Sending request %s ",call.request().toString()));
        call.enqueue(new Callback<List<Hub>>() {
            @Override
            public void onResponse(Call<List<Hub>> call, Response<List<Hub>> response) {
                if (response.isSuccessful()) {
                    // If the user got hubs he can select one to use. If he do not then send it to register one activity.
                    if (!response.body().isEmpty()) {
                        hubs.addAll(response.body());
                        showSelectHubDialog(hubs);
                    } else {
                        openRegisterHubActivity();
                    }
                    hubsPolledSuccesfully = true;
                } else {
                    AnalyticsApplication.getInstance().trackEvent("Weird Event", "NoAccessToHubs", "The user do not have access to the hubs? token:" + API_TOKEN);
                }
            }

            @Override
            public void onFailure(Call<List<Hub>> call, Throwable t) {
                // something went completely south (like no internet connection)
                Constants.showNoInternetMessage(getApplicationContext());
                Log.d("Error", t.getMessage());
                AnalyticsApplication.getInstance().trackException(new Exception(t));
            }
        });
    }

    /**
     * This method opens the register new hub activity
     */
    private void openRegisterHubActivity() {
        Intent intent = new Intent(this, CreatedUserActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        startActivity(intent);
    }

    /**
     * If the user have not selected a preferred hub to use, this method will be called and a pupop
     * will be displayed asking for one.
     *
     * @param hubs the list of all available hubs of the user.
     */
    private void showSelectHubDialog(final List<Hub> hubs) {
        String[] temp = new String[hubs.size()];
        for (int i = 0; i < hubs.size(); i++)
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
    private void savePreferredHub() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_HUB, String.valueOf(PREFERRED_HUB_ID));
        editor.apply();
    }

    /**
     * This method created the grid layout of the home activity. It will take the list of endpoints and will output it into the UI
     *
     * @param listaEndpoints The list of devices (endpoints) the user has access in this hub
     */
    private void setGridLayout(final List<Endpoint> listaEndpoints) {
        for (Endpoint e : listaEndpoints) {
            endpointIcons.add(new EndpointIcons(e.getImage()));
        }
        gridAdapter.notifyDataSetChanged();
        final GridView homeMainGridView = (GridView) findViewById(R.id.gridView);
        if (homeMainGridView != null) {
            homeMainGridView.setAdapter(gridAdapter);
            homeMainGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Toast.makeText(HomeActivity.this, listaEndpoints.get(position).getName(),
                            Toast.LENGTH_SHORT).show();
                    processEndpointClick(listaEndpoints.get(position));
                }
            });
            homeMainGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("What do you want to do?")
                            .setPositiveButton("Edit device", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(HomeActivity.this, "Edited", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Remove device", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(HomeActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                                }
                            });
                    // Create the AlertDialog object and return it
                    builder.create().show();
                    return true;
                }
            });
        }
    }

    /**
     * This method is in charge of opening the corresponding controller for each device depending the UI parameter of the device
     *
     * @param endpoint the endpoint clicked by the user in the app UI
     */
    private void processEndpointClick(Endpoint endpoint) {
        switch (endpoint.getUi_class_command()) {
            case "ui-sonos":
                openSONOSController(endpoint);
                break;
            case "ui-temperature-s":
                Intent i = new Intent(this, TriggerMainController.class);
                i.putExtra(EXTRA_MESSAGE, API_TOKEN);
                i.putExtra(EXTRA_MESSAGE_PREF_HUB, PREFERRED_HUB_ID);
                i.putExtra(EXTRA_OBJECT, endpoint);
                i.putExtra(EXTRA_ADDITIONAL_OBJECT, modes);
                i.putExtra(EXTRA_BOOLEAN, false);
                startActivity(i);
                break;
            case "ui-lock":
                if (endpoint.getEndpoint_type().equalsIgnoreCase("zwave")) {
                    openZwaveLockController(endpoint);
                } else {
                    //TODO
                }
                break;
            case "ui-binary-light":
                if (endpoint.getEndpoint_type().equalsIgnoreCase("zwave")) {
                    performZwaveBinaryCommand(endpoint);
                } else {
                    //TODO
                }
                break;
            case "ui-binary-outlet":
                if (endpoint.getEndpoint_type().equalsIgnoreCase("zwave")) {
                    performZwaveBinaryCommand(endpoint);
                } else {
                    //TODO
                }
                break;
            case "ui-level-light":
                if (endpoint.getEndpoint_type().equalsIgnoreCase("zwave")) {
                    performZwaveLevelCommand(endpoint);
                } else {
                    //TODO
                }
                break;
            case "ui-hue":
                openHueController(endpoint);
                break;
            default:
                AnalyticsApplication.getInstance().trackEvent("Device Image", "DoNotExist", "The device in hub:" + endpoint.getHub() + " named:" + endpoint.getName() + " the image does not correspong. image:" + endpoint.getImage());
        }
    }

    private void performZwaveLevelCommand(final Endpoint endpoint) {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final SeekBar seek = new SeekBar(this);
        seek.setMax(255);
        seek.setKeyProgressIncrement(1);
        seek.setProgress(endpoint.getState());

        popDialog.setIcon(R.drawable.light_icon);
        popDialog.setTitle("Please select your desired brightness ");
        popDialog.setView(seek);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                ZwaveLevelEndpoint le = new ZwaveLevelEndpoint(endpoint);
                sendSetCommand(le.getSetValueCommand(progress).toString());
                endpoint.setState(progress);
                endpoint_devices.set(endpoint_devices.indexOf(endpoint), endpoint);
                updateUIWithStates();
            }
        });
        // Button OK
        popDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        popDialog.create();
        popDialog.show();
    }

    private void performZwaveBinaryCommand(Endpoint endpoint) {
        ZwaveBinaryEndpoint device = new ZwaveBinaryEndpoint(endpoint);
        if (endpoint.getState() != 255) {
            sendSetCommand(device.getTurnOnCommand().toString());
            endpoint.setState(255);
        } else {
            sendSetCommand(device.getTurnOffCommand().toString());
            endpoint.setState(0);
        }
        endpoint_devices.set(endpoint_devices.indexOf(endpoint), endpoint);
        updateUIWithStates();
    }

    private void sendSetCommand(String command) {
        Log.d("SE ENVIO", command);
        CommandManager.sendCommandWithoutResult(API_TOKEN, PREFERRED_HUB_ID, "[" + command + "]", new CommandManager.ResponseCallbackInterface() {
            @Override
            public void onFailureCallback() {

            }

            @Override
            public void onSuccessCallback(JSONObject jObject) {
                Log.d("RESPONSE", jObject.toString());
            }

            @Override
            public void onUnsuccessfulCallback() {

            }
        });
    }

    private void openHueController(Endpoint endpoint) {
        Intent intent = new Intent(this, HueControllerActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putExtra(EXTRA_MESSAGE_PREF_HUB, PREFERRED_HUB_ID);
        intent.putExtra(EXTRA_OBJECT, endpoint);
        startActivity(intent);
    }

    /**
     * This method open the activity that handles a SONOS sound player
     *
     * @param sonos the endpoint representing a wifi sonos system
     */
    private void openSONOSController(Endpoint sonos) {
        Intent intent = new Intent(this, SonosControllerActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putExtra(EXTRA_MESSAGE_PREF_HUB, PREFERRED_HUB_ID);
        intent.putExtra(EXTRA_OBJECT, sonos);
        startActivity(intent);
    }

    /**
     * This method open the activity that handles a simple door lock device (No keypad)
     *
     * @param lock the endpoint representing a z-wave door lock
     */
    private void openZwaveLockController(Endpoint lock) {
        Intent intent = new Intent(this, ZwaveLockControllerActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putExtra(EXTRA_OBJECT, lock);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to Log out", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private class EndpointIcons implements co.ar_smart.www.interfaces.IDrawable {

        private String image;

        public EndpointIcons(String path) {
            image = path;
    }

        @Override
        public String getImage() {
            return image;
    }

        @Override
        public boolean isActive() {
            return false;
        }
    }
}
