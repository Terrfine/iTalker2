package net.rong.italker.factory.presenter.group;

import android.support.v7.util.DiffUtil;

import net.rong.italker.factory.data.group.GroupsDataSource;
import net.rong.italker.factory.data.group.GroupsRepository;
import net.rong.italker.factory.data.helper.GroupHelper;
import net.rong.italker.factory.data.helper.UserHelper;
import net.rong.italker.factory.model.db.Group;
import net.rong.italker.factory.presenter.BaseSourcePresenter;
import net.rong.italker.factory.utils.DiffUiDataCallback;

import java.util.List;

public class GroupsPresenter extends BaseSourcePresenter<Group, Group, GroupsDataSource, GroupsContract.View>
        implements GroupsContract.Presenter {

    public GroupsPresenter(GroupsContract.View view) {
        super(new GroupsRepository(), view);

        //加载网络数据,以后可以优化到下拉刷新中
        //只有用户下拉进行网络请求刷新
        GroupHelper.refreshGroups();
    }

    @Override
    public void onDataLoaded(List<Group> groups) {
        final GroupsContract.View view = getView();
        if (view == null)
            return;
        List<Group> old = view.getRecyclerAdapter().getItems();
        DiffUiDataCallback<Group> callback = new DiffUiDataCallback<>(old, groups);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        //刷新
        refreshData(result, groups);
    }
}
