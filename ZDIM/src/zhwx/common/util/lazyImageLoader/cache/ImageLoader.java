package zhwx.common.util.lazyImageLoader.cache;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

	private MemoryCache memoryCache = new MemoryCache();
	private AbstractFileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	// 锟竭程筹拷
	private ExecutorService executorService;

	public ImageLoader(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}

	// 锟斤拷锟斤拷要锟侥凤拷锟斤拷
	public void DisplayImage(String url, ImageView imageView, boolean isLoadOnlyFromCache) {
		imageViews.put(imageView, url);
		// 锟饺达拷锟节存缓锟斤拷锟叫诧拷锟斤拷
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		else if (!isLoadOnlyFromCache){
			
			// 锟斤拷没锟叫的伙拷锟斤拷锟斤拷锟斤拷锟竭程硷拷锟斤拷图片
			queuePhoto(url, imageView);
		}
	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		File f = fileCache.getFile(url);
		
		// 锟饺达拷锟侥硷拷锟斤拷锟斤拷锟叫诧拷锟斤拷锟角凤拷锟斤拷
		Bitmap b = null;
		if (f != null && f.exists()){
			b = decodeFile(f);
		}
		if (b != null){
			return b;
		}
		// 锟斤拷锟斤拷指锟斤拷锟斤拷url锟斤拷锟斤拷锟斤拷图片
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
			return bitmap;
		} catch (Exception ex) {
//			Log.e("", "getBitmap catch Exception...\nmessage = " + ex.getMessage());
			return null;
		}
	}

	// decode锟斤拷锟酵计拷锟斤拷野锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷约锟斤拷锟斤拷诖锟斤拷锟斤拷模锟斤拷锟斤拷锟斤拷锟斤拷每锟斤拷图片锟侥伙拷锟斤拷锟叫∫诧拷锟斤拷锟斤拷锟斤拷频锟�
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			final int REQUIRED_SIZE = 100;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
//			while (true) {
//				if (width_tmp / 2 < REQUIRED_SIZE
//						|| height_tmp / 2 < REQUIRED_SIZE)
//					break;
//				width_tmp /= 2;
//				height_tmp /= 2;
//				scale *= 2;
//			}
			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			// 锟斤拷锟铰的诧拷锟斤拷锟斤拷锟斤拷UI锟竭筹拷锟斤拷
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	/**
	 * 锟斤拷止图片锟斤拷位
	 * 
	 * @param photoToLoad
	 * @return
	 */
	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// 锟斤拷锟斤拷锟斤拷UI锟竭筹拷锟叫革拷锟铰斤拷锟斤拷
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
	
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
			Log.e("", "CopyStream catch Exception...");
		}
	}
	
	public File saveBitmap(Bitmap bm) {
		File f = new File(android.os.Environment.getExternalStorageDirectory(),
				"zwxShareImg.png");
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}
}