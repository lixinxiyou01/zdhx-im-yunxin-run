package zhwx.common.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.ConstantForUikit;
import com.netease.nim.uikit.common.util.file.FileUtil;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import zhwx.Constant;
import zhwx.common.model.ParameterValue;
import zhwx.common.model.V3NoticeCenter;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.ui.dcapp.assets.AMainActivity;
import zhwx.ui.dcapp.carmanage.CMainActivity;
import zhwx.ui.dcapp.checkin.CIMainActivity;
import zhwx.ui.dcapp.homework.StudentHomeWorkDetailsActivity;
import zhwx.ui.dcapp.homework.StudentHomeWorkListActivity;
import zhwx.ui.dcapp.repairs.RMainActivity;
import zhwx.ui.dcapp.score.MyScoreActivity;
import zhwx.ui.dcapp.storeroom.SMainActivity;
import zhwx.ui.dcapp.takecourse.TackCourseIndexActivity;
import zhwx.ui.imapp.notice.NoticeActivity;
import zhwx.ui.imapp.notice.NoticeDetailActivity;
import zhwx.ui.webapp.WebAppActivity;

import static com.netease.nim.uikit.contact_selector.activity.ContactSelectActivity.context;

//自定义android Intent类，

//可用于获取打开以下文件的intent

//PDF,PPT,WORD,EXCEL,CHM,HTML,TEXT,AUDIO,VIDEO

public class IntentUtil {
	public static void openFile(Context context, FileAttachment fileAttachment) {

		String lastName = FileUtil.getExtensionName(fileAttachment.getDisplayName());

		String path = fileAttachment.getPath();

		try {
			if(ConstantForUikit.ATTACHMENT_DOC.equals(lastName)){
                context.startActivity(IntentUtil.getWordFileIntent(path));

            }else if(ConstantForUikit.ATTACHMENT_DOCX.equals(lastName)){
                context.startActivity(IntentUtil.getWordFileIntent(path));

            }else if(ConstantForUikit.ATTACHMENT_PPT.equals(lastName)){
                context.startActivity(IntentUtil.getPptFileIntent(path));

            }else if(ConstantForUikit.ATTACHMENT_PPTX.equals(lastName)){
                context.startActivity(IntentUtil.getPptFileIntent(path));

            }else if(ConstantForUikit.ATTACHMENT_XLS.equals(lastName)){
                context.startActivity(IntentUtil.getExcelFileIntent(path));

            }else if(ConstantForUikit.ATTACHMENT_XLSX.equals(lastName)){
                context.startActivity(IntentUtil.getExcelFileIntent(path));

            }else if(ConstantForUikit.ATTACHMENT_PDF.equals(lastName)){
                context.startActivity(IntentUtil.getPdfFileIntent(path));

            }else if(ConstantForUikit.ATTACHMENT_TXT.equals(lastName)){
                context.startActivity(IntentUtil.getTextFileIntent(path, false));

            }else if(ConstantForUikit.ATTACHMENT_JPG.equals(lastName)){
				context.startActivity(IntentUtil.getImageFileIntent(path));

			}else if(ConstantForUikit.ATTACHMENT_JPEG.equals(lastName)){
				context.startActivity(IntentUtil.getImageFileIntent(path));

			}else if(ConstantForUikit.ATTACHMENT_PNG.equals(lastName)){
                context.startActivity(IntentUtil.getImageFileIntent(path));

            }else if(ConstantForUikit.ATTACHMENT_GIF.equals(lastName)){
                context.startActivity(IntentUtil.getImageFileIntent(path));

            }else{
                context.startActivity(IntentUtil.getAllFileIntent(path));
            }
		} catch (Exception e) {
			Toast.makeText(context, "未安装打开此类文件的软件", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}





	// android获取一个用于打开HTML文件的intent

	public static Intent getHtmlFileIntent(String param)

	{

		Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param).build();

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.setDataAndType(uri, "text/html");

		return intent;

	}

	// android获取一个用于打开图片文件的intent

	public static Intent getImageFileIntent(String param)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Uri uri = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(param));

		intent.setDataAndType(uri, "image/*");

		return intent;

	}

	// android获取一个用于打开PDF文件的intent

	public static Intent getPdfFileIntent(String param)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Uri uri = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(param));

		intent.setDataAndType(uri, "application/pdf");

		return intent;

	}

	// android获取一个用于打开文本文件的intent

	public static Intent getTextFileIntent(String paramString, boolean paramBoolean)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		if (paramBoolean)

		{

			Uri uri1 = Uri.parse(paramString);

			intent.setDataAndType(uri1, "text/plain");

		}

		while (true)

		{

//			Uri uri2 = Uri.fromFile(new File(paramString));
			Uri uri2 = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(paramString));

			intent.setDataAndType(uri2, "text/plain");
			return intent;

		}

	}

	// android获取一个用于打开音频文件的intent

	public static Intent getAudioFileIntent(String param)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		intent.putExtra("oneshot", 0);

		intent.putExtra("configchange", 0);

		Uri uri = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(param));

		intent.setDataAndType(uri, "audio/*");

		return intent;

	}

	// android获取一个用于打开视频文件的intent

	public static Intent getVideoFileIntent(String param)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		intent.putExtra("oneshot", 0);

		intent.putExtra("configchange", 0);

		Uri uri = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(param));

		intent.setDataAndType(uri, "video/*");

		return intent;

	}

	// android获取一个用于打开CHM文件的intent

	public static Intent getChmFileIntent(String param)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Uri uri = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(param));

		intent.setDataAndType(uri, "application/x-chm");

		return intent;

	}

	// android获取一个用于打开Word文件的intent

	public static Intent getWordFileIntent(String param)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(param));
//		Uri uri = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(param));

		intent.setDataAndType(uri, "application/msword");

		return intent;

	}

	// android获取一个用于打开Excel文件的intent

	public static Intent getExcelFileIntent(String param)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Uri uri = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(param));

		intent.setDataAndType(uri, "application/vnd.ms-excel");

		return intent;

	}

	// android获取一个用于打开PPT文件的intent

	public static Intent getPptFileIntent(String param)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Uri uri = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(param));

		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");

		return intent;

	}

	// android获取一个用于打开PPT文件的intent

	public static Intent getAllFileIntent(String param)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Uri uri = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(param));

		intent.setDataAndType(uri, "*/*");

		return intent;
	}


	/** 根据消息内容获取启动App的Intent */

	public static Intent getStartAppIntent(Context context,V3NoticeCenter v3NoticeCenter) {

		Intent intent = null;


		if (StringUtil.isNotBlank(v3NoticeCenter.getKind())) { //通知

			if (v3NoticeCenter.getKind().equals(V3NoticeCenter.NOTICE_KIND_NOTICE)) {

				intent = new Intent(context, NoticeDetailActivity.class);

				intent.putExtra("noticeId", v3NoticeCenter.getSourceId());

			}else if (v3NoticeCenter.getKind().equals(V3NoticeCenter.NOTICE_KIND_ZMAIL)) { //Z邮

				intent = new Intent(context, NoticeDetailActivity.class);

				intent.putExtra("noticeId", v3NoticeCenter.getSourceId());

			} else if (v3NoticeCenter.getKind().equals(V3NoticeCenter.NOTICE_KIND_WEEKCALENDAR)) { //周历

				intent = new Intent(context,WebAppActivity.class);

				intent.putExtra("webUrl",  ECApplication.getInstance().getV3Address() + Constant.WEBAPP_URL_WEEKCALENDAR
						+ "?sourceId=" + v3NoticeCenter.getSourceId()
						+ "&userId=" + ECApplication.getInstance().getCurrentIMUser().getV3Id());

			} else if(v3NoticeCenter.getKind().equals(V3NoticeCenter.NOTICE_KIND_WAGE)) { //工资

				intent = new Intent(context,WebAppActivity.class);

				intent.putExtra("webUrl",  ECApplication.getInstance().getV3Address() + Constant.WEBAPP_URL_WAGE
						+ "?sourceId=" + v3NoticeCenter.getSourceId()
						+ "&userId=" + ECApplication.getInstance().getCurrentIMUser().getV3Id());
			} else if(v3NoticeCenter.getKind().equals(V3NoticeCenter.NOTICE_KIND_WAGE2)) { //工资

				intent = new Intent(context,WebAppActivity.class);

				intent.putExtra("webUrl",  ECApplication.getInstance().getAddress() + Constant.WEBAPP_URL_WAGE2
						+ "?sourceId=" + v3NoticeCenter.getSourceId()
						+ "&userId=" + ECApplication.getInstance().getCurrentIMUser().getId());
			} else if(v3NoticeCenter.getKind().equals(V3NoticeCenter.NOTICE_KIND_CAMPUSBULLETIN)) { //校园公告

				intent = new Intent(context,WebAppActivity.class);

				intent.putExtra("webUrl",  ECApplication.getInstance().getAddress() + Constant.WEBAPP_URL_CAMPUSBULLETIN
						+ "?sourceId=" + v3NoticeCenter.getSourceId()
						+ "&userId=" + ECApplication.getInstance().getCurrentIMUser().getId());
			} else if(v3NoticeCenter.getKind().equals(V3NoticeCenter.NOTICE_KIND_ANNOUNCEMENT)) { //系统公告

				intent = new Intent(context,WebAppActivity.class);

				intent.putExtra("webUrl",  ECApplication.getInstance().getAddress() + Constant.WEBAPP_URL_ANNOUNCEMENT
						+ "?sourceId=" + v3NoticeCenter.getSourceId()
						+ "&userId=" + ECApplication.getInstance().getCurrentIMUser().getId());
			}  else if(v3NoticeCenter.getKind().equals(V3NoticeCenter.NOTICE_KIND_MESS)) { //食堂管理

				intent = new Intent(context,WebAppActivity.class);

				intent.putExtra("webUrl",  ECApplication.getInstance().getAddress() + Constant.WEBAPP_URL_MESS
						+ "?sourceId=" + v3NoticeCenter.getSourceId()
						+ "&userId=" + ECApplication.getInstance().getCurrentIMUser().getId());
			} else if(v3NoticeCenter.getKind().equals(V3NoticeCenter.NOTICE_KIND_TECH_MANAGE)) { //课题管理

				intent = new Intent(context,WebAppActivity.class);

				intent.putExtra("webUrl",  Constant.WEBAPP_URL_TECH_MANAGE+ "?loginName=" + ECApplication.getInstance().getCurrentIMUser().getLoginName());

			} else if(v3NoticeCenter.getKind().equals(V3NoticeCenter.NOTICE_KIND_TECH_JLLT)) { //经纶论坛

				intent = new Intent(context,WebAppActivity.class);
				intent.putExtra("webUrl",  Constant.WEBAPP_URL_TECH_JLLT);

			} else if(v3NoticeCenter.getKind().equals(V3NoticeCenter.NOTICE_KIND_CARMANAGE)) { //车辆管理

				intent = new Intent(context,CMainActivity.class);
			}  else if(v3NoticeCenter.getKind().equals(V3NoticeCenter.NOTICE_KIND_CHECKIN)) { //考 勤

				intent = new Intent(context,CIMainActivity.class);
			} else if(v3NoticeCenter.getKind().equals(V3NoticeCenter.NOTICE_KIND_HOMEWORK_DC)) { //数校作业

				intent = new Intent(context,StudentHomeWorkDetailsActivity.class);

				intent.putExtra("id", v3NoticeCenter.getSourceId());
			} else if(v3NoticeCenter.getKind().equals(V3NoticeCenter.NOTICE_KIND_ASSETS)) { //资产管理

				intent = new Intent(context,AMainActivity.class);
			}else if(v3NoticeCenter.getKind().equals(V3NoticeCenter.NOTICE_KIND_STORE)) { //易耗品管理

				intent = new Intent(context,SMainActivity.class);
			}else if(v3NoticeCenter.getKind().equals(V3NoticeCenter.NOTICE_KIND_REPAIR)){ //报修

				intent = new Intent(context,RMainActivity.class);
			}
		}
		return intent;
	}

	public static void openApp(final Context context, String kind){
		if (V3NoticeCenter.NOTICE_KIND_NEWS.equals(kind)) { // "新闻"
			context.startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", ECApplication.getInstance().getV3Address()
					+ Constant.WEBAPP_URL_NEWS));

		} else if (V3NoticeCenter.NOTICE_KIND_WEEKCALENDAR
				.equals(kind)) { // 周历
			Map<String, ParameterValue> map = new HashMap<String, ParameterValue>();
			map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
			map.put("dataSourceName",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getDataSourceName()));
			String url = UrlUtil.getUrl(ECApplication.getInstance().getV3Address() + Constant.WEBAPP_URL_WEEKCALENDAR, map);
			context.startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));

		} else if (V3NoticeCenter.NOTICE_KIND_PUBLIC.equals(kind)) { // 公示
			Map<String, ParameterValue> map = new HashMap<String, ParameterValue>();
			map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
			map.put("dataSourceName",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getDataSourceName()));
			String url = UrlUtil.getUrl(ECApplication.getInstance().getV3Address() + Constant.WEBAPP_URL_PUBLICITY, map);
			context.startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));

		} else if (V3NoticeCenter.NOTICE_KIND_WAGE.equals(kind)) { // 我的工资
			Map<String, ParameterValue> map = new HashMap<String, ParameterValue>();
			map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
			map.put("dataSourceName",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getDataSourceName()));
			String url = UrlUtil.getUrl(ECApplication.getInstance().getV3Address()+ Constant.WEBAPP_URL_WAGE, map);
			context.startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));

		} else if (V3NoticeCenter.NOTICE_KIND_WAGE2.equals(kind)) { // 工资
			// （二级）
			Map<String, ParameterValue> map = new HashMap<String, ParameterValue>();
			map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
			map.put("dataSourceName",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getDataSourceName()));
			String url = UrlUtil.getUrl(ECApplication.getInstance().getAddress() + Constant.WEBAPP_URL_WAGE2, map);
			context.startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));

		} else if (V3NoticeCenter.NOTICE_KIND_COURSE.equals(kind)) { // 查看课表
			Map<String, ParameterValue> map = new HashMap<String, ParameterValue>();
			map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
			map.put("dataSourceName",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getDataSourceName()));
			String url = UrlUtil.getUrl(ECApplication.getInstance().getV3Address() + Constant.WEBAPP_URL_VIEWCOURSEMOBILE, map);
			context.startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));

		} else if (V3NoticeCenter.NOTICE_KIND_CAMPUSBULLETIN.equals(kind)) { // 校园公告
			Map<String, ParameterValue> map = new HashMap<String, ParameterValue>();
			map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
			map.put("dataSourceName",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getDataSourceName()));
			String url = UrlUtil.getUrl(ECApplication.getInstance().getAddress() + Constant.WEBAPP_URL_CAMPUSBULLETIN, map);
			context.startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));

		}  else if (V3NoticeCenter.NOTICE_KIND_QUESTION.equals(kind)) { // 调查问卷
			Map<String, ParameterValue> map = new HashMap<String, ParameterValue>();
			map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
			map.put("dataSourceName",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getDataSourceName()));
			String url = UrlUtil.getUrl(ECApplication.getInstance().getAddress() + Constant.WEBAPP_URL_QUESTION, map);
			context.startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));

		} else if (V3NoticeCenter.NOTICE_KIND_ANNOUNCEMENT.equals(kind)) { // 系统公告
			Map<String, ParameterValue> map = new HashMap<String, ParameterValue>();
			map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
			map.put("dataSourceName",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getDataSourceName()));
			String url = UrlUtil.getUrl(ECApplication.getInstance()
					.getAddress()+ Constant.WEBAPP_URL_ANNOUNCEMENT, map);
			context.startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));

		} else if (V3NoticeCenter.NOTICE_KIND_MESS.equals(kind)) { // 食堂管理
			Map<String, ParameterValue> map = new HashMap<String, ParameterValue>();
			map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
			map.put("dataSourceName",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getDataSourceName()));
			String url = UrlUtil.getUrl(ECApplication.getInstance().getAddress() + Constant.WEBAPP_URL_MESS, map);
			context.startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));

		} else if (V3NoticeCenter.NOTICE_KIND_HOMEWORK_CJL.equals(kind)) { //陈经纶作业
			Map<String, ParameterValue> map = new HashMap<String, ParameterValue>();
			map.put("operationCode", new ParameterValue("il_statistics"));
			map.put("sys_username", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getLoginName()));
			map.put("sys_password", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Pwd()));
			map.put("sys_auto_authenticate", new ParameterValue("true"));
			map.put("dataSourceName",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getDataSourceName()));
			String url = UrlUtil.getUrl(ECApplication.getInstance().getV3Address() + Constant.WEBAPP_URL_HOMEWORK_CJL, map);
			context.startActivity(new Intent(context, WebAppActivity.class).putExtra("webUrl", url));

		}  else if (V3NoticeCenter.NOTICE_KIND_SCORE.equals(kind)) {  //查看成绩
			context.startActivity(new Intent(context, MyScoreActivity.class));

		} else if (V3NoticeCenter.NOTICE_KIND_TACKCOUSE.equals(kind)) {  //选课
			context.startActivity(new Intent(context, TackCourseIndexActivity.class));

		} else if (V3NoticeCenter.NOTICE_KIND_CHECKIN.equals(kind)) {  //考勤
			context.startActivity(new Intent(context, CIMainActivity.class));

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

		} else if (V3NoticeCenter.NOTICE_KIND_TECH_MANAGE.equals(kind)) { //课题管理
			V3NoticeCenter center = new V3NoticeCenter();
			center.setKind(V3NoticeCenter.NOTICE_KIND_TECH_MANAGE);
			context.startActivity(IntentUtil.getStartAppIntent(context, center));

		} else if (V3NoticeCenter.NOTICE_KIND_TECH_JLLT.equals(kind)) { //经纶论坛
			V3NoticeCenter center = new V3NoticeCenter();
			center.setKind(V3NoticeCenter.NOTICE_KIND_TECH_JLLT);
			context.startActivity(IntentUtil.getStartAppIntent(context, center));

		}else if (V3NoticeCenter.NOTICE_KIND_HOMEWORK.equals(kind)) {   //作业本地
			context.startActivity(new Intent(context, StudentHomeWorkListActivity.class));

		} else if (V3NoticeCenter.NOTICE_KIND_HOMEWORK_DC.equals(kind)) { //作业数校
			context.startActivity(new Intent(context, StudentHomeWorkListActivity.class));

		} else if (V3NoticeCenter.NOTICE_KIND_NOTICE.equals(kind)) { //我的通知
			context.startActivity(new Intent(context, NoticeActivity.class));

		} else if (V3NoticeCenter.NOTICE_KIND_CARMANAGE.equals(kind)) { //订车管理
			context.startActivity(new Intent(context, CMainActivity.class));

		} else if (V3NoticeCenter.NOTICE_KIND_ASSETS.equals(kind)) { // 资产管理
			context.startActivity(new Intent(context, AMainActivity.class));

		} else if (V3NoticeCenter.NOTICE_KIND_STORE.equals(kind)) { // 库房管理
			context.startActivity(new Intent(context, SMainActivity.class));

		} else if (V3NoticeCenter.NOTICE_KIND_REPAIR.equals(kind)) { //报修
			context.startActivity(new Intent(context, RMainActivity.class));

		} else {
			ToastUtil.showMessage("研发中...");
		}
	}
}