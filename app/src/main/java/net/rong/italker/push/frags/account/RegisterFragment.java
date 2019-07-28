package net.rong.italker.push.frags.account;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import net.qiujuer.genius.ui.widget.Loading;
import net.rong.italker.common.app.Fragment;
import net.rong.italker.common.app.PresenterFragment;
import net.rong.italker.factory.presenter.account.RegisterContract;
import net.rong.italker.factory.presenter.account.RegisterPresenter;
import net.rong.italker.push.R;
import net.rong.italker.push.activities.MainActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 注册的界面
 */
public class RegisterFragment extends PresenterFragment<RegisterContract.Presenter>
        implements RegisterContract.View {
    private AccountTrigger mAccountTrigger;

    @BindView(R.id.edit_phone)
    EditText mPhone;
    @BindView(R.id.edit_name)
    EditText mName;
    @BindView(R.id.edit_password)
    EditText mPassword;

    @BindView(R.id.loading)
    Loading mLoading;

    @BindView(R.id.btn_submit)
    Button mSubmit;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    protected RegisterContract.Presenter initPresenter() {
        return new RegisterPresenter(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //拿到我们Activity的引用
        mAccountTrigger = (AccountTrigger)context;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_register;
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick(){
        String phone = mPhone.getText().toString();
        String name = mName.getText().toString();
        String password = mPassword.getText().toString();
        //调用P层进行注册
        mPresenter.register(phone, name, password);
    }

    @OnClick(R.id.txt_go_login)
    void onShowLoginClick(){
        //让AccountActivity进行界面切换
        mAccountTrigger.triggerView();
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        //当需要显示错误的时候触发，一定是结束了
        //停止loading
        mLoading.stop();
        mPhone.setEnabled(true);
        mPassword.setEnabled(true);
        mName.setEnabled(true);
        mSubmit.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();

        //正在进行时，正在进行注册，界面不可操作
        //开始loadding
        mLoading.start();
        mPhone.setEnabled(false);
        mPassword.setEnabled(false);
        mName.setEnabled(false);
        mSubmit.setEnabled(false);
    }

    @Override
    public void registerSuccess() {
        //注册成功，这个时候账户已经登录
        //我们需要进行跳转到MainActivity界面
        MainActivity.show(getContext());
        getActivity().finish();
    }
}
