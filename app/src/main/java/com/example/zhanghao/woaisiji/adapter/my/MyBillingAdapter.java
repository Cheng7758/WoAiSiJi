package com.example.zhanghao.woaisiji.adapter.my;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zhanghao.woaisiji.R;
import com.example.zhanghao.woaisiji.bean.billing.BillingDetailsBean;
import com.example.zhanghao.woaisiji.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cheng on 2019/7/16.
 */

public class MyBillingAdapter extends RecyclerView.Adapter<MyBillingAdapter.ViewHolder> {
    private List<BillingDetailsBean.DataBean> mBeanList = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;

    public MyBillingAdapter(Context pContext) {
        mContext = pContext;
        mInflater = LayoutInflater.from(mContext.getApplicationContext());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.billing_details_show, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.update(mBeanList.get(position));
    }

    @Override
    public int getItemCount() {
        return mBeanList != null ? mBeanList.size() : 0;
    }

    public void addData(List<BillingDetailsBean.DataBean> list) {
        if (list != null && !list.isEmpty()) {
            mBeanList.clear();
            mBeanList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView billing_time;
        private TextView billing_back1;
        private TextView billing_money;

        public ViewHolder(View itemView) {
            super(itemView);
            billing_time = (TextView) itemView.findViewById(R.id.billing_time);
            billing_back1 = (TextView) itemView.findViewById(R.id.billing_back1);
            billing_money = (TextView) itemView.findViewById(R.id.billing_money);
        }

        public void update(BillingDetailsBean.DataBean dataBean) {
            Log.e("---账单查询---", dataBean.toString());
            String timeYMD = DateUtil.getTime_YyyyMmdd(dataBean.getCtime());
            Log.e("---------", timeYMD);
            billing_time.setText(timeYMD);

            billing_back1.setText(dataBean.getBack1());
            if (!dataBean.getSilver().equals("0.00")) {
                billing_money.setText("￥" + dataBean.getSilver());
            } else if (!dataBean.getScore().equals("0.00")) {
                billing_money.setText("￥" + dataBean.getScore());
            } else if (!dataBean.getStore_score().equals("0.00")) {
                billing_money.setText("￥" + dataBean.getStore_score());
            } else if (!dataBean.getBalance().equals("0.00")) {
                billing_money.setText("￥" + dataBean.getBalance());
            }
        }
    }
}
