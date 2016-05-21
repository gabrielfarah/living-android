package co.ar_smart.www.helpers;

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
    public static final String BASE_URL = "http://living.ar-smart.co/v1/";
    /**
     * The login URL
     */
    public static final String LOGIN_URL = BASE_URL+"api-token-auth/";
    /**
     * The user profile URL
     */
    public static final String PROFILE_URL = BASE_URL + "profile/";
    /**
     * The register URL
     */
    public static final String REGISTER_URL = BASE_URL+"users/";
    /**
     * UID of intra activities messages. Is used for passing messages between intents among activities.
     */
    public static final String EXTRA_MESSAGE = "co.ar-smart.www.living.MESSAGE";
    /**
     * UID of intra activities messages (parcelable objects to be passed). Is used for passing pojos between intents among activities.
     */
    public static final String EXTRA_OBJECT = "co.ar-smart.www.living.PARCEL";
    /**
     * UID of intra activities messages. Is used for passing messages between intents among activities.
     */
    public static final String EXTRA_MESSAGE_PREF_HUB = "co.ar-smart.www.living.MESSAGE_PREF_HUB";
    /**
     * The header representing JSON media type for he okHttp library
     */
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    /**
     * SSID for connecting to the Living hotspot network while the installation.
     */
    public static final String LIVING_HOTSPOT_SSID = "FARAH";
    /**
     * Password for connecting to the Living hotspot network while the installation.
     */
    public static final String LIVING_HOTSPOT_PASSWORD = "03FARAH07";

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

}
