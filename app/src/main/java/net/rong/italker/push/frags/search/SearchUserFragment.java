package net.rong.italker.push.frags.search;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;
import net.qiujuer.genius.ui.widget.Loading;
import net.rong.italker.common.app.PresenterFragment;
import net.rong.italker.common.widget.EmptyView;
import net.rong.italker.common.widget.PortraitView;
import net.rong.italker.common.widget.recycler.RecyclerAdapter;
import net.rong.italker.factory.model.card.UserCard;
import net.rong.italker.factory.model.db.User;
import net.rong.italker.factory.presenter.contact.FollowContract;
import net.rong.italker.factory.presenter.contact.FollowPresenter;
import net.rong.italker.factory.presenter.search.SearchContract;
import net.rong.italker.factory.presenter.search.SearchUserPresenter;
import net.rong.italker.push.R;
import net.rong.italker.push.activities.PersonalActivity;
import net.rong.italker.push.activities.SearchActivity;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 搜索人的界面实现
 * A simple {@link Fragment} subclass.
 */
public class SearchUserFragment extends PresenterFragment<SearchContract.Presenter>
implements SearchActivity.SearchFragment, SearchContract.UserView {

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    private RecyclerAdapter<UserCard> mAdapter;
    public SearchUserFragment() {
        // Required empty public constructor
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<UserCard>() {

            @Override
            protected int getItemViewType(int position, UserCard data) {
                //返回cell的布局id
                return R.layout.cell_search_list;
            }

            @Override
            protected ViewHolder<UserCard> onCreateViewHolder(View root, int ViewType) {
                return new SearchUserFragment.ViewHolder(root);
            }
        });

        //初始化占位布局
        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected void initData() {
        super.initData();
        //发起首次搜索
        search("");
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_user;
    }

    @Override
    public void search(String content) {
        //Activity->Fragment->Presenter->net
        mPresenter.search(content);
    }

    @Override
    protected SearchContract.Presenter initPresenter() {
        return new SearchUserPresenter(this);
    }

    @Override
    public void onSearchDone(List<UserCard> userCards) {
        //数据成功的情况下返回数据
        mAdapter.replace(userCards);
        //如果没有数据显示空布局
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount()>0);
    }

    /**
     * 每一个celld的操作
     */
    class ViewHolder extends RecyclerAdapter.ViewHolder<UserCard>
            implements FollowContract.View {
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.im_follow)
        ImageView mFollow;

        private FollowContract.Presenter mPresenter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //当前View和Presenter绑定
            new FollowPresenter(this);
        }

        @Override
        protected void onBind(UserCard userCard) {
            mPortraitView.setup( Glide.with(SearchUserFragment.this), userCard);
            mName.setText(userCard.getName());
            mFollow.setEnabled(!userCard.isFollow());
        }

        @OnClick(R.id.im_portrait)
        void onPortraitClick(){
            PersonalActivity.show(getContext(), mData.getId());
        }

        @OnClick(R.id.im_follow)
        void onFollowClick(){
            mPresenter.follow(mData.getId());
        }

        @Override
        public void onFollowSucceed(UserCard userCard) {
            //更改当前界面状态
            if(mFollow.getDrawable() instanceof LoadingDrawable){
                ((LoadingDrawable) mFollow.getDrawable()).stop();
                //设置为已添加drawable
                mFollow.setImageResource(R.drawable.sel_opt_done_add);
            }
            updateData(userCard);
        }

        @Override
        public void showError(int str) {
            //更改当前界面状态
            if(mFollow.getDrawable() instanceof LoadingDrawable){
                //失败则停止动画，并且显示一个圆圈
                LoadingDrawable drawable = ((LoadingDrawable) mFollow.getDrawable());
                drawable.setProgress(1);
                drawable.stop();
            }
        }

        @Override
        public void showLoading() {
            int minSize = (int) Ui.dipToPx(getResources(),22 );
            int maxSize = (int) Ui.dipToPx(getResources(),30 );
            //初始化一个圆形的动画Drawable
            LoadingDrawable drawable = new LoadingCircleDrawable(minSize, maxSize);
            drawable.setBackgroundColor(0);
            int[] color = new int[]{UiCompat.getColor(getResources(), R.color.white_alpha_208)};
            drawable.setForegroundColor(color);
            //设置drawable
            mFollow.setImageDrawable(drawable);
            drawable.start();
        }

        @Override
        public void setPresenter(FollowContract.Presenter presenter) {
            mPresenter = presenter;
        }
    }
}
