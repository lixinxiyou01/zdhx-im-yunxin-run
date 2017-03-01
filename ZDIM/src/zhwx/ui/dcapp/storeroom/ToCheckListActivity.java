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
import android.graphics.Color;
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

import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.HashMap;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RequestWithCacheGet;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.refreshlayout.PullableListView;
import zhwx.common.view.waveview.WaveSwipeRefreshLayout;
import zhwx.ui.dcapp.carmanage.view.PagerSlidingTabStrip;
import zhwx.ui.dcapp.carmanage.view.ScrollTabHolder;
import zhwx.ui.dcapp.carmanage.view.ScrollTabHolderFragment;

/**   
 * @Title: AMainActivity.java 
 * @Package com.lanxum.hzth.im.ui.v3.assets 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-7 上午9:52:07 
 */
public class ToCheckListActivity extends BaseActivity implements OnClickListener, ScrollTabHolder, ViewPager.OnPageChangeListener {
	
	private Activity context;
	
	private RequestWithCacheGet mRequestWithCache;
	
	private HashMap<String, ParameterValue> map;
	
	private String indexJson;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
//	private ListView mystoreLV;
	
	private PullableListView mystoreLV;
	
	private WaveSwipeRefreshLayout layout;
	
	private PagerSlidingTabStrip mPagerSlidingTabStrip;
	
	private ViewPager mViewPager;
	
	private PagerAdapter mPagerAdapter;
	
	private String[] title = {"部门","总务"};
	
    @Override
	protected int getLayoutId() {return R.layout.activity_sm_check_main;}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_store);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"申领审核", this);
		mRequestWithCache = new RequestWithCacheGet(context);
		initView();
//		getData();
	}
	
	/**
	 * 
	 */
	private void initView() {
		mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		
		mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(1);
		mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
		mPagerAdapter.setTabHolderScrollingContent(this);
		mViewPager.setAdapter(mPagerAdapter);
		mPagerSlidingTabStrip.setUnderlineHeight(0);
		mPagerSlidingTabStrip.setShouldExpand(true);
		mPagerSlidingTabStrip.setViewPager(mViewPager);
		mPagerSlidingTabStrip.setIndicatorColor(Color.parseColor("#f28d2b"));
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
				fragment = (ScrollTabHolderFragment) ToCheckFragment.newInstance(0);
			} else {
				fragment = (ScrollTabHolderFragment) ToCheckFragment.newInstance(1);
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

	private void getData() {
		getNotice();   //获取公告板数据
	}
	
	private void getNotice(){
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					indexJson = UrlUtil.getNotSignedGrantNumJson(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
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
	
	
	/**申请资产*/
	public void onOrderCar(View v) {
		startActivity(new Intent(context, ApplyForSActivity.class));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		}
	}
}
