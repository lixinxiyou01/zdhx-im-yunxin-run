package zhwx.ui.dcapp.repairs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.util.HashMap;
import java.util.List;

import volley.Response;
import volley.VolleyError;
import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.model.V3NoticeCenter;
import zhwx.common.util.Log;
import zhwx.common.util.RequestWithCacheGet;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.lazyImageLoader.cache.ImageLoader;
import zhwx.common.view.capture.core.CaptureActivity;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.repairs.model.DeviceKind;


/**   
 * @Title: GrantActivity.java 
 * @Package zhwx.ui.dcapp.assets
 * @Description: 资产发放购物车
 * @author Li.xin @ zdhx
 * @date 2016年8月22日 下午12:42:43 
 */
public class RequestBackActivity extends BaseActivity implements OnClickListener{
	
	private Activity context;
		
	private ECProgressDialog mPostingdialog;
	
	private RequestWithCacheGet cache;
	
	private HashMap<String, ParameterValue> map;
	
	private String circleJson;
	
	private List<DeviceKind> allDataList;

	private ImageLoader imageLoader;
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_rm_device_kind;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		imageLoader = new ImageLoader(context);
		cache = new RequestWithCacheGet(context);
		getTopBarView().setBackGroundColor(R.color.main_bg_repairs);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, R.drawable.icon_sao,"设备分类", this);
	}

	/**
	 * 
	 */
	private void getData() {
		mPostingdialog = new ECProgressDialog(context, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		try {
			circleJson = cache.getRseponse(UrlUtil.getMaintenanceTeamList(ECApplication.getInstance().getV3Address(), map), new RequestWithCacheGet.RequestListener() {
				
				@Override
				public void onResponse(String response) {
					if (response != null && !response.equals(RequestWithCacheGet.NOT_OUTOFDATE)) {
						Log.i("新数据:"+response);
						refreshData(response);
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			mPostingdialog.dismiss();
		}
		
		if ((circleJson != null && !circleJson.equals(RequestWithCacheGet.NO_DATA))) {
			Log.i("缓存数据:"+circleJson);
			refreshData(circleJson);
		}
	}

	/**
	 * @param circleJson2
	 */
	private void refreshData(String circleJson2) {
		if (circleJson2.contains("<html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		allDataList = new Gson().fromJson(circleJson2, new TypeToken<List<DeviceKind>>() {}.getType());
		mPostingdialog.dismiss();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			startActivity(new Intent(context, CaptureActivity.class).putExtra("moduleCode", V3NoticeCenter.NOTICE_KIND_REPAIR));
			break;
		}
	}
}
