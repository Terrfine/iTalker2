package net.rong.italker.factory.data.user;

import net.rong.italker.factory.model.card.UserCard;

/**
 * 用户中心的基本定义
 */
public interface UserCenter {
    //分发处理一堆用户卡片,并更新到数据库
    void dispatch(UserCard... userCards);
}
