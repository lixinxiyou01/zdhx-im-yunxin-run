package com.netease.nim.uikit.common.util.file;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.netease.nim.uikit.ConstantForUikit;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;

import java.io.File;
import java.util.List;

import static com.netease.nim.uikit.contact_selector.activity.ContactSelectActivity.context;

//自定义android Intent类，

//可用于获取打开以下文件的intent

//PDF,PPT,WORD,EXCEL,CHM,HTML,TEXT,AUDIO,VIDEO

public class IntentUtilForUikit {

	private static final String[][] MIME_MapTable={
			//{后缀名， MIME类型}
			{".3gp",    "video/3gpp"},
			{".apk",    "application/vnd.android.package-archive"},
			{".asf",    "video/x-ms-asf"},
			{".avi",    "video/x-msvideo"},
			{".bin",    "application/octet-stream"},
			{".bmp",    "image/bmp"},
			{".c",  "text/plain"},
			{".class",  "application/octet-stream"},
			{".conf",   "text/plain"},
			{".cpp",    "text/plain"},
			{".doc",    "application/msword"},
			{".docx",   "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
			{".xls",    "application/vnd.ms-excel"},
			{".xlsx",   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
			{".exe",    "application/octet-stream"},
			{".gif",    "image/gif"},
			{".gtar",   "application/x-gtar"},
			{".gz", "application/x-gzip"},
			{".h",  "text/plain"},
			{".htm",    "text/html"},
			{".html",   "text/html"},
			{".jar",    "application/java-archive"},
			{".java",   "text/plain"},
			{".jpeg",   "image/jpeg"},
			{".jpg",    "image/jpeg"},
			{".js", "application/x-javascript"},
			{".log",    "text/plain"},
			{".m3u",    "audio/x-mpegurl"},
			{".m4a",    "audio/mp4a-latm"},
			{".m4b",    "audio/mp4a-latm"},
			{".m4p",    "audio/mp4a-latm"},
			{".m4u",    "video/vnd.mpegurl"},
			{".m4v",    "video/x-m4v"},
			{".mov",    "video/quicktime"},
			{".mp2",    "audio/x-mpeg"},
			{".mp3",    "audio/x-mpeg"},
			{".mp4",    "video/mp4"},
			{".mpc",    "application/vnd.mpohun.certificate"},
			{".mpe",    "video/mpeg"},
			{".mpeg",   "video/mpeg"},
			{".mpg",    "video/mpeg"},
			{".mpg4",   "video/mp4"},
			{".mpga",   "audio/mpeg"},
			{".msg",    "application/vnd.ms-outlook"},
			{".ogg",    "audio/ogg"},
			{".pdf",    "application/pdf"},
			{".png",    "image/png"},
			{".pps",    "application/vnd.ms-powerpoint"},
			{".ppt",    "application/vnd.ms-powerpoint"},
			{".pptx",   "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
			{".prop",   "text/plain"},
			{".rc", "text/plain"},
			{".rmvb",   "audio/x-pn-realaudio"},
			{".rtf",    "application/rtf"},
			{".sh", "text/plain"},
			{".tar",    "application/x-tar"},
			{".tgz",    "application/x-compressed"},
			{".txt",    "text/plain"},
			{".wav",    "audio/x-wav"},
			{".wma",    "audio/x-ms-wma"},
			{".wmv",    "audio/x-ms-wmv"},
			{".wps",    "application/vnd.ms-works"},
			{".xml",    "text/plain"},
			{".z",  "application/x-compress"},
			{".zip",    "application/x-zip-compressed"},
			{"",        "*/*"}
	};

	public static void openFile(Context context, FileAttachment fileAttachment) {

		String lastName = FileUtil.getExtensionName(fileAttachment.getDisplayName());

		String path = fileAttachment.getPath();

		try {
			if(ConstantForUikit.ATTACHMENT_DOC.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getWordFileIntent(path,context));

            }else if(ConstantForUikit.ATTACHMENT_DOCX.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getWordFileIntent(path,context));

            }else if(ConstantForUikit.ATTACHMENT_PPT.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getPptFileIntent(path,context));

            }else if(ConstantForUikit.ATTACHMENT_PPTX.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getPptFileIntent(path,context));

            }else if(ConstantForUikit.ATTACHMENT_XLS.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getExcelFileIntent(path,context));

            }else if(ConstantForUikit.ATTACHMENT_XLSX.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getExcelFileIntent(path,context));

            }else if(ConstantForUikit.ATTACHMENT_PDF.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getPdfFileIntent(path,context));

            }else if(ConstantForUikit.ATTACHMENT_TXT.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getTextFileIntent(path, false));

            }else if(ConstantForUikit.ATTACHMENT_JPG.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getImageFileIntent(path,context));

            }else if(ConstantForUikit.ATTACHMENT_PNG.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getImageFileIntent(path,context));

            }else if(ConstantForUikit.ATTACHMENT_GIF.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getImageFileIntent(path,context));

            }else{
                context.startActivity(IntentUtilForUikit.getAllFileIntent(path,context));
            }
		} catch (Exception e) {
			Toast.makeText(context, "未安装打开此类文件的软件", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}





	// android获取一个用于打开HTML文件的intent

	public static Intent getHtmlFileIntent(String param,Context context)

	{

		Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param).build();

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.setDataAndType(uri, "text/html");

		return intent;

	}

	// android获取一个用于打开图片文件的intent

	public static Intent getImageFileIntent(String param,Context context)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			Uri fileUri = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(param));//android 7.0以上
			intent.setDataAndType(fileUri, "image/*");
			grantUriPermission(context, fileUri, intent);
		}else {
			intent.setDataAndType(Uri.fromFile(new File(param)), "image/*");
		}
		return intent;

	}

	// android获取一个用于打开PDF文件的intent

	public static Intent getPdfFileIntent(String param,Context context)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			Uri fileUri = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(param));//android 7.0以上
			intent.setDataAndType(fileUri, "application/pdf");
			grantUriPermission(context, fileUri, intent);
		}else {
			intent.setDataAndType(Uri.fromFile(new File(param)), "application/pdf");
		}

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
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				Uri fileUri = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(paramString));//android 7.0以上
				intent.setDataAndType(fileUri, "text/plain");
				grantUriPermission(context, fileUri, intent);
			}else {
				intent.setDataAndType(Uri.fromFile(new File(paramString)), "text/plain");
			}
			return intent;

		}

	}

	// android获取一个用于打开音频文件的intent

	public static Intent getAudioFileIntent(String param,Context context)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		intent.putExtra("oneshot", 0);

		intent.putExtra("configchange", 0);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			Uri fileUri = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(param));//android 7.0以上
			intent.setDataAndType(fileUri, "audio/*");
			grantUriPermission(context, fileUri, intent);
		}else {
			intent.setDataAndType(Uri.fromFile(new File(param)), "audio/*");
		}

		return intent;

	}

	// android获取一个用于打开视频文件的intent

	public static Intent getVideoFileIntent(String param,Context context)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		intent.putExtra("oneshot", 0);

		intent.putExtra("configchange", 0);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			Uri fileUri = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(param));//android 7.0以上
			intent.setDataAndType(fileUri, "video/*");
			grantUriPermission(context, fileUri, intent);
		}else {
			intent.setDataAndType(Uri.fromFile(new File(param)), "video/*");
		}

		return intent;

	}

	// android获取一个用于打开CHM文件的intent

	public static Intent getChmFileIntent(String param,Context context)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			Uri fileUri = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(param));//android 7.0以上
			intent.setDataAndType(fileUri, "application/x-chm");
			grantUriPermission(context, fileUri, intent);
		}else {
			intent.setDataAndType(Uri.fromFile(new File(param)), "application/x-chm");
		}

		return intent;

	}

	// android获取一个用于打开Word文件的intent

	public static Intent getWordFileIntent(String param,Context context)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			Uri fileUri = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(param));//android 7.0以上
			intent.setDataAndType(fileUri, "application/msword");
			grantUriPermission(context, fileUri, intent);
		}else {
			intent.setDataAndType(Uri.fromFile(new File(param)), "application/msword");
		}
		return intent;

	}

	// android获取一个用于打开Excel文件的intent

	public static Intent getExcelFileIntent(String param,Context context)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			Uri fileUri = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(param));//android 7.0以上
			intent.setDataAndType(fileUri, "application/vnd.ms-excel");
			grantUriPermission(context, fileUri, intent);
		}else {
			intent.setDataAndType(Uri.fromFile(new File(param)), "application/vnd.ms-excel");
		}
		return intent;

	}

	// android获取一个用于打开PPT文件的intent

	public static Intent getPptFileIntent(String param,Context context)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			Uri fileUri = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(param));//android 7.0以上
			intent.setDataAndType(fileUri, "application/vnd.ms-powerpoint");
			grantUriPermission(context, fileUri, intent);
		}else {
			intent.setDataAndType(Uri.fromFile(new File(param)), "application/vnd.ms-powerpoint");
		}

		return intent;

	}
	
	// android获取一个用于打开PPT文件的intent
	
	public static Intent getAllFileIntent(String param,Context context)
	
	{
		
		Intent intent = new Intent("android.intent.action.VIEW");
		
		intent.addCategory("android.intent.category.DEFAULT");
		
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			Uri fileUri = FileProvider.getUriForFile(context, "com.pgyersdk.zdhx.zhwx.provider", new File(param));//android 7.0以上
			intent.setDataAndType(fileUri, "*/*");
			grantUriPermission(context, fileUri, intent);
		}else {
			intent.setDataAndType(Uri.fromFile(new File(param)), "*/*");
		}
		
		return intent;
	}

	private static void grantUriPermission (Context context, Uri fileUri, Intent intent) {
		List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo resolveInfo : resInfoList) {
			String packageName = resolveInfo.activityInfo.packageName;
			context.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}
	}
}