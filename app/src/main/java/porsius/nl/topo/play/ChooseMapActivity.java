package porsius.nl.topo.play;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import porsius.nl.topo.data.MMap;
import porsius.nl.topo.data.MapDatasource;
import porsius.nl.topo.R;

public class ChooseMapActivity extends Activity {

    private List<MMap> maps;
    private MapDatasource mapDatasource;
    private MapAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_map);

        mapDatasource = new MapDatasource(this);
        mapDatasource.open();


        ListView listView = (ListView) findViewById(R.id.listMaps);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Intent intent = new Intent(ChooseMapActivity.this, PlayActivity.class );
                Intent intent = new Intent(ChooseMapActivity.this, PlayActivityLeaflet.class );
                intent.putExtra("MAP", maps.get(position));
                startActivity(intent);
            }
        });
    }

    public void onResume()
    {
        super.onResume();
        maps = mapDatasource.getAllMaps();

        listView = (ListView) findViewById(R.id.listMaps);
        adapter = new MapAdapter(this,
                android.R.layout.simple_list_item_1, maps);
        listView.setAdapter(adapter);
    }

    private class MapAdapter extends ArrayAdapter<MMap>
    {
        private List<MMap> items;

        public MapAdapter(Context context, int resource, List<MMap> objects) {

            super(context, resource, objects);
            this.items = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Context context = getContext();


            TextView view = new TextView(context);
            view.setText(items.get(position).getName());
            view.setPadding(12,12,12,12);


            return view;
        }
    }

}
