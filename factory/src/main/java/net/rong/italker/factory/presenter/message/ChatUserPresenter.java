package net.rong.italker.factory.presenter.message;

import net.rong.italker.factory.data.helper.UserHelper;
import net.rong.italker.factory.data.message.MessageRepository;
import net.rong.italker.factory.model.db.Message;
import net.rong.italker.factory.model.db.User;

public class ChatUserPresenter extends ChatPresenter<ChatContract.UserView>
implements ChatContract.Presenter{


    public ChatUserPresenter(ChatContract.UserView view, String receiverId) {

        //数据源，View，接收者，接收者类型
        super(new MessageRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_USER);
    }

    @Override
    public void start() {
        super.start();

        //从本地拿这个人的信息
        User receiver = UserHelper.findFromLocal(mReceiverId);
        getView().onInit(receiver);
    }
}
