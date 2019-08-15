package net.rong.italker.push.frags.message;


import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.rong.italker.common.widget.PortraitView;
import net.rong.italker.factory.model.db.User;
import net.rong.italker.factory.presenter.message.ChatContract;
import net.rong.italker.factory.presenter.message.ChatUserPresenter;
import net.rong.italker.push.R;
import net.rong.italker.push.activities.PersonalActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 用户聊天界面
 * A simple {@link Fragment} subclass.
 */
public class ChatUserFragment extends ChatFragment<User> implements ChatContract.UserView {

    @BindView(R.id.im_portrait)
    PortraitView mPortraitView;

    private MenuItem mUserInfoMenuItem;

    public ChatUserFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_chat_user;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        Toolbar toolbar = mToolbar;
        toolbar.inflateMenu(R.menu.char_user);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.action_person){
                    onPortraitClick();
                }
                return false;
            }
        });
        mUserInfoMenuItem = toolbar.getMenu().findItem(R.id.action_person);
    }

    //进行高度的综合运算，透明我们的头像和icon
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        super.onOffsetChanged(appBarLayout, verticalOffset);
        MenuItem menuItem = mUserInfoMenuItem;
        View view = mPortraitView;
        if(menuItem == null || view == null)
            return;
        Log.e("toolbar",String.valueOf(verticalOffset));
        if(verticalOffset == 0){
            //完全展开
            view.setVisibility(View.VISIBLE);
            view.setScaleX(1);
            view.setScaleY(1);
            view.setAlpha(1);
            //隐藏菜单
            menuItem.setVisible(false);
            menuItem.getIcon().setAlpha(0);
        }else {
            menuItem.setVisible(true);
            verticalOffset = Math.abs(verticalOffset);
            final int totalScrollRange = appBarLayout.getTotalScrollRange();
            if(verticalOffset >= totalScrollRange){
                view.setVisibility(View.INVISIBLE);
                view.setScaleX(0);
                view.setScaleY(0);
                view.setAlpha(0);

                //显示菜单
                menuItem.getIcon().setAlpha(255);
            }else {
                view.setVisibility(View.VISIBLE);
                float progress = (totalScrollRange-verticalOffset)/(float)totalScrollRange;
                view.setScaleX(progress);
                view.setScaleY(progress);
                view.setAlpha(progress);

                //和头像相反

                menuItem.getIcon().setAlpha((int) ((1.0-progress)*255));
            }
        }
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick(){
        PersonalActivity.show(getContext(), mReceiverId);
    }

    @Override
    protected ChatContract.Presenter initPresenter() {
        //初始化Presenter
        return new ChatUserPresenter(this, mReceiverId);
    }

    @Override
    public void onInit(User user) {
        // 对和你聊天的朋友的信息进行初始化操作
    }
}
