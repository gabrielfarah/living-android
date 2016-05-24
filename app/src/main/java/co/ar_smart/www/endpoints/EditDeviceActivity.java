package co.ar_smart.www.endpoints;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.helpers.RetrofitServiceGenerator;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Endpoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

import static co.ar_smart.www.helpers.Constants.ACTION_ADD;
import static co.ar_smart.www.helpers.Constants.ACTION_EDIT;
import static co.ar_smart.www.helpers.Constants.EXTRA_ACTION;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_ROOM;
import static co.ar_smart.www.helpers.Constants.EXTRA_UID;

public class EditDeviceActivity extends AppCompatActivity {

    private MenuItem btnPost;
    private String icon;
    private String room;
    private String API_TOKEN;
    private Activity myact;
    private EditText txtName;
    private Endpoint dev;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_device);
        myact=this;
        dev=getIntent().getExtras().getParcelable("EndPoint");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(dev.getName());
        }
        Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);

        Button btnIcon=(Button) findViewById(R.id.btnEditIcon);
        Button btnRoom=(Button) findViewById(R.id.btnEditRoom);
        txtName=(EditText) findViewById(R.id.txtNameDev);

        String act=myact.getIntent().getStringExtra(EXTRA_ACTION);

            room="";
            icon="";

        if(act.equals(ACTION_EDIT))
        {
            if(dev.getRoom()!=null)
            room=dev.getRoom();
            if(dev.getRoom()!=null)
            icon=dev.getImage();
            txtName.setText(dev.getName().toString());
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
                startActivityForResult(i,2);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_devices_menu, menu);
        btnPost=menu.findItem(R.id.btnpostdevices);
        btnPost.setEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnpostdevices:
                if(checkFields())
                {
                    String act=myact.getIntent().getStringExtra(EXTRA_ACTION);
                    switch (act)
                    {
                        case Constants.ACTION_EDIT:
                            editDevice();
                            break;
                        case ACTION_ADD:
                            registerDevice();
                            break;
                            default:
                    }
                }
                else
                {
                    Toast.makeText(EditDeviceActivity.this, getResources().getString(R.string.warning_fields), Toast.LENGTH_SHORT).show();
                }
                return true;
            case android.R.id.home:
                // app icon in action bar clicked; go home
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                icon=data.getStringExtra("result");
                if(checkFields())
                btnPost.setIcon(ContextCompat.getDrawable(myact, R.drawable.new_cross_btn));
            }else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
        else if (requestCode == 2) {
            if(resultCode == Activity.RESULT_OK){
                room=data.getStringExtra("result");
                if(checkFields())
                    btnPost.setIcon(ContextCompat.getDrawable(myact, R.drawable.new_cross_btn));
            }else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    private boolean checkFields()
    {
        if(!txtName.getText().equals("") && !room.equals("") && !icon.equals(""))
        {
            return true;
        }
        return false;
    }

    public void registerDevice()
    {
        dev.setAtributes(txtName.getText().toString(),icon,room);
        RegDeviceClient client = RetrofitServiceGenerator.createService(RegDeviceClient.class, API_TOKEN);
        Log.d("ENDPOINT", dev.getEndpoint_type() + " " + dev.getEndpoint_type() + " " + dev.getCategory() + " " + dev.getRoom());
        Call<Endpoint> call = client.regDevice(""+1, dev);
        Log.d("REQUEST", call.request().toString());
        call.enqueue(new Callback<Endpoint>()
        {
            @Override
            public void onResponse(Call<Endpoint> call, Response<Endpoint> response)
            {
                if (response.isSuccessful()) {
                    Endpoint li=response.body();
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
                Toast.makeText(EditDeviceActivity.this, "Error al registrar el dispositivo",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void editDevice()
    {
        dev.setAtributes(txtName.getText().toString(),icon,room);

        String uid=getIntent().getStringExtra(EXTRA_UID);

        RegDeviceClient client = RetrofitServiceGenerator.createService(RegDeviceClient.class, API_TOKEN);
        Call<Endpoint> call = client.editDev(""+1, uid,dev);
        call.enqueue(new Callback<Endpoint>()
        {
            @Override
            public void onResponse(Call<Endpoint> call, Response<Endpoint> response)
            {
                if (response.isSuccessful()) {
                    Endpoint li=response.body();
                    //devices.add(response.body());
                    showDialog();
                }
                else{

                }
            }

            @Override
            public void onFailure(Call<Endpoint> call, Throwable t) {

            }
        });
    }


    public void showDialog()
    {


        // custom dialog
        final Dialog dialog = new Dialog(EditDeviceActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_device_added);

        TextView t=(TextView) dialog.findViewById(R.id.titleAddDevice) ;
        t.setText(dev.getName());

        Button dialogButton = (Button) dialog.findViewById(R.id.btnDialogDevAdd);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent i=new Intent(EditDeviceActivity.this,ManagementEndpointsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        dialog.show();
    }

    /**
     * This interface implements a Retrofit interface for the Home Activity
     */
    private interface RegDeviceClient {
        @POST("hubs/{hub_id}/endpoints/")
        Call<Endpoint> regDevice(@Path("hub_id") String hub_id,@Body Endpoint en );

        @PATCH("hubs/{hub_id}/endpoints/{endp_id}/")
        Call<Endpoint> editDev(@Path("hub_id") String hub_id,@Path("endp_id") String endp_id,@Body Endpoint en);
    }
}
