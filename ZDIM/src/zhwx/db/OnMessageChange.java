/*
 *  Copyright (c) 2015 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */package zhwx.db;

/**
 * Created by 容联•云通讯 Modify By Li.Xin @ 中电和讯 on 2015/3/18.
 */
public interface OnMessageChange {
    /**
     * 数据库改变
     */
    public void onChanged(String sessionId);
}
