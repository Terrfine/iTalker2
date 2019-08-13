package net.rong.italker.factory;

import android.support.annotation.StringRes;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import net.rong.italker.common.app.Application;
import net.rong.italker.factory.data.DataSource;
import net.rong.italker.factory.data.group.GroupCenter;
import net.rong.italker.factory.data.group.GroupDispatcher;
import net.rong.italker.factory.data.message.MessageCenter;
import net.rong.italker.factory.data.message.MessageDispatcher;
import net.rong.italker.factory.data.user.UserCenter;
import net.rong.italker.factory.data.user.UserDispatcher;
import net.rong.italker.factory.model.api.PushModel;
import net.rong.italker.factory.model.api.RspModel;
import net.rong.italker.factory.model.card.GroupCard;
import net.rong.italker.factory.model.card.GroupMemberCard;
import net.rong.italker.factory.model.card.MessageCard;
import net.rong.italker.factory.model.card.UserCard;
import net.rong.italker.factory.model.db.Group;
import net.rong.italker.factory.persistence.Account;
import net.rong.italker.factory.utils.DBFlowExclusionStrategy;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Factory {
    private static final String TAG = Factory.class.getSimpleName();
    //单例模式
    private static final Factory instance;
    private final Executor executor;
    //全局的Gson
    private final Gson gson;
    static {
        instance = new Factory();
    }

    private Factory(){
        //新建一个4个线程的线程池
        executor = Executors.newFixedThreadPool(4);
        gson = new GsonBuilder()
                //设置时间格式
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                //TODO 设置一个过滤器，数据库级别的Model不进行Json转换
                .setExclusionStrategies(new DBFlowExclusionStrategy())
                .create();
    }

    /**
     * 初始化
     */
    public static void setup(){
        //初始化数据库
        FlowManager.init(new FlowConfig.Builder(app())
                .openDatabasesOnInit(true) //数据库初始化的时候就打开
                .build());
        //持久化数据进行初始化
        Account.load(app());
    }

    /**
     * @return 全局的Application
     */
    public static Application app(){
        return Application.getInstance();
    }

    /**
     * 异步运行的方法
     * @param runnable
     */
    public static void runOnAsync(Runnable runnable){
        //异步执行
        instance.executor.execute(runnable);
    }

    /**
     * 返回一个全局的Gson，在这立刻进行Gson的一些全局的初始化
     * @return
     */
    public static Gson getGson(){
        return instance.gson;
    }

    /**
     * 进行错误Code的解析，把网络返回的Code值进行统一的规划并返回一个String资源
     * @param model
     * @param callback
     */
    public static void decodeRspCode(RspModel model, DataSource.FailedCallback callback){
        if(model == null)
            return;
        switch (model.getCode()){
            case RspModel.SUCCEED:
                return;
            case RspModel.ERROR_SERVICE:
                decodeRspCode(R.string.data_rsp_error_service, callback);
            case RspModel.ERROR_NOT_FOUND_USER:
                decodeRspCode(R.string.data_rsp_error_not_found_user, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_GROUP:
                decodeRspCode(R.string.data_rsp_error_not_found_group, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_GROUP_MEMBER:
                decodeRspCode(R.string.data_rsp_error_not_found_group_member, callback);
                break;
            case RspModel.ERROR_CREATE_USER:
                decodeRspCode(R.string.data_rsp_error_create_user, callback);
                break;
            case RspModel.ERROR_CREATE_GROUP:
                decodeRspCode(R.string.data_rsp_error_create_group, callback);
                break;
            case RspModel.ERROR_CREATE_MESSAGE:
                decodeRspCode(R.string.data_rsp_error_create_message, callback);
                break;
            case RspModel.ERROR_PARAMETERS:
                decodeRspCode(R.string.data_rsp_error_parameters, callback);
                break;
            case RspModel.ERROR_PARAMETERS_EXIST_ACCOUNT:
                decodeRspCode(R.string.data_rsp_error_parameters_exist_account, callback);
                break;
            case RspModel.ERROR_PARAMETERS_EXIST_NAME:
                decodeRspCode(R.string.data_rsp_error_parameters_exist_name, callback);
                break;
            case RspModel.ERROR_ACCOUNT_TOKEN:
                Application.showToast(R.string.data_rsp_error_account_token);
                instance.logout();
                break;
            case RspModel.ERROR_ACCOUNT_LOGIN:
                decodeRspCode(R.string.data_rsp_error_account_login, callback);
                break;
            case RspModel.ERROR_ACCOUNT_REGISTER:
                decodeRspCode(R.string.data_rsp_error_account_register, callback);
                break;
            case RspModel.ERROR_ACCOUNT_NO_PERMISSION:
                decodeRspCode(R.string.data_rsp_error_account_no_permission, callback);
                break;
            case RspModel.ERROR_UNKNOWN:
            default:
                decodeRspCode(R.string.data_rsp_error_unknown, callback);
                break;
        }
    }

    private static void decodeRspCode(@StringRes int resId, DataSource.FailedCallback callback){
        if(callback!= null){
            callback.onDataNotAvailable(resId);
        }
    }

    /**
     * 收到账户退出的消息需要进行账户退出，进行重新登录
     */
    private void logout(){

    }


    /**
     * 处理推送来的消息
     * @param str
     */
    public static void dispatchPush(String str){
        //首先检查登录状态
        if(!Account.isLogin())
            return;
        PushModel model = PushModel.decode(str);
        if(model == null){
            return;
        }
        Log.e(TAG, model.toString());
        //对推送集合进行便利
        for (PushModel.Entity entity : model.getEntities()) {
            switch (entity.type){
                case PushModel.ENTITY_TYPE_LOGOUT:
                    instance.logout();
                    //退出情况下，直接返回，并且不可继续
                    return;
                case PushModel.ENTITY_TYPE_MESSAGE:{
                    //普通消息
                    MessageCard card = getGson().fromJson(entity.content, MessageCard.class);
                    getMessageCenter().dispatch(card);
                    break;
                }
                case PushModel.ENTITY_TYPE_ADD_FRIEND:{
                    //好友添加
                    UserCard card = getGson().fromJson(entity.content, UserCard.class);
                    getUserCenter().dispatch(card);
                    break;
                }
                case PushModel.ENTITY_TYPE_ADD_GROUP:{
                    //群添加
                    GroupCard card = getGson().fromJson(entity.content, GroupCard.class);
                    getGroupCenter().dispatch(card);
                    break;
                }
                case PushModel.ENTITY_TYPE_MODIFY_GROUP_MEMBERS:
                case PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS:{
                    //群成员变更，回来的是一个群成员的列表
                    Type type = new TypeToken<List<GroupMemberCard>>(){}.getType();
                    List<GroupMemberCard> cards = getGson().fromJson(entity.content,type);
                    //把数据集合丢到数据中心处理
                    getGroupCenter().dispatch(cards.toArray(new GroupMemberCard[0]));
                    break;
                }
                case PushModel.ENTITY_TYPE_EXIT_GROUP_MEMBERS:{
                    //TODO 成员退出的推送
                }
            }
        }
    }

    /**
     * 获取一个用户中心的实现类
     * @return 用户中心的规范接口
     */
    public static UserCenter getUserCenter(){
        return UserDispatcher.instance();
    }

    /**
     * 获取一个消息中心的实现类
     * @return
     */
    public static MessageCenter getMessageCenter(){
        return MessageDispatcher.instance();
    }

    /**
     * 获取一个群处理中心的实现类
     * @return 群中心的规范接口
     */
    public static GroupCenter getGroupCenter(){
        return GroupDispatcher.instance();
    }
}
