package com.netease.nim.demo.main.fragment;

import android.os.Bundle;

import com.netease.nim.demo.R;
import com.netease.nim.demo.main.model.MainTab;
import com.netease.nim.uikit.common.activity.UI;

/**
 * 应用主TAB页
 * Created by Lixin
 */
public class CircleListFragment extends MainTabFragment {

    private CircleFragment fragment;

    public CircleListFragment() {
        setContainerId(MainTab.CIRCLE.fragmentId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void onInit() {
        fragment = new CircleFragment();
        fragment.setContainerId(R.id.cicle_list_fragment);
        final UI activity = (UI) getActivity();
        fragment = (CircleFragment) activity.addFragment(fragment);
    }

}
