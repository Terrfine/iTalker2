package net.rong.italker.common.app;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import net.rong.italker.common.R;
import net.rong.italker.factory.presenter.BaseContract;

public abstract class PersonalToolbarActivity<Presenter extends BaseContract.Presenter> extends ToolbarActivity
implements BaseContract.View<Presenter>{
    protected Presenter mPresenter;

    @Override
    protected void initBefore() {
        super.initBefore();
        //初始化Presenter
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //进行销毁
        if(mPresenter != null)
            mPresenter.destroy();
    }

    /**
     * 初始化Presenter
     * @return Presenter
     */
    protected abstract Presenter initPresenter();

    @Override
    public void showError(int str) {
        if(mPlaceHolderView != null){
            mPlaceHolderView.triggerError(str);
        }else {
            //显示错误
            Application.showToast(str);
        }
    }

    @Override
    public void showLoading() {
        if(mPlaceHolderView != null){
            mPlaceHolderView.triggerLoading();
        }
    }

    protected void hideLoading(){
        if(mPlaceHolderView != null){
            mPlaceHolderView.triggerOk();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        //View中赋值Presenter
        mPresenter = presenter;
    }
}
