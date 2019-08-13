package net.rong.italker.factory.data.user;

import android.service.autofill.Dataset;

import net.rong.italker.factory.data.DataSource;
import net.rong.italker.factory.data.DbDataSource;
import net.rong.italker.factory.model.db.User;

import java.util.List;

/**
 * 联系人数据源
 */
public interface ContactDataSource extends DbDataSource<User> {
}
