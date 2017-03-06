package com.netease.nim.demo.main.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zhwx.Constant;
import zhwx.common.model.Apps;
import zhwx.common.model.ParameterValue;
import zhwx.common.model.V3NoticeCenter;
import zhwx.common.util.IntentUtil;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.Tools;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.ui.dcapp.assets.AMainActivity;
import zhwx.ui.dcapp.carmanage.CMainActivity;
import zhwx.ui.dcapp.checkin.CIMainActivity;
import zhwx.ui.dcapp.homework.StudentHomeWorkListActivity;
import zhwx.ui.dcapp.score.MyScoreActivity;
import zhwx.ui.dcapp.storeroom.SMainActivity;
import zhwx.ui.dcapp.takecourse.TackCourseIndexActivity;
import zhwx.ui.imapp.notice.NoticeActivity;
import zhwx.ui.webapp.WebAppActivity;


public class ApplicationFragmentGroup extends TFragment {

	private Activity context;

	private List<Apps> apps = new ArrayList<Apps>();

	private String[] appCode; //应用权限
	
    private String appCodeString = "";

	private String addCode = "";

	private List<Apps.AppGroup> appGroups = new ArrayList<>();

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
		//把集合中没有的App移除
		for (int i = 0; i < appGroups.size(); i++) {
			for (int i1 = appGroups.get(i).getApps().size() - 1; i1 >= 0; i1--) {
				boolean gKey = false;
				for (int i2 = 0; i2 < appCode.length; i2++) {
					if(appCode[i2].equals(appGroups.get(i).getApps().get(i1).getCode())) {
						gKey = true;
						break;
					}
				}
				if (!gKey) {
					appGroups.get(i).getApps().remove(i1);
				}
			}
			//本组有APP
			if(appGroups.get(i).getApps().size() > 0) {
				View titleView = LayoutInflater.from(getActivity()).inflate(R.layout.app_group_lay, null);
				TextView groupNameTV = (TextView) titleView.findViewById(R.id.groupNameTV);
				groupNameTV.setText(appGroups.get(i).getGroupName());
				appGroupContener.addView(titleView);

				GridView gridView = new GridView(context);
				gridView.setNumColumns(4);
				gridView.setAdapter(new MyAdapter(appGroups.get(i).getApps()));
				addListener(gridView);
				appGroupContener.addView(gridView);
				Tools.setGridViewHeightBasedOnChildren(gridView);
			}
		}
	}

	public void addListener(GridView gv) {
		gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
					Apps app = (Apps) parent.getAdapter().getItem(position);
					String kind = app.getCode(); // 应用编码
					if (V3NoticeCenter.NOTICE_KIND_NEWS.equals(kind)) { // "新闻"
						startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", ECApplication.getInstance().getV3Address()
								+ Constant.WEBAPP_URL_NEWS));

					} else if (V3NoticeCenter.NOTICE_KIND_WEEKCALENDAR
							.equals(kind)) { // 周历
						Map<String, ParameterValue> map = new HashMap<String, ParameterValue>();
						map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
						String url = UrlUtil.getUrl(ECApplication.getInstance().getV3Address() + Constant.WEBAPP_URL_WEEKCALENDAR, map);
						startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));

					} else if (V3NoticeCenter.NOTICE_KIND_PUBLIC.equals(kind)) { // 公示
						Map<String, ParameterValue> map = new HashMap<String, ParameterValue>();
						map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
						String url = UrlUtil.getUrl(ECApplication.getInstance().getV3Address() + Constant.WEBAPP_URL_PUBLICITY, map);
						startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));

					} else if (V3NoticeCenter.NOTICE_KIND_WAGE.equals(kind)) { // 我的工资
						Map<String, ParameterValue> map = new HashMap<String, ParameterValue>();
						map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
						String url = UrlUtil.getUrl(ECApplication.getInstance().getV3Address()+ Constant.WEBAPP_URL_WAGE, map);
						startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));

					} else if (V3NoticeCenter.NOTICE_KIND_WAGE2.equals(kind)) { // 工资
						// （二级）
						Map<String, ParameterValue> map = new HashMap<String, ParameterValue>();
						map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
						String url = UrlUtil.getUrl(ECApplication.getInstance().getAddress() + Constant.WEBAPP_URL_WAGE2, map);
						startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));

					} else if (V3NoticeCenter.NOTICE_KIND_COURSE.equals(kind)) { // 查看课表
						Map<String, ParameterValue> map = new HashMap<String, ParameterValue>();
						map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
						String url = UrlUtil.getUrl(ECApplication.getInstance().getV3Address() + Constant.WEBAPP_URL_VIEWCOURSEMOBILE, map);
						startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));

					} else if (V3NoticeCenter.NOTICE_KIND_CAMPUSBULLETIN.equals(kind)) { // 校园公告
						Map<String, ParameterValue> map = new HashMap<String, ParameterValue>();
						map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
						String url = UrlUtil.getUrl(ECApplication.getInstance().getAddress() + Constant.WEBAPP_URL_CAMPUSBULLETIN, map);
						startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));

					}  else if (V3NoticeCenter.NOTICE_KIND_QUESTION.equals(kind)) { // 调查问卷
						Map<String, ParameterValue> map = new HashMap<String, ParameterValue>();
						map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
						String url = UrlUtil.getUrl(ECApplication.getInstance().getAddress() + Constant.WEBAPP_URL_QUESTION, map);
						startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));

					} else if (V3NoticeCenter.NOTICE_KIND_ANNOUNCEMENT.equals(kind)) { // 系统公告
						Map<String, ParameterValue> map = new HashMap<String, ParameterValue>();
						map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
						String url = UrlUtil.getUrl(ECApplication.getInstance()
								.getAddress()+ Constant.WEBAPP_URL_ANNOUNCEMENT, map);
						startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));

					} else if (V3NoticeCenter.NOTICE_KIND_MESS.equals(kind)) { // 食堂管理
						Map<String, ParameterValue> map = new HashMap<String, ParameterValue>();
						map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
						String url = UrlUtil.getUrl(ECApplication.getInstance().getAddress() + Constant.WEBAPP_URL_MESS, map);
						startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));

					} else if (V3NoticeCenter.NOTICE_KIND_HOMEWORK_CJL.equals(kind)) { //陈经纶作业
						Map<String, ParameterValue> map = new HashMap<String, ParameterValue>();
						map.put("operationCode", new ParameterValue("il_statistics"));
						map.put("sys_username", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getLoginName()));
						map.put("sys_password", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Pwd()));
						map.put("sys_auto_authenticate", new ParameterValue("true"));
						String url = UrlUtil.getUrl(ECApplication.getInstance().getV3Address() + Constant.WEBAPP_URL_HOMEWORK_CJL, map);
						startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));

					}  else if (V3NoticeCenter.NOTICE_KIND_SCORE.equals(kind)) {  //查看成绩
						startActivity(new Intent(context, MyScoreActivity.class));

					} else if (V3NoticeCenter.NOTICE_KIND_TACKCOUSE.equals(kind)) {  //选课
						startActivity(new Intent(context, TackCourseIndexActivity.class));

					} else if (V3NoticeCenter.NOTICE_KIND_CHECKIN.equals(kind)) {  //考勤
						startActivity(new Intent(context, CIMainActivity.class));

					} else if (V3NoticeCenter.NOTICE_KIND_XUELETANG.equals(kind)) {  //学乐堂
						try {
							Intent intent = new Intent();
							PackageManager packageManager = context.getPackageManager();
							intent = packageManager.getLaunchIntentForPackage("com.lanxum.hzth.intellectualclass");
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
									| Intent.FLAG_ACTIVITY_CLEAR_TOP);
							context.startActivity(intent);
						} catch (Exception e) {
							e.printStackTrace();
							ECAlertDialog buildAlert = ECAlertDialog.buildAlert(context, R.string.intent_xlt_opendownload, null, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									Uri uri = Uri.parse("https://www.pgyer.com/xlt-android");
									Intent intent = new Intent(Intent.ACTION_VIEW, uri);
									context.startActivity(intent);
								}
							});
							buildAlert.setTitle("提示");
							buildAlert.show();
						}

					} else if (V3NoticeCenter.NOTICE_KIND_TECH_MANAGE.equals(kind)) { //科研管理
						V3NoticeCenter center = new V3NoticeCenter();
						center.setKind(V3NoticeCenter.NOTICE_KIND_TECH_MANAGE);
						startActivity(IntentUtil.getStartAppIntent(context, center));

					}else if (V3NoticeCenter.NOTICE_KIND_HOMEWORK.equals(kind)) {   //作业本地
						startActivity(new Intent(context, StudentHomeWorkListActivity.class));

					} else if (V3NoticeCenter.NOTICE_KIND_HOMEWORK_DC.equals(kind)) { //作业数校
						startActivity(new Intent(context, StudentHomeWorkListActivity.class));

					} else if (V3NoticeCenter.NOTICE_KIND_NOTICE.equals(kind)) { //我的通知
						startActivity(new Intent(context, NoticeActivity.class));

					} else if (V3NoticeCenter.NOTICE_KIND_CARMANAGE.equals(kind)) { //订车管理
						startActivity(new Intent(context, CMainActivity.class));

					} else if (V3NoticeCenter.NOTICE_KIND_ASSETS.equals(kind)) { // 资产管理
						startActivity(new Intent(context, AMainActivity.class));

					} else if (V3NoticeCenter.NOTICE_KIND_STORE.equals(kind)) { // 库房管理
						startActivity(new Intent(context, SMainActivity.class));

					}  else {
						ToastUtil.showMessage("研发中...");
					}
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
