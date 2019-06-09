package buildnlive.com.buildem.elements;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class SearchModel  {
    private String item_model_id;
    private String model_no;
    private String model_name;
    private String brand_name;
    private String available_qty;
    private String mrp;
    private String display_price;

    public SearchModel() {
    }

    /* [{"item_model_id":"1","model_no":"MSZ-LN18VF","model_name":"BEE 5 Star Super Premium Heat Pump DC Invertor R32",
            "brand_name":"Mitsubishi Electric","available_qty":0,"mrp":"10000","display_price":"9000"}]
    */

    public SearchModel(String item_model_id, String model_no, String model_name, String brand_name,String available_qty,String mrp,String display_price) {
        this.item_model_id = item_model_id;
        this.model_no = model_no;
        this.brand_name=brand_name;
        this.model_name=model_name;
        this.available_qty=available_qty;
        this.mrp=mrp;
        this.display_price=display_price;
    }

    public SearchModel parseFromJSON(JSONObject obj) throws JSONException {
        setItem_model_id(obj.getString("item_model_id"));
        setModel_no(obj.getString("model_no"));
        setBrand_name(obj.getString("brand_name"));
        setModel_name(obj.getString("model_name"));
        setAvailable_qty(obj.getString("available_qty"));
        setMrp(obj.getString("mrp"));
        setDisplay_price(obj.getString("display_price"));
        return this;
    }

    private void setAvailable_qty(String available_qty) {
        this.available_qty = available_qty;
    }

    private void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    private void setItem_model_id(String item_model_id) {
        this.item_model_id = item_model_id;
    }

    private void setDisplay_price(String display_price) {
        this.display_price = display_price;
    }

    private void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    private void setModel_no(String model_no) {
        this.model_no = model_no;
    }

    private void setMrp(String mrp) {
        this.mrp = mrp;
    }


    public String getBrand_name() {
        return brand_name;
    }

    public String getItem_model_id() {
        return item_model_id;
    }

    public String getAvailable_qty() {
        return available_qty;
    }

    public String getModel_name() {
        return model_name;
    }

    public String getDisplay_price() {
        return display_price;
    }

    public String getModel_no() {
        return model_no;
    }

    public String getMrp() {
        return mrp;
    }

}
