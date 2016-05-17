package co.ar_smart.www.pojos.music_player;

/**
 * Created by Gabriel on 5/17/2016.
 */
public class MusicTrack {
    private String title = "";
    private int queue_number = 0;
    private String album_art_uri = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQueue_number() {
        return queue_number;
    }

    public void setQueue_number(int queue_number) {
        this.queue_number = queue_number;
    }

    public String getAlbum_art_uri() {
        return album_art_uri;
    }

    public void setAlbum_art_uri(String album_art_uri) {
        this.album_art_uri = album_art_uri;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    private String item_id = "";
    private String artist = "";
    private String desc = "";

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
