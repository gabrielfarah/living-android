package co.ar_smart.www.endpoints;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.helpers.RetrofitServiceGenerator;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Room;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import static co.ar_smart.www.helpers.Constants.DEFAULT_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_ROOM;
import static co.ar_smart.www.helpers.Constants.PREFS_NAME;
import static co.ar_smart.www.helpers.Constants.PREF_HUB;

public class EditRoomActivity extends AppCompatActivity {

    /**
     * List view where the rooms will be shown
     */
    private ListView list;
    /**
     * Add room button
     */
    private Button btnaddnew;
    /**
     * Rooms list
     */
    private ArrayList<String> rooms;
    /**
     * Checkbox state Map
     */
    private HashMap<Integer,CheckBox> checks;
    /**
     * Last selected checkbox
     */
    private CheckBox last;
    /**
     * List view Adapter
     */
    private ArrayAdapter<Room> adapter;

    /**
     * Token generated by JWT
     */

    private String API_TOKEN = "";

    /**
     * Room selected
     */
    private String room;
    private Button btRemove;
    private RoomClient client;
    private String PREFERED_HUB_ID = "";
    private ArrayList<Room> rooms_list = new ArrayList<>();
    private Button btnPick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.editroom_btntext));
        }
        API_TOKEN = getIntent().getStringExtra(EXTRA_MESSAGE);
        client = RetrofitServiceGenerator.createService(RoomClient.class, API_TOKEN);
        PREFERED_HUB_ID = getPreferredHub();

        room=getIntent().getStringExtra(EXTRA_ROOM);
        checks=new HashMap<>();

        list=(ListView) findViewById(R.id.listRooms);
        btnaddnew=(Button) findViewById(R.id.btnAddRoom);
        btRemove = (Button) findViewById(R.id.btnRemoveRoom);
        btnPick = (Button) findViewById(R.id.btnPickRoom);
        TextView textMessage = (TextView) findViewById(R.id.rooms_explanation_text_view);
        if (room != null) {
            textMessage.setText(R.string.text_room_explanation_edit);
            btnPick.setVisibility(View.GONE);
        } else {
            btnPick.setVisibility(View.VISIBLE);
            btnPick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            textMessage.setText(R.string.text_room_explanation_add_device);
        }

        btnaddnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        btRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRemoveDialog();
            }
        });

        adapter = new ArrayAdapter<Room>(EditRoomActivity.this, android.R.layout.simple_list_item_1, rooms_list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LinearLayout view;
                view=(LinearLayout)getLayoutInflater().inflate(R.layout.row_add_room,null);
                TextView lb=(TextView)view.findViewById(R.id.labelRoom);
                lb.setText(rooms_list.get(position).getDescription());
                CheckBox ch=(CheckBox)view.findViewById(R.id.chkRoom);
                if (room != null) {
                    if (room.equals(rooms_list.get(position).getDescription())) {
                        ch.setChecked(true);
                        last=ch;
                    }
                }
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
                returnIntent.putExtra("result", rooms_list.get(position).getDescription());
                setResult(Activity.RESULT_OK,returnIntent);
            }
        });

        rooms_list = getRooms();
        Log.d("R_LIST", rooms_list.toString());
        adapter.notifyDataSetChanged();
    }

    private void showRemoveDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Do your Yes progress
                        for (int i = 0; i < rooms_list.size(); i++) {
                            CheckBox a = checks.get(i);
                            if (a.isChecked()) {
                                Log.d("CHECKED", rooms_list.get(i).toString());
                                deleteRoom(rooms_list.get(i));
                            }
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //Do your No progress
                        break;
                }
            }
        };
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage(R.string.label_confirmation_remove_room).setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.cancel, dialogClickListener).show();
    }

    /***
     * This method get all rooms that are bounded to the preferred hub
     * @return Rooms list
     */
    public ArrayList<Room> getRooms() {
        Call<List<Room>> call2 = client.getrooms(PREFERED_HUB_ID);
        call2.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.isSuccessful()) {
                    rooms_list = (ArrayList<Room>) response.body();
                    if (rooms_list.isEmpty()) {
                        Toast.makeText(EditRoomActivity.this, R.string.not_matching_areas,
                                Toast.LENGTH_SHORT).show();
                    }
                    adapter.addAll(rooms_list);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(EditRoomActivity.this, R.string.error_requesting_areas,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Toast.makeText(EditRoomActivity.this, R.string.error_requesting_areas,
                        Toast.LENGTH_SHORT).show();
            }
        });
        return rooms_list;
    }

    private void deleteRoom(final Room r) {
        if (rooms_list.contains(r)) {
            Log.d("LO TIENE", r.toString());
        }
        Call<ResponseBody> call = client.deleteRoom(PREFERED_HUB_ID, r.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    rooms_list.remove(r);
                    adapter.remove(r);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showNoInternetMessage();
            }
        });
    }

    private void showNoInternetMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Constants.showNoInternetMessage(getApplicationContext());
            }
        });
    }

    /**
     * Show dialog that allows add a new room given a name
     */
    public void showDialog()
    {
        final Dialog dialog = new Dialog(EditRoomActivity.this);
        dialog.setTitle(R.string.living_room_lights);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_room);
        final TextView txtname=(TextView) dialog.findViewById(R.id.txtNameNewRoom);
        Button dialogButton = (Button) dialog.findViewById(R.id.btnAddNewRoom);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtname.getText().equals(""))
                {
                    Room temp = new Room(Integer.parseInt(PREFERED_HUB_ID), String.valueOf(txtname.getText()));
                    if (!rooms_list.contains(temp)) {
                        addRoom(temp);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(EditRoomActivity.this, R.string.room_name_used,
                                Toast.LENGTH_SHORT).show();
                    }
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

    /**
     * Send a new room to the backend given a name
     * @param room room name
     */
    public void addRoom(final Room room)
    {
        Call<Room> call = client.addroom(PREFERED_HUB_ID, room);

        call.enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                if (response.isSuccessful()) {
                    Room temp = response.body();
                    rooms_list.add(temp);
                    adapter.add(temp);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(EditRoomActivity.this, R.string.error_adding_room,
                            Toast.LENGTH_SHORT).show();
                    try {
                        Log.d("ERROR Agregar AREA 1", response.errorBody().string());
                        Log.d("ERROR Agregar AREA 1", response.message());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                Toast.makeText(EditRoomActivity.this, R.string.error_adding_room,
                        Toast.LENGTH_SHORT).show();
                Log.d("ERROR Agregar AREA 2", t.getMessage());
                t.printStackTrace();
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
     * This method get the preferred hub from the prefereces
     */
    public String getHub() {
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME,
                Context.MODE_PRIVATE);
        return settings.getString(Constants.PREF_HUB, Constants.DEFAULT_HUB);
    }

    /**
     * This method will load the preferred hub the user selected the last time (if any).
     */
    private String getPreferredHub() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        // Get values using keys
        return settings.getString(PREF_HUB, DEFAULT_HUB);
    }

    /**
     * This interface implements a Retrofit interface for the RoomClient
     */

    private interface RoomClient {
        @GET("hubs/{hub_id}/rooms/")
        Call<List<Room>> getrooms(@Path("hub_id") String hub_id);

        @POST("hubs/{hub_id}/rooms/")
        Call<Room> addroom(@Path("hub_id") String hub_id, @Body Room r);

        @DELETE("hubs/{hub_id}/rooms/{room_id}/")
        Call<ResponseBody> deleteRoom(@Path("hub_id") String hub_id, @Path("room_id") int room_id);
    }
}
