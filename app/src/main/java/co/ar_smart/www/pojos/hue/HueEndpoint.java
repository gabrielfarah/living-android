package co.ar_smart.www.pojos.hue;

import android.os.Parcel;
import android.os.Parcelable;

import co.ar_smart.www.pojos.Endpoint;

/**
 * This class will contain all the commands and attributes of an endpoint (device) of kind SONOS Music Player
 * Created by Gabriel on 5/16/2016.
 */
public class HueEndpoint implements Parcelable {

    public static final Creator<HueEndpoint> CREATOR = new Creator<HueEndpoint>() {
        @Override
        public HueEndpoint createFromParcel(Parcel in) {
            return new HueEndpoint(in);
        }

        @Override
        public HueEndpoint[] newArray(int size) {
            return new HueEndpoint[size];
        }
    };
    /**
     * This is the command for obtaining all the information to pain the controller UI
     */
    private static String get_ui = "";
    /**
     * This is the base endpoint information. It contains the attributes of the device like the ip address.
     */
    private Endpoint endpoint;


    /**
     * The constructor of a new SonosEndpoint class
     *
     * @param nEndpoint the base endpoint with the required fields (specially ip)
     */
    public HueEndpoint(Endpoint nEndpoint) {
        endpoint = nEndpoint;
        get_ui = "[{\"type\":\"wifi\",\"target\":\"hue\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"get_ui_info\",\"parameters\":[]}]";
    }

    protected HueEndpoint(Parcel in) {
        endpoint = in.readParcelable(Endpoint.class.getClassLoader());
        get_ui = in.readString();
    }

    /**
     * this method return the formatted get ui command
     *
     * @return the get ui command
     */
    public String get_ui() {
        return get_ui;
    }


    @Override
    public String toString() {
        return "(" + endpoint + ")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(endpoint, flags);
        dest.writeString(get_ui);
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public String getRGB(int lid, int r, int g, int b) {
        return "[{\"type\":\"wifi\",\"target\":\"hue\",\"ip\":\"" + endpoint.getIp_address() +
                "\",\"function\":\"set_color_to_light_by_id\",\"parameters\":{\"light_id\":" +
                lid + ",\"r\":" + r + ",\"g\":" + g + ",\"b\":" + b + "}}]";
    }

    public String getBrightness(int lid, int value) {
        return "[{\"type\":\"wifi\",\"target\":\"hue\",\"ip\":\"" + endpoint.getIp_address() +
                "\",\"function\":\"set_brightness_to_light_by_id\",\"parameters\":{\"light_id\":" +
                lid + ",\"brightness\":" + value + "}}]";
    }

    public String getSaturation(int lid, int value) {
        return "[{\"type\":\"wifi\",\"target\":\"hue\",\"ip\":\"" + endpoint.getIp_address() +
                "\",\"function\":\"set_saturation_to_light_by_id\",\"parameters\":{\"light_id\":" +
                lid + ",\"saturation\":" + value + "}}]";
    }
}
