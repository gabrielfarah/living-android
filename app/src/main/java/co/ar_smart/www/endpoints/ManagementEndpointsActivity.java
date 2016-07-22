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

    /**
     * Token generated by JWT
     */
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_endpoint);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.label_endpoint_management_title));
        }

        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        // Get values using keys

        token= settings.getString(PREF_JWT, "000");

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
     * Open add wifi activity
     */
    public void addWifi(View v)
    {
        Intent i=new Intent(ManagementEndpointsActivity.this,NewDevicesActivity.class);
        i.putExtra(EXTRA_TYPE_DEVICE,TYPE_DEVICE_WIFI);
        i.putExtra(EXTRA_MESSAGE,token);
        startActivity(i);
    }

    /**
     * Open add zwave activity
     */
    public void addZWave(View v)
    {
        Intent i=new Intent(ManagementEndpointsActivity.this,NewDevicesActivity.class);
        i.putExtra(EXTRA_TYPE_DEVICE,TYPE_DEVICE_ZWAVE);
        i.putExtra(EXTRA_MESSAGE,token);
        startActivity(i);
    }

    /**
     * Open delete wifi device activity
     */
    public void delWifi(View v)
    {
        Intent i=new Intent(ManagementEndpointsActivity.this,DeleteDeviceActivity.class);
        i.putExtra(EXTRA_TYPE_DEVICE,TYPE_DEVICE_WIFI);
        i.putExtra(EXTRA_MESSAGE,token);
        startActivity(i);
    }

    /**
     * Open delete zwave device activity
     */
    public void delZWave(View v)
    {
        Intent i=new Intent(ManagementEndpointsActivity.this,DeleteZwaveActivity.class);
        i.putExtra(EXTRA_TYPE_DEVICE,TYPE_DEVICE_ZWAVE);
        i.putExtra(EXTRA_MESSAGE,token);
        startActivity(i);
    }

    /**
     * Open edit devices activity
     */
    public void editDevices(View v)
    {
        Intent i=new Intent(ManagementEndpointsActivity.this,DevicesActivity.class);
        i.putExtra(EXTRA_MESSAGE,getIntent().getStringExtra(EXTRA_MESSAGE));
        i.putExtra(EXTRA_MESSAGE,token);
        startActivity(i);
    }
}
