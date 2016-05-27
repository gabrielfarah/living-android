package co.ar_smart.www.modes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.helpers.ModeManager;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Command;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.Mode;
import co.ar_smart.www.pojos.hue.HueEndpoint;
import co.ar_smart.www.pojos.sonos.SonosEndpoint;

import static co.ar_smart.www.helpers.Constants.EXTRA_ADDITIONAL_OBJECT;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE_PREF_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;

public class NewModeActivity extends AppCompatActivity {

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
     * This var will be filled with a user promted email
     */
    private ArrayList<Endpoint> endpoint_devices;
    private ArrayList<Triplet> sent_endpoints = new ArrayList<>();
    private TextView sceneName;

    private ArrayList<Command> payload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_mode);
        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        PREFERRED_HUB_ID = intent.getIntExtra(EXTRA_MESSAGE_PREF_HUB, -1);
        modes = intent.getParcelableArrayListExtra(EXTRA_OBJECT);
        endpoint_devices = intent.getParcelableArrayListExtra(EXTRA_ADDITIONAL_OBJECT);
        String modename=intent.getStringExtra("modename");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.label_add_scene_activity));
        }
        sceneName = (TextView) findViewById(R.id.create_new_scene_name);

        Button submit = (Button) findViewById(R.id.create_new_scene_button);
        if (submit != null) {
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addNewMode();
                }
            });
        }
        if(modename!=null)
        {
            sceneName.setText(modename);
            sceneName.setEnabled(false);
            payload =intent.getParcelableArrayListExtra("Commands");
            submit.setText("Save");
        }
        else
        {
            payload=new ArrayList<>();
        }

        createUIFromDevices();
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

    private View createLabel(String text) {
        TextView actionLabel = new TextView(this);
        actionLabel.setText(text);
        actionLabel.setId(View.generateViewId());
        actionLabel.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT));
        actionLabel.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.elementos));
        return actionLabel;
    }

    private View createButton(String text, final boolean on, final ArrayList<Triplet> filtered_endpoints, final String kind_label) {
        Button button = new Button(this);
        button.setText(text);
        button.setId(View.generateViewId());
        button.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT));
        button.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.subBarras));
        button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right_arrow_icon, 0);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Triplet> temp_endpoints = new ArrayList<>();
                for (Triplet t : filtered_endpoints) {
                    t.setOn(on);
                    t.setChecked(sent_endpoints.get(sent_endpoints.indexOf(t)).isChecked());
                    temp_endpoints.add(t);
                }
                startActivityEndpointPickerForResult(temp_endpoints, kind_label);
            }
        });
        return button;
    }

    private void startActivityEndpointPickerForResult(final ArrayList<Triplet> filtered_endpoints, String kind_label) {
        Intent intent = new Intent(this, ModeEndpointActivityPicker.class);
        intent.putExtra(EXTRA_MESSAGE, kind_label);
        intent.putParcelableArrayListExtra(EXTRA_ADDITIONAL_OBJECT, filtered_endpoints);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                ArrayList<Triplet> resultEndpoints = data.getExtras().getParcelableArrayList(EXTRA_ADDITIONAL_OBJECT);
                if (resultEndpoints != null) {
                    sent_endpoints.removeAll(resultEndpoints);
                    sent_endpoints.addAll(resultEndpoints);
                    Log.d("PICKED ENDPOINTS", sent_endpoints.toString());
                }
            }
        }
    }

    private void createUIFromDevices() {

        LinearLayout ll = (LinearLayout) findViewById(R.id.main_mode_add_linear_layout);
        assert ll != null;
        boolean music_label_added = false;
        boolean light_label_added = false;
        boolean doors_label_added = false;
        boolean shades_label_added = false;
        boolean thermostats_vents_label_added = false;
        boolean cameras_label_added = false;
        boolean outlets_label_added = false;
        ArrayList<Triplet> musicEndpoints = new ArrayList<>();
        ArrayList<Triplet> lightEndpoints = new ArrayList<>();
        ArrayList<Triplet> doorsEndpoints = new ArrayList<>();
        ArrayList<Triplet> shadesEndpoints = new ArrayList<>();
        ArrayList<Triplet> ventsThermostatsEndpoints = new ArrayList<>();
        ArrayList<Triplet> camerasEndpoints = new ArrayList<>();
        ArrayList<Triplet> outletsEndpoints = new ArrayList<>();
        for (Endpoint e : endpoint_devices) {
            sent_endpoints.add(new Triplet(false, false, e));
            switch (e.getUi_class_command()) {
                case "ui-sonos":
                    musicEndpoints.add(new Triplet(false, false, e));
                    music_label_added = true;
                    break;
                case "ui-lock":
                    doors_label_added = true;
                    doorsEndpoints.add(new Triplet(false, false, e));
                    if (e.getEndpoint_type().equalsIgnoreCase("zwave")) {
                        //TODO
                    }
                    break;
                case "ui-power-outlet":
                    outlets_label_added = true;
                    outletsEndpoints.add(new Triplet(false, false, e));
                    if (e.getEndpoint_type().equalsIgnoreCase("zwave")) {
                        //TODO
                    }
                    break;
                case "ui-shades":
                    shades_label_added = true;
                    shadesEndpoints.add(new Triplet(false, false, e));
                    if (e.getEndpoint_type().equalsIgnoreCase("zwave")) {
                        //TODO
                    }
                    break;
                case "ui-hue":
                    lightEndpoints.add(new Triplet(false, false, e));
                    light_label_added = true;
                    break;
                default:
                    AnalyticsApplication.getInstance().trackEvent("Device UI Class", "DoNotExist", "The device in hub:" + e.getHub() + " named:" + e.getName() + " the ui class does not correspond. UI:" + e.getUi_class_command());
            }
        }
        View thermostatsAndVentsTag = createLabel("Thermostats & Vents");
        View camerasTag = createLabel("Cameras");
        if (music_label_added) {
            ll.addView(createLabel("Music Players"));
            ll.addView(createButton("Play these music players", true, musicEndpoints, "play-stop"));
            ll.addView(createButton("Stop these music players", false, musicEndpoints, "play-stop"));
        }
        if (doors_label_added) {
            ll.addView(createLabel("Doors"));
            ll.addView(createButton("Open these doors", true, doorsEndpoints, "open-close"));
        }
        if (light_label_added) {
            ll.addView(createLabel("Lights"));
            ll.addView(createButton("Turn on these lights", true, lightEndpoints, "on-off"));
            ll.addView(createButton("Turn off these lights", false, lightEndpoints, "on-off"));
        }
        if (outlets_label_added) {
            ll.addView(createLabel("Power Outlets"));
            ll.addView(createButton("Turn on these outlets", true, outletsEndpoints, "on-off"));
            ll.addView(createButton("Turn off these outlets", false, outletsEndpoints, "on-off"));
        }
        if (shades_label_added) {
            ll.addView(createLabel("Shades"));
            ll.addView(createButton("Open these shades", true, shadesEndpoints, "open-close"));
            ll.addView(createButton("Close these shades", false, shadesEndpoints, "open-close"));
        }
    }

    private void addNewMode() {
        Mode newMode = new Mode();
        String mode_name = sceneName.getText().toString();
        if (mode_name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Scene name cannot be empty", Toast.LENGTH_LONG).show(); //TODO Change text
            return;
        }
        newMode.setName(mode_name);
        payload = new ArrayList<>();
        for (Triplet e : sent_endpoints) {
            if (e.isChecked()) {
                switch (e.getEndpoint().getUi_class_command()) {
                    case "ui-sonos":
                        SonosEndpoint se = new SonosEndpoint(e.getEndpoint());
                        if (e.isOn()) {
                            payload.add(se.getTurnOnCommand());
                        } else {
                            payload.add(se.getTurnOffCommand());
                        }
                        break;
                    case "ui-lock":
                        //TODO
                        break;
                    case "ui-power-outlet":
                        //TODO
                        break;
                    case "ui-shades":
                        //TODO
                        break;
                    case "ui-hue":
                        HueEndpoint ue = new HueEndpoint(e.getEndpoint());
                        if (e.isOn()) {
                            payload.add(ue.getTurnOnCommand());
                        } else {
                            payload.add(ue.getTurnOffCommand());
                        }
                        break;
                    default:
                        AnalyticsApplication.getInstance().trackEvent("Device UI Class", "DoNotExist", "The device in hub:" + e.getEndpoint().getHub() + " named:" + e.getEndpoint().getName() + " the ui class does not correspond. UI:" + e.getEndpoint().getUi_class_command());
                }
            }
        }
        if (payload.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please add at leat one action", Toast.LENGTH_LONG).show(); //TODO change text
            return;
        }
        newMode.setPayload(payload);
        Log.d("PAYLOAD FIELD", newMode.getPayload().toString());
        postModeToServer(newMode);
    }

    private void postModeToServer(Mode mode) {
        ModeManager.addMode(PREFERRED_HUB_ID, mode, API_TOKEN, new ModeManager.ModeCallbackInterface() {
            @Override
            public void onFailureCallback() {

            }

            @Override
            public void onSuccessCallback(List<Mode> guest) {

            }

            @Override
            public void onSuccessCallback() {

            }

            @Override
            public void onUnsuccessfulCallback() {

            }
        });
    }
}
