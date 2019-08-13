package net.rong.italker.common.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.rong.italker.common.widget.convention.PlaceHolderView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class Fragment extends android.support.v4.app.Fragment {
    protected View mRoot;
    protected Unbinder mRootUnbinder;
    protected PlaceHolderView mPlaceHolderView;
    //标志是否第一次初始化数据
    protected boolean mIsFirstInitData = true;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        initArgs(getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mRoot == null){
            int layId = getContentLayoutId();
            mRoot = inflater.inflate(layId, container, false);
            initWidget(mRoot);
        }
        else{
            if(mRoot.getParent() != null){
                ((ViewGroup)mRoot.getParent()).removeView(mRoot);
            }
        }

        return mRoot;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(mIsFirstInitData){
            //触发一次以后不再触发
            mIsFirstInitData = false;
            onFirstInitData();
        }
        //view创建完成后初始化数据
        initData();
    }

    //初始化相关参数
    protected void initArgs(Bundle bundle){
    }

    //得到当前界面的资源文件Id
    //@return 资源文件Id；
    protected abstract int getContentLayoutId();

    //初始化控件
    protected void initWidget(View root){
        mRootUnbinder =  ButterKnife.bind(this, root);
    }

    //初始化数据
    protected void initData(){

    }

    //首次初始化数据
    protected void onFirstInitData(){

    }


    //返回按键触发时调用
    public boolean onBackPressed(){
        return false;
    }

    /**
     * 设置占位布局
     * @param placeHolderView
     */
    public void setPlaceHolderView(PlaceHolderView placeHolderView){
        this.mPlaceHolderView = placeHolderView;
    }
}
