package net.rong.italker.factory.model.db.view;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;

import net.rong.italker.factory.model.db.AppDatabase;

/**
 * 群成员对应的用户的简单信息表
 */
@QueryModel(database = AppDatabase.class)
public class MemberUserModel {
    @Column
    public String userId;  //User_id//Member_userId
    @Column
    public String name;  //User_name
    @Column
    public String alias; //Members_alias
    @Column
    public String portrait;  //User_portrait
}
