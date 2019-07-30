package com.example.zhanghao.woaisiji.activity.send;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.zhanghao.woaisiji.R;
import com.example.zhanghao.woaisiji.WoAiSiJiApp;
import com.example.zhanghao.woaisiji.bean.SendGold;
import com.example.zhanghao.woaisiji.bean.pay.PaySignBean;
import com.example.zhanghao.woaisiji.friends.ui.ChatActivity;
import com.example.zhanghao.woaisiji.friends.ui.ChatFragment;
import com.example.zhanghao.woaisiji.global.ServerAddress;
import com.example.zhanghao.woaisiji.httpurl.Myserver;
import com.example.zhanghao.woaisiji.utils.http.NetManager;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

//TODO  苹果给安卓转账（银积分）条目变成黄色条目  另外银积分转账变成金积分转账
public class SendSilverActivity extends AppCompatActivity {
    private ImageView registerBack;
    private TextView tvRegisterTitle, tv_touid;
    private Button btn_gold;
    private String uid, userid, num, name;
    private EditText edt;
    private String mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_silver);
        initView();
        btn_gold = (Button) findViewById(R.id.btn_gold);
        edt = (EditText) findViewById(R.id.gold_num);

        edt.addTextChangedListener(new OnTextChangeListener());
        btn_gold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SendSilverActivity.this);
                builder.setTitle("确认付款");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        paySign();  //支付签名
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });
        registerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * EditText输入变化事件监听器
     */
    class OnTextChangeListener implements TextWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            //Editable s  可变字符串  表示输入后的字符串
            String numString = s.toString();
            num = numString;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    //银积分转账接口
    private void goldBuy() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerAddress.URL_GLOD_SEND,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                SendGold sendGold = gson.fromJson(response, SendGold.class);
                if (sendGold.getCode() == 200) {
                    Intent intent = new Intent();
                    Log.d("123", num + uid + userid);
                    intent.setClass(SendSilverActivity.this, ChatActivity.class);
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("goldStr", num + "个银积分");
                    bundle2.putString("uid", uid);
                    bundle2.putString("toChatUsername", userid);
                    bundle2.putString("nameTitle2", uid);
                    bundle2.putString("nameTitle", "银积分转让");
                    intent.putExtra("bundle2", bundle2);

                    setResult(0, intent);
                    SendSilverActivity.this.finish();
                } else {
                    Log.d("商品详情获取服务器废了", "" + response);
                }
//                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", WoAiSiJiApp.getUid());
                params.put("token", WoAiSiJiApp.token);
                params.put("give_uid", userid);
                params.put("price", num);
                params.put("type", 1 + "");
                params.put("sign", mData);
                return params;
            }
        };
        WoAiSiJiApp.mRequestQueue.add(stringRequest);
    }

    private void initView() {
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        userid = intent.getStringExtra("uesr_pid");
        tvRegisterTitle = (TextView) findViewById(R.id.tv_register_title);
        tvRegisterTitle.setText("银积分转让");
        registerBack = (ImageView) findViewById(R.id.iv_register_back);
        name = intent.getStringExtra("name");
        tv_touid = (TextView) findViewById(R.id.tv_touid);
        tv_touid.setText(name);
    }

    //支付签名
    private void paySign() {
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", WoAiSiJiApp.getUid());
        map.put("token", WoAiSiJiApp.token);
        map.put("give_uid", userid);
        map.put("price", num);
        map.put("type", 1 + "");
        NetManager.getNetManager().getMyService(Myserver.url)
                .getPaySignBean(map) //支付签名
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PaySignBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PaySignBean value) {
                        if (value != null) {
                            mData = value.getData();
                            goldBuy();  //转让银积分
                            Log.e("----paySign", mData);
                        } else {
                            return;
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
}
