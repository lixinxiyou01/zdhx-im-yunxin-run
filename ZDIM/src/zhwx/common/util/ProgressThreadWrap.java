package zhwx.common.util;

import android.content.Context;
import android.os.Looper;

public class ProgressThreadWrap {


	private RunnableWrap runnableWrap;


	public ProgressThreadWrap(Context context, RunnableWrap runnableWrap) {
		this.runnableWrap = runnableWrap;
	}

	public void start() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				runnableWrap.run();
				Looper.loop();
			}
		}).start();
	}
}
