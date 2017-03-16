package zhwx.common.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Tools {
	// girdview
	public static void setGirdViewHeightBasedOnChildren(Context context,
			GridView gridView) {
		ListAdapter gridAdapter = gridView.getAdapter();
		if (gridAdapter == null) {
			return;
		}
		int totalHeight = 0;
		int itemHeight = DensityUtil.dip2px(120);
		System.out.println(gridAdapter.getCount() + "Q!");
		totalHeight = itemHeight * (gridAdapter.getCount() / 4 + (gridAdapter.getCount() % 4 == 0 ? 0
						: 1));
		LayoutParams params = gridView.getLayoutParams();
		params.height = totalHeight;
		((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
		gridView.setLayoutParams(params);
	}

	// girdview
	public static void setGirdViewHeightBasedOnChildrenNoImg(Context context,
			GridView gridView) {
		ListAdapter gridAdapter = gridView.getAdapter();
		if (gridAdapter == null) {
			return;
		}
		int totalHeight = 0;
		int itemHeight = DensityUtil.dip2px(40);
		System.out.println(gridAdapter.getCount() + "Q!");
		totalHeight = itemHeight
				* (gridAdapter.getCount() / 5 + (gridAdapter.getCount() % 5 == 0 ? 0
						: 1));
		LayoutParams params = gridView.getLayoutParams();
		params.height = totalHeight;
		((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
		gridView.setLayoutParams(params);
	}

	// girdview
	public static void setGirdViewHeightBasedOnChildrenHotWord(Context context,
			GridView gridView) {
		ListAdapter gridAdapter = gridView.getAdapter();
		if (gridAdapter == null) {
			return;
		}
		int totalHeight = 0;
		int itemHeight = DensityUtil.dip2px(54);
		System.out.println(gridAdapter.getCount() + "Q!");
		totalHeight = itemHeight
				* (gridAdapter.getCount() / 3 + (gridAdapter.getCount() % 3 == 0 ? 0
						: 1));
		LayoutParams params = gridView.getLayoutParams();
		params.height = totalHeight;
		((MarginLayoutParams) params).setMargins(DensityUtil.dip2px(10),
				DensityUtil.dip2px(10), DensityUtil.dip2px(10),
				DensityUtil.dip2px(10));
		gridView.setLayoutParams(params);
	}

	// expandlistview
	public static void setExpandListViewHeightBasedOnChildren(
			ExpandableListView listView) {
		ExpandableListAdapter gridAdapter = listView.getExpandableListAdapter();
		if (gridAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < gridAdapter.getGroupCount(); i++) {
			View listItem = gridAdapter.getGroupView(i, false, null, listView);
			listItem.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		LayoutParams params = listView.getLayoutParams();
		params.height = 50
				+ totalHeight
				+ (listView.getDividerHeight() * (gridAdapter.getGroupCount() - 1));
		((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
		listView.setLayoutParams(params);
	}

	public static void setGridViewHeightBasedOnChildren2(GridView listView) {
		// 获取listview的adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		// 固定列宽，有多少列
		int col = 2;// listView.getNumColumns();
		int totalHeight = 0;
		// i每次加4，相当于listAdapter.getCount()小于等于col时 循环一次，计算一次item的高度，
		for (int i = 0; i < listAdapter.getCount(); i += col) {
			// 获取listview的每一个item
			View listItem = listAdapter.getView(i, null, listView);
			if (listItem != null) {
				listItem.setLayoutParams(new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				listItem.measure(0, 0);
				// 获取item的高度和
				totalHeight += listItem.getMeasuredHeight();
			}
		}
		// 获取listview的布局参数
		LayoutParams params = listView.getLayoutParams();
		// 设置高度
		params.height = totalHeight + (listAdapter.getCount() / 2 + 1) * 20;
		// 设置margin
		((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
		// 设置参数
		listView.setLayoutParams(params);
	}

	public static void setGridViewHeightBasedOnChildren3(GridView listView) {
		// 获取listview的adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		// 固定列宽，有多少列
		int col = 3;// listView.getNumColumns();
		int totalHeight = 0;
		// i每次加4，相当于listAdapter.getCount()小于等于col时 循环一次，计算一次item的高度，
		for (int i = 0; i < listAdapter.getCount(); i += col) {
			// 获取listview的每一个item
			View listItem = listAdapter.getView(i, null, listView);
			if (listItem != null) {
				listItem.setLayoutParams(new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				listItem.measure(0, 0);
				// 获取item的高度和
				totalHeight += listItem.getMeasuredHeight();
			}
		}
		// 获取listview的布局参数
		LayoutParams params = listView.getLayoutParams();
		// 设置高度
		params.height = totalHeight + (listAdapter.getCount() / 2 + 1) * 20;
		// 设置margin
		((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
		// 设置参数
		listView.setLayoutParams(params);
	}

	public static void setGridViewHeightBasedOnChildren4(GridView listView) {
		// 获取listview的adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		// 固定列宽，有多少列
		int col = 4;// listView.getNumColumns();
		int totalHeight = 0;
		// i每次加4，相当于listAdapter.getCount()小于等于col时 循环一次，计算一次item的高度，
		for (int i = 0; i < listAdapter.getCount(); i += col) {
			// 获取listview的每一个item
			View listItem = listAdapter.getView(i, null, listView);
			if (listItem != null) {
				listItem.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				listItem.measure(0, 0);
				// 获取item的高度和
				totalHeight += listItem.getMeasuredHeight();
			}
		}
		// 获取listview的布局参数
		LayoutParams params = listView.getLayoutParams();
		// 设置高度
		params.height = totalHeight;
		// 设置margin
//		((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
		// 设置参数
		listView.setLayoutParams(params);
	}

	/**
	 * 动态改变ListView 高度
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {// 获取ListView对应的Adapter

		ListAdapter listAdapter = listView.getAdapter();

		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;

		for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目

			View listItem = listAdapter.getView(i, null, listView);
			listItem.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度

		}

		LayoutParams params = listView.getLayoutParams();

		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		// listView.getDividerHeight()获取子项间分隔符占用的高度

		// params.height最后得到整个ListView完整显示需要的高度

		listView.setLayoutParams(params);
	}
}
