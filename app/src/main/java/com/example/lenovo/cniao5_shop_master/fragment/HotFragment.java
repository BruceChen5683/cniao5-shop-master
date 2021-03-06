package com.example.lenovo.cniao5_shop_master.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.lenovo.cniao5_shop_master.R;
import com.example.lenovo.cniao5_shop_master.adapter.HotWaresAdapter;
import com.example.lenovo.cniao5_shop_master.adapter.decoration.CardViewtemDecortion;
import com.example.lenovo.cniao5_shop_master.bean.Page;
import com.example.lenovo.cniao5_shop_master.bean.Wares;
import com.example.lenovo.cniao5_shop_master.http.Contants;
import com.example.lenovo.cniao5_shop_master.http.OkHttpHelper;
import com.example.lenovo.cniao5_shop_master.http.SpotsCallBack;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.okhttp.Response;

import java.util.List;


public class HotFragment extends Fragment {

    private OkHttpHelper okHttpHelper = OkHttpHelper.getInstance();
    private int curPage = 1 ;
    private int pageSize = 10;

    private int pageCounts=1;

    @ViewInject(R.id.recyclerview)
    private RecyclerView mRecycleView;

    private HotWaresAdapter mHotWareAdapter;

    private List<Wares> waresDatas;


    @ViewInject(R.id.refresh_view)
    private MaterialRefreshLayout mRefreshLayout;


    private  static final int STATE_NORMAL=0;
    private  static final int STATE_REFREH=1;
    private  static final int STATE_MORE=2;

    private int state=STATE_NORMAL;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hot, container,false);  //zhuyi

        ViewUtils.inject(this,view);

        requestDatas();

        initRefreshView();

        return view;
    }

    public void requestDatas(){

        String url = Contants.API.WARES_HOT+"?curPage="+curPage+"&pageSize="+pageSize;

        okHttpHelper.get(url, new SpotsCallBack<Page<Wares>>(getContext()) {

            @Override
            public void onSuccess(Response response, Page<Wares> waresPage) {
                waresDatas = waresPage.getList();
                curPage = waresPage.getCurrentPage();
                pageCounts = waresPage.getTotalPage();

                showRecyHotData();
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });


    }


    public void showRecyHotData(){

        switch (state){
            case STATE_NORMAL:

                mHotWareAdapter = new HotWaresAdapter(waresDatas,getActivity());
                mRecycleView.setAdapter(mHotWareAdapter);
                mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecycleView.addItemDecoration(new CardViewtemDecortion());
                mRecycleView.setItemAnimator(new DefaultItemAnimator());

                break;

            case STATE_REFREH:

                mHotWareAdapter.clearData();
                mHotWareAdapter.addData(waresDatas);
                mRecycleView.scrollToPosition(0);
                mRefreshLayout.finishRefresh();

                break;

            case STATE_MORE:

                mHotWareAdapter.addData(mHotWareAdapter.getDatas().size(),waresDatas);
                mRecycleView.scrollToPosition(mHotWareAdapter.getDatas().size());
                mRefreshLayout.finishRefreshLoadMore();
                break;
        }



    }


    public void initRefreshView(){

        mRefreshLayout.setLoadMore(true);

        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refreshDatas();
            }


            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {

                if(curPage<pageCounts){
                    loadDatas();

                }else{
                    Toast.makeText(getActivity(),"没有更多数据了",Toast.LENGTH_SHORT).show();
                    mRefreshLayout.finishRefreshLoadMore();
                    mRefreshLayout.setLoadMore(false);
                }


            }
        });

    }

    public void refreshDatas(){

        curPage = 1;
        state = STATE_REFREH;
        requestDatas();

    }

    public void loadDatas(){

        curPage+=1;
        state = STATE_MORE;
        requestDatas();

    }

}
