package com.netease.nim.uikit.session.viewholder;

import android.widget.Toast;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.util.media.BitmapDecoder;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;

/**
 * Created by zhoujianghua on 2015/8/5.
 */
public class MsgViewHolderFile extends MsgViewHolderThumbBase {

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_unknown;
    }

    @Override
    protected void onItemClick() {
        Toast.makeText(context, "点击", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected String thumbFromSourceFile(String path) {
        FileAttachment attachment = (FileAttachment) message.getAttachment();
        String thumb = attachment.getThumbPathForSave();
        return BitmapDecoder.extractThumbnail(path, thumb) ? thumb : null;
    }
}
