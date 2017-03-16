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
import zhwx.ui.dcapp.carmanage.CMainActivity;
import zhwx.ui.dcapp.carmanage.OrderListFragment;
import zhwx.ui.dcapp.carmanage.model.ListKind;
import zhwx.ui.dcapp.carmanage.model.OrderCarListItem;
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
public class RepairCheckManageActivity extends BaseActivity implements ScrollTabHolder, ViewPager.OnPageChangeListener{
	
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
		startFlag = getIntent().getIntExtra("startFlag",-1);
		status = getIntent().getStringExtra("status");
		kindList = new ArrayList<ListKind>();
		noticeTypeTV = (TextView) findViewById(R.id.noticeTypeTV);
		if (startFlag == CMainActivity.STARTFLAG_MYORDERCAR) {
			kindList.add(new ListKind("全部",OrderCarListItem.CHECKSTATUS_ALL));
			kindList.add(new ListKind("待派车",OrderCarListItem.CHECKSTATUS_DRAFT));
			kindList.add(new ListKind("派车中",OrderCarListItem.CHECKSTATUS_ASSIGNING));
			kindList.add(new ListKind("已派车",OrderCarListItem.CHECKSTATUS_PASS));
			kindList.add(new ListKind("待评价",OrderCarListItem.CHECKSTATUS_FINISH));
			noticeTypeTV.setText("我的订车单");
		} else if (startFlag == CMainActivity.STARTFLAG_MYTASK) {
			kindList.add(new ListKind("全部",OrderCarListItem.CHECKSTATUS_ALL));
			kindList.add(new ListKind("待确认",OrderCarListItem.CHECKSTATUS_DQR));
			kindList.add(new ListKind("待结束",OrderCarListItem.CHECKSTATUS_DJS));
			kindList.add(new ListKind("待反馈",OrderCarListItem.CHECKSTATUS_DPJ));
			noticeTypeTV.setText("我的任务");
		} else if (startFlag == CMainActivity.STARTFLAG_ORDERCHECK) {
			kindList.add(new ListKind("全部",OrderCarListItem.CHECKSTATUS_ALL));
			kindList.add(new ListKind("待审核",OrderCarListItem.CHECKSTATUS_CHECK));
			kindList.add(new ListKind("待派车",OrderCarListItem.CHECKSTATUS_DRAFT));
			kindList.add(new ListKind("派车中",OrderCarListItem.CHECKSTATUS_ASSIGNING));
			kindList.add(new ListKind("已派车",OrderCarListItem.CHECKSTATUS_PASS));
			kindList.add(new ListKind("已完成",OrderCarListItem.CHECKSTATUS_FINISH));
			noticeTypeTV.setText("订车单管理");
		}
		getTopBarView().setVisibility(View.GONE);
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
		setImmerseLayout(top_bar);
		
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
	
//	public void resume(){
//		mPagerAdapter.notifyDataSetChanged();
//	}
	
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
			if (startFlag == CMainActivity.STARTFLAG_MYTASK) {
				switch (position) {
				case 0:  
					status = OrderCarListItem.CHECKSTATUS_ALL; //全部
					break;
				case 1:
					status = OrderCarListItem.CHECKSTATUS_DQR; //待确认
					break;
				case 2:
					status = OrderCarListItem.CHECKSTATUS_DJS; //待结束
					break;
				case 3:
					status = OrderCarListItem.CHECKSTATUS_DPJ; //待评价
					break;
				default:
					break;
				}
			} else if(startFlag == CMainActivity.STARTFLAG_ORDERCHECK) {
				switch (position) {
				case 0:  
					status = OrderCarListItem.CHECKSTATUS_ALL; //全部
					break;
				case 1:
					status = OrderCarListItem.CHECKSTATUS_CHECK; //待审核  ---- 待派车
					break;
				case 2:
					status = OrderCarListItem.CHECKSTATUS_DRAFT; //待派车 ---- 派车中
					break;
				case 3:
					status = OrderCarListItem.CHECKSTATUS_ASSIGNING; //派车中 ---- 已派车
					break;
				case 4:
					status = OrderCarListItem.CHECKSTATUS_PASS; //已派车 ---- 待评价
					break;
				case 5:
					status = OrderCarListItem.CHECKSTATUS_FINISH; //待评价
					break;
				default:
					break;
				}
			} else if(startFlag == CMainActivity.STARTFLAG_MYORDERCAR){
				switch (position) {
				case 0:  
					status = OrderCarListItem.CHECKSTATUS_ALL; //全部
					break;
				case 1:
					status = OrderCarListItem.CHECKSTATUS_DRAFT; //待派车 ---- 派车中
					break;
				case 2:
					status = OrderCarListItem.CHECKSTATUS_ASSIGNING; //派车中 ---- 已派车
					break;
				case 3:
					status = OrderCarListItem.CHECKSTATUS_PASS; //已派车 ---- 待评价
					break;
				case 4:
					status = OrderCarListItem.CHECKSTATUS_FINISH; //待评价
					break;
				}
			}
			ScrollTabHolderFragment fragment = (ScrollTabHolderFragment) OrderListFragment.newInstance(position,getPageTitle(position).toString(),status,startFlag);
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
	
//	@Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//		if (requestCode == 103) {
//			mPagerAdapter.notifyDataSetChanged();
//		}
//	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_cm_ordermanage;
	}

}
