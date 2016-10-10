package co.ar_smart.www.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import co.ar_smart.www.living.R;
import co.ar_smart.www.modes.ModeManagementActivity;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_mode_management_item, null);
            viewHolder.modeName = (TextView) convertView.findViewById(R.id.mode_text_view);
            viewHolder.btn_edit = (ImageButton) convertView.findViewById(R.id.mode_edit);
            viewHolder.btn_remove = (ImageButton) convertView.findViewById(R.id.mode_remove);
            viewHolder.number_elements = (TextView) convertView.findViewById(R.id.mode_text_view_number_elements);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.modeName.setText(modes.get(position).getName());
        viewHolder.number_elements.setText(String.format(context.getResources().getString(R.string.label_number_of_elements_scene), modes.get(position).getPayload().size()));
        viewHolder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof ModeManagementActivity) {
                    ((ModeManagementActivity) context).openEditActivity(modes.get(position));
                }
            }
        });
        viewHolder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof ModeManagementActivity) {
                    ((ModeManagementActivity) context).openDialog(modes.get(position));
                }
            }
        });
        /*if(modes.get(position).getId() < 0)
        {
            viewHolder.btn_remove.setVisibility(View.GONE);
            viewHolder.btn_edit.setVisibility(View.GONE);
            //viewHolder.modeName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,0.93f));
            //viewHolder.btn_edit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,0.07f));
        }*/
        return convertView;
    }


    private static class ViewHolder {
        TextView modeName;
        TextView number_elements;
        ImageButton btn_edit;
        ImageButton btn_remove;
    }
}
