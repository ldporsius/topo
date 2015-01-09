package porsius.nl.topo.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by linda on 29/10/14.
 */
public class MMap implements Parcelable {

    private int id;
    private String name;
    private String author;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAuthor(String author)
    {
        this.author=author;
    }

    public String getAuthor()
    {
        return author;
    }
    public MMap()
    {

    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    public static final Parcelable.Creator<MMap> CREATOR = new Parcelable.Creator<MMap>() {
        public MMap createFromParcel(Parcel in) {
            return new MMap(in);
        }

        @Override
        public MMap[] newArray(int size) {
            return new MMap[size];
        }

    };

    private MMap(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

}