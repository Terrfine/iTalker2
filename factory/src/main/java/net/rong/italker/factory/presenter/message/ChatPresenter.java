package net.rong.italker.factory.presenter.message;

import android.support.v7.util.DiffUtil;

import net.rong.italker.factory.data.helper.MessageHelper;
import net.rong.italker.factory.data.message.MessageDataSource;
import net.rong.italker.factory.model.api.message.MessageCreateModel;
import net.rong.italker.factory.model.db.Message;
import net.rong.italker.factory.presenter.BaseSourcePresenter;
import net.rong.italker.factory.utils.DiffUiDataCallback;

import java.util.List;

/**
 * 聊天Presenter的基础类
 */
public class ChatPresenter<View extends ChatContract.View>
        extends BaseSourcePresenter<Message, Message, MessageDataSource, View>
implements ChatContract.Presenter{

    //接收者Id，可能是群，或者人的ID
    protected String mReceiverId;
    //区分是人还是 群ID
    protected int mReceiverType;

    public ChatPresenter(MessageDataSource source, View view, String receiverId, int receiverType) {
        super(source, view);
        this.mReceiverId = receiverId;
        this.mReceiverType = receiverType;
    }

    @Override
    public void onDataLoaded(List<Message> messages) {
        ChatContract.View view = getView();
        if(view == null)
            return;
        @SuppressWarnings("unchecked")
        List<Message> old = view.getRecyclerAdapter().getItems();

        //差异计算
        DiffUiDataCallback<Message> callback = new DiffUiDataCallback<>(old, messages);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        //进行界面刷新
        refreshData(result, messages);
    }

    @Override
    public void pushText(String content) {
        MessageCreateModel model = new MessageCreateModel.Builder()
                .receiver(mReceiverId, mReceiverType)
                .content(content,Message.TYPE_STR)
                .build();
        //进行网络发送
        MessageHelper.push(model);
    }

    @Override
    public void pushAudio(String path) {
        //TODO 发送语音
    }

    @Override
    public void pushImages(String[] paths) {

    }

    @Override
    public boolean rePush(Message message) {
        return false;
    }
}
