package net.rong.italker.factory.presenter.user;

import net.rong.italker.factory.presenter.BaseContract;

/**
 * 更新用户基本信息的基本契约
 */
public interface UpdateInfoContract {
    interface Presenter extends BaseContract.Presenter{
        //更新信息
        void update(String photoFilePath, String desc, boolean isMan);
    }

    interface View extends BaseContract.View<Presenter>{
        //回调成功
        void updateSucceed();
    }
}
