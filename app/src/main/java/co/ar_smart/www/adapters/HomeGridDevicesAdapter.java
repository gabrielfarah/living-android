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
                case "ligh":
                    return R.drawable.light_icon;
                case "hue":
                    return R.drawable.hue_icon1;
                case "sonos":
                    return R.drawable.music_icon;
                case "power-outlet":
                    return R.drawable.power_outlet_icon;
                case "door-lock":
                    return R.drawable.door_lock_icon;
                case "shades":
                    return R.drawable.shades_icon;
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
            gridView.setBackgroundColor(ContextCompat.getColor(context, R.color.subBarras));//R.color.subBarras);
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
                        imageView.setImageDrawable(setTint(temp, Color.rgb(209, 211, 212)));
                    }
                }
            }
            return gridView;
        } else {
            return convertView;
        }
    }
}
