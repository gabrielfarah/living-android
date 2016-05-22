package co.ar_smart.www.endpoints;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.ar_smart.www.helpers.RetrofitServiceGenerator;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Endpoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

import static co.ar_smart.www.helpers.Constants.ACTION_EDIT;
import static co.ar_smart.www.helpers.Constants.DEFAULT_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_ACTION;
import static co.ar_smart.www.helpers.Constants.EXTRA_CATEGORY_DEVICE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_UID;
import static co.ar_smart.www.helpers.Constants.PREFS_NAME;
import static co.ar_smart.www.helpers.Constants.PREF_HUB;

public class DeleteDeviceActivity extends AppCompatActivity {

    private String API_TOKEN = "";
    private ArrayList<Endpoint> devices;
    private ListView list;
    private ProgressBar progress;
    private int sol;
    private ArrayAdapter<Endpoint> adapter;
    private Activity myact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_device);
        sol=0;
        devices=new ArrayList<>();
        myact=this;

        list = (ListView) findViewById(R.id.list_DelDevicesHub);
        progress = (ProgressBar) findViewById(R.id.progressDelDevices);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.label_deletedev_activity_title));
        }

        Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        adapter=new ArrayAdapter<Endpoint>(DeleteDeviceActivity.this, android.R.layout.simple_list_item_1, devices){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view=convertView;
                if(view==null)
                {

                    view=getLayoutInflater().inflate(R.layout.row_list_devices,null);
                    TextView lb=(TextView)view.findViewById(R.id.labelDevadd);
                    lb.setText(devices.get(position).getName());
                    lb=(TextView)view.findViewById(R.id.labelDevCategoryadd);
                    lb.setText(devices.get(position).getCategory().getCat());
                    ImageView i=(ImageView) view.findViewById(R.id.iconlistad);
                    i.setImageDrawable(ContextCompat.getDrawable(myact, R.drawable.delete_btn));
                }
                //chk.setChecked(checked[position]);
                return view;
            }
        };

        getDevices();



    }

    public void getDevices()
    {
        final DevicesHubClient client = RetrofitServiceGenerator.createService(DevicesHubClient.class, API_TOKEN);
        Call<List<Endpoint>> call2 = client.getendpoints(""+1);
        devices.clear();

        call2.enqueue(new Callback<List<Endpoint>>()
        {
            @Override
            public void onResponse(Call<List<Endpoint>> call, Response<List<Endpoint>> response)
            {
                if (response.isSuccessful()) {
                    List<Endpoint> li=response.body();
                    if(true)
                    {
                        for(Endpoint endp: li)
                        {
                            devices.add(endp);
                        }

                        list.setAdapter(adapter);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                deleteEndPoint(""+1,""+3);
                            }
                        });

                        progress.setVisibility(View.GONE);
                        list.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        if(sol<30) {
                            sol = sol + 5;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getDevices();
                                }
                            }, 5000);
                        }
                    }

                }
                else {
                    Toast.makeText(DeleteDeviceActivity.this, "Error al solicitar los dispositivos",
                            Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Endpoint>> call, Throwable t) {
                Toast.makeText(DeleteDeviceActivity.this, "Error al solicitar los dispositivos",
                        Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);

            }
        });


    }

    private void deleteEndPoint(String hub,String e)
    {
        DevicesHubClient client = RetrofitServiceGenerator.createService(DevicesHubClient.class, API_TOKEN);
        Call<Endpoint> call2 = client.delendpoint(hub,e);

        call2.enqueue(new Callback<Endpoint>()
        {
            @Override
            public void onResponse(Call<Endpoint> call, Response<Endpoint> response)
            {
                if (response.isSuccessful()) {
                    getDevices();
                }
                else {

                }
            }

            @Override
            public void onFailure(Call<Endpoint> call, Throwable t) {
                Toast.makeText(DeleteDeviceActivity.this, "Error al solicitar los dispositivos",
                        Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);
            }
        });
    }

    private interface DevicesHubClient {
        @GET("hubs/{hub_id}/endpoints/")
        Call<List<Endpoint>> getendpoints(@Path("hub_id") String hub_id);

        @DELETE("hubs/{hub_id}/endpoints/{endpoint_id}/")
        Call<Endpoint> delendpoint(@Path("hub_id") String hub_id,@Path("endpoint_id") String endpoint_id);
    }


    private int getPreferredHub(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        // Get values using keys
        return Integer.parseInt(settings.getString(PREF_HUB, DEFAULT_HUB));
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
}
