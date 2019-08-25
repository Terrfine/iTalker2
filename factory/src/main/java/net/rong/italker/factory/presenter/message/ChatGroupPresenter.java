package net.rong.italker.factory.presenter.message;

import net.rong.italker.factory.data.helper.GroupHelper;
import net.rong.italker.factory.data.helper.UserHelper;
import net.rong.italker.factory.data.message.MessageDataSource;
import net.rong.italker.factory.data.message.MessageGroupRepository;
import net.rong.italker.factory.data.message.MessageRepository;
import net.rong.italker.factory.model.db.Group;
import net.rong.italker.factory.model.db.Message;
import net.rong.italker.factory.model.db.User;
import net.rong.italker.factory.model.db.view.MemberUserModel;
import net.rong.italker.factory.persistence.Account;

import java.util.List;

public class ChatGroupPresenter extends ChatPresenter<ChatContract.GroupView>
        implements ChatContract.Presenter {
    public ChatGroupPresenter(ChatContract.GroupView view, String receiverId) {

        //数据源，View，接收者，接收者类型
        super(new MessageGroupRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_GROUP);
    }

    @Override
    public void start() {
        super.start();

        //从本地拿群的信息
        Group group = GroupHelper.findFromLocal(mReceiverId);
        if (group != null) {
            //初始化操作
            ChatContract.GroupView view = getView();
            boolean isAdmin = Account.getUserId().equalsIgnoreCase(group.getOwner().getId());
            view.showAdminOption(isAdmin);

            //基础信息初始化
            view.onInit(group);

            List<MemberUserModel> models = group.getLatelyGroupMembers();
            final long memberCount = group.getGroupMemberCount();
            //没有显示的成员数量
            long moreCount = memberCount - models.size();

            view.onInitGroupMembers(models, moreCount);
        }
    }
}
