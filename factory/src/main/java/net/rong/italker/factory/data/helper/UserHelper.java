package net.rong.italker.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.rong.italker.factory.Factory;
import net.rong.italker.factory.R;
import net.rong.italker.factory.data.DataSource;
import net.rong.italker.factory.model.api.RspModel;
import net.rong.italker.factory.model.api.account.AccountRspModel;
import net.rong.italker.factory.model.api.user.UserUpdateModel;
import net.rong.italker.factory.model.card.UserCard;
import net.rong.italker.factory.model.db.User;
import net.rong.italker.factory.model.db.User_Table;
import net.rong.italker.factory.net.NetWork;
import net.rong.italker.factory.net.RemoteService;
import net.rong.italker.factory.presenter.contact.FollowPresenter;
import net.rong.italker.utils.CollectionUtil;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHelper {
    //更新用户信息的操作，异步的
    public static void update(UserUpdateModel model, final DataSource.Callback<UserCard> callback){
        //调用Retrofit对我们的网络请求接口做代理
        RemoteService service = NetWork.remote();
        //得到一个Call
        Call<RspModel<UserCard>> call = service.userUpdate(model);
        //异步的请求
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if(rspModel.success()){
                    UserCard userCard = rspModel.getResult();
                    //唤起进行保存的操作
                    Factory.getUserCenter().dispatch(userCard);
                    //返回成功
                    callback.onDataLoaded(userCard);
                }else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    //搜索的操作
    public static Call search(String name, final DataSource.Callback<List<UserCard>> callback){
        //调用Retrofit对我们的网络请求接口做代理
        RemoteService service = NetWork.remote();
        //得到一个Call
        Call<RspModel<List<UserCard>>> call = service.userSearch(name);
        //异步的请求
        call.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if(rspModel.success()){
                    callback.onDataLoaded(rspModel.getResult());
                }else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
        //把当前的调度者返回
        return call;
    }

    /**
     * 关注的网络请求
     * @param id
     * @param callback
     */
    public static void follow(String id, final DataSource.Callback<UserCard> callback) {
        //调用Retrofit对我们的网络请求接口做代理
        RemoteService service = NetWork.remote();
        //得到一个Call
        Call<RspModel<UserCard>> call = service.userFollow(id);
        //异步的请求
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if(rspModel.success()){
                    UserCard userCard = rspModel.getResult();
                    //唤起进行保存的操作
                    Factory.getUserCenter().dispatch(userCard);
//                    User user = userCard.build();
//                    //保存并通知联系人刷新
//                    DbHelper.save(User.class, user);
                    callback.onDataLoaded(userCard);
                }else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    //刷新联系人的操作，不需要CallBack，直接存储到数据库，
    //并通过数据库观察者进行通知界面更新
    //界面更新的时候进行对比，然后进行差异更新
    public static void refreshContacts(){
        //调用Retrofit对我们的网络请求接口做代理
        RemoteService service = NetWork.remote();
        //得到一个Call
        Call<RspModel<List<UserCard>>> call = service.userContacts();
        //异步的请求
        call.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if(rspModel.success()){
                    List<UserCard> cards = rspModel.getResult();
                    if(cards == null || cards.size() == 0)
                        return;
                    UserCard[] cards1 = CollectionUtil.toArray(cards, UserCard.class);
                    //或者 cards1 = cards.toArray(new UserCard[0]);
                    Factory.getUserCenter().dispatch(cards1);
                }else {
                    Factory.decodeRspCode(rspModel, null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                //nothing
            }
        });
    }

    public static User findFromLocal(String id){
        return SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(id))
                .querySingle();
    }

    //从网络查询某用户的信息
    public static User findFromNet(String id){
        RemoteService remoteService = NetWork.remote();
        try {
            Response<RspModel<UserCard>> response = remoteService.userFind(id).execute();
            UserCard card = response.body().getResult();
            if(card != null){
                User user = card.build();
                // 数据库刷新并进行通知
                Factory.getUserCenter().dispatch(card);
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 搜索一个用户，优先本地缓存，没有则从网络拉取
     * @param id
     * @return
     */
    public static User search(String id){
        User user = findFromLocal(id);
        if(user == null){
            return findFromNet(id);
        }
        return user;
    }

    //优先网络查询
    public static User searchFirstOfNet(String id){
        User user = findFromNet(id);
        if(user == null){
            return findFromLocal(id);
        }
        return user;
    }
}
