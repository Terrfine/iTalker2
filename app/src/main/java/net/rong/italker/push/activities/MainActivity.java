package net.rong.italker.push.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.widget.FloatActionButton;
import net.rong.italker.common.Common;
import net.rong.italker.common.app.Activity;
import net.rong.italker.common.widget.PortraitView;
import net.rong.italker.factory.persistence.Account;
import net.rong.italker.push.Helper.NavHelper;
import net.rong.italker.push.R;
import net.rong.italker.push.activities.AccountActivity;
import net.rong.italker.push.frags.assist.PermissionsFragment;
import net.rong.italker.push.frags.main.ActiveFragment;
import net.rong.italker.push.frags.main.ContactFragment;
import net.rong.italker.push.frags.main.GroupFragment;

import java.net.ConnectException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener, NavHelper.OnTabChangedListener<Integer> {

    @BindView(R.id.appbar)
    View mLayoutAppbar;

    @BindView(R.id.im_portrait)
    PortraitView mPortraitView;

    @BindView(R.id.txt_title)
    TextView mTitle;

    @BindView(R.id.lay_container)
    FrameLayout mContainer;

    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    @BindView(R.id.btn_action)
    FloatActionButton mAction;

    private NavHelper<Integer> mNavHelper;

    public static void show(Context context){
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        if(Account.isComplete()){
            //客户端用户信息是否完全，完全则走正常流程
            return super.initArgs(bundle);
        }else {
            UserActivity.show(this);
            return false;
        }
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    protected void initData() {
        super.initData();
        Menu menu = mNavigation.getMenu();
        //触发首次选中Home，会自动触发onNavigationItemSelected
        menu.performIdentifierAction(R.id.action_home,  0);
    }


    @OnClick(R.id.im_portrait)
    void onPortraitClick(){
        PersonalActivity.show(this, Account.getUserId());
    }

    @Override
    protected void initWidget() {
        super.initWidget();
//        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//        }
        //初始化底部辅助工具类
        mNavHelper = new NavHelper<>(this, R.id.lay_container, getSupportFragmentManager(), this);
        mNavHelper.add(R.id.action_home, new NavHelper.Tab<Integer>(ActiveFragment.class, R.string.title_home))
                .add(R.id.action_group, new NavHelper.Tab<Integer>(GroupFragment.class, R.string.title_group))
                .add(R.id.action_contact, new NavHelper.Tab<Integer>(ContactFragment.class, R.string.title_contact));

        mNavigation.setOnNavigationItemSelectedListener(this);

        Glide.with(this).load(R.drawable.bg_src_morning).centerCrop().into(new ViewTarget<View, GlideDrawable>(mLayoutAppbar) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                this.view.setBackground(resource.getCurrent());
            }
        });
    }

    @OnClick({R.id.im_search})
    void onSearchMenuClick(){
        //在群的界面的时候，点击顶部的搜索就进入群搜索界面
        //其他都为人搜索的界面
        int type = Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_group)?
                SearchActivity.TYPE_GROUP : SearchActivity.TYPE_USER;
        SearchActivity.show(this, type);
    }

    @OnClick({R.id.btn_action})
    void onActionClice(){
        //浮动按钮点击时，判断当前界面是群还是联系人界面
        //如果是群，则打开群创建的界面
        //如果是其他，则打开添加用户揭秘那
        if(Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_group)){
            //TODO 打开群创建界面
        }else {
            SearchActivity.show(this, SearchActivity.TYPE_USER);
        }
    }

    boolean isFirst = true;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return mNavHelper.performClickMenu(menuItem.getItemId());
    }

    /**
     * NavHelper处理后回调的方法
     * @param newTab
     * @param oldTab
     */
    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
        //从额外字段中取出我们的Title资源文件
        mTitle.setText(newTab.extra);

        //对浮动按钮进行隐藏与显示的动画
        float transY = 0;
        float rotation = 0;
        if(newTab.extra.equals(R.string.title_home)){
            //对按钮进行隐藏
            transY = Ui.dipToPx(getResources(), 76);
        }
        else if(newTab.extra.equals(R.string.title_group)){
            //添加群按钮
            mAction.setImageResource(R.drawable.ic_group_add);
        }
        else {
            //联系人
            mAction.setImageResource(R.drawable.ic_contact_add);
        }
        //开始动画
        //旋转，Y轴位移，弹性插值器
        mAction.animate()
                .rotation(rotation)
                .translationY(transY)
                .setInterpolator(new AnticipateOvershootInterpolator(1))
                .setDuration(480)
                .start();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode){
//            case 1:
//                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//
//                }else
//                {
//                    finish();
//                }
//                break;
//            default:
//                break;
//        }
//    }
}
