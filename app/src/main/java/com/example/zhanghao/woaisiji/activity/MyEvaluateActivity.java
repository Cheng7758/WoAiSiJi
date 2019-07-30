package com.example.zhanghao.woaisiji.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zhanghao.woaisiji.R;
import com.example.zhanghao.woaisiji.WoAiSiJiApp;
import com.example.zhanghao.woaisiji.adapter.EvaImageAdapter;
import com.example.zhanghao.woaisiji.bean.my.OrderBean;
import com.example.zhanghao.woaisiji.global.ServerAddress;
import com.example.zhanghao.woaisiji.resp.ImageBean;
import com.google.gson.Gson;
import com.hedgehog.ratingbar.RatingBar;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

//TODO 评价界面
public class MyEvaluateActivity extends TakePhotoActivity {

    private String mId;
    private OrderBean.DataBean bean;

    private ImageView image, add_image;
    private TextView content, money, number;
    private EditText pingjia;
    private RecyclerView image_recy;
    private RatingBar ratingbar2, ratingbar3, ratingbar4;
    private Button submit;
    private TakePhoto takePhoto;
    private List<String> mList = new ArrayList<>();
    private EvaImageAdapter mAdapter;
    //评价
    public final static String URL_MY_PERSONAL_INFO_Evaluate = "http://wasj" +
            ".com/APP/Member/do_evaluate";

    private float mCount2;
    private float mCount3;
    private float mCount4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_evaluate);
        bean = getIntent().getParcelableExtra("bean");
        initView();
    }

    private void initView() {
        image = (ImageView) findViewById(R.id.image);
        add_image = (ImageView) findViewById(R.id.add_image);
        content = (TextView) findViewById(R.id.content);
        money = (TextView) findViewById(R.id.money);
        number = (TextView) findViewById(R.id.number);
        pingjia = (EditText) findViewById(R.id.pingjia);
        image_recy = (RecyclerView) findViewById(R.id.image_recy);

        ratingbar2 = (RatingBar) findViewById(R.id.ratingbar2);
        ratingbar3 = (RatingBar) findViewById(R.id.ratingbar3);
        ratingbar4 = (RatingBar) findViewById(R.id.ratingbar4);
        submit = (Button) findViewById(R.id.submit);

        ratingbar2.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {


            @Override
            public void onRatingChange(float RatingCount) {
                mCount2 = RatingCount;
            }
        });

        ratingbar3.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {


            @Override
            public void onRatingChange(float RatingCount) {
                mCount3 = RatingCount;
            }
        });


        ratingbar4.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {


            @Override
            public void onRatingChange(float RatingCount) {
                mCount4 = RatingCount;
            }
        });

        content.setText(bean.getGoods_name());
        Glide.with(this).load(ServerAddress.SERVER_ROOT + bean.getGoods_img()).into(image);
        money.setText("￥" + bean.getPay_price());
        number.setText("x" + bean.getGoods_num());

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickk(getTakePhoto());
            }
        });
        mAdapter = new EvaImageAdapter(this, mList);
        image_recy.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                false));
        image_recy.setAdapter(mAdapter);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getcouponListServer(bean.getId());

            }
        });

    }


    //优惠券 - 商家
    private void getcouponListServer(String id) {
        RequestParams entity = new RequestParams(URL_MY_PERSONAL_INFO_Evaluate);
        entity.addBodyParameter("uid", WoAiSiJiApp.getUid());
        entity.addBodyParameter("id", id);
        entity.addBodyParameter("token", WoAiSiJiApp.token);

        entity.addBodyParameter("goods_c", mCount2 + "");
        entity.addBodyParameter("wl", mCount3 + "");
        entity.addBodyParameter("service_c", mCount4 + "");
//        try {
//            for (int i = 0; i < mList.size(); i++) {
//                byte[] bytes = readStream(mList.get(i));
//                entity.addBodyParameter("file", bytes + "");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        entity.addBodyParameter("content", content.getText().toString());


        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                if (TextUtils.isEmpty(result))
                    return;
                Gson gson = new Gson();
                ImageBean bean = gson.fromJson(result, ImageBean.class);
                if (bean.getCode() == 200) {
                    Toast.makeText(MyEvaluateActivity.this, bean.getMsg(), Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(MyEvaluateActivity.this, bean.getMsg(), Toast.LENGTH_LONG)
                            .show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    public void back(View view) {
        finish();
    }


    public TakePhoto getTakePhoto() {

        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl
                    (this, this));
        }
        //设置压缩规则，最大500kb
        takePhoto.onEnableCompress(new CompressConfig.Builder().setMaxSize(500 * 1024).create(),
                true);
        return takePhoto;
    }

    public void onClickk(TakePhoto takePhoto) {
        configCompress(takePhoto);
        takePhoto.onPickMultiple(3);
    }


    private void configCompress(TakePhoto takePhoto) {//压缩配置
        int maxSize = Integer.parseInt("409600");//最大 压缩
        int width = Integer.parseInt("800");//宽
        int height = Integer.parseInt("800");//高
        CompressConfig config;
        config = new CompressConfig.Builder().setMaxSize(maxSize)
                .setMaxPixel(width >= height ? width : height)
                .enableReserveRaw(false)//拍照压缩后是否显示原图
                .create();
        takePhoto.onEnableCompress(config, false);//是否显示进度条
    }


    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);

        ArrayList<TImage> images = result.getImages();
        mList.clear();
        for (int i = 0; i < images.size(); i++) {
            mList.add(images.get(i).getCompressPath());
        }

        mAdapter.notifyDataSetChanged();

    }


    /**
     * 照片转byte二进制
     *
     * @param imagepath 需要转byte的照片路径
     * @return 已经转成的byte
     * @throws Exception
     */
    public static byte[] readStream(String imagepath) throws Exception {
        FileInputStream fs = new FileInputStream(imagepath);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while (-1 != (len = fs.read(buffer))) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        fs.close();
        return outStream.toByteArray();
    }


}
