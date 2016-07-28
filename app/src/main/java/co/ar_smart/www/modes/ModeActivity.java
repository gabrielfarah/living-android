package co.ar_smart.www.modes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import co.ar_smart.www.adapters.SceneListAdapter;
import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.helpers.ModeManager;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Command;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.Mode;

import static co.ar_smart.www.helpers.Constants.EXTRA_ADDITIONAL_OBJECT;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE_PREF_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;

public class ModeActivity extends AppCompatActivity {

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
    private Mode mode;
    /**
     * This var will be filled with a user promted email
     */
    private ArrayList<Endpoint> endpoint_devices;
    private ArrayList<Triplet> sent_endpoints = new ArrayList<>();
    private TextView sceneName;

    private ArrayList<Command> payload;

    private String modename;
    private int modeid;
    private SceneListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_mode);
        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        PREFERRED_HUB_ID = intent.getIntExtra(EXTRA_MESSAGE_PREF_HUB, -1);
        mode = intent.getParcelableExtra(EXTRA_OBJECT); //Get the optional mode (in the case this activity will be used as edit)
        endpoint_devices = intent.getParcelableArrayListExtra(EXTRA_ADDITIONAL_OBJECT); // List of filtered endpoints (no sensors)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.label_add_scene_activity));
        }
        sceneName = (TextView) findViewById(R.id.create_new_scene_name);
        if (mode != null && mode.getName() != null) {
            sceneName.setText(mode.getName());
        }
        adapter = new SceneListAdapter(this, mode, endpoint_devices);
        Button submit = (Button) findViewById(R.id.create_new_scene_button);
        if (submit != null) {
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("El modo:", adapter.getMode().getPayload().toString());
                    if (!sceneName.getText().toString().isEmpty()) {
                        if (!adapter.getMode().getPayload().isEmpty()) {
                            Mode theMode = adapter.getMode();
                            theMode.setName(sceneName.getText().toString());
                            if (mode != null) {
                                // We are creating a new mode so PUT it
                                editMode(adapter.getMode());
                            } else {
                                // We are creating a new mode so POST it
                                postModeToServer(adapter.getMode());
                            }
                        } else {
                            Constants.showCustomMessage(ModeActivity.this, getString(R.string.select_at_least_action));
                        }
                    } else {
                        Constants.showCustomMessage(ModeActivity.this, getString(R.string.error_scene_name_empty));
                    }
                }
            });
        }
        ListView endpoint_list = (ListView) findViewById(R.id.scene_endpoint_list_list_view);
        if (endpoint_list != null) {
            endpoint_list.setAdapter(adapter);
            endpoint_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //createAndShowAlertDialog(modes.get(position));
                    Log.d("El endpoint:", endpoint_devices.get(position).getName());
                    if (view != null) {
                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.endpoint_in_scene_check_box);
                        checkBox.setChecked(!checkBox.isChecked());
                    }
                }
            });
        }
    }


    /*Intent intent = new Intent();
    intent.putExtra(EXTRA_ADDITIONAL_OBJECT, endpoint_devices);
    setResult(RESULT_OK, intent);
    finish();*/

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



    private void postModeToServer(final Mode mode) {
        ModeManager.addMode(PREFERRED_HUB_ID, mode, API_TOKEN, new ModeManager.ModeCallbackInterface() {
            @Override
            public void onFailureCallback() {

            }

            @Override
            public void onUnsuccessfulCallback() {
                Toast.makeText(getApplicationContext(), R.string.error_doing_request, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccessCallback(Mode body) {
                Log.d("ID", body.getPayload().toString() + " " + body.getId());
                responseToParent(body);
            }
        });
    }

    private void responseToParent(Mode mode) {
        Intent output = new Intent();
        output.putExtra(EXTRA_OBJECT, mode);
        setResult(RESULT_OK, output);
        finish();
    }

    private void editMode(Mode mode) {
        ModeManager.editMode(PREFERRED_HUB_ID, mode, API_TOKEN, new ModeManager.ModeCallbackInterface() {
            @Override
            public void onFailureCallback() {

            }

            @Override
            public void onUnsuccessfulCallback() {

            }

            @Override
            public void onSuccessCallback(Mode body) {
                responseToParent(body);
            }
        });
    }
}
