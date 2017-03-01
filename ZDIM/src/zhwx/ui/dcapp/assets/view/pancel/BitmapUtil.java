package zhwx.ui.dcapp.assets.view.pancel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Base64;
import android.view.View;

public class BitmapUtil {

	private static Bitmap bitmap = null;
	private static Bitmap bm = null;
	static byte[] data;
	private static Map<String, Bitmap> bms = new HashMap<String, Bitmap>();

	/**
	 * 计算图片的缩放值
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	/**
	 * 根据路径获得图片并压缩，返回bitmap用于显示
	 * 
	 * @author hefeng
	 * @version 创建时间：2013-10-9 上午11:05:11
	 * @param filePath
	 * @param width
	 * @param heigh
	 * @return
	 */
	public static Bitmap getSmallBitmap(String filePath, int width, int heigh) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, width, heigh);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}

	/**
	 * 图片变灰
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();
		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	/**
	 * 获取截图
	 */
	public static Bitmap shot(View v) {
		// View vv = v.getRootView();
		v.setDrawingCacheEnabled(true);
		Bitmap bmp = v.getDrawingCache();
		return bmp;
	}

	/**
	 * 获取截图 并保存
	 * 
	 * @author hefeng
	 * @version 创建时间：2013-9-27 上午8:46:31
	 * @param v
	 * @param context
	 * @return
	 */
	public static Bitmap shot(View v, Context context) {
		Bitmap bmp = null;
		View vv = null;
		vv = v.getRootView();
		vv.setDrawingCacheEnabled(true);
		bmp = vv.getDrawingCache();
		saveBitmapToSDCard(bmp, BitmapUtil.getSDCardPath(), "screen.jpg");
		vv.setDrawingCacheEnabled(false);
		return bmp;
	}

	/**
	 * 将Bitmap转换成InputStream
	 * 
	 * @author hefeng
	 * @version 创建时间：2013-10-9 上午11:04:59
	 * @param bm
	 * @return
	 */
	public static InputStream Bitmap2InputStream(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}

	/**
	 * 获取SDCard的目录路径功能
	 */
	public static String getSDCardPath() {
		File sdcardDir = null;
		// 判断SDCard是否存在
		boolean sdcardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		if (sdcardExist) {
			sdcardDir = Environment.getExternalStorageDirectory();
		}
		return sdcardDir.toString();
	}

	/**
	 * Base64编码的String字符串 转成Bitmap
	 */
	public static Bitmap Base64ToBitmap(String str) {
		bm = bms.get(str);
		if (bm == null) {
			if ("" != str && str != null) {
				try {
					if (data != null) {
						data = null;
					}
					data = Base64.decode(str, Base64.DEFAULT);
					if (bm != null) {
						bm = null;
					}
					try {
						bm = BitmapFactory
								.decodeByteArray(data, 0, data.length);
					} catch (RuntimeException e) {
						bm = BitmapFactory
								.decodeByteArray(data, 0, data.length);
					}
					bms.put(str, bm);
				} catch (Exception e) {
				}
			}
		}

		return bm;
	}

	/**
	 * Base64编码的String字符串 转成Bitmap并压缩
	 */
	public static Bitmap Base64ToBitmapAndCompress(String str) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		if (data != null) {
			data = null;
		}
		if (bm != null) {
			bm = null;
		}
		if ("" != str && str != null) {
			try {
				data = Base64.decode(str, Base64.DEFAULT);
				str = "";
				bm = BitmapFactory.decodeByteArray(data, 0, data.length,
						options);
			} catch (Exception e) {
			}
		}
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 100, 80);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}

	/**
	 * Bitmap 转成Base64编码的String字符串
	 */
	public static String BitmapToBase64(Bitmap bmp) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] data = baos.toByteArray();
		return Base64.encodeToString(data, Base64.DEFAULT);
	}

	/**
	 * Bitmap 转成Base64编码的String字符串 适用于PNG
	 */
	public static String BitmapToBase64PNG(Bitmap bmp) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 40, baos);
		byte[] data = baos.toByteArray();
		return Base64.encodeToString(data, Base64.DEFAULT);
	}

	/**
	 * 保存bitmap到sd卡
	 */
	public static void saveBitmapToSDCard(Bitmap bitmap, String path,
			String name) {
		if (bitmap != null) {

			File file = new File(path, name);
			FileOutputStream out;
			try {
				out = new FileOutputStream(file);
				if (bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out)) {
					out.flush();
					out.close();
				}
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}
	}
	  public static byte[] bitampToByteArray(Bitmap bitmap)
	    {
	        byte[] array = null;
	        try 
	        {
	            if (null != bitmap)
	            {
	                ByteArrayOutputStream os = new ByteArrayOutputStream();
	                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, os);
	                array = os.toByteArray();
	                os.close();
	            }
	        } 
	        catch (IOException e) 
	        {
	            e.printStackTrace();
	        }
	        
	        return array;
	    }
	 public static void saveBitmapToSdCard(Bitmap bmp, String strPath){
	    
	        if (null != bmp && null != strPath && !strPath.equalsIgnoreCase(""))
	        {
	            try
	            {
	                File file = new File(strPath);
	                FileOutputStream fos = new FileOutputStream(file);
	                byte[] buffer = BitmapUtil.bitampToByteArray(bmp);
	                fos.write(buffer);
	                fos.close();
	            }
	            catch (FileNotFoundException e)
	            {
	                e.printStackTrace();
	            }
	            catch (IOException e)
	            {
	                e.printStackTrace();
	            }
	        }
	    }
	/**
	 * 保存PNG到sd卡 (PNG)
	 */
	public static void savePNGToSDCard(Bitmap bitmap, String path) {
		File file = new File(path);
		FileOutputStream out;
		try {
			out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	public static Bitmap getBitmapByPath(String path) {
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		return bitmap;

	}

	/**
	 * 获得圆角图片的方法
	 * 
	 * @param bitmap
	 * @param roundPx
	 *            一般设成14
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * drawable----bitmap
	 * 
	 * @author hefeng
	 * @version 创建时间：2013-9-29 上午11:11:06
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitamp(Drawable drawable) {
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		System.out.println("Drawable转Bitmap");
		Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
				: Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		return bitmap;
	}

	/**
	 * 压缩图片
	 * 
	 * @author hefeng
	 * @version 创建时间：2013-10-9 上午11:06:04
	 * @param srcPath
	 * @return
	 */
	public static Bitmap compressImageFromFile(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// 只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 410f;//
		float ww = 580f;//
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置采样率

		newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		// return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
		// 其实是无效的,大家尽管尝试
		return bitmap;
	}

	/**
	 * 压缩图片
	 * 
	 * @author hefeng
	 * @version 创建时间：2013-10-9 上午11:06:04
	 * @param srcPath
	 * @return
	 */
	public static Bitmap compressSmallImageFromFile(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// 只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 80f;//
		float ww = 80f;//
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置采样率

		newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		// return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
		// 其实是无效的,大家尽管尝试
		return bitmap;
	}

	/**
	 * 获得图片
	 * 
	 * @param str
	 * @param context
	 * @return
	 */
	public static Bitmap getAssertImage(String str, Context context) {
		Bitmap bm = null;
		try {
			bm = BitmapFactory.decodeStream(context.getAssets().open(
					str + ".png"));
		} catch (IOException e) {
		}
		return bm;
	}

	/**
	 * 进行截取屏幕
	 * 
	 * @param pActivity
	 * @return bitmap
	 */
	public static Bitmap takeScreenShot(Activity pActivity) {
		Bitmap bitmap = null;
		View view = pActivity.getWindow().getDecorView();
		// 设置是否可以进行绘图缓存
		view.setDrawingCacheEnabled(true);
		// 如果绘图缓存无法，强制构建绘图缓存
		view.buildDrawingCache();
		// 返回这个缓存视图
		bitmap = view.getDrawingCache();

		// 获取状态栏高度
		Rect frame = new Rect();
		// 测量屏幕宽和高
		view.getWindowVisibleDisplayFrame(frame);
		// 根据坐标点和需要的宽和高创建bitmap
		bitmap = Bitmap.createBitmap(bitmap, 90, 20, 955, 712);
		saveBitmapToSDCard(bitmap, BitmapUtil.getSDCardPath(), "screen.jpg");
		return bitmap;
	}

	public static void setFree() {
		if (bm != null) {
			if (!bm.isRecycled()) {
				bm.recycle(); // 回收图片所占的内存
				System.gc(); // 提醒系统及时回收
			}
		}
		if (bitmap != null) {
			if (!bitmap.isRecycled()) {
				bitmap.recycle();
				System.gc();
			}
		}
	}

	/**
	 * @param test
	 */
	public static void removeBM(String test) {
		bms.remove(test);
	}
	 public static Bitmap duplicateBitmap(Bitmap bmpSrc)
	    {
	        if (null == bmpSrc)
	        {
	            return null;
	        }
	        
	        int bmpSrcWidth = bmpSrc.getWidth();
	        int bmpSrcHeight = bmpSrc.getHeight();

	        Bitmap bmpDest = Bitmap.createBitmap(bmpSrcWidth, bmpSrcHeight, Config.ARGB_4444);
	        if (null != bmpDest)
	        {
	            Canvas canvas = new Canvas(bmpDest);
	            final Rect rect = new Rect(0, 0, bmpSrcWidth, bmpSrcHeight);
	            
	            canvas.drawBitmap(bmpSrc, rect, rect, null);
	        }
	        
	        return bmpDest;
	    }
	    
}
