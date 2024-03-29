package com.example.zhanghao.woaisiji.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.example.zhanghao.woaisiji.R;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/8/16.
 */
public class PersonalAddressActivity extends Activity implements WheelView.OnWheelItemSelectedListener{
    /**
     * 把全国的省市区的信息以json的格式保存，解析完成后赋值为null
     */
    private JSONObject mJsonObj;
    /**
     * 省的WheelView控件
     */
    private WheelView mProvince;
    /**
     * 市的WheelView控件
     */
    private WheelView mCity;
    /**
     * 区的WheelView控件
     */
    private WheelView mArea;

    /**
     * 所有省
     */
//    private String[] mProvinceDatas;
    private List<String> mProvinceDatas;
    /**
     * key - 省 value - 市s
     */
//    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    private Map<String, List<String>> mCitisDatasMap = new HashMap<>();
    /**
     * key - 市 values - 区s
     */
    private Map<String, List<String>> mAreaDatasMap = new HashMap<>();

    /**
     * 当前省的名称
     */
    private String mCurrentProviceName;
    /**
     * 当前市的名称
     */
    private String mCurrentCityName;
    /**
     * 当前区的名称
     */
    private String mCurrentAreaName ="";

    private WheelView myWheelView01;
    private WheelView myWheelView02;
    private WheelView myWheelView03;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_address);
        initJsonData();
        mProvince = (WheelView) findViewById(R.id.my_wheelview01);
        mCity = (WheelView) findViewById(R.id.my_wheelview02);
        mArea = (WheelView) findViewById(R.id.my_wheelview03);
        initDatas();

//        mProvince.setViewAdapter(new ArrayWheelAdapter<String>(this, mProvinceDatas));
        mProvince.setWheelAdapter(new ArrayWheelAdapter(this));
        mProvince.setWheelData(mProvinceDatas);
        Log.d("aaaaaaaaa",""+mProvinceDatas.get(2));
        mProvince.setSkin(WheelView.Skin.Holo);

        // 添加change事件
//        mProvince.addChangingListener(this);
        // 添加change事件
//        mCity.addChangingListener(this);
        // 添加change事件
//        mArea.addChangingListener(this);

//        mProvince.setVisibleItems(5);
        mProvince.setWheelSize(5);
        mCity.setWheelSize(5);
        mArea.setWheelSize(5);
//        mCity.setVisibleItems(5);
//        mArea.setVisibleItems(5);
        updateCities();
        updateAreas();
    }


    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas()
    {
        int pCurrent = mCity.getSelection();
//        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
//        String[] areas = mAreaDatasMap.get(mCurrentCityName);
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName).get(pCurrent);

        List<String> areas = mAreaDatasMap.get(mCurrentCityName);
        if (areas == null)
        {
//            areas = new String[] { "" };
            areas = new ArrayList<>();
            areas.add("");
        }
//        mArea.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
        mArea.setWheelAdapter(new ArrayWheelAdapter(this));
        mArea.setWheelData(areas);
       /* mCity.join(mArea);
        mCity.joinDatas(mAreaDatasMap);*/
//        mArea.setCurrentItem(0);
        mArea.setSelection(0);
    }
    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities()
    {
        int pCurrent = mProvince.getSelection();
        Log.d("bbbbbbbbbb",""+pCurrent);
        mCurrentProviceName = mProvinceDatas.get(pCurrent);

//        mCurrentProviceName = mProvinceDatas[pCurrent];
//        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        List<String> cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null)
        {
//            cities = new String[] { "" };
            cities = new ArrayList<>();
            cities.add("");
        }
//        mCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
//        mCity.setCurrentItem(0);
        mCity.setWheelAdapter(new ArrayWheelAdapter(this));
        mCity.setWheelData(cities);
        /*mProvince.join(mCity);
        mProvince.joinDatas(mCitisDatasMap);*/
        updateAreas();
    }

    /**
     * 解析整个Json对象，完成后释放Json对象的内存
     */
    private void initDatas()
    {
        try
        {
            JSONArray jsonArray = mJsonObj.getJSONArray("citylist");
            mProvinceDatas = new ArrayList<>();
//            mProvinceDatas = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonP = jsonArray.getJSONObject(i);// 每个省的json对象
                String province = jsonP.getString("p");// 省名字

//                mProvinceDatas[i] = province;
                mProvinceDatas.add(province);

                JSONArray jsonCs = null;
                try
                {
                    /**
                     * Throws JSONException if the mapping doesn't exist or is
                     * not a JSONArray.
                     */
                    jsonCs = jsonP.getJSONArray("c");
                } catch (Exception e1)
                {
                    continue;
                }
//                String[] mCitiesDatas = new String[jsonCs.length()];
                List<String> mCitiesDatas = new ArrayList<>();
                for (int j = 0; j < jsonCs.length(); j++)
                {
                    JSONObject jsonCity = jsonCs.getJSONObject(j);
                    String city = jsonCity.getString("n");// 市名字
//                    mCitiesDatas[j] = city;
                    mCitiesDatas.add(city);
                    JSONArray jsonAreas = null;
                    try
                    {
                        /**
                         * Throws JSONException if the mapping doesn't exist or
                         * is not a JSONArray.
                         */
                        jsonAreas = jsonCity.getJSONArray("a");
                    } catch (Exception e)
                    {
                        continue;
                    }

//                    String[] mAreasDatas = new String[jsonAreas.length()];// 当前市的所有区
                    List<String> mAreasDatas = new ArrayList<>();
                    for (int k = 0; k < jsonAreas.length(); k++)
                    {
                        String area = jsonAreas.getJSONObject(k).getString("s");// 区域的名称
//                        mAreasDatas[k] = area;
                        mAreasDatas.add(area);
                    }
                    for(int in=0;in<mAreasDatas.size();in++){
                        mAreaDatasMap.put(city, Arrays.asList(mAreasDatas.get(in)));
                    }

                  //  mAreaDatasMap.put(city, mAreasDatas);
                }

                for(int index = 0;index<mCitiesDatas.size();index++){
                    mCitisDatasMap.put(province,Arrays.asList(mCitiesDatas.get(index)));
//                    mCitisDatasMap.put(province, mCitiesDatas);
                }

            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        mJsonObj = null;
    }
    private void initJsonData()
    {
        try
        {
            StringBuffer sb = new StringBuffer();
            InputStream is = getAssets().open("city.json");
            int len = -1;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) != -1)
            {
                sb.append(new String(buf, 0, len, "gbk"));
            }
            is.close();
            mJsonObj = new JSONObject(sb.toString());
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(int position, Object o) {

    }
}
