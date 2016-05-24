package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class represents a device command
 * Created by Gabriel on 5/4/2016.
 */
public class Command implements Parcelable {
    /**
     * The parcel creator
     */
    public static final Creator<Command> CREATOR = new Creator<Command>() {
        @Override
        public Command createFromParcel(Parcel in) {
            return new Command(in);
        }

        @Override
        public Command[] newArray(int size) {
            return new Command[size];
        }
    };

    /**
     * The type of the command (wifi, zwave, etc)
     */
    private String type = "";
    /**
     * the device target (sonos, hue, etc)
     */
    private String target = "";
    /**
     * the function (play, turn_on, etc)
     */
    private String function = "";
    /**
     * the ip of the device (if the device is type wifi)
     */
    private String ip = "";
    /**
     * the ip of the node id (if the device is type zwave)
     */
    private int node = 0;
    /**
     * the value to set the device to (if the device is type zwave)
     */
    private int v = 0;
    /**
     * The parameters for the function ej: light_id, volume, etc.
     */
    private JSONObject parameters;
    /**
     * the uid of the endpoint
     */
    private String endpoint_id = "";
    private String label_ui;

    /**
     * a constructor for the commnd
     */
    public Command() {
    }

    /**
     * the constructor for the command class given an endpoint
     *
     * @param endpoint the endpoint that provides the necessary information
     */
    public Command(Endpoint endpoint) {
        ip = endpoint.getIp_address();
        node = endpoint.getNode();
        type = endpoint.getEndpoint_type();
        endpoint_id = endpoint.getUid();
        try {
            parameters = new JSONObject("{}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * the parcel constructor
     * @param in the parcel
     */
    protected Command(Parcel in) {
        type = in.readString();
        target = in.readString();
        function = in.readString();
        ip = in.readString();
        node = in.readInt();
        v = in.readInt();
        try {
            parameters = new JSONObject("{}");
            parameters = new JSONObject(in.readString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        endpoint_id = in.readString();
    }

    /**
     * returns the target
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * returns the function
     * @return the function
     */
    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    /**
     * returns the ip
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * returns the node
     * @return the node
     */
    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }

    /**
     * returns the value
     * @return the value
     */
    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    /**
     * returns the parameters
     * @return the parameter
     */
    public JSONObject getParameters() {
        return parameters;
    }

    public void setParameters(JSONObject parameters) {
        this.parameters = parameters;
    }

    /**
     * returns the type
     * @return the type
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(target);
        dest.writeString(function);
        dest.writeString(ip);
        dest.writeInt(node);
        dest.writeInt(v);
        dest.writeString(parameters.toString());
        dest.writeString(endpoint_id);
    }

    @Override
    public String toString() {
        return "{\"type\":\"" + getType() + "\",\"target\":\"" + getTarget() + "\",\"ip\":\"" + getIp() +
                "\",\"function\":\"" + getFunction() + "\",\"parameters\":" + getParameters().toString() +
                ",\"node\":" + getNode() + ",\"v\":" + getV() + "}";
    }
}
