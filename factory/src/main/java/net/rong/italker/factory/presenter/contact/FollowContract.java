package net.rong.italker.factory.presenter.contact;

import net.rong.italker.factory.model.card.UserCard;
import net.rong.italker.factory.presenter.BaseContract;

/**
 * 关注的接口定义
 */
public interface FollowContract {
    //任务调度者
    interface Presenter extends BaseContract.Presenter{
        void follow(String id);
    }

    interface View extends BaseContract.View<Presenter>{
        //成功情况下返回一个用户的信息
        void onFollowSucceed(UserCard userCard);
    }
}
