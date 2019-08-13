package net.rong.italker.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.rong.italker.factory.model.db.Session;
import net.rong.italker.factory.model.db.Session_Table;
import net.rong.italker.factory.model.db.User;
import net.rong.italker.factory.model.db.User_Table;

/**
 * 会话地辅助工具类
 */
public class SessionHelper {
    //从本地查询session
    public static Session findFromLocal(String id) {
        return SQLite.select()
                .from(Session.class)
                .where(Session_Table.id.eq(id))
                .querySingle();
    }
}
