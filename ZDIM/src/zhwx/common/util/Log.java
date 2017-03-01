package zhwx.common.util;

public class Log {
	public static final String TAG = "IM";
	public static final boolean on = true;

	public static void v(String message) {
		if (!true)
			return;
		android.util.Log.v(TAG, message);
	}

	public static void e(String message, Exception e) {
		if (!true)
			return;
		android.util.Log.e(TAG, message, e);
	}

	public static void e(String message) {
		if (!true)
			return;
		android.util.Log.e(TAG, message);
	}

	public static void i(String message) {
		if (!true)
			return;
		android.util.Log.i(TAG, message);
	}

	public static void d(String message) {
		if (!true)
			return;
		android.util.Log.d(TAG, message);
	}

	public static void w(String message) {
		if (!true)
			return;
		android.util.Log.w(TAG, message);
	}
}
