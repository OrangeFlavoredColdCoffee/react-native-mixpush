package com.duanglink.huaweipush;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.duanglink.flymepush.FlymePushManager;
import com.duanglink.getui.GeTuiManager;
import com.duanglink.mipush.MiPushManager;
import com.facebook.react.BuildConfig;
import com.facebook.react.ReactActivity;
import com.facebook.react.ReactPackage;
import com.facebook.soloader.SoLoader;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.PushException;
import com.huawei.hms.support.api.push.TokenResult;
import com.duanglink.getui.GeTuiManager;
import java.util.List;

/**
 * Created by wangheng on 2017/11/27.
 */
public class HuaweiPushActivity extends ReactActivity implements HuaweiApiClient.ConnectionCallbacks, HuaweiApiClient.OnConnectionFailedListener {
    public static final String TAG = "HuaweiPushActivity";
    //华为移动服务Client
    private HuaweiApiClient client;
    //作用同startactivityforresult方法中的requestcode
    private static final int REQUEST_HMS_RESOLVE_ERROR = 1000;
    //如果开发者在onConnectionFailed调用了resolveError接口，那么错误结果会通过onActivityResult返回
    //具体的返回码通过该字段获取
    public static final String EXTRA_RESULT = "intent.extra.RESULT";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判断华为手机接入
        if(Build.BRAND.equalsIgnoreCase("huawei")){
                //连接回调以及连接失败监听
            client = new HuaweiApiClient.Builder(this)
                        .addApi(HuaweiPush.PUSH_API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();
            client.connect();
        }if(Build.BRAND.equalsIgnoreCase("xiaomi")){
            MiPushManager mipush=new MiPushManager("2882303761517677749","5231767711749");
            mipush.registerPush(this.getApplicationContext());
        }
        if(Build.BRAND.equalsIgnoreCase("meizu")){
            FlymePushManager meizupush=new FlymePushManager("111821","2110492f96964fb986a9c2cc6d680efe");
            meizupush.registerPush(this.getApplicationContext());
        } else{
            GeTuiManager getui=new GeTuiManager();
            getui.registerPush(this.getApplicationContext());
        }
    }

    @Override
    protected String getMainComponentName() {
        return "mixpush";
    }

    @Override
    public void onConnected() {
        //华为移动服务client连接成功，在这边处理业务自己的事件
        Toast.makeText(getApplicationContext(), "HuaweiApiClient 连接成功", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "HuaweiApiClient 连接成功");
        getTokenAsyn();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        //HuaweiApiClient异常断开连接
        //if (!this.isDestroyed() && !this.isFinishing()) {
        if (!this.isFinishing()) {
            client.connect();
        }
        Log.i(TAG, "HuaweiApiClient 连接断开");
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Toast.makeText(getApplicationContext(),  "HuaweiApiClient连接失败，错误码：" + arg0.getErrorCode(), Toast.LENGTH_SHORT).show();
        Log.i(TAG, "HuaweiApiClient连接失败，错误码：" + arg0.getErrorCode());
        if(HuaweiApiAvailability.getInstance().isUserResolvableError(arg0.getErrorCode())) {
            HuaweiApiAvailability.getInstance().resolveError(this, arg0.getErrorCode(), REQUEST_HMS_RESOLVE_ERROR);
        } else {
            //其他错误码请参见开发指南或者API文档
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //建议在onDestroy的时候停止连接华为移动服务
        //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
        if(client!=null)client.disconnect();
    }

    private void getTokenAsyn() {
        PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(client);
        tokenResult.setResultCallback(new ResultCallback<TokenResult>() {
            @Override
            public void onResult(TokenResult result) {
                if(result.getTokenRes().getRetCode() == 0) {
                    Toast.makeText(getApplicationContext(),  "获取Token成功" , Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),  "获取Token失败" , Toast.LENGTH_SHORT).show();
                }
             //Log.i(TAG, "获取Token成功,token:"+result.getTokenRes().getToken());
             //getPushStatus();
            //这边的结果只表明接口调用成功，是否能收到响应结果只在广播中接收
            }
        });
    }

    private void deleteToken(String token) {
        Log.i(TAG, "删除Token：" + token);
        if (!TextUtils.isEmpty(token)) {
            try {
                HuaweiPush.HuaweiPushApi.deleteToken(client, token);
            } catch (PushException e) {
                Log.i(TAG, "删除Token失败:" + e.getMessage());
            }
        }
    }

    private void getPushStatus() {
        Log.i(TAG, "开始获取PUSH连接状态");
        new Thread() {
            public void run() {
                Toast.makeText(getApplicationContext(),  "开始获取PUSH连接状态" , Toast.LENGTH_SHORT).show();
                HuaweiPush.HuaweiPushApi.getPushState(client);
            };
        }.start();
    }
}