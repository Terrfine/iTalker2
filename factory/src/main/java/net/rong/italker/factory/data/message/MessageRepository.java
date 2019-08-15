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
 * 关注的内容一定是我发给这个人的，或者是他发送给我的
 */
public class MessageRepository extends BaseDbRepository<Message> implements MessageDataSource {

    //聊天的对象Id
    private String receiverId;

    public MessageRepository(String receiverId){
        //TODO 一定要super吗？
        super();
        this.receiverId = receiverId;
    }

    @Override
    public void load(SucceedCallback<List<Message>> callback) {
        super.load(callback);
        SQLite.select()
                .from(Message.class)
                .where(OperatorGroup.clause()
                        .and(Message_Table.sender_id.eq(receiverId))
                        .and(Message_Table.group_id.isNull()))
                .or(Message_Table.receiver_id.eq(receiverId))
                .orderBy(Message_Table.createAt, false)
                .limit(30)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Message message) {
        //receiverId 如果是发送者，那么Group == null的情况下一定是发送给我的消息
        //如果消息的接收者不为空，那么一定是发送给某个人的，这个人只能是我或者是某个人
        //如果这个人就是receiverId，那么就是我需要关注的消息
        return (receiverId.equalsIgnoreCase(message.getSender().getId())
                && message.getGroup() == null)
                || (message.getReceiver() != null && receiverId.equalsIgnoreCase(message.getReceiver().getId()));
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
        //反转返回的集合
        //TODO 不能查询的时候反转？
        Collections.reverse(tResult);
        //然后在调度
        super.onListQueryResult(transaction, tResult);
    }
}
