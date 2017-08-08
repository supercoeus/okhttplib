package com.rachel.okhttplib.builder;

import com.rachel.okhttplib.HandleCallUtils;
import com.rachel.okhttplib.OkHttpCommonClient;
import com.rachel.okhttplib.callback.BaseCallback;
import com.rachel.okhttplib.request.OkhttpRequestBuilder;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by zhengshaorui on 2017/8/8.
 * 提交表单，比如说头像，名字，等等一起上传
 */

public class PostFromBuilder extends OkhttpRequestBuilder<PostFromBuilder> {
    private static final String TAG = "zsr";
    private static final String MEDIATYPE_STRING = "application/octet-stream";
    private Call mCall;
    private String type;
    private File file;
    private String name; //表达域的key
    private String formname; //表达域的key
    private ConcurrentHashMap<String,String> multiPart;
    public PostFromBuilder(){

    }



    public PostFromBuilder addMedieType(String type, File file) {
        this.type = type;
        this.file = file;
        if (this.file == null){
            try {
                throw  new Exception("file can not be null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.type == null){
            this.type = MEDIATYPE_STRING;
        }
        return this;
    }

    public PostFromBuilder addMultPart(ConcurrentHashMap<String,String> multiPart){
        this.multiPart = multiPart;
        return this;
    }

    public PostFromBuilder(String url, String tag, String type, File file, ConcurrentHashMap<String, String> params,
                           ConcurrentHashMap<String, String> headers) {
        this.url = url;
        this.tag = tag;
        this.params = params;
        this.headers = headers;
        this.type = type;
        this.file = file;


        MultipartBody.Builder multBuilder = new MultipartBody.Builder();
        multBuilder.setType(MultipartBody.FORM);
        if (this.multiPart != null){
            for (Map.Entry<String,String> entry : this.multiPart.entrySet()){
                multBuilder.addFormDataPart(entry.getKey(),entry.getValue());
            }
        }
        multBuilder.addFormDataPart(this.name,formname,FormBody.create(MediaType.parse(this.type),file));

        RequestBody formBody = multBuilder.build();

        Request.Builder builder = new Request.Builder();


        builder.url(url).tag(tag).post(formBody);



        if (this.headers != null && !this.headers.isEmpty()){
            for (Map.Entry<String,String> entry : this.headers.entrySet()){
                builder.addHeader(entry.getKey(),entry.getKey());
            }
        }


        Request request = builder.build();


        mCall = OkHttpCommonClient.getInstance().getOkhttpClient().newCall(request);
    }

    /**
     * 异步方法
     * @param listener
     * @return
     */
    public PostFromBuilder enqueue(final BaseCallback listener){
        if (mCall != null){
            HandleCallUtils.enqueueCallBack(mCall,listener);
        }
        return this;
    }

    /**
     * 同步方法，需要在子线程执行，不然提示错误
     * @param listener
     * @return
     */
    public PostFromBuilder execute(BaseCallback listener){
        if (mCall != null){
            HandleCallUtils.executeCallBack(mCall,listener);
        }
        return this;
    }

    @Override
    public PostFromBuilder builder() {

        return new PostFromBuilder(url,tag,this.type,this.file,params,headers);
    }


}
