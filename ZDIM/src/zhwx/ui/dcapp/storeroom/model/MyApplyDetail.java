package zhwx.ui.dcapp.storeroom.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Android on 2017/1/14.
 */

public class MyApplyDetail implements Serializable {


    private List<ApplyreceiverecordBean> applyreceiverecord;
    private List<ApplygoodsListBean> applygoodsList;

    public List<ApplyreceiverecordBean> getApplyreceiverecord() {
        return applyreceiverecord;
    }

    public void setApplyreceiverecord(List<ApplyreceiverecordBean> applyreceiverecord) {
        this.applyreceiverecord = applyreceiverecord;
    }

    public List<ApplygoodsListBean> getApplygoodsList() {
        return applygoodsList;
    }

    public void setApplygoodsList(List<ApplygoodsListBean> applygoodsList) {
        this.applygoodsList = applygoodsList;
    }

    public static class ApplyreceiverecordBean {
        /**
         * deptCheckStatus : 部门审核通过
         * zwCheckStatus : 总务未审核
         * deptAuditOpinition : null
         * zwAuditOpinition : null
         * reason : 23
         * userName : 123
         * kindValue : 部门申领
         * code : 20170115002
         * departmentName : 政教处
         * totalMoney : 0
         * note : 2543541313
         * applyDate : 2017-01-15
         */

        private String deptCheckStatus;
        private String zwCheckStatus;
        private Object deptAuditOpinition;
        private Object zwAuditOpinition;
        private String reason;
        private String userName;
        private String userId;
        private String kindValue;
        private String code;
        private String departmentName;
        private int totalMoney;
        private String note;
        private String applyDate;
        
        private String deptCheckUser;
        private String zwCheckUser;

        public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getDeptCheckStatus() {
            return deptCheckStatus;
        }

        public void setDeptCheckStatus(String deptCheckStatus) {
            this.deptCheckStatus = deptCheckStatus;
        }

        public String getZwCheckStatus() {
            return zwCheckStatus;
        }

        public void setZwCheckStatus(String zwCheckStatus) {
            this.zwCheckStatus = zwCheckStatus;
        }

        public Object getDeptAuditOpinition() {
            return deptAuditOpinition;
        }

        public void setDeptAuditOpinition(Object deptAuditOpinition) {
            this.deptAuditOpinition = deptAuditOpinition;
        }

        public Object getZwAuditOpinition() {
            return zwAuditOpinition;
        }

        public void setZwAuditOpinition(Object zwAuditOpinition) {
            this.zwAuditOpinition = zwAuditOpinition;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getKindValue() {
            return kindValue;
        }

        public void setKindValue(String kindValue) {
            this.kindValue = kindValue;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDepartmentName() {
            return departmentName;
        }

        public void setDepartmentName(String departmentName) {
            this.departmentName = departmentName;
        }

        public int getTotalMoney() {
            return totalMoney;
        }

        public void setTotalMoney(int totalMoney) {
            this.totalMoney = totalMoney;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getApplyDate() {
            return applyDate;
        }

        public void setApplyDate(String applyDate) {
            this.applyDate = applyDate;
        }

		public String getDeptCheckUser() {
			return deptCheckUser;
		}

		public void setDeptCheckUser(String deptCheckUser) {
			this.deptCheckUser = deptCheckUser;
		}

		public String getZwCheckUser() {
			return zwCheckUser;
		}

		public void setZwCheckUser(String zwCheckUser) {
			this.zwCheckUser = zwCheckUser;
		}
    }

    public static class ApplygoodsListBean {
        /**
         * unit :
         * Money : 0
         * count : 1
         * goodsInfoName : b5打印纸
         * code : b5dyz
         * cost :
         */

        private String unit;
        private String id;
        private int Money;
        private int count;
        private String goodsInfoName;
        private String code;
        private String cost;
        
        public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public int getMoney() {
            return Money;
        }

        public void setMoney(int Money) {
            this.Money = Money;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getGoodsInfoName() {
            return goodsInfoName;
        }

        public void setGoodsInfoName(String goodsInfoName) {
            this.goodsInfoName = goodsInfoName;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCost() {
            return cost;
        }

        public void setCost(String cost) {
            this.cost = cost;
        }
    }
}
