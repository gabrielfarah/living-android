package co.ar_smart.www.pojos.sonos;

import java.util.ArrayList;

import co.ar_smart.www.interfaces.ICommandClass;
import co.ar_smart.www.pojos.Command;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.music_player.MusicTrack;

/**
 * This class will contain all the commands and attributes of an endpoint (device) of kind SONOS Music Player
 * Created by Gabriel on 5/16/2016.
 */
public class SonosEndpoint implements ICommandClass {
    /**
     * This is the command for obtaining the playlists saved on the Sonos
     */
    private static Command get_playlists;
    /**
     * This is the command for obtaining the songs queue saved on the Sonos
     */
    private static Command get_current_queue;
    /**
     * This is the command for obtaining all the information to pain the controller UI
     */
    private static String get_ui = "";
    /**
     * This is the command for start playing songs
     */
    private static Command play;
    /**
     * This is the command for stop playing songs
     */
    private static Command pause;
    /**
     * This is the command to stop the player
     */
    private static Command stop;
    /**
     * This is the command to play the previous song
     */
    private static Command back;
    /**
     * This is the command to play the next song
     */
    private static Command next;
    /**
     * This is the base endpoint information. It contains the attributes of the device like the ip address.
     */
    private Endpoint endpoint;
    /**
     * This is the current volume of the Sonos
     */
    private int volume = 0;
    /**
     * This is the playing state of the Sonos
     */
    private String state = ""; //PAUSED_PLAYBACK, PLAYING, STOPPED
    /**
     * This is true if the device is muted or false otherwise
     */
    private boolean mute = false;
    /**
     * The track list currently on the Sonos queue
     */
    private ArrayList<MusicTrack> queue = new ArrayList<>();

    /**
     * The constructor of a new ZwaveBinaryEndpoint class
     * @param nEndpoint the base endpoint with the required fields (specially ip)
     */
    public SonosEndpoint(Endpoint nEndpoint) {
        endpoint = nEndpoint;
        //get_playlists = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"get_sonos_playlists\",\"parameters\":[]}]";
        get_playlists = new Command(nEndpoint);
        get_playlists.setFunction("get_sonos_playlists");
        get_playlists.setTarget("sonos");

        //get_current_queue = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"get_queue\",\"parameters\":[]}]";
        get_current_queue = new Command(nEndpoint);
        get_current_queue.setFunction("get_queue");
        get_current_queue.setTarget("sonos");

        get_ui = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"get_ui_info\",\"parameters\":[]}]";

        //play = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"play\",\"parameters\":[]}]";
        play = new Command(nEndpoint);
        play.setFunction("play");
        play.setTarget("sonos");

        //pause = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"pause\",\"parameters\":[]}]";
        pause = new Command(nEndpoint);
        pause.setFunction("pause");
        pause.setTarget("sonos");

        //back = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"play_previous\",\"parameters\":[]}]";
        back = new Command(nEndpoint);
        back.setFunction("play_previous");
        back.setTarget("sonos");

        //next = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"play_next\",\"parameters\":[]}]";
        next = new Command(nEndpoint);
        next.setFunction("play_next");
        next.setTarget("sonos");

        stop = new Command(nEndpoint);
        stop.setFunction("stop");
        stop.setTarget("sonos");
    }

    public SonosEndpoint() {

    }

    /**
     * This method will get the previous song command
     *
     * @return the command to be sent to the hub
     */
    public static String getBack() {
        return back.toString();
    }

    /**
     * This method will get the next song command
     *
     * @return the command to be sent to the hub
     */
    public static String getNext() {
        return next.toString();
    }

    public void setEndpoint(Endpoint nEndpoint) {
        endpoint = nEndpoint;
        //get_playlists = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"get_sonos_playlists\",\"parameters\":[]}]";
        get_playlists = new Command(nEndpoint);
        get_playlists.setFunction("get_sonos_playlists");
        get_playlists.setTarget("sonos");

        //get_current_queue = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"get_queue\",\"parameters\":[]}]";
        get_current_queue = new Command(nEndpoint);
        get_current_queue.setFunction("get_queue");
        get_current_queue.setTarget("sonos");

        get_ui = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"get_ui_info\",\"parameters\":[]}]";

        //play = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"play\",\"parameters\":[]}]";
        play = new Command(nEndpoint);
        play.setFunction("play");
        play.setTarget("sonos");

        //pause = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"pause\",\"parameters\":[]}]";
        pause = new Command(nEndpoint);
        pause.setFunction("pause");
        pause.setTarget("sonos");

        //back = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"play_previous\",\"parameters\":[]}]";
        back = new Command(nEndpoint);
        back.setFunction("play_previous");
        back.setTarget("sonos");

        //next = "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"play_next\",\"parameters\":[]}]";
        next = new Command(nEndpoint);
        next.setFunction("play_next");
        next.setTarget("sonos");

        stop = new Command(nEndpoint);
        stop.setFunction("stop");
        stop.setTarget("sonos");
    }

    /**
     * This method will get the current volume of the Sonos player
     * @return the current volume of the Sonos
     */
    public int getVolume() {
        return volume;
    }

    /**
     * this method sets the volume of this class
     * @param volume the new volume
     */
    public void setVolume(int volume) {
        this.volume = volume;
    }

    /**
     * This method gets the playing state of the Sonos
     * @return the playing state
     */
    public String getState() {
        return state;
    }

    /**
     * This method sets the playing state of the sonos
     * @param state the new state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * This method tells if the Sonos is muted or not
     * @return true if muted false otherwise
     */
    public boolean isMute() {
        return mute;
    }

    /**
     * This methos sets the mute value
     * @param mute the new mute state
     */
    public void setMute(boolean mute) {
        this.mute = mute;
    }

    /**
     * This method return the track list of all the songs currently on the queue
     * @return the track list of the queue
     */
    public ArrayList<MusicTrack> getQueue() {
        return queue;
    }

    /**
     * this method adds a new music track to the list
     * @param nTrack new track to be added to local queue
     */
    public void addMusicTrack(MusicTrack nTrack) {
        queue.add(nTrack);
    }

    /**
     * this method return the formatted get playlist command
     * @return the get playlist command
     */
    public String get_playlists() {
        return get_playlists.toString();
    }

    /**
     * this method return the formatted get queue command
     * @return the get queue command
     */
    public String get_current_queue() {
        return get_current_queue.toString();
    }

    /**
     * this method return the formatted get ui command
     * @return the get ui command
     */
    public String get_ui() {
        return get_ui;
    }

    /**
     * this method return the formatted play song command
     * @return the get play song command
     */
    public String get_play() {
        return play.toString();
    }

    /**
     * this method return the formatted pause song command
     * @return the get pause song command
     */
    public String get_pause() {
        return pause.toString();
    }

    /**
     * this method return the formatted get volume song command
     * @return the get volume song command
     */
    public String getVolumeCommand(int volume) {
        return "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"set_volume\",\"parameters\":{\"volume\":" + volume + "}}]";
    }

    /**
     * this method return the formatted get track from queue command
     * @return the get track from queue command
     */
    public String getPlayTrackFromQueueCommand(int position) {
        return "[{\"type\":\"wifi\",\"target\":\"sonos\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"play_track_from_queue\",\"parameters\":{\"number\":" + position + "}}]";
    }

    public Command getStopCommand() {
        return stop;
    }

    @Override
    public String toString() {
        return "(" + volume + "-" + queue + ")";
    }

    @Override
    public Command getTurnOnCommand() {
        return play;
    }

    @Override
    public Command getTurnOffCommand() {
        return stop;
    }
}
