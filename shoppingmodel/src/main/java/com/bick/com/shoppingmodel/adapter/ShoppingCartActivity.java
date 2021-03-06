package com.bick.com.shoppingmodel.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bick.com.shoppingmodel.R;
import com.bick.com.shoppingmodel.adapter.adapter.MyExpandableListAdapter;
import com.bick.com.shoppingmodel.adapter.dao.DBHelper;
import com.bick.com.shoppingmodel.adapter.entity.ShoppingCartBean;
import com.bick.com.shoppingmodel.adapter.listener.OnShoppingCartChangeListener;
import com.bick.com.shoppingmodel.adapter.listener.ResponseCallBack;
import com.bick.com.shoppingmodel.adapter.listener.ShoppingCartBiz;
import com.bick.com.shoppingmodel.adapter.listener.ShoppingCartHttpBiz;
import com.bick.com.shoppingmodel.adapter.utils.ToastHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ShoppingCartActivity extends Activity {
    ExpandableListView expandableListView;
    ImageView ivSelectAll;
    TextView btnSettle;
    TextView tvCountMoney;
    TextView tvTitle;
    RelativeLayout rlShoppingCartEmpty;
    RelativeLayout rlBottomBar;

    private List<ShoppingCartBean> mListGoods = new ArrayList<>();
    private MyExpandableListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏 
        setContentView(R.layout.activity_shopping_cart);
        DBHelper.init(getApplicationContext());
        ToastHelper.getInstance().init(getApplicationContext());
        initView();
        setAdapter();
        requestShoppingCartList();

    }


    private void setAdapter() {
        adapter = new MyExpandableListAdapter(this);
        expandableListView.setAdapter(adapter);
        adapter.setOnShoppingCartChangeListener(new OnShoppingCartChangeListener() {
           
            public void onDataChange(String selectCount, String selectMoney) {
                int goodsCount = ShoppingCartBiz.getGoodsCount();
//                if (!isNetworkOk) {//网络状态判断暂时不显示
//                }
                if (goodsCount == 0) {
                    showEmpty(true);
                } else {
                    showEmpty(false);//其实不需要做这个判断，因为没有商品的时候，必须退出去添加商品；
                }
                String countMoney = String.format(getResources().getString(R.string.count_money), selectMoney);
                String countGoods = String.format(getResources().getString(R.string.count_goods), selectCount);
                String title = String.format(getResources().getString(R.string.shop_title), goodsCount + "");
                tvCountMoney.setText(countMoney);
                btnSettle.setText(countGoods);
                tvTitle.setText(title);
            }

            
            public void onSelectItem(boolean isSelectedAll) {
                ShoppingCartBiz.checkItem(isSelectedAll, ivSelectAll);
            }
        });
        //通过监听器关联Activity和Adapter的关系，解耦；
        View.OnClickListener listener = adapter.getAdapterListener();
        if (listener != null) {
            //即使换了一个新的Adapter，也要将“全选事件”传递给adapter处理；
            ivSelectAll.setOnClickListener(adapter.getAdapterListener());
            //结算时，一般是需要将数据传给订单界面的
            btnSettle.setOnClickListener(adapter.getAdapterListener());
        }
        expandableListView.setGroupIndicator(null);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
           
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;
            }
        });
    }
    public void showEmpty(boolean isEmpty) {
        if (isEmpty) {
            expandableListView.setVisibility(View.GONE);
            rlShoppingCartEmpty.setVisibility(View.VISIBLE);
            rlBottomBar.setVisibility(View.GONE);
        } else {
            expandableListView.setVisibility(View.VISIBLE);
            rlShoppingCartEmpty.setVisibility(View.GONE);
            rlBottomBar.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
    	  expandableListView= $(R.id.expandableListView);
          ivSelectAll=    $(R.id.ivSelectAll);
          btnSettle=   $(R.id.btnSettle);
          tvCountMoney=  $(R.id.tvCountMoney);
          tvTitle=   $(R.id.tvTitle);
          rlShoppingCartEmpty=    $(R.id.rlShoppingCartEmpty);
          rlBottomBar=    $(R.id.rlBottomBar);
     
    }

    /** 获取购物车列表的数据（数据和网络请求也是非通用部分） */
    private void requestShoppingCartList() {
        ShoppingCartBiz.delAllGoods();
        testAddGood();
        //使用本地JSON，作测试用。本来应该是将商品ID发送的服务器，服务器返回对应的商品信息；
        ShoppingCartHttpBiz.requestOrderList(this, new ResponseCallBack() {//requestOrderList(list, new VollyHelperNew.ResponseCallBack())
          
         

			public void handleResponse(Object o, int code)
			{
				// TODO Auto-generated method stub
				    mListGoods = ShoppingCartHttpBiz.handleOrderList((JSONObject)o, code);
	                ShoppingCartBiz.updateShopList(mListGoods);
	                updateListView();
			}
        });
    }

    private void updateListView() {
        adapter.setList(mListGoods);
        adapter.notifyDataSetChanged();
        expandAllGroup();
    }

    /**
     * 展开所有组
     */
    private void expandAllGroup() {
        for (int i = 0; i < mListGoods.size(); i++) {
            expandableListView.expandGroup(i);
        }
    }

    /** 测试添加数据 ，添加的动作是通用的，但数据上只是添加ID而已，数据非通用 */
    private void testAddGood() {
        ShoppingCartBiz.addGoodToCart("279457f3-4692-43bf-9676-fa9ab9155c38", "6");
        ShoppingCartBiz.addGoodToCart("95fbe11d-7303-4b9f-8ca4-537d06ce2f8a", "8");
        ShoppingCartBiz.addGoodToCart("8c6e52fb-d57c-45ee-8f05-50905138801b", "9");
        ShoppingCartBiz.addGoodToCart("7d6e52fb-d57c-45ee-8f05-50905138801d", "3");
        ShoppingCartBiz.addGoodToCart("7d6e52fb-d57c-45ee-8f05-50905138801e", "3");
        ShoppingCartBiz.addGoodToCart("7d6e52fb-d57c-45ee-8f05-50905138801f", "3");
        ShoppingCartBiz.addGoodToCart("7d6e52fb-d57c-45ee-8f05-50905138801g", "3");
        ShoppingCartBiz.addGoodToCart("7d6e52fb-d57c-45ee-8f05-50905138801h", "3");
    }
    
    /**
	 * 省去类型转换  将此方法写在基类Activity
	 */
	protected <T extends View> T $(int id)
	{
		return (T) super.findViewById(id);
	}

}
