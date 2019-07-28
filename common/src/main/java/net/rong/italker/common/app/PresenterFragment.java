package net.rong.italker.common.app;

import android.content.Context;

import net.rong.italker.factory.presenter.BaseContract;

public abstract class PresenterFragment<Presenter extends BaseContract.Presenter> extends Fragment
        implements BaseContract.View<Presenter> {

    protected Presenter mPresenter;

    /**
     * 初始化Presenter
     * @return Presenter
     */
    protected abstract Presenter initPresenter();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //在界面OnAttach之后触发初始化Presenter
        initPresenter();
    }

    @Override
    public void showError(int str) {
        //显示错误
        Application.showToast(str);
    }

    @Override
    public void showLoading() {
        //TODO 显示一个loading
    }

    @Override
    public void setPresenter(Presenter presenter) {
        //View中赋值Presenter
        mPresenter = presenter;
    }
}
