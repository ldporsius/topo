package porsius.nl.topo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import porsius.nl.topo.create.ListMapsActivity;
import porsius.nl.topo.data.FileUtil;
import porsius.nl.topo.play.ChooseMapActivity;


public class StartActivity extends ActionBarActivity {

    private TextView startUser;
    private RelativeLayout startUserContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ImageButton create = (ImageButton) findViewById(R.id.buttonCreate);
        ImageButton play = (ImageButton) findViewById(R.id.buttonPlay);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartActivity.this, ListMapsActivity.class);
                startActivity(i);
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartActivity.this, ChooseMapActivity.class);
                startActivity(i);
            }
        });

        startUser = (TextView) findViewById(R.id.startUser);
        startUserContainer = (RelativeLayout) findViewById(R.id.startUserContainer);
        startUserContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserDialog();
            }
        });
        String user = FileUtil.readFile(FileUtil.STORAGE_DIR+"/topo_user.txt");

        if(user == null)
        {
            showUserDialog();
        }
        else
            Toast.makeText(StartActivity.this, "Hallo "+user, Toast.LENGTH_SHORT).show();
            startUser.setText(user);

        ImageView ship = (ImageView) findViewById(R.id.ship);
        Animation movedown = AnimationUtils.loadAnimation(this, R.anim.movedown);
        ship.startAnimation(movedown);
    }

    private void showUserDialog() {

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Welkom bij Topo");
        alert.setMessage("Wat is je (nick)naam?");
        final EditText editText = new EditText(this);
        alert.setView(editText);
        alert.setPositiveButton("Opslaan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    FileUtil.write("topo_user.txt", editText.getText().toString(), false);
                    startUser.setText(editText.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("Zeg ik niet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FileUtil.delete("topo_user.txt");
                startUser.setText("onbekend");
                dialog.dismiss();
            }
        });

        alert.show();
    }


}
