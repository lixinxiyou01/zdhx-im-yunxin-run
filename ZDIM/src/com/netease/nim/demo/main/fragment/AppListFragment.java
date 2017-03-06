package com.netease.nim.demo.main.fragment;

import android.os.Bundle;

import com.netease.nim.demo.R;
import com.netease.nim.demo.main.model.MainTab;
import com.netease.nim.uikit.common.activity.UI;

/**
 * 应用主TAB页
 * Created by Lixin
 */
public class AppListFragment extends MainTabFragment {

    private ApplicationFragmentGroup fragment;

    public AppListFragment() {
        setContainerId(MainTab.APP_LIST.fragmentId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void onInit() {
        fragment = new ApplicationFragmentGroup();
        fragment.setContainerId(R.id.app_list_fragment);
        final UI activity = (UI) getActivity();
        fragment = (ApplicationFragmentGroup) activity.addFragment(fragment);
    }

}
