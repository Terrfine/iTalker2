package net.rong.italker.push;

import com.igexin.sdk.PushManager;

import net.rong.italker.common.app.Application;
import net.rong.italker.factory.Factory;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //调用Factory进行初始化
        Factory.setup();
        //推送进行初始化
        PushManager.getInstance().initialize(this, AppPushService.class);
        PushManager.getInstance().registerPushIntentService(this, AppMessageReceiverService.class);
    }
}
