package co.ar_smart.www.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Mode;

/**
 * Created by Gabriel on 5/13/2016.
 */
public class ModeAdapter extends BaseAdapter {

    private Context context;
    private List<Mode> modes;
    private LayoutInflater inflater;

    public ModeAdapter(Context c, List<Mode> nModes) {
        context = c;
        modes = nModes;
        inflater = (LayoutInflater.from(c));
    }

    @Override
    public int getCount() {
        return modes.size();
    }

    @Override
    public Object getItem(int position) {
        return modes.get(position);
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
            convertView = inflater.inflate(R.layout.activity_mode_management_item, null);
            viewHolder.modeName = (TextView) convertView.findViewById(R.id.mode_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.modeName.setText(modes.get(position).getName());
        return convertView;
    }

    private static class ViewHolder {
        TextView modeName;
    }
}
