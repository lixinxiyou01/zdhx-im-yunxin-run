package zhwx.ui.dcapp.storeroom.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Android on 2017/1/14.
 */

public class MyApply implements Serializable {


    /**
     * gridModel : [{"deptAuditOpinion":null,"CHECKSTATUS_ZWPASS":"4","provideStatus":"0","CHECKSTATUS_ZWNOPASS":"3","reason":"23","provideStatusValue":"未发放","CHECKSTATUS_DEPTPASS":"2","lu":null,"lt":null,"date":"2017-01-15","kind":"bmsl","id":"20170115115153699561889254342631","outWarehouseDate":null,"checkStatusValue":"总务未审核","checkStatus":"2","PROVIDESTATUS_ABLEING":"2","deptDate":"2017-01-15","fu":"20151116112937165940444498014398","ft":"2017-01-15 11:51:53","PROVIDESTATUS_UNABLE":"0","note":"2543541313","PROVIDESTATUS_ENABLE":"1","code":"20170115002","deptCheckStatusValue":"部门审核通过","CHECKSTATUS_DEPTNOPASS":"1","zwDate":null,"zwCheckStatusValue":"总务未审核","CHECKSTATUS_UNAUDIT":"0","kindValue":"部门申领","zwAuditopinion":null},{"deptAuditOpinion":null,"CHECKSTATUS_ZWPASS":"4","provideStatus":"0","CHECKSTATUS_ZWNOPASS":"3","reason":"4354354","provideStatusValue":"未发放","CHECKSTATUS_DEPTPASS":"2","lu":null,"lt":null,"date":"2017-01-15","kind":"grsl","id":"20170115115040515678109794960994","outWarehouseDate":null,"checkStatusValue":"总务未审核","checkStatus":"2","PROVIDESTATUS_ABLEING":"2","deptDate":"2017-01-15","fu":"20151116112937165940444498014398","ft":"2017-01-15 11:50:40","PROVIDESTATUS_UNABLE":"0","note":"4543543543","PROVIDESTATUS_ENABLE":"1","code":"20170115001","deptCheckStatusValue":"部门审核通过","CHECKSTATUS_DEPTNOPASS":"1","zwDate":null,"zwCheckStatusValue":"总务未审核","CHECKSTATUS_UNAUDIT":"0","kindValue":"个人申领","zwAuditopinion":null}]
     * page : 1
     * record : 2
     * rows : 10
     * total : 1
     */

    private int page;
    private int record;
    private int rows;
    private int total;
    private List<GridModelBean> gridModel;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRecord() {
        return record;
    }

    public void setRecord(int record) {
        this.record = record;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<GridModelBean> getGridModel() {
        return gridModel;
    }

    public void setGridModel(List<GridModelBean> gridModel) {
        this.gridModel = gridModel;
    }

    public static class GridModelBean {
        /**
         * deptAuditOpinion : null
         * CHECKSTATUS_ZWPASS : 4
         * provideStatus : 0
         * CHECKSTATUS_ZWNOPASS : 3
         * reason : 23
         * provideStatusValue : 未发放
         * CHECKSTATUS_DEPTPASS : 2
         * lu : null
         * lt : null
         * date : 2017-01-15
         * kind : bmsl
         * id : 20170115115153699561889254342631
         * outWarehouseDate : null
         * checkStatusValue : 总务未审核
         * checkStatus : 2
         * PROVIDESTATUS_ABLEING : 2
         * deptDate : 2017-01-15
         * fu : 20151116112937165940444498014398
         * ft : 2017-01-15 11:51:53
         * PROVIDESTATUS_UNABLE : 0
         * note : 2543541313
         * PROVIDESTATUS_ENABLE : 1
         * code : 20170115002
         * deptCheckStatusValue : 部门审核通过
         * CHECKSTATUS_DEPTNOPASS : 1
         * zwDate : null
         * zwCheckStatusValue : 总务未审核
         * CHECKSTATUS_UNAUDIT : 0
         * kindValue : 部门申领
         * zwAuditopinion : null
         */

        private Object deptAuditOpinion;
        private String CHECKSTATUS_ZWPASS;
        private String provideStatus;
        private String CHECKSTATUS_ZWNOPASS;
        private String reason;
        private String provideStatusValue;
        private String CHECKSTATUS_DEPTPASS;
        private String date;
        private String kind;
        private String id;
        private Object outWarehouseDate;
        private String checkStatusValue;
        private String checkStatus;
        private String PROVIDESTATUS_ABLEING;
        private String deptDate;
        private String note;
        private String code;
        private String deptCheckStatusValue;
        private String CHECKSTATUS_DEPTNOPASS;
        private Object zwDate;
        private String zwCheckStatusValue;
        private String CHECKSTATUS_UNAUDIT;
        private String kindValue;
        private Object zwAuditopinion;
        private String departmentName;
        private String userName;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getDepartmentName() {
			return departmentName;
		}

		public void setDepartmentName(String departmentName) {
			this.departmentName = departmentName;
		}

		public Object getDeptAuditOpinion() {
            return deptAuditOpinion;
        }

        public void setDeptAuditOpinion(Object deptAuditOpinion) {
            this.deptAuditOpinion = deptAuditOpinion;
        }

        public String getCHECKSTATUS_ZWPASS() {
            return CHECKSTATUS_ZWPASS;
        }

        public void setCHECKSTATUS_ZWPASS(String CHECKSTATUS_ZWPASS) {
            this.CHECKSTATUS_ZWPASS = CHECKSTATUS_ZWPASS;
        }

        public String getProvideStatus() {
            return provideStatus;
        }

        public void setProvideStatus(String provideStatus) {
            this.provideStatus = provideStatus;
        }

        public String getCHECKSTATUS_ZWNOPASS() {
            return CHECKSTATUS_ZWNOPASS;
        }

        public void setCHECKSTATUS_ZWNOPASS(String CHECKSTATUS_ZWNOPASS) {
            this.CHECKSTATUS_ZWNOPASS = CHECKSTATUS_ZWNOPASS;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getProvideStatusValue() {
            return provideStatusValue;
        }

        public void setProvideStatusValue(String provideStatusValue) {
            this.provideStatusValue = provideStatusValue;
        }

        public String getCHECKSTATUS_DEPTPASS() {
            return CHECKSTATUS_DEPTPASS;
        }

        public void setCHECKSTATUS_DEPTPASS(String CHECKSTATUS_DEPTPASS) {
            this.CHECKSTATUS_DEPTPASS = CHECKSTATUS_DEPTPASS;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Object getOutWarehouseDate() {
            return outWarehouseDate;
        }

        public void setOutWarehouseDate(Object outWarehouseDate) {
            this.outWarehouseDate = outWarehouseDate;
        }

        public String getCheckStatusValue() {
            return checkStatusValue;
        }

        public void setCheckStatusValue(String checkStatusValue) {
            this.checkStatusValue = checkStatusValue;
        }

        public String getCheckStatus() {
            return checkStatus;
        }

        public void setCheckStatus(String checkStatus) {
            this.checkStatus = checkStatus;
        }

        public String getPROVIDESTATUS_ABLEING() {
            return PROVIDESTATUS_ABLEING;
        }

        public void setPROVIDESTATUS_ABLEING(String PROVIDESTATUS_ABLEING) {
            this.PROVIDESTATUS_ABLEING = PROVIDESTATUS_ABLEING;
        }

        public String getDeptDate() {
            return deptDate;
        }

        public void setDeptDate(String deptDate) {
            this.deptDate = deptDate;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDeptCheckStatusValue() {
            return deptCheckStatusValue;
        }

        public void setDeptCheckStatusValue(String deptCheckStatusValue) {
            this.deptCheckStatusValue = deptCheckStatusValue;
        }

        public String getCHECKSTATUS_DEPTNOPASS() {
            return CHECKSTATUS_DEPTNOPASS;
        }

        public void setCHECKSTATUS_DEPTNOPASS(String CHECKSTATUS_DEPTNOPASS) {
            this.CHECKSTATUS_DEPTNOPASS = CHECKSTATUS_DEPTNOPASS;
        }

        public Object getZwDate() {
            return zwDate;
        }

        public void setZwDate(Object zwDate) {
            this.zwDate = zwDate;
        }

        public String getZwCheckStatusValue() {
            return zwCheckStatusValue;
        }

        public void setZwCheckStatusValue(String zwCheckStatusValue) {
            this.zwCheckStatusValue = zwCheckStatusValue;
        }

        public String getCHECKSTATUS_UNAUDIT() {
            return CHECKSTATUS_UNAUDIT;
        }

        public void setCHECKSTATUS_UNAUDIT(String CHECKSTATUS_UNAUDIT) {
            this.CHECKSTATUS_UNAUDIT = CHECKSTATUS_UNAUDIT;
        }

        public String getKindValue() {
            return kindValue;
        }

        public void setKindValue(String kindValue) {
            this.kindValue = kindValue;
        }

        public Object getZwAuditopinion() {
            return zwAuditopinion;
        }

        public void setZwAuditopinion(Object zwAuditopinion) {
            this.zwAuditopinion = zwAuditopinion;
        }
    }
}
