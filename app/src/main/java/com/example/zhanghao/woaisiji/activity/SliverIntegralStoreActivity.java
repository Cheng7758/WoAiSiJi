package com.example.zhanghao.woaisiji.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.zhanghao.woaisiji.R;
import com.example.zhanghao.woaisiji.WoAiSiJiApp;
import com.example.zhanghao.woaisiji.adapter.SliverIntegralStoreAdapter;
import com.example.zhanghao.woaisiji.friends.ui.BaseActivity;
import com.example.zhanghao.woaisiji.global.ServerAddress;
import com.example.zhanghao.woaisiji.resp.RespMerchantList;
import com.example.zhanghao.woaisiji.view.SiJiWenDaListView;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class SliverIntegralStoreActivity extends BaseActivity {

    private LinearLayout ll_sliver_integral_store_location, ll_sliver_integral_store_province, ll_sliver_integral_store_city, ll_sliver_integral_store_country;
    private ImageView iv_sliver_integral_store_more_condition;
    private TextView tv_sliver_integral_store_province, tv_sliver_integral_store_city, tv_sliver_integral_store_country;

    private SiJiWenDaListView listView;
    private RespMerchantList respMerchantDetail;
    private SliverIntegralStoreAdapter adapter;
    //Title
    private ImageView iv_title_bar_view_left_left;
    private TextView tv_title_bar_view_centre_title;

    private String province, city, district;
    public LocationClient mLocationClient = null;
    public BaiduMapLocationListener myListener = new BaiduMapLocationListener();
    private double mLatitude;
    private double mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliver_integral_store);
        initLocation();
        initView();
        getDataFromServer();
    }

    private void initView() {
        tv_title_bar_view_centre_title = (TextView) findViewById(R.id.tv_title_bar_view_centre_title);
        tv_title_bar_view_centre_title.setText("银积分商城");
        iv_title_bar_view_left_left = (ImageView) findViewById(R.id.iv_title_bar_view_left_left);
        iv_title_bar_view_left_left.setVisibility(View.VISIBLE);

        tv_sliver_integral_store_province = (TextView) findViewById(R.id.tv_sliver_integral_store_province);
        tv_sliver_integral_store_city = (TextView) findViewById(R.id.tv_sliver_integral_store_city);
        tv_sliver_integral_store_country = (TextView) findViewById(R.id.tv_sliver_integral_store_country);

        listView = (SiJiWenDaListView) findViewById(R.id.lv_sliver_integral_store_show_data);
        adapter = new SliverIntegralStoreAdapter(SliverIntegralStoreActivity.this);
        listView.setAdapter(adapter);

        ll_sliver_integral_store_location = (LinearLayout) findViewById(R.id.ll_sliver_integral_store_location);
        ll_sliver_integral_store_province = (LinearLayout) findViewById(R.id.ll_sliver_integral_store_province);
        ll_sliver_integral_store_city = (LinearLayout) findViewById(R.id.ll_sliver_integral_store_city);
        ll_sliver_integral_store_country = (LinearLayout) findViewById(R.id.ll_sliver_integral_store_country);
        iv_sliver_integral_store_more_condition = (ImageView) findViewById(R.id.iv_sliver_integral_store_more_condition);
        initListener();
    }

    private void initListener() {
        ll_sliver_integral_store_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SliverIntegralStoreActivity.this, "定位系统在优化中~", Toast.LENGTH_SHORT).show();
            }
        });
        iv_title_bar_view_left_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initLocation() {
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myListener);

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    private void getDataFromServer() {
        String url = ServerAddress.URL_JOIN_PARTNER_REQUEST_JOIN_LIST;
        StringRequest questionRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                respMerchantDetail = gson.fromJson(response, RespMerchantList.class);
                if (respMerchantDetail.getCode() == 200) {
                    if (respMerchantDetail.getData() != null) {
                        adapter.getDataSource().clear();
                        adapter.getDataSource().addAll(respMerchantDetail.getData());
                        adapter.notifyDataSetChanged();
                    }
                } else if (respMerchantDetail.getCode() == 400) {
                    listView.removeAllViewsInLayout();
                    Toast.makeText(getApplicationContext(), "暂无数据",
                            Toast.LENGTH_SHORT).show();
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
                params.put("pageno", "1");
                params.put("pagesize", "500");
                return params;
            }
        };
        WoAiSiJiApp.mRequestQueue.add(questionRequest);
    }

    public class BaiduMapLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null) {
                return;
            }
            province = location.getProvince();
            if (!TextUtils.isEmpty(province))
                tv_sliver_integral_store_province.setText(province);
            city = location.getCity();
            if (!TextUtils.isEmpty(city))
                tv_sliver_integral_store_city.setText(city);
            district = location.getDistrict();
            if (!TextUtils.isEmpty(district))
                tv_sliver_integral_store_country.setText(district);

            //定位
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();

            adapter.setAltitude(mLongitude);
            adapter.setLatitude(mLatitude);
            adapter.notifyDataSetChanged();
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    public void onDestroy() {
        mLocationClient.stop();
        super.onDestroy();
    }
}
