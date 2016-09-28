package co.ar_smart.www.helpers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.interfaces.ICommandClass;
import co.ar_smart.www.interfaces.IGridItem;
import co.ar_smart.www.pojos.Command;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.Room;

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
            te = map.get(e.getUi_class_command());
            if (te != null) {
                te.setEndpoint(e);
                Triplet nTriplet = new Triplet(new Room(hub_id, e.getRoom()));
                if (!response.contains(nTriplet)) {
                    nTriplet.addOffCommand(te.getTurnOffCommand());
                    nTriplet.addOnCommand(te.getTurnOnCommand());
                    response.add(nTriplet);
                } else {
                    Triplet triplet = response.get(response.indexOf(nTriplet));
                    triplet.addOffCommand(te.getTurnOffCommand());
                    triplet.addOnCommand(te.getTurnOnCommand());
                }
            } else {
                AnalyticsApplication.getInstance().trackEvent("Device UI Class", "DoNotExist", "The device in hub:" + e.getHub() + " named:" + e.getName() + " the ui class does not correspond. UI:" + e.getUi_class_command());
            }
        }
        return response;
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
                if (this.room != null && ((Triplet) object).room != null) {
                    same = this.room.getDescription().equalsIgnoreCase(((Triplet) object).room.getDescription());
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
