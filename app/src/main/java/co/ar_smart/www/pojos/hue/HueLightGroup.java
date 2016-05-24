package co.ar_smart.www.pojos.hue;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * This class represents a group of lights of a phillips hue
 * Created by Gabriel on 5/4/2016.
 */
public class HueLightGroup implements Parcelable {
    /**
     * the parcel creator
     */
    public static final Creator<HueLightGroup> CREATOR = new Creator<HueLightGroup>() {
        @Override
        public HueLightGroup createFromParcel(Parcel in) {
            return new HueLightGroup(in);
        }

        @Override
        public HueLightGroup[] newArray(int size) {
            return new HueLightGroup[size];
        }
    };
    /**
     * the group id
     */
    private int group_id;
    /**
     * the lights group name
     */
    private String name;
    /**
     * the list of lights in the group
     */
    private ArrayList<HueLight> lights = new ArrayList<>();

    /**
     * the parcel constructor
     *
     * @param in the parcel
     */
    protected HueLightGroup(Parcel in) {
        group_id = in.readInt();
        name = in.readString();
        in.readTypedList(lights, HueLight.CREATOR);

    }

    @Override
    public String toString() {
        return "(" + group_id + " - " + name + " - " + lights + ")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(group_id);
        dest.writeTypedList(lights);
    }
}
