package com.netease.nim.uikit.session.module.input;

import android.text.Editable;

/**
 * Created by hzchenkang on 2016/12/1.
 */

public interface MessageEditWatcher {

    void afterTextChanged(Editable editable, int start, int count);

}
