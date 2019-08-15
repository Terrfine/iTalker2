package net.rong.italker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

import net.rong.italker.common.app.Activity;
import net.rong.italker.common.app.Fragment;
import net.rong.italker.factory.model.Author;
import net.rong.italker.factory.model.db.Group;
import net.rong.italker.push.R;
import net.rong.italker.push.frags.message.ChatGroupFragment;
import net.rong.italker.push.frags.message.ChatUserFragment;

public class MessageActivity extends Activity {
    //可以是群也可以是人的id
    public static final String KEY_RECEIVER_ID = "KEY_RECEIVER_ID";
    public static final String KEY_RECEIVER_IS_GROUP = "KEY_RECEIVER_IS_GROUP";

    private String mReceiverId;
    private boolean mIsGroup;

    /**
     * 显示人的聊天 界面
     * @param context
     * @param author 人的信息
     */
    public static void show(Context context, Author author){
        if(author == null || context == null || TextUtils.isEmpty(author.getId())){
            return;
        }
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, author.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP,false);
        context.startActivity(intent);
    }


    /**
     * 发起群聊天
     * @param context
     * @param group
     */
    public static void show(Context context, Group group){
        if(group == null || context == null || TextUtils.isEmpty(group.getId())){
            return;
        }
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, group.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP,true);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mReceiverId = bundle.getString(KEY_RECEIVER_ID);
        mIsGroup = bundle.getBoolean(KEY_RECEIVER_IS_GROUP);
        return !TextUtils.isEmpty(mReceiverId);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
        Fragment fragment;
        if(mIsGroup)
            fragment = new ChatGroupFragment();
        else
            fragment = new ChatUserFragment();
        //从Activity中传递参数到Fragment中
        Bundle bundle = new Bundle();
        bundle.putString(KEY_RECEIVER_ID, mReceiverId);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.lay_container, fragment)
                .commit();
//        Window window = getWindow();
//        View decorView= window.getDecorView();
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
    }
}
