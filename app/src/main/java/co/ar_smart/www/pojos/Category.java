package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gabriel on 5/4/2016.
 */
public class Category implements Parcelable{
    private String description;
    private String code;

    protected Category(Parcel in) {
        description = in.readString();
        code = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public String toString(){
        return "("+description+" - "+code+")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(code);
    }
}
