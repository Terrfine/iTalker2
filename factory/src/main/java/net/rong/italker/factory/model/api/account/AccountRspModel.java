package net.rong.italker.factory.model.api.account;

import net.rong.italker.factory.model.db.User;

public class AccountRspModel {
    private User user;
    //当前登陆的账号
    private String account;
    //当前登录成功后获取的token，可以通过Token获取用户的所有信息
    private String token;
    //标识是否已经绑定到了设备PushId
    private boolean isBind;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isBind() {
        return isBind;
    }

    public void setBind(boolean bind) {
        isBind = bind;
    }

    @Override
    public String toString() {
        return "AccountRspModel{" +
                "user=" + user +
                ", account='" + account + '\'' +
                ", token='" + token + '\'' +
                ", isBind=" + isBind +
                '}';
    }
}
