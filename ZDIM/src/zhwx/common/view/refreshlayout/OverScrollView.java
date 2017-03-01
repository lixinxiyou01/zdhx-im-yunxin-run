package zhwx.common.view.refreshlayout;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * ��������http://blog.csdn.net/zhongkejingwang/article/details/38868463
 * 
 * @author �¾�
 */
public class OverScrollView extends ScrollView {
	public static final String TAG = "PullToRefreshLayout";
	 //�����¼��ĸ߶�Ĭ�Ϸ�ֵ
    private static final int TRIGGER_HEIGHT = 120;
    //�������ܾ���
    private float overScrollDistance;
	 //�����¼��ĸ߶ȷ�ֵ����СֵΪ30
	private int mOverScrollTrigger = TRIGGER_HEIGHT;
	private OverScrollTinyListener mOverScrollTinyListener;
	private OverScrollListener mOverScrollListener;
	// ����Y��꣬��һ���¼���Y���
	private float downY, lastY;
	// �����ľ��롣ע�⣺pullDownY��pullUpY������ͬʱ��Ϊ0
	public float pullDownY = 0;
	// �����ľ���
	private float pullUpY = 0;
	private MyTimer timer;
	// �ع��ٶ�
	public float MOVE_SPEED = 8;
	// ��һ��ִ�в���
	private boolean isLayout = false;
	// ��ָ��������������ͷ�Ļ�������ȣ��м�������к���仯
	private float radio = 2;
	// ʵ����Pullable�ӿڵ�View
	private View pullableView;
	// ���˶�㴥��
	private int mEvents;
	// ������������������pull�ķ������ӿ��ƣ����������������ֿ�����ʱû������
	private boolean canPullDown = true;
	private boolean canPullUp = true;

	/**
	 * ִ���Զ��ع���handler
	 */
	Handler updateHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// �ص��ٶ�����������moveDeltaY���������
			MOVE_SPEED = (float) (5 + 15 * Math.tan(Math.PI / 2
					/ getMeasuredHeight() * (pullDownY + Math.abs(pullUpY))));
			if (pullDownY > 0)
				pullDownY -= MOVE_SPEED;
			else if (pullUpY < 0)
				pullUpY += MOVE_SPEED;
			if (pullDownY < 0) {
				// ����ɻص�
				pullDownY = 0;
				timer.cancel();
			}
			if (pullUpY > 0) {
				// ����ɻص�
				pullUpY = 0;
				timer.cancel();
			}
			// ˢ�²���,���Զ�����onLayout
			requestLayout();
		}

	};

	public OverScrollView(Context context) {
		super(context);
		initView(context);
	}

	public OverScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public OverScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		timer = new MyTimer(updateHandler);
		setFadingEdgeLength(0);
	}

	private void hide() {
		timer.schedule(5);
	}

	/**
	 * ����������������
	 */
	private void releasePull() {
		canPullDown = true;
		canPullUp = true;
	}

	/*
	 * ���� Javadoc���ɸ��ؼ������Ƿ�ַ��¼�����ֹ�¼���ͻ
	 * 
	 * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			downY = ev.getY();
			lastY = downY;
			timer.cancel();
			mEvents = 0;
			releasePull();
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_POINTER_UP:
			// ���˶�㴥��
			mEvents = -1;
			break;
		case MotionEvent.ACTION_MOVE:
			float deltaY = ev.getY() - lastY;
			if (mEvents == 0) {
				if (canPullDown && isCanPullDown()) {
					// �������������ڼ���ʱ��������
					// ��ʵ�ʻ�����������С������������ĸо�
					pullDownY = pullDownY + deltaY / radio;
					if (ev.getY() - lastY  < 0) {
						pullDownY = pullDownY + deltaY ;
					}
					if (pullDownY < 0) {
						pullDownY = 0;
						canPullDown = false;
						canPullUp = true;
					}
					if (pullDownY > getMeasuredHeight())
						pullDownY = getMeasuredHeight();
					overScrollDistance = pullDownY;
				} else if (canPullUp && isCanPullUp()) {
					// ��������������ˢ��ʱ��������
					pullUpY = pullUpY + deltaY / radio;
					if (ev.getY() - lastY > 0) {
						pullUpY = pullUpY + deltaY ;
					}
					if (pullUpY > 0) {
						pullUpY = 0;
						canPullDown = true;
						canPullUp = false;
					}
					if (pullUpY < -getMeasuredHeight())
						pullUpY = -getMeasuredHeight();
					overScrollDistance = pullUpY;
				} else
					releasePull();
			} else
				mEvents = 0;
			lastY = ev.getY();
			// �����������ı����
			radio = (float) (2 + 3 * Math.tan(Math.PI / 2 / getMeasuredHeight()
					* (pullDownY + Math.abs(pullUpY))));
			requestLayout();
			// ��Ϊˢ�ºͼ��ز�������ͬʱ���У�����pullDownY��pullUpY����ͬʱ��Ϊ0�����������(pullDownY +
			// Math.abs(pullUpY))�Ϳ��Բ��Ե�ǰ״̬�������
			if ((pullDownY + Math.abs(pullUpY)) > 8) {
				// ��ֹ����������󴥷������¼��͵���¼�
				ev.setAction(MotionEvent.ACTION_CANCEL);
			}
			if(mOverScrollTinyListener != null){
				mOverScrollTinyListener.scrollDistance((int)deltaY, (int)overScrollDistance);
			}
			break;
		case MotionEvent.ACTION_UP:
			hide();
			overScrollTrigger();
			if(mOverScrollTinyListener != null && (isCanPullDown() || isCanPullUp())){
				mOverScrollTinyListener.scrollLoosen();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			if(mOverScrollTinyListener != null && (isCanPullDown() || isCanPullUp())){
				mOverScrollTinyListener.scrollLoosen();
			}
			break;
		default:
			break;
		}
		// �¼��ַ�������
		try {
			super.dispatchTouchEvent(ev);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}


	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (!isLayout) {
			// �����ǵ�һ�ν�����ʱ����һЩ��ʼ��
			pullableView = getChildAt(0);
			isLayout = true;
		}
		
		pullableView.layout(0, (int) (pullDownY + pullUpY),
				pullableView.getMeasuredWidth(), (int) (pullDownY + pullUpY)
						+ pullableView.getMeasuredHeight());
	}

	class MyTimer {
		private Handler handler;
		private Timer timer;
		private MyTask mTask;

		public MyTimer(Handler handler) {
			this.handler = handler;
			timer = new Timer();
		}

		public void schedule(long period) {
			if (mTask != null) {
				mTask.cancel();
				mTask = null;
			}
			mTask = new MyTask(handler);
			timer.schedule(mTask, 0, period);
		}

		public void cancel() {
			if (mTask != null) {
				mTask.cancel();
				mTask = null;
			}
		}

		class MyTask extends TimerTask {
			private Handler handler;

			public MyTask(Handler handler) {
				this.handler = handler;
			}

			@Override
			public void run() {
				handler.obtainMessage().sendToTarget();
			}

		}
	}
	
	/** 
     * �ж��Ƿ���������� 
     */  
    private boolean isCanPullDown() {  
        return getScrollY() == 0 ||   
        		pullableView.getHeight() < getHeight() + getScrollY();  
    }  
      
    /** 
     * �ж��Ƿ�������ײ� 
     */  
    private boolean isCanPullUp() {  
        return  pullableView.getHeight() <= getHeight() + getScrollY();  
    } 
    
    private boolean isOnTop(){
		return getScrollY() == 0;
	}
	
	private boolean isOnBottom(){
		return getScrollY() + getHeight() == pullableView.getHeight();
	}
	
	/**
	 * ��OverScroll����һ��ֵʱ�����ô˼���
	 * 
	 * @author King
	 * @since 2014-4-9 ����4:36:29
	 */
	public interface OverScrollListener {

		/**
		 * ����
		 */
		void headerScroll();
		
		/**
		 * �ײ�
		 */
		void footerScroll();
		
	}
	
	/**
	 * ÿ��OverScrollʱ�����ܴ����ļ���
	 * @author King
	 * @since 2014-4-9 ����4:39:06
	 */
	public interface OverScrollTinyListener{
		
		/**
		 * ��������
		 * @param tinyDistance ��ǰ������ϸС����
		 * @param totalDistance �������ܾ���
		 */
		void scrollDistance(int tinyDistance, int totalDistance);

        /**
         * �����ɿ�
         */
        void scrollLoosen();
	}
	
	/**
	 * ����OverScrollListener������ֵ
	 * @param height
	 */
	public void setOverScrollTrigger(int height){
		if(height >= 30){
			mOverScrollTrigger = height;
		}
	}
	
	private void overScrollTrigger(){
		if(mOverScrollListener == null){
			return;
		}
		
		if(overScrollDistance > mOverScrollTrigger && overScrollDistance >= 0){
			mOverScrollListener.headerScroll();
		}
		
		if(overScrollDistance < -mOverScrollTrigger && overScrollDistance < 0){
			mOverScrollListener.footerScroll();
		}
	}
	
	public OverScrollTinyListener getOverScrollTinyListener() {
		return mOverScrollTinyListener;
	}

	public void setOverScrollTinyListener(OverScrollTinyListener OverScrollTinyListener) {
		this.mOverScrollTinyListener = OverScrollTinyListener;
	}

	public OverScrollListener getOverScrollListener() {
		return mOverScrollListener;
	}

	public void setOverScrollListener(OverScrollListener OverScrollListener) {
		this.mOverScrollListener = OverScrollListener;
	}
}
