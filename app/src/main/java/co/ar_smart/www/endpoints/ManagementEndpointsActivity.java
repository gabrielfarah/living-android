package co.ar_smart.www.endpoints;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.living.LoginActivity;
import co.ar_smart.www.living.R;

import static co.ar_smart.www.helpers.Constants.ACTION_EDIT;
import static co.ar_smart.www.helpers.Constants.DEFAULT_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_ACTION;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_TYPE_DEVICE;
import static co.ar_smart.www.helpers.Constants.PREFS_NAME;
import static co.ar_smart.www.helpers.Constants.PREF_EMAIL;
import static co.ar_smart.www.helpers.Constants.PREF_HUB;
import static co.ar_smart.www.helpers.Constants.PREF_JWT;
import static co.ar_smart.www.helpers.Constants.PREF_PASSWORD;
import static co.ar_smart.www.helpers.Constants.TYPE_DEVICE_WIFI;
import static co.ar_smart.www.helpers.Constants.TYPE_DEVICE_ZWAVE;

public class ManagementEndpointsActivity extends AppCompatActivity {

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_endpoint);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.label_user_management_title));
        }

        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        // Get values using keys

        token= settings.getString(PREF_JWT, "000");



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

    public void addWifi(View v)
    {
        Intent i=new Intent(ManagementEndpointsActivity.this,NewDevicesActivity.class);
        i.putExtra(EXTRA_TYPE_DEVICE,TYPE_DEVICE_WIFI);
        i.putExtra(EXTRA_MESSAGE,token);
        startActivity(i);
    }

    public void addZWave(View v)
    {
        Intent i=new Intent(ManagementEndpointsActivity.this,NewDevicesActivity.class);
        i.putExtra(EXTRA_TYPE_DEVICE,TYPE_DEVICE_ZWAVE);
        i.putExtra(EXTRA_MESSAGE,token);
        startActivity(i);
    }

    public void delWifi(View v)
    {
        Intent i=new Intent(ManagementEndpointsActivity.this,DeleteDeviceActivity.class);
        i.putExtra(EXTRA_TYPE_DEVICE,TYPE_DEVICE_WIFI);
        i.putExtra(EXTRA_MESSAGE,token);
        startActivity(i);
    }

    public void delZWave(View v)
    {
        Intent i=new Intent(ManagementEndpointsActivity.this,DeleteDeviceActivity.class);
        i.putExtra(EXTRA_TYPE_DEVICE,TYPE_DEVICE_ZWAVE);
        i.putExtra(EXTRA_MESSAGE,token);
        startActivity(i);
    }

    public void editDevices(View v)
    {
        Intent i=new Intent(ManagementEndpointsActivity.this,DevicesActivity.class);
        i.putExtra(EXTRA_MESSAGE,getIntent().getStringExtra(EXTRA_MESSAGE));
        i.putExtra(EXTRA_MESSAGE,token);
        startActivity(i);
    }
}
