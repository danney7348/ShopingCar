package com.bw.fuxicar.presenter;

import com.bw.fuxicar.model.ProductsModel;
import com.bw.fuxicar.view.ProductView;



public class ProductPresenter implements ProductsModel.OnProductsData {
    private ProductsModel productModel;
    private ProductView productView;

    public ProductPresenter(ProductView productView) {
        this.productView = productView;
        productModel = new ProductsModel();
        productModel.setOnProductsData(this);
    }

    public void requestData(){
        productModel.onProductData();
    }

    @Override
    public void onProductSuccess(String string) {
        productView.onProductSuccess(string);
    }

    @Override
    public void onProductFailure() {
        productView.onProductFailure();
    }

    @Override
    public void onFailure() {
        productView.onFailure();
    }
}
