package net.rong.italker.factory.net;


import net.rong.italker.factory.model.api.RspModel;
import net.rong.italker.factory.model.api.account.AccountRspModel;
import net.rong.italker.factory.model.api.account.LoginModel;
import net.rong.italker.factory.model.api.account.RegisterModel;
import net.rong.italker.factory.model.api.user.UserUpdateModel;
import net.rong.italker.factory.model.card.UserCard;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * 网络请求的所有接口
 */
public interface RemoteService {
    /**
     * 网络请求注册接口
     * @param model
     * @return
     */
    @POST("account/register")
    Call<RspModel<AccountRspModel>> accountRegister(@Body RegisterModel model);

    /**
     * 登录接口
     * @param model
     * @return
     */
    @POST("account/login")
    Call<RspModel<AccountRspModel>> accountLogin(@Body LoginModel model);

    /**
     * 绑定设备Id
     * @param pushId
     * @return
     */
    @POST("account/bind/{pushId}")
    Call<RspModel<AccountRspModel>> accountBind( @Path(encoded = true, value = "pushId") String pushId);

    //用户更新的接口
    @PUT("user")
    Call<RspModel<UserCard>> userUpdate(@Body UserUpdateModel model);

    //用户更新的接口
    @GET("user/search/{name}")
    Call<RspModel<List<UserCard>>> userSearch(@Path("name") String name);

    //用户关注的接口
    @PUT("user/follow/{userId}")
    Call<RspModel<UserCard>> userFollow(@Path("userId") String userId);

    //获取联系人列表
    @GET("user/contact")
    Call<RspModel<List<UserCard>>> userContacts();

    //获取联系人列表
    @GET("user/{userId}")
    Call<RspModel<UserCard>> userFind(@Path("userId")String userId);
}
