package co.ar_smart.www.modes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.ar_smart.www.helpers.ModeManager;
import co.ar_smart.www.helpers.RetrofitServiceGenerator;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.Mode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

import static co.ar_smart.www.helpers.Constants.DEFAULT_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MODE;
import static co.ar_smart.www.helpers.Constants.PREFS_NAME;
import static co.ar_smart.www.helpers.Constants.PREF_HUB;

public class ListModesActivity extends AppCompatActivity {

    private String API_TOKEN = "";
    private ArrayList<Mode> modes;
    private ListView list;
    private ProgressBar progress;
    private ArrayAdapter<Mode> adapter;
    private Activity myact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_stand);
        modes=new ArrayList<>();
        myact=this;

        list = (ListView) findViewById(R.id.list_view_stand);
        progress = (ProgressBar) findViewById(R.id.progressListStand);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.label_deletedev_activity_title));
        }

        Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        adapter=new ArrayAdapter<Mode>(ListModesActivity.this, android.R.layout.simple_list_item_1, modes){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view=convertView;
                if(view==null)
                {

                    view=getLayoutInflater().inflate(R.layout.row_list_stand,null);
                    TextView lb=(TextView)view.findViewById(R.id.label_item_stand);
                    lb.setText(modes.get(position).getName());
                    lb=(TextView)view.findViewById(R.id.label_item2_stand);
                    lb.setText(modes.get(position).getName());
                    ImageView i=(ImageView) view.findViewById(R.id.icon_list_stand);
                    i.setImageDrawable(ContextCompat.getDrawable(myact, R.drawable.connect_btn));
                }
                //chk.setChecked(checked[position]);
                return view;
            }
        };

        getModes(1);



    }

    private void deleteMode(int hubid,int id) {
        ModeManager.removeMode(hubid, id, API_TOKEN, new ModeManager.ModeCallbackInterface() {
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

    public void getModes(int hubid)
    {
        ModeManager.getModes(hubid, API_TOKEN, new ModeManager.ModeCallbackInterface(){
            @Override
            public void onFailureCallback() {
            }

            @Override
            public void onSuccessCallback(List<Mode> lista) {

                    for(Mode mode: lista)
                    {
                        modes.add(mode);
                    }

                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Mode e=modes.get(position);
                            Intent i=new Intent(ListModesActivity.this,EditModeActivity.class);
                            i.putExtra(EXTRA_MESSAGE,API_TOKEN);
                            Bundle b=new Bundle();
                            b.putParcelable(EXTRA_MODE,e);
                            i.putExtras(b);
                            startActivity(i);
                        }
                    });

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
}
