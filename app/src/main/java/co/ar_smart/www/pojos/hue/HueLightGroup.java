package co.ar_smart.www.pojos.hue;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Gabriel on 5/4/2016.
 */
public class HueLightGroup implements Parcelable {
    private int group_id;
    private String name;
    private ArrayList<HueLight> lights = new ArrayList<>();

    protected HueLightGroup(Parcel in) {
        group_id = in.readInt();
        name = in.readString();
        in.readTypedList(lights, HueLight.CREATOR);

    }

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
