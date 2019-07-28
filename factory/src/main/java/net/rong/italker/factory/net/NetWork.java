package net.rong.italker.factory.net;

import android.text.TextUtils;

import net.rong.italker.common.Common;
import net.rong.italker.factory.Factory;
import net.rong.italker.factory.persistence.Account;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetWork {
    private static NetWork instance;
    private Retrofit retrofit;
    static
    {
        instance = new NetWork();
    }

    private NetWork(){
    }

    //构建一个Retrofit
    public static Retrofit getRetrofit(){
        if(instance.retrofit != null)
            return instance.retrofit;
        OkHttpClient client = new OkHttpClient.Builder()
                //给所有的请求添加一个拦截器
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        //拿到我们的请求
                        Request original = chain.request();
                        //重新进行build
                        Request.Builder builder = original.newBuilder();
                        if(!TextUtils.isEmpty(Account.getToken())){
                            //注入一个token
                            builder.addHeader("token", Account.getToken());
                        }
                        builder.addHeader("Content-Type","application/json");
                        Request newRequest = builder.build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();
        Retrofit.Builder builder = new Retrofit.Builder();
        //设置电脑链接
        instance.retrofit =  builder.baseUrl(Common.Constance.API_URL)
                //设置client
                .client(client)
                //设置Json解析器
                .addConverterFactory(GsonConverterFactory.create(Factory.getGson()))
                .build();
        return instance.retrofit;
    }

    /**
     * 返回一个请求代理
     * @return
     */
    public static RemoteService remote(){
        return NetWork.getRetrofit().create(RemoteService.class);
    }
}
