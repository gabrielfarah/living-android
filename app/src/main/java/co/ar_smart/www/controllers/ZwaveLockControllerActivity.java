package co.ar_smart.www.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import org.json.JSONObject;

import co.ar_smart.www.helpers.CommandManager;
import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.zwave_lock.ZwaveLockEndpoint;

import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE_PREF_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;

public class ZwaveLockControllerActivity extends AppCompatActivity {

    /**
     * The backend auth token
     */
    private String API_TOKEN = "";
    private int HUB;
    private ZwaveLockEndpoint lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zwave_lock_controller);
        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        Endpoint endpoint = intent.getParcelableExtra(EXTRA_OBJECT);
        lock = new ZwaveLockEndpoint(endpoint);
        HUB = intent.getIntExtra(EXTRA_MESSAGE_PREF_HUB, -1);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(endpoint.getName());
        }
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Is the toggle on?
                boolean on = ((ToggleButton) view).isChecked();

                if (on) {
                    // Enable vibrate
                    sendCommand("[" + lock.getOpenCommand().toString() + "]");
                } else {
                    // Disable vibrate
                    sendCommand("[" + lock.getCloseCommand().toString() + "]");
                }
            }
        });
    }

    private void sendCommand(String command) {
        Log.d("COMMAND", command);
        CommandManager.sendCommandWithoutResult(API_TOKEN, HUB, command, new CommandManager.ResponseCallbackInterface() {
            @Override
            public void onFailureCallback() {
                Constants.showNoInternetMessage(ZwaveLockControllerActivity.this);
            }

            @Override
            public void onSuccessCallback(JSONObject jObject) {

            }

            @Override
            public void onUnsuccessfulCallback() {
                showMessage();
            }
        });
    }

    private void showMessage() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Constants.showCustomMessage(ZwaveLockControllerActivity.this, getResources().getString(R.string.try_again));
            }
        });
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
}
