package com.example.mapapplication;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mapapplication.network.VolleySingleton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapsActivity extends AppCompatActivity {

    private MapView mapView;
    private String showUrl = "http://10.0.2.2/map_project/getPosition.php";
    private GeoPoint lastKnownPosition = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(),
                getSharedPreferences("osmdroid", MODE_PRIVATE));
        setContentView(R.layout.activity_maps);

        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(14.0);
        mapView.getController().setCenter(new GeoPoint(48.8566, 2.3522)); // Paris

        Button btnCenter = findViewById(R.id.btnCenter);
        btnCenter.setOnClickListener(v -> {
            if (lastKnownPosition != null) {
                mapView.getController().animateTo(lastKnownPosition);
            } else {
                Toast.makeText(this, "Position inconnue", Toast.LENGTH_SHORT).show();
            }
        });

        loadMarkers();
    }

    private void loadMarkers() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, showUrl, null,
                response -> {
                    try {
                        JSONArray positions = response.getJSONArray("positions");
                        for (int i = 0; i < positions.length(); i++) {
                            JSONObject pos = positions.getJSONObject(i);
                            double lat = pos.getDouble("latitude");
                            double lng = pos.getDouble("longitude");
                            String date = pos.getString("date");

                            GeoPoint point = new GeoPoint(lat, lng);
                            Marker marker = new Marker(mapView);
                            marker.setPosition(point);
                            marker.setTitle("Position du " + date);
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                            // Redimensionner l'icône
                            Drawable d = getResources().getDrawable(R.drawable.marker, getTheme());
                            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 80, 80, false);
                            marker.setIcon(new BitmapDrawable(getResources(), scaled));

                            mapView.getOverlays().add(marker);
                            lastKnownPosition = point;
                        }
                        mapView.invalidate();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erreur JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Snackbar.make(mapView, "Erreur de chargement", Snackbar.LENGTH_LONG).show();
                });
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}