package zhwx.ui.dcapp.takecourse.listviewgroup;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import java.util.LinkedList;
import java.util.List;

import zhwx.ui.dcapp.takecourse.listviewgroup.interfaces.ItemHeaderClickedListener;


/**
 * A {@link ListAdapter} which wraps a {@link StickyListHeadersAdapter} and automatically handles wrapping the
 * result of {@link StickyListHeadersAdapter#getView(int, View, ViewGroup)} and
 * {@link StickyListHeadersAdapter#getHeaderView(int, View, ViewGroup)}
 * appropriately.
 * 
 * @author Jake Wharton (jakewharton@gmail.com)
 */
public class AdapterWrapper extends BaseAdapter implements StickyListHeadersAdapter {

	public final StickyListHeadersAdapter mAdapterListHeader;
	private final List<View> mHeaderCache = new LinkedList<View>();
	private final Context mContext;
	private Drawable mDivider;
	private int mDividerHeight;
	private ItemHeaderClickedListener mOnHeaderClickListener;
	private DataSetObserver mDataSetObserver = new DataSetObserver() {

		@Override
		public void onInvalidated() {
			mHeaderCache.clear();
			AdapterWrapper.super.notifyDataSetInvalidated();
		}

		@Override
		public void onChanged() {
			AdapterWrapper.super.notifyDataSetChanged();
		}
	};

	public AdapterWrapper(Context context, StickyListHeadersAdapter delegate) {
		this.mContext = context;
		this.mAdapterListHeader = delegate;
		delegate.registerDataSetObserver(mDataSetObserver);
	}

	public void setDivider(Drawable divider) {
		this.mDivider = divider;
	}

	public  void setDividerHeight(int dividerHeight) {
		this.mDividerHeight = dividerHeight;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return mAdapterListHeader.areAllItemsEnabled();
	}

	@Override
	public boolean isEnabled(int position) {
		return mAdapterListHeader.isEnabled(position);
	}

	@Override
	public int getCount() {
		return mAdapterListHeader.getCount();
	}

	@Override
	public Object getItem(int position) {
		return mAdapterListHeader.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return mAdapterListHeader.getItemId(position);
	}

	@Override
	public boolean hasStableIds() {
		return mAdapterListHeader.hasStableIds();
	}

	@Override
	public int getItemViewType(int position) {
		return mAdapterListHeader.getItemViewType(position);
	}

	@Override
	public int getViewTypeCount() {
		return mAdapterListHeader.getViewTypeCount();
	}

	@Override
	public boolean isEmpty() {
		return mAdapterListHeader.isEmpty();
	}

	/**
	 * Will recycle header from {@link WrapperView} if it exists
	 */
	private void recycleHeaderIfExists(WrapperView wv) {
		View header = wv.mHeader;
		if (header != null) {
			mHeaderCache.add(header);
		}
	}

	/**
	 * Get a header view. This optionally pulls a header from the supplied {@link WrapperView} and will also
	 * recycle the divider if it exists.
	 */
	private View configureHeader(WrapperView wv, final int position) {
		View header = wv.mHeader == null ? popHeader() : wv.mHeader;
		header = mAdapterListHeader.getHeaderView(position, header, wv);
		if (header == null) {
			throw new NullPointerException("Header view must not be null.");
		}
		// if the header isn't clickable, the listselector will be drawn on top of the header
		header.setClickable(true);
		header.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnHeaderClickListener != null) {
					long headerId = mAdapterListHeader.getHeaderId(position);
					mOnHeaderClickListener.onItemHeaderClick(v, position, headerId);
				}
			}
		});

		return header;
	}

	private View popHeader() {
		if (mHeaderCache.size() > 0) {
			return mHeaderCache.remove(0);
		}
		return null;
	}

	/** Returns {@code true} if the previous position has the same header ID. */
	private boolean previousPositionHasSameHeader(int position) {
		return position != 0 && mAdapterListHeader.getHeaderId(position) == mAdapterListHeader.getHeaderId(position - 1);
	}

	@Override
	public WrapperView getView(int position, View convertView, ViewGroup parent) {
		WrapperView wv = (convertView == null) ? new WrapperView(mContext) : (WrapperView) convertView;
		View item = mAdapterListHeader.getView(position, wv.mItem, wv);
		View header = null;
		if (previousPositionHasSameHeader(position)) {
			recycleHeaderIfExists(wv);
		} else {
			header = configureHeader(wv, position);
		}
		// if ((item instanceof Checkable) && !(wv instanceof CheckableWrapperView)) {
		// // Need to create Checkable subclass of WrapperView for ListView to work correctly
		// wv = new CheckableWrapperView(mContext);
		// } else if (!(item instanceof Checkable) && (wv instanceof CheckableWrapperView)) {
		// wv = new WrapperView(mContext);
		// }
		wv.update(item, header, mDivider, mDividerHeight);
		return wv;
	}

	public void setOnHeaderClickListener(ItemHeaderClickedListener onHeaderClickListener) {
		this.mOnHeaderClickListener = onHeaderClickListener;
	}

	@Override
	public boolean equals(Object o) {
		return mAdapterListHeader.equals(o);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return ((BaseAdapter) mAdapterListHeader).getDropDownView(position, convertView, parent);
	}

	@Override
	public int hashCode() {
		return mAdapterListHeader.hashCode();
	}

	@Override
	public void notifyDataSetChanged() {
		((BaseAdapter) mAdapterListHeader).notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetInvalidated() {
		((BaseAdapter) mAdapterListHeader).notifyDataSetInvalidated();
	}

	@Override
	public String toString() {
		return mAdapterListHeader.toString();
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		return mAdapterListHeader.getHeaderView(position, convertView, parent);
	}

	@Override
	public long getHeaderId(int position) {
		return mAdapterListHeader.getHeaderId(position);
	}

}
