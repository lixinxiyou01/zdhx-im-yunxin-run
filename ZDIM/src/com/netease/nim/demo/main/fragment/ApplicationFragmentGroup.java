package com.netease.nim.demo.main.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.fragment.TFragment;

import java.util.ArrayList;
import java.util.List;

import zhwx.common.model.AppGroup;
import zhwx.common.model.Apps;
import zhwx.common.util.IntentUtil;
import zhwx.common.util.StringUtil;
import zhwx.common.util.Tools;


public class ApplicationFragmentGroup extends TFragment {

	private Activity context;

	private List<Apps> apps = new ArrayList<Apps>();

	private String[] appCode; //应用权限
	
    private String appCodeString = "";

//	private String addCode = ",rm";

	private String addCode = "";

	private List<AppGroup> appGroups = new ArrayList<>();

	private List<AppGroup> currAppGroups = new ArrayList<>();

	private LinearLayout appGroupContener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		if (StringUtil.isNotBlank(ECApplication.getInstance().getCurrentIMUser().getAppCode())) {
			appCodeString = ECApplication.getInstance().getCurrentIMUser().getAppCode();
			appCodeString = appCodeString + addCode;
			appCode = appCodeString.split(",");
			for (int i = 0; i < appCode.length; i++) {
				System.out.println(i + appCode[i]);
				if (Apps.getApp(appCode[i]) != null) {
					apps.add(Apps.getApp(appCode[i]));
				}
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_application_group, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		BuildAppGrid();
	}

	public void BuildAppGrid(){
		if (appCode ==null || appCode.length==0) {
			return;
		}
		appGroupContener = findView(R.id.appGroupContener);
        appGroupContener.removeAllViews();
        appGroups = Apps.getAppGroupList();
		currAppGroups.clear();
		for (int i = 0; i < appGroups.size(); i++) {
			AppGroup aag = new AppGroup(appGroups.get(i).getGroupName(),appGroups.get(i).getGroupCode());
			List<Apps> apps = new ArrayList<Apps>();

			for (int i1 = 0; i1 < appCode.length; i1++) {
				for (int i2 = 0; i2 < appGroups.get(i).getApps().size(); i2++) {
					if(appCode[i1].equals(appGroups.get(i).getApps().get(i2).getCode())) {
						apps.add(appGroups.get(i).getApps().get(i2));
					}
				}
			}
			aag.setApps(apps);
			currAppGroups.add(aag);
		}
		for (int i = 0; i < currAppGroups.size(); i++) {
			//本组有APP
			if(currAppGroups.get(i).getApps().size() > 0) {
				View titleView = LayoutInflater.from(getActivity()).inflate(R.layout.app_group_lay, null);
				TextView groupNameTV = (TextView) titleView.findViewById(R.id.groupNameTV);
				groupNameTV.setText(currAppGroups.get(i).getGroupName());
				appGroupContener.addView(titleView);

				GridView gridView = new GridView(context);
				gridView.setNumColumns(4);
				gridView.setAdapter(new MyAdapter(currAppGroups.get(i).getApps()));
				addListener(gridView);
				appGroupContener.addView(gridView);
				Tools.setGridViewHeightBasedOnChildren4(gridView);
			}
		}
	}

	public void addListener(GridView gv) {
		gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
					Apps app = (Apps) parent.getAdapter().getItem(position);
					String kind = app.getCode(); // 应用编码
					IntentUtil.openApp(context,kind);
				}
			});
	}

    class MyAdapter extends BaseAdapter {

		private List<Apps> groupApps = new ArrayList<>();


		public MyAdapter(List<Apps> app) {
			this.groupApps = app;
		}

		@Override
        public int getCount() {
            return groupApps.size();
        }

        @Override
        public Apps getItem(int position) {
            return groupApps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(getActivity()).inflate(R.layout.item_nmain_gv, null);
                AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,  LinearLayout.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(param);
                viewHolder.tv_title = (TextView) view.findViewById(R.id.tv_title);
                viewHolder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.tv_title.setText(getItem(position).getName());
            viewHolder.iv_icon.setImageResource(getItem(position).getIcon());
            return view;
        }

    }

    final static class ViewHolder {
        TextView tv_title;
        ImageView iv_icon;
    }


	@Override
	public void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
	}
}
