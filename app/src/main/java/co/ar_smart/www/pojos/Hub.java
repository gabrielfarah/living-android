package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * This class represents a Living HUB
 * Created by Gabriel on 5/4/2016.
 */
public class Hub implements Parcelable{

    /**
     * the parcel creator
     */
    public static final Creator<Hub> CREATOR = new Creator<Hub>() {
        @Override
        public Hub createFromParcel(Parcel in) {
            return new Hub(in);
        }

        @Override
        public Hub[] newArray(int size) {
            return new Hub[size];
        }
    };
    /**
     * the user given name for the hub
     */
    private String custom_name;
    /**
     * the id of the hub controller
     */
    private int id;
    /**
     * list of the endpoints connected to the hub
     */
    private ArrayList<Endpoint> endpoints;

    /**
     * creates a new hub from a parcel instance
     *
     * @param in the parcel
     */
    protected Hub(Parcel in) {
        custom_name = in.readString();
        id = in.readInt();
        in.readTypedList(endpoints, Endpoint.CREATOR);
    }

    /**
     * returns the hubs custon name
     *
     * @return custom name
     */
    public String getCustom_name() {
        return custom_name;
    }

    /**
     * returns the hub id
     * @return hub id
     */
    public int getId() {
        return id;
    }

    @Override
    public String toString(){
        if (custom_name.isEmpty()) {
            return "( Unnamed Hub )";
        } else {
            return "(" + custom_name + " " + id + ")";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(custom_name);
        dest.writeInt(id);
        dest.writeTypedList(endpoints);
    }
}
