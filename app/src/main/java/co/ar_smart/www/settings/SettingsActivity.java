package co.ar_smart.www.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.helpers.RetrofitServiceGenerator;
import co.ar_smart.www.interfaces.IHomeClient;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Hub;
import co.ar_smart.www.register.TurnAPOnInstructionActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE_PREF_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;
import static co.ar_smart.www.helpers.Constants.PREFS_NAME;
import static co.ar_smart.www.helpers.Constants.PREF_HUB;

public class SettingsActivity extends AppCompatActivity {

    private String API_TOKEN;
    private int PREFERRED_HUB_ID = -1;
    private ArrayList<Hub> hubs;
    private Spinner hub_picker;
    private ArrayAdapter<Hub> dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        PREFERRED_HUB_ID = intent.getIntExtra(EXTRA_MESSAGE_PREF_HUB, -1);
        hubs = intent.getParcelableArrayListExtra(EXTRA_OBJECT);
        getHubs();
        //leer lista de hubs para cambiarlos
        //Dar opcion de cambiar datos del hub
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.label_settings_title));
        }

        hub_picker = (Spinner) findViewById(R.id.hub_list_picker_spinner);
        Button openUpdateWifiButton = (Button) findViewById(R.id.open_update_wifi_button);

        openUpdateWifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTurnAPOnInstructionActivity();
            }
        });

        dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hubs);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hub_picker.setAdapter(dataAdapter);
        hub_picker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int selected = hubs.get(position).getId();
                if (selected != PREFERRED_HUB_ID) {
                    updatePreferredHub(selected);
                    Log.d("ITEM", hubs.get(position).toString());
                    PREFERRED_HUB_ID = selected;
                    Toast.makeText(SettingsActivity.this, String.format(getResources().getString(R.string.hub_selected_toast_text), hub_picker.getItemAtPosition(position).toString()), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
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
                        hubs.clear();
                        hubs.addAll(response.body());
                        dataAdapter.notifyDataSetChanged();
                    }
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

    private void openTurnAPOnInstructionActivity() {
        Intent intent = new Intent(this, TurnAPOnInstructionActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        startActivity(intent);
    }

    /**
     * This method will load the preferred hub the user selected the last time (if any).
     */
    private void updatePreferredHub(int hub_id) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_HUB, String.valueOf(hub_id));
        editor.apply();
    }
}
