package buildnlive.com.buildem.elements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CustomerData {

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

    public class Details {
        @Expose
        @SerializedName("qty")
        private String qty;
        @Expose
        @SerializedName("model_name")
        private String modelName;

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }
    }

    public class CustomerDetails {
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
    }
}