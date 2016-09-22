package co.ar_smart.www.endpoints;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import co.ar_smart.www.adapters.GridDevicesAdapter;
import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.helpers.RetrofitServiceGenerator;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Endpoint;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

import static co.ar_smart.www.helpers.Constants.ACTION_ADD;
import static co.ar_smart.www.helpers.Constants.ACTION_EDIT;
import static co.ar_smart.www.helpers.Constants.DEFAULT_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_ACTION;
import static co.ar_smart.www.helpers.Constants.EXTRA_LIST_PARCELABLE_FIRST;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE_PREF_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;
import static co.ar_smart.www.helpers.Constants.EXTRA_ROOM;
import static co.ar_smart.www.helpers.Constants.EXTRA_UID;
import static co.ar_smart.www.helpers.Constants.PREFS_NAME;
import static co.ar_smart.www.helpers.Constants.PREF_HUB;

public class EditDeviceActivity extends AppCompatActivity {

    /**
     * New device or edit device button
     */
    private MenuItem btnPost;
    /**
     * Icon device
     */
    private String icon;
    /**
     * Room device
     */
    private String room;
    /**
     * Token generated by JWT
     */
    private String API_TOKEN;
    /**
     * Represent the current instance
     */
    private Activity myact;
    /**
     * Name Device
     */
    private EditText txtName;
    /**
     * Current device
     */
    private Endpoint dev;
    private int PREFERRED_HUB_ID;
    private TextView select_room;
    private TextView selected_room;
    private TextView select_icon;
    private ImageView selected_icon;
    private ArrayList<Endpoint> endpoint_devices = new ArrayList<>();

    public static String bodyToString(final RequestBody request) {
        try {
            final Buffer buffer = new Buffer();
            if (request != null)
                request.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_device);
        myact=this;
        Intent intent = getIntent();
        dev = intent.getParcelableExtra(EXTRA_OBJECT);
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        Log.d("SIGNATURE", API_TOKEN);
        PREFERRED_HUB_ID = intent.getIntExtra(EXTRA_MESSAGE_PREF_HUB, -1);
        endpoint_devices = intent.getParcelableArrayListExtra(EXTRA_LIST_PARCELABLE_FIRST);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(dev.getName());
        }
        Button btnIcon=(Button) findViewById(R.id.btnEditIcon);
        Button btnRoom=(Button) findViewById(R.id.btnEditRoom);
        Button saveButton = (Button) findViewById(R.id.save_device_button);
        txtName=(EditText) findViewById(R.id.txtNameDev);

        String act=myact.getIntent().getStringExtra(EXTRA_ACTION);

        room = "";
        icon = "";

        select_room = (TextView) findViewById(R.id.select_room_text_view);
        selected_room = (TextView) findViewById(R.id.selected_room_text_view);
        select_icon = (TextView) findViewById(R.id.select_icon_text_view);
        selected_icon = (ImageView) findViewById(R.id.selected_icon_image_view);

        if(act.equals(ACTION_EDIT))
        {
            if (dev.getRoom() != null && !dev.getImage().isEmpty()) {
                room = dev.getRoom();
            }
            if (dev.getImage() != null && !dev.getImage().isEmpty()) {
                icon = dev.getImage();
            }
            txtName.setText(dev.getName());
        }

        btnIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(EditDeviceActivity.this,EditIconActivity.class);
                i.putExtra(EXTRA_ROOM,dev.getRoom());
                startActivityForResult(i,1);
            }
        });
        btnRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(EditDeviceActivity.this,EditRoomActivity.class);
                i.putExtra(EXTRA_MESSAGE,API_TOKEN);
                i.putExtra(EXTRA_ROOM,dev.getRoom());
                startActivityForResult(i,2);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateActions();
            }
        });
    }

    private void validateActions() {
        if (checkFields()) {
            String act = myact.getIntent().getStringExtra(EXTRA_ACTION);
            switch (act) {
                case Constants.ACTION_EDIT:
                    editDevice();
                    break;
                case ACTION_ADD:
                    registerDevice();
                    break;
                default:
            }
        } else {
            Toast.makeText(EditDeviceActivity.this, getResources().getString(R.string.warning_fields), Toast.LENGTH_SHORT).show();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_devices_menu, menu);
        btnPost=menu.findItem(R.id.btnpostdevices);
        btnPost.setEnabled(true);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnpostdevices:
                validateActions();
                return true;
            case android.R.id.home:
                // app icon in action bar clicked; go home
                validateActions();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        validateActions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                icon=data.getStringExtra("result");
                if (checkFields()) {
                }
                //btnPost.setIcon(ContextCompat.getDrawable(myact, R.drawable.new_cross_btn));
            }else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
        else if (requestCode == 2) {
            if(resultCode == Activity.RESULT_OK){
                room=data.getStringExtra("result");
                if (checkFields()) {
                    //btnPost.setIcon(ContextCompat.getDrawable(myact, R.drawable.new_cross_btn));
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    /**
     * This method check if the fields are empty.
     */
    private boolean checkFields()
    {
        if (room != null && !room.isEmpty()) {
            Log.d("ROOM", room);
            select_room.setVisibility(View.VISIBLE);
            selected_room.setVisibility(View.VISIBLE);
            selected_room.setText(room);
        }
        if (icon != null && !icon.isEmpty()) {
            Log.d("ICON RET", icon);
            select_icon.setVisibility(View.VISIBLE);
            selected_icon.setVisibility(View.VISIBLE);
            selected_icon.setImageResource(GridDevicesAdapter.getDrawableFromString(icon));
        }
        return !txtName.getText().toString().equals("") && !room.equals("") && !icon.equals("");
    }

    /**
     * This method send the the new device to the backend
     */
    public void registerDevice()
    {
        dev.setAtributes(txtName.getText().toString(),icon,room);
        RegDeviceClient client = RetrofitServiceGenerator.createService(RegDeviceClient.class, API_TOKEN);
        Call<Endpoint> call = client.regDevice(getPreferredHub(), dev);
        call.enqueue(new Callback<Endpoint>()
        {
            @Override
            public void onResponse(Call<Endpoint> call, Response<Endpoint> response)
            {
                if (response.isSuccessful()) {
                    dev = response.body();
                    //devices.add(response.body());
                    showDialog();
                }
                else{
                    Log.d("Fallo", call.request().toString());
                    try {
                        Log.d("Fallo", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Endpoint> call, Throwable t) {
                Toast.makeText(EditDeviceActivity.this, R.string.error_registering_device,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This method sends the devices with the new fields to the backend
     */
    public void editDevice()
    {
        dev.setAtributes(txtName.getText().toString(),icon,room);

        String uid=getIntent().getStringExtra(EXTRA_UID);

        RegDeviceClient client = RetrofitServiceGenerator.createService(RegDeviceClient.class, API_TOKEN);
        Call<Endpoint> call = client.editDev(getPreferredHub(), uid,dev);
        call.enqueue(new Callback<Endpoint>()
        {
            @Override
            public void onResponse(Call<Endpoint> call, Response<Endpoint> response)
            {
                if (response.isSuccessful()) {
                    dev = response.body();
                    //devices.add(response.body());
                    showDialog();
                }
            }

            @Override
            public void onFailure(Call<Endpoint> call, Throwable t) {

            }
        });
    }

    /**
     * Show a notification message dialog when the device was sent to the backend
     */
    public void showDialog()
    {
        final Dialog dialog = new Dialog(EditDeviceActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_device_added);

        TextView t=(TextView) dialog.findViewById(R.id.titleAddDevice) ;
        t.setText(dev.getName());

        Button dialogButton = (Button) dialog.findViewById(R.id.btnDialogDevAdd);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //Replace updated device in devices list
                Log.d("SameSame", "" + dev.getId());
                Log.d("Lista com", endpoint_devices.toString());
                if (endpoint_devices.indexOf(dev) != -1) {
                    endpoint_devices.set(endpoint_devices.indexOf(dev), dev);
                }
                Intent i=new Intent(EditDeviceActivity.this,ManagementEndpointsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra(EXTRA_OBJECT, dev);
                i.putExtra(EXTRA_MESSAGE, API_TOKEN);
                i.putExtra(EXTRA_MESSAGE_PREF_HUB, PREFERRED_HUB_ID);
                i.putParcelableArrayListExtra(EXTRA_OBJECT, endpoint_devices);
                setResult(RESULT_OK, i);
                startActivity(i);
            }
        });

        dialog.show();
    }

    private String getPreferredHub() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        // Get values using keys
        return settings.getString(PREF_HUB, DEFAULT_HUB);
    }

    /**
     * This interface implements a Retrofit interface for the EditDeviceActivity
     */
    private interface RegDeviceClient {
        @POST("hubs/{hub_id}/endpoints/")
        Call<Endpoint> regDevice(@Path("hub_id") String hub_id,@Body Endpoint en );

        @PATCH("hubs/{hub_id}/endpoints/{endp_id}/")
        Call<Endpoint> editDev(@Path("hub_id") String hub_id,@Path("endp_id") String endp_id,@Body Endpoint en);
    }
}
