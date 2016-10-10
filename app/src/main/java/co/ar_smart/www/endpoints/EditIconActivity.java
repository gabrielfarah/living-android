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

import co.ar_smart.www.adapters.GridDevicesAdapter;
import co.ar_smart.www.interfaces.IDrawable;
import co.ar_smart.www.living.R;

public class EditIconActivity extends AppCompatActivity {

    /**
     * Icon list
     */
    private  Icons[] ic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_icon);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.editicon_btntext));
        }

        /*
      Grid view where the icons will be shown
     */
        GridView grid = (GridView) findViewById(R.id.gridIcons);


        ImageView ima=new ImageView(getApplicationContext());
        ima.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.light_icon));

        ic=new Icons[]{new Icons("light"), new Icons("light2"), new Icons("light3"), new Icons( "light4"), new Icons("light5"), new Icons("hue"), new Icons("hue2"), new Icons("sonos"), new Icons("music"), new Icons("music2"), new Icons("music3"), new Icons("music4"), new Icons("power-outlet"), new Icons("power-outlet2"), new Icons("power-outlet3"), new Icons("power-outlet4"), new Icons("door-lock"), new Icons("door-lock2"), new Icons("shades"), new Icons("shades2"), new Icons("shades3"), new Icons("shades4"), new Icons("temperature"), new Icons("temperature2"), new Icons("sensor"), new Icons("alarm"), new Icons("alarm2"), new Icons("alarm3"), new Icons("battery"), new Icons("coffee-maker"), new Icons("door"), new Icons("door2"), new Icons("energy"), new Icons("energy2"), new Icons("energy3"), new Icons("energy4"), new Icons("lamp"), new Icons("lamp2"), new Icons("lamp3"), new Icons("movement-sensor"), new Icons("movement-sensor2"), new Icons("movement-sensor3"), new Icons("movement-sensor4"), new Icons("open-close-sensor"),new Icons("open-close-sensor2"), new Icons("open-close-sensor3"), new Icons("water"), new Icons("water2"), new Icons("water3")};

        GridDevicesAdapter<Icons> adapter = new GridDevicesAdapter<>(getApplicationContext(), ic);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String r=ic[position].getImage();
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

    /***
     * This inteface represents a drawable object
     */
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
