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
import com.google.gson.reflect.TypeToken;
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
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.treelistview.bean.FileBean;
import zhwx.common.view.treelistview.utils.Node;
import zhwx.common.view.treelistview.utils.adapter.TreeListViewAdapter;
import zhwx.ui.dcapp.storeroom.adapter.SimpleTreeListViewAdapterForStoreApply;
import zhwx.ui.dcapp.storeroom.model.GoodsTree;

/**   
 * @Title: AMainActivity.java 
 * @Package com.lanxum.hzth.im.ui.v3.assets 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-7 上午9:52:07 
 */
public class GoodsKindActivity extends BaseActivity implements OnClickListener {
	
	private Activity context;
	
	private HashMap<String, ParameterValue> map;
	
	private String indexJson;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private ListView mystoreLV;
	
	private List<GoodsTree> treeList = new ArrayList<GoodsTree>();
	
	private List<FileBean> datas;
	
	private SimpleTreeListViewAdapterForStoreApply<FileBean> treeListViewAdapter;
	
	private String kind;
	
	
    @Override
	protected int getLayoutId() {return R.layout.activity_sm_goodskind;}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_store);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"物品分类", this);
		kind = getIntent().getStringExtra("kind");
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
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					indexJson = UrlUtil.getGoodsKindJson(ECApplication.getInstance().getV3Address(), map);
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
		treeList = new Gson().fromJson(indexJson, new TypeToken<List<GoodsTree>>() {}.getType());
		datas = new ArrayList<FileBean>();
		for (int i = 0; i < treeList.size(); i++) {
			datas.add(new FileBean(Integer.parseInt(treeList.get(i).getId().length() < 5 ? "0" : treeList.get(i).getId().substring(23, 31)), Integer
					.parseInt(treeList.get(i).getParentId().length() < 5 ? "0"
							: treeList.get(i).getParentId().substring(23, 31)),
							treeList.get(i).getName(), treeList.get(i).getId(), ""));
			if (treeList.get(i).getNodes().size() > 0) {
				for (int j = 0; j < treeList.get(i).getNodes().size(); j++) {
					addNodeData(treeList.get(i).getNodes().get(j));
				}
			}
		}
		
		try {
			treeListViewAdapter = new SimpleTreeListViewAdapterForStoreApply<FileBean>(
					mystoreLV, (GoodsKindActivity) context, datas, 0);
			mystoreLV.setAdapter(treeListViewAdapter);
			treeListViewAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {

				@Override
				public void onClick(Node node, int arg0) {
					if (node.isLeaf()) {
						if (StringUtil.isNotBlank(kind)) {
							Intent intent = new Intent(context,GoodsListForHandActivity.class);
							intent.putExtra("kindId", node.getContactId());
							startActivityForResult(intent, 100);
						} else {
							Intent intent = new Intent(context,GoodsListActivity.class);
							intent.putExtra("kindId", node.getContactId());
							startActivityForResult(intent, 100);
						}
					}
				}
			});
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	private void addNodeData(GoodsTree.GoodsNode node) {
		datas.add(new FileBean(Integer.parseInt(node.getId().length() < 5 ? "0" : node.getId().substring(23, 31)), Integer
				.parseInt(node.getParentId().length() < 5 ? "0"
						: node.getParentId().substring(23, 31)),node.getName(), node.getId(), ""));
		if (node.getNodes().size() > 0) {
			for (int j = 0; j < node.getNodes().size(); j++) {
				addNodeData(node.getNodes().get(j));
			}
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
