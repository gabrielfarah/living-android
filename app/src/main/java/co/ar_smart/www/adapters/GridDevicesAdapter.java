package co.ar_smart.www.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

import co.ar_smart.www.Interfaces.IDrawable;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Endpoint;

/**
 * Created by Gabriel on 5/3/2016.
 */
public class GridDevicesAdapter<T extends IDrawable> extends BaseAdapter{

    private Context context;
    private List<T> endpoints;

    public GridDevicesAdapter(Context c, List<T> endpointGrid){
        context = c;
        endpoints = endpointGrid;
    }

    private int getDrawableFromString(String name){
        if (name != null) {
            switch (name) {
                case "ligh":
                    return R.drawable.light_icon;
                case "hue":
                    return R.drawable.hue_icon1;
            }
        }
        return R.drawable.light_icon;
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
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(250, 250));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            imageView.setBackgroundColor(ContextCompat.getColor(context, R.color.subBarras));//R.color.subBarras);
        } else
        {
            imageView = (ImageView) convertView;
        }
        if(endpoints.get(position)!=null)
        {
            imageView.setImageResource(getDrawableFromString(endpoints.get(position).getImage()));
        }
        return imageView;
    }
}
