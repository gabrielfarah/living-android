package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gabriel on 5/4/2016.
 */
public class Command implements Parcelable {
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public JSONObject getParameters() {
        return parameters;
    }

    public void setParameters(JSONObject parameters) {
        this.parameters = parameters;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;
    private String target;
    private String function;
    private String ip;
    private int node;
    private int v;
    private JSONObject parameters;

    protected Command(Parcel in) {
        type = in.readString();
        target = in.readString();
        function = in.readString();
        ip = in.readString();
        node = in.readInt();
        v = in.readInt();
        try {
            parameters = new JSONObject(in.readString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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
    }

    @Override
    public String toString() {
        return "(" + ip + " - " + node + " - " + function + " - " + parameters + " - " + v + ")";
    }
}
