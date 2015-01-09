package porsius.nl.topo.data;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import porsius.nl.topo.create.EditMapActivity;

/**
 * Created by linda on 01/11/14.
 */
public class EditMapInterface {
    private MMapLoc locSelected;
    private Context context;
    private MMap map;
    public List<MMapLoc> list;
    public List<MMapLoc> toDelete = new ArrayList<MMapLoc>();
    private Handler handler;
    private int clickable = 0;
    /**
     * Instantiate the interface and set the context
     */
    public EditMapInterface(Context c) {
        this(c, null, null);
    }

    public EditMapInterface(Context c, MMap map, Handler h) {
        this.context = c;
        this.map = map;
        this.handler = h;

        MapLocDatasource mapLocDatasource = new MapLocDatasource(context);
        mapLocDatasource.open();

        if(map!=null) {
            list = mapLocDatasource.getAllLocs(map.getId());
        }

        mapLocDatasource.close();
    }

    @JavascriptInterface
    public int getClickable()
    {
        return clickable;
    }
    @JavascriptInterface
    public void setClickable(int c)
    {
        this.clickable=c;
    }
    @JavascriptInterface
    public void setNewButtonGray()
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                EditMapActivity.setNewButtonGray();
            }
        });
    }
    @JavascriptInterface
    public String getMark()
    {
        String result = "";

        for(int i=0;i<list.size();i++){
            double lat = list.get(i).getLat();
            double lng = list.get(i).getLng();
            System.out.println(" ooo "+ lat + " "+lng);
            if(lat!=0 && lng!=0)
                result = result + lat + "," + lng + "|";
        }
        while(result.endsWith("|"))
        {
            result = result.substring(0, result.length()-1);
        }
        return result;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void setLocSelected(int index)
    {
        locSelected = list.get(index);
    }

    public MMapLoc getLocSelected()
    {
        return locSelected;
    }
    @JavascriptInterface
    public void showName(final int index)
    {

        handler.post(new Runnable() {
            @Override
            public void run() {
                // This gets executed on the UI thread so it can safely modify Views
                EditMapActivity.setEditText(list.get(index).getName());
            }

        });
    }
    @JavascriptInterface
    public void changeLoc(int index, String latlng){
        Location loc = getLocFromString(latlng);

        MMapLoc mloc = list.get(index);
        mloc.setLat(loc.getLatitude());
        mloc.setLng(loc.getLongitude());

        EditMapActivity.madeChanges = true;

    }
    public List<MMapLoc> getList()
    {
        return list;
    }
    public List<MMapLoc> getListToDelete()
    {
        return toDelete;
    }
    @JavascriptInterface
    public void addToList(String latlng)
    {
        Location loc = getLocFromString(latlng);

        MMapLoc mloc = new MMapLoc();
        mloc.setLat(loc.getLatitude());
        mloc.setLng(loc.getLongitude());
        mloc.setMap_id(map.getId());

        list.add(mloc);

        EditMapActivity.madeChanges = true;
    }
    public Location getLocFromString(String latlng)
    {
        Location loc = new Location("topo");

        int start = latlng.indexOf("(");
        int end = latlng.indexOf(",");
        String lat = latlng.substring(start+1, end);
        int end1 = latlng.indexOf(")");
        String lng = latlng.substring(end + 1, end1);

        loc.setLatitude(Double.valueOf(lat));
        loc.setLongitude(Double.valueOf(lng));

        return loc;
    }
    @JavascriptInterface
    public void delete(int i)
    {
        MMapLoc loc = list.get(i);
        if(loc!=null)
            toDelete.add(loc);

        EditMapActivity.madeChanges = true;
    }
    @JavascriptInterface
    public int getListSize()
    {
        return list.size();
    }
}