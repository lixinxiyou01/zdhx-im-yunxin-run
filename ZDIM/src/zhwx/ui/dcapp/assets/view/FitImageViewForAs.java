package zhwx.ui.dcapp.assets.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class FitImageViewForAs extends ImageView {
	private Bitmap mBitmap;
	private int mBitmapWidth;
	private int mBitmapHeight;

	private boolean mReady;
	private boolean mSetupPending;

	public FitImageViewForAs(Context context) {
		super(context);
		init();
	}

	public FitImageViewForAs(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FitImageViewForAs(Context context, AttributeSet attrs, int defStyle) {
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
		radio = (width * 1.0) / imgW;
		if (imgH * radio > height) {
			radio = (height * 1.0) / imgH;
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