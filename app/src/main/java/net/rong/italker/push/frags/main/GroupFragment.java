package net.rong.italker.push.frags.main;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
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
import net.rong.italker.factory.model.db.Group;
import net.rong.italker.factory.model.db.User;
import net.rong.italker.factory.presenter.group.GroupsContract;
import net.rong.italker.factory.presenter.group.GroupsPresenter;
import net.rong.italker.push.R;
import net.rong.italker.push.activities.MessageActivity;
import net.rong.italker.push.activities.PersonalActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends PresenterFragment<GroupsContract.Presenter>
implements GroupsContract.View{

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;


    //适配器，User，可以直接从数据库查询数据
    private RecyclerAdapter<Group> mAdapter;
    public GroupFragment() {
        // Required empty public constructor
    }
    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<Group>() {

            @Override
            protected int getItemViewType(int position, Group group) {
                //返回cell的布局id
                return R.layout.cell_group_list;
            }

            @Override
            protected ViewHolder<Group> onCreateViewHolder(View root, int ViewType) {
                return new GroupFragment.ViewHolder(root);
            }
        });
        //点击事件监听
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Group>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Group group) {
                //跳转到聊天界面
                MessageActivity.show(getContext(), group);
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
        return R.layout.fragment_group;
    }

    @Override
    protected GroupsContract.Presenter initPresenter() {
        return new GroupsPresenter(this);
    }

    @Override
    public RecyclerAdapter<Group> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        //进行界面操作
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount()>0);
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<Group>{
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.txt_desc)
        TextView mDesc;

        @BindView(R.id.txt_member)
        TextView mMember;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Group group) {
            mPortraitView.setup(Glide.with(GroupFragment.this), group.getPicture());
            mName.setText(group.getName());
            mDesc.setText(group.getDesc());
            if (group.holder != null && group.holder instanceof String){
                mMember.setText((String)group.holder);
            }else {
                mMember.setText("");
            }
        }

        @OnClick(R.id.im_portrait)
        void onPortraitClick(){
            PersonalActivity.show(getContext(), mData.getId());
        }
    }
}
