package net.rong.italker.factory.presenter.contact;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.rong.italker.common.widget.recycler.RecyclerAdapter;
import net.rong.italker.factory.data.DataSource;
import net.rong.italker.factory.data.helper.UserHelper;
import net.rong.italker.factory.data.user.ContactDataSource;
import net.rong.italker.factory.data.user.ContactRepository;
import net.rong.italker.factory.model.card.UserCard;
import net.rong.italker.factory.model.db.AppDatabase;
import net.rong.italker.factory.model.db.User;
import net.rong.italker.factory.model.db.User_Table;
import net.rong.italker.factory.persistence.Account;
import net.rong.italker.factory.presenter.BasePresenter;
import net.rong.italker.factory.presenter.BaseRecyclerPresenter;
import net.rong.italker.factory.presenter.BaseSourcePresenter;
import net.rong.italker.factory.utils.DiffUiDataCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * 联系人的presenter实现
 */
public class ContactPresenter extends BaseSourcePresenter<User, User,ContactDataSource,ContactContract.View>
implements ContactContract.Presenter{

    public ContactPresenter(ContactContract.View view) {
        //初始化数据仓库
        super(new ContactRepository(),view);
    }

    @Override
    public void start() {
        super.start();
        //加载网络数据
        UserHelper.refreshContacts();

//        new DataSource.Callback<List<UserCard>>() {
//            @Override
//            public void onDataNotAvailable(int strRes) {
//                // 网络失败，因为本地有数据，不管错误
//            }
//
//            @Override
//            public void onDataLoaded(final List<UserCard> userCards) {
//
//                final List<User> users = new ArrayList<>();
//                for(UserCard userCard : userCards){
//                    users.add(userCard.build());
//                }
//                //丢到事务中保存数据库
//                DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
//                definition.beginTransactionAsync(new ITransaction() {
//                    @Override
//                    public void execute(DatabaseWrapper databaseWrapper) {
//                        FlowManager.getModelAdapter(User.class).saveAll(users);
//                    }
//                }).build().execute();
//
//                //网络的数据往往是新的，我们需要直接刷新到界面
//
//                diff(getView().getRecyclerAdapter().getItems(), users);
//            }
//        }

        //TODO 问题，
        //1.关注后虽然存储数据库，但没有刷新联系人 √
        //2.如果刷新数据库，或者从网络刷新，最总刷新的时候是全局刷新 √
        //3.本地刷新和网络刷新，在添加到界面的时候可能会有冲突（查询数据库为异步操作），导致数据显示异常
        //4.如何识别已经在数据库中有这样的数据

    }

    @Override
    public void onDataLoaded(List<User> users) {
        //无论怎么操作，数据变更，最终都会通知到这里来

        final ContactContract.View view =getView();
        if(view == null)
            return;
        RecyclerAdapter<User> adapter = view.getRecyclerAdapter();
        List<User> old = adapter.getItems();

        //进行数据的对比
        DiffUtil.Callback callback = new DiffUiDataCallback<>(old, users);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        refreshData(result, users);
    }

//    private void diff(List<User> oldList, List<User> newList){
//        //进行数据的对比
//        DiffUtil.Callback callback = new DiffUiDataCallback<>(oldList, newList);
//        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
//
//        //在对比完成后进行数据的赋值
//        getView().getRecyclerAdapter().replace(newList);
//        //TODO 这样做不是更耗时？
//        //尝试刷新界面
//        result.dispatchUpdatesTo(getView().getRecyclerAdapter());
//        getView().onAdapterDataChanged();
//    }

}
