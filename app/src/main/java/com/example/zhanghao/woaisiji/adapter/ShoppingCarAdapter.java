package com.example.zhanghao.woaisiji.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.example.zhanghao.woaisiji.R;
import com.example.zhanghao.woaisiji.activity.PersonalCouponActivity;
import com.example.zhanghao.woaisiji.bean.my.PersonalCouponBean;
import com.example.zhanghao.woaisiji.bean.shoppingcar.ShoppingCarGoodsInfo;
import com.example.zhanghao.woaisiji.bean.shoppingcar.ShoppingCarStoreInfo;
import com.example.network.utils.StringUtils;

import java.util.List;

public class ShoppingCarAdapter extends BaseExpandableListAdapter {

    public List<ShoppingCarStoreInfo> groups;
    private Context context;
    private CheckInterface checkInterface;
    private ModifyCountInterface modifyCountInterface;
    public int flag = 0;
    int count = 0;
    boolean isSilver;
    /**
     * 构造函数
     *
     * @param groups  组元素列表
     * @param context
     */
    public ShoppingCarAdapter(List<ShoppingCarStoreInfo> groups, Context context,boolean isSilver) {
        this.groups = groups;
        this.context = context;
        this.isSilver = isSilver;
    }

    public void setSilver(boolean silver) {
        isSilver = silver;
    }

    public void setCheckInterface(CheckInterface checkInterface) {
        this.checkInterface = checkInterface;
    }

    public void setModifyCountInterface(ModifyCountInterface modifyCountInterface) {
        this.modifyCountInterface = modifyCountInterface;
    }


    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).getGoods().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).getGoods().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        final GroupViewHolder gholder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_shopping_car_commodity, null);
            gholder = new GroupViewHolder(convertView);
            convertView.setTag(gholder);
        } else {
            gholder = (GroupViewHolder) convertView.getTag();
        }
        final ShoppingCarStoreInfo group = (ShoppingCarStoreInfo) getGroup(groupPosition);
        if (gholder.tvSourceName.getTag() == null || !gholder.tvSourceName.getTag().toString().equals(group.getStore_name()+"")) {
            gholder.tvSourceName.setText(group.getStore_name()+"");
            gholder.tvSourceName.setTag(group.getStore_name());
        }
        if (gholder.determineChekbox.getTag()==null || Boolean.valueOf(gholder.determineChekbox.getTag().toString()) != group.isChoosed()) {
            gholder.determineChekbox.setChecked(group.isChoosed());
            gholder.determineChekbox.setTag(group.isChoosed());
        }

        gholder.determineChekbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.setChoosed(((CheckBox) v).isChecked());
                checkInterface.checkGroup(groupPosition, ((CheckBox) v).isChecked());// 暴露组选接口
            }
        });
        gholder.couponContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSilver)
                    return;
                String store_id = group.getStore_id();
                Intent intent = new Intent(ActivityUtils.getTopActivity(), PersonalCouponActivity.class);
                intent.putExtra("store_id",store_id);
                intent.putExtra("selectedIndex",groupPosition);
                ActivityUtils.startActivityForResult(ActivityUtils.getTopActivity(),intent,10);
            }
        });
        PersonalCouponBean couponBean = group.couponBean;
        gholder.couponContainer.setVisibility(isSilver ? View.GONE : View.VISIBLE);
        if (couponBean != null){
            String gold = couponBean.getGold();
            String gold_condition = couponBean.getGold_condition();

            String money_condition = couponBean.getMoney_condition();
            String money = couponBean.getMoney();
            String s = "满" + money_condition + "减" + money;
            gholder.coupon.setText(s);
        }
//        notifyDataSetChanged();
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView, final ViewGroup parent) {

        final ChildViewHolder cholder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_shopping_car_commodity_detail, null);
            cholder = new ChildViewHolder(convertView);
            convertView.setTag(cholder);
        } else {
            cholder = (ChildViewHolder) convertView.getTag();
        }

        final ShoppingCarGoodsInfo goodsInfo = (ShoppingCarGoodsInfo) getChild(groupPosition, childPosition);

        if (goodsInfo != null) {
            if (cholder.tvTitle.getTag() == null || !cholder.tvTitle.getTag().equals(goodsInfo.getTitle())) {
                cholder.tvTitle.setText(goodsInfo.getTitle());
                cholder.tvTitle.setTag(goodsInfo.getTitle());
            }
            if (cholder.tvPrice.getTag() == null || !cholder.tvPrice.getTag().equals("￥" + goodsInfo.getPay_price() )) {
                int pay_price = goodsInfo.getPay_price();
                cholder.tvPrice.setText("￥ " + pay_price + "");
                cholder.tvPrice.setTag("￥ " + pay_price + "");
            }
            if (cholder.etNum.getTag() == null || !cholder.etNum.getTag().equals(goodsInfo.getNum() + "")){
                cholder.etNum.setText(goodsInfo.getNum() + "");
                cholder.etNum.setTag(goodsInfo.getNum() + "");
                cholder.etNum.setFocusable(false);

                cholder.etNum.setFocusableInTouchMode(false);
            }
          if (cholder.ivAdapterListPic.getTag(R.id.img_tag_data) == null || !cholder.ivAdapterListPic.getTag().equals(goodsInfo.getImg()+"")) {
              Glide.with(context).load(goodsInfo.getImg()).placeholder(R.drawable.icon_loading).into(cholder.ivAdapterListPic);
              cholder.ivAdapterListPic.setTag(R.id.img_tag_data,goodsInfo.getImg());
          }


          if (cholder.tvInventory.getTag() == null || !cholder.tvInventory.getTag().equals("库存：" + goodsInfo.getNumber())) {
              cholder.tvInventory.setText("库存：" + goodsInfo.getNumber());
              cholder.tvInventory.setTag("库存：" + goodsInfo.getNumber());
          }
            if (cholder.checkBox.getTag()==null || Boolean.valueOf(cholder.checkBox.getTag().toString()) != goodsInfo.isChoosed()) {
                cholder.checkBox.setChecked(goodsInfo.isChoosed());
                cholder.checkBox.setTag(goodsInfo.isChoosed());
            }

            cholder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goodsInfo.setChoosed(((CheckBox) v).isChecked());
                    cholder.checkBox.setChecked(((CheckBox) v).isChecked());
                    checkInterface.checkChild(groupPosition, childPosition, ((CheckBox) v).isChecked());// 暴露子选接口
                }
            });
            cholder.btAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int num = goodsInfo.getNum();
                    String number = goodsInfo.getNumber();
                    if (++num > Integer.parseInt(StringUtils.defaultStr(number,"0"))){
                        ToastUtils.showShort("库存不足");
                        return;
                    }
                    modifyCountInterface.doIncrease(groupPosition, childPosition, cholder.etNum, cholder.checkBox.isChecked());// 暴露增加接口
                }
            });
            cholder.btReduce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (goodsInfo.getNum() == 1){
                        ToastUtils.showShort("至少要选择一个噢，可以点击右上方进行删除");
                        return;
                    }
                    modifyCountInterface.doDecrease(groupPosition, childPosition, cholder.etNum, cholder.checkBox.isChecked());// 暴露删减接口
                }
            });

            cholder.etNum.addTextChangedListener(new GoodsNumWatcher(goodsInfo));//监听文本输入框的文字变化，并且刷新数据
            notifyDataSetChanged();
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    /**
     * 复选框接口
     */
    public interface CheckInterface {
        /**
         * 组选框状态改变触发的事件
         *
         * @param groupPosition 组元素位置
         * @param isChecked     组元素选中与否
         */
        void checkGroup(int groupPosition, boolean isChecked);

        /**
         * 子选框状态改变时触发的事件
         *
         * @param groupPosition 组元素位置
         * @param childPosition 子元素位置
         * @param isChecked     子元素选中与否
         */
        void checkChild(int groupPosition, int childPosition, boolean isChecked);
    }

    /**
     * 改变数量的接口
     */
    public interface ModifyCountInterface {
        /**
         * 增加操作
         *
         * @param groupPosition 组元素位置
         * @param childPosition 子元素位置
         * @param showCountView 用于展示变化后数量的View
         * @param isChecked     子元素选中与否
         */
        void doIncrease(int groupPosition, int childPosition, View showCountView, boolean isChecked);

        /**
         * 删减操作
         *
         * @param groupPosition 组元素位置
         * @param childPosition 子元素位置
         * @param showCountView 用于展示变化后数量的View
         * @param isChecked     子元素选中与否
         */
        void doDecrease(int groupPosition, int childPosition, View showCountView, boolean isChecked);
    }

    /**
     * 组元素绑定器
     */
    static class GroupViewHolder {
        CheckBox determineChekbox;
        TextView tvSourceName;
        TextView coupon;
        View couponContainer;
        @SuppressLint("WrongViewCast")
        GroupViewHolder(View view) {
            determineChekbox = (CheckBox) view.findViewById(R.id.cb_shopping_car_store_choose_store);
            tvSourceName = (TextView) view.findViewById(R.id.tv_shopping_car_store_name);
            coupon = (TextView) view.findViewById(R.id.coupon);
            couponContainer = view.findViewById(R.id.couponContainer);
        }
    }

    /**
     * 子元素绑定器
     */
    static class ChildViewHolder {
        CheckBox checkBox;
        ImageView ivAdapterListPic;
        TextView tvTitle;
        TextView tvPrice;
        TextView tvInventory;
        TextView btAdd, btReduce;
        EditText etNum;

        ChildViewHolder(View view) {
            checkBox = (CheckBox) view.findViewById(R.id.cb_shopping_car_commodity_detail);
            ivAdapterListPic = (ImageView) view.findViewById(R.id.iv_shopping_car_commodity_detail);

            tvTitle = (TextView) view.findViewById(R.id.tv_shopping_car_commodity_name);
            tvPrice = (TextView) view.findViewById(R.id.tv_shopping_car_commodity_price);
            tvInventory = (TextView) view.findViewById(R.id.tv_shopping_car_commodity_inventory);
            btAdd = (TextView) view.findViewById(R.id.tv_shopping_car_commodity_amount_add);
            btReduce = (TextView) view.findViewById(R.id.tv_shopping_car_commodity_amount_reduce);
            etNum = (EditText) view.findViewById(R.id.et_shopping_car_commodity_number);
        }

    }

    /**
     * 购物车的数量修改编辑框的内容监听
     */
    class GoodsNumWatcher implements TextWatcher {
        ShoppingCarGoodsInfo goodsInfo;

        public GoodsNumWatcher(ShoppingCarGoodsInfo goodsInfo) {
            this.goodsInfo = goodsInfo;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(s.toString())) {//当输入的数字不为空时，更新数字
//                goodsInfo.setNum(Integer.valueOf(s.toString().trim()));
            }
        }
    }

    /**
     * 显示修改购物车商品数量的dialog
     * @param goodinfo  item的商品信息实体
     * @param edittext   购物车item的数量文本框
     */
//    private void showDialog(final RespShoppingCarList.ShoppingCarGoodsInfo goodinfo,final EditText edittext){
//        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
//        View alertDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_change_num, null,false);
//        final AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.setView(alertDialogView);
//        count = goodinfo.getCount();
//        final EditText editText = (EditText) alertDialogView.findViewById(R.id.et_num);
//        editText.setText(""+goodinfo.getCount());//设置dialog的数量初始值
//        //自动弹出软键盘
//        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            public void onShow(DialogInterface dialog) {
//                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(editText, InputMethodManager.RESULT_SHOWN);
//                imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
//            }
//        });
//        final   Button btadd= (Button) alertDialogView.findViewById(R.id.bt_add);
//        final   Button btreduce= (Button) alertDialogView.findViewById(R.id.bt_reduce);
//        final   TextView cancle= (TextView) alertDialogView.findViewById(R.id.tv_cancle);
//        final   TextView sure= (TextView) alertDialogView.findViewById(R.id.tv_sure);
//        cancle.setOnClickListener(new View.OnClickListener() { //取消按钮
//            @Override
//            public void onClick(View v) {
//                alertDialog.dismiss();
//            }
//        });
//        sure.setOnClickListener(new View.OnClickListener() {//确定按钮
//            @Override
//            public void onClick(View v) {
//                goodinfo.setCount(count);//重新设置数量
//                edittext.setText(count+"");//购物车界面的文本框显示同步
//                alertDialog.dismiss();
//            }
//        });
//        btadd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                count ++;   //点一下量加1
//                editText.setText(""+count);//动态显示dialog的文本框的数据
//
//            }
//        });
//        btreduce.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(count>1) {//数量大雨1时操作
//                    count--; //点一下减1
//                    editText.setText("" + count);
//                }
//            }
//        });
//        alertDialog.show();
//    }
}
