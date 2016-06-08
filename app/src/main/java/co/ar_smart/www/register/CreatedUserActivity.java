package co.ar_smart.www.register;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.io.InputStream;

import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.living.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CreatedUserActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    /**
     * Google api client for static map image
     */
    GoogleApiClient mGoogleApiClient;
    /**
     * Last location registered for the user
     */
    Location mLastLocation;

    /**
     * Constant used when the application verifies the permissions given by the user
     */
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    /**
     * Boolean that represent if the user give the required permissions
     */
    private boolean permissionCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_user);
        final Context mContext = this;

        //Initialize atribute in charge of getting the static map image
        askAndroidPermissions();
        initializeAll();
    }

    /**
     * Method that initialize the atributes that get the static map for the next activity
     */
    private void initializeAll() {
        //verifies if the user have given the required permissions
        permissionCheck = (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=-1)
                            && (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=-1);
        // in the positive case creates and initialize the atributes for getting the static map image
        if (permissionCheck) {
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
            mGoogleApiClient.connect();
            mLastLocation = new Location("Last registered location");
            mLastLocation.setLatitude(Constants.DEFAULT_LATITUDE);
            mLastLocation.setLongitude(Constants.DEFAULT_LONGITUDE);
        }
        else {
            // in the negative case shows again the permission ask so the user can accept them.
            askAndroidPermissions();
            initializeAll();
        }
    }

    /**
     * This method ask the user the required permissions for the application to work well
     */
    private void askAndroidPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            // PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    /**
     * Creates bitmap with static map of last registered location
     * @param v - View needed for onClick property
     */
    public void registerHub(View v)
    {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            //Assign last registered location
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(mLastLocation==null)
            {
                mLastLocation = new Location("Last registered location");
                mLastLocation.setLatitude(Constants.DEFAULT_LATITUDE);
                mLastLocation.setLongitude(Constants.DEFAULT_LONGITUDE);
            }
            //URL of last registered location static map
            String url = "http://maps.google.com/maps/api/staticmap?center=" +
                    String.valueOf(mLastLocation.getLatitude()) + "," +
                    String.valueOf(mLastLocation.getLongitude()) + "&zoom=14&size=400x130&sensor=false";
            //Gets bitmap of the given url
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback()
            {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException
                {

                    InputStream in = response.body().byteStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(in);
                    //Call method in charge of changing activity
                    registerHub(myBitmap);
                }
            });
        }
    }

    /**
     * Methot in charge of redirects to new hub activity
     * @param myBitmap - Bitmap of static map from last registered location
     */
    private void registerHub(Bitmap myBitmap)
    {
        Intent i = new Intent(this, PropertiesRegisterHubActivity.class);
        i.putExtra("bitmap", myBitmap);
        i.putExtra("lat", mLastLocation.getLatitude());
        i.putExtra("long", mLastLocation.getLongitude());
        startActivity(i);
    }

    //-------------------------------------
    // Methods required by implementations
    //-------------------------------------

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Necesary method for GoogleApiClient.ConnectionCallbacks implementation
     * @param bundle - Bundle required
     */
    @Override
    public void onConnected(@Nullable Bundle bundle)
    {

    }
    /**
     * Necesary method for GoogleApiClient.ConnectionCallbacks implementation
     * @param i - Integer required
     */
    @Override
    public void onConnectionSuspended(int i)
    {

    }
    /**
     * Necesary method for GoogleApiClient.ConnectionCallbacks implementation
     * @param connectionResult - ConnectionResult required
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

}

