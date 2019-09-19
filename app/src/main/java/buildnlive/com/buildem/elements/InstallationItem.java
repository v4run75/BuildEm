package buildnlive.com.buildem.elements;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class InstallationItem implements Parcelable {
    private String service_id;
    private String name;
    private String address;
    private String time;
    private String date;
    private String mobileNo;
    private String email;
    private String comment;

    public InstallationItem() {
    }

    public InstallationItem(String service_id, String name, String address, String time, String date, String mobileNo, String email, String comment) {
        this.service_id = service_id;
        this.name = name;
        this.time = time;
        this.address = address;
        this.date = date;
        this.mobileNo = mobileNo;
        this.email = email;
        this.comment = comment;
    }

    public InstallationItem parseFromJSON(JSONObject obj) throws JSONException {
        setServiceId(obj.getString("service_id"));
        setName(obj.getString("name"));
        setTime(obj.getString("service_time"));
        setAddress(obj.getString("address"));
        setDate(obj.getString("service_date"));
        setMobileNo(obj.getString("contact_no"));
        setEmail(obj.getString("email_id"));
        setComment(obj.getString("comments"));
        return this;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setServiceId(String service_id) {
        this.service_id = service_id;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getName() {
        return name;
    }

    public String getServiceId() {
        return service_id;
    }

    public String getTime() {
        return time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.service_id);
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeString(this.time);
        dest.writeString(this.date);
        dest.writeString(this.mobileNo);
        dest.writeString(this.email);
    }

    protected InstallationItem(Parcel in) {
        this.service_id = in.readString();
        this.name = in.readString();
        this.address = in.readString();
        this.time = in.readString();
        this.date = in.readString();
        this.mobileNo = in.readString();
        this.email = in.readString();
    }

    public static final Parcelable.Creator<InstallationItem> CREATOR = new Parcelable.Creator<InstallationItem>() {
        @Override
        public InstallationItem createFromParcel(Parcel source) {
            return new InstallationItem(source);
        }

        @Override
        public InstallationItem[] newArray(int size) {
            return new InstallationItem[size];
        }
    };
}
