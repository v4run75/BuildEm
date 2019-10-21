package buildnlive.com.buildem.elements;

import android.os.Parcel;
import android.os.Parcelable;

public class Packet implements Parcelable {
    String name;
    String extra;
    int type;

    public Packet(String name, String extra, int type) {
        this.name = name;
        this.extra = extra;
        this.type = type;
    }

    public Packet(String name, String extra) {
        this.name = name;
        this.extra = extra;
    }

    public Packet() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.extra);
        dest.writeInt(this.type);
    }

    protected Packet(Parcel in) {
        this.name = in.readString();
        this.extra = in.readString();
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<Packet> CREATOR = new Parcelable.Creator<Packet>() {
        @Override
        public Packet createFromParcel(Parcel source) {
            return new Packet(source);
        }

        @Override
        public Packet[] newArray(int size) {
            return new Packet[size];
        }
    };
}
