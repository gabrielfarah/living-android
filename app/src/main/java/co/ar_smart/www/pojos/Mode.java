package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriel on 5/4/2016.
 */
public class Mode implements Parcelable {
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
    private String name = "";
    private int id = -1;
    private List<Command> payload = new ArrayList<>();

    public Mode() {
    }

    public Mode(int nId, String nName) {
        id = nId;
        name = nName;
    }

    protected Mode(Parcel in) {
        name = in.readString();
        in.readTypedList(payload, Command.CREATOR);
    }

    public List<Command> getPayload() {
        return payload;
    }

    public void setPayload(List<Command> payload) {
        this.payload = payload;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
