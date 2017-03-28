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

import com.netease.nim.demo.R;

import zhwx.common.base.BaseActivity;
import zhwx.ui.dcapp.carmanage.view.PagerSlidingTabStrip;
import zhwx.ui.dcapp.carmanage.view.ScrollTabHolder;
import zhwx.ui.dcapp.carmanage.view.ScrollTabHolderFragment;


/**   
 * @Title: RepairDetailActivity.java
 * @Package zhwx.ui.dcapp.carmanage
 * @author Li.xin @ 中电和讯
 * @date 2016-3-17 下午4:08:38 
 */
public class RepairDetailActivity extends BaseActivity implements ScrollTabHolder, ViewPager.OnPageChangeListener,View.OnClickListener{
	
	private Activity context;
	
	private PagerSlidingTabStrip mPagerSlidingTabStrip;
	
	private ViewPager mViewPager;
	
	private PagerAdapter mPagerAdapter;
	
	private String[] title = {"报修单详情","维修记录"};
	
	private String id;
	private String status;
	private String evaluateFlag;
	private int startFlag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		id = getIntent().getStringExtra("id");
		status = getIntent().getStringExtra("status");
		startFlag = getIntent().getIntExtra("startFlag" , -1);
		initView();
	}

	private void initView() {
		getTopBarView().setBackGroundColor(R.color.main_bg_repairs);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"报修单详情", this);
		mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
		mPagerAdapter.setTabHolderScrollingContent(this);
		mViewPager.setAdapter(mPagerAdapter);
		mPagerSlidingTabStrip.setUnderlineHeight(0);
		mPagerSlidingTabStrip.setShouldExpand(true);
		mPagerSlidingTabStrip.setViewPager(mViewPager);
		mPagerSlidingTabStrip.setIndicatorColor(getResources().getColor(R.color.main_bg_repairs));
		mPagerSlidingTabStrip.setDividerColor(getResources().getColor(R.color.gray));
		mPagerSlidingTabStrip.setUnderlineColor(getResources().getColor(R.color.gray));
		mPagerSlidingTabStrip.setTabPaddingLeftRight(10);
		mPagerSlidingTabStrip.setOnPageChangeListener(this);
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
			return title[position];
		}

		@Override
		public int getCount() {
			return title.length;
		}

		@Override
		public Fragment getItem(int position) {
			ScrollTabHolderFragment fragment = null;
			if(position == 0) {
				fragment = (ScrollTabHolderFragment) RepairDetailFragment.newInstance(id,startFlag,status,evaluateFlag);
			} else {
				fragment = (ScrollTabHolderFragment) RepairStatusFragment.newInstance(id);
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
		// TODO Auto-generated method stub
		return R.layout.activity_rm_repairdetail;
	}



}
