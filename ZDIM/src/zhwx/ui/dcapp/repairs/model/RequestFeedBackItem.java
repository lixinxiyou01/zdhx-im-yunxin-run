package zhwx.ui.dcapp.repairs.model;

/**
 * Created by Android on 2017/3/21.
 */

public class RequestFeedBackItem {
    /**
     * attitudeStr : 0.0
     * qualityStr : 0.0
     * technicalLevelStr : 0.0
     * speedStr : 0.0
     * scoreStr :  0.0
     * suggestion : null
     * repairFlag : null
     */

    private String attitudeStr;
    private String qualityStr;
    private String technicalLevelStr;
    private String speedStr;
    private String scoreStr;
    private String suggestion;
    private String repairFlag;

    public String getAttitudeStr() {
        return attitudeStr;
    }

    public void setAttitudeStr(String attitudeStr) {
        this.attitudeStr = attitudeStr;
    }

    public String getQualityStr() {
        return qualityStr;
    }

    public void setQualityStr(String qualityStr) {
        this.qualityStr = qualityStr;
    }

    public String getTechnicalLevelStr() {
        return technicalLevelStr;
    }

    public void setTechnicalLevelStr(String technicalLevelStr) {
        this.technicalLevelStr = technicalLevelStr;
    }

    public String getSpeedStr() {
        return speedStr;
    }

    public void setSpeedStr(String speedStr) {
        this.speedStr = speedStr;
    }

    public String getScoreStr() {
        return scoreStr;
    }

    public void setScoreStr(String scoreStr) {
        this.scoreStr = scoreStr;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getRepairFlag() {
        return repairFlag;
    }

    public void setRepairFlag(String repairFlag) {
        this.repairFlag = repairFlag;
    }
}
