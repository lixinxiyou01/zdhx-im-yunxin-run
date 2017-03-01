package zhwx.ui.dcapp.assets;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;

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
import zhwx.ui.dcapp.assets.model.MyAssets;
import zhwx.ui.dcapp.carmanage.model.ListKind;
import zhwx.ui.dcapp.carmanage.view.PagerSlidingTabStrip;
import zhwx.ui.dcapp.carmanage.view.ScrollTabHolder;
import zhwx.ui.dcapp.carmanage.view.ScrollTabHolderFragment;

/**   
 * @Title: OrderManageActivity.java 
 * @Package zhwx.ui.dcapp.carmanage
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-7 上午11:04:38 
 */
public class MyAssetsManageActivity extends BaseActivity implements ScrollTabHolder, ViewPager.OnPageChangeListener,OnClickListener {
	
	private Activity context;
	
	private PagerSlidingTabStrip mPagerSlidingTabStrip;
	
	private ViewPager mViewPager;
	
	public static PagerAdapter mPagerAdapter;
	
	private String status = "";
	
	private List<ListKind> kindList;
	
	private ECProgressDialog mPostingdialog;
	
	private Handler handler = new Handler();
	
	private HashMap<String, ParameterValue> map;
	
	private String json;
	
	private MyAssets myAssets;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_assets);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"我的资产", this);
		kindList = new ArrayList<ListKind>();
		kindList.add(new ListKind("我的资产", MyAssets.STATUS_MYASSETS));
		kindList.add(new ListKind("申请记录",MyAssets.STATUS_APPLYRECORD));
		kindList.add(new ListKind("已归还",MyAssets.STATUS_REBACKED));
		getData();
	}

	
	private void getData(){
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					json = UrlUtil.getMyAssetListJson(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData(json);
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

	/**
	 * @param json2
	 */
	private void refreshData(String json2) {
		System.out.println(json2);
		if (json2.contains("<html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		myAssets = new Gson().fromJson(json2, MyAssets.class);
		initView();
		mPostingdialog.dismiss();
	}
	
	private void initView() {
		mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(1);
		mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
		mPagerAdapter.setTabHolderScrollingContent(this);
		mViewPager.setAdapter(mPagerAdapter);
		mPagerSlidingTabStrip.setShouldExpand(true);
		mPagerSlidingTabStrip.setIndicatorColor(getResources().getColor(R.color.main_bg_assets));
		mPagerSlidingTabStrip.setDividerColor(getResources().getColor(R.color.main_bg_assets));
		mPagerSlidingTabStrip.setUnderlineColor(getResources().getColor(R.color.main_bg_assets));
		mPagerSlidingTabStrip.setTabPaddingLeftRight(10);
		mPagerSlidingTabStrip.setViewPager(mViewPager);
		mPagerSlidingTabStrip.setOnPageChangeListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int position) {
	}

	@Override
	public void adjustScroll(int scrollHeight) {
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount, int pagePosition) {
		
	}
	
	public class PagerAdapter extends FragmentStatePagerAdapter {

		private SparseArrayCompat<ScrollTabHolder> mScrollTabHolders;
		
		private ScrollTabHolder mListener;

		public PagerAdapter(FragmentManager fm) {
			super(fm);
			mScrollTabHolders = new SparseArrayCompat<ScrollTabHolder>();
		}

		public void setTabHolderScrollingContent(ScrollTabHolder listener) {
			mListener = listener;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return kindList.get(position).getName();
		}

		@Override
		public int getCount() {
			return kindList.size();
		}
		
		@Override  
		public int getItemPosition(Object object) {  
		    return POSITION_NONE;  
		} 
		
		@Override
		public Fragment getItem(int position) {
			ScrollTabHolderFragment fragment = null;
			switch (position) {
			case 0:  
				status = MyAssets.STATUS_MYASSETS; //我的资产
				fragment = (ScrollTabHolderFragment) MyAssetsFragment.newInstance(position,myAssets);
				break;
			case 1:
				status = MyAssets.STATUS_APPLYRECORD; //申请记录
				fragment = (ScrollTabHolderFragment) ApplyRecordFragment.newInstance(position,myAssets);
				break;
			case 2:
				status = MyAssets.STATUS_REBACKED; //已归还
				fragment = (ScrollTabHolderFragment) ReturnAssetsFragment.newInstance(position,myAssets);
				break;
			default:
				break;
			}	
			mScrollTabHolders.put(position, fragment);
			if (mListener != null) {
				fragment.setScrollTabHolder(mListener);
			}
			return fragment;
		}

		public SparseArrayCompat<ScrollTabHolder> getScrollTabHolders() {
			return mScrollTabHolders;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		}
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_as_checkmanage;
	}
}
