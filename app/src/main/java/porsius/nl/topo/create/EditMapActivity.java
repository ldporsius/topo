package porsius.nl.topo.create;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import porsius.nl.topo.R;
import porsius.nl.topo.data.EditMapInterface;
import porsius.nl.topo.data.MMap;
import porsius.nl.topo.data.MMapLoc;
import porsius.nl.topo.data.MapLocDatasource;

public class EditMapActivity extends ActionBarActivity {

    public static EditText editLocText;
    public static ImageButton newButton;
    public static boolean madeChanges;
    private WebView webview;
    private EditMapInterface editMapInterface;
    private MMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editmap);

        madeChanges = false;

        webview = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        map = getIntent().getParcelableExtra("MAP");

        cleanup();

        editMapInterface = new EditMapInterface(this, map, new Handler());
        webview.addJavascriptInterface(editMapInterface, "Android");

        String url = ("file:///android_asset/leafletCreate.html");
        webview.setWebViewClient(new MyWebViewClient(webview, url));

        ImageButton save = (ImageButton) findViewById(R.id.editButtonSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        newButton = (ImageButton) findViewById(R.id.editButtonNew);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editMapInterface.getClickable()==0) {
                    editMapInterface.setClickable(1);
                    newButton.setImageResource(R.drawable.marker_blue);
                }
                else
                {
                    editMapInterface.setClickable(0);
                    newButton.setImageResource(R.drawable.marker_gray);
                }
            }
        });
        setTitle(map.getName());

        editLocText = (EditText) findViewById(R.id.editLocText);

        editLocText.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {

                MMapLoc loc = editMapInterface.getLocSelected();
                if(loc!=null)
                    loc.setName(editLocText.getText().toString());

                madeChanges = true;

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

    }
    public static void setNewButtonGray()
    {
        newButton.setImageResource(R.drawable.marker_gray);

    }
    public static void setEditText(String text){
        editLocText.setText(text);
        editLocText.setFocusable(true);
        editLocText.setFocusableInTouchMode(true);
        editLocText.requestFocus();
    }

    public void save()
    {
        MapLocDatasource mapLocDatasource = new MapLocDatasource(EditMapActivity.this);
        mapLocDatasource.open();

        mapLocDatasource.deleteAllLocsForMap(map);

        List<MMapLoc> list = editMapInterface.getList();
        list.removeAll(editMapInterface.getListToDelete());
        for (MMapLoc l: list) {
            mapLocDatasource.createLoc(map.getId(), l.getName(), (float)l.getLat(), (float)l.getLng());
        }

        mapLocDatasource.close();
        Toast.makeText(EditMapActivity.this, "Kaart opgeslagen", Toast.LENGTH_SHORT).show();
        webview.reload();

        madeChanges = false;
    }
    private class MyWebViewClient extends WebViewClient {

        private WebView view;
        private String url;

        public MyWebViewClient(WebView view, String url)
        {
            this.view=view;
            this.url=url;

            view.loadUrl(url);
        }
        @Override
        public void onPageFinished(WebView view, String url)
        {

            webview.requestFocus();
        }


    }

    @Override
    public void onBackPressed()
    {

        if(madeChanges)
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Wijzigingen opslaan?")
                    .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                           save();
                           finish();
                        }
                    })
                    .setNegativeButton("Nee", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            alert.show();

        }
        else super.onBackPressed();


    }
    private void cleanup()
    {
        MapLocDatasource mapLocDatasource = new MapLocDatasource(EditMapActivity.this);
        mapLocDatasource.open();

        Set<MMapLoc> toDelete = new HashSet<MMapLoc>();
        List<MMapLoc> list = mapLocDatasource.getAllLocs(map.getId());
        for(int i=0;i<list.size();i++)
        {
            String name = list.get(i).getName();
            for(int ii = i+1; ii<list.size(); ii++)
            {
                if(name.equalsIgnoreCase(list.get(ii).getName()))
                {
                    toDelete.add(list.get(ii));

                    System.out.println(" ooo "+ list.get(ii).getName());
                }
            }
        }
        System.out.println(" ooo list size "+list.size());
        System.out.println(" ooo delete size "+toDelete.size());

        for(MMapLoc loc: toDelete)
        {
            mapLocDatasource.deleteMapLoc(loc);
        }
    }
}
