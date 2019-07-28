package net.rong.italker.push.frags.user;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import net.qiujuer.genius.ui.widget.Loading;
import net.rong.italker.common.app.Application;
import net.rong.italker.common.app.PresenterFragment;
import net.rong.italker.common.widget.PortraitView;
import net.rong.italker.factory.presenter.user.UpdateInfoPresenter;
import net.rong.italker.factory.presenter.user.UpdateInfoContract;
import net.rong.italker.push.R;
import net.rong.italker.push.activities.MainActivity;
import net.rong.italker.push.frags.media.GalleyFragment;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * 用户更新信息的碎片
 */
public class UpdateInfoFragment extends PresenterFragment<UpdateInfoContract.Presenter>
 implements UpdateInfoContract.View{
    @BindView(R.id.im_sex)
    ImageView mSex;

    @BindView(R.id.edit_desc)
    EditText mDesc;

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    @BindView(R.id.loading)
    Loading mLoading;

    @BindView(R.id.btn_submit)
    Button mSubmit;

    //头像的本地路径
    private String mPortraitPath;
    private boolean isMan = true;
    public UpdateInfoFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_update_info;
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick(){
        new GalleyFragment()
                .setListener(new GalleyFragment.OnSelectedListener() {
                    @Override
                    public void onSelectedImage(String path) {
                        UCrop.Options options = new UCrop.Options();
                        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                        options.setCompressionQuality(96);

                        //得到头像的缓存地址
                        File dPath = Application.getPortraitTmpFile();

                        //发起剪切
                        UCrop.of(Uri.fromFile(new File(path)), Uri.fromFile(dPath))
                                .withAspectRatio(1, 1)  //1:1比例
                                .withMaxResultSize(520, 520) //返回最大的尺寸
                                .withOptions(options) //相关参数
                                .start(getActivity());
                    }
                    //show的时候建议使用getChildFragmentManager
                    //tag  GalleyFragment class名
                }).show(getChildFragmentManager(), GalleyFragment.class.getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //收到从Activity传递过来的回调，然后取出其中的值进行图片加载
        //如果是我能够处理的类型
        if(resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP){
            final Uri resultUri = UCrop.getOutput(data);
            if(resultUri != null){
                loadPortrait(resultUri);
            }
            else if(resultCode == UCrop.RESULT_ERROR){
                Application.showToast(R.string.data_rsp_error_unknown);
                final  Throwable cropError = UCrop.getError(data);
            }
        }
    }

    /**
     * 加载Uri到当前的头像中
     * @param uri
     */
    private void loadPortrait(Uri uri){
        //得到头像地址
        mPortraitPath = uri.getPath();
        Glide.with(this)
                .load(uri)
                .asBitmap()
                .centerCrop()
                .into(mPortrait);

//        //拿到本地文件的地址
//        String localPath = uri.getPath();
//        Log.e("TAG","localPath" + localPath);
//        Factory.runOnAsync(new Runnable() {
//            @Override
//            public void run() {
//                String url =  UploadHelper.uploadPortrait(localPath);
//                Log.e("TAG", "url:" + localPath);
//            }
//        });
    }

    @OnClick(R.id.im_sex)
    void  onSexClick(){
        //性别图片点击时候触发
        isMan = !isMan; //反向性别
        Drawable drawable = getResources().getDrawable(isMan?
                R.drawable.ic_sex_man : R.drawable.ic_sex_woman);
        mSex.setImageDrawable(drawable);
        //设置背景的层级，切换颜色
        mSex.getBackground().setLevel(isMan?0:1);
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick(){
        String desc = mDesc.getText().toString();
        mPresenter.update(mPortraitPath, desc, isMan);
    }

    @Override
    protected UpdateInfoContract.Presenter initPresenter() {
        return new UpdateInfoPresenter(this);
    }

    @Override
    public void updateSucceed() {
        //更新成功跳转到主界面
        MainActivity.show(getContext());
        getActivity().finish();
    }
    @Override
    public void showError(int str) {
        super.showError(str);
        //当需要显示错误的时候触发，一定是结束了
        //停止loading
        mLoading.stop();
        mDesc.setEnabled(true);
        mPortrait.setEnabled(true);
        mSex.setEnabled(true);
        mSubmit.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();

        //正在进行时，正在进行注册，界面不可操作
        //开始loadding
        mLoading.start();
        mDesc.setEnabled(false);
        mPortrait.setEnabled(false);
        mSex.setEnabled(false);
        mSubmit.setEnabled(false);
    }

}
