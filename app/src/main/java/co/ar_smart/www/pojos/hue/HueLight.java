package co.ar_smart.www.pojos.hue;

import android.os.Parcel;
import android.os.Parcelable;

import co.ar_smart.www.controllers.hue.PHUtils;

/**
 * Created by Gabriel on 5/4/2016.
 */
public class HueLight implements Parcelable {
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
    private boolean on = false;
    private int saturation;
    private int light_id;
    private int brightness;
    private boolean reachable = false;
    private String unique_id;
    private String color_light;
    private String alert;
    private String effect;
    private float[] xy = new float[2];
    private String modelid;
    private String name;
    protected HueLight(Parcel in) {
        on = in.readByte() != 0;
        reachable = in.readByte() != 0;
        saturation = in.readInt();
        light_id = in.readInt();
        brightness = in.readInt();
        unique_id = in.readString();
        color_light = in.readString();
        alert = in.readString();
        effect = in.readString();
        modelid = in.readString();
        in.readFloatArray(xy);
        name = in.readString();
    }

    public int getLight_id() {
        return light_id;
    }

    public int getBrightness() {
        return brightness;
    }

    public int getSaturation() {
        return saturation;
    }

    public boolean isOn() {
        return on;
    }

    public boolean isReachable() {
        return reachable;
    }

    public String getColor_light() {
        return color_light;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public String getModelid() {
        return modelid;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "(" + light_id + " - " + getRGBfromXY() + " " + reachable + ")";
    }

    public int getRGBfromXY() {
        return PHUtils.colorFromXY(xy, modelid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (on ? 1 : 0));
        dest.writeByte((byte) (reachable ? 1 : 0));
        dest.writeInt(saturation);
        dest.writeInt(light_id);
        dest.writeInt(brightness);
        dest.writeString(unique_id);
        dest.writeString(color_light);
        dest.writeString(alert);
        dest.writeString(effect);
        dest.writeString(modelid);
        dest.writeFloatArray(xy);
        dest.writeString(name);
    }
}
