package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents a room in a home
 * Created by Gabriel on 5/4/2016.
 */
public class Room implements Parcelable{
    private String description;
    private int hub;

    protected Room(Parcel in) {
        description = in.readString();
        hub = in.readInt();
    }

    public Room(int h,String d)
    {
        hub=h;
        description=d;
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    public String toString(){
        return "("+description+" - "+hub+")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeInt(hub);
    }

    public String getDescription()
    {
        return description;
    }
}
