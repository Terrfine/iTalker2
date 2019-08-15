package net.rong.italker.factory.model.api.message;

import android.os.Build;

import net.rong.italker.factory.model.card.MessageCard;
import net.rong.italker.factory.model.db.Message;
import net.rong.italker.factory.persistence.Account;

import java.util.Date;
import java.util.UUID;

public class MessageCreateModel {

    private MessageCreateModel() {
        //随机生成一个UUID
        this.id = UUID.randomUUID().toString();
    }

    //ID从客户端生成，一个UUID
    private String id;

    private String content;

    private String attach;

    //消息类型
    private int type = Message.TYPE_STR;

    //接收者 可为空
    private String receiverId;

    //接收者类型，群，人
    private int receiverType = Message.RECEIVER_TYPE_USER;

    public String getId() {
        return id;
    }


    public String getContent() {
        return content;
    }


    public String getAttach() {
        return attach;
    }

    public int getType() {
        return type;
    }


    public String getReceiverId() {
        return receiverId;
    }

    public int getReceiverType() {
        return receiverType;
    }

    /**
     * 建造者模式，快速建立一个发送Model
     */
    public static class Builder{
        private MessageCreateModel model;

        public Builder(){
            this.model = new MessageCreateModel();
        }

        //设置接收者
        public Builder receiver(String receiverId, int receiverType){
            model.receiverId = receiverId;
            model.receiverType = receiverType;
            return this;
        }

        //设置内容
        public Builder content(String content, int type){
            this.model.content = content;
            this.model.type = type;
            return this;
        }

        public Builder attach(String attach){
            this.model.attach = attach;
            return this;
        }

        public MessageCreateModel build(){
            return this.model;
        }
    }

    //当我们需要发送一个文件的时候，content刷新的问题
    private MessageCard card;

    //返回一个Card
    public MessageCard buildCard(){
        if(card == null){
            MessageCard card = new MessageCard();
            card.setId(id);
            card.setContent(content);
            card.setAttach(attach);
            card.setType(type);
            card.setSenderId(Account.getUserId());

            //如果是群
            if(receiverType == Message.RECEIVER_TYPE_GROUP){
                card.setGroupId(receiverId);
            }
            else {
                card.setReceiverId(receiverId);
            }
            //通过当前Model建立的Card就是一个初步状态的Card
            card.setStatus(Message.STATUS_CREATED);
            card.setCreateAt(new Date());
            this.card = card;
        }
        return this.card;
    }
}
