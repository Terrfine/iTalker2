package net.rong.italker.push.frags.main;


import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.rong.italker.common.app.Fragment;
import net.rong.italker.common.app.PresenterFragment;
import net.rong.italker.common.widget.EmptyView;
import net.rong.italker.common.widget.PortraitView;
import net.rong.italker.common.widget.recycler.RecyclerAdapter;
import net.rong.italker.factory.model.card.UserCard;
import net.rong.italker.factory.model.db.User;
import net.rong.italker.factory.presenter.contact.ContactContract;
import net.rong.italker.factory.presenter.contact.ContactPresenter;
import net.rong.italker.push.R;
import net.rong.italker.push.activities.MessageActivity;
import net.rong.italker.push.activities.PersonalActivity;
import net.rong.italker.push.frags.search.SearchUserFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends PresenterFragment<ContactContract.Presenter>
implements ContactContract.View {

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    //适配器，User，可以直接从数据库查询数据
    private RecyclerAdapter<User> mAdapter;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<User>() {

            @Override
            protected int getItemViewType(int position, User data) {
                //返回cell的布局id
                return R.layout.cell_contact_list;
            }

            @Override
            protected ViewHolder<User> onCreateViewHolder(View root, int ViewType) {
                return new ContactFragment.ViewHolder(root);
            }
        });

        //点击事件监听
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<User>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, User user) {
                //跳转到聊天界面
                MessageActivity.show(getContext(), user);
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
        return R.layout.fragment_contact;
    }

    @Override
    protected ContactContract.Presenter initPresenter() {
        return new ContactPresenter(this);
    }

    @Override
    public RecyclerAdapter<User> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        //进行界面操作
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount()>0);
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<User>{
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.txt_desc)
        TextView mDesc;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(User user) {
            mPortraitView.setup(Glide.with(ContactFragment.this), user);
            mName.setText(user.getName());
            mDesc.setText(user.getDesc());
        }

        @OnClick(R.id.im_portrait)
        void onPortraitClick(){
            PersonalActivity.show(getContext(), mData.getId());
        }
    }
}
