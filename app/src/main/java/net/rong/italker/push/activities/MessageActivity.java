package net.rong.italker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import net.rong.italker.common.app.Activity;
import net.rong.italker.factory.model.Author;
import net.rong.italker.factory.model.db.Group;
import net.rong.italker.push.R;

public class MessageActivity extends Activity {

    /**
     * 显示人的聊天 界面
     * @param context
     * @param author 人的信息
     */
    public static void show(Context context, Author author){
        context.startActivity(new Intent(context, MessageActivity.class));
    }


    /**
     * 发起群聊天
     * @param context
     * @param group
     */
    public static void show(Context context, Group group){
        context.startActivity(new Intent(context, MessageActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_message;
    }

}
