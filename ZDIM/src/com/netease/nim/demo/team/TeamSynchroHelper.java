package com.netease.nim.demo.team;

import android.content.Context;
import android.os.Handler;

import com.netease.nim.demo.ECApplication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.UrlUtil;

/**
 * Created by Android on 2017/3/3.
 */

public class TeamSynchroHelper {

    public static Handler handler = new Handler();
    public static Map<String, ParameterValue> map;

    public static void synchroTeam(String tId,Context context) {

        map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
        map.put("tid",new ParameterValue(tId));
        new ProgressThreadWrap(context, new RunnableWrap() {
            @Override
            public void run() {
                try {
                    final String flag = UrlUtil.synchroTeam(ECApplication.getInstance().getAddress(), map).trim();
                    handler.postDelayed(new Runnable() {
                        public void run() {

                        }
                    }, 5);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
