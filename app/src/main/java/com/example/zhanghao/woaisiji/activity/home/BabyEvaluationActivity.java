package com.example.zhanghao.woaisiji.activity.home;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.zhanghao.woaisiji.R;
import com.example.zhanghao.woaisiji.adapter.BabyEvaluationAdapter;
import com.example.zhanghao.woaisiji.bean.merchandise.MerchandiseDetails;
import com.example.zhanghao.woaisiji.httpurl.Myserver;
import com.example.zhanghao.woaisiji.utils.http.NetManager;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BabyEvaluationActivity extends AppCompatActivity {

    private String mId;
    private RecyclerView mRlv;
    private List<MerchandiseDetails.DataBean> mBeanList;
    private BabyEvaluationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_evaluation);
        initData();
        initView();
        mId = getIntent().getStringExtra("id");
    }

    private void initData() {
        NetManager.getNetManager().getMyService(Myserver.url)
                .getMerchandiseDetails(mId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MerchandiseDetails>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MerchandiseDetails value) {
                        if (value.getData() == null) {
                            return;
                        } else {
                            mBeanList = value.getData();
                            mAdapter.setList(mBeanList);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void initView() {
        mRlv = (RecyclerView) findViewById(R.id.baby_evaluation_rlv);
        mRlv.setLayoutManager(new LinearLayoutManager(BabyEvaluationActivity.this));
        mAdapter = new BabyEvaluationAdapter(BabyEvaluationActivity.this, mBeanList);
        mRlv.setAdapter(mAdapter);
    }
}
