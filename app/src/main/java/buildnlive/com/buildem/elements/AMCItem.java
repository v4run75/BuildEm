package buildnlive.com.buildem.elements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AMCItem {

    @Expose
    @SerializedName("status")
    private String status;
    @Expose
    @SerializedName("amc_date")
    private String amcDate;
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
    @SerializedName("amc_id")
    private String amcId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmcDate() {
        return amcDate;
    }

    public void setAmcDate(String amcDate) {
        this.amcDate = amcDate;
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

    public String getAmcId() {
        return amcId;
    }

    public void setAmcId(String amcId) {
        this.amcId = amcId;
    }
}
