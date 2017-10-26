package com.bw.fuxicar.utils;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static java.lang.String.valueOf;

/**
 * Created by asus on 2017/10/25.
 */

public class Okutils {
    public static final int TIMEOUT=1000*60;
    private static OkHttpClient client;
    public Okutils(){
        client=getInstance();
    }
    public static OkHttpClient getInstance() {
        if (client == null)
        {
            synchronized (Okutils.class)
            {
                if (client == null)
                {
                    client = new OkHttpClient.Builder()
                            .addInterceptor(new Loginterceptor())
                            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(false).build();
                }
            }
        }
        return client;
    }


    public   void getdata(String url,final Backquer backquer){
        Request request=new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                backquer.onfailure(call,e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                backquer.onresponse(call,response);
            }
        });
    }
    public  void  postdata(String url, Map<String,Object> map, final Backquer backquer){
        FormBody.Builder builder=new FormBody.Builder();
        //遍历map
        if(map!=null){
            for (Map.Entry<String,Object> entry : map.entrySet()){
                builder.add(entry.getKey(), entry.getValue().toString());
            }
        }
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                backquer.onfailure(call,e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                backquer.onresponse(call,response);
            }
        });
    }

    public   void updata(Bitmap photo){
        try {
            File file=new File("mnt/sdcard/icon.png");
            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileOutputStream  outputStream = new FileOutputStream(file);
            photo.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            if(file!=null) {
                String filename = file.getName();
                Map<String, Object> params = new HashMap<>();
                params.put("uid", 1);
                OkHttpClient okHttpClient = new OkHttpClient();
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                builder.addFormDataPart("file", filename, requestBody);
                if (params != null) {
                    // map 里面是请求中所需要的 key 和 value
                    for (Map.Entry entry : params.entrySet()) {
                        builder.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
                    }
                }
                Request request = new Request.Builder().url("http://120.27.23.105/file/upload").post(builder.build()).build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                    }
                });
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    static class  Loginterceptor implements Interceptor {

        public String TAG = "LogInterceptor";

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long startTime = System.currentTimeMillis();
            Response response = chain.proceed(chain.request());
            long endTime = System.currentTimeMillis();
            long duration=endTime-startTime;
            MediaType mediaType = response.body().contentType();
            String content = response.body().string();
            Log.d(TAG,"\n");
            Log.d(TAG,"----------Start----------------");
            Log.d(TAG, "| "+request.toString());
            String method=request.method();
            if("POST".equals(method)){
                StringBuilder sb = new StringBuilder();
                if (request.body() instanceof FormBody) {
                    FormBody body = (FormBody) request.body();
                    for (int i = 0; i < body.size(); i++) {
                        sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + ",");
                    }
                    sb.delete(sb.length() - 1, sb.length());
                    Log.d(TAG, "| RequestParams:{"+sb.toString()+"}");
                }
            }
            Log.d(TAG, "| Response:" + content);
            Log.d(TAG,"----------End:"+duration+"毫秒----------");
            return response.newBuilder()
                    .body(ResponseBody.create(mediaType, content))
                    .build();
        }
    }
    public interface  Backquer{
        void  onfailure(Call call, IOException e);
        void  onresponse(Call call, Response response);
    }

}