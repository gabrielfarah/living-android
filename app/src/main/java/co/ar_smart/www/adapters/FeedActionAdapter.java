package co.ar_smart.www.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.FeedAction;

/**
 * Created by Gabriel on 5/11/2016.
 */
public class FeedActionAdapter extends BaseAdapter {

    private Context context;
    private List<FeedAction> actions;
    private LayoutInflater inflater;

    private static class ViewHolder {
        TextView message;
        TextView time;
    }

    public FeedActionAdapter(Context c, List<FeedAction> actionsList) {
        context = c;
        actions = actionsList;
        inflater = (LayoutInflater.from(c));
    }

    @Override
    public int getCount() {
        return actions.size();
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
            convertView = inflater.inflate(R.layout.activity_action_item, null);
            viewHolder.message = (TextView) convertView.findViewById(R.id.action_feed_message);
            viewHolder.time = (TextView) convertView.findViewById(R.id.action_feed_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.message.setText(actions.get(position).getMessage());
        String timeString = DateUtils.getRelativeTimeSpanString(context, actions.get(position).getCreated_at().getTime(), true).toString();
        viewHolder.time.setText(timeString);
        return convertView;
    }
}
