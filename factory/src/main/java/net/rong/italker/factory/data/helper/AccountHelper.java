package net.rong.italker.factory.data.helper;

import android.text.TextUtils;

import net.rong.italker.common.app.Activity;
import net.rong.italker.factory.Factory;
import net.rong.italker.factory.R;
import net.rong.italker.factory.data.DataSource;
import net.rong.italker.factory.model.api.RspModel;
import net.rong.italker.factory.model.api.account.AccountRspModel;
import net.rong.italker.factory.model.api.account.LoginModel;
import net.rong.italker.factory.model.api.account.RegisterModel;
import net.rong.italker.factory.model.db.User;
import net.rong.italker.factory.net.NetWork;
import net.rong.italker.factory.net.RemoteService;
import net.rong.italker.factory.persistence.Account;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountHelper {

    /**
     * 注册的接口，异步的调用
     * @param model 传递一个注册的model进来
     * @param callback 成功与失败的接口会送
     */
    public static void register(final RegisterModel model, final DataSource.Callback<User> callback){
        //调用Retrofit对我们的网络请求接口做代理
        RemoteService service = NetWork.remote();
        //得到一个Call
        Call<RspModel<AccountRspModel>> call = service.accountRegister(model);
        //异步的请求
        call.enqueue(new AccountRspCallback(callback));
    }

    /**
     * 登录的调用
     * @param model
     * @param callback
     */
    public static void login(final LoginModel model, final DataSource.Callback<User> callback){
        //调用Retrofit对我们的网络请求接口做代理
        RemoteService service = NetWork.remote();
        //得到一个Call
        Call<RspModel<AccountRspModel>> call = service.accountLogin(model);
        //异步的请求
        call.enqueue(new AccountRspCallback(callback));
    }

    /**
     * 对设备Id进行绑定的操作
     * @param callback
     */
    public static void bindPush(final DataSource.Callback<User> callback){
        //检查是否为空
        String pushId = Account.getPushId();
        if(TextUtils.isEmpty(pushId))
            return;
        RemoteService service = NetWork.remote();
        Call<RspModel<AccountRspModel>> call = service.accountBind(pushId);
        call.enqueue(new AccountRspCallback(callback));
    }

    /**
     * 请求的回调部分封装
     */
    private static class AccountRspCallback implements Callback<RspModel<AccountRspModel>>{

        final DataSource.Callback<User> callback;

        private AccountRspCallback(DataSource.Callback<User> callback) {
            this.callback = callback;
        }


        @Override
        public void onResponse(Call<RspModel<AccountRspModel>> call, Response<RspModel<AccountRspModel>> response) {
            //请求成功返回
            //从返回中得到我们的全局Model，内部是使用Gson进行解析
            RspModel<AccountRspModel> rspModel = response.body();
            if(rspModel.success()){
                //拿到实体
                AccountRspModel accountRspModel = rspModel.getResult();
                User user = accountRspModel.getUser();
                DbHelper.save(User.class, user);
                //进行的是数据库写入和缓存绑定
                //第一种存储方式
//                user.save();

                //第二种储存方式
                //FlowManager.getModelAdapter(User.class).save(user);
                //然后返回

                //第三种，事务
//                        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
//                        definition.beginTransactionAsync(new ITransaction() {
//                            @Override
//                            public void execute(DatabaseWrapper databaseWrapper) {
//                                FlowManager.getModelAdapter(User.class).save(user);
//                            }
//                        }).build().execute();
                //同步到XML持久化中
                Account.login(accountRspModel);
                //判断绑定状态，是否进行了绑定
                if(accountRspModel.isBind()){
                    //设置绑定状态为True
                    Account.setBind(true);
                    if(callback != null)
                        callback.onDataLoaded(user);
                }else {
                    //进行绑定设备
                    bindPush(callback);
                }
                if (callback != null) {
                    callback.onDataLoaded(accountRspModel.getUser());
                }
            }else {
                //错误解析
                Factory.decodeRspCode(rspModel, callback);
            }
        }

        @Override
        public void onFailure(Call<RspModel<AccountRspModel>> call, Throwable t) {
            if(callback != null)
                callback.onDataNotAvailable(R.string.data_network_error);
        }
    }
}
