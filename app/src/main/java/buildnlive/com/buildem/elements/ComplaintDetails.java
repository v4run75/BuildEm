package buildnlive.com.buildem.elements;

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

    public static class Details {
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
    }

    public class CustomerDetails {
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
    }
}
