package net.rong.italker.factory.data.message;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.rong.italker.factory.data.BaseDbRepository;
import net.rong.italker.factory.model.db.Message;
import net.rong.italker.factory.model.db.Message_Table;

import java.util.Collections;
import java.util.List;

/**
 * 跟某人聊天的时候的聊天列表
 * 关注的内容一定是我发给这个群的，或者是别人发送给群的
 */
public class MessageGroupRepository extends BaseDbRepository<Message> implements MessageDataSource {

    //聊天的群Id
    private String receiverId;

    public MessageGroupRepository(String receiverId) {
        //TODO 一定要super吗？
        super();
        this.receiverId = receiverId;
    }

    @Override
    public void load(SucceedCallback<List<Message>> callback) {
        super.load(callback);

        //TODO

        SQLite.select()
                .from(Message.class)
                .where(Message_Table.group_id.eq(receiverId))
                .orderBy(Message_Table.createAt, false)
                .limit(30)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Message message) {
        //如果消息的group不为空，则一定是发送到一个群的
        //如果群Id等于我们需要的，那就是通过的
        return message.getGroup() != null && receiverId.equalsIgnoreCase(message.getGroup().getId());

    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
        //反转返回的集合
        //TODO 不能查询的时候反转？ 不能
        Collections.reverse(tResult);
        //然后在调度
        super.onListQueryResult(transaction, tResult);
    }
}
