package net.rong.italker.factory.data.helper;

import android.os.SystemClock;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.rong.italker.common.Common;
import net.rong.italker.common.app.Application;
import net.rong.italker.utils.PicturesCompressor;
import net.rong.italker.utils.StreamUtil;
import net.rong.italker.factory.Factory;
import net.rong.italker.factory.model.api.RspModel;
import net.rong.italker.factory.model.api.message.MessageCreateModel;
import net.rong.italker.factory.model.card.MessageCard;
import net.rong.italker.factory.model.db.Message;
import net.rong.italker.factory.model.db.Message_Table;
import net.rong.italker.factory.net.NetWork;
import net.rong.italker.factory.net.RemoteService;
import net.rong.italker.factory.net.UploadHelper;

import java.io.File;
import java.util.concurrent.ExecutionException;

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
                if (message != null && message.getStatus() != Message.STATUS_FAILED)
                    return;

                //我们在发送的时候需要通知界面更新状态，Card
                final MessageCard card = model.buildCard();
                Factory.getMessageCenter().dispatch(card);

                //发送文件消息分两部分：上传到云服务器，消息Push到我们自己的服务器
                //TODO 如果是文件类型的（语音，图片，文件），需要先上传后才发送
                if (card.getType() != Message.TYPE_STR) {
                    //不是文字类型
                    if (!card.getContent().startsWith(UploadHelper.ENDPOINT)) {
                        //没有上传到云服务器的，还是本地手机文件
                        String content;

                        switch (card.getType()) {
                            case Message.TYPE_PIC:
                                content = uploadPicture(card.getContent());
                                break;
                            case Message.TYPE_AUDIO:
                                content = uploadAudio(card.getContent());
                                break;
                            default:
                                content = "";
                                break;
                        }

                        if (TextUtils.isEmpty(content)) {
                            //失败
                            card.setStatus(Message.STATUS_FAILED);
                            Factory.getMessageCenter().dispatch(card);
                            //TODO 疑惑：不用return？
                        }

                        //成功则把网络路径进行替换
                        card.setContent(content);
                        Factory.getMessageCenter().dispatch(card);
                        //因为卡片的内容改变了，而我们上传到服务器是使用model
                        //所以model也需要跟着更改
                        model.refreshByCard();
                    }
                }

                //直接发送,进行网络调度
                RemoteService service = NetWork.remote();
                service.msgPush(model).enqueue(new Callback<RspModel<MessageCard>>() {
                    @Override
                    public void onResponse(Call<RspModel<MessageCard>> call, Response<RspModel<MessageCard>> response) {
                        RspModel<MessageCard> rspModel = response.body();
                        if (rspModel != null && rspModel.success()) {
                            MessageCard rspCard = rspModel.getResult();
                            if (rspCard != null) {
                                //成功的调度
                                Factory.getMessageCenter().dispatch(rspCard);
                            }
                        } else {
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


    private static String uploadAudio(String content) {
        //上传语音
        File file = new File(content);
        if (!file.exists() || file.length() <= 0)
            return null;
        //上传并返回
        return UploadHelper.uploadAudio(content);
    }

    //上传图片
    private static String uploadPicture(String path) {
        File file = null;
        try {
            //通过Glide的缓存区间解决了图片外部权限的问题
            file = Glide.with(Factory.app())
                    .load(path)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (file != null) {
            //进行压缩
            String cacheDir = Application.getCacheDirFile().getAbsolutePath();
            String tempFile = String.format("%s/image/Cache_%s.png", cacheDir, SystemClock.uptimeMillis());
            try {
                //压缩工具类
                if (PicturesCompressor.compressImage(file.getAbsolutePath(), tempFile, Common.Constance.MAX_UPLOAD_IMAGE_LENGTH)) {
                    String ossPath = UploadHelper.uploadImage(tempFile);
                    //清理缓存
                    StreamUtil.delete(tempFile);
                    return ossPath;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 查询一个消息，这个消息时一个群中的最后一条消息
     *
     * @param groupId
     * @return
     */
    public static Message findLastWithGroup(String groupId) {
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.group_id.eq(groupId))
                .orderBy(Message_Table.createAt, false)
                .querySingle();
    }

    /**
     * 查询一个消息，这个消息是和一个人的最后一条聊天消息
     *
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
