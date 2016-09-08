package co.ar_smart.www.adapters.hue;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.hue.HueLightGroup;

/**
 * Created by Gabriel on 5/11/2016.
 */
public class BulbsGroupAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;

    private ArrayList<HueLightGroup> bulbs = new ArrayList<>();

    public BulbsGroupAdapter(Context applicationContext, ArrayList<HueLightGroup> bulbs) {
        context = applicationContext;
        this.bulbs = bulbs;
        inflater = (LayoutInflater.from(applicationContext));
    }

    private Drawable getDrawable(String name) {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable",
                context.getPackageName());
        return ResourcesCompat.getDrawable(resources, resourceId, null);
    }

    @Override
    public int getCount() {
        return bulbs.size();
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
            convertView = inflater.inflate(R.layout.hue_bulbs_fragment_list_item, null);
            viewHolder.bulb_name = (TextView) convertView.findViewById(R.id.hue_bulbs_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.bulb_name.setText(bulbs.get(position).getName());
        return convertView;
    }

    private static class ViewHolder {
        TextView bulb_name;
    }
}
