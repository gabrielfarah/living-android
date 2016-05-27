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

import co.ar_smart.www.helpers.RetrofitServiceGenerator;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Command;
import co.ar_smart.www.pojos.Endpoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

import static co.ar_smart.www.helpers.Constants.DEFAULT_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_ADDITIONAL_OBJECT;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.PREFS_NAME;
import static co.ar_smart.www.helpers.Constants.PREF_HUB;

public class ListCommandsActivity extends AppCompatActivity {

    private String API_TOKEN = "";
    private ArrayList<Command> commands;
    private ListView list;
    private ProgressBar progress;
    private ArrayAdapter<Command> adapter;
    private ArrayList<Endpoint> endpoints;
    private Activity myact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_commands);
        endpoints=getIntent().getParcelableArrayListExtra(EXTRA_ADDITIONAL_OBJECT);
        commands=new ArrayList<>();
        myact=this;

        list = (ListView) findViewById(R.id.list_commands);
        progress = (ProgressBar) findViewById(R.id.progresscommands);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.label_listcommands_activity_title));
        }

        Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);

        loadCommands();
    }

    /**
     * This method load all commands
     */
    public void loadCommands()
    {
        commands=getIntent().getParcelableArrayListExtra("Commands");
        for(Command c:commands)
        {

        }
        adapter=new ArrayAdapter<Command>(ListCommandsActivity.this, android.R.layout.simple_list_item_1, commands){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view=convertView;
                if(view==null)
                {
                    view=getLayoutInflater().inflate(R.layout.row_list_stand,null);
                    TextView lb=(TextView)view.findViewById(R.id.label_item_stand);
                    lb.setText(commands.get(position).getType());
                    lb=(TextView)view.findViewById(R.id.label_item2_stand);
                    lb.setText(commands.get(position).getType());
                    ImageView i=(ImageView) view.findViewById(R.id.icon_list_stand);
                    i.setImageDrawable(ContextCompat.getDrawable(myact, R.drawable.delete_btn));
                }
                //chk.setChecked(checked[position]);
                return view;
            }
        };
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadDialog(position);
            }
        });
        progress.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
    }

    /**
     * Show a warning dialog asking if the user is sure to delete the device selected
     * @param position item position in the list
     */
    public void loadDialog(final int position)
    {

        final Dialog dialog = new Dialog(ListCommandsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_warning_delete);
        TextView txtname=(TextView) dialog.findViewById(R.id.lbl_warning_del_device);
        txtname.setText(getResources().getString(R.string.label_warning_delete_device)+" "+commands.get(position).getType()+"?");
        Button dialogButton = (Button) dialog.findViewById(R.id.btnDel);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commands.remove(position);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        dialogButton = (Button) dialog.findViewById(R.id.btnCancelDel);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * this method is called when the user click button add comand , this allows add other command to the list
     */
    public void addnewCommand(View v)
    {
        Intent i=new Intent(ListCommandsActivity.this,NewModeActivity.class);
        i.putExtra("modename",getIntent().getStringExtra("modename"));
        i.putParcelableArrayListExtra(EXTRA_ADDITIONAL_OBJECT,endpoints);
        i.putParcelableArrayListExtra("Commands",commands);
        startActivity(i);
    }

    /**
     * this method get preferred hub
     */
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

    private interface DevicesHubClient {
        @GET("hubs/{hub_id}/endpoints/")
        Call<List<Endpoint>> getendpoints(@Path("hub_id") String hub_id);

        @DELETE("hubs/{hub_id}/modes/{endpoint_id}/")
        Call<Endpoint> delendpoint(@Path("hub_id") String hub_id, @Path("endpoint_id") String endpoint_id);
    }
}
