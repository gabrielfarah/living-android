package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Gabriel on 5/4/2016.
 */
public class FeedAction implements Parcelable {

    private Date created_at;
    private String message;

    protected FeedAction(Parcel in) {
        created_at = new Date(in.readLong());
        message = in.readString();
    }

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

    public String toString() {
        return "(" + created_at + " - " + message + ")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(created_at.getTime());
        dest.writeString(message);
    }

    public Date getCreated_at() {
        return created_at;
    }

    public String getMessage() {
        return message;
    }
}
