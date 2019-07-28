package net.rong.italker.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;

import net.rong.italker.factory.Factory;
import net.rong.italker.factory.data.helper.AccountHelper;
import net.rong.italker.factory.persistence.Account;

/**
 * 个推消息接收器
 */
public class MessageReceiver extends BroadcastReceiver {
    private static final String TAG = MessageReceiver.class.getName();
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null)
            return;
        Bundle bundle = intent.getExtras();
        //判断当前消息的意图
        switch (bundle.getInt(PushConsts.CMD_ACTION)){
            case PushConsts.GET_CLIENTID:
                Log.i(TAG, "GET_CLIENTID:" + bundle.toString());
                //当id初始化的时候
                //获取设备id
                onClientInit(bundle.getString("clientid"));
                break;
            case PushConsts.GET_MSG_DATA:
                //常规消息送达
                byte[] payload = bundle.getByteArray("payload");
                if(payload != null){
                    String message = new String (payload);
                    Log.i(TAG, "GET_MSG_DATA:" + message);
                    onMessageArrived(message);
                }
                break;
            default:
                Log.i(TAG,"other"+bundle.toString());
                break;
        }
    }

    /**
     * 当id初始化的时候
     * @param cid 设备id
     */
    private void onClientInit(String cid){
        Account.setPushId(cid);
        if(Account.isLogin()){
            //账户登录状态，进行一次PushId绑定
            AccountHelper.bindPush(null);
        }
    }

    /**
     * 消息送达时
     * @param message
     */
    private void onMessageArrived(String message){
        Factory.dispatchPush(message);
    }
}
