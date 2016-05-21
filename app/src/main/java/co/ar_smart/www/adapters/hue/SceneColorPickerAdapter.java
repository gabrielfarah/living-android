package co.ar_smart.www.adapters.hue;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import co.ar_smart.www.living.R;

/**
 * Created by Gabriel on 5/11/2016.
 */
public class SceneColorPickerAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;

    private List<Map.Entry<String, String>> scenes = new java.util.ArrayList<>();

    private static class ViewHolder {
        ImageView scene_image;
        TextView scene_name;
    }

    public SceneColorPickerAdapter(Context c, List<Map.Entry<String, String>> scenes) {
        context = c;
        this.scenes = scenes;
        inflater = (LayoutInflater.from(c));
    }

    private Drawable getDrawable(String name) {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable",
                context.getPackageName());
        return ResourcesCompat.getDrawable(resources, resourceId, null);
    }

    @Override
    public int getCount() {
        return scenes.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.scene_color_picker_fragment_list_item, null);
            viewHolder.scene_image = (ImageView) convertView.findViewById(R.id.scene_color_picker_icon);
            viewHolder.scene_name = (TextView) convertView.findViewById(R.id.scene_color_picker_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.scene_name.setText(scenes.get(position).getKey());
        Drawable path = getDrawable(scenes.get(position).getValue());
        viewHolder.scene_image.setImageDrawable(path);
        return convertView;
    }
}
