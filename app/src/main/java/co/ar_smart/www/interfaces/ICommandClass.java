package co.ar_smart.www.Interfaces;

import co.ar_smart.www.pojos.Command;

/**
 * Created by user on 19/05/2016.
 */
public interface ICommandClass {

    Command getTurnOnCommand();

    Command getTurnOffCommand();
}
