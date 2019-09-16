package buildnlive.com.buildem.elements;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AMCItemDetails implements Parcelable {

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

        public Details() {
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

        protected Details(Parcel in) {
            this.units = in.readString();
            this.qty = in.readString();
            this.workName = in.readString();
            this.workId = in.readString();
        }

        public static final Creator<Details> CREATOR = new Creator<Details>() {
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
        @SerializedName("address")
        private String address;
        @Expose
        @SerializedName("contact_no")
        private String mobileNo;
        @Expose
        @SerializedName("customer_name")
        private String customerName;
        @Expose
        @SerializedName("customer_id")
        private String customerId;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.status);
            dest.writeString(this.address);
            dest.writeString(this.mobileNo);
            dest.writeString(this.customerName);
            dest.writeString(this.customerId);
        }

        public CustomerDetails() {
        }

        protected CustomerDetails(Parcel in) {
            this.status = in.readString();
            this.address = in.readString();
            this.mobileNo = in.readString();
            this.customerName = in.readString();
            this.customerId = in.readString();
        }

        public static final Parcelable.Creator<CustomerDetails> CREATOR = new Parcelable.Creator<CustomerDetails>() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.details);
        dest.writeParcelable(this.customerDetails, flags);
    }

    public AMCItemDetails() {
    }

    protected AMCItemDetails(Parcel in) {
        this.details = in.createTypedArrayList(Details.CREATOR);
        this.customerDetails = in.readParcelable(CustomerDetails.class.getClassLoader());
    }

    public static final Parcelable.Creator<AMCItemDetails> CREATOR = new Parcelable.Creator<AMCItemDetails>() {
        @Override
        public AMCItemDetails createFromParcel(Parcel source) {
            return new AMCItemDetails(source);
        }

        @Override
        public AMCItemDetails[] newArray(int size) {
            return new AMCItemDetails[size];
        }
    };
}
