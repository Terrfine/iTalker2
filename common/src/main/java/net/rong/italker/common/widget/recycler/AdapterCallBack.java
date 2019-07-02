package net.rong.italker.common.widget.recycler;

public interface AdapterCallBack<T> {
    void upadte(T data, RecyclerAdapter.ViewHolder<T> holder);
}
