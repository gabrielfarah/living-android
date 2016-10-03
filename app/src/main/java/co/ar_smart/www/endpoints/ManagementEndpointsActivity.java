package co.ar_smart.www.endpoints;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import co.ar_smart.www.living.HomeActivity;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Endpoint;

import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE_PREF_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;
import static co.ar_smart.www.helpers.Constants.EXTRA_TYPE_DEVICE;
import static co.ar_smart.www.helpers.Constants.TYPE_DEVICE_WIFI;
import static co.ar_smart.www.helpers.Constants.TYPE_DEVICE_ZWAVE;

public class ManagementEndpointsActivity extends AppCompatActivity {

    /**
     * Token generated by JWT
     */
    private String API_TOKEN;
    private int PREFERRED_HUB_ID;
    private ArrayList<Endpoint> endpoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_endpoint);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.label_devices_activity_title));
        }

        API_TOKEN = getIntent().getStringExtra(EXTRA_MESSAGE);
        PREFERRED_HUB_ID = getIntent().getIntExtra(EXTRA_MESSAGE_PREF_HUB, -1);
        endpoints = getIntent().getParcelableArrayListExtra(EXTRA_OBJECT);
        ImageView img = (ImageView)findViewById(R.id.imgAni);
        img.setBackgroundResource(R.drawable.basic_animation);

        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                setResponseAndClose();
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
        Intent intent = new Intent(ManagementEndpointsActivity.this, NewDevicesActivity.class);
        intent.putExtra(EXTRA_TYPE_DEVICE, TYPE_DEVICE_WIFI);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putExtra(EXTRA_MESSAGE_PREF_HUB, PREFERRED_HUB_ID);
        startActivityForResult(intent, HomeActivity.ACTIVITY_CODE_ENDPOINT);
    }

    /**
     * Open add zwave activity
     */
    public void addZWave(View v)
    {
        Intent intent = new Intent(ManagementEndpointsActivity.this, NewDevicesActivity.class);
        intent.putExtra(EXTRA_TYPE_DEVICE, TYPE_DEVICE_ZWAVE);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putExtra(EXTRA_MESSAGE_PREF_HUB, PREFERRED_HUB_ID);
        startActivityForResult(intent, HomeActivity.ACTIVITY_CODE_ENDPOINT);
    }

    /**
     * Open delete wifi device activity
     */
    public void delWifi(View v)
    {
        Intent intent = new Intent(ManagementEndpointsActivity.this, DeleteDeviceActivity.class);
        intent.putExtra(EXTRA_TYPE_DEVICE, TYPE_DEVICE_WIFI);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putExtra(EXTRA_MESSAGE_PREF_HUB, PREFERRED_HUB_ID);
        intent.putParcelableArrayListExtra(EXTRA_OBJECT, endpoints);
        startActivityForResult(intent, HomeActivity.ACTIVITY_CODE_ENDPOINT_DELETE);
    }

    /**
     * Open delete zwave device activity
     */
    public void delZWave(View v)
    {
        Intent intent = new Intent(ManagementEndpointsActivity.this, DeleteZwaveActivity.class);
        intent.putExtra(EXTRA_TYPE_DEVICE, TYPE_DEVICE_ZWAVE);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putExtra(EXTRA_MESSAGE_PREF_HUB, PREFERRED_HUB_ID);
        intent.putParcelableArrayListExtra(EXTRA_OBJECT, endpoints);
        startActivityForResult(intent, HomeActivity.ACTIVITY_CODE_ENDPOINT_DELETE);
    }

    /**
     * Open edit devices activity
     */
    public void editDevices(View v)
    {
        Intent intent = new Intent(ManagementEndpointsActivity.this, DevicesActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putExtra(EXTRA_MESSAGE_PREF_HUB, PREFERRED_HUB_ID);
        intent.putParcelableArrayListExtra(EXTRA_OBJECT, endpoints);
        startActivityForResult(intent, HomeActivity.ACTIVITY_CODE_ENDPOINT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == HomeActivity.ACTIVITY_CODE_ENDPOINT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Endpoint endpoint = data.getExtras().getParcelable(EXTRA_OBJECT);
                int position = endpoints.indexOf(endpoint);
                if (position != -1) {
                    endpoints.set(position, endpoint);
                } else {
                    endpoints.add(endpoint);
                }
                Log.d("Lista com", endpoints.toString());
            }
        } else if (requestCode == HomeActivity.ACTIVITY_CODE_ENDPOINT_DELETE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                endpoints = data.getParcelableArrayListExtra(EXTRA_OBJECT);
                Log.d("ELRES2", endpoints.toString());
            }
        }
    }

    private void setResponseAndClose() {
        Intent output = new Intent();
        output.putExtra(EXTRA_OBJECT, endpoints);
        setResult(RESULT_OK, output);
        finish();//finishing activity
    }

    @Override
    public void onBackPressed() {
        setResponseAndClose();
    }

}
