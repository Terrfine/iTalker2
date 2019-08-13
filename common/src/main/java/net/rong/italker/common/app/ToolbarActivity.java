package net.rong.italker.common.app;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import net.rong.italker.common.R;

public abstract class ToolbarActivity extends Activity {
    private Toolbar mToolbar;

    @Override
    protected void initWidget() {
        super.initWidget();
        initToolbar((Toolbar) findViewById(R.id.toolbar));
    }

    /**
     * 初始化toolbar
     * @param toolbar
     */
    public void initToolbar(Toolbar toolbar){
        mToolbar = toolbar;
        if(mToolbar != null){
            setSupportActionBar(toolbar);
        }
        initTitleNeedBack();
    }

    protected void initTitleNeedBack(){
        //设置左上角的返回按钮为实际的返回效果
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

}
