package zhwx.ui.dcapp.repairs;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.netease.nim.demo.R;

import java.util.ArrayList;
import java.util.List;

import zhwx.common.base.BaseActivity;
import zhwx.ui.dcapp.carmanage.model.ListKind;
import zhwx.ui.dcapp.carmanage.view.PagerSlidingTabStrip;
import zhwx.ui.dcapp.carmanage.view.ScrollTabHolder;
import zhwx.ui.dcapp.carmanage.view.ScrollTabHolderFragment;
import zhwx.ui.dcapp.repairs.model.RepairListItem;

/**   
 * @Title: MyRepairManageActivity.java
 * @Package zhwx.ui.dcapp.carmanage
 * @author Li.xin @ 中电和讯
 * @date 2016-3-7 上午11:04:38 
 */
public class MyRepairManageActivity extends BaseActivity implements ScrollTabHolder, ViewPager.OnPageChangeListener,View.OnClickListener {
	
	private Activity context;
	
	private FrameLayout top_bar;
	
	private PagerSlidingTabStrip mPagerSlidingTabStrip;
	
	private ViewPager mViewPager;
	
	public static PagerAdapter mPagerAdapter;
	
	private String status = "";
	
	private List<ListKind> kindList;
	/** 启动分类 */
	private int startFlag = -1;
	
	private TextView noticeTypeTV;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_repairs);
		startFlag = getIntent().getIntExtra("startFlag",-1);
		status = getIntent().getStringExtra("status");
		kindList = new ArrayList<ListKind>();
		noticeTypeTV = (TextView) findViewById(R.id.noticeTypeTV);
		if (startFlag == RMainActivity.STARTFLAG_MYREQUEST) {
			kindList.add(new ListKind("全部", RepairListItem.CHECKSTATUS_ALL));
			kindList.add(new ListKind("未接单",RepairListItem.CHECKSTATUS_WJD));
			kindList.add(new ListKind("维修中",RepairListItem.CHECKSTATUS_WXZ));
			kindList.add(new ListKind("待反馈",RepairListItem.CHECKSTATUS_DFK));
			kindList.add(new ListKind("已修好",RepairListItem.CHECKSTATUS_YXH));

			getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"我的报修单", this);
		} else if (startFlag == RMainActivity.STARTFLAG_MYTASK) {
			kindList.add(new ListKind("全部",RepairListItem.CHECKSTATUS_ALL));
			kindList.add(new ListKind("未接单",RepairListItem.CHECKSTATUS_WJD));
			kindList.add(new ListKind("维修中",RepairListItem.CHECKSTATUS_WXZ));
			kindList.add(new ListKind("已反馈",RepairListItem.CHECKSTATUS_YFK));

			getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"我的维修", this);
		} else if (startFlag == RMainActivity.STARTFLAG_ORDERCHECK) {
			kindList.add(new ListKind("全部",RepairListItem.CHECKSTATUS_ALL));
			kindList.add(new ListKind("待处理",RepairListItem.CHECKSTATUS_DCL));
			kindList.add(new ListKind("已派单",RepairListItem.CHECKSTATUS_YPD));
			kindList.add(new ListKind("已完成",RepairListItem.CHECKSTATUS_YWC));
			kindList.add(new ListKind("费用审批",RepairListItem.CHECKSTATUS_FYSP));

			getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"报修管理", this);
		}
		initView();
		//直接跳转相应tab
		for (int i = 0; i < kindList.size(); i++) {
			if (status.equals(kindList.get(i).getCode())) {
				mViewPager.setCurrentItem(i, true);
			}
		}
	}

	private void initView() {
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(1);
		mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
		mPagerAdapter.setTabHolderScrollingContent(this);
		mViewPager.setAdapter(mPagerAdapter);
		mPagerSlidingTabStrip.setShouldExpand(true);
		mPagerSlidingTabStrip.setViewPager(mViewPager);
		mPagerSlidingTabStrip.setIndicatorColor(getResources().getColor(R.color.main_bg_repairs));
		mPagerSlidingTabStrip.setDividerColor(getResources().getColor(R.color.main_bg_repairs));
		mPagerSlidingTabStrip.setUnderlineColor(getResources().getColor(R.color.main_bg_repairs));
		mPagerSlidingTabStrip.setTabPaddingLeftRight(10);
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
			if (startFlag == RMainActivity.STARTFLAG_MYTASK) {
				switch (position) {
				case 0:  
					status = RepairListItem.CHECKSTATUS_ALL; //全部
					break;
				case 1:
                    status = RepairListItem.CHECKSTATUS_WJD; //未接单
					break;
				case 2:
					status = RepairListItem.CHECKSTATUS_WXZ; //维修中
					break;
				case 3:
					status = RepairListItem.CHECKSTATUS_YFK; //已反馈
					break;
				default:
					status = RepairListItem.CHECKSTATUS_ALL;
					break;
				}
			} else if(startFlag == RMainActivity.STARTFLAG_ORDERCHECK) {
				switch (position) {
				case 0:  
					status = RepairListItem.CHECKSTATUS_ALL; //全部
					break;
				case 1:
                    status = RepairListItem.CHECKSTATUS_DCL; //待处理
					break;
				case 2:
					status = RepairListItem.CHECKSTATUS_YPD; //已派单
					break;
				case 3:
					status = RepairListItem.CHECKSTATUS_YWC; //已完成
					break;
				case 4:
					status = RepairListItem.CHECKSTATUS_FYSP; //费用审批
					break;
				default:
					status = RepairListItem.CHECKSTATUS_ALL;
					break;
				}
			} else if(startFlag == RMainActivity.STARTFLAG_MYREQUEST){
				switch (position) {
				case 0:  
					status = RepairListItem.CHECKSTATUS_ALL; //全部
					break;
				case 1:
					status = RepairListItem.CHECKSTATUS_WJD; //未接单
					break;
				case 2:
					status = RepairListItem.CHECKSTATUS_WXZ; //维修中
					break;
				case 3:
					status = RepairListItem.CHECKSTATUS_DFK; //待反馈
					break;
				case 4:
					status = RepairListItem.CHECKSTATUS_YXH; //已修好
					break;
				default:
					status = RepairListItem.CHECKSTATUS_ALL;
					break;
				}
			}
			ScrollTabHolderFragment fragment = (ScrollTabHolderFragment) MyRepairListFragment.newInstance(position,getPageTitle(position).toString(),status,startFlag);
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
		return R.layout.activity_rm_myrepair_manage;
	}

}
