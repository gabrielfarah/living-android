package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents a guest user in a hub
 * Created by Gabriel on 5/4/2016.
 */
public class Guest implements Parcelable {
    /**
     * the parcelable creator
     */
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
    /**
     * The id of the user
     */
    private int id;
    /**
     * the emails of the user representing a guest
     */
    private String email;

    /**
     * creates a guest from a parcel
     *
     * @param in the parcel
     */
    protected Guest(Parcel in) {
        id = in.readInt();
        email = in.readString();
    }

    /**
     * the constructor for a guest
     */
    public Guest() {

    }

    /**
     * returns the user email
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * sets the email of the user representing a guest
     * @param email the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * returns the id of the user
     * @return the user id
     */
    public int getId() {
        return id;
    }

    /**
     * sets the id of the user
     * @param id the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    @Override
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
