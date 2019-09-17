package buildnlive.com.buildem.elements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServiceItem {

    @Expose
    @SerializedName("status")
    private String status;
    @Expose
    @SerializedName("service_date")
    private String serviceDate;
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
    @SerializedName("service_id")
    private String serviceId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
