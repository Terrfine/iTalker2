package net.rong.italker.factory.data.message;

import android.text.TextUtils;

import net.rong.italker.factory.data.helper.DbHelper;
import net.rong.italker.factory.data.helper.GroupHelper;
import net.rong.italker.factory.data.helper.MessageHelper;
import net.rong.italker.factory.data.helper.UserHelper;
import net.rong.italker.factory.data.user.UserDispatcher;
import net.rong.italker.factory.model.card.MessageCard;
import net.rong.italker.factory.model.db.Group;
import net.rong.italker.factory.model.db.Message;
import net.rong.italker.factory.model.db.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MessageDispatcher implements MessageCenter {
    private static MessageCenter instance;
    //单线程池；处理卡片一个个的消息进行处理
    private final Executor executor = Executors.newSingleThreadExecutor();

    public static MessageCenter instance(){
        if(instance == null){
            //避免重复创建
            synchronized (MessageDispatcher.class){
                if(instance == null)
                    instance = new MessageDispatcher();
            }
        }
        return instance;
    }

    @Override
    public void dispatch(MessageCard... cards) {
        if(cards == null || cards.length == 0)
            return;
        //丢到单线程池中
        executor.execute(new MessageCardHandler(cards));
    }

    /**
     * 消息的卡片的线程调度的处理会触发的run方法
     */
    private class MessageCardHandler implements Runnable{

        private final MessageCard[] cards;

        MessageCardHandler(MessageCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<Message> messages = new ArrayList<>();
            //卡片基础信息过滤，错误卡片直接过滤
            for(MessageCard card : cards){
                if(card == null || TextUtils.isEmpty(card.getSenderId())
                        || TextUtils.isEmpty(card.getId())
                        || (TextUtils.isEmpty(card.getReceiverId())
                        && TextUtils.isEmpty(card.getGroupId())))
                    continue;
                //消息卡片有可能是推送过来的，也有可能是直接造的
                //推送过来的代表服务器一定有，我们可以查询到（本地有可能有，有可能没有）
                //如果是自己造的，那么先存储本地，然后发送网络
                //发送消息流程：写消息->存储本地->发送网络->网络返回->刷新本地状态
                Message message = MessageHelper.findFromLocal(card.getId());
                if(message != null){
                    //消息本身字段从发送后就不变化了，如果收到了消息
                    // 本地有，同时本地显示消息状态为完成状态，则不处理
                    //因为此时回来的消息和本地一定一模一样
                    //如果本地消息显示已经完成则不处理
                    if(message.getStatus() == Message.STATUS_DONE)
                        continue;
                    //新状态为完成才更新服务器时间，不然不做更新
                    if(card.getStatus() == Message.STATUS_DONE){
                        //代表网络发送成功，此时需要修改时间为服务器时间
                        message.setCreateAt(card.getCreateAt());
                        //如果没有进入判断，则代表这个消息发送失败了
                        //重新进行数据库更新而已
                    }
                    //更新一些会变化的内容
                    message.setContent(card.getContent());
                    message.setAttach(card.getAttach());
                    //更新状态
                    message.setStatus(card.getStatus());
                }
                else {
                    User sender = UserHelper.search(card.getSenderId());
                    User receiver = null;
                    Group group = null;
                    if(!TextUtils.isEmpty(card.getReceiverId())){
                        receiver = UserHelper.search(card.getReceiverId());
                    }else if(!TextUtils.isEmpty(card.getGroupId())){
                        group = GroupHelper.findFromLocal(card.getGroupId());
                    }
                    //接收者总有一个
                    //TODO
                    if((receiver == null && group == null) || sender == null)
                        continue;
                    message = card.build(sender, receiver, group);
                }
                messages.add(message);
            }
            if(messages.size() > 0)
                DbHelper.save(Message.class, messages.toArray(new Message[0]));
        }
    }
}
