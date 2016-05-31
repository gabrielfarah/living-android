package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Ej: [{"node":7,"active":false,"state":[-1]},{"active":false,"state":[1],"uid":"0123"}]}
 * Created by Gabriel on 5/28/2016.
 */
public class EndpointState implements Parcelable {
    public static final Creator<EndpointState> CREATOR = new Creator<EndpointState>() {
        @Override
        public EndpointState createFromParcel(Parcel in) {
            return new EndpointState(in);
        }

        @Override
        public EndpointState[] newArray(int size) {
            return new EndpointState[size];
        }
    };
    private int node;
    private boolean active;
    private ArrayList<Integer> state = new ArrayList<>();
    private String uid;

    protected EndpointState(Parcel in) {
        node = in.readInt();
        active = in.readByte() != 0;
        uid = in.readString();
        in.readList(state, getClass().getClassLoader());
    }

    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ArrayList<Integer> getState() {
        return state;
    }

    public void setState(ArrayList<Integer> state) {
        this.state = state;
    }

    public int getMainState() {
        return state.get(0);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(node);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeString(uid);
        dest.writeList(state);
    }

    @Override
    public String toString() {
        return "{" + node + "," + active + "," + uid + "," + state + "}";
    }
}
