package com.bw.fuxicar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.bw.fuxicar.bean.Pro;
import com.bw.fuxicar.presenter.ProductPresenter;
import com.bw.fuxicar.view.ProductView;
import com.bwie.mycartutils.bean.ChildBean;
import com.bwie.mycartutils.bean.GroupBean;
import com.bwie.mycartutils.utils.CartUtils;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProductView {

    private ExpandableListView car_lv;
    private CheckBox cb_select;
    private TextView pay;
    private TextView sumprice;
    private List<GroupBean> gList;
    private List<List<ChildBean>> cList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        ProductPresenter pre = new ProductPresenter(this);
        pre.requestData();
        gList = new ArrayList<>();
        cList = new ArrayList<>();
    }

    private void initView() {
        car_lv = (ExpandableListView) findViewById(R.id.car_rv);
        cb_select = (CheckBox) findViewById(R.id.cb_select);
        pay = (TextView) findViewById(R.id.pay);
        sumprice = (TextView) findViewById(R.id.sumprice);
    }

    @Override
    public void onProductSuccess(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Gson gson = new Gson();
                Pro pro = gson.fromJson(string, Pro.class);
                List<Pro.DataBean> data = pro.getData();
                for (int i = 0; i < data.size(); i++) {
                    String sellerName = data.get(i).getSellerName();
                    gList.add(new GroupBean(sellerName,false));
                    List<Pro.DataBean.ListBean> list = data.get(i).getList();
                    List<ChildBean> clist = new ArrayList<ChildBean>();
                    for (int i1 = 0; i1 < list.size(); i1++) {
                        double bargainPrice = list.get(i1).getBargainPrice();
                        String title = list.get(i1).getTitle();
                        int num = list.get(i1).getNum();
                        String s = list.get(i1).getImages().split("\\|")[0];
                        clist.add(new ChildBean(title,bargainPrice,s,false,num));
                    }
                    cList.add(clist);
                }
                CartUtils.setCartData(MainActivity.this,gList,cList,car_lv,cb_select,pay,sumprice);
            }
        });
    }
    @Override
    public void onProductFailure() {

    }
    @Override
    public void onFailure() {

    }
}
