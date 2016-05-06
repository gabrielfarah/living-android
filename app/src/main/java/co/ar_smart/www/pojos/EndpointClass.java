package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriel on 5/4/2016.
 */
public class EndpointClass implements Parcelable{
    private String description;
    private List<EndpointClassCommand> commands = new ArrayList<>();

    protected EndpointClass(Parcel in) {
        description = in.readString();
        in.readTypedList(commands, EndpointClassCommand.CREATOR);
    }

    public static final Creator<EndpointClass> CREATOR = new Creator<EndpointClass>() {
        @Override
        public EndpointClass createFromParcel(Parcel in) {
            return new EndpointClass(in);
        }

        @Override
        public EndpointClass[] newArray(int size) {
            return new EndpointClass[size];
        }
    };

    public String toString(){
        return "("+description+" - "+commands+")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeTypedList(commands);
    }
}
