package com.example.mapapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.mapapplication.network.VolleySingleton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private LocationManager locationManager;
    private String insertUrl = "http://10.0.2.2/map_project/createPosition.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnMap = findViewById(R.id.btnMap);
        btnMap.setOnClickListener(v -> startActivity(new Intent(this, MapsActivity.class)));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        checkPermissionsAndStart();
    }

    private void checkPermissionsAndStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Veuillez activer le GPS", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 150,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();
                        sendPositionToServer(lat, lon);
                        Toast.makeText(MainActivity.this,
                                String.format("Lat: %.5f, Lon: %.5f", lat, lon),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {
                        Toast.makeText(MainActivity.this, "GPS désactivé", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendPositionToServer(double lat, double lon) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        Map<String, String> params = new HashMap<>();
        params.put("latitude", String.valueOf(lat));
        params.put("longitude", String.valueOf(lon));
        params.put("date", date);
        params.put("imei", androidId);

        StringRequest request = new StringRequest(Request.Method.POST, insertUrl,
                response -> { /* succès silencieux */ },
                error -> Toast.makeText(this, "Erreur envoi", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            Toast.makeText(this, "Permission refusée", Toast.LENGTH_LONG).show();
        }
    }
}