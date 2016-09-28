package co.ar_smart.www.helpers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import co.ar_smart.www.interfaces.ICommandClass;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.hue.HueEndpoint;
import co.ar_smart.www.pojos.sonos.SonosEndpoint;
import co.ar_smart.www.pojos.zwave_binary.ZwaveBinaryEndpoint;
import co.ar_smart.www.pojos.zwave_level.ZwaveLevelEndpoint;
import co.ar_smart.www.pojos.zwave_lock.ZwaveLockEndpoint;
import okhttp3.MediaType;

/**
 * This class will contain a list of constants to be used by the application
 * Created by Gabriel on 4/27/2016.
 */
public final class Constants {
    /**
     * The ID of the shared preferences file
     */
    public static final String PREFS_NAME = "living_preferences";
    /**
     * The ID of the user email inside the shared preferences
     */
    public static final String PREF_EMAIL = "Email";
    /**
     * The ID of the user password inside the shared preferences
     */
    public static final String PREF_PASSWORD = "Password";
    /**
     * The ID of the api token inside the shared preferences
     */
    public static final String PREF_JWT = "JWT";
    /**
     * The ID of the default/preferred living hub to load inside the shared preferences
     */
    public static final String PREF_HUB = "PreferredHub";
    /**
     * The default value of the user email inside the shared preferences
     */
    public static final String DEFAULT_EMAIL = "";
    /**
     * The default value of the user password inside the shared preferences
     */
    public static final String DEFAULT_PASSWORD = "";
    /**
     * The default value of the api token inside the shared preferences
     */
    public static final String DEFAULT_JWT = "";
    /**
     * The default value of the default/preferred living hub inside the shared preferences
     */
    public static final String DEFAULT_HUB = "-1";
    /**
     * The base URL of the api server
     */
    public static final String BASE_URL = "https://living.ar-smart.co/v1/";
    /**
     * The login URL
     */
    public static final String LOGIN_URL = BASE_URL+"api-token-auth/";
    /**
     * The URL for changing the user password
     */
    public static final String CHANGE_PASSWORD_URL = BASE_URL + "change_password/";
    /**
     * The register URL
     */
    public static final String REGISTER_URL = BASE_URL+"users/";
    /**
     * The URL for register a hub
     */
    public static final String HUB_REGISTER_URL = BASE_URL+"hubs/";
    /**
     * UID of intra activities messages. Is used for passing messages between intents among activities.
     */
    public static final String EXTRA_MESSAGE = "co.ar-smart.www.living.MESSAGE";
    /**
     * UID of intra activities messages. Is used for passing messages between intents among activities.
     */
    public static final String EXTRA_ADITIONAL_MESSAGE = "co.ar-smart.www.living.ADITIONAL_MESSAGE";
    /**
     * UID of intra activities messages (parcelable objects to be passed). Is used for passing pojos between intents among activities.
     */
    public static final String EXTRA_OBJECT = "co.ar-smart.www.living.PARCEL";
    /**
     * UID of intra activities messages. Is used for passing messages between intents among activities.
     */
    public static final String EXTRA_MESSAGE_PREF_HUB = "co.ar-smart.www.living.MESSAGE_PREF_HUB";
    /**
     * UID of intra activities messages. Is used for passing messages between intents among activities.
     */
    public static final String EXTRA_BOOLEAN = "co.ar-smart.www.living.BOOLEAN";
    /**
     * The header representing JSON media type for he okHttp library
     */
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    /**
     * SSID for connecting to the Living hotspot network while the installation.
     */
    public static final String LIVING_HOTSPOT_SSID = "Domu-AP";
    /**
     * Password for connecting to the Living hotspot network while the installation.
     */
    public static final String LIVING_HOTSPOT_PASSWORD = "DomuAP2016";
    public static final String LIVING_URL = "http://192.168.42.1:8080/";
    /**
     * Default latitude for map-like activities
     */
    public static final double DEFAULT_LATITUDE = 4;
    /**
     * Default latitude for map-like activities
     */
    public static final double DEFAULT_LONGITUDE = -72;
    /**
     * Default radius for map-like activities
     */
    public static final double DEFAULT_RADIUS = 1000;
    /**
     * Default background image path
     */
    public static final String DEFAULT_BACKGROUND_PATH = "drawable://" + R.drawable.default_background;
    public static final String EXTRA_ADDITIONAL_OBJECT = "co.ar-smart.www.living.EXTRA_PARCEL";

    //Add by Sergio
    /**
     * Extra ID add devices type
     */
    public static final String EXTRA_TYPE_DEVICE = "EXTRA_TYPE_DEV";
    /**
     * Extra ID device category
     */
    public static final String EXTRA_CATEGORY_DEVICE = "EXTRA_CATEGORY_DEV";
    /**
     * Type WIFI device
     */
    public static final String TYPE_DEVICE_WIFI = "TYPE_DEV_WIFI";
    /**
     * Type ZWAVE device
     */
    public static final String TYPE_DEVICE_ZWAVE = "TYPE_DEV_ZWAVE";
    /**
     * ID Extra Action
     */
    public static final String EXTRA_ACTION = "Extra_ACTION";
    /**
     * ID Extra UID
     */
    public static final String EXTRA_UID = "EXTRA_UID";

    public static final String ACTION_EDIT = "ACTION_edit";

    public static final String ACTION_ADD = "ACTION_add";

    public static final int TIMEOUT_DEVICES__SECS = 60;

    public static final int PULL_INTERVAL_SECS = 2;

    public static final String EXTRA_ROOM = "EXTRA_ROOM";

    public static final String EXTRA_MODE = "EXTRA_MODE";
    /**
     *
     */
    public static final String EXTRA_LIST_PARCELABLE_FIRST = "co.ar-smart.www.living.EXTRA_LIST_PARCELABLE_FIRST";
    /**
     * Array of the days of the week
     */
    public static final String[] daysArray = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    /**
     * Default boolean list of selected days
     */
    public static final boolean[] boolSelectedDaysArray = new boolean[]{true, false, true, false, true, false, true};
    public static final String UI_CLASS_SONOS = "ui-sonos";
    public static final String UI_CLASS_ZWAVE_BINAY_LIGHT = "ui-binary-light-zwave";
    public static final String UI_CLASS_ZWAVE_LEVEL_LIGHT = "ui-level-light-zwave";
    public static final String UI_CLASS_ZWAVE_LEVEL_THERMOSTAT = "ui-level-thermostat-zwave";
    public static final String UI_CLASS_ZWAVE_BINARY_OUTLET = "ui-binary-outlet-zwave";
    public static final String UI_CLASS_ZWAVE_LOCK = "ui-lock-zwave";
    public static final String UI_CLASS_ZWAVE_SHADES = "ui-shades-zwave";
    public static final String UI_CLASS_ZWAVE_TEMPERATURE_SENSOR = "ui-temp-sensor-zwave";
    public static final String UI_CLASS_HUE = "ui-hue";
    public static final String UI_CLASS_ZWAVE_BINARY_SWITCH = "ui-switch-binary-zwave";
    public static final String UI_CLASS_ZWAVE_OPEN_CLOSE_SENSOR = "ui-sensor-open-close-zwave";
    public static final String UI_CLASS_ZWAVE_MOTION_SENSOR = "ui-sensor-motion-zwave";
    public static final String UI_CLASS_ZWAVE_LEVEL_SWITCH = "ui-switch-multilevel-zwave";
    public static final String UI_CLASS_ZWAVE_BINARY_SENSOR = "ui-sensor-binary-zwave";
    public static final String UI_CLASS_ZWAVE_LEVEL_SENSOR = "ui-sensor-multilevel-zwave";
    public static final String UI_CLASS_ZWAVE_BINARY_GENERIC = "ui-basic-zwave";
    public static final String UI_CLASS_ZWAVE_WATER_SENSOR = "ui-water-sensor-zwave";
    public static final String UI_CLASS_ZWAVE_ENERGY_SENSOR = "ui-energy-sensor-zwave";
    public static final String ZWAVE_FUNCTION_LEVEL_SET = "zwif_level_set";
    public static final String ZWAVE_FUNCTION_DOOR_SET = "zwif_dlck_op_set";
    public static final String ZWAVE_FUNCTION_BINARY_SET = "zwif_switch_set";
    public static final String ZWAVE_FUNCTION_BASIC_SET = "zwif_basic_set";
    public static final String SONOS_FUNCTION_STOP = "stop";
    public static final String SONOS_FUNCTION_PLAY = "play";
    public static final String HUE_FUNCTION_OFF = "turn_off_all_lights";
    public static final String HUE_FUNCTION_ON = "turn_on_all_lights";
    public static final String PASSWORD_REGEX = "(/^(?=.*\\d)(?=.*[A-Z])([@$%&#])[0-9a-zA-Z]{4,}$/)";
    /**
     * Constant for path to user selected background
     */
    public final static String DEFLT_BACKGRND = "default_backgroung";
    /**
     * HashMap of the days of the week with its abreviatures
     * key for map is a String
     */
    private static Map<String,String> daysMapString;
    /**
     * HashMap of the days of the week with its abreviatures
     * key for map is an Integer
     */
    private static Map<Integer, String> daysMapInt;

    public static HashMap<String, ICommandClass> getUiMapClasses() {
        HashMap<String, ICommandClass> uiMapClasses = new HashMap<>();
        uiMapClasses.put(UI_CLASS_SONOS, new SonosEndpoint());
        uiMapClasses.put(UI_CLASS_ZWAVE_BINAY_LIGHT, new ZwaveBinaryEndpoint());
        uiMapClasses.put(UI_CLASS_ZWAVE_LEVEL_LIGHT, new ZwaveLevelEndpoint());
        uiMapClasses.put(UI_CLASS_ZWAVE_BINARY_OUTLET, new ZwaveBinaryEndpoint());
        uiMapClasses.put(UI_CLASS_ZWAVE_LOCK, new ZwaveLockEndpoint());
        uiMapClasses.put(UI_CLASS_ZWAVE_SHADES, new ZwaveLevelEndpoint());
        uiMapClasses.put(UI_CLASS_HUE, new HueEndpoint());
        uiMapClasses.put(UI_CLASS_ZWAVE_BINARY_SWITCH, new ZwaveBinaryEndpoint());
        uiMapClasses.put(UI_CLASS_ZWAVE_LEVEL_SWITCH, new ZwaveLevelEndpoint());
        uiMapClasses.put(UI_CLASS_ZWAVE_BINARY_GENERIC, new ZwaveBinaryEndpoint());
        return uiMapClasses;
    }

    public static Map<String,String> getHashMapDaysFromString()
    {
        if(daysMapString == null)
        {
            daysMapString = new HashMap<>();
            daysMapString.put("Monday", "Mon");
            daysMapString.put("Tuesday", "Tue");
            daysMapString.put("Wednesday", "Wed");
            daysMapString.put("Thursday", "Thu");
            daysMapString.put("Friday", "Fri");
            daysMapString.put("Saturday", "Sat");
            daysMapString.put("Sunday", "Sun");
        }
        return daysMapString;
    }

    public static Map<Integer,String> getHashMapDaysFromInteger()
    {
        if(daysMapInt == null)
        {
            daysMapInt = new HashMap<>();
            daysMapInt.put(0, "Mon");
            daysMapInt.put(1, "Tue");
            daysMapInt.put(2, "Wed");
            daysMapInt.put(3, "Thu");
            daysMapInt.put(4, "Fri");
            daysMapInt.put(5, "Sat");
            daysMapInt.put(6, "Sun");
        }
        return daysMapInt;
    }

    public static double FToCTemperature(int f) {
        return (f - 32) / 1.8000;
    }

    /**
     * This method will return a future date given a timeout in seconds.
     *
     * @param timeout number of seconds into the future
     * @return current date + timeout in seconds
     */
    public static Date calculateTimeout(int timeout) {
        Calendar date = Calendar.getInstance();
        long t = date.getTimeInMillis();
        return new Date(t + (1000 * timeout));
    }

    /**
     * This method will show a no internet error message to the user
     */
    public static void showNoInternetMessage(Context c) {
        Toast.makeText(c.getApplicationContext(), c.getResources().getString(R.string.toast_missing_internet),
                Toast.LENGTH_LONG).show();
    }

    /**
     * This method will show a custom message to the user
     */
    public static void showCustomMessage(Context c, String message) {
        Toast.makeText(c.getApplicationContext(), message,
                Toast.LENGTH_LONG).show();
    }

    public static void showDialogMessage(final String message, final String submessage, final Activity context) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_device_added);

                TextView title = (TextView) dialog.findViewById(R.id.titleAddDevice);
                title.setText(message);

                TextView subtitle = (TextView) dialog.findViewById(R.id.subtitleAddDevice);
                subtitle.setText(submessage);

                Button dialogButton = (Button) dialog.findViewById(R.id.btnDialogDevAdd);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        context.finish();
                    }
                });
                dialog.show();
            }
        });
    }


    /**
     * Enum for sensors type for Triggers
     */
    public enum Trigger_type {
        BINARY, RANGE
    }

    /**
     * Enum for operands for triggers
     */
    public enum Operand {
        less, greater, equals, between, not_between, distinc
    }
}
