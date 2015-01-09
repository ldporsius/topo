package porsius.nl.topo.data;

/**
 * Created by linda on 29/10/14.
 */
public class MMapLoc {

    private int id;
    private int map_id;
    private String name;
    private double lat;
    private double lng;
    private boolean answered;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id=id;
    }
    public void setMap_id(int id)
    {
        this.map_id=id;
    }

    public int getMap_id()
    {
        return map_id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    public void setLat(double lat)
    {
        this.lat=lat;
    }

    public double getLat()
    {
        return lat;
    }

    public void setLng(double lng)
    {
        this.lng=lng;
    }

    public double getLng()
    {
        return lng;
    }

    public boolean getAnswered()
    {
        return answered;
    }

    public void setAnswered(boolean b)
    {
        this.answered=b;
    }
}
