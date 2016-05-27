package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriel on 5/4/2016.
 */
public class EndpointClassCommand implements Parcelable{
    private String description;
    private String label;

    public List<Command> getPayload() {
        return payload;
    }

    public void setPayload(List<Command> payload) {
        this.payload = payload;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getManagement_id() {
        return management_id;
    }

    public void setManagement_id(int management_id) {
        this.management_id = management_id;
    }

    private List<Command> payload = new ArrayList<>(); //
    private int management_id;

    protected EndpointClassCommand(Parcel in) {
        description = in.readString();
        label = in.readString();
        in.readTypedList(payload, Command.CREATOR);//payload = in.readString();//
        management_id = in.readInt();
        for (Command i : payload) {
            Log.d("COMMAnd", i.toString());
        }
        Log.d("ID MANA", "" + management_id);
        Log.d("ID MANA", "" + payload);


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
        dest.writeTypedList(payload);//dest.writeString(payload);//
        dest.writeInt(management_id);
    }
}
