package co.ar_smart.www.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import java.util.List;

import co.ar_smart.www.living.R;
import co.ar_smart.www.modes.Triplet;

/**
 * Created by Gabriel on 5/25/2016.
 */
public class ModeListAdapter extends BaseAdapter {
    private Context context;
    private List<Triplet> data;
    private LayoutInflater inflater;
    private String label;

    public ModeListAdapter(Context c, List<Triplet> nData, String kind_label) {
        context = c;
        data = nData;
        inflater = (LayoutInflater.from(c));
        label = kind_label;
    }

    private String getProperLabel(String name, boolean action) {
        switch (label) {
            case "play-stop":
                if (action) {
                    return String.format(context.getResources().getString(R.string.label_play), name);
                } else {
                    return String.format(context.getResources().getString(R.string.label_pause), name);
                }
            case "open-close":
                if (action) {
                    return String.format(context.getResources().getString(R.string.label_open), name);
                } else {
                    return String.format(context.getResources().getString(R.string.label_close), name);
                }
            case "on-off":
                if (action) {
                    return String.format(context.getResources().getString(R.string.label_on), name);
                } else {
                    return String.format(context.getResources().getString(R.string.label_off), name);
                }
                //TODO completar con el resto de opciones
            default:
                if (action) {
                    return String.format(context.getResources().getString(R.string.label_selected), name);
                } else {
                    return String.format(context.getResources().getString(R.string.label_unselected), name);
                }
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
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
            convertView = inflater.inflate(R.layout.activity_mode_endpoint_activity_picker_item, null);
            viewHolder.checkedTextView = (CheckedTextView) convertView.findViewById(R.id.checked_mode_endpoint_picker_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (data.get(position).isChecked()) {
            ((ListView) parent).setItemChecked(position, true);
            viewHolder.checkedTextView.setChecked(true);
            viewHolder.checkedTextView.setText(getProperLabel(data.get(position).getEndpoint().getName(), true));
        } else {
            ((ListView) parent).setItemChecked(position, false);
            viewHolder.checkedTextView.setChecked(false);
            viewHolder.checkedTextView.setText(getProperLabel(data.get(position).getEndpoint().getName(), false));
        }
        return convertView;
    }

    private static class ViewHolder {
        CheckedTextView checkedTextView;
    }
}
