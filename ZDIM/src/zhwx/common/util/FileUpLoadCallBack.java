package zhwx.common.util;

/**
 * 附件上传进度回调
 * Created by Li.Xin @ ZDHX on 2017/4/13.
 */

public interface FileUpLoadCallBack {
    void upLoadProgress(int fileCount,int currentIndex,int currentProgress,int allProgress);
}
