package buildnlive.com.buildem.utils;

public class Config {

    public static final int REQ_TIME_OUT = 45000; //45 seconds
    public static final String REQ_GET_LABOUR = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/SendLabourList&user_id=[0]&project_id=[1]";
    public static final String REQ_GET_INVENTORY = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/SendInventory&user_id=[0]&project_id=[1]";
    public static final String REQ_GET_ITEM_INVENTORY = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/SendItemListing&user_id=[0]&project_id=[1]";
    public static final String REQ_SYNC_PROJECT = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/SyncProject&user_id=[0]&project_id=[1]";
    public static final String REQ_POST_INVENTORY_UPDATES = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/updateInventory";
    public static final String REQ_POST_CHECK_IN = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/UpdateAttendenceCheckIn";
    public static final String REQ_POST_CHECK_OUT = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/UpdateAttendenceCheckOut";
    public static final String REQ_GET_REQUEST_TYPE = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/GetRequestTypeList&type=[0]&user_id=[1]";
    public static final String REQ_GET_USER_ATTENDANCE = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/SendLabourAttendence&user_id=[0]&labour_id=[1]&project_id=[2]";
    public static final String SEND_REQUEST_ITEM = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/GetRequestForm";
    public static final String GET_REQUEST_LIST = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/SendRequestList&project_id=[0]&user_id=[1]";
    public static final String GET_PROJECT_PLANS = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/SendProjectPlans&project_id=[0]&user_id=[1]";
    public static final String REQ_LOGIN = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/UserLogin";
    public static final String SEND_ISSUED_ITEM = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/GetIssueItem";
    public static final String REQ_DAILY_WORK = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/WorkActivityDaily&user_id=[0]&project_id=[1]&type=Work";
    public static final String REQ_DAILY_WORK_ACTIVITY = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/ActivityByWork&user_id=[0]&project_work_list_id=[1]";
    public static final String INVENTORY_ITEM_REQUEST = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/InventoryItemRequest";
    public static final String REQ_DAILY_WORK_ACTIVITY_UPDATE = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/GetWorkActivityUpdate";
    public static final String REQ_PLAN_MARKUP = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/ProjectPlansMarkup";
    public static final String REQ_PURCHASE_ORDER = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/SitePurchaseOrder&user_id=[0]&project_id=[1]";
    public static final String REQ_PURCHASE_ORDER_LISTING = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/PurchaseOrderList&user_id=[0]&purchase_order_id=[1]";
    public static final String REQ_PURCHASE_ORDER_UPDATE = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/GetPurchaseOrderList";
    public static final String REQ_SENT_CATEGORIES= "https://buildnlive.com/app/mobileapp_em/index.php?r=site/SentCategories&user_id=[0]";
    public static final String GET_SITE_LIST="https://buildnlive.com/app/mobileapp_em/index.php?r=site/SendItemList&cat_id=[0]&user_id=[1]&project_id=[2]";
    public static final String SEND_REQUEST_SITE_LIST="https://buildnlive.com/app/mobileapp_em/index.php?r=site/SiteRequestItem&user_id=[0]";
    public static final String SEND_REQUEST_STATUS="https://buildnlive.com/app/mobileapp_em/index.php?r=site/SendRequestStatus&user_id=[0] ";
    public static final String SEND_LOCAL_PURCHASE="https://buildnlive.com/app/mobileapp_em/index.php?r=site/LocalPurchase";
    public static final String SEND_SITE_PAYMENTS="https://buildnlive.com/app/mobileapp_em/index.php?r=site/SitePayments";
    public static final String SEND_NOTIFICATIONS="https://buildnlive.com/app/mobileapp_em/index.php?r=site/SendNotifications&user_id=[0]&project_id=[1]";
    public static final String GET_NOTIFICATIONS="https://buildnlive.com/app/mobileapp_em/index.php?r=site/GetNotifications";
    public static final String GET_NOTIFICATIONS_COUNT="https://buildnlive.com/app/mobileapp_em/index.php?r=site/GetNotificationCount";
    public static final String GET_ISSUE_STATUS="https://buildnlive.com/app/mobileapp_em/index.php?r=site/SendIssueStatus&user_id=[0]&project_id=[1]";
    public static final String GET_LABOUR_VENDOR_LIST="https://buildnlive.com/app/mobileapp_em/index.php?r=site/GetContractorList&user_id=[0]&project_id=[1]";
    public static final String GET_LABOUR_LIST="https://buildnlive.com/app/mobileapp_em/index.php?r=site/GetLabourCount&user_id=[0]&project_id=[1]&vendor_id=[2]";
    public static final String SEND_LABOUR_LIST="https://buildnlive.com/app/mobileapp_em/index.php?r=site/SaveLabourTransfer";
    public static final String VIEW_LABOUR_LIST="https://buildnlive.com/app/mobileapp_em/index.php?r=site/SendTransferDetails&user_id=[0]&project_id=[1]";
    public static final String SEND_COMMENTS="https://buildnlive.com/app/mobileapp_em/index.php?r=site/UpdateLabourTransfer";
    public static final String FORGOT_PASSWORD="https://buildnlive.com/app/mobileapp_em/index.php?r=site/ForgotPassword";
    public static final String RESET_PASSWORD="https://buildnlive.com/app/mobileapp_em/index.php?r=site/ChangePassword";
    public static final String WORK_FILTERS="https://buildnlive.com/app/mobileapp_em/index.php?r=site/WorkActivityFilters";
    public static final String PREF_NAME = "OGIL";
    public static final String GET_CONTRACTORS ="https://buildnlive.com/app/mobileapp_em/index.php?r=site/SendVendors&user_id=[0]&project_id=[1]" ;
    public static final String VIEW_LABOUR_REPORT = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/ViewLabourReport&user_id=[0]&project_id=[1]";
    public static final String GET_LABOUR_PROGRESS = "https://buildnlive.com/app/mobileapp_em/index.php?r=site/GetLabourProgress";
    public static final String VIEW_DETAILED_LABOUR_REPORT="https://buildnlive.com/app/mobileapp_em/index.php?r=site/ViewLabourReportBreakup&user_id=[0]&daily_labour_report_id=[1]";
    public static final String LABOUR_TYPE_LIST ="https://buildnlive.com/app/mobileapp_em/index.php?r=site/GetLabourTypes&user_id=[0]";
    public static final String GET_PLANNED_LIST ="https://buildnlive.com/app/mobileapp_em/index.php?r=site/GetPlannedWork&user_id=[0]&project_id=[1]";
    public static final String UPDATE_PLANNED_WORK ="https://buildnlive.com/app/mobileapp_em/index.php?r=site/GetPlannedWorkUpdate";
    public static final String SEND_ASSETS ="https://buildnlive.com/app/mobileapp_em/index.php?r=site/SendAssets&user_id=[0]&project_id=[1]";
    public static final String SEND_ISSUE_VENDORS ="https://buildnlive.com/app/mobileapp_em/index.php?r=site/SendIssueVendors&user_id=[0]&project_id=[1]";
    public static final String RETURN_ISSUED_ITEM ="https://buildnlive.com/app/mobileapp_em/index.php?r=site/ReturnIssuedItem&issue_id=[0]&item_record_id=[1]&return_qty=[2]&return_type=[3]";
    public static final String INVENTORY_SEARCH ="https://buildnlive.com/app/mobileapp_em/index.php?r=site/searchItem&project=[1]&user_id=[0]&text=[2]";
    public static final String INVENTORY_ITEM_SEARCH ="https://buildnlive.com/app/mobileapp_em/index.php?r=site/searchItemStock&project=[1]&user_id=[0]&text=[2]";
    public static final String LOGOOUT ="https://buildnlive.com/app/mobileapp_em/index.php?r=site/UserLogout&user_id=[0]";
    public static final String CREATE_LABOUR ="https://buildnlive.com/app/mobileapp_em/index.php?r=Site/CreateLabour";
    public static final String CREATE_ACTIVITY ="https://buildnlive.com/app/mobileapp_em/index.php?r=Site/CreateActivity";
    public static final String GET_MACHINE_LIST ="https://buildnlive.com/app/mobileapp_em/index.php?r=Site/SendMachineList&user_id=[0]&project_id=[1]";
    public static final String VIEW_MACHINE_LIST ="https://buildnlive.com/app/mobileapp_em/index.php?r=Site/ViewJobSheet&user_id=[0]&project_id=[1]";
    public static final String SEND_MACHINE_LIST ="https://buildnlive.com/app/mobileapp_em/index.php?r=Site/AssetJobSheet";
    public static final String UPDATE_FCM_KEY ="https://buildnlive.com/app/mobileapp_em/index.php?r=Site/UpdateFCMKey";
}
