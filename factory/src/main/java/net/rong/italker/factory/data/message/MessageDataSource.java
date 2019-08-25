package net.rong.italker.factory.data.message;

import net.rong.italker.factory.data.DataSource;
import net.rong.italker.factory.data.DbDataSource;
import net.rong.italker.factory.model.db.Message;

/**
 * 消息的数据源定义，他的实现是MessageRepository,MessageGroupRepository
 * 关注的对象是Message表
 */
public interface MessageDataSource extends DbDataSource<Message> {
}
