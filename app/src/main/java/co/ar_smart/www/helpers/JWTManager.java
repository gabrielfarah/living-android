package co.ar_smart.www.helpers;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * This helper class, implements the methods responsable to validate the expiracy of a JWT using the HS256 encription algorithm.
 * Created by Gabriel on 4/27/2016.
 */
public class JWTManager {

    /**
     * This method validate if a date passed as parameter if after in time than the actual date of the device.
     * @param jDate string timestamp of a unix epoch
     * @return true if jDate is later in time than current time, false otherwise.
     */
    private static boolean validateDate(String jDate){
        double var = Double.parseDouble(jDate);
        Date expiration_time =new Date((long)var*1000);
        return expiration_time.after(new Date());
    }

    /**
     * This method validates the expiration date of a JWT in string format.
     * The JWT must be generated using the HS256 algorithm and in BASE64 encoding.
     * @param oJWT the string representation of a JWT to be validated given the exp date contained within.
     * @return true if the JWT haven't expired, false otherwise.
     */
    public static boolean validateJWT(String oJWT){
        try {
            String[] jwtTokenValues = oJWT.split("\\.");
            if (jwtTokenValues.length>2) {
                String value = new String(Base64.decode(jwtTokenValues[1].getBytes(), Base64.DEFAULT));
                JSONObject jsonPayload = new JSONObject(value);
                String exp = jsonPayload.getString("exp");
                //OK, now we validate the expiration date
                return validateDate(exp);
            }else {
                return false;
            }
        } catch (JSONException e) {
            Log.d("Error", e.getMessage());
            return false;
        }
    }

}
