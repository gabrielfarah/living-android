package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents a room in a home
 * Created by Gabriel on 5/4/2016.
 */
public class Room implements Parcelable{
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
    private String description;
    private int hub = -1;
    private int id = -1;

    protected Room(Parcel in) {
        description = in.readString();
        hub = in.readInt();
        id = in.readInt();
    }

    public Room(int h,String d)
    {
        hub=h;
        description=d;
    }

    public int getHub() {
        return hub;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
        dest.writeInt(id);
    }

    public String getDescription()
    {
        if (description == null || description.isEmpty()) {
            return "Unnamed Room";
        } else {
            return description;
        }
    }

    @Override
    public boolean equals(Object object) {
        boolean sameSame = false;

        if (object != null && object instanceof Room) {
            sameSame = this.description == ((Room) object).getDescription();
        }

        return sameSame;
    }
}
