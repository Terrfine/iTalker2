package net.rong.italker.common.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.rong.italker.common.R;
import net.rong.italker.common.widget.recycler.RecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GalleryView extends RecyclerView {

    private static final int LOADER_ID = 0X0100;
    private static final int MAX_IMAGE_COUNT = 3; //选中图片的最大数目
    private static final int MIN_IMAGE_FILE_SIZE = 10*1024; //最小的图片大小
    private LoaderCallback  mLoaderCallback = new LoaderCallback();
    private Adapter mAdapter = new Adapter();
    private List<Image> mSelectedImages = new LinkedList<>();
    private SelectedChangeListener mListener;


    public GalleryView(Context context) {
        super(context);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(new GridLayoutManager(getContext(), 4));
        setAdapter(mAdapter);
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Image>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Image image) {
                //cell点击操作，如果说点击是允许的，那么更新对应的Cell状态
                //然后更新界面，同理，如果不允许点击（已经达到最大的选中数量）那么就不刷新界面
                if(onItemSelectClick(image)){
                    holder.updateData(image);
                }
            }

        });
    }

    //初始化方法
    //返回一个LOADERID，可用于销毁Loader
    public  int setup(LoaderManager loaderManager, SelectedChangeListener listener){
        mListener = listener;
        loaderManager.initLoader(LOADER_ID, null, mLoaderCallback);
        return LOADER_ID;
    }

    /**
     * Cell点击的具体逻辑
     * @param image
     * @return
     */
    private boolean onItemSelectClick(Image image){
        boolean notifyRefresh;
        if(mSelectedImages.contains(image)){
            mSelectedImages.remove(image);
            image.isSelect = false;
            notifyRefresh = true;
        }
        else {
            if(mSelectedImages.size() >= MAX_IMAGE_COUNT){
                //得到提示文字
                String str = getResources().getString(R.string.label_gallery_select_max_size);
                //格式化填充
                str = String.format(str, MAX_IMAGE_COUNT);

                Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                notifyRefresh = false;
            }
            else {
                mSelectedImages.add(image);
                image.isSelect = true;
                notifyRefresh = true;
            }
        }

        //如果数据有更改，那么我们需要通知外面的监听者我们的数据选中改变了
        if(notifyRefresh)
            notifySelectChanged();
        return true;
    }



    public String[] getSelectedPath(){
        String[] paths = new String[mSelectedImages.size()];
        int index = 0;
        for (Image image : mSelectedImages){
            paths[index++] = image.path;
        }
        return paths;
    }

    public void clear(){
        for(Image image : mSelectedImages){
            //重置状态
            image.isSelect = false;
        }
        mSelectedImages.clear();
        //通知更新
        mAdapter.notifyDataSetChanged();
    }


    private void notifySelectChanged(){
        SelectedChangeListener listener = mListener;
        //得到监听者，并判断是否有监听者，然后进行回调数量变化
        if(listener != null)
        {
            listener.onSelectedCountChanged(mSelectedImages.size());
        }
    }

    private void updateSource(List<Image> images){
        mAdapter.replace(images);
    }


    private class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor>{
        private final String[] IMAGE_PROJECTION = new String[]{
                MediaStore.Images.Media._ID, //Id
                MediaStore.Images.Media.DATA, //图片路径
                MediaStore.Images.Media.DATE_ADDED, //图片创建时间
        };

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
            //如果是正确的ID则可以进行初始化
            if(id == LOADER_ID){
                return new CursorLoader(getContext(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION,
                        null,
                        null,
                        IMAGE_PROJECTION[2] + " DESC");  //倒序查询
            }
            return null;
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            //当Loader加载完成时
            List<Image> images = new ArrayList<>();
            //判断是否有数据
            if(data != null){
                int count = data.getCount();
                if(count > 0){
                    data.moveToFirst();
                    int indexId = data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]);
                    int indexPath = data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]);
                    int indexDate = data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]);
                    do{
                        int id = data.getInt(indexId);
                        String path = data.getString(indexPath);
                        long dateTime = data.getLong(indexDate);
                        File file = new File(path);
                        if(! file.exists() || file.length() < MIN_IMAGE_FILE_SIZE){
                            //文件不存在，或图片太小
                            continue;
                        }
                        Image image = new Image();
                        image.id = id;
                        image.path = path;
                        image.date = dateTime;
                        images.add(image);
                    }while(data.moveToNext());
                }
            }
            updateSource(images);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            //当Loader销毁或者重置了,进行清空
            updateSource(null);
        }
    }


    /**
     * 内部数据结构
     */
    private static class Image{
        int id;  //数据ID
        String path; //图片的路径
        long date; //图片的创建如期
        boolean isSelect; //是否选中

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Image image = (Image) o;

            return path != null ? path.equals(image.path) : image.path == null;
        }

        @Override
        public int hashCode() {
            return path != null ? path.hashCode() : 0;
        }
    }

    /**
     * 适配器
     */
    private class Adapter extends RecyclerAdapter<Image>{
        @Override
        protected int getItemViewType(int position, Image data) {
            return R.layout.cell_galley;
        }

        @Override
        protected ViewHolder<Image> onCreateViewHolder(View root, int ViewType) {
            return new GalleryView.ViewHolder(root);
        }

    }

    /**
     * Cell对应的Holder
     */
    private class ViewHolder extends RecyclerAdapter.ViewHolder<Image>{
        private ImageView mPic;
        private View mShade;
        private CheckBox mSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mPic = (ImageView)itemView.findViewById(R.id.im_image);
            mShade=  itemView.findViewById(R.id.view_shade);
            mSelected = (CheckBox)itemView.findViewById(R.id.cb_select);
        }

        @Override
        protected void onBind(Image image) {
            Glide.with(getContext())
                    .load(image.path)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不使用缓存，直接从原图加载
                    .centerCrop()
                    .placeholder(R.color.green_200)
                    .into(mPic);
            mShade.setVisibility(image.isSelect? VISIBLE: INVISIBLE);
            mSelected.setVisibility(VISIBLE);
            mSelected.setChecked(image.isSelect);
        }
    }


    /**
     * 对外的监听器
     */
    public interface SelectedChangeListener{
        void onSelectedCountChanged(int count);
    }
}
