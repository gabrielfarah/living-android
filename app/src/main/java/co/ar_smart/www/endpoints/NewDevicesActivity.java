package co.ar_smart.www.endpoints;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import co.ar_smart.www.helpers.CommandManager;
import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.helpers.EndpointManager;
import co.ar_smart.www.living.HomeActivity;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Category;
import co.ar_smart.www.pojos.Endpoint;

import static co.ar_smart.www.helpers.Constants.ACTION_ADD;
import static co.ar_smart.www.helpers.Constants.EXTRA_ACTION;
import static co.ar_smart.www.helpers.Constants.EXTRA_CATEGORY_DEVICE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE_PREF_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;
import static co.ar_smart.www.helpers.Constants.EXTRA_TYPE_DEVICE;
import static co.ar_smart.www.helpers.Constants.TYPE_DEVICE_WIFI;
import static co.ar_smart.www.helpers.Constants.TYPE_DEVICE_ZWAVE;

public class NewDevicesActivity extends AppCompatActivity {

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
    private int PREFERRED_HUB_ID;
    private Date addDeviceTimeoutDate;
    private String addDevicePollingURL;
    private Runnable runnableEndpointAddResponse;
    private Handler pollingResponseHandler = new Handler();
    private boolean stopHandlerFlag = false;
    private ArrayList<Endpoint> endpoints = new ArrayList<>();
    private int tryCount = 0;
    private String addType;
    private ArrayAdapter<Endpoint> adapter;
    private Button addManuallyButton;
    private TextView description;
    private TextView noDeviceMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_devices);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.label_newdev_activity_title));
        }

        list = (ListView) findViewById(R.id.list_new_devices);
        progress = (ProgressBar) findViewById(R.id.progressnewDev);
        description = (TextView) findViewById(R.id.add_device_text_view_message);
        noDeviceMessage = (TextView) findViewById(R.id.no_device_mmesage_while_adding);
        addManuallyButton = (Button) findViewById(R.id.add_device_wifi_manual_add_button);

        Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        PREFERRED_HUB_ID = intent.getIntExtra(EXTRA_MESSAGE_PREF_HUB, -1);
        addType = intent.getStringExtra(EXTRA_TYPE_DEVICE);

        adapter = new ArrayAdapter<Endpoint>(this, android.R.layout.simple_list_item_1, endpoints) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    view = getLayoutInflater().inflate(R.layout.row_list_devices, null);
                }
                TextView device_name = (TextView) view.findViewById(R.id.nameDeviceEdit);
                TextView category_name = (TextView) view.findViewById(R.id.nameCategoryEdit);
                ImageView i = (ImageView) view.findViewById(R.id.iconEdit);
                device_name.setText(endpoints.get(position).getName());
                Category cat = endpoints.get(position).getCategory();
                if (cat != null) {
                    category_name.setText(cat.getCat());
                }
                i.setImageDrawable(ContextCompat.getDrawable(NewDevicesActivity.this, R.drawable.new_cross_btn));
                return view;
            }
        };
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                finishAddingProcess(endpoints.get(position), endpoints.get(position).getCategory());
            }
        });

        Button retryButton = (Button) findViewById(R.id.add_device_wifi_try_again_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopHandlerFlag = false;
                endpoints.clear();
                adapter.notifyDataSetChanged();
                addDevice(addType);
            }
        });

        addManuallyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addWifiDeviceManually();
            }
        });

        Log.d("TIPO ADD=====", "" + addType);


        if (addType != null && addType.equalsIgnoreCase(TYPE_DEVICE_WIFI)) { // Then add a Wifi
            description.setText(getResources().getString(R.string.description_add_wifi_initial));
            addManuallyButton.setVisibility(View.GONE);
        } else if (addType != null && addType.equalsIgnoreCase(TYPE_DEVICE_ZWAVE)) { // Then add a Z-Wave
            description.setText(getResources().getString(R.string.description_add_zwave));
            addManuallyButton.setVisibility(View.GONE);
        }

        addDevice(addType);
    }

    private void finishAddingProcess(Endpoint nEndpoint, Category nCategory) {
        Intent i = new Intent(NewDevicesActivity.this, EditDeviceActivity.class);
        Bundle b = new Bundle();
        b.putParcelable(EXTRA_OBJECT, nEndpoint);
        i.putExtras(b);
        Category cat = nCategory;
        if (cat != null) {
            i.putExtra(EXTRA_CATEGORY_DEVICE, cat.getCat());
        }
        i.putExtra(EXTRA_MESSAGE, API_TOKEN);
        i.putExtra(EXTRA_ACTION, ACTION_ADD);
        startActivityForResult(i, HomeActivity.ACTIVITY_CODE_ENDPOINT);
    }

    private void addWifiDeviceManually() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(NewDevicesActivity.this);
        final Spinner spinner = new Spinner(NewDevicesActivity.this);

        String options[] = {getResources().getString(R.string.sonos_player), getResources().getString(R.string.philips_hue)};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        LinearLayout linearLayout = new LinearLayout(NewDevicesActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(edittext);
        linearLayout.addView(spinner);

        edittext.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edittext.setKeyListener(DigitsKeyListener.getInstance("0123456789."));

        alert.setMessage(getResources().getString(R.string.manually_message));
        alert.setTitle(getResources().getString(R.string.enter_ip_address_message));
        alert.setView(linearLayout);
        alert.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String ipAddress = edittext.getText().toString();
                String type = (String) spinner.getSelectedItem();
                Category category;
                Endpoint endpoint = new Endpoint();
                endpoint.setIp_address(ipAddress);
                endpoint.setEndpoint_type("wifi");
                endpoint.setUid(UUID.randomUUID().toString());
                if (type.equalsIgnoreCase(getResources().getString(R.string.sonos_player))) {
                    category = new Category("Entertainment", "001");
                    endpoint.setManufacturer_name("SONOS");
                } else {
                    category = new Category("Lighting", "002");
                    endpoint.setManufacturer_name("Philips");
                }
                finishAddingProcess(endpoint, category);
                //Log.d("Manual","IP: "+ipAddress+" Type: "+type+" Cat: "+category.toString());
            }
        });
        alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });
        alert.show();
    }

    private void addDevice(String type) {
        progress.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);
        noDeviceMessage.setVisibility(View.GONE);
        String url = "";
        if (type != null && type.equalsIgnoreCase(TYPE_DEVICE_WIFI)) { // Then add a Z-Wave
            url = "/command/add/wifi/";
        } else if (type != null && type.equalsIgnoreCase(TYPE_DEVICE_ZWAVE)) { // Then add a Wifi
            url = "/command/add/zwave/";
        }
        Log.d("PASO", "1");
        EndpointManager.sendAddEndpointCommand(API_TOKEN, PREFERRED_HUB_ID, url, new CommandManager.CommandWithResultsCallbackInterface() {
            @Override
            public void onFailureCallback() {
                Log.d("PASO", "-1");
            }

            @Override
            public void onSuccessCallback(String pollingUrl, int timeout) {
                addDeviceTimeoutDate = Constants.calculateTimeout(timeout);
                addDevicePollingURL = pollingUrl;
                loadAsyncEndpointAddDeviceResponse();
            }

            @Override
            public void onUnsuccessfulCallback() {
                Log.d("PASO", "-2");
                Constants.showDialogMessage("Only the owner of this hub can add devices", "Please ask him to perform this action.", NewDevicesActivity.this);
            }
        });
    }


    private void loadAsyncEndpointAddDeviceResponse() {
        Log.d("PASO", "1.5" + " - " + !stopHandlerFlag + " " + addDeviceTimeoutDate.after(new Date()));
        runnableEndpointAddResponse = new Runnable() {
            @Override
            public void run() {
                // while the handler is not stoped create a new request every time delta
                Date now = new Date();
                if (!stopHandlerFlag && addDeviceTimeoutDate.after(now)) {
                    processEndpointAddResponse();
                    Log.d("PASO", "2");
                    pollingResponseHandler.postDelayed(this, 3000);
                }
                // If the request timmed out, we hide the loader animation and set a message.
                if (!addDeviceTimeoutDate.after(now) && endpoints.isEmpty()) {
                    progress.setVisibility(View.GONE);
                    noDeviceMessage.setVisibility(View.VISIBLE);
                    list.setVisibility(View.GONE);
                    Log.d("Inf", endpoints.toString());
                    Log.d("Inf", tryCount + "");
                    Log.d("Inf", addType + "");
                    tryCount++;
                    if (addType != null && addType.equalsIgnoreCase(TYPE_DEVICE_WIFI) && tryCount > 1) { // Then add a Z-Wave
                        description.setText(getResources().getString(R.string.description_add_wifi));
                        addManuallyButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        };
        // start it with:
        pollingResponseHandler.post(runnableEndpointAddResponse);
    }

    private void processEndpointAddResponse() {
        Log.d("PASO", "3");
        CommandManager.getCommandResult(API_TOKEN, addDevicePollingURL, new CommandManager.ResponseCallbackInterface() {
            @Override
            public void onFailureCallback() {
                stopHandlerFlag = true;
            }

            @Override
            public void onSuccessCallback(JSONObject jObject) {
                Log.d("LOG RESPONSE ====", jObject.toString());
                try {
                    if (jObject.has("status")) {
                        if (!jObject.getString("status").equalsIgnoreCase("processing")) {
                            stopHandlerFlag = true;
                            Object response = jObject.get("response");
                            if (response instanceof JSONArray) {
                                // It's an array
                                JSONArray endpointJsonArray = (JSONArray) response;
                                Type listType = new TypeToken<List<Endpoint>>() {
                                }.getType();
                                ArrayList<Endpoint> endpointsResponse = new Gson().fromJson(endpointJsonArray.toString(), listType);
                                for (int i = 0; i < endpointsResponse.size(); i++) {
                                    if (!validateEndpoint(endpointsResponse.get(i))) {
                                        endpointsResponse.remove(i);
                                    }
                                }
                                endpoints.addAll(endpointsResponse);
                            } else if (response instanceof JSONObject) {
                                // It's an object
                                JSONObject endpointObject = (JSONObject) response;
                                Endpoint endpointsResponse = new Gson().fromJson(endpointObject.toString(), Endpoint.class);
                                if (validateEndpoint(endpointsResponse)) {
                                    endpoints.add(endpointsResponse);
                                }
                            } else {
                                // It's something else, like a string or number
                            }
                            Log.d("ENDPOINT", endpoints.toString());
                            updateUIWithResponse();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onUnsuccessfulCallback() {
                stopHandlerFlag = true;
            }
        });
    }

    /**
     * This function validates that an endpoint is valid
     *
     * @param endpointsResponse the endpoint to validate
     * @return true if is not null, if either id or node are valid and if type is valid
     */
    private boolean validateEndpoint(Endpoint endpointsResponse) {
        return endpointsResponse != null &&
                (endpointsResponse.getId() > 0 || endpointsResponse.getNode() > 0) &&
                endpointsResponse.getEndpoint_type() != null && !endpointsResponse.getEndpoint_type().isEmpty();
    }

    private void updateUIWithResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!endpoints.isEmpty()) {
                    adapter.notifyDataSetChanged();
                    progress.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                    noDeviceMessage.setVisibility(View.GONE);
                } else {
                    tryCount++;
                    if (addType != null && addType.equalsIgnoreCase(TYPE_DEVICE_WIFI) && tryCount > 1) { // Then add a Z-Wave
                        description.setText(getResources().getString(R.string.description_add_wifi));
                        addManuallyButton.setVisibility(View.VISIBLE);
                    }
                    progress.setVisibility(View.GONE);
                    list.setVisibility(View.GONE);
                    noDeviceMessage.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void stopPolling() {
        stopHandlerFlag = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        pollingResponseHandler.removeCallbacks(runnableEndpointAddResponse);
        stopPolling();
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

}
