package co.ar_smart.www.endpoints;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.ar_smart.www.adapters.GridDevicesAdapter;
import co.ar_smart.www.interfaces.IDrawable;
import co.ar_smart.www.living.R;

public class EditIconActivity extends AppCompatActivity {

    private GridView grid;
    private  List<Icons> ic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_icon);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.label_icon_activity_title));
        }

        grid=(GridView) findViewById(R.id.gridIcons);
        Map<Integer,Integer> dic=new HashMap<>();
        dic.put(1,R.drawable.light_icon);
        dic.put(2,R.drawable.light_icon);
        dic.put(3,R.drawable.light_icon);
        dic.put(4,R.drawable.light_icon);
        ImageView ima=new ImageView(getApplicationContext());
        ima.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.light_icon));

        //Prueba
        ic=new ArrayList<>();
        ic.add(new Icons("ligh"));
        ic.add(new Icons("ligh"));
        ic.add(new Icons("ligh"));

        GridDevicesAdapter<Icons> adapter=new GridDevicesAdapter(getApplicationContext(),ic);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String r=ic.get(position).getImage();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",r);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
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

    private class Icons implements IDrawable{

        private String image;

        public Icons(String path)
        {
            image=path;
        }

        @Override
        public String getImage() {
            return image;
        }

        @Override
        public boolean isActive() {
            return false;
        }
    }


}
