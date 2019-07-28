package net.rong.italker.factory.presenter.user;

import android.text.TextUtils;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.rong.italker.factory.Factory;
import net.rong.italker.factory.R;
import net.rong.italker.factory.data.DataSource;
import net.rong.italker.factory.data.helper.UserHelper;
import net.rong.italker.factory.model.api.user.UserUpdateModel;
import net.rong.italker.factory.model.card.UserCard;
import net.rong.italker.factory.model.db.User;
import net.rong.italker.factory.net.UploadHelper;
import net.rong.italker.factory.presenter.BasePresenter;
import net.rong.italker.factory.presenter.account.LoginContract;

public class UpdateInfoPresenter extends BasePresenter<UpdateInfoContract.View>
 implements UpdateInfoContract.Presenter , DataSource.Callback<UserCard> {

    public UpdateInfoPresenter(UpdateInfoContract.View view) {
        super(view);
    }

    @Override
    public void update(final String photoFilePath, final String desc, final boolean isMan) {
        start();
        final UpdateInfoContract.View view = getView();

        if(TextUtils.isEmpty(photoFilePath) || TextUtils.isEmpty(desc)){
            view.showError(R.string.data_account_update_invalid_parameter);
        }else {
            //上传头像
            Factory.runOnAsync(new Runnable() {
                @Override
                public void run() {
                    String url = UploadHelper.uploadPortrait(photoFilePath);
                    if(TextUtils.isEmpty(url)){
                        //上传失败
                        view.showError(R.string.data_upload_error);
                    }else {
                        //构建Model
                        UserUpdateModel model = new UserUpdateModel("", url,desc,
                                isMan? User.SEX_MAN : User.SEX_WOMAN);
                        UserHelper.update(model, UpdateInfoPresenter.this);
                    }
                }
            });
        }
    }

    @Override
    public void onDataLoaded(UserCard userCard) {
        final UpdateInfoContract.View view = getView();
        if(view == null){
            return;
        }
        //此时是从网络 回送回来的，并不保证处于主现场状态
        //强制执行在主线程中
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                //调用主界面登录成功
                view.updateSucceed();
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        //网络请求告知注册失败
        final UpdateInfoContract.View view = getView();
        if(view == null){
            return;
        }
        //此时是从网络 回送回来的，并不保证处于主现场状态
        //强制执行在主线程中
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                //调用主界面注册成功
                view.showError(strRes);
            }
        });
    }
}
