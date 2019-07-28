package net.rong.italker.factory.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.rong.italker.factory.Factory;
import net.rong.italker.factory.model.api.account.AccountRspModel;
import net.rong.italker.factory.model.db.User;
import net.rong.italker.factory.model.db.User_Table;

public class Account {
    private static final String KEY_PUSH_ID = "KEY_PUSH_ID";
    private static final String KEY_IS_BIND = "KEY_IS_BAND";
    private static final String KEY_TOKEN = "KEY_TOKEN";
    private static final String KEY_USERID = "KEY_USERID";
    private static final String KEY_ACCOUNT = "KEY_ACCOUNT";
    //设备的推送Id
    private static String pushId ;
    //设备Id是否已经绑定到了服务器
    private static boolean isBind;
    //登录状态的Token，用来接口请求
    private static String token;
    //登录的用户ID
    private static String userId;

    //登录的账户
    private static String account;

    private static void save(Context context){
        SharedPreferences sp = context.getSharedPreferences(Account.class.getName(), Context.MODE_PRIVATE);
        //储存数据
        sp.edit()
                .putString(KEY_PUSH_ID, pushId)
                .putBoolean(KEY_IS_BIND,isBind())
                .putString(KEY_TOKEN,token)
                .putString(KEY_USERID, userId)
                .putString(KEY_ACCOUNT, account)
                .apply();
    }


    /**
     * 进行数据加载
     * @param context
     */
    public static void load(Context context){
        SharedPreferences sp = context.getSharedPreferences(Account.class.getName(), Context.MODE_PRIVATE);
        pushId = sp.getString(KEY_PUSH_ID, "");
        isBind = sp.getBoolean(KEY_IS_BIND, false);
        token = sp.getString(KEY_TOKEN,"");
        userId = sp.getString(KEY_USERID, "");
        account = sp.getString(KEY_ACCOUNT,"");
    }

    public static void setPushId(String pushId){
        Account.pushId = pushId;
        Account.save(Factory.app());
    }

    /**
     * 获取推送id
     * @return
     */
    public static String getPushId(){
        return pushId;
    }

    /**
     * 返回当前账户是否登录
     * @return
     */
    public static boolean isLogin(){
        //用户Id和Token不为空
        return !TextUtils.isEmpty(userId)
                && !TextUtils.isEmpty(token);
    }

    /**
     * 是否已经完善了用户信息
     * @return
     */
    public static boolean isComplete(){
        if(isLogin()){
            User self = getUser();
            return !TextUtils.isEmpty(self.getDesc())
                    &&!TextUtils.isEmpty(self.getPortrait())
                    &&self.getSex()!=0;
        }
        //未登录返回信息不完全
        return false;
    }

    public static boolean isBind(){
        return isBind;
    }

    /**
     * 设置绑定状态
     * @param isBind
     */
    public static void setBind(boolean isBind){
        Account.isBind = isBind;
        Account.save(Factory.app());
    }

    /**
     * 保存我自己的信息到持久化xml中
     * @param model
     */
    public static void login(AccountRspModel model){
        //存储当前登录的账户，token，用户Id，方便从数据库中查询我的信息
        Account.token = model.getToken();
        Account.account = model.getAccount();
        Account.userId = model.getUser().getId();
        save(Factory.app());
    }

    /**
     * 获取当前登录的用户信息
     * @return
     */
    public static User getUser(){
        return TextUtils.isEmpty(userId)? new User() : SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(userId))
                .querySingle();
    }

    /**
     * 获取Token
     * @return
     */
    public static String getToken(){
        return token;
    }
}
