package net.rong.italker.factory.data.user;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.rong.italker.factory.data.BaseDbRepository;
import net.rong.italker.factory.data.DataSource;
import net.rong.italker.factory.data.helper.DbHelper;
import net.rong.italker.factory.model.db.User;
import net.rong.italker.factory.model.db.User_Table;
import net.rong.italker.factory.persistence.Account;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 联系人仓库
 */
public class ContactRepository extends BaseDbRepository<User> implements ContactDataSource{

//    private final Set<User> users = new HashSet<>();

    @Override
    public void load(DataSource.SucceedCallback<List<User>> callback) {
        super.load(callback);
        //加载本地数据
        SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    /**
     * 检查一个User是否是我需要关注的数据
     * @param user
     * @return
     */
    protected boolean isRequired(User user){
        return user.isFollow()&&!user.getId().equals(Account.getUserId());
    }
}
