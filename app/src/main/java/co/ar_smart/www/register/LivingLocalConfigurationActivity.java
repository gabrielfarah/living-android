package co.ar_smart.www.register;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import co.ar_smart.www.living.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static co.ar_smart.www.helpers.Constants.JSON;
import static co.ar_smart.www.helpers.Constants.LIVING_HOTSPOT_PASSWORD;
import static co.ar_smart.www.helpers.Constants.LIVING_HOTSPOT_SSID;
import static co.ar_smart.www.helpers.Constants.LIVING_URL;

/**
 * This activity will add the information of the user home into the hub using the hub local LAN
 */
public class LivingLocalConfigurationActivity extends AppCompatActivity {

    /**
     * Constant for asking the permission
     */
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    /**
     * http client for doing the requests
     */
    private static OkHttpClient client = new OkHttpClient();
    /**
     * List of all the names of networks the phone can see
     */
    private List<String> SSIDList = new ArrayList<String>();
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
    /**
     * The check of the webserver worked
     */
    private boolean getWorked = false;
    /**
     * Number of tries at sending the data to the local webserver
     */
    private int counter = 0;


    /**
     * This method will send the user input into the hub local webserver. It will try to force the use of wifi but only in devices > api 21
     *
     * @param userWifiSSID     The wifi network name
     * @param userWifiPassword The Wifi password
     * @param userHomeTimeZone The user home timezone
     */
    private void sendWifiDataToHub(final String userWifiSSID, final String userWifiPassword, final String userHomeTimeZone) {
        final String json = "{\"ssid\":\"" + userWifiSSID + "\",\"password\":\"" + userWifiPassword + "\",\"timezone\":\"" + userHomeTimeZone + "\"}";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
                    if (client == null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            client = new OkHttpClient.Builder().socketFactory(network.getSocketFactory()).build();
                        }
                    }
                    getFromHub();
                    sendPost(json, userWifiSSID);
                }
            });
        } else {
            client = new OkHttpClient();
            getFromHub();
            sendPost(json, userWifiSSID);
        }
    }

    /**
     * This method sends a POST request to the hub local webserver will all the connection parameters
     *
     * @param json         the string formatted json object with the information
     * @param userWifiSSID the name of the user home WIFI network
     */
    private void sendPost(String json, String userWifiSSID) {
        boolean finished = true;
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(LIVING_URL)
                .post(body)
                .build();
        if (getWorked) {
            try {
                Response response = client.newCall(request).execute();
                String resp = response.body().string();
                if (resp.contains("ssid not found")) {
                    Toast.makeText(mContext, String.format(getResources().getString(R.string.welcome_messages), userWifiSSID), Toast.LENGTH_LONG).show();
                    finished = false;
                }
                if (finished) {
                    disconnectFromLivingWifi();
                    Intent i = new Intent(mContext, RegisteredHubActivity.class);
                    startActivity(i);
                }
                Log.d("FUNCIONO", resp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (counter > 1) {
                Toast.makeText(mContext, getResources().getString(R.string.toast_error_connecting_to_local_webserver), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * This method validates if the webserver running in the hub is accesible to the phone doing a GET request
     */
    public void getFromHub() {
        Request request = new Request.Builder()
                .url(LIVING_URL)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            String resp = response.body().string();
            if (resp.contains("connected")) {
                getWorked = true;
            } else {
                counter += 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_living_local_configuration);
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

        ArrayAdapter<String> ssidAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, SSIDList);
        final AutoCompleteTextView ssidAutoCompleteView = (AutoCompleteTextView) findViewById(R.id.available_wifi_networks);
        ssidAutoCompleteView.setAdapter(ssidAdapter);
        if (SSIDList.size() > 0) ssidAutoCompleteView.setText(SSIDList.get(0));

        // Get all the available Time Zones present in the Device
        String[] timeZones = getAvailableTimeZones();
        // Get the default device Time Zone
        String defaultTimeZone = TimeZone.getDefault().getID();
        ArrayAdapter<String> timeZonesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, timeZones);
        //Get the spinner from the UI
        final Spinner timeZonesSpinner = (Spinner) findViewById(R.id.time_zones_available);
        // Set the spinner with the Time Zones
        timeZonesSpinner.setAdapter(timeZonesAdapter);
        // Get position in the adapter of default Time Zone
        int spinnerPosition = timeZonesAdapter.getPosition(defaultTimeZone);
        // Set the default according to value
        timeZonesSpinner.setSelection(spinnerPosition);


        final TextView passwordText = (TextView) findViewById(R.id.localConfPasswordText);
        Button submitButton = (Button) findViewById(R.id.submitLocalConfigurationButton);
        if (submitButton != null) {
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userHomeTimeZone = timeZonesSpinner.getSelectedItem().toString();
                    String userWifiSSID = ssidAutoCompleteView.getText().toString();
                    String userWifiPassword = passwordText.getText().toString();
                    boolean valid = validateUserInput(userWifiSSID, userWifiPassword, userHomeTimeZone);
                    if (valid) {
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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
}
