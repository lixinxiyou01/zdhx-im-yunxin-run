package zhwx.common.view.pullrefreshview;

import android.content.Context;
import android.os.SystemClock;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.ListView;

public class PullListView extends ListView implements OnScrollListener {
	private float mInitialMotionY, mLastMotionY, moveY_1, moveY_2;
	private boolean mIsBeingDragged = false;
	private int mTop = 0;
	private Interpolator mScrollAnimationInterpolator;
	private SmoothScrollRunnable mCurrentSmoothScrollRunnable;
	private OnRefreshListener mOnRefreshListener;
	private OnLoadMoreListener mOnLoadMoreListener;
	private RotateLayout rotateLayout;
	private boolean isRefreshing = false, isLoadMore = false;

	public PullListView(Context context) {
		super(context);
		init();
	}

	public PullListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PullListView getInstance() {
		return this;
	}

	void init() {
		getInstance().setOnScrollListener(this);
	}

	private View headerView;

	public void setPullHeaderView(View v) {
		headerView = v;
		addHeaderView(v);
		if (mTop == 0) {
			mTop = 1080 / 5;
		}
		pullEvent(mTop);
		if (null == mScrollAnimationInterpolator) {
			mScrollAnimationInterpolator = new DecelerateInterpolator();
		}
	}

	public View getPullHeaderView() {
		return headerView;
	}

	public void setPullHeaderViewHeight(int height) {
		mTop = height;
		if (headerView != null) {
			pullEvent(mTop);
		}
	}

	public void setRotateLayout(RotateLayout rotateLayout) {
		this.rotateLayout = rotateLayout;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev); // 触屏事件传递给子view
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE: {
			if (isRefreshing || isLoadMore) {
				return super.onTouchEvent(event);
			}
			if (isFirstItemVisible()) {
				mLastMotionY = event.getY();
				moveY_1 = event.getY();
				if (moveY_1 != moveY_2) {
					float rotate = moveY_2 - moveY_1;
					System.out.println("rotate= " + rotate);
					moveY_2 = moveY_1;
					if (mLastMotionY - mInitialMotionY > 0) {
						mIsBeingDragged = true;
						rotateLayout.reset();
						rotateLayout.roate(rotate);
						pullEvent(mLastMotionY + mTop - mInitialMotionY);
						return super.onTouchEvent(newMotionEvent(event));
					}
				}
				System.out.println("mLastMotionY:" + mLastMotionY);
			}
			break;
		}
		case MotionEvent.ACTION_DOWN: {
			if (isRefreshing || isLoadMore) {
				return super.onTouchEvent(event);
			}
			if (isFirstItemVisible()) {
				mLastMotionY = mInitialMotionY = event.getY();
			}
			break;
		}
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP: {
			if (mIsBeingDragged) {
				mIsBeingDragged = false;
				mLastMotionY = event.getY();
				if (mLastMotionY - mInitialMotionY > mTop) {
					OnRefreshing();
					if (null != mOnRefreshListener) {
						mOnRefreshListener.onRefresh(PullListView.this);
					}
				} else {
					onCompleteRefresh();
				}

			}
			break;
			}
		}
		return super.onTouchEvent(event);
	}

	private MotionEvent newMotionEvent(MotionEvent ev) {
		return MotionEvent.obtain(ev.getDownTime(), SystemClock.uptimeMillis(),
				MotionEvent.ACTION_CANCEL, ev.getX(), ev.getY(), 0);
	}

	public final void setOnRefreshListener(OnRefreshListener listener) {
		mOnRefreshListener = listener;
	}

	public final void setOnLoadMoreListener(OnLoadMoreListener listener) {
		mOnLoadMoreListener = listener;
	}

	/**
	 * Simple Listener to listen for any callbacks to Refresh.
	 * 
	 * @author Chris Banes
	 */
	public static interface OnRefreshListener<V extends View> {

		/**
		 * onRefresh will be called for both a Pull from start, and Pull from
		 * end
		 */
		public void onRefresh(PullListView refreshView);

	}

	/**
	 * Simple Listener to listen for any callbacks to Refresh.
	 * 
	 * @author Chris Banes
	 */
	public static interface OnLoadMoreListener<V extends View> {

		/**
		 * onRefresh will be called for both a Pull from start, and Pull from
		 * end
		 */
		public void onLoadMore(PullListView refreshView);

	}

	public void onCompleteRefresh() {
		if (isRefreshing) {
			isRefreshing = false;
		}
		rotateLayout.toup();
		if (isLoadMore) {
			isLoadMore = false;
		}
		mCurrentSmoothScrollRunnable = new SmoothScrollRunnable(
				headerView.getPaddingTop() + mTop, mTop, 300,
				new OnSmoothScrollFinishedListener() {
					@Override
					public void onSmoothScrollFinished() {

					}
				});
		postDelayed(mCurrentSmoothScrollRunnable, 20);
	}
	
	public void onCompleteLoad() {
		if (isRefreshing) {
			isRefreshing = false;
		}
		if (isLoadMore) {
			isLoadMore = false;
		}
	}
	
	
	
	/**
	 * 正在刷新
	 */
	public void OnRefreshing() {
		isRefreshing = true;
		rotateLayout.rotateAnimation();
		mCurrentSmoothScrollRunnable = new SmoothScrollRunnable(
				headerView.getPaddingTop() + mTop, mTop, 300,
				new OnSmoothScrollFinishedListener() {
					@Override
					public void onSmoothScrollFinished() {

					}
				});
		postDelayed(mCurrentSmoothScrollRunnable, 20);
	}

	final class SmoothScrollRunnable implements Runnable {
		private final Interpolator mInterpolator;
		private final int mScrollToY;
		private final int mScrollFromY;
		private final long mDuration;
		private OnSmoothScrollFinishedListener mListener;

		private boolean mContinueRunning = true;
		private long mStartTime = -1;
		private int mCurrentY = -1;

		public SmoothScrollRunnable(int fromY, int toY, long duration,
				OnSmoothScrollFinishedListener listener) {
			mScrollFromY = fromY;
			mScrollToY = toY;
			mInterpolator = mScrollAnimationInterpolator;
			mDuration = duration;
			mListener = listener;
		}

		@Override
		public void run() {

			/**
			 * Only set mStartTime if this is the first time we're starting,
			 * else actually calculate the Y delta
			 */
			if (mStartTime == -1) {
				mStartTime = System.currentTimeMillis();
			} else {

				/**
				 * We do do all calculations in long to reduce software float
				 * calculations. We use 1000 as it gives us good accuracy and
				 * small rounding errors
				 */
				long normalizedTime = (1000 * (System.currentTimeMillis() - mStartTime))
						/ mDuration;
				normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

				final int deltaY = Math.round((mScrollFromY - mScrollToY)
						* mInterpolator
								.getInterpolation(normalizedTime / 1000f));
				mCurrentY = mScrollFromY - deltaY;
				pullEvent(mCurrentY);
			}

			// If we're not at the target Y, keep going...
			if (mContinueRunning && mScrollToY != mCurrentY) {
				ViewCompat.postOnAnimation(PullListView.this, this);
			} else {
				if (null != mListener) {
					mListener.onSmoothScrollFinished();
				}
			}
		}

		public void stop() {
			mContinueRunning = false;
			removeCallbacks(this);
		}
	}

	static interface OnSmoothScrollFinishedListener {
		void onSmoothScrollFinished();
	}

	private void pullEvent(float newScrollValue) {
		// scrollTo(0, (int) newScrollValue + mTop);
		headerView.setPadding(0, (int) newScrollValue - mTop, 0, 0);
	}

	private boolean isFirstItemVisible() {
		final Adapter adapter = getAdapter();
		if (null == adapter || adapter.isEmpty()) {
			return true;
		} else {
			if (getFirstVisiblePosition() <= 1) {
				final View firstVisibleChild = getChildAt(0);
				if (firstVisibleChild != null) {
					return firstVisibleChild.getTop() >= getTop();
				}
			}
		}
		return false;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem + visibleItemCount == totalItemCount
				&& totalItemCount > 0 && !isRefreshing && !isLoadMore) {

		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			if (view.getLastVisiblePosition() == view.getCount() - 1
					&& !isRefreshing && !isLoadMore) {
				if (mOnLoadMoreListener != null) {
					isLoadMore = true;
					mOnLoadMoreListener.onLoadMore(getInstance());
				}
			}
		}
	}
}