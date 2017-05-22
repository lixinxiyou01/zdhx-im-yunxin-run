package zhwx.common.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;

public class DownloadAsyncTask extends AsyncTask<String, Integer, File> {
	private DownloadResponser responser;
	private String position = "";
	private static HashMap<String, DownloadAsyncTask> map;
	private String[] strings;
	private Context mContext;
	

	public DownloadAsyncTask(DownloadResponser responser, Context context) {
		super();
		this.responser = responser;
		if (map == null) {
			map = new HashMap<String, DownloadAsyncTask>();
		}
		mContext = context;
	}

	public interface DownloadResponser {
		public void predownload();

		public void downloading(int progress, int position);

		public void downloaded(File file, int position);

		public void canceled(int position);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responser.predownload();
	}

	@Override
	protected File doInBackground(String... params) {
		position = params[2];
		if (map != null) {
			map.put(position, this);
		}
		File idr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			idr = mContext.getFilesDir();
		}
		File file = null;
		if (!idr.exists()) {
			try {
				// 按照指定的路径创建文件夹
				idr.mkdirs();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {

			URL url = new URL(params[0]);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Accept-Encoding", "identity");
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			String filename = null;
			if(params.length >= 4) {
				filename = params[3];
			}
			if (filename==null) {
				filename = conn.getHeaderField("Content-Disposition");
				if (filename == null) {
					filename = "noname";
				} else {
					filename = filename.split(";filename=")[1];
					filename = new String(filename.getBytes("ISO-8859-1"), "UTF-8");
				}
			}
			int totalSize = conn.getContentLength();
			if (totalSize <= 0) {
				return null;
			}
			
			file = new File(idr, filename);
			if (file.exists() && file.length() == totalSize) {
				publishProgress(100);
				conn.disconnect();
				return file;
			}
			FileOutputStream fos;
			if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				fos = mContext.openFileOutput(filename, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
				
			} else {
				fos = new FileOutputStream(file);
			}
			int downloadSize = 0;
			InputStream is = conn.getInputStream();
			byte[] buf = new byte[256];
			int len = -1;
			while ((len = is.read(buf)) != -1 && !isCancelled()) {
				downloadSize += len;
				fos.write(buf, 0, len);
				publishProgress((int) ((double) downloadSize / totalSize * 100));
			}
			is.close();
			fos.close();
			conn.disconnect();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!file.exists()) {
			return null;
		}
		return file;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		responser.downloading(values[0], Integer.parseInt(position));
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(File result) {
		super.onPostExecute(result);
		responser.downloaded(result, Integer.parseInt(position));
		removeAsyncTask(position);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responser.canceled(Integer.parseInt(position));
		removeAsyncTask(position);
	}

	public static DownloadAsyncTask getAsyncTask(String key) {

		return map.get(key);
	}

	private void removeAsyncTask(String key) {

		if (map != null) {
			map.remove(key);
			if (map.isEmpty()) {
				map = null;
			}
		}

	}

	public void setPar(String... params) {
		strings = params;
	}

	public void ex() {
		this.execute(strings[0], strings[1], strings[2]);
	};

}
