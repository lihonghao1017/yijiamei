package com.sucetech.yijiamei.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.ericssonlabs.BarCodeTestActivity;
import com.google.gson.Gson;
import com.sucetech.yijiamei.MainActivity;
import com.sucetech.yijiamei.R;
import com.sucetech.yijiamei.UserMsg;
import com.sucetech.yijiamei.bean.FormImage;
import com.sucetech.yijiamei.bean.RecycleMaterial;
import com.sucetech.yijiamei.bean.RecycleMaterialDetails;
import com.sucetech.yijiamei.bean.RecycleMaterialDto;
import com.sucetech.yijiamei.manager.EventStatus;
import com.sucetech.yijiamei.request.HeGeWUliaoRequest;
import com.sucetech.yijiamei.request.LoginRequest;
import com.sucetech.yijiamei.request.MultipartRequest;
import com.sucetech.yijiamei.request.PostUploadRequest;
import com.zxing.activity.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by lihh on 2018/9/18.
 */

public class ConBluthView extends BaseView implements View.OnClickListener {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice device;
    private Bluetooth_Scale mBl_Scale;
    private Handler mHandler;
    private SCALENOW scalenow = new SCALENOW();
    private Boolean b_scaleIsConnect = Boolean.FALSE;
    private Boolean bisClosed = false;
    public TextView weidthStr;
    private View speedLayout;
    private View addView;
    private LinearLayout wuliaoLayout;
    private View commitLayout, commitHeGe, commitNoHeGe;
    private LinearLayout noHeGeViewLayout;
    private NoHeGeView noHeGeView;


    public ConBluthView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConBluthView(Context context) {
        super(context);
    }

    @Override
    public void initView(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.connect_bluth_layout, null);
        weidthStr = (TextView) v.findViewById(R.id.weidthStr);
        noHeGeViewLayout = (LinearLayout) v.findViewById(R.id.noHeGeViewLayout);
        speedLayout = v.findViewById(R.id.speedLayout);
        speedLayout.setOnClickListener(this);
        addView = v.findViewById(R.id.addView);
        addView.setOnClickListener(this);
        wuliaoLayout = (LinearLayout) v.findViewById(R.id.wuliaoLayout);
        commitLayout = v.findViewById(R.id.commitLayout);
        commitHeGe = v.findViewById(R.id.commitHeGe);
        commitHeGe.setOnClickListener(this);
        commitNoHeGe = v.findViewById(R.id.commitNoHeGe);
        commitNoHeGe.setOnClickListener(this);
        addView(v, -1, -1);
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        ((MainActivity) getContext()).hideProgressDailogView();
                        Bundle bundle = msg.getData();
                        GetWeight(bundle.getByteArray("weight"));
                        if (scalenow.bOverFlag) {
                            weidthStr.setText("----");
                        } else {
                            String wei = scalenow.sformatNetWeight.trim();
                            weidthStr.setText(wei);
                            mEventManager.notifyObservers(EventStatus.weight, wei);
                        }
                        speedLayout.setBackgroundResource(R.drawable.nav_ic_speed_limit);
                        speedLayout.setEnabled(false);
                        if (addView.getVisibility() == View.GONE) {
                            wuliaoLayout.addView(new WuliaoItemView(getContext(), wuliaoLayout));
                            addView.setVisibility(View.VISIBLE);
                            commitLayout.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 2:
                        ((MainActivity) getContext()).hideProgressDailogView();
                        Toast.makeText(getContext(), "蓝牙链接失败", Toast.LENGTH_SHORT).show();
                        if (msg.arg1 < 2) {
                            weidthStr.setText("----");
                            b_scaleIsConnect = Boolean.FALSE;
                            speedLayout.setEnabled(true);
                            speedLayout.setBackgroundResource(R.drawable.nav_ic_speed_limit_g);
                        } else b_scaleIsConnect = Boolean.TRUE;
                        break;
                    case 3:
                        connectBluth((String) msg.obj);
                        break;

                }
            }
        };
        mBl_Scale = new Bluetooth_Scale(getContext(), mHandler);
    }

    public void startBlouth(String blouth) {
        ((MainActivity) getContext()).showProgressDailogView("蓝牙链接中...");
        this.setVisibility(View.VISIBLE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getContext(), "此设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }
        connectBluth(blouth);
    }

    private void connectBluth(String bluth) {
        if (mBluetoothAdapter.enable()) {
            try {
                device = mBluetoothAdapter.getRemoteDevice(bluth.split("-")[1]);
                if (device != null) {
                    mBl_Scale.connect(device);
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "二维码格式异常-->" + bluth, Toast.LENGTH_SHORT).show();
            }
        } else {
            Message message = Message.obtain();
            message.what = 3;
            message.obj = bluth;
            mHandler.sendMessageDelayed(message, 1000);
        }
    }

    @Override
    public void updata(EventStatus status, Object obj) {
        switch (status) {
            case logined:
                this.setVisibility(View.VISIBLE);
                break;
            case loginFail:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.speedLayout:
                Intent openCameraIntent = new Intent(getContext(), CaptureActivity.class);
                ((MainActivity) getContext()).startActivityForResult(openCameraIntent, R.id.speedLayout);
                break;
            case R.id.addView:
                wuliaoLayout.addView(new WuliaoItemView(getContext(), wuliaoLayout));
                break;
            case R.id.commitHeGe:
                imageUpload();
                break;
            case R.id.commitNoHeGe:
                noHeGeViewLayout.setVisibility(View.VISIBLE);
                noHeGeView = new NoHeGeView(getContext(), noHeGeViewLayout);
                noHeGeViewLayout.addView(noHeGeView);
                commitNoHeGe.setEnabled(false);
                break;
        }
    }

    public void showImages(Bitmap bitmap, File file) {
        if (noHeGeView != null) {
            noHeGeView.showImages(bitmap, file);
        }

    }

    public void onDestroy() {
        if (mBl_Scale != null) mBl_Scale.stop();
        bisClosed = true;
        System.exit(0);
    }

    void GetWeight(byte[] databuf) {
        int i, j, offset = 6;
        boolean StartFalg = false;
        scalenow.bZeroFlag = true;
        scalenow.bOverFlag = false;
        scalenow.bWeiStaFlag = false;
        switch (databuf[0]) {
            case 'o':
            case 'O':
                scalenow.bOverFlag = true;
                break;
            case 'u':
            case 'U':
                scalenow.bWeiStaFlag = false;
                offset = 6;    //6
                break;
            case 's':
            case 'S':
                scalenow.bWeiStaFlag = true;
                break;
        }
        if (databuf[5] == '-') offset = 5;
        for (i = 0; i < 14; i++) {
            if (databuf[i + offset] == '\'') databuf[i + offset] = '.';
            if (StartFalg) {
                if (((databuf[i + offset] > '9') || (databuf[i + offset] < '.')) && (!((databuf[i + offset] == ' ') && (databuf[i + offset + 1] <= '9')))) {
                    break;
                }
            } else if ((databuf[i + offset] >= '0') && (databuf[i + offset] <= '9')) {
                StartFalg = true;
                if (databuf[i + offset] != '0') scalenow.bZeroFlag = false;
            }
        }
        scalenow.sformatNetWeight = new String(databuf, offset, i);


        for (j = 0; j < 6; j++) {
            if (databuf[i + j + offset] < 0x20) {
                break;
            }
        }
        scalenow.sUnit = new String(databuf, i + offset, j);

    }

    public static class SCALENOW {
        public String sformatNetWeight = "0";
        public String sUnit = "0";
        public boolean bWeiStaFlag;
        public boolean bZeroFlag;
        public boolean bOverFlag;
    }


    private void imageUpload() {
        String url = "http://60.205.139.90:81/api/v1/yijiamei/recycle";
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("LLL", "成功--》"+response);
//                        try {
//                            JSONObject jObj = new JSONObject(response);
//                            String status = jObj.getString("status");
//                            String message = jObj.getString("message");
//                            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
//                        } catch (JSONException e) {
//                            // JSON error
//                            e.printStackTrace();
//                            Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LLL", "error.getMessage()--->"+error.getMessage());
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {


                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", UserMsg.getToken());
                return headers;
            }

        };
        String data=getRecycleMaterialDto();
        Log.e("data","data-->"+data);

                smr.addMultipartParam("data", "application/json",data);

                File f= new File("/sdcard/mcarnavi/updataimg.jpg");
        smr.addFile("licenses",f.getPath());
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(smr);

    }

    private String getRecycleMaterialDto(){
        RecycleMaterialDto rmd=new RecycleMaterialDto();
        rmd.details=new ArrayList<>();
        RecycleMaterialDetails details=new RecycleMaterialDetails();
        details.weight=7.5f;
        details.flowType=1;
        details.volume="1*2*3";
        details.id=1001;
        rmd.details.add(details);



        rmd.material=new RecycleMaterial();
        rmd.material.description="onetwo";
        rmd.material.id=1111;
        rmd.material.licenseNumber="1";
        rmd.material.medicalId=1;
        rmd.material.packager="laoli";
        rmd.material.price=7.7f;
        rmd.material.selfCheck="ok";

        Gson gson=new Gson();
        String js=gson.toJson(rmd);
        return js;
    }
}
