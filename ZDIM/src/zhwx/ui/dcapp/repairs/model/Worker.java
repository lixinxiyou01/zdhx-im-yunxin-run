package zhwx.ui.dcapp.repairs.model;

/**
 * Created by Android on 2017/3/15.
 */

public class Worker {

    /**
     * workerName : 技术支持
     * workerId : 20150520125211813393070681955161
     * workingCount : 7
     * sameKindWorkCount : 7
     */

    private String workerName;
    private String workerId;
    private String workingCount;
    private String sameKindWorkCount;
    private boolean isCheck;

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getWorkingCount() {
        return workingCount;
    }

    public void setWorkingCount(String workingCount) {
        this.workingCount = workingCount;
    }

    public String getSameKindWorkCount() {
        return sameKindWorkCount;
    }

    public void setSameKindWorkCount(String sameKindWorkCount) {
        this.sameKindWorkCount = sameKindWorkCount;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
