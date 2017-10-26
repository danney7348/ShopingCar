package com.bw.fuxicar.model;

import com.bw.fuxicar.utils.Okutils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;


public class ProductsModel {

    public void onProductData(){
        Okutils okutils = new Okutils();
        okutils.getdata("http://120.27.23.105/product/getCarts?uid=92", new Okutils.Backquer() {
            @Override
            public void onfailure(Call call, IOException e) {
                onProductsData.onFailure();
            }

            @Override
            public void onresponse(Call call, Response response) {

                try {
                    String string = response.body().string();
                    onProductsData.onProductSuccess(string);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private OnProductsData onProductsData;

    public void setOnProductsData(OnProductsData onProductsData) {
        this.onProductsData = onProductsData;
    }

    public interface OnProductsData{
        void onProductSuccess(String string);
        void onProductFailure();
        void onFailure();
    }
}
