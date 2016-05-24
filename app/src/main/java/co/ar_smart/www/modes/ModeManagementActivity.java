package co.ar_smart.www.modes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.ar_smart.www.adapters.ModeAdapter;
import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.helpers.CommandManager;
import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.helpers.ModeManager;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.Mode;
import co.ar_smart.www.pojos.hue.HueEndpoint;
import co.ar_smart.www.pojos.sonos.SonosEndpoint;

import static co.ar_smart.www.helpers.Constants.EXTRA_ADDITIONAL_OBJECT;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE_PREF_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;

public class ModeManagementActivity extends AppCompatActivity {

    /**
     * The user api token to perform requets
     */
    private String API_TOKEN;
    /**
     * The id of the hub to perform requets with
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
    /**
     * This var will be filled with a user promted email
     */
    private String new_guest_email_str = "";
    private ArrayList<Endpoint> endpoint_devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_management);
        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        PREFERRED_HUB_ID = intent.getIntExtra(EXTRA_MESSAGE_PREF_HUB, -1);
        modes = intent.getParcelableArrayListExtra(EXTRA_OBJECT);
        endpoint_devices = intent.getParcelableArrayListExtra(EXTRA_ADDITIONAL_OBJECT);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.label_manage_modes));
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
                    //createAndShowAlertDialog(modes.get(position));
                    Log.d("El modo:", modes.get(position).getPayload().toString());
                    performCommand(modes.get(position).getPayload().toString());
                }
            });
        }
        createUIFromDevices();
    }

    private View createLabel(String text) {
        TextView actionLabel = new TextView(this);
        actionLabel.setText(text);
        actionLabel.setId(View.generateViewId());
        actionLabel.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT));
        return actionLabel;
    }

    private void createUIFromDevices() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.linear_layout_insert_devices_options);
        for (Endpoint e : endpoint_devices) {
            switch (e.getUi_class_command()) {
                case "ui-sonos":
                    Log.d("meto modos1", "sonos");
                    SonosEndpoint sonosEndpoint = new SonosEndpoint(e);
                    Log.d("ACTIONs", "SONOS");
                    View musicTag = createLabel("Music Players");
                    if (ll != null && musicTag.getParent() == ll) {
                        ll.addView(musicTag);
                    }
                    Log.d("ACTION", "PLAY this music player");
                    Log.d("ACTION", "   Sonos  - With this song");
                    Log.d("ACTION", "Stop this music player");
                    break;
                case "ui-lock":
                    if (e.getEndpoint_type().equalsIgnoreCase("zwave")) {
                        //TODO
                    } else {
                        //TODO
                    }
                    break;
                case "ui-hue":
                    Log.d("meto modos1", "hue");
                    HueEndpoint hueEndpoint = new HueEndpoint(e);
                    Log.d("ACTIONs", "HUE");
                    View lightsTag = createLabel("Lights");
                    if (ll != null && lightsTag.getParent() == ll) {
                        ll.addView(lightsTag);
                    }
                    Log.d("ACTION", "Turn on these hue lights");
                    Log.d("ACTION", "   HUE  - With this color");
                    Log.d("ACTION", "Turn off these hue lights");
                    break;
                default:
                    AnalyticsApplication.getInstance().trackEvent("Device UI Class", "DoNotExist", "The device in hub:" + e.getHub() + " named:" + e.getName() + " the ui class does not correspond. UI:" + e.getUi_class_command());
            }
        }
    }

    /**
     * This method send a "play track" command to the sonos
     */
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
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method will ask the user to input an email address and will try to add a new guest using this email.
     * The user must be registered for the request to succeed.
     */
    private void addNewMode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.label_add_guest_by_email));

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(getResources().getString(R.string.label_add_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new_guest_email_str = input.getText().toString();
                if (!new_guest_email_str.isEmpty()) {
                }
                //addNewModeFromEmail();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.label_cancel_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * This method creates a dialog for the user to validate or cancel the elimination of a guest
     *
     * @param guest the user to be confirmed if eliminated or not
     */
    private void createAndShowAlertDialog(final Mode guest) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.label_confirmation_remove_guest) + guest.getName() + "?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeUser(guest);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * This method removes a guest from the guest list of this hub
     *
     * @param guest the guest to be removed from the hub
     */
    private void removeUser(final Mode guest) {
        ModeManager.removeMode(PREFERRED_HUB_ID, guest.getId(), API_TOKEN, new ModeManager.ModeCallbackInterface() {
            @Override
            public void onFailureCallback() {
                failedToRemoveMode();
            }

            @Override
            public void onSuccessCallback(List<Mode> guest) {
            }

            @Override
            public void onSuccessCallback() {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.label_guest_removed_message), Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.label_failed_remove_guest), Toast.LENGTH_LONG).show();
    }

    /**
     * This method shows a message to the user when the email entered is not yet registered
     */
    private void failedToAddMode() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.label_only_failed_add_guest), Toast.LENGTH_LONG).show();
    }
}
