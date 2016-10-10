package co.ar_smart.www.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;

import co.ar_smart.www.living.R;

/**
 * Created by Gabriel on 5/3/2016.
 */
public class GridDevicesAdapter<T extends co.ar_smart.www.interfaces.IDrawable> extends BaseAdapter {

    private Context context;
    private T[] endpoints;

    public GridDevicesAdapter(Context c, T[] endpointGrid){
        context = c;
        endpoints = endpointGrid;
    }

    public static int getDrawableFromString(String name) {
        if (name != null) {
            switch (name) {
                case "light":return R.drawable.light_icon;
                case "light2":return R.drawable.light_2_icon;
                case "light3":return R.drawable.light_3_icon;
                case "light4":return R.drawable.light_4_icon;
                case "light5":return R.drawable.light_5_icon;
                case "hue":return R.drawable.hue_icon1;
                case "hue2":return R.drawable.hue_icon2;
                case "sonos":return R.drawable.music_icon;
                case "music":return R.drawable.music_2_icon;
                case "music2":return R.drawable.music_3_icon;
                case "music3":return R.drawable.music_4_icon;
                case "music4":return R.drawable.music_5_icon;
                case "power-outlet":return R.drawable.power_outlet_icon;
                case "power-outlet2":return R.drawable.power_outlet_2_icon;
                case "power-outlet3":return R.drawable.switch_icon;
                case "power-outlet4":return R.drawable.tv_icon;
                case "door-lock":return R.drawable.door_lock_icon;
                case "door-lock2":return R.drawable.door_lock_2_icon;
                case "shades":return R.drawable.shades_icon;
                case "shades2":return R.drawable.shades_2_icon;
                case "shades3":return R.drawable.shades_3_icon;
                case "shades4":return R.drawable.shades_4_icon;
                case "temperature":return R.drawable.ac_icon;
                case "temperature2":return R.drawable.temperature_icon;
                case "sensor":return R.drawable.sensor_icon;
                case "alarm":return R.drawable.alarm_icon;
                case "alarm2":return R.drawable.alarm_2_icon;
                case "alarm3":return R.drawable.alarm_3_icon;
                case "battery":return R.drawable.battery_icon;
                case "coffee-maker":return R.drawable.cofee_maker_icon;
                case "door":return R.drawable.door_icon;
                case "door2":return R.drawable.door_2_icon;
                case "energy":return R.drawable.energy_icon;
                case "energy2":return R.drawable.energy_2_icon;
                case "energy3":return R.drawable.energy_sensor_icon;
                case "energy4":return R.drawable.power_icon;
                case "lamp":return R.drawable.lamp_icon;
                case "lamp2":return R.drawable.lamp_2_icon;
                case "lamp3":return R.drawable.lamp_3_icon;
                case "movement-sensor":return R.drawable.movement_sensor_icon;
                case "movement-sensor2":return R.drawable.movement_sensor_2_icon;
                case "movement-sensor3":return R.drawable.movement_sensor_3_icon;
                case "movement-sensor4":return R.drawable.movement_sensor_4_icon;
                case "open-close-sensor":return R.drawable.open_close_sensor_icon;
                case "open-close-sensor2":return R.drawable.open_close_sensor_2_icon;
                case "open-close-sensor3":return R.drawable.open_close_sensor_3_icon;
                case "water":return R.drawable.water_icon;
                case "water2":return R.drawable.water_2_icon;
                case "water3":return R.drawable.water_3_icon;
            }
        }
        return R.drawable.default_icon;
    }

    public Drawable setTint(int dr, int color) {
        Drawable d = ResourcesCompat.getDrawable(context.getResources(), dr, null);
        Drawable wrappedDrawable = null;
        if (d != null) {
            wrappedDrawable = DrawableCompat.wrap(d);
        }
        if (wrappedDrawable != null) {
            DrawableCompat.setTint(wrappedDrawable, color);
        }
        return wrappedDrawable;
    }

    @Override
    public int getCount() {
        return endpoints.length;
    }

    @Override
    public Object getItem(int position) {
        return endpoints[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            int icon = (int) context.getResources().getDimension(R.dimen.icon_size);
            imageView.setLayoutParams(new GridView.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, icon));
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(5, 2, 5, 2);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            //imageView.setBackgroundColor(ContextCompat.getColor(context, R.color.subBarras));//R.color.subBarras);
        } else
        {
            imageView = (ImageView) convertView;
        }
        if(endpoints[position]!=null)
        {
            int temp = getDrawableFromString(endpoints[position].getImage());
            if (!endpoints[position].isActive()) {
                imageView.setImageDrawable(setTint(temp, R.color.elementos));
            } else {
                imageView.setImageResource(temp);
            }
        }
        return imageView;
    }
}
