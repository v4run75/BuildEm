package buildnlive.com.buildem.elements;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ComplaintDetails {

    @Expose
    @SerializedName("details")
    private ArrayList<Details> details;
    @Expose
    @SerializedName("customer_details")
    private CustomerDetails customerDetails;

    public ArrayList<Details> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<Details> details) {
        this.details = details;
    }

    public CustomerDetails getCustomerDetails() {
        return customerDetails;
    }

    public void setCustomerDetails(CustomerDetails customerDetails) {
        this.customerDetails = customerDetails;
    }

    public static class Details implements Parcelable {

        @Expose
        @SerializedName("units")
        private String units;
        @Expose
        @SerializedName("qty")
        private String qty;
        @Expose
        @SerializedName("work_name")
        private String workName;
        @Expose
        @SerializedName("work_id")
        private String workId;

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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.units);
            dest.writeString(this.qty);
            dest.writeString(this.workName);
            dest.writeString(this.workId);
        }

        public Details() {
        }

        protected Details(Parcel in) {
            this.units = in.readString();
            this.qty = in.readString();
            this.workName = in.readString();
            this.workId = in.readString();
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

    public static class CustomerDetails implements Parcelable {
        @Expose
        @SerializedName("status")
        private String status;
        @Expose
        @SerializedName("customer_id")
        private String customerId;
        @Expose
        @SerializedName("customer_name")
        private String customerName;
        @Expose
        @SerializedName("contact_no")
        private String mobileNo;
        @Expose
        @SerializedName("address")
        private String address;
        @Expose
        @SerializedName("comment")
        private String comment;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public CustomerDetails() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.status);
            dest.writeString(this.customerId);
            dest.writeString(this.customerName);
            dest.writeString(this.mobileNo);
            dest.writeString(this.address);
            dest.writeString(this.comment);
        }

        protected CustomerDetails(Parcel in) {
            this.status = in.readString();
            this.customerId = in.readString();
            this.customerName = in.readString();
            this.mobileNo = in.readString();
            this.address = in.readString();
            this.comment = in.readString();
        }

        public static final Creator<CustomerDetails> CREATOR = new Creator<CustomerDetails>() {
            @Override
            public CustomerDetails createFromParcel(Parcel source) {
                return new CustomerDetails(source);
            }

            @Override
            public CustomerDetails[] newArray(int size) {
                return new CustomerDetails[size];
            }
        };
    }
}
