package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gabriel on 5/4/2016.
 */
public class Hub implements Parcelable{

    private String custom_name;
    private int id;

    protected Hub(Parcel in) {
        custom_name = in.readString();
        id = in.readInt();
    }

    public static final Creator<Hub> CREATOR = new Creator<Hub>() {
        @Override
        public Hub createFromParcel(Parcel in) {
            return new Hub(in);
        }

        @Override
        public Hub[] newArray(int size) {
            return new Hub[size];
        }
    };

    public String getCustom_name() {
        return custom_name;
    }

    public int getId() {
        return id;
    }

    public String toString(){
        return "("+custom_name+" "+id+")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(custom_name);
        dest.writeInt(id);
    }
}
