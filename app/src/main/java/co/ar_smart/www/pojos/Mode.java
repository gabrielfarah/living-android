package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import co.ar_smart.www.interfaces.IGridItem;

/**
 * This class represents a scene
 * Created by Gabriel on 5/4/2016.
 */
public class Mode implements Parcelable, IGridItem {
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
     * This indicates the mode will automatically execute on the given minutes_of_day and days_of_the_week
     */
    private boolean timed = false;
    private int minute_of_day = 0;
    private List<Integer> days_of_the_week = new ArrayList<>();

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
        minute_of_day = in.readInt();
        in.readList(days_of_the_week, getClass().getClassLoader());
        id=in.readInt();
        timed = in.readByte() != 0;
    }

    public List<Integer> getDays_of_the_week() {
        return days_of_the_week;
    }

    public int getMinute_of_day() {
        return minute_of_day;
    }

    public boolean isTimed() {
        return timed;
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
        dest.writeInt(minute_of_day);
        dest.writeList(days_of_the_week);
        dest.writeInt(id);
        dest.writeByte((byte) (timed ? 1 : 0));
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

    @Override
    public String getImage() {
        return "mode";
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

    @Override
    public boolean equals(Object object) {
        boolean sameSame = false;

        if (object != null && object instanceof Mode) {
            sameSame = this.id == ((Mode) object).getId();
        }

        return sameSame;
    }

    public void addCommandToPayload(Command command) {
        payload.add(command);
    }

    public void removeCommandFromPayload(Command command) {
        payload.remove(command);
    }
}
