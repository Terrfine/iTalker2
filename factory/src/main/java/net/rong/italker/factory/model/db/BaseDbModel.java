package net.rong.italker.factory.model.db;

import com.raizlabs.android.dbflow.structure.BaseModel;

import net.rong.italker.factory.utils.DiffUiDataCallback;

/**
 * 我们App中的基础的一个BaseDbModel
 * 继承了数据库框架Dbflow中的基础类
 * 同时定义了我们需要的方法
 */
public abstract class BaseDbModel<Model> extends BaseModel implements DiffUiDataCallback.UiDataDiffer<Model> {

}
