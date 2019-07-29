package com.example.zhanghao.woaisiji.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.zhanghao.woaisiji.R;
import com.example.zhanghao.woaisiji.WoAiSiJiApp;
import com.example.zhanghao.woaisiji.adapter.FBHStoreAdapter;
import com.example.zhanghao.woaisiji.adapter.FBHStoreDataDetailAdapter;
import com.example.zhanghao.woaisiji.bean.fbh.FBHStoreCategory;
import com.example.zhanghao.woaisiji.bean.merchant.MerchantInfoDetailBean;
import com.example.zhanghao.woaisiji.friends.ui.BaseActivity;
import com.example.zhanghao.woaisiji.global.ServerAddress;
import com.example.zhanghao.woaisiji.resp.RespCommodityList;
import com.example.zhanghao.woaisiji.resp.RespFBHStoreCategory;
import com.example.zhanghao.woaisiji.resp.RespMerchantDetail;
import com.example.zhanghao.woaisiji.utils.PrefUtils;
import com.example.zhanghao.woaisiji.view.BaseQuickLoadMoreView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO  商家详情页面商品金额显示0 导致支付的时候显示金额太小 结算那也不显示金额
// 另外这里面定位那 需要跳转到地图导航页面
//左侧应该有个优惠券的列表
public class SliverIntegralStoreDetail extends BaseActivity {
    private ImageView iv_title_bar_view_left_left, iv_sliver_shangcheng_detail_merchant_picture,
            iv_sliver_shangcheng_detail_merchant_phone_number;
    private TextView tv_title_bar_view_centre_title, tv_sliver_shangcheng_detail_merchant_name,
            tv_sliver_shangcheng_detail_merchant_distance, tv_sliver_shangcheng_detail_merchant_position,
            tv_sliver_shangcheng_no_data;
    private EditText edit_sliver_shangcheng_detail_key_query;
    private ImageButton ib_sliver_shangcheng_detail_search_button;

    //分类集合
    private List<FBHStoreCategory> mCategoryListData;
    private ListView lv_sliver_shangcheng_detail_category;
    private FBHStoreAdapter fbhStoreCategoryAdapter;
    //展示详情数据
    private RecyclerView gv_sliver_shangcheng_category_data;
    private RespFBHStoreCategory respFBHStoreCategory;
    private FBHStoreDataDetailAdapter fbhStoreDataDetailAdapter;
    private List<RespCommodityList.CommodityDataDetail> commodityDataDetailList;

    private String intentCommodityId;
    private MerchantInfoDetailBean detailBean;
    private int fromType = 1;//0-FBH   1-银积分
    private int currentPage = 1;
    private String currentCategoryId = "";
    private RespMerchantDetail respMerchantDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sliver_shangcheng_detail);
        initData();
        initView();
    }

    private void initData() {
        mCategoryListData = new ArrayList<>();
        commodityDataDetailList = new ArrayList<>();
        if (getIntent() != null)
            intentCommodityId = getIntent().getStringExtra("IntentSliverDetailCommodityID");
        getDetailDataFromService(); //商家详情
        getCategoryDataFromServer();    //商品分类
    }

    private void getDetailDataFromService() {
        if (!TextUtils.isEmpty(intentCommodityId)) {
            //商家详情
            StringRequest questionRequest = new StringRequest(Request.Method.POST, ServerAddress.URL_COMMODITY_DETAIL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Gson gson = new Gson();
                            try {
                                respMerchantDetail = gson.fromJson(response, RespMerchantDetail.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (respMerchantDetail.getCode() == 200) {
                                detailBean = respMerchantDetail.getData();
                                setViewValue();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id", intentCommodityId);
                    Log.e("------", intentCommodityId);
                    return params;
                }
            };
            WoAiSiJiApp.mRequestQueue.add(questionRequest);
        }
    }

    private void initView() {
        initTitleBar();
        mCategoryListData = new ArrayList<>();
        Log.e("-----category", mCategoryListData.toString());
        commodityDataDetailList = new ArrayList<>();
        //左侧列表
        lv_sliver_shangcheng_detail_category = (ListView) findViewById(R.id.lv_sliver_shangcheng_detail_category);
        //左侧列表适配器
        fbhStoreCategoryAdapter = new FBHStoreAdapter(SliverIntegralStoreDetail.this, mCategoryListData);
        lv_sliver_shangcheng_detail_category.setAdapter(fbhStoreCategoryAdapter);
        //商品列表
        gv_sliver_shangcheng_category_data = (RecyclerView) findViewById(R.id.gv_sliver_shangcheng_category_data);
        gv_sliver_shangcheng_category_data.setLayoutManager(new GridLayoutManager(
                SliverIntegralStoreDetail.this, 2));
        //商品列表适配器
        fbhStoreDataDetailAdapter = new FBHStoreDataDetailAdapter(
                SliverIntegralStoreDetail.this, commodityDataDetailList,fromType);
        fbhStoreDataDetailAdapter.setLoadMoreView(new BaseQuickLoadMoreView());
        gv_sliver_shangcheng_category_data.setAdapter(fbhStoreDataDetailAdapter);
        iv_sliver_shangcheng_detail_merchant_picture = (ImageView) findViewById(R.id.iv_sliver_shangcheng_detail_merchant_picture);
        iv_sliver_shangcheng_detail_merchant_phone_number = (ImageView) findViewById(R.id.iv_sliver_shangcheng_detail_merchant_phone_number);
        tv_sliver_shangcheng_detail_merchant_name = (TextView) findViewById(R.id.tv_sliver_shangcheng_detail_merchant_name);
        tv_sliver_shangcheng_detail_merchant_distance = (TextView) findViewById(R.id.tv_sliver_shangcheng_detail_merchant_distance);
        tv_sliver_shangcheng_detail_merchant_position = (TextView) findViewById(R.id.tv_sliver_shangcheng_detail_merchant_position);
        tv_sliver_shangcheng_no_data = (TextView) findViewById(R.id.tv_sliver_shangcheng_no_data);
        edit_sliver_shangcheng_detail_key_query = (EditText) findViewById(R.id.edit_sliver_shangcheng_detail_key_query);
        ib_sliver_shangcheng_detail_search_button = (ImageButton) findViewById(R.id.ib_sliver_shangcheng_detail_search_button);
        initListener();
    }

    private void initTitleBar() {
        iv_title_bar_view_left_left = (ImageView) findViewById(R.id.iv_title_bar_view_left_left);
        iv_title_bar_view_left_left.setVisibility(View.VISIBLE);
        tv_title_bar_view_centre_title = (TextView) findViewById(R.id.tv_title_bar_view_centre_title);
        tv_title_bar_view_centre_title.setVisibility(View.VISIBLE);
    }

    private void initListener() {
        //添加商品界面
        iv_title_bar_view_left_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        iv_sliver_shangcheng_detail_merchant_phone_number.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(detailBean.getPhone())) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + detailBean.getPhone()));
                    startActivity(intent);
                } else {
                    Toast.makeText(SliverIntegralStoreDetail.this, "该商家没有留电话", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        ib_sliver_shangcheng_detail_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//搜索框的点击
                searchFunction();
            }
        });

        edit_sliver_shangcheng_detail_key_query.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    handled = true;
                    /*隐藏软键盘*/
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                    searchFunction();
                }
                return handled;
            }
        });

        //listView的itme点击事件
        lv_sliver_shangcheng_detail_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                currentPage = 1;
                currentCategoryId = mCategoryListData.get(position).getId();

                getDataDetailFromServer();  //商品列表
                fbhStoreCategoryAdapter.setSelectPos(position);
            }
        });
    }

    private void setViewValue() {
        if (!TextUtils.isEmpty(detailBean.getName())) {
            tv_sliver_shangcheng_detail_merchant_name.setText(detailBean.getName());
            tv_title_bar_view_centre_title.setText(detailBean.getName());
            tv_sliver_shangcheng_detail_merchant_position.setText(detailBean.getName());
        }
        if (!TextUtils.isEmpty(detailBean.getLogo()))
            Picasso.with(this).load(detailBean.getLogo()).error(R.drawable.icon_loading)
                    .into(iv_sliver_shangcheng_detail_merchant_picture);
        if (!TextUtils.isEmpty(detailBean.getJuli())) {
            tv_sliver_shangcheng_detail_merchant_distance.setText("(距离" + detailBean.getJuli() + ")");
        }
    }

    /**
     * 搜索
     */
    private void searchFunction() {
        String keywords = edit_sliver_shangcheng_detail_key_query.getText().toString();
        if (!TextUtils.isEmpty(keywords)) {

        } else {
            Toast.makeText(this, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取商品分类数据
     */
    public void getCategoryDataFromServer() {
        StringBuffer url = new StringBuffer(ServerAddress.URL_COMMODITY_CATEGORY);
        url.append("?sid=" + intentCommodityId);

        StringRequest reqFBHStoreCategory = new StringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (TextUtils.isEmpty(response))
                            return;
                        PrefUtils.setString(SliverIntegralStoreDetail.this, "SliverStoreCategoryCache", response);
                        Gson gson = new Gson();
                        respFBHStoreCategory = gson.fromJson(response, RespFBHStoreCategory.class);
                        //判断code是否等于200
                        if (respFBHStoreCategory.getCode() == 200 && respFBHStoreCategory.getData().size() > 0) {
                            mCategoryListData.clear();
                            mCategoryListData.addAll(respFBHStoreCategory.getData());
                            fbhStoreCategoryAdapter.notifyDataSetChanged();//分类刷新
                            //获取第一种分类
                            currentCategoryId = mCategoryListData.get(0).getId();
                            fbhStoreCategoryAdapter.setSelectPos(0);
                            getDataDetailFromServer();  //商品列表
                        }
                    }
                    //请求网络失败
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String categoryCache = "";
                categoryCache = PrefUtils.getString(SliverIntegralStoreDetail.this,
                        "SliverStoreCategoryCache", "");
                if (TextUtils.isEmpty(categoryCache))
                    return;
                Gson gson = new Gson();
                respFBHStoreCategory = gson.fromJson(categoryCache, RespFBHStoreCategory.class);
                //判断code是否等于200
                if (respFBHStoreCategory.getCode() == 200 && respFBHStoreCategory.getData().size() > 0) {
                    mCategoryListData.clear();
                    mCategoryListData.addAll(respFBHStoreCategory.getData());
                    //listView适配器
                    fbhStoreCategoryAdapter.notifyDataSetChanged();
                }
            }
        });
        //添加网络请求队列
        WoAiSiJiApp.mRequestQueue.add(reqFBHStoreCategory);
    }

    /**
     * 获取商品列表数据
     */
    private void getDataDetailFromServer() {
        StringBuffer url = new StringBuffer(ServerAddress.URL_FBH_COMMODITY_DATA_LIST);
        url.append("?cid=" + currentCategoryId + "&page=" + currentPage + "&type=" + fromType +
                "&store_id=" + intentCommodityId + "&row_num=10");
        String temp = url.toString();
        //没有数据提示
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                fbhStoreDataDetailAdapter.loadMoreComplete();
                if (TextUtils.isEmpty(response)) return;
                Gson gson = new Gson();
                RespCommodityList respCommodityList = gson.fromJson(response, RespCommodityList.class);
                if (respCommodityList.getCode() == 200) {
                    if (respCommodityList.getData() == null) {
                        tv_sliver_shangcheng_no_data.setVisibility(View.VISIBLE);
                        if (currentPage == 1) {
                            fbhStoreDataDetailAdapter.setNewData(new ArrayList<RespCommodityList.CommodityDataDetail>());
                            fbhStoreDataDetailAdapter.loadMoreFail();
                            fbhStoreDataDetailAdapter.loadMoreEnd(true);
                        } else
                            fbhStoreDataDetailAdapter.loadMoreEnd();
                        return;
                    } else {
                        tv_sliver_shangcheng_no_data.setVisibility(View.GONE);
                    }
                    if (currentPage == 1) {
                        fbhStoreDataDetailAdapter.setNewData(respCommodityList.getData());
                    } else {
                        fbhStoreDataDetailAdapter.addData(respCommodityList.getData());
                    }
                } else {
                    Toast.makeText(SliverIntegralStoreDetail.this, "商家还未加商品哦", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SliverIntegralStoreDetail.this, "未获取到服务器商城数据", Toast.LENGTH_LONG).show();
            }
        });
        WoAiSiJiApp.mRequestQueue.add(stringRequest);
    }
}
