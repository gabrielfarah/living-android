package co.ar_smart.www.register;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_user);
        if (mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mLastLocation = new Location("Last registered location");
        mLastLocation.setLatitude(Constants.DEFAULT_LATITUDE);
        mLastLocation.setLongitude(Constants.DEFAULT_LONGITUDE);
    }

    /**
     * Redirects to Hub Register and creates bitmap with static map of last registered location
     *
     * @param v - View needed for onClick property
     */
    public void registerHub(View v)
    {
        String lastLat = String.valueOf(Constants.DEFAULT_LATITUDE);
        String lastLng = String.valueOf(Constants.DEFAULT_LONGITUDE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null)
            {
                lastLat = String.valueOf(mLastLocation.getLatitude());
                lastLng = String.valueOf(mLastLocation.getLongitude());
            }
            String url = "http://maps.google.com/maps/api/staticmap?center=" + lastLat + "," + lastLng + "&zoom=14&size=400x130&sensor=false";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url)
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
    protected void onStart() {
        mGoogleApiClient.connect();
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

