package zhwx.ui.dcapp.repairs.model;

import java.util.List;

/**
 * Created by Android on 2017/3/25.
 */

public class ConsumResult {
    private String totalCost;
    private List<ConsumItem> consumItems;

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public List<ConsumItem> getConsumItems() {
        return consumItems;
    }

    public void setConsumItems(List<ConsumItem> consumItems) {
        this.consumItems = consumItems;
    }
}
