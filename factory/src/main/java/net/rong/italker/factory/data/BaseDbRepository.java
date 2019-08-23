package net.rong.italker.factory.data;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.qiujuer.genius.kit.reflect.Reflector;
import net.rong.italker.factory.data.helper.DbHelper;
import net.rong.italker.factory.model.db.BaseDbModel;
import net.rong.italker.factory.model.db.User;
import net.rong.italker.utils.CollectionUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * 基础的数据库仓库
 * 实现对数据库的基本的监听操作
 */
public abstract class BaseDbRepository<Data extends BaseDbModel<Data>> implements DbDataSource<Data>,
        DbHelper.ChangedListener<Data> , QueryTransaction.QueryResultListCallback<Data>{

    //和presenter交互的回调
    private SucceedCallback<List<Data>> callback;
    protected final LinkedList<Data> dataList = new LinkedList<>();  //当前缓存的数据
    private Class<Data> dataClass; //当前Data的对应的真是的class信息

    @SuppressWarnings("unchecked")
    public BaseDbRepository() {
        //拿当前类的泛型数组信息
        Type[] types = Reflector.getActualTypeArguments(BaseDbRepository.class, this.getClass());
        dataClass = (Class<Data>) types[0];
    }

    @Override
    public void load(SucceedCallback<List<Data>> callback) {
        this.callback = callback;
        //进行数据库监听操作
        registerDbChangedListener();
    }

    @Override
    public void dispose() {
        //取消监听，销毁数据
        this.callback = null;
        DbHelper.removeChangedListener(dataClass, this);
        dataList.clear();
    }

    //数据库统一通知的地方：增加更改
    @Override
    public void onDataSave(Data... list) {
        boolean isChanged = false;
        //当数据库数据变更的操作
        for (Data data : list) {
            //是关注的人同时不是我自己
            if(isRequired(data)){
                insertOrUpdate(data);
                isChanged = true;
            }
        }
        if(isChanged)
            notifyDataChange();
    }



    //数据库统一通知的地方：删除
    @Override
    public void onDataDelete(Data... list) {

        //在删除情况下不用进行过滤删除
        boolean isChanged = false;
        //当数据库数据删除的操作
        for (Data data : list) {
            if(dataList.remove(data)){
                isChanged =true;
            }
        }

        //有数据变更，则进行界面刷新
        if(isChanged)
            notifyDataChange();
    }

    //插入或更新
    private void insertOrUpdate(Data data){
        int index = indexOf(data);
        if(index >= 0){
            replace(index,data);
        }else {
            insert(data);
        }
    }

    //更新操作，更新某个坐标下的数据
    private void replace(int index, Data data){
        dataList.remove(index);
        dataList.add(index, data);
    }

    //添加方法
    protected void insert(Data data){
        dataList.add(data);
    }


    //查询一个数据是否在当前的缓存数据中，如果在则返回坐标
    private int indexOf(Data newData){
        int index = -1;
        for (Data data : dataList) {
            index++;
            if(data.isSame(newData)){
                return index;
            }
        }
        return -1;
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Data> tResult) {
        //数据库加载数据成功
        if(tResult.size()==0){
            dataList.clear();
            notifyDataChange();
            return;
        }
        //转变为数组
        //Data[] users = tResult.toArray(new Data[0]);  //不可行
        Data[] datas = CollectionUtil.toArray(tResult, dataClass);
        //回到数据集更新的操作中
        onDataSave(datas);
    }

    /**
     * 检查一个Data是否是我们需要关注的数据
     * @param data
     * @return
     */
    protected abstract boolean isRequired(Data data);

    protected void registerDbChangedListener(){
        DbHelper.addChangedListener(dataClass, this);
    }

    //通知界面刷新的方法
    private void notifyDataChange(){
        SucceedCallback<List<Data>> callback = this.callback;
        if(callback != null){
            callback.onDataLoaded(dataList);
        }
    }
}
