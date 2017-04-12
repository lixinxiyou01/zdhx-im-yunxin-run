package zhwx.ui.dcapp.storeroom.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import zhwx.ui.dcapp.assets.model.IdAndName;


/**
 * Created by Android on 2017/1/14.
 */

public class ApplyRecordInfo implements Serializable{

    /**
     * applymessage : [{"userId":"20151116112937165940444498014398","userName":"123","code":"20170114001","date":"2017-01-14"}]
     * applyReceiveKind : {"grsl":"个人申领","bmsl":"部门申领"}
     * myDepartmentList : [{"departmentId":"20150617142910322892725872172267","departmentName":"全体教职工"},{"departmentId":"20150707133737645483001339205998","departmentName":"行政干部12"},{"departmentId":"20150707133747518897931919532631","departmentName":"学科主任"},{"departmentId":"20150520141705811013126097820985","departmentName":"办公室444"},{"departmentId":"20150520141746057828382543355534","departmentName":"政教处"},{"departmentId":"20150520141831641163533325278125","departmentName":"教务处"},{"departmentId":"20150520141720925408985478874841","departmentName":"总务处"},{"departmentId":"20150520141842777962660017823824","departmentName":"新疆部"},{"departmentId":"20150520141935304745026254689837","departmentName":"初中部"},{"departmentId":"20150520142019711097157620529959","departmentName":"高一年级组"},{"departmentId":"20150520142036415756086677134261","departmentName":"高二年级组"},{"departmentId":"20150520142045239207147216788501","departmentName":"高三年级组"},{"departmentId":"20150623085054494062936414178683","departmentName":"班主任"},{"departmentId":"20150805093139176066036178744761","departmentName":"教研组"},{"departmentId":"20150707135523413894492804318465","departmentName":"技术支持"}]
     */

    private Map<String,String> applyReceiveKind;
    private List<ApplymessageBean> applymessage;
    private List<MyDepartmentListBean> myDepartmentList;

    public Map<String,String> getApplyReceiveKind() {
        return applyReceiveKind;
    }

    public void setApplyReceiveKind(Map<String,String> applyReceiveKind) {
        this.applyReceiveKind = applyReceiveKind;
    }

    public List<ApplymessageBean> getApplymessage() {
        return applymessage;
    }

    public void setApplymessage(List<ApplymessageBean> applymessage) {
        this.applymessage = applymessage;
    }

    public List<MyDepartmentListBean> getMyDepartmentList() {
        return myDepartmentList;
    }

    public void setMyDepartmentList(List<MyDepartmentListBean> myDepartmentList) {
        this.myDepartmentList = myDepartmentList;
    }

    public static class ApplyReceiveKindBean {
        /**
         * grsl : 个人申领
         * bmsl : 部门申领
         */

        private String grsl;
        private String bmsl;

        public String getGrsl() {
            return grsl;
        }

        public void setGrsl(String grsl) {
            this.grsl = grsl;
        }

        public String getBmsl() {
            return bmsl;
        }

        public void setBmsl(String bmsl) {
            this.bmsl = bmsl;
        }
    }

    public static class ApplymessageBean {
        /**
         * userId : 20151116112937165940444498014398
         * userName : 123
         * code : 20170114001
         * date : 2017-01-14
         */

        private String userId;
        private String userName;
        private String code;
        private String date;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    public static class MyDepartmentListBean extends IdAndName {
        /**
         * departmentId : 20150617142910322892725872172267
         * departmentName : 全体教职工
         */
        private String departmentId;
        private String departmentName;
        private List<CheckUser> userList;

        public String getDepartmentId() {
            return departmentId;
        }

        public void setDepartmentId(String departmentId) {
            this.departmentId = departmentId;
        }

        public String getDepartmentName() {
            return departmentName;
        }

        public void setDepartmentName(String departmentName) {
            this.departmentName = departmentName;
        }

		public String getName() {
			return departmentName;
		}

		public String getId() {
			return departmentId;
		}
		
		public List<CheckUser> getUserList() {
			return userList;
		}

		public void setUserList(List<CheckUser> userList) {
			this.userList = userList;
		}



		public static class CheckUser extends IdAndName{
			private String checkUserId;
			private String checkUserName;
			public String getCheckUserId() {
				return checkUserId;
			}
			public void setCheckUserId(String checkUserId) {
				this.checkUserId = checkUserId;
			}
			public String getCheckUserName() {
				return checkUserName;
			}
			public void setCheckUserName(String checkUserName) {
				this.checkUserName = checkUserName;
			}
			
			public String getId() {
				return checkUserId;
			}
			public String getName() {
				return checkUserName;
			}
		}
    }
}
