package net.rong.italker.push.Helper;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;

/**完成对fragment的调度与重用问题
 * 达到最优的fragment切换
 *
 */
public class NavHelper<T> {

    //所有Tab的集合
    private final SparseArray<Tab<T>> tabs = new SparseArray();
    //初始化参数
    private final Context context;
    private final int containerId;
    private final FragmentManager fragmentManager;
    private OnTabChangedListener<T> listener;
    //当前选中的Tab
    private Tab<T> currentTab;

    public NavHelper(Context context, int containerId, FragmentManager fragmentManager, OnTabChangedListener<T> listener) {
        this.context = context;
        this.containerId = containerId;
        this.fragmentManager = fragmentManager;
        this.listener = listener;
    }

    /**添加Tab
     * @param menuId Tab对应的菜单Id
     * @param tab
     */
    public NavHelper<T> add(int menuId, Tab<T> tab){
        tabs.put(menuId, tab);
        return this;
    }

    public Tab<T> getCurrentTab(){
        return currentTab;
    }

    /**
     * 执行点击菜单的操作
     * @param menuId
     * @return
     */
    public boolean performClickMenu(int menuId){
        //集合中寻找点击的菜单对应的Tab
        //如果有则进行处理
        Tab<T> tab =tabs.get(menuId);
        if(tab != null){
            doSelect(tab);
            return true;
        }
        return false;
    }


    /**
     * 进行真是的Tab选择操作
     * @param tab
     */
    private void doSelect(Tab<T> tab){
        Tab<T> oldTab = null;
        if(currentTab != null){
            oldTab = currentTab;
            if(oldTab == tab){
                notifyTabReselect(tab);
                return;
            }
        }
        currentTab = tab;
        doTabChanged(currentTab, oldTab);
    }


    //fragment的真实的调度操作
    private void doTabChanged(Tab<T> newTab, Tab<T> oldTab){
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if(oldTab != null){
            if(oldTab.fragment != null){
                //从界面移除，但是还在Fragment的缓存空间
                ft.detach(oldTab.fragment);
            }
        }

        if(newTab != null){
            if(newTab.fragment == null){
                Fragment fragment =  Fragment.instantiate(context, newTab.cls.getName(), null);
                //缓存起来
                newTab.fragment = fragment;
                //提交到FragmentMannager
                ft.add(containerId, fragment, newTab.cls.getName());
            }
            else {
                //从FragmentManager的缓存空间中重新加载到界面中
                ft.attach(newTab.fragment);
            }
        }
        ft.commit();
        //通知回调
        notifyTabSelect(newTab, oldTab);
    }

    /**
     * 回调监听器
     * @param newTab
     * @param oldTab
     */
    private void notifyTabSelect(Tab<T> newTab, Tab<T> oldTab){
        // TODO 二次点击Tab所做的操作
        if(listener != null){
            listener.onTabChanged(newTab, oldTab);
        }
    }

    private void notifyTabReselect(Tab<T> tab){
        // TODO 二次点击Tab所做的操作
    }


    /**
     * 我们所有的Tab基础属性
     * @param <T> 泛型的额外参数
     */
    public static class Tab<T>{

        public Tab(Class<?> cls, T extra) {
            this.cls = cls;
            this.extra = extra;
        }

        //Fragment对应的Class
        public Class<?> cls;
        //额外的字段，用户自己设定需要使用
        public T extra;

        //Package权限，在包内可使用
        Fragment fragment;
    }

    /**
     * 定义事件处理完成后的回调接口
     * @param <T>
     */
    public interface OnTabChangedListener<T>{
        void onTabChanged(Tab<T> newTab, Tab<T> oldTab);
    }
}
