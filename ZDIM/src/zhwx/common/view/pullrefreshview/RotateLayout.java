package zhwx.common.view.pullrefreshview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.netease.nim.demo.R;


public class RotateLayout extends LinearLayout {

	public RotateLayout(Context context) {
		super(context);
		init();
	}

	public RotateLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private Bitmap bm;
	private View view;
	private ImageView imageview;
	private float curDegrees = 0;

	void init() {
		view = LayoutInflater.from(getContext()).inflate(R.layout.rotatelayout,
				this);
		bm = BitmapFactory.decodeResource(getResources(),
				R.drawable.userguide_moments_icon);
		imageview = (ImageView) view.findViewById(R.id.ImageView1);
		imageview.setImageBitmap(bm);
	}

	public void rotateAnimation() {
		RotateAnimation animation = new RotateAnimation(0f, 360f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		LinearInterpolator lin = new LinearInterpolator();
		animation.setInterpolator(lin);
		animation.setDuration(1000);
		animation.setRepeatCount(-1);
		animation.setFillAfter(true);
		imageview.startAnimation(animation);
	}

	public void celarAniamtion() {
		imageview.clearAnimation();
	}

	/**
	 * @param roate
	 */
	public void roate(float roate) {
		int bmpW = bm.getWidth();
		int bmpH = bm.getHeight();
		Matrix mt = new Matrix();
		mt.setRotate(curDegrees = curDegrees - roate, bmpW / 2, bmpH / 2);
		Bitmap resizeBmp = Bitmap.createBitmap(bm, 0, 0, bmpW, bmpH, mt, true);
		imageview.setScaleType(ScaleType.CENTER);
		imageview.setImageBitmap(resizeBmp);
		if(isScroll){
		}
	}

	/**
	 * 
	 */
	public void reset() {
		if (isComplete) {
			return;
		}
		isComplete = true;
		Animation animations = AnimationUtils.loadAnimation(getContext(),
				R.anim.slide_in_from_top);
		animations.setDuration(500);
		animations.setFillAfter(true);
		imageview.setVisibility(View.VISIBLE);
		imageview.startAnimation(animations);
		animations.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				isScroll = false;
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				isScroll = true;
			}
		});
	}

	private boolean isComplete = false;
	private boolean isScroll = false;

	/**
	 * 
	 */
	public void toup() {
		celarAniamtion();
		isComplete = false;
		Animation animation = null;
		if (animation == null) {
			animation = AnimationUtils.loadAnimation(getContext(),
					R.anim.slide_out_to_top);
			animation.setDuration(500);
			animation.setFillAfter(true);
		}
		imageview.startAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				scrollTo(0, 0);
			}
		});
	}
	
	public class  baseAnimationListener implements AnimationListener{

		@Override
		public void onAnimationEnd(Animation arg0) {
			
		}

		@Override
		public void onAnimationRepeat(Animation arg0) {
			
		}

		@Override
		public void onAnimationStart(Animation arg0) {
			
		}
		
	}
}