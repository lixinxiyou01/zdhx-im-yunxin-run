package zhwx.common.view.imagegallery;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * com.zdhx.edu.im.ui.chatting in ECDemo_Android
 * Created by 容联•云通讯 Modify By Li.Xin @ 中电和讯 on 2015/4/17.
 */
public class ViewImageInfo implements Parcelable {


    public static final Creator<ViewImageInfo> CREATOR
                 = new Creator<ViewImageInfo>() {
                 public ViewImageInfo createFromParcel(Parcel in) {
                         return new ViewImageInfo(in);
                     }

                 public ViewImageInfo[] newArray(int size) {
                         return new ViewImageInfo[size];
                     }
             };


    private int index;
    private String msgLocalId;
    private String thumbnailurl;
    private String picurl;
    private boolean isDownload = false;

    public ViewImageInfo(int index , String thumb , String url) {
        this.index = index;
        this.thumbnailurl = thumb;
        this.picurl = url;
    }

    private ViewImageInfo(Parcel in) {
        this.index = in.readInt();
        this.thumbnailurl = in.readString();
        this.picurl = in.readString();

    }

    public ViewImageInfo(String thumb , String url) {
        this(0, thumb, url);
    }

    public ViewImageInfo(Cursor cursor) {
    }

    public int getIndex() {
        return index;
    }

    public String getThumbnailurl() {
        return thumbnailurl;
    }

    public void setThumbnailurl(String thumbnailurl) {
        this.thumbnailurl = thumbnailurl;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public String getMsgLocalId() {
        return msgLocalId;
    }

    public void setMsgLocalId(String msgLocalId) {
        this.msgLocalId = msgLocalId;
    }


    public boolean isDownload() {
        return isDownload;
    }

    public void setIsDownload(boolean isDownload) {
        this.isDownload = isDownload;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.index);
        dest.writeString(this.thumbnailurl);
        dest.writeString(this.picurl);
    }
}
