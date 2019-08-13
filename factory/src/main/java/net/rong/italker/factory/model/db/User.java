package net.rong.italker.factory.model.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import net.rong.italker.factory.model.Author;
import net.rong.italker.factory.utils.DiffUiDataCallback;

import java.util.Date;
import java.util.Objects;

@Table(database = AppDatabase.class)
public class User extends BaseDbModel<User> implements Author {
    public static final int SEX_MAN = 1;
    public static final int SEX_WOMAN = 2;

    @PrimaryKey
    private String id;
    @Column
    //用户名必须唯一
    private String name;
    @Column
    //电话必须唯一
    private String phone;
    @Column
    private String portrait;
    @Column
    private String desc;
    @Column
    private int sex = 0;

    //我对某人的备注信息，也应该写入到数据库中
    @Column
    private String alias;

    //用户关注人的数量
    @Column
    private int follows;

    //用户粉丝的数量
    @Column
    private int following;

    //我与当前User的关系状态，是否已经关注了这个人
    @Column
    private boolean isFollow;

    //时间字段
    @Column
    private Date modifyAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (sex != user.sex) return false;
        if (follows != user.follows) return false;
        if (following != user.following) return false;
        if (isFollow != user.isFollow) return false;
        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (phone != null ? !phone.equals(user.phone) : user.phone != null) return false;
        if (portrait != null ? !portrait.equals(user.portrait) : user.portrait != null)
            return false;
        if (desc != null ? !desc.equals(user.desc) : user.desc != null) return false;
        if (alias != null ? !alias.equals(user.alias) : user.alias != null) return false;
        return modifyAt != null ? modifyAt.equals(user.modifyAt) : user.modifyAt == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean isSame(User old) {
        //主要关注Id即可
        return this == old || Objects.equals(id, old.id);
    }

    @Override
    public boolean isUiContentSame(User old) {
        return this == old || (
                //TODO
                Objects.equals(name, old.name)
                        && Objects.equals(portrait, old.portrait)
                        && Objects.equals(sex, old.sex)
                        && Objects.equals(desc, old.desc)
                        && Objects.equals(isFollow, old.isFollow));

    }
}
