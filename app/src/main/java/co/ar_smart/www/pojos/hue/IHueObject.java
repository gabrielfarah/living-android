package co.ar_smart.www.pojos.hue;

import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Gabriel on 19/05/2016.
 */
public interface IHueObject extends Parcelable {

    int getId();

    String getName();

    int getRGBfromXY();

    ArrayList<? extends IHueObject> getLights();

    @Override
    boolean equals(Object object);
}
