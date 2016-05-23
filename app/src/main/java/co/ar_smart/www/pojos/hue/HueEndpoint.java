package co.ar_smart.www.pojos.hue;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import co.ar_smart.www.interfaces.ICommandClass;
import co.ar_smart.www.pojos.Command;
import co.ar_smart.www.pojos.Endpoint;

/**
 * This class will contain all the commands and attributes of an endpoint (device) of kind SONOS Music Player
 * Created by Gabriel on 5/16/2016.
 */
public class HueEndpoint implements Parcelable, ICommandClass {

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

    public Command getSetRGBColorCommand(int lid, int r, int g, int b) {
        Command c = new Command(endpoint);
        c.setFunction("set_color_to_light_by_id");
        c.setTarget("hue");
        JSONObject jp = new JSONObject();
        try {
            jp.put("r", r);
            jp.put("g", g);
            jp.put("b", b);
            jp.put("light_id", lid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        c.setParameters(jp);
        return c;
    }

    public Command getBrightnessCommand(int lid, int value) {
        Command c = new Command(endpoint);
        c.setFunction("set_brightness_to_light_by_id");
        c.setTarget("hue");
        JSONObject jp = new JSONObject();
        try {
            jp.put("brightness", value);
            jp.put("light_id", lid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        c.setParameters(jp);
        return c;
        /*return "[{\"type\":\"wifi\",\"target\":\"hue\",\"ip\":\"" + endpoint.getIp_address() +
                "\",\"function\":\"set_brightness_to_light_by_id\",\"parameters\":{\"light_id\":" +
                lid + ",\"brightness\":" + value + "}}]";*/
    }

    public Command getSaturationCommand(int lid, int value) {
        Command c = new Command(endpoint);
        c.setFunction("set_saturation_to_light_by_id");
        c.setTarget("hue");
        JSONObject jp = new JSONObject();
        try {
            jp.put("saturation", value);
            jp.put("light_id", lid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        c.setParameters(jp);
        return c;
        /*return "[{\"type\":\"wifi\",\"target\":\"hue\",\"ip\":\"" + endpoint.getIp_address() +
                "\",\"function\":\"set_saturation_to_light_by_id\",\"parameters\":{\"light_id\":" +
                lid + ",\"saturation\":" + value + "}}]";*/
    }

    @Override
    public Command getTurnOnCommand() {
        Command c = new Command(endpoint);
        c.setFunction("turn_on_all_lights");
        c.setTarget("hue");
        return c;
    }

    @Override
    public Command getTurnOffCommand() {
        Command c = new Command(endpoint);
        c.setFunction("turn_off_all_lights");
        c.setTarget("hue");
        return c;
    }
}
