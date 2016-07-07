package co.ar_smart.www.modes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        adapter = new ModeAdapter(ModeManagementActivity.this, modes, PREFERRED_HUB_ID, API_TOKEN, this, endpoint_devices);
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
        //TODO el add deberia de empezarce para resultado y al agregar un modo se deberia de agregar a la lista del UI de aca
        Intent intent = new Intent(this, NewModeActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putExtra(EXTRA_MESSAGE_PREF_HUB, PREFERRED_HUB_ID);
        intent.putParcelableArrayListExtra(EXTRA_OBJECT, modes);
        intent.putParcelableArrayListExtra(EXTRA_ADDITIONAL_OBJECT, endpoint_devices);
        startActivity(intent);
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

    public void editModes(View v)
    {
        Intent intent = new Intent(this, ListModesActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        intent.putParcelableArrayListExtra(EXTRA_ADDITIONAL_OBJECT, endpoint_devices);
        startActivity(intent);
    }

    public void delModes(View v)
    {
        Intent intent = new Intent(this, DeleteModesActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        startActivity(intent);
    }

}
