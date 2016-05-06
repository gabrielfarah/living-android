package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gabriel on 5/4/2016.
 */
public class EndpointClassCommand implements Parcelable{
    private String description;
    private String label;
    private String payload;

    protected EndpointClassCommand(Parcel in) {
        description = in.readString();
        label = in.readString();
        payload = in.readString();
    }

    public static final Creator<EndpointClassCommand> CREATOR = new Creator<EndpointClassCommand>() {
        @Override
        public EndpointClassCommand createFromParcel(Parcel in) {
            return new EndpointClassCommand(in);
        }

        @Override
        public EndpointClassCommand[] newArray(int size) {
            return new EndpointClassCommand[size];
        }
    };

    public String toString(){
        return "("+description+" - "+payload+")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(label);
        dest.writeString(payload);
    }
}
