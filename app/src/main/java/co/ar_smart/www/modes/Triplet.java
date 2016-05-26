package co.ar_smart.www.modes;

import android.os.Parcel;
import android.os.Parcelable;

import co.ar_smart.www.pojos.Endpoint;

/**
 * Created by Gabriel on 5/26/2016.
 */
public class Triplet implements Parcelable {
    public static final Creator<Triplet> CREATOR = new Creator<Triplet>() {
        @Override
        public Triplet createFromParcel(Parcel in) {
            return new Triplet(in);
        }

        @Override
        public Triplet[] newArray(int size) {
            return new Triplet[size];
        }
    };
    private boolean checked;
    private Endpoint endpoint;
    private boolean on;

    protected Triplet(Parcel in) {
        checked = in.readByte() != 0;
        endpoint = in.readParcelable(Endpoint.class.getClassLoader());
        on = in.readByte() != 0;
    }

    public Triplet(boolean nChecked, boolean nOn, Endpoint nEndpoint) {
        checked = nChecked;
        on = nOn;
        endpoint = nEndpoint;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (checked ? 1 : 0));
        dest.writeParcelable(endpoint, flags);
        dest.writeByte((byte) (on ? 1 : 0));
    }

    @Override
    public boolean equals(Object object) {
        boolean sameSame = false;

        if (object != null && object instanceof Triplet) {
            sameSame = this.endpoint.getId() == ((Triplet) object).getEndpoint().getId();
        }

        return sameSame;
    }

    @Override
    public String toString() {
        return "{" + endpoint.toString() + " - " + checked + " - " + on + "}";
    }
}
