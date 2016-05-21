package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gabriel on 5/4/2016.
 */
public class Guest implements Parcelable {
    private int id;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String email;

    protected Guest(Parcel in) {
        id = in.readInt();
        email = in.readString();
    }

    public Guest() {

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof Guest))
            return false;
        Guest other = (Guest) obj;
        return getEmail() == null ? false : getEmail().equalsIgnoreCase(other.getEmail());//Compare email if null false
    }
}
