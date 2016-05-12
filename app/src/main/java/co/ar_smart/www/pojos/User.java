package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Gabriel on 5/11/2016.
 */
public class User implements Parcelable {

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile_os() {
        return mobile_os;
    }

    public void setMobile_os(String mobile_os) {
        this.mobile_os = mobile_os;
    }

    public String getPush_token() {
        return push_token;
    }

    public void setPush_token(String push_token) {
        this.push_token = push_token;
    }

    public Date getDate_joined() {
        return date_joined;
    }

    public void setDate_joined(Date date_joined) {
        this.date_joined = date_joined;
    }

    public Date getLast_login() {
        return last_login;
    }

    public void setLast_login(Date last_login) {
        this.last_login = last_login;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    private String first_name;
    private String last_name;
    private String email;
    private String mobile_os;
    private String push_token;
    private Date date_joined;
    private Date last_login;
    private int id;
    private float latitude;
    private float longitude;

    public User() {
    }

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
        dest.writeLong(date_joined.getTime());
        dest.writeLong(last_login.getTime());
        dest.writeFloat(latitude);
        dest.writeFloat(longitude);
        dest.writeInt(id);
    }

    @Override
    public String toString() {
        return "(" + getEmail() + " , " + getPush_token() + ")";
    }
}
