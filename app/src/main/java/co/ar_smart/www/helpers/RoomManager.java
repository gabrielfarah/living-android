package co.ar_smart.www.helpers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.interfaces.ICommandClass;
import co.ar_smart.www.interfaces.IGridItem;
import co.ar_smart.www.pojos.Command;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.Mode;
import co.ar_smart.www.pojos.Room;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * This class implements all the methods to manage the modess in a Hub
 * Created by Gabriel on 5/11/2016.
 */
public class RoomManager {

    public static ArrayList<Triplet> getDefaultRooms(ArrayList<Endpoint> endpoint_devices, int hub_id) {
        ArrayList<Triplet> response = new ArrayList<>();
        HashMap<String, ICommandClass> map = Constants.getUiMapClasses();
        ICommandClass te;
        for (Endpoint e : endpoint_devices) {
            Triplet temp = new Triplet(new Room(hub_id, e.getRoom()));
            if (!response.contains(temp)) {
                te = map.get(e.getUi_class_command());
                if (te != null) {
                    te.setEndpoint(e);
                    temp.addOffCommand(te.getTurnOffCommand());
                    temp.addOnCommand(te.getTurnOnCommand());
                } else {
                    AnalyticsApplication.getInstance().trackEvent("Device UI Class", "DoNotExist", "The device in hub:" + e.getHub() + " named:" + e.getName() + " the ui class does not correspond. UI:" + e.getUi_class_command());
                }
                response.add(temp);
            }
        }
        return response;
    }


    /**
     * This interface defines the callbacks for the states of the getApiToken method
     */
    public interface ModeCallbackInterface {
        /**
         * This method will be called if the request failed. The main cause may be the lack of internet connection.
         */
        void onFailureCallback();

        /**
         * This method will return a list of modess from the server in case the request was successful.
         *
         * @param modes The list of modess accounts for the hub
         */
        void onSuccessCallback(List<Mode> modes);

        /**
         * This method will be called in case the request was successful.
         */
        void onSuccessCallback();

        /**
         * This method will be called if the user doing the request is not the hub admin
         */
        void onUnsuccessfulCallback();
    }


    /**
     * This interface defines the callbacks for the states of the getApiToken method
     */
    public interface EndPointCallbackInterface {
        /**
         * This method will be called if the request failed. The main cause may be the lack of internet connection.
         */
        void onFailureCallback();

        /**
         * This method will return a list of guests from the server in case the request was successful.
         *
         * @param guest The list of guests accounts for the hub
         */
        void onSuccessCallback(List<Endpoint> guest);

        /**
         * This method will be called in case the request was successful.
         */
        void onSuccessCallback();

        /**
         * This method will be called if the user doing the request is not the hub admin
         */
        void onUnsuccessfulCallback();
    }


    /**
     * the interface describing the API urls and their response types
     */
    public interface ModeService {
        /**
         * This method will retrieve all the modes given a valid hub id
         *
         * @param hub_id the hub id to obtain the modes from
         * @return a list of modes
         */
        @GET("hubs/{hub_id}/modes/")
        Call<List<Mode>> getModes(@Path("hub_id") int hub_id);

        /**
         * This method will create a new modes in the hub
         *
         * @param hub_id the hub id into which the modes will be created
         * @return a JSON containing teh response of the post method
         */
        @POST("hubs/{hub_id}/modes/")
        Call<ResponseBody> addMode(@Path("hub_id") int hub_id, @Body Mode email);

        /**
         * This method will remove a modes user from the hub
         *
         * @param hub_id   the hub id into which the modes will be removed
         * @param modes_id the id of the modes to be removed
         * @param hub_id   the hub id into which the guest will be removed
         * @param modes_id the id of the guest to be removed
         * @return a JSON containing teh response of the delete method
         */
        @DELETE("hubs/{hub_id}/modes/{modes_id}/")
        Call<ResponseBody> deleteMode(@Path("hub_id") int hub_id, @Path("modes_id") int modes_id);


        @GET("hubs/{hub_id}/endpoints/")
        Call<List<Endpoint>> getendpoints(@Path("hub_id") String hub_id);

        @PUT("hubs/{hub_id}/modes/{mode_id}/")
        Call<List<Endpoint>> editMode(@Path("hub_id") int hub_id, @Path("mode_id") int mode_id, @Body Mode mode);
    }

    public static class Triplet implements Parcelable, IGridItem {
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
        private Room room;
        private ArrayList<Command> off = new ArrayList<>();
        private ArrayList<Command> on = new ArrayList<>();
        private boolean clicked = false;

        public Triplet(Room r) {
            room = r;
        }

        protected Triplet(Parcel in) {
            room = in.readParcelable(Room.class.getClassLoader());
            off = in.createTypedArrayList(Command.CREATOR);
            on = in.createTypedArrayList(Command.CREATOR);
            clicked = in.readByte() != 0;
        }

        public void addOffCommand(Command n) {
            off.add(n);
        }

        public void addOnCommand(Command n) {
            on.add(n);
        }

        public ArrayList<Command> getOff() {
            clicked = false;
            return off;
        }

        public ArrayList<Command> getOn() {
            clicked = true;
            return on;
        }

        public boolean getClicked() {
            return clicked;
        }

        @Override
        public boolean equals(Object object) {
            boolean same = false;

            if (object != null && object instanceof Triplet) {
                if (room != null && ((Triplet) object).room != null) {
                    same = this.room.getDescription() == ((Triplet) object).room.getDescription();
                }
            }
            return same;
        }

        @Override
        public String getName() {
            return room.getDescription();
        }

        @Override
        public String getImage() {
            return "room";
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(off);
            dest.writeTypedList(on);
            dest.writeParcelable(room, flags);
            dest.writeByte((byte) (clicked ? 1 : 0));
        }
    }
}
