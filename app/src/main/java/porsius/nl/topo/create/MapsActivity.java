package porsius.nl.topo.create;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import porsius.nl.topo.data.FileUtil;
import porsius.nl.topo.data.MMap;
import porsius.nl.topo.data.MMapLoc;
import porsius.nl.topo.data.MapLocDatasource;
import porsius.nl.topo.R;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private MapLocDatasource mapLocDatasource;
    List<MMapLoc> list;
    private List<MyLatLng> latLngList;
    private List<Marker> markers;
    private MMap map;

    private class InitTask extends AsyncTask<Void,Void,Boolean>
    {
        ProgressDialog progressDialog;
        protected void onPreExecute()
        {
            if(progressDialog==null) {
                progressDialog = new ProgressDialog(MapsActivity.this);
            }
            progressDialog.setTitle("Even geduld a.u.b.");
            progressDialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... params) {

            return true;
        }

        protected void onPostExecute(Boolean b)
        {
            if(b) {

            }
            if(progressDialog!=null)
            {
                progressDialog.dismiss();
                progressDialog=null;
            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


    }

    @Override
    protected void onResume() {
        super.onResume();

       // new InitTask().execute();
        latLngList = new ArrayList<MyLatLng>();
        markers = new ArrayList<Marker>();

        Intent intent = getIntent();
        map = intent.getParcelableExtra("MAP");

        if(map==null)
            return;

        getLocations(map);


        setTitle(map.getName());

        Button save = (Button) findViewById(R.id.buttonSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(MMapLoc l: list)
                {
                    mapLocDatasource.deleteMapLoc(l);
                }

                for (MyLatLng l:latLngList) {
                    mapLocDatasource.createLoc(map.getId(), l.name, (float)l.latitude(), (float)l.longitude());
                }


                Toast.makeText(MapsActivity.this, "Kaart opgeslagen", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MapsActivity.this, ListMapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                MapsActivity.this.finish();

            }
        });

        Button delete = (Button) findViewById(R.id.ambuttonDelete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> toDelete = new ArrayList<Integer>();

                for (Marker marker : markers) {

                    if(marker.getSnippet().equalsIgnoreCase("delete")) {
                        int index = markers.indexOf(marker);
                        if (index != -1) {
                            toDelete.add(index);
                        }
                        marker.remove();
                    }
                }

                for(int d=0;d<toDelete.size();d++)
                {
                    int index = toDelete.get(d);
                    markers.remove(index);

                    latLngList.remove(index);
                }
                toDelete.clear();
            }
        });

        setUpMapIfNeeded();
    }

    private void getLocations(MMap map) {
        mapLocDatasource = new MapLocDatasource(this);
        mapLocDatasource.open();

        list = mapLocDatasource.getAllLocs(map.getId());
        for(int i=0;i<list.size();i++)
        {
            latLngList.add(new MyLatLng(list.get(i).getName(), list.get(i).getLat(), list.get(i).getLng()));

        }
    }

    private LatLngBounds getBounds()
    {
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if(markers.size()==0)
            return new LatLngBounds(new LatLng(-90, -180), new LatLng(90,180));

        for(Marker m: markers){
            LatLng latlng = m.getPosition();
            builder.include(latlng);
        }

        return builder.build();
    }
    public void onStop() {
        super.onStop();
        mapLocDatasource.close();

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {

        Marker marker;
        for(int i=0;i<latLngList.size();i++)
        {
            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLngList.get(i).latLng)
                    .title(latLngList.get(i).name)
                    .snippet("")
                    .draggable(true))
            ;

            markers.add(marker);
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);

                alert.setTitle("Nieuwe locatie");
                alert.setMessage("Typ een naam");

// Set an EditText view to get user input
                final EditText input = new EditText(MapsActivity.this);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = String.valueOf(input.getText());

                        Marker marker =  mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(value)
                                .snippet("")
                                .draggable(true));

                        latLngList.add(new MyLatLng(value, marker.getPosition().latitude, marker.getPosition().longitude));
                        markers.add(marker);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();

            }

        });
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {


            @Override
            public void onCameraChange(CameraPosition arg0) {
                // Move camera.
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBounds(), 15));
                // Remove listener to prevent position reset on camera move.
                mMap.setOnCameraChangeListener(null);
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getSnippet()!=null && !marker.getSnippet().equalsIgnoreCase("delete")) {
                    marker.setSnippet("delete");
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin10));
                }
                else
                {
                    marker.setSnippet("");
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                return false;
            }
        });
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {


            }
        });

    }

    private class MyLatLng
    {
        private LatLng latLng;
        private String name;

        public MyLatLng(String name, double latitude, double longitude) {
            this.latLng = new LatLng(latitude,longitude);
            this.name = name;
        }

        public double latitude()
        {
            return latLng.latitude;
        }

        public double longitude()
        {
            return latLng.longitude;
        }
    }
}
