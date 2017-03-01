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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageViewWithWhite extends ImageView {

	public CircleImageViewWithWhite(Context context) {
		super(context);
		
	}
	public CircleImageViewWithWhite(Context context, AttributeSet attrs) {
		super(context, attrs);
	
	}
	public CircleImageViewWithWhite(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
	}
	
	protected void onDraw(Canvas canvas){
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE); //�������ɫ�����˱�Ե����ɫ
		
		Drawable drawable = getDrawable();
		if(drawable == null){
			return ;
		}
		if(getWidth() == 0 || getHeight() == 0){
			return ;
		}
		
		Bitmap b = ((BitmapDrawable)drawable).getBitmap();
		Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
		
		int w = getWidth() ;
		int h = getHeight() ;
		System.out.println("Windth="+w+",Height="+h);
		
		//Բ��ImageView�İ뾶Ϊ�����е�ImageView�����С
		Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
		
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawCircle(w / 2, h / 2, w / 2 , paint);
		canvas.drawBitmap(roundBitmap, 0, 0, null);
	}
	public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
		
		Bitmap sbmp;
		if (bmp.getWidth() != radius || bmp.getHeight() != radius)
		   sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
		else
		   sbmp = bmp;
		
		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),Config.ARGB_8888);
		Canvas canvas = new Canvas(output);                       

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.WHITE);
		//������������ȥ����ֵΪ�ױߵĿ��.
		canvas.drawCircle(sbmp.getWidth() / 2 ,sbmp.getHeight() / 2 , sbmp.getWidth() / 2 - 5, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));   
		
		canvas.drawBitmap(sbmp, rect, rect, paint);
		
		return output;
	}
}