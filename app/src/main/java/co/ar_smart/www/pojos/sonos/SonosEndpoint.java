package co.ar_smart.www.pojos.sonos;

import java.util.ArrayList;

import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.music_player.MusicTrack;

/**
 * Created by Gabriel on 5/16/2016.
 */
public class SonosEndpoint {
    private static String get_playlists = "";
    private static String get_current_queue = "";
    private static String get_ui = "";
    private static String play = "";
    private static String pause = "";

    public static String getBack() {
        return back;
    }

    public static String getNext() {
        return next;
    }

    private static String back = "";
    private static String next = "";
    private Endpoint endpoint;

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    private int volume = 0;
    private String state = ""; //PAUSED_PLAYBACK, PLAYING, STOPPED
    private boolean mute = false;

    public ArrayList<MusicTrack> getQueue() {
        return queue;
    }

    private ArrayList<MusicTrack> queue = new ArrayList<>();

    public SonosEndpoint(Endpoint nEndpoint) {
        endpoint = nEndpoint;
        get_playlists = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"get_sonos_playlists\",\"parameters\":[]}]";
        get_current_queue = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"get_queue\",\"parameters\":[]}]";
        get_ui = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"get_ui_info\",\"parameters\":[]}]";
        play = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"play\",\"parameters\":[]}]";
        pause = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"pause\",\"parameters\":[]}]";
        back = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"play_previous\",\"parameters\":[]}]";
        next = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"play_next\",\"parameters\":[]}]";
    }

    public void addMusicTrack(MusicTrack nTrack) {
        queue.add(nTrack);
    }

    public String get_playlists() {
        return get_playlists;
    }

    public String get_current_queue() {
        return get_current_queue;
    }

    public String get_ui() {
        return get_ui;
    }

    public String get_play() {
        return play;
    }

    public String get_pause() {
        return pause;
    }

    public String getVolumeCommand(int volume) {
        return "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"set_volume\",\"parameters\":{\"volume\":" + volume + "}}]";
    }

    public String getPlayTrackFromQueueCommand(int position) {
        return "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"play_track_from_queue\",\"parameters\":{\"number\":" + position + "}}]";
    }

    @Override
    public String toString() {
        return "(" + volume + "-" + queue + ")";
    }
}
