package zhwx.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class FitImageView extends ImageView {
	private Bitmap mBitmap;
	private int mBitmapWidth;
	private int mBitmapHeight;

	private boolean mReady;
	private boolean mSetupPending;

	public FitImageView(Context context) {
		super(context);
		init();
	}

	public FitImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FitImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mReady = true;
		if (mSetupPending) {
			setup();
			mSetupPending = false;
		}
	}

	private void reSize() { //TODO  怎么回事
		int width = getResources().getDisplayMetrics().widthPixels;
		int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
		int imgH = mBitmapHeight;
		int imgW = mBitmapWidth;
		int lastH = imgH;
		int lastW = imgW;
		double radio = 0.5;
		radio = (width * 0.5) / imgW;
		if (imgH * radio > height) {
			radio = (height * 0.5) / imgH;
		}
		lastH = (int) (radio * imgH);
		lastW = (int) (radio * imgW);
		LayoutParams lp = this.getLayoutParams();
		lp.width = lastW;
		lp.height = lastH;
		this.setLayoutParams(lp);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		mBitmap = bm;
		setup();
		reSize();
	}

	private void setup() {
		if (!mReady) {
			mSetupPending = true;
			return;
		}

		if (mBitmap == null) {
			return;
		}

		mBitmapHeight = mBitmap.getHeight();
		mBitmapWidth = mBitmap.getWidth();
		invalidate();
	}

}