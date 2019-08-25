package net.rong.italker.factory.presenter.group;

import net.rong.italker.factory.model.db.view.MemberUserModel;
import net.rong.italker.factory.presenter.BaseContract;

/**
 * 群成员的契约
 */
public interface GroupMembersContract {
    interface Presenter extends BaseContract.Presenter{
        //具有一个刷新的方法
        void refresh();
    }

    //界面
    interface View extends BaseContract.RecyclerView<Presenter, MemberUserModel>{
        //获取群的id
        String getGroupId();
    }
}
