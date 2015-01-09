package porsius.nl.topo.create;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import porsius.nl.topo.R;
import porsius.nl.topo.data.FileUtil;
import porsius.nl.topo.data.MapDatasource;
import porsius.nl.topo.data.MapLocDatasource;

public class DownloadsActivity extends ActionBarActivity {

    private DownloadsAdapter adapter;
    private ListView listView;
    private List<String> files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        ListView listView = (ListView) findViewById(R.id.listDownloads);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String file = FileUtil.readFile(files.get(position));
                try {
                    JSONObject object = new JSONObject(file);

                    MapDatasource mapDatasource = new MapDatasource(DownloadsActivity.this);
                    mapDatasource.open();

                    int map_id = mapDatasource.fromJson(object);

                    MapLocDatasource mapLocDatasource = new MapLocDatasource(DownloadsActivity.this);
                    mapLocDatasource.open();

                    JSONArray locations = object.getJSONArray("locations");
                    for(int i=0;i<locations.length();i++)
                    {
                        JSONObject loc = (JSONObject) locations.get(i);
                        mapLocDatasource.fromJson(map_id, loc);
                    }

                    mapLocDatasource.close();
                    mapDatasource.close();

                    Intent intent = new Intent(DownloadsActivity.this, ListMapsActivity.class);

                    startActivity(intent);

                    DownloadsActivity.this.finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onResume()
    {
        super.onResume();

        files = FileUtil.getList(Environment.getExternalStorageDirectory(), "/");

        listView = (ListView) findViewById(R.id.listDownloads);
        adapter = new DownloadsAdapter(this, R.layout.list_item1, files);
        listView.setAdapter(adapter);
    }

    private class DownloadsAdapter extends ArrayAdapter<String>
    {
        private List<String> items;
        private int resource;

        public DownloadsAdapter(Context context, int resource, List<String> objects) {

            super(context, resource, objects);
            this.items = objects;
            this.resource = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Context context = getContext();
            View row = convertView;
            Holder holder = null;


            if(row == null)
            {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(resource, parent, false);

                holder = new Holder();
                holder.downloadTitle = (TextView) row.findViewById(R.id.downloadTitle);
                holder.downloadAuthor = (TextView) row.findViewById(R.id.downloadAuthor);

                row.setTag(holder);
            }
            else
            {
                holder = (Holder)row.getTag();
            }

            String file = FileUtil.readFile(items.get(position));

            try {
                JSONObject object = new JSONObject(file);

                System.out.println(" jjj :" + object.toString(2));

                if(object.has("map_name"))
                    holder.downloadTitle.setText(object.getString("map_name"));

                if(object.has("author"))
                    holder.downloadAuthor.setText(object.getString("author"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return row;
        }
    }

    static class Holder
    {
        TextView downloadTitle;
        TextView downloadAuthor;
    }

}
