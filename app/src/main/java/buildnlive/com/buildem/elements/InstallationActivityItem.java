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

/*
* package buildnlive.com.buildem.elements;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public  class InstallationActivityItem implements Parcelable {
    @Expose
    @SerializedName("show_on_job_button")
    private String showOnJobButton;
    @Expose
    @SerializedName("details")
    private ArrayList<Details> details;

    public String getShowOnJobButton() {
        return showOnJobButton;
    }

    public void setShowOnJobButton(String showOnJobButton) {
        this.showOnJobButton = showOnJobButton;
    }

    public ArrayList<Details> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<Details> details) {
        this.details = details;
    }

    public static class Details implements Parcelable {
        @Expose
        @SerializedName("id")
        private String id;
        @Expose
        @SerializedName("units")
        private String units;
        @Expose
        @SerializedName("qty")
        private String qty;
        @Expose
        @SerializedName("type")
        private String type;
        @Expose
        @SerializedName("name")
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUnits() {
            return units;
        }

        public void setUnits(String units) {
            this.units = units;
        }

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.units);
            dest.writeString(this.qty);
            dest.writeString(this.type);
            dest.writeString(this.name);
        }

        public Details() {
        }

        protected Details(Parcel in) {
            this.id = in.readString();
            this.units = in.readString();
            this.qty = in.readString();
            this.type = in.readString();
            this.name = in.readString();
        }

        public static final Parcelable.Creator<Details> CREATOR = new Parcelable.Creator<Details>() {
            @Override
            public Details createFromParcel(Parcel source) {
                return new Details(source);
            }

            @Override
            public Details[] newArray(int size) {
                return new Details[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.showOnJobButton);
        dest.writeTypedList(this.details);
    }

    public InstallationActivityItem() {
    }

    protected InstallationActivityItem(Parcel in) {
        this.showOnJobButton = in.readString();
        this.details = in.createTypedArrayList(Details.CREATOR);
    }

    public static final Parcelable.Creator<InstallationActivityItem> CREATOR = new Parcelable.Creator<InstallationActivityItem>() {
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
*/
