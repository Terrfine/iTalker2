package net.rong.italker.factory.presenter;

import android.support.annotation.StringRes;

import net.rong.italker.common.widget.recycler.RecyclerAdapter;

/**
 * MVP模式中公共的基本契约
 */
public interface BaseContract {
    /**
     * 基本的界面职责
     * @param <T>
     */
    interface View<T extends Presenter>{
        //公共的：显示一个字符串错误
        void showError(@StringRes int str);

        //公共的：显示进度条
        void showLoading();

        //支持设置一个Presenter
        void setPresenter(T presenter);
    }

    interface Presenter{
        //公用的开始方法
        void start();

        //公用的销毁触发
        void destroy();
    }

    /**
     * 基本的一个列表的View的职责
     * @param <T>
     */
    interface RecyclerView<T extends Presenter, ViewModel> extends View<T>{
        //界面端只能刷新整个数据集合，不能精确到每一条数据更新
//        void onDone(List<User> users);

        //拿到一个适配器，然后自己自主的进行刷新
        RecyclerAdapter<ViewModel> getRecyclerAdapter();

        //当适配器数据更改了的时候触发
        void onAdapterDataChanged();
    }
}
