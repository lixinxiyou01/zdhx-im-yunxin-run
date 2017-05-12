package zhwx.ui.dcapp.checkin.model;

/**   
 * @Title: Statistics.java 
 * @Package com.zdhx.edu.im.ui.v3.checkin
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ zdhx
 * @date 2016年9月26日 下午3:39:28 
 */
public class CIStatistics {
    /**
     * startAddress : 金域国际中心A座
     * workTime : 0
     * startTime : 16:48
     * endAddress : 金域国际中心A座
     * endTime : 16:48
     * name : 高亚存
     */

    private String startAddress = "暂无";
    private String workTime ;
    private String startTime = "未考勤";
    private String endAddress = "暂无";
    private String endTime = "未考勤";
    private String name;

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
