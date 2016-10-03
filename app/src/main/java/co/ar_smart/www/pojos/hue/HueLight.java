package co.ar_smart.www.pojos.hue;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

import co.ar_smart.www.controllers.hue.PHUtils;

/**
 * This class models a light for a hue device. A device can have multiple lights associated to it.
 * Created by Gabriel on 5/4/2016.
 */
public class HueLight implements Parcelable, IHueObject {
    /**
     * Parcelable creator implementation
     */
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

    /**
     * If the light is on or not
     */
    private boolean on = false;
    /**
     * The saturation of the light (0-254)
     */
    private int saturation;
    /**
     * The ID of the light inside the device
     */
    private int light_id;
    /**
     * the brightness of the light
     */
    private int brightness;
    /**
     * If the device can reach that light or not
     */
    private boolean reachable = false;
    /**
     * the unique id of the light given by the menufacturer
     */
    private String unique_id;
    /**
     * The type of the light ( Color Light ...)
     */
    private String type;
    /**
     * The kind of alert mode the light has [select|lselect|none]
     */
    private String alert;
    /**
     * The effect for this light [none|colorloop] (colorloop: will loop tru all the color until stopped)
     */
    private String effect;
    /**
     * The xy color value of the light
     */
    private float[] xy = new float[2];
    /**
     * The light model ID
     */
    private String modelid;
    /**
     * The name of the light
     */
    private String name;

    /**
     * The constructor for this light from a parcel
     *
     * @param in the parcel
     */
    protected HueLight(Parcel in) {
        on = in.readByte() != 0;
        reachable = in.readByte() != 0;
        saturation = in.readInt();
        light_id = in.readInt();
        brightness = in.readInt();
        unique_id = in.readString();
        type = in.readString();
        alert = in.readString();
        effect = in.readString();
        modelid = in.readString();
        in.readFloatArray(xy);
        name = in.readString();
    }

    /**
     * returns the light id
     * @return the ID of this light
     */
    public int getId() {
        return light_id;
    }

    /**
     * returns the brightness
     * @return the brightness of this light
     */
    public int getBrightness() {
        return brightness;
    }

    /**
     * returns the saturation
     * @return the saturation of this light
     */
    public int getSaturation() {
        return saturation;
    }

    /**
     * returns the state of the light
     * @return true if the light is on false otherwise
     */
    public boolean isOn() {
        return on;
    }

    /**
     * returns if the light is reachable by the device
     * @return true if reachable false otherwise
     */
    public boolean isReachable() {
        return reachable;
    }

    /**
     * return the type of the light
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * gets the light unique id
     * @return the uid
     */
    public String getUnique_id() {
        return unique_id;
    }

    /**
     * returns the light model id
     * @return the model id
     */
    public String getModelid() {
        return modelid;
    }

    /**
     * returns the name of the light
     * @return the light name
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "(" + light_id + " - " + getRGBfromXY() + " " + reachable + ")";
    }

    /**
     * This method convers the light XY color value to a RGB (int version) color using the model id of the light
     * @return an int with the RGB value for the light current XY color value
     */
    public int getRGBfromXY() {
        return PHUtils.colorFromXY(xy, modelid);
    }

    @Override
    public ArrayList<? extends IHueObject> getLights() {
        return null;
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
        dest.writeString(type);
        dest.writeString(alert);
        dest.writeString(effect);
        dest.writeString(modelid);
        dest.writeFloatArray(xy);
        Log.d("XY:", xy.length + "");
        dest.writeString(name);
    }

    @Override
    public boolean equals(Object object) {
        boolean isSame = false;
        if (object != null && object instanceof HueLight) {
            isSame = this.light_id == ((HueLight) object).light_id;
        }
        return isSame;
    }
}
