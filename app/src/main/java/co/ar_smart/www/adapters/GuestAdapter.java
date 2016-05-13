package co.ar_smart.www.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Guest;

/**
 * Created by Gabriel on 5/13/2016.
 */
public class GuestAdapter extends BaseAdapter {

    private Context context;
    private List<Guest> guests;
    private LayoutInflater inflater;

    private static class ViewHolder {
        TextView guestEmail;
    }

    public GuestAdapter(Context c, List<Guest> nGuests) {
        context = c;
        guests = nGuests;
        inflater = (LayoutInflater.from(c));
    }

    @Override
    public int getCount() {
        return guests.size();
    }

    @Override
    public Object getItem(int position) {
        return guests.get(position);
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
            convertView = inflater.inflate(R.layout.activity_guest_management_item, null);
            viewHolder.guestEmail = (TextView) convertView.findViewById(R.id.guest_email_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.guestEmail.setText(guests.get(position).getEmail());
        return convertView;
    }
}
