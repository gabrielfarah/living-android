package co.ar_smart.www.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.ar_smart.www.living.R;

/**
 * Created by Gabriel on 5/3/2016.
 * Last revision 5/3/2016
 */
public class HomeGridAdapter<T extends co.ar_smart.www.interfaces.IGridItem> extends BaseAdapter {

    private Context context;
    private List<T> items;

    public HomeGridAdapter(Context c, List<T> itemsGrid) {
        context = c;
        items = itemsGrid;
    }

    public void updateItems(ArrayList<T> nItems) {
        items = nItems;
        notifyDataSetChanged();
    }

    public void updateDataItems(List<T> newlist) {
        items.clear();
        items.addAll(newlist);
        this.notifyDataSetChanged();
    }

    private int getDrawableFromString(String name) {
        if (name != null) {
            switch (name) {
                case "mode":
                    return R.drawable.scene_icon;
                case "room":
                    return R.drawable.room_icon;
            }
        }
        return R.drawable.default_icon;
    }

    private Drawable setTint(int dr, int color) {
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
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.home_grid_view_item, null);
            holder.gridView = (LinearLayout) convertView;
            holder.imageView = (ImageView) convertView.findViewById(R.id.endpointImage);
            holder.textView = (TextView) convertView.findViewById(R.id.endpointTitle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int screenHeight = metrics.heightPixels;
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                screenHeight / 4);
        holder.gridView.setLayoutParams(params);
        holder.gridView.setPadding(4, 4, 4, 4);
        holder.gridView.setBackgroundColor(ContextCompat.getColor(context, R.color.white_ctran));
        if (items.get(position) != null) {
            int temp = getDrawableFromString(items.get(position).getImage());
            holder.textView.setText(items.get(position).getName());
            holder.imageView.setImageDrawable(setTint(temp, Color.rgb(44, 194, 190)));
        }
        return convertView;
    }

    public static class ViewHolder {
        LinearLayout gridView;
        ImageView imageView;
        TextView textView;
    }
}
