package net.rong.italker.common.app;

import android.drm.DrmStore;
import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.widget.Toast;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.io.File;

public class Application extends android.app.Application {

    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     * 外部获取单例
     * @return
     */
    public static Application getInstance(){
        return instance;
    }

    /**
     * 获取缓存文件夹地址
     * @return
     */
    public static File getCacheDirFile(){
        return instance.getCacheDir();
    }

    /**
     * 获取头像的临时储存文件地址
     * @return
     */
    public static File getPortraitTmpFile(){
        File dir = new File(getCacheDirFile(),"portrait");
        dir.mkdir();

        //删除旧的缓存文件
        File[] files = dir.listFiles();
        if(files != null && files.length > 0){
            for(File file : files){
                file.delete();
            }
        }

        //返回一个当前文件戳的目录文件地址
        File path = new File(dir, SystemClock.uptimeMillis()+".jpg");
        return path.getAbsoluteFile();
    }

    /**
     * 获取声音文件的本地地址,True,每次返回的文件地址是一样的
     * @return 录音文件的地址
     */
    public static File getAudioTmpFile(boolean isTmp){
        File dir = new File(getCacheDirFile(),"audio");
        dir.mkdir();

        //删除旧的缓存文件
        File[] files = dir.listFiles();
        if(files != null && files.length > 0){
            for(File file : files){
                file.delete();
            }
        }

        //返回一个当前文件戳的目录文件地址
        File path = new File(getCacheDirFile(), isTmp? "tmp.mp3" : SystemClock.uptimeMillis()+".mp3");
        return path.getAbsoluteFile();
    }

    public static void showToast(final String msg){
        //Toast只能在主线程中显示，所以需要进行线程转换

        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                Toast.makeText(instance, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showToast(@StringRes int msgId){
        showToast(instance.getString(msgId));

    }}
