package zhwx.common.view.refreshlayout;

public interface Pullable
{
	/**
	 * �ж��Ƿ���������������Ҫ�������ܿ���ֱ��return false
	 * 
	 * @return true�������������򷵻�false
	 */
	boolean canPullDown();
	boolean canPullUp();
}
