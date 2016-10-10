package co.ar_smart.www.register;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;

import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.living.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapRegisterHubActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        OnMapReadyCallback {

    /**
     * Radius of earth in meters for
     */
    public static final double RADIUS_OF_EARTH_METERS = 6371009;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 3;
    /**
     * Google api client for static map image
     */
    private GoogleApiClient mGoogleApiClient;
    /**
     * Google map showed in activity
     */
    private GoogleMap mMap;
    /**
     * Circle showed over the map
     */
    private DraggableCircle mCircles;
    /**
     * Current marker latitude
     */
    private double latitude = 4.676623;
    /**
     * Current marker longitude
     */
    private double longitude = -74.048305;
    /**
     * Circle filling color
     */
    private int colorCircle;
    /**
     * Current context
     */
    private Context mContext;
    /**
     * Textview message
     */
    private TextView map_message;

    /**
     * Generate LatLng of radius marker
     */
    private static LatLng toRadiusLatLng(LatLng center, double radius) {
        double radiusAngle = Math.toDegrees(radius / RADIUS_OF_EARTH_METERS) /
                Math.cos(Math.toRadians(center.latitude));
        return new LatLng(center.latitude, center.longitude + radiusAngle);
    }

    private static double toRadiusMeters(LatLng center, LatLng radius) {
        float[] result = new float[1];
        Location.distanceBetween(center.latitude, center.longitude,
                radius.latitude, radius.longitude, result);
        return result[0];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_register_hub);
        latitude = getIntent().getDoubleExtra("lat", 4.676623);
        longitude = getIntent().getDoubleExtra("long", -74.048305);

        mContext = this;
        int tempColor = ContextCompat.getColor(getApplicationContext(), R.color.soporteClaro);
        colorCircle = Color.argb(100, Color.red(tempColor), Color.green(tempColor), Color.blue(tempColor));
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        map_message = (TextView) findViewById(R.id.map_message);
        final TextView back = (TextView) findViewById(R.id.goBackText);
        back.setVisibility(View.GONE);
        final TextView forward = (TextView) findViewById(R.id.goForwardText);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map_message.setText(R.string.label_map_register_back);
                forward.setVisibility(View.VISIBLE);
                back.setVisibility(View.GONE);
            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map_message.setText(R.string.label_map_register_forward);
                forward.setVisibility(View.GONE);
                back.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.clear();
        double preRadius = getIntent().getDoubleExtra("radius", Constants.DEFAULT_RADIUS);
        double radius = preRadius == 0.0 ? Constants.DEFAULT_RADIUS : preRadius;
        LatLng defaultLocation = new LatLng(latitude, longitude);
        mCircles = new DraggableCircle(defaultLocation, radius);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 14.0f));
    }

    /**
     * Method called when user make a long click in the map
     * Replace the point of the map with a new centered in the new position
     * @param point
     */
    @Override
    public void onMapLongClick(LatLng point) {
        mMap.clear();
        mCircles = new DraggableCircle(point, Constants.DEFAULT_RADIUS);
    }

    /**
     * Method called when user clic in done button
     * @param myBitmap
     */
    private void endActivity(final Bitmap myBitmap) {
        MapRegisterHubActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                setResult(Activity.RESULT_OK,
                        new Intent().putExtra("latitude", mCircles.centerMarker.getPosition().latitude)
                                .putExtra("longitude", mCircles.centerMarker.getPosition().longitude)
                                .putExtra("radius", mCircles.radius)
                                .putExtra("bitmap", myBitmap));
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Get the feed icon and add the click action + change its color to white
        getMenuInflater().inflate(R.menu.map_register_hub_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.Continue:
                try {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        String url = "http://maps.google.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=14&size=400x130&sensor=false";
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder().url(url)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                InputStream in = response.body().byteStream();
                                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                                endActivity(myBitmap);
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getPhoneLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            mMap.clear();
            double preRadius = getIntent().getDoubleExtra("radius", Constants.DEFAULT_RADIUS);
            double radius = preRadius == 0.0 ? Constants.DEFAULT_RADIUS : preRadius;
            LatLng defaultLocation = new LatLng(latitude, longitude);
            mCircles = new DraggableCircle(defaultLocation, radius);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 14.0f));
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        getPhoneLocation();
    }


    @Override
    public void onMarkerDragStart(Marker marker)
    {
        onMarkerMoved(marker);
    }

    @Override
    public void onMarkerDrag(Marker marker)
    {
        onMarkerMoved(marker);
    }

    @Override
    public void onMarkerDragEnd(Marker marker)
    {
        onMarkerMoved(marker);
    }

    private void onMarkerMoved(Marker marker)
    {
        mCircles.onMarkerMoved(marker);
    }

    @Override
    public void onConnectionSuspended(int i){}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){}

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPhoneLocation();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private class DraggableCircle
    {

        private final Marker centerMarker;

        private final Marker radiusMarker;

        private final Circle circle;

        private double radius;

        public DraggableCircle(LatLng center, double radius)
        {
            this.radius = radius;
            centerMarker = mMap.addMarker(new MarkerOptions()
                    .position(center)
                    .draggable(true));
            radiusMarker = mMap.addMarker(new MarkerOptions()
                    .position(toRadiusLatLng(center, radius))
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_GREEN)));
            circle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .fillColor(colorCircle)
                    .radius(radius));
        }

        public boolean onMarkerMoved(Marker marker)
        {
            if (marker.equals(centerMarker))
            {
                circle.setCenter(marker.getPosition());
                radiusMarker.setPosition(toRadiusLatLng(marker.getPosition(), radius));
                latitude = marker.getPosition().latitude;
                longitude = marker.getPosition().longitude;
                return true;
            }
            if (marker.equals(radiusMarker))
            {
                radius = toRadiusMeters(centerMarker.getPosition(), radiusMarker.getPosition());
                circle.setRadius(radius);
                return true;
            }
            return false;
        }
    }

}
