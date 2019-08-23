package net.rong.italker.factory.net;


import net.rong.italker.factory.model.api.RspModel;
import net.rong.italker.factory.model.api.account.AccountRspModel;
import net.rong.italker.factory.model.api.account.LoginModel;
import net.rong.italker.factory.model.api.account.RegisterModel;
import net.rong.italker.factory.model.api.group.GroupCreateModel;
import net.rong.italker.factory.model.api.group.GroupMemberAddModel;
import net.rong.italker.factory.model.api.message.MessageCreateModel;
import net.rong.italker.factory.model.api.user.UserUpdateModel;
import net.rong.italker.factory.model.card.GroupCard;
import net.rong.italker.factory.model.card.GroupMemberCard;
import net.rong.italker.factory.model.card.MessageCard;
import net.rong.italker.factory.model.card.UserCard;
import net.rong.italker.factory.model.db.Group;

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
     *
     * @param model
     * @return
     */
    @POST("account/register")
    Call<RspModel<AccountRspModel>> accountRegister(@Body RegisterModel model);

    /**
     * 登录接口
     *
     * @param model
     * @return
     */
    @POST("account/login")
    Call<RspModel<AccountRspModel>> accountLogin(@Body LoginModel model);

    /**
     * 绑定设备Id
     *
     * @param pushId
     * @return
     */
    @POST("account/bind/{pushId}")
    Call<RspModel<AccountRspModel>> accountBind(@Path(encoded = true, value = "pushId") String pushId);

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

    //查询某人的消息
    @GET("user/{userId}")
    Call<RspModel<UserCard>> userFind(@Path("userId") String userId);

    @POST("msg")
    Call<RspModel<MessageCard>> msgPush(@Body MessageCreateModel model);

    //创建群
    @POST("group")
    Call<RspModel<GroupCard>> groupCreate(@Body GroupCreateModel model);

    //查询群的信息
    @GET("group/{groupId}")
    Call<RspModel<GroupCard>> groupFind(@Path("groupId") String groupId);

    //用户更新的接口
    @GET("group/search/{name}")
    Call<RspModel<List<GroupCard>>> groupSearch(@Path(value = "name", encoded = true) String name);

    //获取群列表
    @GET("group/list/{date}")
    Call<RspModel<List<GroupCard>>> groups(@Path(value = "date", encoded = true) String date);

    //群成员列表
    @GET("group/{groupId}/member")
    Call<RspModel<List<GroupMemberCard>>> groupMembers(@Path("groupId") String groupId);

    //给群添加成员
    @POST("group/{groupId}/member")
    Call<RspModel<List<GroupMemberCard>>> groupMemberAdd(@Path("groupId") String groupId, @Body GroupMemberAddModel model);
}
