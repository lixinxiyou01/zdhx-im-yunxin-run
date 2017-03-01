package zhwx.common.view.capture.core;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class StringUtils {

	public static boolean isNull(String s){
		if(s == null || s.equals("") || s.length() == 0){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * show images in the android device media store
	 */
	public static void showPictures(Activity a,int type){
		Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); //"android.intent.action.GET_CONTENT"
        String IMAGE_UNSPECIFIED = "image/*";
        innerIntent.setType(IMAGE_UNSPECIFIED); //�鿴���� String IMAGE_UNSPECIFIED = "image/*"; ��ϸ�������� com.google.android.mms.ContentType ��
        Intent wrapperIntent = Intent.createChooser(innerIntent, null);
        a.startActivityForResult(wrapperIntent, type);
	}
	
	public static Bitmap getPicFromBytes(byte[] bytes,
			BitmapFactory.Options opts) {
		if (bytes != null)
			if (opts != null)
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
						opts);
			else
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return null;
	}

	public static byte[] readStream(InputStream inStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;

	}

}
