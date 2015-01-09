package porsius.nl.topo.play;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import porsius.nl.topo.data.MMapLoc;
import porsius.nl.topo.data.MapLocDatasource;
import porsius.nl.topo.R;

public class PlayActivityGoogle extends FragmentActivity {

        private GoogleMap mMap; // Might be null if Google Play services APK is not available.

        private MapLocDatasource mapLocDatasource;
        List<MMapLoc> list;
        private List<MyLatLng> latLngList = new ArrayList<MyLatLng>();
        private List<Marker> markers = new ArrayList<Marker>();
        private int map_id;
        private TextView playQuestion;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_play);

            playQuestion = (TextView) findViewById(R.id.playQuestion);
            ImageButton political = (ImageButton) findViewById(R.id.buttonPolitical);
            ImageButton satellite = (ImageButton) findViewById(R.id.buttonSatellite);

            political.setOnClickListener(new ChangeMapType());
            satellite.setOnClickListener(new ChangeMapType());
        }

    private class ChangeMapType implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            if(mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE)
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            else
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
    }
        @Override
        protected void onResume() {
            super.onResume();

            Intent intent = getIntent();
            map_id = intent.getIntExtra("MAP_ID", -1);

            System.out.println("xxx map_id: "+ map_id);

            mapLocDatasource = new MapLocDatasource(this);
            mapLocDatasource.open();

            if(map_id!=-1)
            {
                list = mapLocDatasource.getAllLocs(map_id);
                for(int i=0;i<list.size();i++)
                {
                    latLngList.add(new MyLatLng(list.get(i).getName(), list.get(i).getLat(), list.get(i).getLng()));
                }
            }



            setUpMapIfNeeded();
        }

        public void onStop()
        {
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

            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

            Marker marker;
            for(int i=0;i<latLngList.size();i++)
            {
                marker = mMap.addMarker(new MarkerOptions()
                        .position(latLngList.get(i).latLng)
                        .title(latLngList.get(i).name)
                        )
                ;

                markers.add(marker);
            }

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                   if(marker.getTitle().equalsIgnoreCase(playQuestion.getText().toString()))
                   {
                      // int index = latLngList.indexOf(new MyLatLng(marker.getTitle(), marker.getPosition().latitude, marker.getPosition().longitude));
                       int index = markers.indexOf(marker);

                       if(index!=-1) {
                           markers.remove(index);
                           latLngList.remove(index);
                       }

                       marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                   }
                    String question = getQuestion();
                    if(question==null) {
                        playQuestion.setText("Goed zo!");
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    }
                    else
                    {
                        playQuestion.setText(question);
                    }
                    return false;
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
            playQuestion.setText(getQuestion());
        }
    private LatLngBounds getBounds()
    {
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(Marker m: markers){
            LatLng latlng = m.getPosition();
            builder.include(latlng);
        }

        return builder.build();
    }
    private String getQuestion()
    {
        int min = 0;
        int max = latLngList.size();
        String question = null;

        if(latLngList.size()>0) {
            Random random = new Random();
            int i = random.nextInt(max - min) + min;

            question = latLngList.get(i).name;

        }

        return question;
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


