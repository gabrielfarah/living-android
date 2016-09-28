package co.ar_smart.www.endpoints;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.helpers.CommandManager;
import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.helpers.EndpointManager;
import co.ar_smart.www.helpers.RetrofitServiceGenerator;
import co.ar_smart.www.interfaces.IHomeClient;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Endpoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE_PREF_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;

public class DeleteZwaveActivity extends AppCompatActivity {

    /**
     * Token generated by JWT
     */
    private String API_TOKEN = "";
    /**
     * Endpoints list
     */
    private ArrayList<Endpoint> devices = new ArrayList<>();
    /**
     * Task id from the new devices request
     */
    private String task = "";
    /**
     * List view of devices
     */
    private ListView list;
    /**
     * Represent the progress circle shown while the devices are been downloaded
     */
    private ProgressBar progress;
    /**
     * Integer that represent the elapsed time while the devices are been detected
     */
    private int sol;
    /**
     * Represent the current instance
     */
    private Activity myact;
    private int prefered_hub;

    /**
     * Adapter for the list view
     */
    private ArrayAdapter<Endpoint> adapter;
    private int PREFERRED_HUB_ID;
    private Date removeDeviceTimeoutDate;
    private String removeDevicePollingURL;
    private Runnable runnableEndpointRemoveResponse;
    private Handler pollingResponseHandler = new Handler();
    private boolean stopHandlerFlag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_device);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.label_newdev_activity_title));
        }
        myact=this;
        sol=0;


        list = (ListView) findViewById(R.id.list_DelDevicesHub);
        progress = (ProgressBar) findViewById(R.id.progressDelDevices);
        TextView description = (TextView) findViewById(R.id.add_device_text_view_message);
        description.setText(getResources().getString(R.string.description_delete_zwave));
        Button tryAgain = (Button) findViewById(R.id.try_again_button_delete_zwave);
        tryAgain.setVisibility(View.VISIBLE);

        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeZwaveDevice();
            }
        });

        Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        PREFERRED_HUB_ID = intent.getIntExtra(EXTRA_MESSAGE_PREF_HUB, -1);
        devices = getIntent().getParcelableArrayListExtra(EXTRA_OBJECT);
        adapter=new ArrayAdapter<Endpoint>(DeleteZwaveActivity.this, android.R.layout.simple_list_item_1, devices){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view=convertView;
                if (view == null) {

                    view = getLayoutInflater().inflate(R.layout.row_list_devices, null);
                }
                TextView device_name = (TextView) view.findViewById(R.id.nameDeviceEdit);
                TextView category_name = (TextView) view.findViewById(R.id.nameCategoryEdit);
                device_name.setText(devices.get(position).getName());
                category_name.setText(devices.get(position).getCategory().getCat());
                //chk.setChecked(checked[position]);
                return view;
            }
        };
        list.setAdapter(adapter);
        if (!devices.isEmpty()) {
            for (int i = 0; i < devices.size(); i++) {
                if (!devices.get(i).getEndpoint_type().equalsIgnoreCase("zwave")) {
                    devices.remove(i);
                }
            }
            adapter.notifyDataSetChanged();
        } else {
            getDevices();
        }
        removeZwaveDevice();
    }

    private void removeZwaveDevice() {
        progress.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);
        String url = "/command/remove/zwave/";
        Log.d("PASO", "1");
        EndpointManager.sendAddEndpointCommand(API_TOKEN, PREFERRED_HUB_ID, url, new CommandManager.CommandWithResultsCallbackInterface() {
            @Override
            public void onFailureCallback() {
                Log.d("PASO", "-1");
            }

            @Override
            public void onSuccessCallback(String pollingUrl, int timeout) {
                removeDeviceTimeoutDate = Constants.calculateTimeout(timeout);
                removeDevicePollingURL = pollingUrl;
                loadAsyncEndpointRemoveDeviceResponse();
            }

            @Override
            public void onUnsuccessfulCallback() {
                Log.d("PASO", "-2");
            }
        });
    }


    private void loadAsyncEndpointRemoveDeviceResponse() {
        Log.d("PASO", "1.5" + " - " + !stopHandlerFlag + " " + removeDeviceTimeoutDate.after(new Date()));
        runnableEndpointRemoveResponse = new Runnable() {
            @Override
            public void run() {
                // while the handler is not stoped create a new request every time delta
                Date now = new Date();
                if (!stopHandlerFlag && removeDeviceTimeoutDate.after(now)) {
                    processEndpointRemoveResponse();
                    Log.d("PASO", "2");
                    pollingResponseHandler.postDelayed(this, 3000);
                }
            }
        };
        // start it with:
        pollingResponseHandler.post(runnableEndpointRemoveResponse);
    }

    private void processEndpointRemoveResponse() {
        Log.d("PASO", "3");
        CommandManager.getCommandResult(API_TOKEN, removeDevicePollingURL, new CommandManager.ResponseCallbackInterface() {
            @Override
            public void onFailureCallback() {
                stopHandlerFlag = true;
                Constants.showNoInternetMessage(getApplicationContext());
            }

            @Override
            public void onSuccessCallback(JSONObject jObject) {
                Log.d("LOG RESPONSE ====", jObject.toString());
                try {
                    if (jObject.has("status")) {
                        if (!jObject.getString("status").equalsIgnoreCase("processing")) {
                            stopHandlerFlag = true;
                            updateUIWithResponse();
                        }
                    }
                } catch (JSONException e) {
                    AnalyticsApplication.getInstance().trackException(new Exception(e));
                }
            }

            @Override
            public void onUnsuccessfulCallback() {
                stopHandlerFlag = true;
            }
        });
    }

    public void getDevices() {
        IHomeClient livingIHomeClient = RetrofitServiceGenerator.createService(IHomeClient.class, API_TOKEN);
        Call<ArrayList<Endpoint>> call = livingIHomeClient.endpoints("" + PREFERRED_HUB_ID);
        //Log.d("OkHttp", String.format("Sending request %s ",call.request().toString()));
        call.enqueue(new Callback<ArrayList<Endpoint>>() {
            @Override
            public void onResponse(Call<ArrayList<Endpoint>> call, Response<ArrayList<Endpoint>> response) {
                if (response.isSuccessful()) {
                    devices = response.body();
                    for (int i = 0; i < devices.size(); i++) {
                        if (!devices.get(i).getEndpoint_type().equalsIgnoreCase("zwave")) {
                            devices.remove(i);
                        }
                    }
                    updateUIWithResponse();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Endpoint>> call, Throwable t) {
                // something went completely south (like no internet connection)
                Constants.showNoInternetMessage(getApplicationContext());
                t.printStackTrace();
                AnalyticsApplication.getInstance().trackException(new Exception(t));
            }
        });

    }

    private void updateUIWithResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        });
    }


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

    private void stopPolling() {
        stopHandlerFlag = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        pollingResponseHandler.removeCallbacks(runnableEndpointRemoveResponse);
        stopPolling();
    }

}
