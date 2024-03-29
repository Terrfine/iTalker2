package net.rong.italker.push.frags.main;


import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.qiujuer.genius.ui.Ui;
import net.rong.italker.common.app.Fragment;
import net.rong.italker.common.app.PresenterFragment;
import net.rong.italker.common.widget.EmptyView;
import net.rong.italker.common.widget.GalleryView;
import net.rong.italker.common.widget.PortraitView;
import net.rong.italker.common.widget.recycler.RecyclerAdapter;
import net.rong.italker.face.Face;
import net.rong.italker.factory.model.db.Session;
import net.rong.italker.factory.model.db.User;
import net.rong.italker.factory.presenter.message.SessionContract;
import net.rong.italker.factory.presenter.message.SessionPresenter;
import net.rong.italker.push.R;
import net.rong.italker.push.activities.MessageActivity;
import net.rong.italker.push.activities.PersonalActivity;
import net.rong.italker.utils.DateTimeUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveFragment extends PresenterFragment<SessionContract.Presenter>
        implements SessionContract.View {
    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    //适配器，User，可以直接从数据库查询数据
    private RecyclerAdapter<Session> mAdapter;

    public ActiveFragment() {
        // Required empty public constructor
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<Session>() {

            @Override
            protected int getItemViewType(int position, Session data) {
                //返回cell的布局id
                return R.layout.cell_chat_list;
            }

            @Override
            protected ViewHolder<Session> onCreateViewHolder(View root, int ViewType) {
                return new ActiveFragment.ViewHolder(root);
            }
        });

        //点击事件监听
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Session>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Session session) {
                //跳转到聊天界面
                MessageActivity.show(getContext(), session);
            }
        });

        //初始化占位布局
        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected void onFirstInitData() {
        super.onFirstInitData();
        //进行一次数据加载
        mPresenter.start();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_active;
    }

    @Override
    protected SessionContract.Presenter initPresenter() {
        return new SessionPresenter(this);
    }

    @Override
    public RecyclerAdapter<Session> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    //界面数据渲染
    class ViewHolder extends RecyclerAdapter.ViewHolder<Session> {
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.txt_content)
        TextView mContent;

        @BindView(R.id.txt_time)
        TextView mTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Session session) {
            mPortraitView.setup(Glide.with(ActiveFragment.this), session.getPicture());
            mName.setText(session.getTitle());

            String str = TextUtils.isEmpty(session.getContent()) ? "" : session.getContent();
            Spannable spannable = new SpannableString(str);
            //解析表情
            Face.decode(mContent, spannable, (int) mContent.getTextSize());
            //设置内容
            mContent.setText(spannable);
            mTime.setText(DateTimeUtil.getSampleDate(session.getModifyAt()));
        }
    }
}
