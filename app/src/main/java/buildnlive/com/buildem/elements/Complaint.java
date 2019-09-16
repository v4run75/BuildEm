package buildnlive.com.buildem.elements;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Complaint implements Parcelable {

    @Expose
    @SerializedName("status")
    private String status;
    @Expose
    @SerializedName("complaint_date")
    private String complaintDate;
    @Expose
    @SerializedName("customer_address")
    private String customerAddress;
    @Expose
    @SerializedName("customer_contact")
    private String customerContact;
    @Expose
    @SerializedName("customer_name")
    private String customerName;
    @Expose
    @SerializedName("complaint_id")
    private String complaintId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComplaintDate() {
        return complaintDate;
    }

    public void setComplaintDate(String complaintDate) {
        this.complaintDate = complaintDate;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeString(this.complaintDate);
        dest.writeString(this.customerAddress);
        dest.writeString(this.customerContact);
        dest.writeString(this.customerName);
        dest.writeString(this.complaintId);
    }

    public Complaint() {
    }

    protected Complaint(Parcel in) {
        this.status = in.readString();
        this.complaintDate = in.readString();
        this.customerAddress = in.readString();
        this.customerContact = in.readString();
        this.customerName = in.readString();
        this.complaintId = in.readString();
    }

    public static final Parcelable.Creator<Complaint> CREATOR = new Parcelable.Creator<Complaint>() {
        @Override
        public Complaint createFromParcel(Parcel source) {
            return new Complaint(source);
        }

        @Override
        public Complaint[] newArray(int size) {
            return new Complaint[size];
        }
    };
}
