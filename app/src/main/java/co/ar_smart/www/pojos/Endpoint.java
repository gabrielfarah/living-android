package co.ar_smart.www.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This POJO class represents a Device (Endpoint) in Living.
 * Created by Gabriel on 5/3/2016.
 */
public class Endpoint implements Parcelable{

    /**
     * The Parcelable creator
     */
    public static final Creator<Endpoint> CREATOR = new Creator<Endpoint>() {
        @Override
        public Endpoint createFromParcel(Parcel in) {
            return new Endpoint(in);
        }

        @Override
        public Endpoint[] newArray(int size) {
            return new Endpoint[size];
        }
    };
    /**
     * The name of the device
     */
    private int id;
    /**
     * The name of the device
     */
    private String name;
    /**
     * The name of the manufacturer of the device
     */
    private String manufacturer_name;
    /**
     * The image of the device for using in the app
     */
    private String image;
    /**
     * The unique id the device manufacturer gave to the product
     */
    private String uid;
    /**
     * This field represents if the device is unplugged or not. True == plugged in
     */
    private boolean active;
    /**
     * This field represents if the device is on/off (but plugged in)
     */
    private int state;
    /**
     * The technology the device uses (zwave, wifi or BT
     */
    private String endpoint_type;
    /**
     * The ID of the hub this device is connected to
     */
    private int hub;
    /**
     * The type of controller this device must use
     */
    private String ui_class_command;
    /**
     * The ip of the device (only if endpoint_type == wifi)
     */
    private String ip_address;
    /**
     * The node of the device (only if endpoint_type == zwave)
     */
    private int node;
    /**
     * The name of the room/area this device is on
     */
    private String room;
    /**
     * The category of the device (Lighting, entertainment, security, etc)
     */
    private Category category;

    public Endpoint() {
    }

    /**
     * This constructor method creates an Endpoint object from a Parcel
     * @param in The parcer to deserialize the object
     */
    private Endpoint(Parcel in) {
        name = in.readString();
        manufacturer_name = in.readString();
        image = in.readString();
        uid = in.readString();
        active = (Boolean) in.readValue(getClass().getClassLoader());
        state = in.readInt();
        endpoint_type = in.readString();
        hub = in.readInt();
        ui_class_command = in.readString();
        ip_address = in.readString();
        node = in.readInt();
        room = in.readString();
        category = in.readParcelable(getClass().getClassLoader());
        //in.readTypedList(endpoint_classes, EndpointClass.CREATOR);
        id = in.readInt();
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    /**
     * Get the device node
     * @return device node
     */
    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }

    /**
     * Get the device ip address
     * @return the device ip address
     */
    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    /**
     * Get the device UI class
     * @return the device ui class
     */
    public String getUi_class_command() {
        return ui_class_command;
    }

    public void setUi_class_command(String ui_class_command) {
        this.ui_class_command = ui_class_command;
    }

    /**
     * Get the hub ID
     * @return the hub ID
     */
    public int getHub() {
        return hub;
    }

    /**
     * Get the endpoint type
     *
     * @return the endpoint type
     */
    public String getEndpoint_type() {
        return endpoint_type;
    }

    public void setEndpoint_type(String endpoint_type) {
        this.endpoint_type = endpoint_type;
    }

    /**
     * Get the state of the device
     *
     * @return 0 if off 255 if on or a value for other devices
     */
    public int isState() {
        return state;
    }

    /**
     * Get the endpoint image
     *
     * @return the endpoint imege
     */
    public String getImage() {
        return image;
    }

    /**
     * Get the endpoint UID
     *
     * @return the endpoint UID
     */
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Get if the device is active
     *
     * @return true if active
     */
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Get the endpoint name
     * @return the endpoint name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the manufacturer name
     *
     * @return the manufacturer name
     */
    public String getManufacturer_name() {
        return manufacturer_name;
    }

    public void setManufacturer_name(String manufacturer_name) {
        this.manufacturer_name = manufacturer_name;
    }

    /**
     * Get the room name (if any)
     *
     * @return the endpoint room name
     */
    public String getRoom() {
        return room;
    }

    /**
     * Get the category of the endpoint
     *
     * @return the endpoint category
     */
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Get the ID of the endpoint
     *
     * @return the endpoint ID
     */
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "(" + name + " - " + id + ")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(manufacturer_name);
        dest.writeString(image);
        dest.writeString(uid);
        dest.writeValue(active);
        dest.writeInt(state);
        dest.writeString(endpoint_type);
        dest.writeInt(hub);
        dest.writeString(ui_class_command);
        dest.writeString(ip_address);
        dest.writeInt(node);
        dest.writeString(room);
        dest.writeParcelable(category, flags);
        //dest.writeTypedList(endpoint_classes);
        dest.writeInt(id);
    }

    /**
     * This method setssome parameters into the class
     *
     * @param nName  the new name
     * @param nImage the new image
     * @param nRoom  the new room name
     */
    public void setAtributes(String nName, String nImage, String nRoom) {
        this.name = nName;
        this.image = nImage;
        this.room = nRoom;
    }

    @Override
    public boolean equals(Object object) {
        boolean isSame = false;

        if (object != null && object instanceof Endpoint) {
            isSame = this.id == ((Endpoint) object).id;
        }

        return isSame;
    }
}
