package co.ar_smart.www.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Endpoint;

import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;

public class ZwaveLockControllerActivity extends AppCompatActivity {

    /**
     * The backend auth token
     */
    private String API_TOKEN = "";
    private Endpoint endpoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sonos_controller);
        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        endpoint = intent.getParcelableExtra(EXTRA_OBJECT);
        Log.d(">>>>>>",endpoint.toString());
    }
}
