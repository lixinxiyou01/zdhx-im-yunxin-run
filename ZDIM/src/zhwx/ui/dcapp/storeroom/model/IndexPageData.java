package zhwx.ui.dcapp.storeroom.model;

/**
 * Created by Android on 2017/2/14.
 */

public class IndexPageData {

    /**
     * zwslsh : 1      总务的审核权限   1有 0无
     * zwCount : 8     总务待审核数量
     * sr_statistics : 1         查看统计权限
     * sr_warehouseManage : 1    发放物品权限
     * deptslsh : 1     部门审核权限
     * deptCount : 4    部门待审核数量
     */

    private String zwslsh;
    private String zwCount;
    private String sr_statistics;
    private String sr_warehouseManage;
    private String deptslsh;
    private String deptCount;

    public String getZwslsh() {
        return zwslsh;
    }

    public void setZwslsh(String zwslsh) {
        this.zwslsh = zwslsh;
    }

    public String getZwCount() {
        return zwCount;
    }

    public void setZwCount(String zwCount) {
        this.zwCount = zwCount;
    }

    public String getSr_statistics() {
        return sr_statistics;
    }

    public void setSr_statistics(String sr_statistics) {
        this.sr_statistics = sr_statistics;
    }

    public String getSr_warehouseManage() {
        return sr_warehouseManage;
    }

    public void setSr_warehouseManage(String sr_warehouseManage) {
        this.sr_warehouseManage = sr_warehouseManage;
    }

    public String getDeptslsh() {
        return deptslsh;
    }

    public void setDeptslsh(String deptslsh) {
        this.deptslsh = deptslsh;
    }

    public String getDeptCount() {
        return deptCount;
    }

    public void setDeptCount(String deptCount) {
        this.deptCount = deptCount;
    }
}
