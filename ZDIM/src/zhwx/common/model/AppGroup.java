package zhwx.common.model;

import java.util.List;

/**
 * Created by Android on 2017/3/18.
 */

public class AppGroup {
    private String groupName;
    private String groupCode;
    private List<Apps> apps;

    public AppGroup(String groupName, String groupCode) {
        this.groupName = groupName;
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public List<Apps> getApps() {
        return apps;
    }

    public void setApps(List<Apps> apps) {
        this.apps = apps;
    }
}
