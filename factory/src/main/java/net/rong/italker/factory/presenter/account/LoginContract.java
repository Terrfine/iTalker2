package net.rong.italker.factory.presenter.account;

import android.support.annotation.StringRes;

import net.rong.italker.factory.presenter.BaseContract;

public interface LoginContract {
    interface View extends BaseContract.View<Presenter>{
        //注册成功
        void loginSuccess();
    }

    interface Presenter extends BaseContract.Presenter {
        //发起一个登录
        void login(String phone, String password);
    }
}
