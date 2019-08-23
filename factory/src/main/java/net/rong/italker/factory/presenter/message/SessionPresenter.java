package net.rong.italker.factory.presenter.message;

import android.support.v7.util.DiffUtil;

import net.rong.italker.factory.model.db.Session;
import net.rong.italker.factory.presenter.BaseSourcePresenter;
import net.rong.italker.factory.utils.DiffUiDataCallback;

import java.util.List;

/**
 * 最近聊天列表的Presenter
 */
public class SessionPresenter extends BaseSourcePresenter<Session, Session,
        SessionDataSource, SessionContract.View>
implements SessionContract.Presenter{

    public SessionPresenter(SessionContract.View view) {
        super(new SessionRepository(), view);
    }

    @Override
    public void onDataLoaded(List<Session> sessions) {
        SessionContract.View view =getView();
        if (view == null)
            return;

        List<Session> old = view.getRecyclerAdapter().getItems();
        DiffUiDataCallback<Session> callback = new DiffUiDataCallback<>(old, sessions);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        refreshData(result, sessions);
    }
}
