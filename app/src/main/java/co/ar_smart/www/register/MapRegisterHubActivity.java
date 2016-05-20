package co.ar_smart.www.register;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

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
import co.ar_smart.www.living.R;

public class MapRegisterHubActivity extends FragmentActivity implements GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener, OnMapReadyCallback {

    public static final double RADIUS_OF_EARTH_METERS = 6371009;
    private static final double DEFAULT_RADIUS = 1000000;

    private GoogleMap mMap;
    private DraggableCircle mCircles;
    private int latitude;
    private int longitude;
    private static final int WIDTH_MAX = 50;
    private SeekBar mWidthBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_register_hub);
        latitude = getIntent().getIntExtra("lat",4);
        longitude = getIntent().getIntExtra("long",-74);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        (findViewById(R.id.btnReady)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_OK,
                        new Intent().putExtra("latitude", mCircles.centerMarker.getPosition().latitude)
                                .putExtra("longitude", mCircles.centerMarker.getPosition().longitude)
                                .putExtra("radius", mCircles.radius));
                finish();
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
        // Override the default content description on the view, for accessibility mode.
        googleMap.setContentDescription("Hola");

        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.clear();
        mWidthBar = (SeekBar) findViewById(R.id.widthSeekBar);
        mWidthBar.setMax(WIDTH_MAX);
        mWidthBar.setProgress(10);
        LatLng defaultLocation = new LatLng(latitude, longitude);
        Log.d("DEBUG", "5");
        DraggableCircle circle = new DraggableCircle(defaultLocation, DEFAULT_RADIUS);
        Log.d("DEBUG", "6");
        mCircles = circle;
        Log.d("DEBUG", "7");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 4.0f));
        Log.d("DEBUG", "8");
    }

    @Override
    public void onMapLongClick(LatLng point) {
        // We know the center, let's place the outline at a point 3/4 along the view.
        View view =  getSupportFragmentManager().findFragmentById(R.id.map)
                .getView();
        LatLng radiusLatLng = null;
        if (view != null)
        {
            radiusLatLng = mMap.getProjection().fromScreenLocation(new Point(
                    view.getHeight() * 3 / 4, view.getWidth() * 3 / 4));
        }

        mMap.clear();
        // ok create it
        mCircles = new DraggableCircle(point, radiusLatLng);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        onMarkerMoved(marker);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        onMarkerMoved(marker);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        onMarkerMoved(marker);
    }

    private void onMarkerMoved(Marker marker) {
        mCircles.onMarkerMoved(marker);
    }

    private class DraggableCircle {

        private final Marker centerMarker;

        private final Marker radiusMarker;

        private final Circle circle;

        private double radius;

        public DraggableCircle(LatLng center, double radius) {
            this.radius = radius;
            centerMarker = mMap.addMarker(new MarkerOptions()
                    .position(center)
                    .draggable(true));
            radiusMarker = mMap.addMarker(new MarkerOptions()
                    .position(toRadiusLatLng(center, radius))
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_AZURE)));
            circle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .strokeWidth(mWidthBar.getProgress())
                    .radius(radius));
        }

        public DraggableCircle(LatLng center, LatLng radiusLatLng) {
            this.radius = toRadiusMeters(center, radiusLatLng);
            centerMarker = mMap.addMarker(new MarkerOptions()
                    .position(center)
                    .draggable(true));
            radiusMarker = mMap.addMarker(new MarkerOptions()
                    .position(radiusLatLng)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_AZURE)));
            circle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .strokeWidth(mWidthBar.getProgress())
                    .radius(radius));
        }

        public boolean onMarkerMoved(Marker marker) {
            if (marker.equals(centerMarker)) {
                circle.setCenter(marker.getPosition());
                radiusMarker.setPosition(toRadiusLatLng(marker.getPosition(), radius));
                return true;
            }
            if (marker.equals(radiusMarker)) {
                radius = toRadiusMeters(centerMarker.getPosition(), radiusMarker.getPosition());
                circle.setRadius(radius);
                return true;
            }
            return false;
        }

        public void onStyleChange() {
            circle.setStrokeWidth(mWidthBar.getProgress());
        }
    }
    /** Generate LatLng of radius marker */
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


}
