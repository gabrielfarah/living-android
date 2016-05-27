package co.ar_smart.www.modes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IntegerRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.ar_smart.www.endpoints.EditDeviceActivity;
import co.ar_smart.www.helpers.ModeManager;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Command;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.Mode;

import static co.ar_smart.www.helpers.Constants.ACTION_EDIT;
import static co.ar_smart.www.helpers.Constants.DEFAULT_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_ACTION;
import static co.ar_smart.www.helpers.Constants.EXTRA_ADDITIONAL_OBJECT;
import static co.ar_smart.www.helpers.Constants.EXTRA_CATEGORY_DEVICE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MODE;
import static co.ar_smart.www.helpers.Constants.EXTRA_UID;
import static co.ar_smart.www.helpers.Constants.PREFS_NAME;
import static co.ar_smart.www.helpers.Constants.PREF_HUB;

public class EditModeActivity extends AppCompatActivity {

    private String API_TOKEN = "";
    private ArrayList<Command> commands;
    private ArrayList<Endpoint> endpoints;
    private ListView list;
    private ProgressBar progress;
    private ArrayAdapter<CustomBinder> adapter;
    private Activity myact;
    private Mode mode;
    private ArrayList<CustomBinder> binders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_stand);
        commands=new ArrayList<>();
        myact=this;
        endpoints=new ArrayList<>();
        binders=new ArrayList<>();
        mode=getIntent().getExtras().getParcelable(EXTRA_MODE);

        list = (ListView) findViewById(R.id.list_view_stand);
        progress = (ProgressBar) findViewById(R.id.progressListStand);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.label_selectmodedevice_activity_title));
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i=new Intent(EditModeActivity.this,ListCommandsActivity.class);
                i.putParcelableArrayListExtra("Commands",binders.get(position).cm);
                Bundle b=new Bundle();
                b.putParcelable(EXTRA_MODE,mode);
                i.putExtras(b);
                i.putExtra(EXTRA_MESSAGE,API_TOKEN);
                i.putParcelableArrayListExtra(EXTRA_ADDITIONAL_OBJECT,endpoints);
                startActivity(i);
            }
        });


        Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        loadEndPoints(1);

        adapter=new ArrayAdapter<CustomBinder>(EditModeActivity.this, android.R.layout.simple_list_item_1, binders){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view=convertView;
                if(view==null)
                {
                    view=getLayoutInflater().inflate(R.layout.row_list_stand,null);
                    TextView lb=(TextView)view.findViewById(R.id.label_item_stand);
                    lb.setText(binders.get(position).en.getName());
                    lb=(TextView)view.findViewById(R.id.label_item2_stand);
                    lb.setText(binders.get(position).en.getUi_class_command());
                    ImageView i=(ImageView) view.findViewById(R.id.icon_list_stand);
                    i.setImageDrawable(ContextCompat.getDrawable(myact, R.drawable.connect_btn));
                }
                //chk.setChecked(checked[position]);
                return view;
            }
        };

        //getModes(1);
    }

    /**
     * This method get all devices
     * @param hubid id preferred hub
     */
    public void loadEndPoints(int hubid)
    {
        ModeManager.getEndPoints(hubid, API_TOKEN, new ModeManager.EndPointCallbackInterface(){
            @Override
            public void onFailureCallback() {
            }

            @Override
            public void onSuccessCallback(List<Endpoint> lista) {

                for(Endpoint endpoint: lista)
                {
                    endpoints.add(endpoint);
                }
                loadBinders();
                list.setAdapter(adapter);
                progress.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccessCallback(){

            }

            @Override
            public void onUnsuccessfulCallback(){
            }
        });
    }

    /**
     * this method get preferred hub
     */
    private int getPreferredHub(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
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

    /**
     * This method bind endoint to each command in an object CustomBinder
     */
    public void loadBinders() {
        for(Endpoint e:endpoints)
        {
            ArrayList<Command> table=new ArrayList<>();;
            for(Command c:mode.getPayload())
            {
                int x=c.getEndpoint_id();
                String y=""+e.getId();
                if(x==Integer.parseInt(y))
                {
                    table.add(c);
                }
            }
            if(table.size()!=0)
            binders.add(new CustomBinder(table,e));
        }
    }

    private class CustomBinder
    {
        public Endpoint en;
        public ArrayList<Command> cm;

        public CustomBinder(ArrayList<Command> pcm, Endpoint pen)
        {
            en=pen;
            cm=pcm;
        }
    }

}
