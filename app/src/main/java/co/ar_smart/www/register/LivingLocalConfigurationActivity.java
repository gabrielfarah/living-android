package co.ar_smart.www.register;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.living.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static co.ar_smart.www.helpers.Constants.JSON;
import static co.ar_smart.www.helpers.Constants.LIVING_HOTSPOT_PASSWORD;
import static co.ar_smart.www.helpers.Constants.LIVING_HOTSPOT_SSID;
import static co.ar_smart.www.helpers.Constants.LIVING_URL;

public class LivingLocalConfigurationActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private static OkHttpClient client = new OkHttpClient();
    private int permissionCheck = -1;
    private List<String> SSIDList = new ArrayList<String>();
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter = new IntentFilter();
    private boolean wasNetworkStarted = false;
    private Context mContext;

    public static void getFromHub() {
        Request request = new Request.Builder()
                .url(LIVING_URL)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AnalyticsApplication.getInstance().trackException(e);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                response.body().close();
                if (!response.isSuccessful()) {
                    Log.d("fallo!!!", jsonData);
                } else {
                    try {
                        JSONObject jObject = new JSONObject(jsonData);
                        Log.d("respuesta", jObject.toString());
                    } catch (JSONException e) {
                        AnalyticsApplication.getInstance().trackException(e);
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_living_local_configuration);
        mContext = this;
        askAndroidPermissions();
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck!=-1){
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
            for (int i=0;i<results.size();i++){
                if (!SSIDList.contains(results.get(i).SSID))
                    SSIDList.add(results.get(i).SSID);
            }
        }

        ArrayAdapter<String> ssidAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, SSIDList);
        final AutoCompleteTextView ssidAutoCompleteView = (AutoCompleteTextView) findViewById(R.id.available_wifi_networks);
        ssidAutoCompleteView.setAdapter(ssidAdapter);
        if (SSIDList.size()>0)ssidAutoCompleteView.setText(SSIDList.get(0));

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
                    if (valid){
                        //TODO add real behavior
                        //sendDataToHub(userWifiSSID, userWifiPassword, userHomeTimeZone);
                        getFromHub();
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

    private void connectToLivingHotSpot(WifiManager wifiManager) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        //  This is for WPA2!! TODO check if edison is the same
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

    private void disconnectFromLivingWifi() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        wifiManager.disconnect();
    }

    private boolean validateUserInput(String ssid, String password, String timeZone){
        if (ssid.isEmpty() || password.isEmpty() || timeZone.isEmpty()){
            Toast.makeText(this, getResources().getString(R.string.toast_missing_fields), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

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

    private String[] getAvailableTimeZones(){
        return TimeZone.getAvailableIDs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    public void sendDataToHub(String userWifiSSID, String userWifiPassword, String userHomeTimeZone) {
        String json = "{\"ssid\":\"" + userWifiSSID + "\",\"password\":\"" + userWifiPassword + "\",\"timezone\":\"" + userHomeTimeZone + "\"}";
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(LIVING_URL)
                .post(body)
                .build();
        Log.d("OkHTTP:", request.toString());
        Log.d("OkHTTP:", request.body().toString());
        Log.d("OkHTTP:", json);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                AnalyticsApplication.getInstance().trackException(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                response.body().close();
                if (!response.isSuccessful()) {
                    //callback.onUnsuccessfulCallback();
                    Toast.makeText(mContext, "Try again", Toast.LENGTH_SHORT).show();
                } else {
                    disconnectFromLivingWifi();
                    Intent i = new Intent(mContext, RegisteredHubActivity.class);
                    startActivity(i);
                }
            }
        });
    }

}
