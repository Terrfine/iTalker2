package net.rong.italker.common.widget.recycler;

import android.provider.ContactsContract;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.rong.italker.common.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @param <T>
 */
public abstract class RecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder<T>>
implements View.OnClickListener, View.OnLongClickListener, AdapterCallBack<T>{

    private final List<T> mDataList ;
    private AdapterListener<T> mListener;

    //构造函数模块
    public RecyclerAdapter(List<T> dataList, AdapterListener<T> listener){
        this.mDataList = dataList;
        this.mListener = listener;
    }

    public RecyclerAdapter(AdapterListener<T> listener){
        this(new ArrayList<T>(), listener);
    }

    public RecyclerAdapter(){
        this(null);
    }

    /**
     * 复写默认的布局类型返回
     * @param position
     * @return 类型，其实复写后返回的都是XML文件的ID
     */
    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position, mDataList.get(position));
    }

    /**
     * @param position 坐标
     * @param data 当前的数据
     * @return  XML文件的ID,用于创建ViewHolder
     */
    @LayoutRes
    protected abstract int getItemViewType(int position, T data);

    /**
     * @param parent
     * @param viewType viewType界面的类型，约定为XML布局的Id
     * @return
     */
    @NonNull
    @Override
    public ViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(viewType, parent ,false);
        //通过子类必须是实现的方法，得到一个ViewHolder
        ViewHolder<T> holder = onCreateViewHolder(root, viewType);

        //设置View的Tag为ViewHolder，进行双向绑定
        root.setTag(R.id.tag_recycler_holder, holder);
        //设置事件点击
        root.setOnClickListener(this);
        root.setOnLongClickListener(this);

        //进行界面注解绑定
        holder.unbunder =  ButterKnife.bind(holder, root);
        //绑定callback
        holder.callBack = this;
        return holder;
    }

    /**
     * 得到一个新的ViewHolder
     * @param root 根布局
     * @param ViewType 布局类型，其实就是XML的ID
     * @return
     */
    protected abstract ViewHolder<T> onCreateViewHolder(View root, int ViewType);


    @Override
    public void onBindViewHolder(@NonNull ViewHolder<T> holder, int position) {
        T data = mDataList.get(position);
        holder.bind(data);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    public List<T> getItems(){
        return mDataList;
    }
    @Override
    public void upadte(T data, ViewHolder<T> holder) {
        int pos = holder.getAdapterPosition();
        if(pos >= 0){
            //移除并更新
            mDataList.remove(pos);
            mDataList.add(pos, data);
            notifyItemChanged(pos);
        }
    }

    @Override
    public void onClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
        if(this.mListener != null){
            int pos = viewHolder.getAdapterPosition();
            this.mListener.onItemClick(viewHolder, mDataList.get(pos));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
        if(this.mListener != null){
            int pos = viewHolder.getAdapterPosition();
            this.mListener.onItemClick(viewHolder, mDataList.get(pos));
            return true;
        }
        return false;
    }

    //设置适配器监听
    public void setListener(AdapterListener<T> adapterListener){
        this.mListener =adapterListener;
    }

    /**
     * z自定义监听器
     * @param <T>
     */
    public interface AdapterListener<T>{
        //当Cell点击时触发
        void onItemClick(RecyclerAdapter.ViewHolder holder, T data);
        //当Cell长按时触发
        void onItemLongClick(RecyclerAdapter.ViewHolder holder, T data);
    }

    //插入一条数据
    public void add(T data){
        mDataList.add(data);
        //notifyDataSetChanged();
        notifyItemInserted(mDataList.size() -1);
    }

    //插入一堆数据，并通知范围更新
    public void add(T... dataList){
        if(dataList != null && dataList.length > 0){
            int startPos = mDataList.size();
            Collections.addAll(mDataList, dataList);
            notifyItemRangeInserted(startPos, dataList.length);
        }
    }

    public void add(Collection<T> dataList){
        if(dataList != null && dataList.size() > 0){
            int startPos = mDataList.size();
            mDataList.addAll(dataList);
            notifyItemRangeInserted(startPos, dataList.size());
        }
    }

    //删除操作
    public void clear(){
        mDataList.clear();
        notifyDataSetChanged();
    }

    //替换为一个新的集合，其中包括了清空
    public void replace(Collection<T> dataList){
        mDataList.clear();
        if(dataList == null || dataList.size() ==0){
            return;
        }
        mDataList.addAll(dataList);
        notifyDataSetChanged();
    }

    /**
     * 自定义ViewHolder
     * @param <T>
     */
    public static abstract class ViewHolder<T> extends RecyclerView.ViewHolder{
        protected T mData;
        private Unbinder unbunder;
        private AdapterCallBack<T> callBack;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void bind(T data){
            this.mData = data;
            onBind(data);
        }

        //抽象方法，子类复写
        protected abstract void onBind(T data);


        /**
         * Holder自己对自己对应的Data进行更新操作
         * @param data
         */
        public void updateData(T data){
            if(callBack != null){
                this.callBack.upadte(data, this);
            }
        }
    }

    /**
     * 对回调接口做一次实现
     * @param <T>
     */
    public abstract static class AdapterListenerImpl<T> implements AdapterListener<T>{
        @Override
        public void onItemClick(ViewHolder holder, T data) {

        }

        @Override
        public void onItemLongClick(ViewHolder holder, T data) {

        }
    }
}
