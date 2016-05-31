package co.ar_smart.www.interfaces;

import co.ar_smart.www.pojos.Command;
import co.ar_smart.www.pojos.Endpoint;

/**
 * Created by user on 19/05/2016.
 */
public interface ICommandClass {

    Command getTurnOnCommand();

    Command getTurnOffCommand();

    void setEndpoint(Endpoint nEndpoint);
}
