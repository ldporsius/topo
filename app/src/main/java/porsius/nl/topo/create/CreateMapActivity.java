package porsius.nl.topo.create;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import porsius.nl.topo.data.FileUtil;
import porsius.nl.topo.data.MapDatasource;
import porsius.nl.topo.R;

public class CreateMapActivity extends ActionBarActivity {

    private MapDatasource mapDatasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_map);

        mapDatasource = new MapDatasource(this);
        mapDatasource.open();

        final EditText editName = (EditText) findViewById(R.id.editText);

        Button save = (Button) findViewById(R.id.buttonSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editName.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(CreateMapActivity.this, "Typ een naam", Toast.LENGTH_SHORT).show();
                else {
                    mapDatasource.createMap(editName.getText().toString(), FileUtil.readFile("topo_user.txt"));
                    Toast.makeText(CreateMapActivity.this, "Kaart opgeslagen", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateMapActivity.this, EditMapActivity.class);
                    intent.putExtra("MAP", mapDatasource.getLastMap());
                    startActivity(intent);
                    CreateMapActivity.this.finish();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
