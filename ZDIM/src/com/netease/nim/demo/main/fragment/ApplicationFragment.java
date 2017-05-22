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
import android.widget.AdapterView.OnItemClickListener;
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
import zhwx.common.util.SharPreUtil;
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


public class ApplicationFragment extends TFragment {

	private Activity context;

	private GridView appGV;

	private MyAdapter adapter;
	
	private List<Apps> apps = new ArrayList<Apps>();

	private String[] appCode; //应用权限
	
	private String[] appCodeStore;//存储的排序

    private String appCodeString = "";

	private String addCode = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_application, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		if (StringUtil.isNotBlank(ECApplication.getInstance().getCurrentIMUser().getAppCode())) {
			appCodeString = ECApplication.getInstance().getCurrentIMUser().getAppCode();
			appCodeString += addCode;
			appCode = appCodeString.split(",");
			appCodeStore = SharPreUtil.getField(ECApplication.getInstance().getCurrentIMUser().getId() + "appCode").split(",");
			//构建个人存储的应用顺序
			//1、先把当前获取到的应用列表中在存储序列中已有的排序
			for (int i = 0; i < appCodeStore.length; i++) {
				for (int j = 0; j < appCode.length; j++) {
					if (appCodeStore[i].equals(appCode[j])) {
						if (Apps.getApp(appCode[j]) != null) {
							apps.add(Apps.getApp(appCode[j]));
						}
						break;
					}
				}
			}
			//2、不在存储序列中的按获取顺序排列在后面
			int size = apps.size();
			for (int i = 0; i < appCode.length; i++) {
				boolean isContent = false;
				for (int j = 0; j < size; j++) {
					if (appCode[i].equals(apps.get(j).getCode())) {
						isContent = true;
						break;
					}
				}
				if (!isContent) {
					if (Apps.getApp(appCode[i]) != null) {
						apps.add(Apps.getApp(appCode[i]));
					}
				}
			}
			appGV = (GridView) getView().findViewById(R.id.gridView1);
			appGV.setFocusable(false); //禁止gridview获取焦点 防止加载后滚动到底部
			adapter = new MyAdapter();
			appGV.setAdapter(adapter);
			Tools.setGridViewHeightBasedOnChildren4(appGV);
			appGV.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,int position, long arg3) {
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

					} else if (V3NoticeCenter.NOTICE_KIND_XUELETANG.equals(kind)) {  //青蚕学堂
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

					} else if (V3NoticeCenter.NOTICE_KIND_TECH_MANAGE.equals(kind)) { // 科研管理
						V3NoticeCenter center = new V3NoticeCenter();
						center.setKind(V3NoticeCenter.NOTICE_KIND_TECH_MANAGE);
						startActivity(IntentUtil.getStartAppIntent(context, center));

					}else if (V3NoticeCenter.NOTICE_KIND_HOMEWORK.equals(kind)) {   //作业1
						startActivity(new Intent(context, StudentHomeWorkListActivity.class));

					} else if (V3NoticeCenter.NOTICE_KIND_HOMEWORK_DC.equals(kind)) {//作业2
						startActivity(new Intent(context, StudentHomeWorkListActivity.class));

					} else if (V3NoticeCenter.NOTICE_KIND_NOTICE.equals(kind)) { // 我的通知
						startActivity(new Intent(context, NoticeActivity.class));

					} else if (V3NoticeCenter.NOTICE_KIND_CARMANAGE.equals(kind)) { // 订车管理
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
	}

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return apps.size();
        }

        @Override
        public Apps getItem(int position) {
            return apps.get(position);
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
