package com.sucetech.yijiamei.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.bean.RecycleMaterialDetails;
import com.sucetech.yijiamei.bean.WuLiaoBean;
import com.sucetech.yijiamei.manager.EventStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lihh on 2018/9/22.
 */

public class WuliaoItemView extends BaseView implements View.OnClickListener ,SuggistPopView.OnSuggistItemClick {
    private TextView name, beishu, weight, ok,price,volume;
    private String weightStr;
    public float commitWeightStr;
    public WuLiaoBean wuLiaoBean;
    private boolean isOK;
    private SuggistPopView mSuggistPopView;
    private List<WuLiaoBean> datas;
    public RecycleMaterialDetails recycleMaterialDetails;

    public WuliaoItemView(Context context, ViewGroup ParentView) {
        super(context, ParentView);
    }

    @Override
    public void updata(EventStatus status, Object obj) {
        switch (status){
            case weight:
                weightStr= (String) obj;
                break;
            case wuliaoList:
                datas.clear();
                datas.addAll((List<WuLiaoBean>) obj);
                break;
        }
    }

    @Override
    public void initView(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.wuliao_item_layout, null);
        this.addView(v, -1, -1);
        name = (TextView) v.findViewById(R.id.name);
        name.setOnClickListener(this);
        beishu = (TextView) v.findViewById(R.id.beishu);
        weight = (TextView) v.findViewById(R.id.weight);
        price= (TextView) v.findViewById(R.id.price);
        volume= (TextView) v.findViewById(R.id.volume);
        ok = (TextView) v.findViewById(R.id.ok);
        ok.setOnClickListener(this);
        datas=new ArrayList<>();
        for (int i = 0; i <6 ; i++) {
            WuLiaoBean wuLiaoBean=new WuLiaoBean();
            wuLiaoBean.name="塑料"+i;
            wuLiaoBean.index=i;
            datas.add(wuLiaoBean);
        }
        mSuggistPopView=new SuggistPopView(getContext(),datas,this);
        mSuggistPopView.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
        recycleMaterialDetails=new RecycleMaterialDetails();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                if (isOK) {
                    prentView.removeView(this);
                } else {
                    isOK=true;
                    ok.setText("删除");
                    float www= Float.valueOf(0);
                    if (beishu.getText().toString()!=null&&!beishu.getText().toString().equals("")){
                        www=Float.parseFloat(weightStr)*Float.parseFloat(beishu.getText().toString());
                    }else{
                        www=Float.parseFloat(weightStr);
                    }
                    weight.setText(www+"");
                    beishu.setEnabled(false);
                    recycleMaterialDetails.volume=volume.getText().toString();
                    recycleMaterialDetails.weight=www;
                }
                break;
            case R.id.name:
                mSuggistPopView.setWidth(this.getWidth());
                mSuggistPopView.showAsDropDown(((MainActivity)getContext()).mConBluthView.weidthStr,0,0);
                break;
        }
    }

    @Override
    public void onSuggistItemClick(int position, String str) {
        name.setText(str);
        recycleMaterialDetails.type=(byte)position;
        wuLiaoBean=datas.get(position);
        mSuggistPopView.dismiss();
    }
}
