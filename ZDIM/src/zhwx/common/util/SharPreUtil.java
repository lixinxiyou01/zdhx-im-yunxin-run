package zhwx.common.util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.netease.nim.demo.ECApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * 
 * @Title: SharPreUtil.java 
 * @Package com.zdhx.edu.im.common.utils 
 * @Description: (SharedPreferences 存储和获取对象)
 * @author Li.xin @ 中电和讯
 * @date 2015-11-9 下午3:55:59
 */
public class SharPreUtil {
	
	/**
	 * 存储
	 * @param key
	 * @param object
	 */
	public static void saveObject(String key,Object object) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ECApplication.getInstance());
		// 创建字节输出流
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			// 创建对象输出流，并封装字节流
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			// 将对象写入字节流
			oos.writeObject(object);
			// 将字节流编码成base64的字符窜
			String object_Base64 = new String(Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
			Editor editor = preferences.edit();
			editor.putString(key, object_Base64);

			editor.commit();
		} catch (IOException e) {
		}
	}
	
	/**
	 * 获取
	 * @param key
	 * @return Object
	 */
	public static Object readObjece(String key) {
		Object object = null;
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ECApplication.getInstance());
		String productBase64 = preferences.getString(key, "");
				
		//读取字节
		byte[] base64 = Base64.decode(productBase64.getBytes(), Base64.DEFAULT);
		
		//封装到字节流
		ByteArrayInputStream bais = new ByteArrayInputStream(base64);
		try {
			//再次封装
			ObjectInputStream bis = new ObjectInputStream(bais);
			try {
				//读取对象
				object = (Object) bis.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return object;
	}
	
	public static void saveField(String key , String value){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ECApplication.getInstance());
		preferences.edit().putString(key,value).commit();
	}
	
	public static String getField(String key){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ECApplication.getInstance());
		return preferences.getString(key, "");
	}
	
	
}
