package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gabriel on 5/4/2016.
 */
public class Guest implements Parcelable {
    private int id;
    private String email;

    protected Guest(Parcel in) {
        id = in.readInt();
        email = in.readString();
    }

    public static final Creator<Guest> CREATOR = new Creator<Guest>() {
        @Override
        public Guest createFromParcel(Parcel in) {
            return new Guest(in);
        }

        @Override
        public Guest[] newArray(int size) {
            return new Guest[size];
        }
    };

    public String toString() {
        return "(" + id + " - " + email + ")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(email);
    }
}
