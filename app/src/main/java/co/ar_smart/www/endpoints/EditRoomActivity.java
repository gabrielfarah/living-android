package co.ar_smart.www.endpoints;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.ar_smart.www.helpers.RetrofitServiceGenerator;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.Room;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import static co.ar_smart.www.helpers.Constants.ACTION_EDIT;
import static co.ar_smart.www.helpers.Constants.DEFAULT_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_ACTION;
import static co.ar_smart.www.helpers.Constants.EXTRA_CATEGORY_DEVICE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_ROOM;
import static co.ar_smart.www.helpers.Constants.EXTRA_UID;
import static co.ar_smart.www.helpers.Constants.PREFS_NAME;
import static co.ar_smart.www.helpers.Constants.PREF_HUB;

public class EditRoomActivity extends AppCompatActivity {

    private ListView list;
    private Button btnaddnew;
    private ArrayList<String> rooms;
    private HashMap<Integer,CheckBox> checks;
    private CheckBox last;
    private ArrayAdapter<String> adapter;
    private String API_TOKEN = "";
    private String room;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.label_room_activity_title));
        }
        API_TOKEN = getIntent().getStringExtra(EXTRA_MESSAGE);

        room=getIntent().getStringExtra(EXTRA_ROOM);
        checks=new HashMap<>();

        list=(ListView) findViewById(R.id.listRooms);
        btnaddnew=(Button) findViewById(R.id.btnAddRoom);
        rooms=getRooms();

        btnaddnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        adapter= new ArrayAdapter<String>(EditRoomActivity.this, android.R.layout.simple_list_item_1, rooms)
        {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LinearLayout view;
                view=(LinearLayout)getLayoutInflater().inflate(R.layout.row_add_room,null);


                TextView lb=(TextView)view.findViewById(R.id.labelRoom);
                lb.setText(rooms.get(position));
                CheckBox ch=(CheckBox)view.findViewById(R.id.chkRoom);
                //if(room.equals(rooms.get(position)))
                //{
                  //  ch.setChecked(true);
                //}

                checks.put(position,ch);
                return view;

            }
        };


        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(last!=null)
                {
                    last.setChecked(false);
                }
                last=checks.get(position);
                last.setChecked(true);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",rooms.get(position));
                setResult(Activity.RESULT_OK,returnIntent);
                //finish();
            }
        });
    }

    public ArrayList<String> getRooms()
    {
        ArrayList<String> r=new ArrayList();


        RoomClient client = RetrofitServiceGenerator.createService(RoomClient.class, API_TOKEN);
        Call<List<Room>> call2 = client.getrooms(""+1);

        call2.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.isSuccessful()) {
                    List<Room> li = response.body();
                    if (li.size() != 0) {
                        for (Room room : li) {
                            rooms.add(room.getDescription());
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(EditRoomActivity.this, "Ningún área encontrada ",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(EditRoomActivity.this, "Error al solicitar las áreas",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Toast.makeText(EditRoomActivity.this, "Error al solicitar las áreas",
                        Toast.LENGTH_SHORT).show();
            }
        });
        return r;
    }

    public void showDialog()
    {
        final Dialog dialog = new Dialog(EditRoomActivity.this);
        dialog.setTitle("Luces Sala");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_room);
        final TextView txtname=(TextView) dialog.findViewById(R.id.txtNameNewRoom);
        Button dialogButton = (Button) dialog.findViewById(R.id.btnAddNewRoom);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtname.getText().equals(""))
                {
                    addRoom(String.valueOf(txtname.getText()));
                    adapter.notifyDataSetChanged();
                }

                dialog.dismiss();
            }
        });

        dialogButton = (Button) dialog.findViewById(R.id.btnCancelNewRoom);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public void addRoom(final String room)
    {
        final Room newRoom=new Room(Integer.parseInt(getHub()),room);
        RoomClient client = RetrofitServiceGenerator.createService(RoomClient.class, API_TOKEN);
        Call<List<Room>> call2 = client.addroom(getHub(),newRoom);

        call2.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.isSuccessful()) {
                     //     rooms.clear//
                    // rooms.addAll(getRooms());();
                    rooms.add(newRoom.getDescription());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(EditRoomActivity.this, "Error al agregar área",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Toast.makeText(EditRoomActivity.this, "Error al agregar área",
                        Toast.LENGTH_SHORT).show();
            }
        });

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

    /**
     * This interface implements a Retrofit interface for the RoomClient
     *
     */

    private interface RoomClient {
        @GET("hubs/{hub_id}/rooms/")
        Call<List<Room>> getrooms(@Path("hub_id") String hub_id);

        @POST("hubs/{hub_id}/rooms/")
        Call<List<Room>> addroom(@Path("hub_id") String hub_id,@Body Room r);
    }

    public String getHub()
    {
        /*SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        return settings.getString(PREF_HUB, DEFAULT_HUB);*/
        return ""+1;

    }
}
