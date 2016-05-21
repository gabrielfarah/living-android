package co.ar_smart.www.adapters.music_player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.music_player.MusicTrack;

/**
 * Created by Gabriel on 5/13/2016.
 */
public class TrackAdapter extends BaseAdapter {

    private Context context;
    private List<MusicTrack> tracks;
    private LayoutInflater inflater;

    private static class ViewHolder {
        TextView trackName;
    }

    public TrackAdapter(Context c, List<MusicTrack> nMusicTracks) {
        context = c;
        tracks = nMusicTracks;
        inflater = (LayoutInflater.from(c));
    }

    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public Object getItem(int position) {
        return tracks.get(position);
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
            convertView = inflater.inflate(R.layout.activity_sonos_controller_item, null);
            viewHolder.trackName = (TextView) convertView.findViewById(R.id.music_track_title_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.trackName.setText(tracks.get(position).getTitle());
        return convertView;
    }
}
