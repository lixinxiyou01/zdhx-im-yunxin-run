package zhwx.ui.dcapp.repairs.model;

import java.util.List;

import zhwx.ui.dcapp.assets.model.IdAndName;

/**
 * Created by Android on 2017/3/13.
 */

public class FaultList {

    private String currSchoolId;
    private List<FaultListBean> faultList;
    private List<SchoolListBean> schoolList;

    public List<FaultListBean> getFaultList() {
        return faultList;
    }

    public void setFaultList(List<FaultListBean> faultList) {
        this.faultList = faultList;
    }

    public List<SchoolListBean> getSchoolList() {
        return schoolList;
    }

    public void setSchoolList(List<SchoolListBean> schoolList) {
        this.schoolList = schoolList;
    }

    public static class FaultListBean {
        /**
         * id : 20170311160853104736242608848552
         * name : 不启动
         */

        private String id;
        private String name;
        private boolean isChecked = false;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }

    public static class SchoolListBean extends IdAndName {
        private List<BuildingListBean> buildingList;
        public List<BuildingListBean> getBuildingList() {
            return buildingList;
        }

        public void setBuildingList(List<BuildingListBean> buildingList) {
            this.buildingList = buildingList;
        }
        public static class BuildingListBean extends IdAndName {
            private List<FloorListBean> floorList;

            public List<FloorListBean> getFloorList() {
                return floorList;
            }

            public void setFloorList(List<FloorListBean> floorList) {
                this.floorList = floorList;
            }

            public static class FloorListBean extends IdAndName {
                private List<RoomListBean> roomList;

                public List<RoomListBean> getRoomList() {
                    return roomList;
                }

                public void setRoomList(List<RoomListBean> roomList) {
                    this.roomList = roomList;
                }

                public static class RoomListBean extends IdAndName {

                }
            }
        }
    }

    public String getCurrSchoolId() {
        return currSchoolId;
    }

    public void setCurrSchoolId(String currSchoolId) {
        this.currSchoolId = currSchoolId;
    }
}
