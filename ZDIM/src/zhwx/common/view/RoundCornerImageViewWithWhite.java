package zhwx.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundCornerImageViewWithWhite extends ImageView{

	public RoundCornerImageViewWithWhite(Context context) {
		super(context);
		
	}
	public RoundCornerImageViewWithWhite(Context context, AttributeSet attrs) {
		super(context, attrs);
	
	}
	public RoundCornerImageViewWithWhite(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
	}
	
	protected void onDraw(Canvas canvas){
		int roundPx = 5;
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.GRAY); //这里的颜色决定了边缘的颜色
		
		Drawable drawable = getDrawable();
		if(drawable == null){
			return ;
		}
		if(getWidth() == 0 || getHeight() == 0){
			return ;
		}
		
		Bitmap b = ((BitmapDrawable)drawable).getBitmap();
		Bitmap bitmap = b.copy(Config.ARGB_8888, true);
		
		int w = getWidth() ;
		int h = getHeight() ;
		RectF rectF = new RectF(0, 0, w, w);
		
		Bitmap roundBitmap = getCroppedBitmap(bitmap, w, roundPx);
		
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		canvas.drawBitmap(roundBitmap, 0, 0, null);
	}
	public static Bitmap getCroppedBitmap(Bitmap bmp, int length,int roundPx) {
		
		Bitmap sbmp;
		if (bmp.getWidth() != length || bmp.getHeight() != length)
		   sbmp = Bitmap.createScaledBitmap(bmp, length, length, false);
		else
		   sbmp = bmp;
		
		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),Config.ARGB_8888);
		Canvas canvas = new Canvas(output);                       
		
		final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());
		final RectF rectF = new RectF(1, 1, sbmp.getWidth() - 1, sbmp.getHeight() - 1); 
		
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.WHITE);
		
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));   
		
		canvas.drawBitmap(sbmp, rect, rect, paint);
		
		return output;
	}
}

