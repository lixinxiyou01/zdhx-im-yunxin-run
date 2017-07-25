package com.netease.nim.uikit.contact.core.viewholder;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.contact.core.item.ContactItem;
import com.netease.nim.uikit.contact.core.model.ContactDataAdapter;
import com.netease.nim.uikit.contact.core.model.IContact;

/**
 * Created by hzchenkang on 2017/4/6.
 */

public class OnlineStateContactHolder extends ContactHolder {

    @Override
    public void refresh(ContactDataAdapter adapter, int position, ContactItem item) {
        super.refresh(adapter, position, item);
        IContact contact = item.getContact();
        // 在线状态
        if (contact.getContactType() != IContact.Type.Friend || !NimUIKit.enableOnlineState()) {
            desc.setVisibility(View.GONE);
        } else {
            String onlineStateContent = NimUIKit.getOnlineStateContentProvider().getSimpleDisplay(contact.getContactId());
            if (TextUtils.isEmpty(onlineStateContent)) {
                desc.setVisibility(View.GONE);
            }else {
                desc.setVisibility(View.VISIBLE);
                desc.setText(onlineStateContent);
                if(onlineStateContent.contains("在线")) {
                    desc.setTextColor(Color.parseColor("#FF018F57"));
                } else {
                    desc.setTextColor(Color.parseColor("#ffaaaaaa"));
                }
            }
        }
    }
}
