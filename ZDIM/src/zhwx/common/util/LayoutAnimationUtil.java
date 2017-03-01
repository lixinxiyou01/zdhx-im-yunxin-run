package zhwx.common.util;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;

/**
 * listview加载动画工具类
 * @author 容联•云通讯 Modify By Li.Xin @ 中电和讯
 * 2014-6-17 下午1:59:48
 */
public class LayoutAnimationUtil {
	/**
	 * 竖直加载
	 * @return
	 */
	public static LayoutAnimationController getVerticalListAnim() {
		AnimationSet set = new AnimationSet(true);
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(300);
		set.addAnimation(animation);
		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(200);
		set.addAnimation(animation);
		LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
		return controller;
	}
	/**
	 * 从左至右飞入
	 * @return
	 */
	public static LayoutAnimationController getHorizontalListAnim() {
		AnimationSet set = new AnimationSet(true);
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(400);
		set.addAnimation(animation);
		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f,
		Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
		0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(300);
		set.addAnimation(animation);
		LayoutAnimationController controller = new LayoutAnimationController(
		set, 0.5f);
		return controller;
	}
}
