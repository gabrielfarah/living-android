package co.ar_smart.www.helpers;

import android.content.Context;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import co.ar_smart.www.living.R;
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
     * The URL for changing the user password
     */
    public static final String CHANGE_PASSWORD_URL = BASE_URL + "change_password/";
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
}
