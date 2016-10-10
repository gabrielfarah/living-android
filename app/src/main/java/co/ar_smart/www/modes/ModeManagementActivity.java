package co.ar_smart.www.modes;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import co.ar_smart.www.adapters.ModeAdapter;
import co.ar_smart.www.helpers.CommandManager;
import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.helpers.ModeManager;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.Mode;

import static co.ar_smart.www.helpers.Constants.EXTRA_ADDITIONAL_OBJECT;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE_PREF_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;

public class ModeManagementActivity extends AppCompatActivity {

    /**
     * The user api token to perform requests
     */
    private String API_TOKEN;
    /**
     * The id of the hub to perform requests with
     */
    private int PREFERRED_HUB_ID;
    /**
     * The list of modes the hub contains
     */
    private ArrayList<Mode> modes = new ArrayList<>();
    /**
     * This field is the ui adapter for displaying the modes list
     */
    private ModeAdapter adapter;
    private ArrayList<Endpoint> endpoint_devices;
    private ArrayList<Mode> default_modes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_management);
        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        PREFERRED_HUB_ID = intent.getIntExtra(EXTRA_MESSAGE_PREF_HUB, -1);
        modes = intent.getParcelableArrayListExtra(EXTRA_OBJECT);
        endpoint_devices = intent.getParcelableArrayListExtra(EXTRA_ADDITIONAL_OBJECT);

        for (int i = 0; i < modes.size(); i++) {
            if (modes.get(i).getId() < 0) {
                default_modes.add(modes.get(i));
            }
        }
        modes.removeAll(default_modes);
        // Filter the sensors out of the list of endpoints. Sensors can't be inside scenes.
        // If the user wants to insert a trigger, then he must go to the inside of the sensor in the home.
        ArrayList<Endpoint> temp_remove = new ArrayList<>();
        for (int i = 0; i < endpoint_devices.size(); i++) {
            if (endpoint_devices.get(i).getUi_class_command().contains("sensor")) {
                temp_remove.add(endpoint_devices.get(i));
            }
        }
        endpoint_devices.removeAll(temp_remove);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.label_manage_scenes));
        }
        Button createNewModeButton = (Button) findViewById(R.id.add_new_mode_button);
        if (createNewModeButton != null) {
            createNewModeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addNewMode();
                }
            });
        }
        adapter = new ModeAdapter(ModeManagementActivity.this, modes);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.mode_list_view);
        if (listView != null) {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    performCommand(modes.get(position).getPayload().toString());
                }
            });
        }
    }

    private void performCommand(String command) {
        CommandManager.sendCommandWithoutResult(API_TOKEN, PREFERRED_HUB_ID, command, new CommandManager.ResponseCallbackInterface() {
            @Override
            public void onFailureCallback() {
                Constants.showNoInternetMessage(getApplicationContext());
            }

            @Override
            public void onSuccessCallback(JSONObject jObject) {
                Log.d("respuesta", jObject.toString());
            }

            @Override
            public void onUnsuccessfulCallback() {
                //TODO bad request
                Log.d("??", "??");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                responseToParent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        responseToParent();
    }

    private void addNewMode() {
        Intent intent = new Intent(this, ModeActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putExtra(EXTRA_MESSAGE_PREF_HUB, PREFERRED_HUB_ID);
        intent.putParcelableArrayListExtra(EXTRA_ADDITIONAL_OBJECT, endpoint_devices);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Mode resultingMode = data.getExtras().getParcelable(EXTRA_OBJECT);
                if (resultingMode != null) {
                    if (!modes.contains(resultingMode)) {
                        modes.add(resultingMode);
                    } else {
                        modes.set(modes.indexOf(resultingMode), resultingMode);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void responseToParent() {
        modes.addAll(default_modes);
        Intent output = new Intent();
        output.putExtra(EXTRA_OBJECT, modes);
        setResult(RESULT_OK, output);
        finish();
    }

    public void openDialog(final Mode elimMode) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_warning_delete);
        TextView txtname = (TextView) dialog.findViewById(R.id.lbl_warning_del_device);
        txtname.setText(this.getResources().getString(R.string.label_warning_delete_scene) + " " + elimMode.getName() + "?");
        Button dialogButton = (Button) dialog.findViewById(R.id.btnDel);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMode(elimMode);
                dialog.dismiss();
            }
        });

        dialogButton = (Button) dialog.findViewById(R.id.btnCancelDel);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void openEditActivity(final Mode editMode) {
        Intent i = new Intent(getApplicationContext(), ModeActivity.class);
        i.putExtra(EXTRA_MESSAGE, API_TOKEN);
        Bundle b = new Bundle();
        b.putParcelable(EXTRA_OBJECT, editMode);
        b.putParcelableArrayList(EXTRA_ADDITIONAL_OBJECT, endpoint_devices);
        b.putInt(EXTRA_MESSAGE_PREF_HUB, PREFERRED_HUB_ID);
        i.putExtras(b);
        startActivityForResult(i, 1);
    }

    private void removeMode(final Mode guest) {
        ModeManager.removeMode(PREFERRED_HUB_ID, guest.getId(), API_TOKEN, new ModeManager.ModeCallbackInterfaceDelete() {
            @Override
            public void onFailureCallback() {
                failedToAddMode();
            }

            @Override
            public void onSuccessCallback() {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.label_remove_escene_success), Toast.LENGTH_SHORT).show();
                modes.remove(guest);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onUnsuccessfulCallback() {
                failedToRemoveMode();
            }
        });
    }

    /**
     * This method displays a message when the the removing a guest fails
     */
    private void failedToRemoveMode() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.label_delete_scene_failed), Toast.LENGTH_LONG).show();
    }

    /**
     * This method shows a message to the user when the email entered is not yet registered
     */
    private void failedToAddMode() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_missing_internet), Toast.LENGTH_LONG).show();
    }

}
