package co.ar_smart.www.pojos.zwave_binary;

import co.ar_smart.www.interfaces.ICommandClass;
import co.ar_smart.www.pojos.Command;
import co.ar_smart.www.pojos.Endpoint;

/**
 * This class will contain all the commands and attributes of an endpoint (device) of kind SONOS Music Player
 * Created by Gabriel on 5/16/2016.
 */
public class ZwaveBinaryEndpoint implements ICommandClass {
    /**
     * This is the base endpoint information. It contains the attributes of the device like the ip address.
     */
    private Endpoint endpoint;

    /**
     * The constructor of a new ZwaveBinaryEndpoint class
     *
     * @param nEndpoint the base endpoint with the required fields (specially node)
     */
    public ZwaveBinaryEndpoint(Endpoint nEndpoint) {
        endpoint = nEndpoint;
    }

    public ZwaveBinaryEndpoint() {
    }

    public void setEndpoint(Endpoint nEndpoint) {
        endpoint = nEndpoint;
    }

    @Override
    public String toString() {
        return "(" + endpoint.getName() + "-" + endpoint.getState() + ")";
    }

    @Override
    public Command getTurnOnCommand() {
        Command c = new Command(endpoint);
        c.setFunction("zwif_switch_set");
        c.setType("zwave");
        c.setNode(endpoint.getNode());
        c.setV(255);
        return c;
    }

    @Override
    public Command getTurnOffCommand() {
        Command c = new Command(endpoint);
        c.setFunction("zwif_switch_set");
        c.setType("zwave");
        c.setNode(endpoint.getNode());
        c.setV(0);
        return c;
    }
}
