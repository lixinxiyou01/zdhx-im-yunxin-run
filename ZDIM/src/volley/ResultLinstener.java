
package volley;

/**
 * 
 * @author 马海明
 * @created 马海明 2015年5月6日
 */
public interface ResultLinstener {

	void onSuccess(String response);

	void onError();

	void onServerError();

	void onIOError();

	void onTimeOutError();

	void onNoConnectionError();

	void onSetTag(String tag);
}
