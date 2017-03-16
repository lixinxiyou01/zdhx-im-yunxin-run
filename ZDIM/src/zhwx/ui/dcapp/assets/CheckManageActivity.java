package zhwx.ui.dcapp.assets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;

import com.netease.nim.demo.R;

import java.util.ArrayList;
import java.util.List;

import zhwx.common.base.BaseActivity;
import zhwx.common.view.capture.core.CaptureActivity;
import zhwx.ui.dcapp.assets.model.CheckListItem;
import zhwx.ui.dcapp.carmanage.model.ListKind;
import zhwx.ui.dcapp.carmanage.view.PagerSlidingTabStrip;
import zhwx.ui.dcapp.carmanage.view.ScrollTabHolder;
import zhwx.ui.dcapp.carmanage.view.ScrollTabHolderFragment;

/**   
 * @Title: MyRepairManageActivity.java
 * @Package zhwx.ui.dcapp.carmanage
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-7 上午11:04:38 
 */
public class CheckManageActivity extends BaseActivity implements ScrollTabHolder, ViewPager.OnPageChangeListener,OnClickListener {
	
	private Activity context;
	
	private PagerSlidingTabStrip mPagerSlidingTabStrip;
	
	private ViewPager mViewPager;
	
	public static PagerAdapter mPagerAdapter;
	
	private String status = "";
	
	private List<ListKind> kindList;
	/** 启动分类 */
	private int startFlag = -1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		startFlag = getIntent().getIntExtra("startFlag",-1);
		status = getIntent().getStringExtra("status");
		kindList = new ArrayList<ListKind>();
		
		kindList.add(new ListKind("全部", CheckListItem.STATUS_ALL));
		kindList.add(new ListKind("待审核",CheckListItem.STATUS_NOTAUDIT));
		kindList.add(new ListKind("已通过",CheckListItem.STATUS_PASS));
		kindList.add(new ListKind("未通过",CheckListItem.STATUS_NOTPASS));
//		kindList.add(new ListKind("已发放",CheckListItem.STATUS_GRANTED));
		
		initView();
		//直接跳转相应tab
		for (int i = 0; i < kindList.size(); i++) {
			if (status.equals(kindList.get(i).getCode())) {
				mViewPager.setCurrentItem(i, true);
			}
		}
	}

	private void initView() {
		getTopBarView().setBackGroundColor(R.color.main_bg_assets);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"资产申请", this);
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
		mPagerAdapter.notifyDataSetChanged();
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
			System.out.println(position+"position");
			switch (position) {
			case 0:  
				status = CheckListItem.STATUS_ALL; //全部
				break;
			case 1:
				status = CheckListItem.STATUS_NOTAUDIT; //待审核
				break;
			case 2:
				status = CheckListItem.STATUS_PASS; //已通过
				break;
			case 3:
				status = CheckListItem.STATUS_NOTPASS; //未通过
				break;
			case 4:
				status = CheckListItem.STATUS_GRANTED; //已发放
				break;
			default:
				break;
			}	
			ScrollTabHolderFragment fragment = (ScrollTabHolderFragment) CheckListFragment.newInstance(position,status);
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
		case R.id.btn_right:
			startActivity(new Intent(context, CaptureActivity.class));
			break;
		}
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_as_checkmanage;
	}

}
