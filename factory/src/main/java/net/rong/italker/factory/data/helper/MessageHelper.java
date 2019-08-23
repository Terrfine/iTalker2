package net.rong.italker.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.rong.italker.factory.Factory;
import net.rong.italker.factory.model.api.RspModel;
import net.rong.italker.factory.model.api.message.MessageCreateModel;
import net.rong.italker.factory.model.card.MessageCard;
import net.rong.italker.factory.model.db.Message;
import net.rong.italker.factory.model.db.Message_Table;
import net.rong.italker.factory.net.NetWork;
import net.rong.italker.factory.net.RemoteService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 消息工具类
 */
public class MessageHelper {
    //从本地找信息
    public static Message findFromLocal(String id) {
        //TODO
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.id.eq(id))
                .querySingle();
    }

    //发送是异步进行的
    public static void push(final MessageCreateModel model) {
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                //成功状态或正在发送状态：如果是一个已经发送过的消息，则不能重新发送
                Message message = findFromLocal(model.getId());
                if(message != null && message.getStatus() != Message.STATUS_FAILED)
                    return;

                //TODO 如果是文件类型的（语音，图片，文件），需要先上传后才发送

                //我们在发送的时候需要通知界面更新状态，Card
                final MessageCard card = model.buildCard();
                Factory.getMessageCenter().dispatch(card);

                //直接发送,进行网络调度
                RemoteService service = NetWork.remote();
                service.msgPush(model).enqueue(new Callback<RspModel<MessageCard>>() {
                    @Override
                    public void onResponse(Call<RspModel<MessageCard>> call, Response<RspModel<MessageCard>> response) {
                        RspModel<MessageCard> rspModel = response.body();
                        if(rspModel!= null &&rspModel.success()){
                            MessageCard rspCard = rspModel.getResult();
                            if (rspCard != null){
                                //成功的调度
                                Factory.getMessageCenter().dispatch(rspCard);
                            }
                        }else {
                            //解析是否是账户异常
                            Factory.decodeRspCode(rspModel, null);
                            //走失败流程
                            onFailure(call, null);
                        }
                    }

                    @Override
                    public void onFailure(Call<RspModel<MessageCard>> call, Throwable t) {
                        //通知失败
                        card.setStatus(Message.STATUS_FAILED);
                        Factory.getMessageCenter().dispatch(card);
                    }
                });
            }
        });
    }

    /**
     * 查询一个消息，这个消息时一个群中的最后一条消息
     * @param groupId
     * @return
     */
    public static Message findLastWithGroup(String groupId) {
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.group_id.eq(groupId))
                .orderBy(Message_Table.createAt,false)
                .querySingle();
    }

    /**
     * 查询一个消息，这个消息是和一个人的最后一条聊天消息
     * @param userId
     * @return
     */
    public static Message findLastWithUser(String userId) {
        return SQLite.select()
                .from(Message.class)
                .where(OperatorGroup.clause()
                        .and(Message_Table.sender_id.eq(userId))
                        .and(Message_Table.group_id.isNull()))
                .or(Message_Table.receiver_id.eq(userId))
                .orderBy(Message_Table.createAt, false)
                .querySingle();
    }
}
