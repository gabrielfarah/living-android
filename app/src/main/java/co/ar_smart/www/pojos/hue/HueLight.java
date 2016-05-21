package co.ar_smart.www.pojos.hue;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gabriel on 5/4/2016.
 */
public class HueLight implements Parcelable {
    private boolean on;
    private int saturation;
    private int light_id;
    private int brightness;
    private boolean reachable;
    private String unique_id;
    private String color_light;
    private String alert;
    private String effect;
    private int red;
    private int green;
    private int blue;

    protected HueLight(Parcel in) {
        on = (Boolean) in.readValue(getClass().getClassLoader());
        saturation = in.readInt();
        light_id = in.readInt();
        brightness = in.readInt();
        reachable = (Boolean) in.readValue(getClass().getClassLoader());
        unique_id = in.readString();
        color_light = in.readString();
        alert = in.readString();
        effect = in.readString();

    }

    public static final Creator<HueLight> CREATOR = new Creator<HueLight>() {
        @Override
        public HueLight createFromParcel(Parcel in) {
            return new HueLight(in);
        }

        @Override
        public HueLight[] newArray(int size) {
            return new HueLight[size];
        }
    };

    public String toString() {
        return "(" + light_id + " - " + red + " - " + green + " - " + blue + ")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(unique_id);
        dest.writeString(color_light);
        dest.writeString(alert);
        dest.writeString(effect);
        dest.writeInt(saturation);
        dest.writeInt(light_id);
        dest.writeInt(brightness);
        dest.writeValue(reachable);
        dest.writeValue(on);
        dest.writeInt(red);
        dest.writeInt(green);
        dest.writeInt(blue);
    }
}
