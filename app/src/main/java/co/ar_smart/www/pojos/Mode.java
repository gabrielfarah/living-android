package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a scene
 * Created by Gabriel on 5/4/2016.
 */
public class Mode implements Parcelable {
    /**
     * The parcelable creator
     */
    public static final Creator<Mode> CREATOR = new Creator<Mode>() {
        @Override
        public Mode createFromParcel(Parcel in) {
            return new Mode(in);
        }

        @Override
        public Mode[] newArray(int size) {
            return new Mode[size];
        }
    };
    /**
     * The name of the scene
     */
    private String name = "";
    /**
     * the id of the scene
     */
    private int id = -1;
    /**
     * a list of commands of the scene
     */
    private List<Command> payload = new ArrayList<>();

    /**
     * a constructor of the scene
     */
    public Mode() {
    }

    /**
     * the constructor of a scene given an id and a name
     *
     * @param nId   the new id
     * @param nName the new name
     */
    public Mode(int nId, String nName) {
        id = nId;
        name = nName;
    }

    /**
     * the parcelable constructor for a ascene
     * @param in the parcel
     */
    protected Mode(Parcel in) {
        name = in.readString();
        in.readTypedList(payload, Command.CREATOR);
    }

    /**
     * returns the list of commands that conform this scene
     * @return the list of commands
     */
    public List<Command> getPayload() {
        return payload;
    }

    /**
     * sets the list of commands
     * @param payload the new list of commands for this scene
     */
    public void setPayload(List<Command> payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "(" + name + ")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(payload);
    }

    /**
     * returns the scene name
     * @return the scene name
     */
    public String getName() {
        return name;
    }

    /**
     * change the scane name
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * gets the user id
     * @return the user id
     */
    public int getId() {
        return id;
    }

    /**
     * change the user id
     * @param id the new user id
     */
    public void setId(int id) {
        this.id = id;
    }
}
