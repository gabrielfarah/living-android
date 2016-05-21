package co.ar_smart.www.endpoints;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import co.ar_smart.www.living.R;

public class EditRoomActivity extends AppCompatActivity {

    private ListView list;
    private Button btnaddnew;
    private ArrayList<String> rooms;
    private HashMap<Integer,CheckBox> checks;
    private CheckBox last;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

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

                checks.put(position,(CheckBox)view.findViewById(R.id.chkRoom));
                //chk.setChecked(checked[position]);
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
        r.add("Sala");
        r.add("Comedor");
        r.add("Cocina");
        return r;
    }

    public void showDialog()
    {
        // custom dialog
        final Dialog dialog = new Dialog(EditRoomActivity.this);
        dialog.setTitle("Luces Sala");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_room);
        final TextView txtname=(TextView) dialog.findViewById(R.id.txtNameNewRoom);
        Button dialogButton = (Button) dialog.findViewById(R.id.btnAddNewRoom);
        // if button is clicked, close the custom dialog
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
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public void addRoom(String room)
    {
        rooms.add(room);
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
}
