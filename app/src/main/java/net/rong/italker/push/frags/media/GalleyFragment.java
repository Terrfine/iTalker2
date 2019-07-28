package net.rong.italker.push.frags.media;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import net.qiujuer.genius.ui.Ui;
import net.rong.italker.common.app.Fragment;
import net.rong.italker.common.widget.GalleryView;
import net.rong.italker.push.R;

/**
 * 图片选择Fragment
 * A simple {@link Fragment} subclass.
 */
public class GalleyFragment extends BottomSheetDialogFragment implements GalleryView.SelectedChangeListener{
    private GalleryView mGallery;
    private OnSelectedListener mListener;

    public GalleyFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //先使用默认的
        return new BottomSheetDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        //获取GalleryView
        View root = inflater.inflate(R.layout.fragment_galley,container, false);
        mGallery = (GalleryView)root.findViewById(R.id.view_gallery);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGallery.setup(getLoaderManager(), this);
    }



    protected int getContentLayoutId() {
        return R.layout.fragment_galley;
    }

    @Override
    public void onSelectedCountChanged(int count) {
        //如果选中一张图片，隐藏自己
        if(count > 0){
            dismiss();
            if(mListener != null){
                //得到选中图片的路径
                String[] paths = mGallery.getSelectedPath();
                mListener.onSelectedImage(paths[0]);
                //取消和唤起者之间的引用，加快内存回收
                mListener = null;
            }
        }
    }


    //设置监听并返回自己
    public GalleyFragment setListener(OnSelectedListener listener){
        mListener = listener;
        return this;
    }

    /**
     * 选中图片的监听器
     */
    public interface OnSelectedListener{
        void onSelectedImage(String path);

    }

    //解决顶部状态栏变黑而写的透明的dialog
    public static class TransStatusBottomSheetDialog extends BottomSheetDialog{
        public TransStatusBottomSheetDialog(@NonNull Context context) {
            super(context);
        }

        public TransStatusBottomSheetDialog(@NonNull Context context, int theme) {
            super(context, theme);
        }


        protected TransStatusBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final Window window =getWindow();
            if(Build.VERSION.SDK_INT >= 21){
                View decorView = window.getDecorView();
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.setStatusBarColor(Color.TRANSPARENT);
            }

//            if(window == null)
//                return;
//            //得到屏幕高度
//            int screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
//
//            int statusHeight = (int) Ui.dipToPx(getContext().getResources(), 25);
//
            //计算dialog的高度
//            int dialogHeight = screenHeight - statusHeight;
//            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight <= 0? ViewGroup.LayoutParams.MATCH_PARENT: dialogHeight);
        }
    }
}
