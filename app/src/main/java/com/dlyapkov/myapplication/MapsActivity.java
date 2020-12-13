package com.dlyapkov.myapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE = 10;
    private EditText textAddress;
    Marker currentMarker;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initViews();
        requestPermissions();
        createGoogleApiClient();
        initNotificationChannel();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        currentMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                .title("Current position"));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Marker marker = addMarker(latLng);
                Geofence geofence = createGeofence(marker);
                createGeofencingRequest(geofence);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Http.requestRetrofit(marker.getPosition().latitude, marker.getPosition().longitude, BuildConfig.WEATHER_API_KEY);


                return false;
            }
        });
    }

    private Marker addMarker(LatLng location) {
        String title = Double.toString(location.latitude) + "," + Double.toString(location.longitude);
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(title));
        mMap.addCircle(new CircleOptions()
                .center(location));
        return marker;
    }

    private void initViews() {
        textAddress = findViewById(R.id.searchAddress);
        initSearchByAddress();
    }

    private void initSearchByAddress() {
        findViewById(R.id.searchAddress).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    searchAddress(v);
                    return true;
                }
                return false;
            }
        });
        findViewById(R.id.buttonSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAddress(v);
            }
        });
    }

    private void searchAddress(View v) {
        hideKeyboard(v);
        final Geocoder geocoder = new Geocoder(MapsActivity.this);
        final String searchText = textAddress.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Address> addresses = geocoder.getFromLocationName(searchText, 1);
                    if (addresses.size() > 0) {
                        final LatLng location = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMap.addMarker(new MarkerOptions()
                                        .position(location)
                                        .title(searchText)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current)));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, (float) 15));
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void createGeofencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        // вешаем триггеры на вход, перемещение внутри и выход из зоны
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT | GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofence(geofence);  // Добавим геозону
        GeofencingRequest geoFenceRequest = builder.build();  // это запрос на добавление геозоны (параметры только что задавали, теперь строим)
        // создадим интент, при сигнале от Google Play будет вызываться этот интент, а интент настроен на запуск службы, обслуживающей всё это
        Intent geoService = new Intent(MapsActivity.this, GeoFenceService.class);
        // интент будет работать через этот класс
        PendingIntent pendingIntent = PendingIntent
                .getService(MapsActivity.this, 0, geoService, PendingIntent.FLAG_UPDATE_CURRENT);
        // это клиент геозоны, собственно он и занимается вызовом нашей службы
        GeofencingClient geoClient = LocationServices.getGeofencingClient(MapsActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geoClient.addGeofences(geoFenceRequest, pendingIntent);   // добавляем запрос запрос геозоны и указываем, какой интент будет при этом срабатывать
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private Geofence createGeofence(Marker marker) {
        // создаем геозону через построитель.
        return new Geofence.Builder()
                .setRequestId(String.valueOf(marker.getTitle()))   // Здесь указывается имя геозоны (вернее это идентификатор, но он строковый)
                // типа геозоны, вход, перемещение внутри, выход
                .setTransitionTypes(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT | GeofencingRequest.INITIAL_TRIGGER_DWELL)
                .setCircularRegion(marker.getPosition().latitude, marker.getPosition().longitude, 150) // Координаты геозоны
                .setExpirationDuration(Geofence.NEVER_EXPIRE)   // Геозона будет постоянной, пока не удалим геозону или приложение
                .setLoiteringDelay(1000)    // Установим временную задержку в мс между событиями входа в зону и перемещения в зоне
                .build();
    }

    private void createGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
        Log.d("GeoFence", "connect to googleApiClient");
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                LatLng currentPosition = new LatLng(lat, lng);
                LatLng prevPosition = currentMarker.getPosition();
                if (!(prevPosition.longitude == 0 && prevPosition.latitude == 0)) {
                    mMap.addPolyline(new PolylineOptions()
                            .add(prevPosition, currentPosition)
                            .color(Color.RED)
                            .width(5));
                }
                currentMarker.setPosition(currentPosition);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, (float) 10));
            }
        });
    }

    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        } else {
            requestLocationPermissions();
        }
    }

    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {   // Это та самая пермиссия, что мы запрашивали?
            if (grantResults.length == 2 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                requestLocation();
            }
        }
    }

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel("2", "name", importance);
            notificationManager.createNotificationChannel(mChannel);
        }
    }

}