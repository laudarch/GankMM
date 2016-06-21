package com.maning.gankmm.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.maning.gankmm.R;
import com.maning.gankmm.ui.adapter.RecycleDayAdapter;
import com.maning.gankmm.ui.base.BaseActivity;
import com.maning.gankmm.bean.DayEntity;
import com.maning.gankmm.bean.GankEntity;
import com.maning.gankmm.http.MyCallBack;
import com.maning.gankmm.http.GankApi;
import com.maning.gankmm.ui.iView.IGankView;
import com.maning.gankmm.ui.presenter.impl.GankPresenterImpl;
import com.maning.gankmm.utils.DensityUtil;
import com.maning.gankmm.utils.IntentUtils;
import com.maning.gankmm.utils.MySnackbar;
import com.maning.gankmm.utils.StatusBarCompat;
import com.maning.gankmm.ui.view.FullyLinearLayoutManager;
import com.maning.gankmm.ui.view.ProgressWheel;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GankActivity extends BaseActivity implements IGankView {


    private static final String TAG = GankActivity.class.getSimpleName();
    @Bind(R.id.iv_top)
    ImageView ivTop;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recycleView)
    RecyclerView myRecycleView;
    @Bind(R.id.progressbar)
    ProgressWheel progressbar;

    private String dayDate;

    private ArrayList<String> images;

    private GankPresenterImpl gankPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_show);
        //设置状态栏的颜色
        StatusBarCompat.setStatusBarColor(GankActivity.this, StatusBarCompat.COLOR_DEFAULT_TRANSLATE);
        ButterKnife.bind(this);

        gankPresenter = new GankPresenterImpl(this, this);

        initIntent();

        initBar();

        initView();

        gankPresenter.getOneDayDatas(dayDate);

    }

    @OnClick(R.id.iv_top)
    void iv_top() {
        if (images != null && images.size() > 0) {
            IntentUtils.startToImageShow(this, images, 0);
        }
    }

    private void initView() {
        FullyLinearLayoutManager linearLayoutManager = new FullyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        myRecycleView.setLayoutManager(linearLayoutManager);
        myRecycleView.setItemAnimator(new DefaultItemAnimator());
        //设置图片的最大高度
        ivTop.setMaxHeight((int) (DensityUtil.getHeight(this) * 0.75));
    }

    private void initAdapter(final List<GankEntity> gankList) {
        RecycleDayAdapter recycleDayAdapter = new RecycleDayAdapter(this, gankList);
        myRecycleView.setAdapter(recycleDayAdapter);
        myRecycleView.setNestedScrollingEnabled(false);
        myRecycleView.setHasFixedSize(true);
        recycleDayAdapter.setOnItemClickLitener(new RecycleDayAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!gankList.get(position).getType().equals("title")) {
                    IntentUtils.startToWebActivity(GankActivity.this, gankList.get(position).getType(), gankList.get(position).getDesc(), gankList.get(position).getUrl());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initIntent() {
        Intent intent = getIntent();
        dayDate = intent.getStringExtra(IntentUtils.DayDate);
    }


    private void initBar() {
        initToolBar(toolbar, dayDate, R.drawable.ic_back);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);          //统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        gankPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void showToast(String msg) {
        MySnackbar.makeSnackBarBlue(toolbar, msg);
    }

    @Override
    public void showImageView(String url) {
        Glide
                .with(this)
                .load(url)
                .fitCenter()
                .into(ivTop);
        //添加到集合
        images = new ArrayList<>();
        images.add(url);
    }

    @Override
    public void setGankList(List<GankEntity> gankList) {
        initAdapter(gankList);
    }

    @Override
    public void setProgressBarVisility(int visility) {
        progressbar.setVisibility(visility);
    }

    @Override
    public void showBaseProgressDialog(String msg) {
        showProgressDialog(msg);
    }

    @Override
    public void hideBaseProgressDialog() {
        dissmissProgressDialog();
    }
}
