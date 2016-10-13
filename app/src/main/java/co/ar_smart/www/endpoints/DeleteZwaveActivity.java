package co.ar_smart.www.endpoints;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Endpoint;

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
     * Adapter for the list view
     */
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
            getSupportActionBar().setTitle(getString(R.string.label_remove_device_activity_title));
        }


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
        /*adapter=new ArrayAdapter<Endpoint>(DeleteZwaveActivity.this, android.R.layout.simple_list_item_1, devices){
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
        list.setAdapter(adapter);*/
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
                            int deleted_id = jObject.getInt("id");
                            stopHandlerFlag = true;
                            updateUIWithResponse(deleted_id);
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

    private void finishActivity() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_OBJECT, devices);
        setResult(RESULT_OK, intent);
        finish();//finishing activity
    }

    private void updateUIWithResponse(final int deleted_id) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(View.GONE);
                //list.setVisibility(View.VISIBLE);
                Endpoint deleted = null;
                for (Endpoint temp : devices) {
                    if (temp.getId() == deleted_id) {
                        deleted = temp;
                        break;
                    }
                }
                if (deleted != null) {
                    devices.remove(deleted);
                    finishActivity();
                }
            }
        });
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finishActivity();
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
