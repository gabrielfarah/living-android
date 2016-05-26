package co.ar_smart.www.modes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import co.ar_smart.www.adapters.ModeListAdapter;
import co.ar_smart.www.living.R;

import static co.ar_smart.www.helpers.Constants.EXTRA_ADDITIONAL_OBJECT;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;

public class ModeEndpointActivityPicker extends AppCompatActivity {

    private ArrayList<Triplet> endpoint_devices;
    private ModeListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_endpoint_activity_picker);
        final Intent intent = getIntent();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.label_select_devices_activity));
        }
        endpoint_devices = intent.getParcelableArrayListExtra(EXTRA_ADDITIONAL_OBJECT);
        String kind_label = intent.getStringExtra(EXTRA_MESSAGE);
        adapter = new ModeListAdapter(ModeEndpointActivityPicker.this, endpoint_devices, kind_label);
        adapter.notifyDataSetChanged();
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.mode_endpoint_activity_picker_list_view);
        if (listView != null) {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (endpoint_devices.get(position).isChecked()) {
                        endpoint_devices.get(position).setChecked(false);
                    } else {
                        endpoint_devices.get(position).setChecked(true);
                    }
                    //((ListView) parent).setItemChecked(position, endpoint_devices.get(position).isChecked());
                    //adapter.notifyDataSetInvalidated();
                    adapter.notifyDataSetChanged();
                }
            });
        }
        Button submitButton = (Button) findViewById(R.id.mode_endpoint_activity_picker_button);
        if (submitButton != null) {
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_ADDITIONAL_OBJECT, endpoint_devices);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }
}
