package buildnlive.com.buildem.elements;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WorkListItem implements Parcelable {

    @Expose
    @SerializedName("work_units")
    private String units;
    @Expose
    @SerializedName("work_name")
    private String workName;
    @Expose
    @SerializedName("work_id")
    private String workId;

    private String qty;

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public WorkListItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.units);
        dest.writeString(this.workName);
        dest.writeString(this.workId);
        dest.writeString(this.qty);
    }

    protected WorkListItem(Parcel in) {
        this.units = in.readString();
        this.workName = in.readString();
        this.workId = in.readString();
        this.qty = in.readString();
    }

    public static final Creator<WorkListItem> CREATOR = new Creator<WorkListItem>() {
        @Override
        public WorkListItem createFromParcel(Parcel source) {
            return new WorkListItem(source);
        }

        @Override
        public WorkListItem[] newArray(int size) {
            return new WorkListItem[size];
        }
    };
}
