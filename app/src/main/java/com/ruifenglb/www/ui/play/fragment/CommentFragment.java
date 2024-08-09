package com.ruifenglb.www.ui.play.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.github.StormWyrm.wanandroid.base.exception.ResponseException;
import com.github.StormWyrm.wanandroid.base.net.observer.BaseObserver;
import com.github.StormWyrm.wanandroid.base.sheduler.IoMainScheduler;
import com.ruifenglb.www.R;
import com.ruifenglb.www.bean.BaseResult;
import com.ruifenglb.www.bean.CommentBean;
import com.ruifenglb.www.bean.GetScoreBean;
import com.ruifenglb.www.bean.Page;
import com.ruifenglb.www.bean.StartBean;
import com.ruifenglb.www.bean.VodBean;
import com.ruifenglb.www.netservice.VodService;
import com.ruifenglb.www.ui.login.LoginActivity;
import com.ruifenglb.www.ui.play.CommentDialog;
import com.ruifenglb.www.ui.play.VideoDetailFragment;
import com.ruifenglb.www.utils.AgainstCheatUtil;
import com.ruifenglb.www.utils.MMkvUtils;
import com.ruifenglb.www.utils.Retrofit2Utils;
import com.ruifenglb.www.utils.UserUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.jetbrains.annotations.NotNull;


public class CommentFragment extends Fragment {
    private VodBean vodBean;
    public CommentFragment(VodBean bean) {
        Bundle b = new Bundle();
        b.putParcelable("bean", bean);
        setArguments(b);
    }

    private SmartRefreshLayout refreshLayout ;
    private RecyclerView rvPlayDetail ;
    private VideoDetailFragment.CommentAdapter commentAdapter;

    public void setData(VodBean vodBean){
        this.vodBean = vodBean;
        commentAdapter.getData().clear();
        getCommentList(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_comment, container, false);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        rvPlayDetail = view.findViewById(R.id.rvPlayDetail);
        return view;
    }
    private int curCommentPage = 1;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                curCommentPage++;
                getCommentList(false);
            }
        });
        rvPlayDetail.setLayoutManager(new LinearLayoutManager(getActivity()));

        View mHeader = LayoutInflater.from(getActivity()).inflate(R.layout.item_discuss, null);
        LinearLayout layout= mHeader.findViewById(R.id.iv_play_message);
        TextView message= mHeader.findViewById(R.id.play_message);

        StartBean startBean = MMkvUtils.Companion.Builds().loadStartBean("");
        StartBean.Ads ads = null;
        if (startBean != null && startBean.getAds() != null) {
            ads= startBean.getAds();
        }
        if (ads.getPlay_message() == null || ads.getPlay_message().getStatus() != 1 || ads.getPlay_message().getDescription().isEmpty()) {
            layout.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.VISIBLE);
            message.setText(ads.getPlay_message().getDescription());

        }
        getView().findViewById(R.id.rlComment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserUtils.isLogin()) {

                    new CommentDialog(getContext(), "讨论")
                            .setOnCommentSubmitClickListener( new CommentDialog.OnCommentSubmitClickListener (){

                                @Override
                                public void onCommentSubmit(@NotNull String comment) {
                                    commitComment(comment);
                                }
                            }).show();
                } else {
                    LoginActivity.Companion.start();
                }
            }
        });
        commentAdapter = new VideoDetailFragment.CommentAdapter();
        rvPlayDetail.setAdapter(commentAdapter);
        commentAdapter.addHeaderView(mHeader);

        vodBean = getArguments().getParcelable("bean");
        getCommentList(false);
    }

    public void refreshCommnet(){
        curCommentPage = 1;
        getCommentList(true);
    }

    private void getCommentList(boolean isFresh ){
        VodService vodService = Retrofit2Utils.INSTANCE.createByGson(VodService.class);
        if (AgainstCheatUtil.showWarn(vodService)) {
            return;
        }
        vodService.getCommentList(vodBean.getVod_id(),"1",curCommentPage,10).compose(new IoMainScheduler<>())
                .subscribe(new BaseObserver<BaseResult<Page<CommentBean>>>() {
                    @Override
                    public void onSuccess(BaseResult<Page<CommentBean>> data) {
                        if (curCommentPage == 1) {
                            if (isFresh)
                                commentAdapter.setNewData(data.getData().getList());
                            else
                                commentAdapter.addData(data.getData().getList());
                        }

                        if (curCommentPage > 1) {
                            commentAdapter.addData(data.getData().getList());
                            if (refreshLayout != null) {
                                if (data.getData().getList().isEmpty()) {
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                } else {
                                    refreshLayout.finishLoadMore(true);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull ResponseException e) {
                        if (curCommentPage > 1 && refreshLayout != null) {
                            refreshLayout.finishLoadMore(false);
                        }
                    }


    });
    }

    private long lastCommentTime = 0L;
    private final long MIN_COMMENT_INTERVAL_MS = 3 * 60 * 1000; // 3 minutes in milliseconds

    private void commitComment(String commentContent) {
        if (System.currentTimeMillis() - lastCommentTime < MIN_COMMENT_INTERVAL_MS) {
            ToastUtils.showShort("请勿频繁评论，至少等待3分钟后再次尝试");
            return;
        }

        VodService vodService = Retrofit2Utils.INSTANCE.createByGson(VodService.class);
        if (AgainstCheatUtil.showWarn(vodService)) {
            return;
        }

        lastCommentTime = System.currentTimeMillis(); // 更新最后评论时间

        vodService.comment(commentContent, "1", String.valueOf(vodBean.getVod_id()))
                .compose(new IoMainScheduler<>())
                .subscribe(new BaseObserver<BaseResult<GetScoreBean>>() {
                    @Override
                    public void onSuccess(BaseResult<GetScoreBean> data) {
                        if (data.getData().getScore().equals("0")) {
                            ToastUtils.showShort("评论成功");
                        } else {
                            ToastUtils.showShort("评论成功,获得" + data.getData().getScore() + "积分");
                        }

                        refreshCommnet();
                    }

                    @Override
                    public void onError(@NotNull ResponseException e) {
                        // Handle error here
                    }
                });
    }

}
