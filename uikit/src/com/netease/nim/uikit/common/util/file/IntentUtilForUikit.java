package com.netease.nim.uikit.common.util.file;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.netease.nim.uikit.ConstantForUikit;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;

import java.io.File;

//自定义android Intent类，

//可用于获取打开以下文件的intent

//PDF,PPT,WORD,EXCEL,CHM,HTML,TEXT,AUDIO,VIDEO

public class IntentUtilForUikit {
	public static void openFile(Context context, FileAttachment fileAttachment) {

		String lastName = FileUtil.getExtensionName(fileAttachment.getDisplayName());

		String path = fileAttachment.getPath();

		try {
			if(ConstantForUikit.ATTACHMENT_DOC.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getWordFileIntent(path));

            }else if(ConstantForUikit.ATTACHMENT_DOCX.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getWordFileIntent(path));

            }else if(ConstantForUikit.ATTACHMENT_PPT.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getPptFileIntent(path));

            }else if(ConstantForUikit.ATTACHMENT_PPTX.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getPptFileIntent(path));

            }else if(ConstantForUikit.ATTACHMENT_XLS.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getExcelFileIntent(path));

            }else if(ConstantForUikit.ATTACHMENT_XLSX.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getExcelFileIntent(path));

            }else if(ConstantForUikit.ATTACHMENT_PDF.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getPdfFileIntent(path));

            }else if(ConstantForUikit.ATTACHMENT_TXT.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getTextFileIntent(path, false));

            }else if(ConstantForUikit.ATTACHMENT_JPG.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getImageFileIntent(path));

            }else if(ConstantForUikit.ATTACHMENT_PNG.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getImageFileIntent(path));

            }else if(ConstantForUikit.ATTACHMENT_GIF.equals(lastName)){
                context.startActivity(IntentUtilForUikit.getImageFileIntent(path));

            }else{
                context.startActivity(IntentUtilForUikit.getAllFileIntent(path));
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

		Uri uri = Uri.fromFile(new File(param));

		intent.setDataAndType(uri, "image/*");

		return intent;

	}

	// android获取一个用于打开PDF文件的intent

	public static Intent getPdfFileIntent(String param)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Uri uri = Uri.fromFile(new File(param));

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

			Uri uri2 = Uri.fromFile(new File(paramString));

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

		Uri uri = Uri.fromFile(new File(param));

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

		Uri uri = Uri.fromFile(new File(param));

		intent.setDataAndType(uri, "video/*");

		return intent;

	}

	// android获取一个用于打开CHM文件的intent

	public static Intent getChmFileIntent(String param)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Uri uri = Uri.fromFile(new File(param));

		intent.setDataAndType(uri, "application/x-chm");

		return intent;

	}

	// android获取一个用于打开Word文件的intent

	public static Intent getWordFileIntent(String param)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Uri uri = Uri.fromFile(new File(param));

		intent.setDataAndType(uri, "application/msword");

		return intent;

	}

	// android获取一个用于打开Excel文件的intent

	public static Intent getExcelFileIntent(String param)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Uri uri = Uri.fromFile(new File(param));

		intent.setDataAndType(uri, "application/vnd.ms-excel");

		return intent;

	}

	// android获取一个用于打开PPT文件的intent

	public static Intent getPptFileIntent(String param)

	{

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Uri uri = Uri.fromFile(new File(param));

		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");

		return intent;

	}
	
	// android获取一个用于打开PPT文件的intent
	
	public static Intent getAllFileIntent(String param)
	
	{
		
		Intent intent = new Intent("android.intent.action.VIEW");
		
		intent.addCategory("android.intent.category.DEFAULT");
		
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		Uri uri = Uri.fromFile(new File(param));
		
		intent.setDataAndType(uri, "*/*");
		
		return intent;
	}

}