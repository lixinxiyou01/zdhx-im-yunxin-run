package zhwx.ui.dcapp.repairs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.HashMap;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.repairs.model.RepairIndexData;
import zhwx.ui.dcapp.repairs.model.RepairListItem;

/**
 * 报修主页
 *
 * @author Li.xin @ 中电和讯
 * @date 2016-3-7 上午9:52:07
 */
public class RMainActivity extends BaseActivity {

    private Activity context;

    private FrameLayout top_bar;

    private HashMap<String, ParameterValue> map;

    private String noticeJson;

    private String indexJson;

    private TextView noticeTV;

    private Handler handler = new Handler();

    private ECProgressDialog mPostingdialog;

    private TextView count_wjd_a, count_wxz_a, count_dfk_a, count_yxh_a, count_dcl_b, count_ypd_b,
            count_ywc_b, count_fysp_b, count_wjd_c, count_wxz_c, count_hfk_c;

    private LinearLayout myOrderLay, managerLay, dirverLay;

    /**
     * 维修工
     */
    public static final int STARTFLAG_MYTASK = 0;

    /**
     * 报修人
     */
    public static final int STARTFLAG_MYREQUEST = 1;

    /**
     * 管理员
     */
    public static final int STARTFLAG_ORDERCHECK = 2;

    private PopupWindow publicPop;

    private View publicView;

    private TextView publicTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTopBarView().setVisibility(View.GONE);
        context = this;
        initView();
    }


    private void getIndex() {
        mPostingdialog = new ECProgressDialog(this, "正在获取信息");
        mPostingdialog.show();
        map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
        map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
        new ProgressThreadWrap(this, new RunnableWrap() {
            @Override
            public void run() {
                try {
                    indexJson = UrlUtil.getIndexData(ECApplication.getInstance().getV3Address(), map);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            refreshData1(indexJson);
                        }
                    }, 5);
                } catch (IOException e) {
                    e.printStackTrace();
                    ToastUtil.showMessage("请求失败，请稍后重试");
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            mPostingdialog.dismiss();
                        }
                    }, 5);
                }
            }
        }).start();
    }

    private void refreshData1(String indexJson) {

        System.out.println(indexJson);
        if (indexJson.contains("<html>")) {
            ToastUtil.showMessage("数据异常");
            return;
        }
        if ("{}".equals(indexJson)) {
            ToastUtil.showMessage("无权限");
            finish();
            return;
        }
        RepairIndexData data = new Gson().fromJson(indexJson, RepairIndexData.class);

        if (data.getNote()!= null && StringUtil.isNotBlank(data.getNote())) {
//            String  note = data.getNote().replaceAll("<p>","<br>").replaceAll("<\\/p>","<\\/br>");
            System.out.println("note" + data.getNote());
            noticeTV.setText(Html.fromHtml(data.getNote()));
            publicTV.setText(Html.fromHtml(data.getNote()));
        }

        //角色权限控制
        if (data.getMyRepair() != null) {
            count_wjd_a.setText(data.getMyRepair().getWjd());
            count_wjd_a.setVisibility("0".equals(data.getMyRepair().getWjd()) ? View.INVISIBLE : View.VISIBLE);
            count_wjd_a.bringToFront();

            count_wxz_a.setText(data.getMyRepair().getWxz());
            count_wxz_a.setVisibility("0".equals(data.getMyRepair().getWxz()) ? View.INVISIBLE : View.VISIBLE);
            count_wxz_a.bringToFront();

            count_dfk_a.setText(data.getMyRepair().getDfk());
            count_dfk_a.setVisibility("0".equals(data.getMyRepair().getDfk()) ? View.INVISIBLE : View.VISIBLE);
            count_dfk_a.bringToFront();

            count_yxh_a.setText(data.getMyRepair().getYxh());
            count_yxh_a.setVisibility("0".equals(data.getMyRepair().getYxh()) ? View.INVISIBLE : View.VISIBLE);
            count_yxh_a.bringToFront();
        }

        if (data.getRepairManage() != null) {
            managerLay.setVisibility(View.VISIBLE);
            count_dcl_b.setText(data.getRepairManage().getDcl());
            count_dcl_b.setVisibility("0".equals(data.getRepairManage().getDcl()) ? View.INVISIBLE : View.VISIBLE);

            count_ypd_b.setText(data.getRepairManage().getYpd());
            count_ypd_b.setVisibility("0".equals(data.getRepairManage().getYpd()) ? View.INVISIBLE : View.VISIBLE);

            count_ywc_b.setText(data.getRepairManage().getYwc());
            count_ywc_b.setVisibility("0".equals(data.getRepairManage().getYwc()) ? View.INVISIBLE : View.VISIBLE);

            count_fysp_b.setText(data.getRepairManage().getFysp());
            count_fysp_b.setVisibility("0".equals(data.getRepairManage().getFysp()) ? View.INVISIBLE : View.VISIBLE);
        }

        if (data.getMyTask() != null) {
            dirverLay.setVisibility(View.VISIBLE);
            count_wjd_c.setText(data.getMyTask().getWjd());
            count_wjd_c.setVisibility("0".equals(data.getMyTask().getWjd()) ? View.INVISIBLE : View.VISIBLE);

            count_wxz_c.setText(data.getMyTask().getWxz());
            count_wxz_c.setVisibility("0".equals(data.getMyTask().getWxz()) ? View.INVISIBLE : View.VISIBLE);

            count_hfk_c.setText(data.getMyTask().getYfk());
            count_hfk_c.setVisibility("0".equals(data.getMyTask().getYfk()) ? View.INVISIBLE : View.VISIBLE);
        }
        mPostingdialog.dismiss();
    }

    public void initView() {
        top_bar = (FrameLayout) findViewById(R.id.top_bar);
        setImmerseLayout(top_bar);
        noticeTV = (TextView) findViewById(R.id.noticeTV);

        count_wjd_a = (TextView) findViewById(R.id.count_wjd_a);
        count_wxz_a = (TextView) findViewById(R.id.count_wxz_a);
        count_dfk_a = (TextView) findViewById(R.id.count_dfk_a);
        count_yxh_a = (TextView) findViewById(R.id.count_yxh_a);
        count_dcl_b = (TextView) findViewById(R.id.count_dcl_b);
        count_ypd_b = (TextView) findViewById(R.id.count_ypd_b);
        count_ywc_b = (TextView) findViewById(R.id.count_ywc_b);
        count_fysp_b = (TextView) findViewById(R.id.count_fysp_b);
        count_wjd_c = (TextView) findViewById(R.id.count_wjd_c);
        count_wxz_c = (TextView) findViewById(R.id.count_wxz_c);
        count_hfk_c = (TextView) findViewById(R.id.count_hfk_c);

        myOrderLay = (LinearLayout) findViewById(R.id.myOrderLay);
        managerLay = (LinearLayout) findViewById(R.id.managerLay);
        dirverLay = (LinearLayout) findViewById(R.id.dirverLay);
        initPopMenu();
    }

    public void onExpend(View v) {
        showPop();
    }

    /**
     * 初始化popupWindow
     */
    private void initPopMenu() {
        // 公告详情弹窗
        publicView = context.getLayoutInflater().inflate(R.layout.layout_rm_public, null);
        if (publicPop == null) {
            publicPop = new PopupWindow(publicView, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, true);
        }
        if (publicPop.isShowing()) {
            publicPop.dismiss();
        }

        publicTV = (TextView) publicView.findViewById(R.id.publicTV);
    }

    public void showPop() {
        if (!publicPop.isShowing()) {
            publicPop.setFocusable(false);
            publicPop.setOutsideTouchable(true);
            publicPop.setAnimationStyle(R.style.PopupAnimation_cm);
            publicPop.showAtLocation(context.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        } else {
            publicPop.dismiss();
        }
    }

    public void onClose(View v) {
        if (publicPop.isShowing()) {
            publicPop.dismiss();
        }
    }


    /**
     * 订车人查看全部报修单
     */
    public void oncheckAllOrder(View v) {
        startActivity(new Intent(context, MyRepairManageActivity.class)
                .putExtra("startFlag", STARTFLAG_MYREQUEST)
                .putExtra("status", RepairListItem.CHECKSTATUS_ALL));
    }

    /**
     * 管理员查看全部维修单
     */
    public void oncheckAllOrderManager(View v) {
        startActivity(new Intent(context, MyRepairManageActivity.class)
                .putExtra("startFlag", STARTFLAG_ORDERCHECK)
                .putExtra("status", RepairListItem.CHECKSTATUS_ALL));
    }

    /**
     * 司机查看我的全部维修单
     */
    public void oncheckAllOrderDirver(View v) {
        startActivity(new Intent(context, MyRepairManageActivity.class)
                .putExtra("startFlag", STARTFLAG_MYTASK)
                .putExtra("status", RepairListItem.CHECKSTATUS_ALL));
    }

    /**
     * 报修
     */
    public void onRepair(View v) {
        startActivity(new Intent(context, DeviceKindActivity.class));
    }

    /**
     * 未接单
     */
    public void onWjdA(View v) {
        startActivity(new Intent(context, MyRepairManageActivity.class)
                .putExtra("startFlag", STARTFLAG_MYREQUEST)
                .putExtra("status", RepairListItem.CHECKSTATUS_WJD));
    }

    /**
     * 维修中
     */
    public void onWxzA(View v) {
        startActivity(new Intent(context, MyRepairManageActivity.class)
                .putExtra("startFlag", STARTFLAG_MYREQUEST)
                .putExtra("status", RepairListItem.CHECKSTATUS_WXZ));
    }

    /**
     * 待反馈
     */
    public void onDfkA(View v) {
        startActivity(new Intent(context, MyRepairManageActivity.class)
                .putExtra("startFlag", STARTFLAG_MYREQUEST)
                .putExtra("status", RepairListItem.CHECKSTATUS_DFK));
    }

    /**
     * 已修好
     */
    public void onYxhA(View v) {
        startActivity(new Intent(context, MyRepairManageActivity.class)
                .putExtra("startFlag", STARTFLAG_MYREQUEST)
                .putExtra("status", RepairListItem.CHECKSTATUS_YXH));
    }

    /**
     * 待处理
     */
    public void onDclB(View v) {
        startActivity(new Intent(context, MyRepairManageActivity.class)
                .putExtra("startFlag", STARTFLAG_ORDERCHECK)
                .putExtra("status", RepairListItem.CHECKSTATUS_DCL));
    }

    /**
     * 已派单
     */
    public void onYpdB(View v) {
        startActivity(new Intent(context, MyRepairManageActivity.class)
                .putExtra("startFlag", STARTFLAG_ORDERCHECK)
                .putExtra("status", RepairListItem.CHECKSTATUS_YPD));
    }

    /**
     * 已完成
     */
    public void onYwcB(View v) {
        startActivity(new Intent(context, MyRepairManageActivity.class)
                .putExtra("startFlag", STARTFLAG_ORDERCHECK)
                .putExtra("status", RepairListItem.CHECKSTATUS_YWC));
    }

    /**
     * 费用审批
     */
    public void onFyspB(View v) {
        startActivity(new Intent(context, MyRepairManageActivity.class)
                .putExtra("startFlag", STARTFLAG_ORDERCHECK)
                .putExtra("status", RepairListItem.CHECKSTATUS_FYSP));
    }

    /**
     * 未接单
     */
    public void onWjdC(View v) {
        startActivity(new Intent(context, MyRepairManageActivity.class)
                .putExtra("startFlag", STARTFLAG_MYTASK)
                .putExtra("status", RepairListItem.CHECKSTATUS_WJD));
    }

    /**
     * 维修中
     */
    public void onWxzC(View v) {
        startActivity(new Intent(context, MyRepairManageActivity.class)
                .putExtra("startFlag", STARTFLAG_MYTASK)
                .putExtra("status", RepairListItem.CHECKSTATUS_WXZ));
    }

    /**
     * 已反馈
     */
    public void onYfkC(View v) {
        startActivity(new Intent(context, MyRepairManageActivity.class)
                .putExtra("startFlag", STARTFLAG_MYTASK)
                .putExtra("status", RepairListItem.CHECKSTATUS_YFK));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getIndex();        //获取状态信息
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_re_main;
    }
}
