package net.rong.italker.factory.net;

import android.text.format.DateFormat;
import android.util.Log;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import net.rong.italker.factory.Factory;
import net.rong.italker.utils.HashUtil;

import java.io.File;
import java.util.Date;

/**
 * 上传工具类，用于上传任意文件到阿里oss存储
 */
public class UploadHelper {
    private static final String TAG = UploadHelper.class.getSimpleName();
    private static final String ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";
    private static final String BUCKET_NAME = "myitalker2";
    public static OSS getClient(){

        String stsServer = "STS应用服务器地址，例如http://abc.com";
        // 推荐使用OSSAuthCredentialsProvider。token过期可以及时更新。
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("LTAI9qehF2KWcyRv", "jqAFAUwDiiVARIMHb1EI7F9UI5wKIZ");
        return  new OSSClient(Factory.app(), ENDPOINT, credentialProvider);
    }

    /**
     * 上传的最终方法，成功返回一个路径
     * @param objKey 上传上去后，在服务器上的独立的KEY
     * @param path 需要上传的文件路径
     * @return 存储的地址
     */
    private static String upload(String objKey, String path){
        PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, objKey, path);
        try{
            //初始化上传的client
            OSS client = getClient();
            //开始同步上传
            PutObjectResult result =  client.putObject(request);
            //得到外网可以访问的地址
            String url = client.presignPublicObjectURL(BUCKET_NAME, objKey);
            Log.d(TAG, String.format("PublicObjectUrl:%s",url));
            return url;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 上传普通图片
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadImage(String path){
        String key = getImageObjkey(path);
        return upload(key, path);
    }

    /**
     * 上传头像
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadPortrait(String path){
        String key = getPortraitObjkey(path);
        return upload(key, path);
    }

    /**
     * 上传音频
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadAudio(String path){
        String key = getAudioObjkey(path);
        return upload(key, path);
    }

    /**
     * 分月存储
     * @return yyyyMM
     */
    private static String getDateString(){
        return DateFormat.format("yyyyMM", new Date()).toString() ;
    }

    // image/201703/dswdasdasdasdwasx123.jpg
    private static String getImageObjkey(String path){
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("image/%s/%s.jpg", dateString, fileMd5);
    }

    //portrait/201703/dswdasdasdasdwasx123.jpg
    private static String getPortraitObjkey(String path){
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("protrait/%s/%s.jpg", dateString, fileMd5);
    }

    //audio/201703/dswdasdasdasdwasx123.jpg
    private static String getAudioObjkey(String path){
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("audio/%s/%s.mp3", dateString, fileMd5);
    }
}
