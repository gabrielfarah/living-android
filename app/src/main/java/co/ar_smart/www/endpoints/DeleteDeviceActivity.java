package co.ar_smart.www.endpoints;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import co.ar_smart.www.helpers.RetrofitServiceGenerator;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.EndpointClassCommand;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

import static co.ar_smart.www.helpers.Constants.DEFAULT_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_TYPE_DEVICE;
import static co.ar_smart.www.helpers.Constants.PREFS_NAME;
import static co.ar_smart.www.helpers.Constants.PREF_HUB;
import static co.ar_smart.www.helpers.Constants.TYPE_DEVICE_WIFI;
import static co.ar_smart.www.helpers.Constants.TYPE_DEVICE_ZWAVE;

public class DeleteDeviceActivity extends AppCompatActivity {

    private String API_TOKEN = "";
    private ArrayList<EndpointClassCommand> command;
    private ArrayList<Endpoint> devices;
    private String task = "";
    private ListView list;
    private ProgressBar progress;
    private int sol;
    private ArrayAdapter<Endpoint> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_device);
        sol=0;
        devices=new ArrayList<Endpoint>();


        list = (ListView) findViewById(R.id.list_DelDevicesHub);
        progress = (ProgressBar) findViewById(R.id.progressDelDevices);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        adapter=new ArrayAdapter<Endpoint>(DeleteDeviceActivity.this, android.R.layout.simple_list_item_1, devices);

        getDevices();



    }


    private  class ResponseEndPoints{
        private List<Endpoint> response;
        private String status;

        public List<Endpoint> getResponse() {
            return response;
        }
        public String getStatus()
        {
            return status;
        }
    }
    private class EP{
        private String name;
        private String category;
        private String room;
        public String getName()
        {
            return name;
        }
        public String getCategory()
        {
            return category;
        }
        public String getRoom()
        {
            return room;
        }

        public EP()
        {
            name="dsfds";
            category="Iluminacion";
            room="dfdssd";
        }
    }
    public void getDevices()
    {
        final DevicesHubClient client = RetrofitServiceGenerator.createService(DevicesHubClient.class, API_TOKEN);
        Call<List<Endpoint>> call2 = client.getendpoints(""+2);

        call2.enqueue(new Callback<List<Endpoint>>()
        {
            @Override
            public void onResponse(Call<List<Endpoint>> call, Response<List<Endpoint>> response)
            {
                if (response.isSuccessful()) {
                    List<Endpoint> li=response.body();
                    //devices.add(response.body());
                    //String sta=li.getStatus();
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

                                client.delendpoint(""+1,devices.get(position).getName());
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
                else if(sol<30){
                    sol=sol+5;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getDevices();
                        }
                    },5000);
                }
            }

            @Override
            public void onFailure(Call<List<Endpoint>> call, Throwable t) {

            }
        });


    }


    private interface DevicesHubClient {
        @GET("hubs/{hub_id}/endpoints/")
        Call<List<Endpoint>> getendpoints(@Path("hub_id") String hub_id);

        @DELETE("hubs/{hub_id}/endpoints/{endpoint_id}/")
        Call<List<Endpoint>> delendpoint(@Path("hub_id") String hub_id,@Path("endpoint_id") String endpoint_id);
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
