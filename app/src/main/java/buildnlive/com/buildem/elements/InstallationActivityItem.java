package buildnlive.com.buildem.elements;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class InstallationActivityItem implements Parcelable {
    private String id;
    private String name;
    private String type;
    private String quantity;
    private String units;

    public InstallationActivityItem() {
    }

    public InstallationActivityItem(String id, String name, String type, String quantity, String units) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.units = units;

    }

    public InstallationActivityItem parseFromJSON(JSONObject obj) throws JSONException {
        setId(obj.getString("id"));
        setName(obj.getString("name"));
        setType(obj.getString("type"));
        setQuantity(obj.getString("qty"));
        setUnits(obj.getString("units"));
        return this;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getUnits() {
        return units;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.type);
    }

    protected InstallationActivityItem(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.type = in.readString();
    }

    public static final Creator<InstallationActivityItem> CREATOR = new Creator<InstallationActivityItem>() {
        @Override
        public InstallationActivityItem createFromParcel(Parcel source) {
            return new InstallationActivityItem(source);
        }

        @Override
        public InstallationActivityItem[] newArray(int size) {
            return new InstallationActivityItem[size];
        }
    };
}
