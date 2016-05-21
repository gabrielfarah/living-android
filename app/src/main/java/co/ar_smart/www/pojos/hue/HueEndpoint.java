package co.ar_smart.www.pojos.hue;

import co.ar_smart.www.pojos.Endpoint;

/**
 * This class will contain all the commands and attributes of an endpoint (device) of kind SONOS Music Player
 * Created by Gabriel on 5/16/2016.
 */
public class HueEndpoint {

    /**
     * This is the command for obtaining all the information to pain the controller UI
     */
    private static String get_ui = "";
    /**
     * This is the base endpoint information. It contains the attributes of the device like the ip address.
     */
    private Endpoint endpoint;

    /**
     * The constructor of a new SonosEndpoint class
     *
     * @param nEndpoint the base endpoint with the required fields (specially ip)
     */
    public HueEndpoint(Endpoint nEndpoint) {
        endpoint = nEndpoint;
        get_ui = "[{\"type\":\"wifi\",\"target\":\"hue\",\"ip\":\"" + endpoint.getIp_address() + "\",\"function\":\"get_ui_info\",\"parameters\":[]}]";
    }


    /**
     * this method return the formatted get ui command
     *
     * @return the get ui command
     */
    public String get_ui() {
        return get_ui;
    }


    @Override
    public String toString() {
        return "(" + endpoint + ")";
    }
}
