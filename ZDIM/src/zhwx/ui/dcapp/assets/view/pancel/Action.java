package zhwx.ui.dcapp.assets.view.pancel;



import android.graphics.*;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;

//基础类
public class Action {
	public int color;
	public Path path;

	Action() {
		color = Color.BLUE;
	}

	Action(int color) {
		this.color = color;
	}


	public void draw(Canvas canvas) {
	}

	public void move(float mx, float my) {

	}

	public void line(float mx, float my) {

	}
}

// 自由曲线
class MyPath extends Action {
	Path path;
	int size;

	MyPath() {
		
		path = new Path();
		size = 1;
	}

	MyPath(float x,float y,int size, int color) {
		super(color);
		path=new Path();
		this.size=size;
		path.moveTo(x, y);
		path.lineTo(x, y);
	}

	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setColor(color);
		paint.setStrokeWidth(5);
		paint.setAntiAlias(true);

		canvas.drawPath(path, paint);
	}

	public void line(float mx, float my) {
		path.lineTo(mx, my);
	}

	public void move(float mx, float my) {
		path.moveTo(mx, my);
	}

}

// 橡皮
class MyEraser extends Action {
	Path path;
	int size;

	MyEraser() {
		path = new Path();
		size = 1;
	}

	MyEraser(float x, float y, int size, int color) {
		super(color);
		path = new Path();
		this.size = size;
		path.moveTo(x, y);
		path.lineTo(x, y);
	}

	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(100);
		canvas.drawPath(path, paint);
	}

	public void line(float mx, float my) {
		path.lineTo(mx, my);
	}

	public void move(float mx, float my) {
		path.moveTo(mx, my);
	}
}