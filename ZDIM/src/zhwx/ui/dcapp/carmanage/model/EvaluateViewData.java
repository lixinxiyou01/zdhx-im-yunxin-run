package zhwx.ui.dcapp.carmanage.model;

import android.widget.EditText;

import java.util.List;

/**   
 * @Title: EvaluateData.java 
 * @Package zhwx.ui.dcapp.carmanage.model
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-30 下午3:19:10 
 */
public class EvaluateViewData {
	
	private EditText editText;
	private String id;
	private List<RatingData> ratingData;
	
	public EditText getEditText() {
		return editText;
	}

	public void setEditText(EditText editText) {
		this.editText = editText;
	}

	public List<RatingData> getRatingData() {
		return ratingData;
	}

	public void setRatingData(List<RatingData> ratingData) {
		this.ratingData = ratingData;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
