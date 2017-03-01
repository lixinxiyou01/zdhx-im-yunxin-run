package zhwx.common.view.pullrefreshview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.demo.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class CommonUtils {

	public static int transDip(int dip) {
		float scale = Resources.getSystem().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}

	/**
	 * ???????????????????绰????
	 * 
	 * @param context
	 * @return ?????绰??????
	 */

	// public static List<String> getContactListByPhoneNumber(Context context,
	// String phoneNumber) {
	// List<String> phoneNameList = new ArrayList<String>();
	// Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
	// Uri.encode(phoneNumber));
	// Cursor cursor = context.getContentResolver().query(uri, new String[] {
	// PhoneLookup.DISPLAY_NAME }, null, null,
	// PhoneLookup.DISPLAY_NAME);
	//
	// for (int i = 0; i < cursor.getCount(); i++) {
	// cursor.moveToPosition(i);
	// int nameFieldColumnIndex =
	// cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
	// String name = cursor.getString(nameFieldColumnIndex);
	// if (null != name && !"".equals(name) && !phoneNameList.contains(name)) {
	// phoneNameList.add(name);
	// }
	// }
	// cursor.close();
	// return phoneNameList;
	// }

	/**
	 * ???????
	 * 
	 * @param context
	 *            context
	 * @param content
	 *            ????????
	 */
	// public static void sendSMS(Context context, String toNumber, String
	// content) {
	// try {
	// Uri smsUri = Uri.parse("smsto:" + toNumber);
	// Intent it = new Intent(android.content.Intent.ACTION_SENDTO, smsUri);
	// it.putExtra("sms_body", content);
	// context.startActivity(it);
	// } catch (Exception ee) {
	// }
	//
	// }
	public static String getVersion(Context context) {
		String version = "";
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			version = "" + info.versionCode;
		} catch (NameNotFoundException e) {
			version = "1";
			e.printStackTrace();
		}

		return version;
	}

	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			versionName = "" + info.versionName;
		} catch (NameNotFoundException e) {
			versionName = "1.0";
			e.printStackTrace();
		}

		return versionName;
	}

	/*
	 * MD5????
	 */
	public static String md5(String str) {
		String value = "";
		if (str != null) {
			MessageDigest md5 = null;
			try {
				md5 = MessageDigest.getInstance("MD5");
				md5.update(str.getBytes());
				value = toHex(md5.digest());
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return value.toLowerCase();
	}

	public static String toHex(final byte[] b) {
		final StringBuffer sb = new StringBuffer(b.length * 3);
		for (int i = 0; i < b.length; ++i) {
			sb.append(Character.forDigit((b[i] & 0xF0) >> 4, 16));
			sb.append(Character.forDigit(b[i] & 0xF, 16));
		}
		return ((sb != null) ? sb.toString().toUpperCase() : null);
	}

	/*
	 * MD5????
	 */
	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		// 16λ????????9λ??25λ
		return md5StrBuff.substring(8, 24).toString().toUpperCase();
	}

	public static boolean is24H(Context context) {
		ContentResolver resolver = context.getContentResolver();
		String strTimeFormat = android.provider.Settings.System.getString(
				resolver, android.provider.Settings.System.TIME_12_24);
		if ("24".equals(strTimeFormat)) {
			return true;
		}
		return false;
	}

	public static String getTimeZone(Context context) {
		ContentResolver resolver = context.getContentResolver();
		String strTimeFormat = android.provider.Settings.System.getString(
				resolver, android.provider.Settings.Global.AUTO_TIME_ZONE);
		return strTimeFormat;
	}

	public static void saveValueToPublicSharedPreferences(Context context,
			String key, String Value) {
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = settings.edit();
		editor.putString(key, Value);
		editor.commit();
	}

	public static String getValueFromPublicSharedPreferences(Context context,
			String key) {
		String value = "";
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(context);
		value = settings.getString(key, "");
		return value;
	}

	public static Date parseDate(String string) {
		Date date = null;
		try {
			DateFormat XEP_0082_UTC_FORMAT_WITHOUT_MILLIS = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			XEP_0082_UTC_FORMAT_WITHOUT_MILLIS.setTimeZone(TimeZone
					.getTimeZone("UTC"));
			date = XEP_0082_UTC_FORMAT_WITHOUT_MILLIS.parse(string);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String isNull(String str, String suffix) {
		String strTemp = "";
		if (null != str) {
			if (!"".equals(str)) {
				if (!"".equals(suffix)) {
					return str + suffix;
				} else {
					return str;
				}
			}
		}
		return strTemp;
	}

	public static String isNull(String str) {
		return isNull(str, "");
	}

	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		return true;
	}

	public static double getFileSize(File file) throws Exception {// ????????С
		double size = 0;
		if (file.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			size = fis.available();
		} else {
			return 0;
		}
		size = size / 1024.00;
		size = Double.parseDouble(size + "");
		BigDecimal b = new BigDecimal(size);
		double y1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		return Double.parseDouble(df.format(y1));
	}

	public static String getDate(int year, int monthIndex, int day, int hour) {
		return String.format("%d-%d-%dT%d:00:00", year, monthIndex, day, hour);
	}

	public static String getDate(int year, int monthIndex, int day, int hour,
			int minute) {
		return String.format("%d-%d-%dT%d:%d:00", year, monthIndex, day, hour,
				minute);
	}

	// ----------------------new-----------------

	/**
	 * ?ж? list ???? null ?? size = 0
	 * 
	 * @param list
	 * @return ??? null ?? size =0 ???? true, ????? false
	 */
	public static boolean isNullOrEmpty(List<?> list) {
		return null == list || list.isEmpty();
	}

	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			return isNetworkInfoAvailable(getConnectivityManager(context)
					.getActiveNetworkInfo());
		}
		return false;
	}

	public static boolean validateNetworkConnection(Context context) {
		if (!isNetworkConnected(context)) {
			return false;
		}
		return true;
	}

	private static ConnectivityManager getConnectivityManager(Context context) {
		return (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	private static boolean isNetworkInfoAvailable(NetworkInfo info) {
		if (info == null) {
			return false;
		}
		return info.isAvailable();
	}

	public static void hide(Context context, IBinder windowToken) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(windowToken,
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static void hide(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(
				activity.getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * ???????0
	 * 
	 * @author hjt
	 */
	public static String addLeftZeroForNum(String str, int strLength) {
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				sb.append("0").append(str);// ??0
				// sb.append(str).append("0");//???0
				str = sb.toString();
				strLen = str.length();
			}
		}

		return str;
	}

	/**
	 * ???long?????
	 * 
	 * @param s
	 * @return
	 */
	public static long getDateLong(String s) {
		long l = 0;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			l = sdf.parse(s).getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return l;
	}

	public static String getDateString(long time) {
		String s = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			s = sdf.format(new Date(time));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * ??????????
	 * 
	 * @param date
	 * @return
	 */
	public static String getShortDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		return sdf.format(date);
	}

	/**
	 * ????date???n???????
	 */
	public static Date getDateAfter(Date date, int n) {
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.set(Calendar.DATE, now.get(Calendar.DATE) + n);
		return now.getTime();
	}

	private static long lastClickTime;

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 300) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	public static boolean isFastDoubleClick(int s) {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < s) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	/**
	 * ????????????????(??????????????,???????)??
	 * 
	 * @param context
	 *            ?????????
	 * @param uniqueName
	 *            ??????
	 * @return ??????????
	 */
	public static File getDiskCacheDir(Context context, String uniqueName) {
		// ?????????洢??????????,?????????,???????
		// ?????? ??
		// ?????????????? ??
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) ? getExternalCacheDir(context)
				.getPath() : context.getCacheDir().getPath();
		File appCacheDir = new File(cachePath + File.separator + uniqueName);
		if (!appCacheDir.exists()) {
			if (!appCacheDir.mkdirs()) {
				return null;
			}
			try {
				new File(appCacheDir, ".nomedia").createNewFile();
			} catch (IOException e) {
			}
		}
		return appCacheDir;
	}

	/**
	 * ???????ó??????
	 * 
	 * @param context
	 *            ?????????
	 * @return ????????
	 */
	@SuppressLint("NewApi")
	public static File getExternalCacheDir(Context context) {
		if (hasFroyo()) {
			return context.getExternalCacheDir();
		}
		String cacheDir = "/Android/data/" + context.getPackageName()
				+ "/cache/";
		File appCacheDir = new File(Environment.getExternalStorageDirectory()
				.getPath() + cacheDir);
		if (!appCacheDir.exists()) {
			if (!appCacheDir.mkdirs()) {
				return null;
			}
			try {
				new File(appCacheDir, ".nomedia").createNewFile();
			} catch (IOException e) {
			}
		}
		return appCacheDir;
	}

	/**
	 * ???Android???汾?????? Froyo?? Android 2.2?? Android 2.2????
	 * 
	 * @return
	 */
	public static boolean hasFroyo() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean isMobile(String strMobile) {
		String reg = "^1[0-9]{10}$"; // ?????????
		// String reg = "^(13[0-9]|15[012356789]|18[0236789]|14[57])[0-9]{8}$";
		// // ?????????
		return strMobile.matches(reg);
	}

	public static boolean isMail(String strMobile) {
		String reg = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$"; // ???????
		return strMobile.matches(reg);
	}

	/**
	 * ?????????????
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getCircleBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		int size = (bitmap.getWidth() > bitmap.getHeight()) ? bitmap
				.getHeight() : bitmap.getWidth();
		final Rect rect = new Rect(0, 0, size, size);
		final RectF rectF = new RectF(rect);
		final float roundPx = bitmap.getWidth() / 2;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);

		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static String changeTelToPsw(String tel, String rstr) {
		String s1 = "";
		String s2 = "";
		int n = 4;
		try {
			s1 = tel.substring(0, n - 1);
			s2 = tel.substring(n + 3, tel.length());
		} catch (Exception ex) {
			// throw new Throwable("?滻??λ?????????λ??");
		}
		return s1 + rstr + s2;
	}

	public static void CallPhone(Context context, String phone) {
		Intent intent = new Intent(Intent.ACTION_DIAL,
				Uri.parse("tel:" + phone));
		context.startActivity(intent);
	}

	public static void saveLog(String s) {
		try {
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long time = System.currentTimeMillis();
			String now = sf.format(new Date(time));

			File file = new File("mnt/sdcard/time_log.txt");
			byte[] contentByte = getBytesFromFile(file);
			if (contentByte != null) {
				String content = new String(contentByte);
				s = "----------------------------------------\r\n" + now
						+ " : " + s + "\r\n" + content;
			} else {
				s = now + " : " + s + "\r\n";
			}

			byte[] data = s.getBytes();
			ByteArrayInputStream is;
			is = new ByteArrayInputStream(data, 0, data.length);
			if (is != null) {
				String url = "mnt/sdcard/time_log.txt";
				try {
					writeSDFromInput(is, url);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[] getBytesFromFile(File f) {
		if (f == null) {
			return null;
		}
		try {
			FileInputStream stream = new FileInputStream(f);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = stream.read(b)) != -1)
				out.write(b, 0, n);
			byte[] datas = out.toByteArray();
			stream.close();
			out.close();
			return datas;
		} catch (IOException e) {
		}
		return null;
	}

	/**
	 * ?????InputStream????????д??SD????
	 */
	public static void writeSDFromInput(ByteArrayInputStream input, String _file)
			throws IOException {
		OutputStream output = null;
		try {
			File file = new File(_file);
			// String _filePath_file.replace(File.separatorChar +
			// file.getName(), "");
			int end = _file.lastIndexOf(File.separator);
			String _filePath = _file.substring(0, end);
			File filePath = new File(_filePath);
			if (!filePath.exists()) {
				filePath.mkdirs();
			}
			file.createNewFile();
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			int i;
			while (-1 != (i = input.read(buffer))) {
				output.write(buffer, 0, i);
			}
			output.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static long diff = 0;

	/**
	 * ?洢???????????????????
	 */
	public static void saveDiffTime(Context context, String systime, long diff) {
		long diffTime = CommonUtils.getDateLong(systime) + diff
				- System.currentTimeMillis();
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putLong("diff", diffTime).commit();
		diff = 0;
	}

	/**
	 * ??????????????????????
	 */
	public static long readDiffTime(Context context) {
		if (diff == 0)
			diff = PreferenceManager.getDefaultSharedPreferences(context)
					.getLong("diff", 0);
		return diff;
	}

	/**
	 * ??????
	 * 
	 * @return
	 */
	public static long getSystemTime(Context context) {
		return System.currentTimeMillis() + CommonUtils.readDiffTime(context);
	}

	/**
	 * ??????????????
	 * 
	 * @param cancelable
	 *            ???????????????????dialog?????
	 * @return Dialog????
	 */
	public static Dialog createLoadingDialog(Context context, boolean cancelable) {
		return createLoadingDialog(context, null, cancelable);
	}

	/**
	 * ??????????????
	 * 
	 * @param context
	 *            ?????????
	 * @param msg
	 *            ??????????????? ?磺??????????
	 * @param cancelable
	 *            ???????????????????dialog?????
	 * @return Dialog????
	 */
	public static Dialog createLoadingDialog(Context context, String msg,
			boolean cancelable) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dialog_loading, null);
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);
		if (!TextUtils.isEmpty(msg))
			tipTextView.setText(msg);
		Dialog loadingDialog = new Dialog(context, R.style.dialog_common);
		loadingDialog.setCancelable(cancelable);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		return loadingDialog;
	}

	/**
	 * 06??-18?? ????
	 * 
	 * @return
	 */
	public static boolean isBaitian() {
		Date d = new Date();
		int hours = d.getHours();
		if (6 < hours && hours < 18) {
			return true;
		} else {
			return false;
		}
	}
}
