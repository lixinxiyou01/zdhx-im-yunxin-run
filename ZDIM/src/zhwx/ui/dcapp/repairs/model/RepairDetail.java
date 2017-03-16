package zhwx.ui.dcapp.repairs.model;

import java.util.List;

/**
 * Created by Android on 2017/3/15.
 */

public class RepairDetail {

    /**
     * repairInfo : {"repairStatus":"延迟维修","repairTime":null,"malfunctionKind":"自然损坏","malfunctionReason":"委托方如果结婚","goodsSum":"","workerName":"技术支持","repairImageList":[]}
     * requestInfo : {"phoneNum":"324","malfunctionPlace":"教学楼","malfunction":"多媒体设备","imageList":["/component/attachment!showPic.action?checkUser=false&period=year&downloadToken=2017031416182726594997071453345268fa0c3ae7d550677fd4fab2dbc0598d&configCode=re_reportPic"],"requestTime":"2017-03-14 16:19:59","deviceName":"多媒体设备-[信息化设备]","malfunctionDescribe":"范德萨","requestUserName":"技术支持"}
     */

    private RepairInfoBean repairInfo;
    private RequestInfoBean requestInfo;

    public RepairInfoBean getRepairInfo() {
        return repairInfo;
    }

    public void setRepairInfo(RepairInfoBean repairInfo) {
        this.repairInfo = repairInfo;
    }

    public RequestInfoBean getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(RequestInfoBean requestInfo) {
        this.requestInfo = requestInfo;
    }

    public static class RepairInfoBean {
        /**
         * repairStatus : 延迟维修
         * repairTime : null
         * malfunctionKind : 自然损坏
         * malfunctionReason : 委托方如果结婚
         * goodsSum :
         * workerName : 技术支持
         * repairImageList : []
         */

        private String repairStatus;
        private String repairTime;
        private String malfunctionKind;
        private String malfunctionReason;
        private String goodsSum;
        private String workerName;
        private List<String> repairImageList;

        public String getRepairStatus() {
            return repairStatus;
        }

        public void setRepairStatus(String repairStatus) {
            this.repairStatus = repairStatus;
        }

        public String getRepairTime() {
            return repairTime;
        }

        public void setRepairTime(String repairTime) {
            this.repairTime = repairTime;
        }

        public String getMalfunctionKind() {
            return malfunctionKind;
        }

        public void setMalfunctionKind(String malfunctionKind) {
            this.malfunctionKind = malfunctionKind;
        }

        public String getMalfunctionReason() {
            return malfunctionReason;
        }

        public void setMalfunctionReason(String malfunctionReason) {
            this.malfunctionReason = malfunctionReason;
        }

        public String getGoodsSum() {
            return goodsSum;
        }

        public void setGoodsSum(String goodsSum) {
            this.goodsSum = goodsSum;
        }

        public String getWorkerName() {
            return workerName;
        }

        public void setWorkerName(String workerName) {
            this.workerName = workerName;
        }

        public List<String> getRepairImageList() {
            return repairImageList;
        }

        public void setRepairImageList(List<String> repairImageList) {
            this.repairImageList = repairImageList;
        }
    }

    public static class RequestInfoBean {
        /**
         * phoneNum : 324
         * malfunctionPlace : 教学楼
         * malfunction : 多媒体设备
         * imageList : ["/component/attachment!showPic.action?checkUser=false&period=year&downloadToken=2017031416182726594997071453345268fa0c3ae7d550677fd4fab2dbc0598d&configCode=re_reportPic"]
         * requestTime : 2017-03-14 16:19:59
         * deviceName : 多媒体设备-[信息化设备]
         * malfunctionDescribe : 范德萨
         * requestUserName : 技术支持
         */

        private String phoneNum;
        private String malfunctionPlace;
        private String malfunction;
        private String requestTime;
        private String deviceName;
        private String faultDescription;
        private String requestUserName;
        private String RepairHistory;
        private List<String> imageList;

        public String getPhoneNum() {
            return phoneNum;
        }

        public void setPhoneNum(String phoneNum) {
            this.phoneNum = phoneNum;
        }

        public String getRepairHistory() {
            return RepairHistory;
        }

        public void setRepairHistory(String repairHistory) {
            RepairHistory = repairHistory;
        }

        public String getMalfunctionPlace() {
            return malfunctionPlace;
        }

        public void setMalfunctionPlace(String malfunctionPlace) {
            this.malfunctionPlace = malfunctionPlace;
        }

        public String getMalfunction() {
            return malfunction;
        }

        public void setMalfunction(String malfunction) {
            this.malfunction = malfunction;
        }

        public String getRequestTime() {
            return requestTime;
        }

        public void setRequestTime(String requestTime) {
            this.requestTime = requestTime;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getMalfunctionDescribe() {
            return faultDescription;
        }

        public void setMalfunctionDescribe(String malfunctionDescribe) {
            this.faultDescription = malfunctionDescribe;
        }

        public String getRequestUserName() {
            return requestUserName;
        }

        public void setRequestUserName(String requestUserName) {
            this.requestUserName = requestUserName;
        }

        public List<String> getImageList() {
            return imageList;
        }

        public void setImageList(List<String> imageList) {
            this.imageList = imageList;
        }
    }
}
