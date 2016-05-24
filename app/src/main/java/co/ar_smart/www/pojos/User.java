package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * This class represents a user of the system
 * Created by Gabriel on 5/11/2016.
 */
public class User implements Parcelable {

    /**
     * the parcelable creator
     */
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    /**
     * the users frist name
     */
    private String first_name;
    /**
     * the users last name
     */
    private String last_name;
    /**
     * the users email
     */
    private String email;
    /**
     * the users mobile operating system
     */
    private String mobile_os;
    /**
     * the users push notification token
     */
    private String push_token;
    /**
     * the users account creation date
     */
    private Date date_joined;
    /**
     * the users last login date
     */
    private Date last_login;
    /**
     * the users ID
     */
    private int id;
    /**
     * the users latitude
     */
    private float latitude;
    /**
     * the users longitude
     */
    private float longitude;

    /**
     * A contructor for a user
     */
    public User() {
    }

    /**
     * The parcelable constructor of a user
     *
     * @param in
     */
    protected User(Parcel in) {
        first_name = in.readString();
        last_name = in.readString();
        email = in.readString();
        mobile_os = in.readString();
        push_token = in.readString();
        date_joined = new Date(in.readLong());
        last_login = new Date(in.readLong());
        latitude = in.readFloat();
        longitude = in.readFloat();
        id = in.readInt();
    }

    /**
     * returns the first name of the user
     *
     * @return the first name
     */
    public String getFirst_name() {
        return first_name;
    }

    /**
     * change the first name of the user
     * @param first_name the new first name
     */
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    /**
     * returns the last name of the user
     * @return the last name
     */
    public String getLast_name() {
        return last_name;
    }

    /**
     * sets the last name of the user
     * @param last_name the new last name
     */
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    /**
     * returns the user email
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * sets the email of the user
     * @param email the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * returns the mobile operating system this user is using (ios, android, windows or other)
     * @return the mobile operating system of the user
     */
    public String getMobile_os() {
        return mobile_os;
    }

    /**
     * sets the mobile operating system of the user
     * @param mobile_os the mobile system
     */
    public void setMobile_os(String mobile_os) {
        this.mobile_os = mobile_os;
    }

    /**
     * the push token for this user account (used to send push notifications to it)
     * @return the user push notification token
     */
    public String getPush_token() {
        return push_token;
    }

    /**
     * change the user push notification token
     * @param push_token the new user push notification token
     */
    public void setPush_token(String push_token) {
        this.push_token = push_token;
    }

    /**
     * the date when the user created his account
     * @return the user creation date
     */
    public Date getDate_joined() {
        return date_joined;
    }

    /**
     * sets the user creation date
     * @param date_joined the user date creation
     */
    public void setDate_joined(Date date_joined) {
        this.date_joined = date_joined;
    }

    /**
     * gets the user last date logged
     * @return the last time the user logged in
     */
    public Date getLast_login() {
        return last_login;
    }

    /**
     * change the last date logged
     * @param last_login the new last date logged
     */
    public void setLast_login(Date last_login) {
        this.last_login = last_login;
    }

    /**
     * return the user id
     * @return the user ID
     */
    public int getId() {
        return id;
    }

    /**
     * change the user id
     * @param id the new user ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * the user current latitude
     * @return the latitude
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * set the latitude of the user
     * @param latitude the new latitude
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /**
     * the user current longitude
     * @return the longitude
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * set the longitude of the user
     * @param longitude the new longitude
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(email);
        dest.writeString(mobile_os);
        dest.writeString(push_token);
        dest.writeLong(date_joined != null ? date_joined.getTime() : 0);
        dest.writeLong(last_login != null ? last_login.getTime() : 0);
        dest.writeFloat(latitude);
        dest.writeFloat(longitude);
        dest.writeInt(id);
    }

    @Override
    public String toString() {
        return "(" + getEmail() + " , " + getPush_token() + ")";
    }
}
