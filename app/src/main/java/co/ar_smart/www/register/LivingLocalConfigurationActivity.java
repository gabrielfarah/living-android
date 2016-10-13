package co.ar_smart.www.register;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import co.ar_smart.www.living.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static co.ar_smart.www.helpers.Constants.LIVING_HOTSPOT_PASSWORD;
import static co.ar_smart.www.helpers.Constants.LIVING_HOTSPOT_SSID;
import static co.ar_smart.www.helpers.Constants.LIVING_URL;

/**
 * This activity will add the information of the user home into the hub using the hub local LAN
 */
public class LivingLocalConfigurationActivity extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    /**
     * Constant for asking the permission
     */
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    /**
     * Constant for asking the second permission
     */
    private static final int PERMISSIONS_REQUEST_CHANGE_NETWORK_STATE = 1;
    /**
     * Constant for asking the third permission
     */
    private static final int PERMISSIONS_REQUEST_WRITE_SETTINGS = 2;
    /**
     * http client for doing the requests
     */
    private static OkHttpClient client = new OkHttpClient();
    /**
     * List of all the names of networks the phone can see
     */
    private List<String> SSIDList = new ArrayList<>();
    /**
     * Broadcast receiver for the permissions
     */
    private BroadcastReceiver broadcastReceiver;
    /**
     * True if the network adapter was started
     */
    private boolean wasNetworkStarted = false;
    /**
     * The application context
     */
    private Context mContext;
    private Button submitButton;

    /**
     * This method will send the user input into the hub local webserver. It will try to force the use of wifi but only in devices > api 21
     *
     * @param userWifiSSID     The wifi network name
     * @param userWifiPassword The Wifi password
     * @param userHomeTimeZone The user home timezone
     */
    private void sendWifiDataToHub(final String userWifiSSID, final String userWifiPassword, final String userHomeTimeZone) {
        final String json = "{\"ssid\":\"" + userWifiSSID + "\",\"password\":\"" + userWifiPassword + "\",\"timezone\":\"" + userHomeTimeZone + "\"}";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != -1 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) != -1) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkRequest request;
            request = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
            connectivityManager.requestNetwork(request, new ConnectivityManager.NetworkCallback() {
                /**
                 * Called when the framework connects and has declared a new network ready for use.
                 * This callback may be called more than once if the {@link Network} that is
                 * satisfying the request changes.
                 *
                 * This method will be called on non-UI thread, so beware not to use any UI updates directly.
                 *
                 * @param network The {@link Network} of the satisfying network.
                 */
                @Override
                public void onAvailable(final Network network) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        client = new OkHttpClient.Builder().socketFactory(network.getSocketFactory()).build();
                    }
                }
            });
        }
        testConnectionWithHub(json, userWifiSSID);
    }

    /**
     * This method sends a POST request to the hub local webserver will all the connection parameters
     *
     * @param json         the string formatted json object with the information
     * @param userWifiSSID the name of the user home WIFI network
     */
    private void sendPost(final String json, final String userWifiSSID) {
        final boolean[] finished = {true};
        Log.d("JSON", json);
        postMethod(json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("FAIL2", " " + e.getMessage() + " " + call.request().toString());
                showDialog();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("PASS2", " " + response.message() + " " + call.request().toString());
                if (response.isSuccessful()) {
                    String resp = response.body().string();
                    if (response.isSuccessful() && resp.contains("ssid not found")) {
                        Toast.makeText(mContext, String.format(getResources().getString(R.string.welcome_messages), userWifiSSID), Toast.LENGTH_LONG).show();
                        finished[0] = false;
                    }
                } else {
                    // Request not successful
                    showDialog();
                }
            }
        });
        if (finished[0]) {
            disconnectFromLivingWifi();
            Intent i = new Intent(mContext, VerifyConfigurationCompleteActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Call postMethod(String json, Callback callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(LIVING_URL)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    private Call getMethod(Callback callback) {
        Request request = new Request.Builder()
                .url(LIVING_URL)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    /**
     * This method validates if the webserver running in the hub is accesible to the phone doing a GET request
     * @param json the information to send to the hub
     * @param userWifiSSID the user's home WIFI name
     */
    public void testConnectionWithHub(final String json, final String userWifiSSID) {
        getMethod(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //AnalyticsApplication.getInstance().trackException(e);
                showDialog();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("Va a PASO?", " " + response.message() + " " + call.request().toString());
                if (response.isSuccessful()) {
                    String resp = response.body().string();
                    if (resp.contains("connected")) {
                        Log.d("PASO?", resp + " " + response.message() + " " + call.toString());
                        enableButton();
                        sendPost(json, userWifiSSID);
                    } else {
                        showDialog();
                    }
                } else {
                    // Request not successful
                    //AnalyticsApplication.getInstance().trackEvent("RegistrationFail", response.message(), call.toString());
                    showDialog();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_living_local_configuration);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.label_configuration1_title));
        }
        mContext = this;
        askAndroidPermissions();
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != -1) {
            WifiManager wifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
            //WifiInfo info = wifiManager.getConnectionInfo();
            /*if(!info.getSSID().isEmpty()){
                currentNetwork = info.getSSID();
                Log.d("DEBUG",currentNetwork);
            }else{
                registerNetworkChangeBroadcastReceiver();
                wifiManager.setWifiEnabled(true);
            }*/
            wifiManager.setWifiEnabled(true);
            List<ScanResult> results = wifiManager.getScanResults();
            for (int i = 0; i < results.size(); i++) {
                if (!SSIDList.contains(results.get(i).SSID))
                    SSIDList.add(results.get(i).SSID);
            }
        }
        ImageView img = (ImageView)findViewById(R.id.connecting);
        img.setBackgroundResource(R.drawable.connecting_anim);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();
        ArrayAdapter<String> ssidAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, SSIDList);
        final AutoCompleteTextView ssidAutoCompleteView = (AutoCompleteTextView) findViewById(R.id.available_wifi_networks);
        ssidAutoCompleteView.setAdapter(ssidAdapter);
        if (SSIDList.size() > 0) ssidAutoCompleteView.setText(SSIDList.get(0));
        /**for (int i = 0; i < SSIDList.size(); i++)
        {
            Log.d("DEBUG",SSIDList.get(i));
        }**/

        // Get all the available Time Zones present in the Device
        String[] timeZones = getAvailableTimeZones();
        // Get the default device Time Zone
        String defaultTimeZone = TimeZone.getDefault().getID();
        ArrayAdapter<String> timeZonesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, timeZones);
        //Get the spinner from the UI
        final Spinner timeZonesSpinner = (Spinner) findViewById(R.id.time_zones_available);
        // Set the spinner with the Time Zones
        timeZonesSpinner.setAdapter(timeZonesAdapter);
        // Get position in the adapter of default Time Zone
        int spinnerPosition = timeZonesAdapter.getPosition(defaultTimeZone);
        // Set the default according to value
        timeZonesSpinner.setSelection(spinnerPosition);


        final TextView passwordText = (TextView) findViewById(R.id.localConfPasswordText);
        submitButton = (Button) findViewById(R.id.submitLocalConfigurationButton);
        if (submitButton != null) {
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userHomeTimeZone = timeZonesSpinner.getSelectedItem().toString();
                    String userWifiSSID = ssidAutoCompleteView.getText().toString();
                    String userWifiPassword = passwordText.getText().toString();
                    boolean valid = validateUserInput(userWifiSSID, userWifiPassword, userHomeTimeZone);
                    if (valid) {
                        submitButton.setEnabled(false);
                        sendWifiDataToHub(userWifiSSID, userWifiPassword, userHomeTimeZone);
                    }
                }
            });
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!wasNetworkStarted) {
                    wasNetworkStarted = true;
                    //Toast.makeText(LivingLocalConfigurationActivity.this, "Wifi State Changed!", Toast.LENGTH_SHORT).show();
                    WifiManager wifiManager = (WifiManager) LivingLocalConfigurationActivity.this.getSystemService(WIFI_SERVICE);
                    List<ScanResult> results = wifiManager.getScanResults();
                    for (int i = 0; i < results.size(); i++) {
                        if (!SSIDList.contains(results.get(i).SSID))
                            SSIDList.add(results.get(i).SSID);
                    }
                    connectToLivingHotSpot(wifiManager);
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
    }

    /**
     * This method will connect the user phone to the hub access point automatically
     *
     * @param wifiManager the phone wifi manager class
     */
    private void connectToLivingHotSpot(WifiManager wifiManager) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

        wifiConfig.SSID = String.format("\"%s\"", LIVING_HOTSPOT_SSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", LIVING_HOTSPOT_PASSWORD);
        //remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        if (netId != -1) {
            // success, can call wfMgr.enableNetwork(networkId, true) to connect
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();
        }
    }

    /**
     * This method will close the conection to the access point wifi
     */
    private void disconnectFromLivingWifi() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        wifiManager.disconnect();
    }

    /**
     * This method validates that the wifi name, the password and the timezone are all not empty
     *
     * @param ssid     the wifi name
     * @param password the wifi password
     * @param timeZone the user timezone
     * @return true if all of the variables are not empty
     */
    private boolean validateUserInput(String ssid, String password, String timeZone) {
        if (ssid.isEmpty() || password.isEmpty() || timeZone.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.toast_missing_fields), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * This method will ask the user for its permission to access the action location and will call the method onRequestPermissionsResult as a callback
     */
    private void askAndroidPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            // PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CHANGE_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CHANGE_NETWORK_STATE},
                    PERMISSIONS_REQUEST_CHANGE_NETWORK_STATE);

            // PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_SETTINGS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_SETTINGS},
                    PERMISSIONS_REQUEST_WRITE_SETTINGS);

            // PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the task you need to do.
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
            }
            case PERMISSIONS_REQUEST_CHANGE_NETWORK_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the task you need to do.
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
            }
            case PERMISSIONS_REQUEST_WRITE_SETTINGS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the task you need to do.
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
            }
        }
    }

    /**
     * This method will obtain all the available time zones stored in the device
     *
     * @return String Array containing all the names of the time zones present in the phone
     */
    private String[] getAvailableTimeZones() {
        return TimeZone.getAvailableIDs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * This method shows a dialog informing the user of a possible error and how to approach it.
     */
    public void showDialog() {
        LivingLocalConfigurationActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                submitButton.setEnabled(true);
                final Dialog dialog = new Dialog(LivingLocalConfigurationActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_local_configuration_wifi);
                Button dialogButton = (Button) dialog.findViewById(R.id.local_configuration_button_accept);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    private void enableButton() {
        LivingLocalConfigurationActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                submitButton.setEnabled(true);
            }
        });
    }
}
