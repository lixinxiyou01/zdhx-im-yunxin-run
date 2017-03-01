package zhwx.ui.dcapp.takecourse.listviewgroup;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.reflect.Field;
import java.util.ArrayList;

import zhwx.ui.dcapp.takecourse.MailAdapter;
import zhwx.ui.dcapp.takecourse.listviewgroup.interfaces.ItemClickedListener;
import zhwx.ui.dcapp.takecourse.listviewgroup.interfaces.ItemHeaderClickedListener;


/**
 * 欢迎加入QQ开发群讨论：88130145
 * 
 * @Description:
 * @author: zhuanggy
 * @see:
 * @since:
 * @copyright © 35.com
 * @Date:2013-10-14
 */
public class MailListView extends ListView {

	private int mScreenWidth;

	private OnScrollListener mOnScrollListenerDelegate;
	private boolean mAreHeadersSticky = true;
	private int mHeaderBottomPosition;
	private View mHeader;
	private int mDividerHeight;
	private Drawable mDivider;
	private Boolean mClippingToPadding;
	private final Rect mClippingRect = new Rect();
	private AdapterWrapper mAdapter;
	private MailAdapter mailAdapter;
	private Integer mHeaderPosition;
	private ArrayList<View> mFooterViews;
	private boolean mDrawingListUnderStickyHeader = false;
	private Rect mSelectorRect = new Rect();
	private Field mSelectorPositionField;

	private ItemHeaderClickedListener mAdapterHeaderClickListener = new ItemHeaderClickedListener() {

		@Override
		public void onItemHeaderClick(View header, int itemPosition, long headerId) {
			if (mailAdapter.getmItemHeaderClickedListener() != null) {
				mailAdapter.getmItemHeaderClickedListener().onItemHeaderClick(header, itemPosition, headerId);
			}
		}
	};

	private DataSetObserver mDataSetChangedObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
			reset();
		}

		@Override
		public void onInvalidated() {
			reset();
		}
	};

	private OnScrollListener mOnScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (mOnScrollListenerDelegate != null) {
				mOnScrollListenerDelegate.onScrollStateChanged(view, scrollState);
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (mOnScrollListenerDelegate != null) {
				mOnScrollListenerDelegate.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				scrollChanged(firstVisibleItem);
			}
		}
	};

	public MailListView(Context context) {
		this(context, null);
		init(context);
	}

	public MailListView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.listViewStyle);
		init(context);
	}

	private void init(Context paramContext) {
		// mPaint = new TextPaint();

		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		((Activity) paramContext).getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
		this.mScreenWidth = localDisplayMetrics.widthPixels;
	}

	public MailListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);

		super.setOnScrollListener(mOnScrollListener);
		// null out divider, dividers are handled by adapter so they look good
		// with headers
		super.setDivider(null);
		super.setDividerHeight(0);
		if (mClippingToPadding == null) {
			mClippingToPadding = true;
		}

		try {
			Field selectorRectField = AbsListView.class.getDeclaredField("mSelectorRect");
			selectorRectField.setAccessible(true);
			mSelectorRect = (Rect) selectorRectField.get(this);

			mSelectorPositionField = AbsListView.class.getDeclaredField("mSelectorPosition");
			mSelectorPositionField.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			reset();
			scrollChanged(getFirstVisiblePosition());
		}
	}

	private void reset() {
		mHeader = null;
		mHeaderPosition = null;
		mHeaderBottomPosition = -1;
	}

	@Override
	public boolean performItemClick(View view, int position, long id) {
		if (view instanceof WrapperView) {
			view = ((WrapperView) view).mItem;
		}
		return super.performItemClick(view, position, id);
	}

	@Override
	public void setSelectionFromTop(int position, int y) {
		if (hasStickyHeaderAtPosition(position)) {
			y += getHeaderHeight();
		}
		super.setSelectionFromTop(position, y);
	}

	private boolean hasStickyHeaderAtPosition(int position) {
		position -= getHeaderViewsCount();
		return mAreHeadersSticky && position > 0 && position < mAdapter.getCount() && mAdapter.getHeaderId(position) == mAdapter.getHeaderId(position - 1);
	}

	@Override
	public void setDivider(Drawable divider) {
		this.mDivider = divider;
		if (divider != null) {
			int dividerDrawableHeight = divider.getIntrinsicHeight();
			if (dividerDrawableHeight >= 0) {
				setDividerHeight(dividerDrawableHeight);
			}
		}
		if (mAdapter != null) {
			mAdapter.setDivider(divider);
			requestLayout();
			invalidate();
		}
	}

	@Override
	public void setDividerHeight(int height) {
		mDividerHeight = height;
		if (mAdapter != null) {
			mAdapter.setDividerHeight(height);
			requestLayout();
			invalidate();
		}
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mOnScrollListenerDelegate = l;
	}

	public void setAreHeadersSticky(boolean areHeadersSticky) {
		if (this.mAreHeadersSticky != areHeadersSticky) {
			this.mAreHeadersSticky = areHeadersSticky;
			requestLayout();
		}
	}

	public boolean getAreHeadersSticky() {
		return mAreHeadersSticky;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {

		this.mailAdapter = ((MailAdapter) adapter);

		if (this.isInEditMode()) {
			super.setAdapter(adapter);
			return;
		}
		if (adapter == null) {
			mAdapter = null;
			reset();
			super.setAdapter(null);
			return;
		}
		if (!(adapter instanceof StickyListHeadersAdapter)) {
			throw new IllegalArgumentException("Adapter must implement StickyListHeadersAdapter");
		}
		mAdapter = wrapAdapter(adapter);
		reset();
		super.setAdapter(this.mAdapter);
	}

	private AdapterWrapper wrapAdapter(ListAdapter adapter) {
		AdapterWrapper wrapper;
		wrapper = new AdapterWrapper(getContext(), (StickyListHeadersAdapter) adapter);
		// TODO 给header设置分隔线
		wrapper.setDivider(mDivider);
		wrapper.setDividerHeight(mDividerHeight);

		wrapper.registerDataSetObserver(mDataSetChangedObserver);
		wrapper.setOnHeaderClickListener(mAdapterHeaderClickListener);
		return wrapper;
	}

	public StickyListHeadersAdapter getWrappedAdapter() {
		return mAdapter == null ? null : mAdapter.mAdapterListHeader;
	}

	public View getWrappedView(int position) {
		View view = getChildAt(position);
		if ((view instanceof WrapperView))
			return ((WrapperView) view).mItem;
		return view;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			scrollChanged(getFirstVisiblePosition());
		}
		positionSelectorRect();
		if (!mAreHeadersSticky || mHeader == null) {
			super.dispatchDraw(canvas);
			return;
		}

		if (!mDrawingListUnderStickyHeader) {
			mClippingRect.set(0, mHeaderBottomPosition, getWidth(), getHeight());
			canvas.save();
			canvas.clipRect(mClippingRect);
		}

		super.dispatchDraw(canvas);

		if (!mDrawingListUnderStickyHeader) {
			canvas.restore();
		}

		drawStickyHeader(canvas);
	}

	private void positionSelectorRect() {
		if (!mSelectorRect.isEmpty()) {
			int selectorPosition = getSelectorPosition();
			if (selectorPosition >= 0) {
				int firstVisibleItem = fixedFirstVisibleItem(getFirstVisiblePosition());
				View v = getChildAt(selectorPosition - firstVisibleItem);
				if (v instanceof WrapperView) {
					WrapperView wrapper = ((WrapperView) v);
					mSelectorRect.top = wrapper.getTop() + wrapper.mItemTop;
				}
			}
		}
	}

	private int getSelectorPosition() {
		if (mSelectorPositionField == null) { // not all supported andorid
												// version have this variable
			for (int i = 0; i < getChildCount(); i++) {
				if (getChildAt(i).getBottom() == mSelectorRect.bottom) {
					return i + fixedFirstVisibleItem(getFirstVisiblePosition());
				}
			}
		} else {
			try {
				return mSelectorPositionField.getInt(this);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	private void drawStickyHeader(Canvas canvas) {
		int headerHeight = getHeaderHeight();
		int top = mHeaderBottomPosition - headerHeight;
		// clip the headers drawing region
		mClippingRect.left = getPaddingLeft();
		mClippingRect.right = getWidth() - getPaddingRight();
		mClippingRect.bottom = top + headerHeight;
		mClippingRect.top = mClippingToPadding ? getPaddingTop() : 0;

		canvas.save();
		canvas.clipRect(mClippingRect);
		canvas.translate(getPaddingLeft(), top);
		mHeader.draw(canvas);
		canvas.restore();
	}

	private void measureHeader() {

		int widthMeasureSpec = MeasureSpec.makeMeasureSpec(getWidth() - getPaddingLeft() - getPaddingRight() - (isScrollBarOverlay() ? 0 : getVerticalScrollbarWidth()), MeasureSpec.EXACTLY);
		int heightMeasureSpec = 0;

		ViewGroup.LayoutParams params = mHeader.getLayoutParams();
		if (params == null) {
			mHeader.setLayoutParams(new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		}
		if (params != null && params.height > 0) {
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
		} else {
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		mHeader.measure(widthMeasureSpec, heightMeasureSpec);
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
		// mHeader.setLayoutDirection(this.getLayoutDirection());
		// }

		mHeader.layout(getPaddingLeft(), 0, getWidth() - getPaddingRight(), mHeader.getMeasuredHeight());
	}

	private boolean isScrollBarOverlay() {
		int scrollBarStyle = getScrollBarStyle();
		return scrollBarStyle == SCROLLBARS_INSIDE_OVERLAY || scrollBarStyle == SCROLLBARS_OUTSIDE_OVERLAY;
	}

	private int getHeaderHeight() {
		return mHeader == null ? 0 : mHeader.getMeasuredHeight();
	}

	@Override
	public void setClipToPadding(boolean clipToPadding) {
		super.setClipToPadding(clipToPadding);
		mClippingToPadding = clipToPadding;
	}

	private void scrollChanged(int reportedFirstVisibleItem) {

		int adapterCount = mAdapter == null ? 0 : mAdapter.getCount();
		if (adapterCount == 0 || !mAreHeadersSticky) {
			return;
		}

		final int listViewHeaderCount = getHeaderViewsCount();
		final int firstVisibleItem = fixedFirstVisibleItem(reportedFirstVisibleItem) - listViewHeaderCount;

		if (firstVisibleItem < 0 || firstVisibleItem > adapterCount - 1) {
			reset();
			updateHeaderVisibilities();
			invalidate();
			return;
		}

		if (mHeaderPosition == null || mHeaderPosition != firstVisibleItem) {
			mHeaderPosition = firstVisibleItem;
			mHeader = mAdapter.getHeaderView(mHeaderPosition, mHeader, this);
			measureHeader();
		}

		int childCount = getChildCount();
		if (childCount != 0) {
			View viewToWatch = null;
			int watchingChildDistance = Integer.MAX_VALUE;
			boolean viewToWatchIsFooter = false;

			for (int i = 0; i < childCount; i++) {
				final View child = super.getChildAt(i);
				final boolean childIsFooter = mFooterViews != null && mFooterViews.contains(child);

				final int childDistance = child.getTop() - (mClippingToPadding ? getPaddingTop() : 0);
				if (childDistance < 0) {
					continue;
				}

				if (viewToWatch == null || (!viewToWatchIsFooter && !((WrapperView) viewToWatch).hasHeader()) || ((childIsFooter || ((WrapperView) child).hasHeader()) && childDistance < watchingChildDistance)) {
					viewToWatch = child;
					viewToWatchIsFooter = childIsFooter;
					watchingChildDistance = childDistance;
				}
			}

			final int headerHeight = getHeaderHeight();
			if (viewToWatch != null && (viewToWatchIsFooter || ((WrapperView) viewToWatch).hasHeader())) {
				if (firstVisibleItem == listViewHeaderCount && super.getChildAt(0).getTop() > 0 && !mClippingToPadding) {
					mHeaderBottomPosition = 0;
				} else {
					final int paddingTop = mClippingToPadding ? getPaddingTop() : 0;
					mHeaderBottomPosition = Math.min(viewToWatch.getTop(), headerHeight + paddingTop);
					mHeaderBottomPosition = mHeaderBottomPosition < paddingTop ? headerHeight + paddingTop : mHeaderBottomPosition;
				}
			} else {
				mHeaderBottomPosition = headerHeight + (mClippingToPadding ? getPaddingTop() : 0);
			}
		}
		updateHeaderVisibilities();
		invalidate();
	}

	@Override
	public void addFooterView(View v) {
		super.addFooterView(v);
		if (mFooterViews == null) {
			mFooterViews = new ArrayList<View>();
		}
		mFooterViews.add(v);
	}

	@Override
	public boolean removeFooterView(View v) {
		if (super.removeFooterView(v)) {
			mFooterViews.remove(v);
			return true;
		}
		return false;
	}

	private void updateHeaderVisibilities() {
		int top = mClippingToPadding ? getPaddingTop() : 0;
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = super.getChildAt(i);
			if (child instanceof WrapperView) {
				WrapperView wrapperViewChild = (WrapperView) child;
				if (wrapperViewChild.hasHeader()) {
					View childHeader = wrapperViewChild.mHeader;
					if (wrapperViewChild.getTop() < top) {
						childHeader.setVisibility(View.INVISIBLE);
					} else {
						childHeader.setVisibility(View.VISIBLE);
					}
				}
			}
		}
	}

	private int fixedFirstVisibleItem(int firstVisibleItem) {
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// return firstVisibleItem;
		// }

		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i).getBottom() >= 0) {
				firstVisibleItem += i;
				break;
			}
		}

		// work around to fix bug with firstVisibleItem being to high because
		// listview does not take clipToPadding=false into account
		if (!mClippingToPadding && getPaddingTop() > 0) {
			if (super.getChildAt(0).getTop() > 0) {
				if (firstVisibleItem > 0) {
					firstVisibleItem -= 1;
				}
			}
		}
		return firstVisibleItem;
	}

	public void setDrawingListUnderStickyHeader(boolean drawingListUnderStickyHeader) {
		mDrawingListUnderStickyHeader = drawingListUnderStickyHeader;
	}

	public boolean isDrawingListUnderStickyHeader() {
		return mDrawingListUnderStickyHeader;
	}

	public int getScreenWidth() {
		return this.mScreenWidth;
	}

	public void setOnItemClickedListener(ItemClickedListener listener) {
		mailAdapter.setOnItemClickedListener(listener);

	}

	public void setOnItemHeaderClickedListener(ItemHeaderClickedListener listener) {
		mailAdapter.setOnItemHeaderClickedListener(listener);
	}

	public MailAdapter getMailAdapter() {
		return mailAdapter;
	}

}
