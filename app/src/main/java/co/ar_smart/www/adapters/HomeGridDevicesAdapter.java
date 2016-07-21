package co.ar_smart.www.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Endpoint;

/**
 * Created by Gabriel on 5/3/2016.
 */
public class HomeGridDevicesAdapter extends BaseAdapter {

    private Context context;
    private List<Endpoint> endpoints;

    public HomeGridDevicesAdapter(Context c, List<Endpoint> endpointGrid) {
        context = c;
        endpoints = endpointGrid;
    }

    public Drawable setTint(int dr, int color) {
        Drawable d = ResourcesCompat.getDrawable(context.getResources(), dr, null);
        Drawable wrappedDrawable = DrawableCompat.wrap(d);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

    private int getDrawableFromString(String name) {
        if (name != null) {
            switch (name) {
                case "light":
                    return R.drawable.light_icon;
                case "light2":
                    return R.drawable.light_2_icon;
                case "light3":
                    return R.drawable.light_3_icon;
                case "light4":
                    return R.drawable.light_4_icon;
                case "light5":
                    return R.drawable.light_5_icon;
                case "hue":
                    return R.drawable.hue_icon1;
                case "hue2":
                    return R.drawable.hue_icon2;
                case "sonos":
                    return R.drawable.music_icon;
                case "music":
                    return R.drawable.music_2_icon;
                case "music2":
                    return R.drawable.music_3_icon;
                case "music3":
                    return R.drawable.music_4_icon;
                case "music4":
                    return R.drawable.music_5_icon;
                case "power-outlet":
                    return R.drawable.power_outlet_icon;
                case "power-outlet2":
                    return R.drawable.power_outlet_2_icon;
                case "power-outlet3":
                    return R.drawable.switch_icon;
                case "power-outlet4":
                    return R.drawable.tv_icon;
                case "door-lock":
                    return R.drawable.door_lock_icon;
                case "door-lock2":
                    return R.drawable.door_lock_2_icon;
                case "shades":
                    return R.drawable.shades_icon;
                case "shades2":
                    return R.drawable.shades_2_icon;
                case "shades3":
                    return R.drawable.shades_3_icon;
                case "shades4":
                    return R.drawable.shades_4_icon;
                case "temperature":
                    return R.drawable.ac_icon;
                case "temperature2":
                    return R.drawable.temperature_icon;
                case "sensor":
                    return R.drawable.sensor_icon;
                case "alarm":
                    return R.drawable.alarm_icon;
                case "alarm2":
                    return R.drawable.alarm_2_icon;
                case "alarm3":
                    return R.drawable.alarm_3_icon;
                case "battery":
                    return R.drawable.battery_icon;
                case "coffee-maker":
                    return R.drawable.cofee_maker_icon;
                case "door":
                    return R.drawable.door_icon;
                case "door2":
                    return R.drawable.door_2_icon;
                case "energy":
                    return R.drawable.energy_icon;
                case "energy2":
                    return R.drawable.energy_2_icon;
                case "energy3":
                    return R.drawable.energy_sensor_icon;
                case "energy4":
                    return R.drawable.power_icon;
                case "lamp":
                    return R.drawable.lamp_icon;
                case "lamp2":
                    return R.drawable.lamp_2_icon;
                case "lamp3":
                    return R.drawable.lamp_3_icon;
                case "movement-sensor":
                    return R.drawable.movement_sensor_icon;
                case "movement-sensor2":
                    return R.drawable.movement_sensor_2_icon;
                case "movement-sensor3":
                    return R.drawable.movement_sensor_3_icon;
                case "movement-sensor4":
                    return R.drawable.movement_sensor_4_icon;
                case "open-close-sensor":
                    return R.drawable.open_close_sensor_icon;
                case "open-close-sensor2":
                    return R.drawable.open_close_sensor_2_icon;
                case "open-close-sensor3":
                    return R.drawable.open_close_sensor_3_icon;
                case "water":
                    return R.drawable.water_icon;
                case "water2":
                    return R.drawable.water_2_icon;
                case "water3":
                    return R.drawable.water_3_icon;
            }
        }
        return R.drawable.default_icon;
    }

    @Override
    public int getCount() {
        return endpoints.size();
    }

    @Override
    public Object getItem(int position) {
        return endpoints.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.home_grid_view_item, null);
            //params.setLayoutDirection(1);
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int screenHeight = metrics.heightPixels;
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    screenHeight / 4);
            gridView.setLayoutParams(params);
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            gridView.setPadding(4, 4, 4, 4);
            gridView.setBackgroundColor(ContextCompat.getColor(context, R.color.white_ctran));
            if (endpoints.get(position) != null) {
                int temp = getDrawableFromString(endpoints.get(position).getImage());
                ImageView imageView = (ImageView)gridView.findViewById(R.id.endpointImage);
                TextView textView = (TextView) gridView.findViewById(R.id.endpointTitle);
                textView.setText(endpoints.get(position).getName());
                TextView textView2 = (TextView)gridView.findViewById(R.id.endpointRoom);
                textView2.setText(endpoints.get(position).getRoom());
                if (!endpoints.get(position).isActive()) {
                    imageView = (ImageView)gridView.findViewById(R.id.endpointImage);
                    imageView.setImageResource(temp);
                    imageView.setAlpha((float) 0.4);
                    Log.d("color", "GRIS");
                } else {
                    imageView.setAlpha((float) 1.0);

                    if (endpoints.get(position).getState() > 0) {
                        imageView.setImageDrawable(setTint(temp, Color.rgb(44, 194, 190)));
                        //imageView.setImageResource(temp);
                        Log.d("color", "ORIGINAL");
                    } else {
                        //imageView.setImageResource(temp);
                        imageView.setImageDrawable(setTint(temp, Color.rgb(128, 128, 128)));
                    }
                }
            }
            return gridView;
        } else {
            return convertView;
        }
    }
}
