package com.netease.nim.demo.session.action;

import android.content.Intent;

import com.netease.nim.demo.R;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nim.uikit.session.constant.RequestCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;

import zhwx.common.plugin.FileExplorerActivity;


/**
 * Created by hzxuwen on 2015/6/11.
 */
public class FileAction extends BaseAction {

    public FileAction() {
        super(R.drawable.message_plus_file_selector, R.string.input_panel_file);
    }

    /**
     * **********************文件************************
     */
    private void chooseFile() {

        getActivity().startActivityForResult(new Intent(getActivity(), FileExplorerActivity.class), makeRequestCode(RequestCode.GET_LOCAL_FILE));
//        FileBrowserActivity.startActivityForResult(getActivity(), makeRequestCode(RequestCode.GET_LOCAL_FILE));
    }

    @Override
    public void onClick() {
        chooseFile();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.GET_LOCAL_FILE) {
//            String path = data.getStringExtra(FileBrowserActivity.EXTRA_DATA_PATH);
            String path = data.getStringExtra("choosed_file_path");
            File file = new File(path);
            IMMessage message = MessageBuilder.createFileMessage(getAccount(), getSessionType(), file, file.getName());
            sendMessage(message);
        }
    }
}
