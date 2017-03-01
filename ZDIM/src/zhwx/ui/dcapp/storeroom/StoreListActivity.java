package zhwx.ui.dcapp.storeroom;

/** code is far away from bug with the animal protecting
* 
*     ┏┓　　　┏┓
*   ┏┛┻━━━┛┻┓
*   ┃　　　　　　　┃ 　
*   ┃　　　━　　　┃
*   ┃　┳┛　┗┳　┃
*   ┃　　　　　　　┃
*   ┃　　　┻　　　┃
*   ┃　　　　　　　┃
*   ┗━┓　　　┏━┛
 *     　   　┃　　　┃神兽保佑
 *     　   　┃　　　┃永无BUG！
 *     　　   ┃　　　┗━━━┓
 *     　   　┃　　　　　　　┣┓
 *     　   　┃　　　　　　　┏┛
 *     　   　┗┓┓┏━┳┓┏┛
 *   　  　   　┃┫┫　┃┫┫
 *   　  　   　┗┻┛　┗┻┛
*
*/

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.treelistview.bean.FileBean;
import zhwx.common.view.treelistview.utils.Node;
import zhwx.common.view.treelistview.utils.adapter.TreeListViewAdapter;
import zhwx.ui.dcapp.storeroom.adapter.SimpleTreeListViewAdapterForStoreApply;
import zhwx.ui.dcapp.storeroom.model.ProvideGoodsDetal;

/**   
 * @Title: AMainActivity.java 
 * @Package com.lanxum.hzth.im.ui.v3.assets 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-7 上午9:52:07 
 */
public class StoreListActivity extends BaseActivity implements OnClickListener {
	
	private Activity context;
	
	private HashMap<String, ParameterValue> map;
	
	private String indexJson;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private ListView mystoreLV;
	
	private List<FileBean> datas;
	
	private SimpleTreeListViewAdapterForStoreApply<FileBean> treeListViewAdapter;
	
	private ProvideGoodsDetal provideGoodsDetal;
	
	
    @Override
	protected int getLayoutId() {return R.layout.activity_sm_goodskind;}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_store);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"选择出库仓库", this);
		initView();
		getData();
	}
	
	/**
	 * 
	 */
	private void initView() {
		mystoreLV = (ListView) findViewById(R.id.mystoreLV);
	}

	private void getData() {
		getNotice();  
	}
	
	private void getNotice(){
		mPostingdialog = new ECProgressDialog(context, "正在加载");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("kind", new ParameterValue("1"));
		map.put("id", new ParameterValue(getIntent().getStringExtra("id")));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					indexJson = UrlUtil.getProvideGoodsDetal(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData(indexJson);
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					ToastUtil.showMessage("请求失败，请稍后重试");
					handler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							mPostingdialog.dismiss();
						}
					}, 5);
				}
			}
		}).start();
	}
	
	
	private void refreshData(String indexJson) {
		System.out.println(indexJson);
		if (indexJson.contains("</html>")) {
			ToastUtil.showMessage("数据错误");
			return;
		}
		mPostingdialog.dismiss();
		provideGoodsDetal = new Gson().fromJson(indexJson, ProvideGoodsDetal.class);
		datas = new ArrayList<FileBean>();
		for (int i = 0; i < provideGoodsDetal.getSchoolwarehouseTree().size(); i++) {
			datas.add(new FileBean(Integer.parseInt(provideGoodsDetal.getSchoolwarehouseTree().get(i).getSchoolId().length() < 5 ? "0" : provideGoodsDetal.getSchoolwarehouseTree().get(i).getSchoolId().substring(23, 31)),
					0,provideGoodsDetal.getSchoolwarehouseTree().get(i).getSchoolName(), "", ""));
			if (provideGoodsDetal.getSchoolwarehouseTree().get(i).getWarehouseList().size() > 0) {
				for (int j = 0; j < provideGoodsDetal.getSchoolwarehouseTree().get(i).getWarehouseList().size(); j++) {
					datas.add(new FileBean(Integer.parseInt(provideGoodsDetal.getSchoolwarehouseTree().get(i).getWarehouseList().get(j).getWarehouseId().length() < 5 ?
							"0" : provideGoodsDetal.getSchoolwarehouseTree().get(i).getWarehouseList().get(j).getWarehouseId().substring(23, 31)),
							Integer.parseInt(provideGoodsDetal.getSchoolwarehouseTree().get(i).getSchoolId().length() < 5 ? "0" : provideGoodsDetal.getSchoolwarehouseTree().get(i).getSchoolId().substring(23, 31)),
							provideGoodsDetal.getSchoolwarehouseTree().get(i).getWarehouseList().get(j).getWarehouseName(), provideGoodsDetal.getSchoolwarehouseTree().get(i).getWarehouseList().get(j).getWarehouseId(), ""));
					
				}
			}
		}
		
		try {
			treeListViewAdapter = new SimpleTreeListViewAdapterForStoreApply<FileBean>(
					mystoreLV, (StoreListActivity) context, datas, 0);
			mystoreLV.setAdapter(treeListViewAdapter);
			treeListViewAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {

				@Override
				public void onClick(Node node, int arg0) {
					if (node.isLeaf()) {
						Intent intent = new Intent();
						intent.putExtra("storeId", node.getContactId());
						intent.putExtra("storeName", node.getName());
						setResult(101, intent);
						finish();
					}
				}
			});
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onActivityResult(int requsetCode, int resultCode, Intent data) {
		super.onActivityResult(requsetCode, resultCode, data);
		if(resultCode == RESULT_OK) {
			finish();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.text_right:
			finish();
			break;
		}
	}
}
