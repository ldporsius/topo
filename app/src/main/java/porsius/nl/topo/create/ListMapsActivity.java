package porsius.nl.topo.create;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import porsius.nl.topo.data.FileUtil;
import porsius.nl.topo.data.MMap;
import porsius.nl.topo.data.MMapLoc;
import porsius.nl.topo.data.MapDatasource;
import porsius.nl.topo.R;
import porsius.nl.topo.data.MapLocDatasource;
import porsius.nl.topo.data.User;

public class ListMapsActivity extends ActionBarActivity {


    private List<MMap> maps;
    private MapDatasource mapDatasource;
    private MapAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_maps);

        mapDatasource = new MapDatasource(this);


        Button buttonNew = (Button) findViewById(R.id.buttonNew);
        buttonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListMapsActivity.this, CreateMapActivity.class);
                startActivity(intent);
            }
        });

        Button buttonLoad = (Button) findViewById(R.id.buttonLoad);
        buttonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ListMapsActivity.this, DownloadsActivity.class);
                startActivity(intent);
            }
        });

    }

    public void onResume()
    {
        super.onResume();

        mapDatasource.open();
        maps = mapDatasource.getAllMaps();
        mapDatasource.close();

        listView = (ListView) findViewById(R.id.listMaps);
        adapter = new MapAdapter(this, R.layout.list_item, maps);
        listView.setAdapter(adapter);
    }

    private class MapAdapter extends ArrayAdapter<MMap>
    {
        private List<MMap> items;
        private int resource;

        public MapAdapter(Context context, int resource, List<MMap> objects) {

            super(context, resource, objects);
            this.items = objects;
            this.resource = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View row = convertView;
            Holder holder = null;

            if(row == null)
            {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(resource, parent, false);

                holder = new Holder();
                holder.textView = (TextView) row.findViewById(R.id.textView);
                holder.buttonEdit = (ImageButton) row.findViewById(R.id.buttonEdit);
                holder.buttonDelete = (ImageButton) row.findViewById(R.id.buttonDelete);
                row.setTag(holder);
            }
            else
            {
                holder = (Holder)row.getTag();
            }


            holder.textView.setText(items.get(position).getName());

            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), EditMapActivity.class);
                    intent.putExtra("MAP", items.get(position));
                    startActivity(intent);
                }
            });

            holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(ListMapsActivity.this);

                    alert.setTitle("Verwijder kaart");
                    alert.setMessage("Weet je het zeker?");

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            List<MMapLoc> toDelete;

                            MapLocDatasource mapLocDatasource = new MapLocDatasource(ListMapsActivity.this);
                            mapLocDatasource.open();

                            toDelete = mapLocDatasource.getAllLocs(items.get(position).getId());
                            for(MMapLoc loc: toDelete) {
                                mapLocDatasource.deleteMapLoc(loc);
                            }
                            mapLocDatasource.close();

                            mapDatasource.open();
                            mapDatasource.deleteMap(items.get(position));
                            mapDatasource.close();

                            adapter.remove(items.get(position));
                            adapter.notifyDataSetChanged();

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

            holder.buttonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendEmail(items.get(position));
                }
            });
            return row;
        }
    }

    static class Holder
    {
        TextView textView;
        ImageButton buttonEdit;
        ImageButton buttonDelete;
    }

    public void sendEmail(MMap map) {

        String filelocation = Environment.getExternalStorageDirectory()+ "/" + FileUtil.STORAGE_DIR
                + "/" + FileUtil.FILE_PREFIX+"-"+map.getName()+".txt";


        MapLocDatasource ds = new MapLocDatasource(ListMapsActivity.this);
        ds.open();

        List<MMapLoc> locs = ds.getAllLocs(map.getId());

        String user = FileUtil.readFile(FileUtil.STORAGE_DIR+"/topo_user.txt");
        String json = ds.toJson(map, locs, user);
        System.out.println(" jjj json : " +json);

        try {
            FileUtil.write(FileUtil.FILE_PREFIX+"-" + map.getName() + ".txt", json, false);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setType("plain/text");
        String to[] = {"linda.d.porsius@gmail.com"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
// the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filelocation)));

// the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Topo kaart");
        startActivity(Intent.createChooser(emailIntent, "Stuur kaart..."));
    }
}

