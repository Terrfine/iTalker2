package net.rong.italker.factory.data.group;

import net.rong.italker.factory.model.card.GroupCard;
import net.rong.italker.factory.model.card.GroupMemberCard;
import net.rong.italker.factory.model.db.Group;

/**
 * 群中心的接口定义
 */
public interface GroupCenter {
    //群卡片的处理
    void dispatch(GroupCard... cards);

    //群成员的处理
    void dispatch(GroupMemberCard... cards);
}
