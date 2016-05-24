package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * This class represents a action that occured in the hub
 * Created by Gabriel on 5/4/2016.
 */
public class FeedAction implements Parcelable {

    /**
     * the parcel creator
     */
    public static final Creator<FeedAction> CREATOR = new Creator<FeedAction>() {
        @Override
        public FeedAction createFromParcel(Parcel in) {
            return new FeedAction(in);
        }

        @Override
        public FeedAction[] newArray(int size) {
            return new FeedAction[size];
        }
    };
    /**
     * the date when the action happened
     */
    private Date created_at;
    /**
     * What happened
     */
    private String message;

    /**
     * the parcel creator
     *
     * @param in the parcel
     */
    protected FeedAction(Parcel in) {
        created_at = new Date(in.readLong());
        message = in.readString();
    }

    @Override
    public String toString() {
        return "(" + created_at + " - " + message + ")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(created_at != null ? created_at.getTime() : 0);
        dest.writeString(message);
    }

    /**
     * returns the date te action happened
     * @return the date of the action
     */
    public Date getCreated_at() {
        return created_at;
    }

    /**
     * returns what happened
     * @return what happened (in a message)
     */
    public String getMessage() {
        return message;
    }
}
