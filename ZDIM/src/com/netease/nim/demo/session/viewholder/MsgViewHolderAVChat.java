package com.netease.nim.demo.session.viewholder;

import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;

/**
 * Created by zhoujianghua on 2015/8/6.
 */
public class MsgViewHolderAVChat extends MsgViewHolderBase {
    @Override
    protected int getContentResId() {
        return 0;
    }

    @Override
    protected void inflateContentView() {

    }

    @Override
    protected void bindContentView() {

    }

//    private ImageView typeImage;
//    private TextView statusLabel;
//
//    @Override
//    protected int getContentResId() {
//        return R.layout.nim_message_item_avchat;
//    }
//
//    @Override
//    protected void inflateContentView() {
//        typeImage = findViewById(R.id.message_item_avchat_type_img);
//        statusLabel = findViewById(R.id.message_item_avchat_state);
//    }
//
//    @Override
//    protected void bindContentView() {
//        if (message.getAttachment() == null) {
//            return;
//        }
//
//        layoutByDirection();
//
//        refreshContent();
//    }
//
//    private void layoutByDirection() {
//        AVChatAttachment attachment = (AVChatAttachment) message.getAttachment();
//
//        if (isReceivedMessage()) {
//            if (attachment.getType() == AVChatType.AUDIO) {
//                typeImage.setImageResource(R.drawable.avchat_left_type_audio);
//            } else {
//                typeImage.setImageResource(R.drawable.avchat_left_type_video);
//            }
//            statusLabel.setTextColor(context.getResources().getColor(R.color.color_grey_999999));
//        } else {
//            if (attachment.getType() == AVChatType.AUDIO) {
//                typeImage.setImageResource(R.drawable.avchat_right_type_audio);
//            } else {
//                typeImage.setImageResource(R.drawable.avchat_right_type_video);
//            }
//            statusLabel.setTextColor(Color.WHITE);
//        }
//    }
//
//    private void refreshContent() {
//        AVChatAttachment attachment = (AVChatAttachment) message.getAttachment();
//
//        String textString = "";
//        switch (attachment.getState()) {
//        case Success: //成功接听
//            textString = TimeUtil.secToTime(attachment.getDuration());
//            break;
//        case Missed: //未接听
//        case Rejected: //主动拒绝
//            textString = context.getString(R.string.avchat_no_pick_up);
//            break;
//        default:
//            break;
//        }
//
//        statusLabel.setText(textString);
//    }
}
