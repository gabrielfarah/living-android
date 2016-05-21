package co.ar_smart.www.pojos.music_player;

/**
 * This class represents a music track from a music player
 * Created by Gabriel on 5/17/2016.
 */
public class MusicTrack {
    /**
     * The title of the track
     */
    private String title = "";
    /**
     * The position the track is in the queue
     */
    private int queue_number = 0;
    /**
     * the URI of the album cover
     */
    private String album_art_uri = "";

    /**
     * gets the title of the track
     *
     * @return the track title
     */
    public String getTitle() {
        return title;
    }

    /**
     * sets the title of the track
     *
     * @param title the track title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * gets the queue number of the track
     * @return the track queue number
     */
    public int getQueue_number() {
        return queue_number;
    }

    /**
     * sets the queue number of the track
     * @param queue_number track queue number to set
     */
    public void setQueue_number(int queue_number) {
        this.queue_number = queue_number;
    }

    /**
     * gets the album uri art of the track
     * @return the track album uri art
     */
    public String getAlbum_art_uri() {
        return album_art_uri;
    }

    /**
     * sets the album uri art of the track
     * @param album_art_uri track album uri art
     */
    public void setAlbum_art_uri(String album_art_uri) {
        this.album_art_uri = album_art_uri;
    }

    /**
     * gets the track internal id of the track
     * @return the track internal id
     */
    public String getItem_id() {
        return item_id;
    }

    /**
     * sets the internal id of the track
     * @param item_id track internal id
     */
    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    /**
     * gets the artist of the track
     * @return the track artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * set the artist of the track
     * @param artist track title
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * gets the description of the track
     * @return the track description
     */
    public String getDesc() {
        return desc;
    }

    /**
     * set the description of the track
     * @param desc track description
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * the internal id of the track
     */
    private String item_id = "";
    /**
     * the artist of the track
     */
    private String artist = "";
    /**
     * the description of the track
     */
    private String desc = "";

    /**
     * Creates a new track object
     * @param nTitle the new title
     * @param nqueue_number the new queue number
     * @param nAlbum_art_id the new album art uri
     * @param nItem_id the new internal track id
     * @param nArtist the new artist
     * @param nDesc the new description
     */
    public MusicTrack(String nTitle, int nqueue_number, String nAlbum_art_id, String nItem_id, String nArtist, String nDesc) {
        title = nTitle;
        queue_number = nqueue_number;
        album_art_uri = nAlbum_art_id;
        item_id = nItem_id;
        artist = nArtist;
        desc = nDesc;
    }

    @Override
    public String toString() {
        return "(" + title + "-" + queue_number + ")";
    }
}
