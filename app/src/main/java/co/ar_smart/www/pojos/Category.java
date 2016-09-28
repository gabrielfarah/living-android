package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents the category of a device (ilumination, entertaiment, etc)
 * Created by Gabriel on 5/4/2016.
 */
public class Category implements Parcelable{
    /**
     * the parcel creator
     */
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
    /**
     * the name of the category
     */
    private String description;
    /**
     * the category code
     */
    private String code;

    /**
     * the parcel constructor
     *
     * @param in the parcel
     */
    protected Category(Parcel in) {
        description = in.readString();
        code = in.readString();
    }

    public Category(String nDescription, String nCode) {
        description = nDescription;
        code = nCode;
    }

    @Override
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

    /**
     * returns the name of the category
     * @return the category name
     */
    public String getCat(){return description;}
}
