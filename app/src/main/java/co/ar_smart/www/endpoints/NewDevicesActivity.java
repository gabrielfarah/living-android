package co.ar_smart.www.endpoints;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.helpers.RetrofitServiceGenerator;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.EndpointClassCommand;
import co.ar_smart.www.pojos.Hub;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import static co.ar_smart.www.helpers.Constants.ACTION_ADD;
import static co.ar_smart.www.helpers.Constants.DEFAULT_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_ACTION;
import static co.ar_smart.www.helpers.Constants.EXTRA_CATEGORY_DEVICE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_TYPE_DEVICE;
import static co.ar_smart.www.helpers.Constants.JSON;
import static co.ar_smart.www.helpers.Constants.LOGIN_URL;
import static co.ar_smart.www.helpers.Constants.PREFS_NAME;
import static co.ar_smart.www.helpers.Constants.PREF_HUB;
import static co.ar_smart.www.helpers.Constants.TYPE_DEVICE_WIFI;
import static co.ar_smart.www.helpers.Constants.TYPE_DEVICE_ZWAVE;
import static co.ar_smart.www.helpers.Constants.BASE_URL;

public class NewDevicesActivity extends AppCompatActivity {

    private String API_TOKEN = "";
    private ArrayList<EndpointClassCommand> command;
    private ArrayList<Endpoint> devices;
    private String task = "";
    private ListView list;
    private ProgressBar progress;
    private int sol;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_devices);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.label_user_management_title));
        }

        sol=0;
        devices=new ArrayList<>();


        list = (ListView) findViewById(R.id.list_new_devices);
        progress = (ProgressBar) findViewById(R.id.progress);




        Intent intent = getIntent();

        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);

        updateDevices(null);
        //devices.add(new Endpoint());

    }

    public void  addWifiService()
    {
        String json = "";
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(BASE_URL+"hubs/1/command/add/wifi/")
                .header("Authorization", "JWT "+API_TOKEN)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .post(body)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String jsonData = response.body().string();
                response.body().close();
                if (!response.isSuccessful()) {
                } else {
                    try {
                        JSONObject jObject = new JSONObject(jsonData);
                        String ur[]=jObject.getString("url").split("/");
                        task=ur[6];
                        getDevices(task);
                    } catch (JSONException e) {

                    }
                }
            }
        });

    }

    public void  addZWaveService()
    {

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
    public void getDevices(final String taskid)
    {
        NewDeviceClient client = RetrofitServiceGenerator.createService(NewDeviceClient.class, API_TOKEN);
        Call<ResponseEndPoints> call2 = client.getEndPoint(""+1, task);

        call2.enqueue(new Callback<ResponseEndPoints>()
        {
            @Override
            public void onResponse(Call<ResponseEndPoints> call, Response<ResponseEndPoints> response)
            {
                if (response.isSuccessful()) {
                    ResponseEndPoints li=response.body();
                    //devices.add(response.body());
                    String sta=li.getStatus();
                    if(sta.equals("done"))
                    {
                        List<Endpoint> en=li.getResponse();
                        for(Endpoint endp: en)
                        {
                            devices.add(endp);
                        }

                        progress.setVisibility(View.GONE);
                        list.setVisibility(View.VISIBLE);

                        list.setAdapter(new ArrayAdapter<Endpoint>(NewDevicesActivity.this, android.R.layout.simple_list_item_1, devices)
                        {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view=convertView;
                                if(view==null)
                                {

                                    view=getLayoutInflater().inflate(R.layout.row_devices_add,null);
                                    TextView lb=(TextView)view.findViewById(R.id.labelDevadd);
                                    lb.setText(devices.get(position).getName());
                                    lb=(TextView)view.findViewById(R.id.labelDevCategoryadd);
                                    lb.setText(devices.get(position).getCategory().getCat());

                                }
                                //chk.setChecked(checked[position]);
                                return view;
                            }
                        });
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                Intent i=new Intent(NewDevicesActivity.this,EditDeviceActivity.class);
                                Bundle b=new Bundle();
                                b.putParcelable("EndPoint", devices.get(position));
                                i.putExtras(b);
                                i.putExtra(EXTRA_CATEGORY_DEVICE,devices.get(position).getCategory());
                                i.putExtra(EXTRA_MESSAGE,API_TOKEN);
                                i.putExtra(EXTRA_ACTION,ACTION_ADD);
                                startActivity(i);
                            }
                        });


                    }
                    else
                    {
                        if(sol<30) {
                            sol = sol + 5;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getDevices(taskid);
                                }
                            }, 5000);
                        }
                        else
                        {
                            Toast.makeText(NewDevicesActivity.this, "Ningun disposivo Encontrado",
                                    Toast.LENGTH_SHORT).show();
                            progress.setVisibility(View.GONE);
                            list.setVisibility(View.VISIBLE);
                        }
                    }
                }
                else {

                }
            }

            @Override
            public void onFailure(Call<ResponseEndPoints> call, Throwable t) {

            }
        });

    }


    private interface NewDeviceClient {

        @POST("hubs/{hub_id}/command/add/wifi/")
        Call<List<EndpointClassCommand>> addwifi(
                @Path("hub_id") String hub_id
        );

        @POST("hubs/{hub_id}/command/add/zwave/")
        Call<List<Endpoint>> addzwave(
                @Path("hub_id") String hub_id
        );

        @GET("hubs/{hub_id}/command/response/{task_id}/")
        Call<ResponseEndPoints> getEndPoint(
                @Path("hub_id") String hub_id,@Path("task_id") String task_id
        );
        @POST("hubs/{hub_id}/command/response/{task_id}/")
        Call<List<Endpoint>> postEndPoint(
                @Path("hub_id") String hub_id,@Path("task_id") String task_id
        );
    }


    private int getPreferredHub(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        // Get values using keys
        return Integer.parseInt(settings.getString(PREF_HUB, DEFAULT_HUB));
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

    public void updateDevices(View v)
    {
        String addType = getIntent().getStringExtra(EXTRA_TYPE_DEVICE);
        API_TOKEN = getIntent().getStringExtra(EXTRA_MESSAGE);

        progress.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);

        if(addType!=null)
        {
            switch (addType)
            {
                case TYPE_DEVICE_WIFI:
                    addWifiService();
                    break;
                case TYPE_DEVICE_ZWAVE:
                    addZWaveService();
                    break;
            }
        }
    }

}
