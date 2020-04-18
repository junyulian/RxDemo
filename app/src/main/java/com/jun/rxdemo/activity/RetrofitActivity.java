package com.jun.rxdemo.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.jun.rxdemo.R;
import com.jun.rxdemo.intercepter.Logintercepter;
import com.jun.rxdemo.utils.LogUtil;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class RetrofitActivity extends RxAppCompatActivity {

    //https://raw.githubusercontent.com/junyulian/LearnDemo/master/jsonData
    public static final String API_URL = "https://raw.githubusercontent.com/";
    //public static final String API_URL = "https://github.com/";
    private Retrofit retrofit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);

        retrofit = new Retrofit.Builder()
                            .baseUrl(API_URL)
                            .client(initOkHttp())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//关联RxJava
                            .addConverterFactory(GsonConverterFactory.create())//转换工厂
                            .build();

        //发送请求，返回数据的Observable
        retrofit.create(GetGithub.class)
                .itemDatas("junyulian", "LearnDemo")
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        
                    }
                })
                .flatMap(new Function<List<String>, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(List<String> strings) throws Exception {
                        return Observable.fromIterable(strings);
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        LogUtil.e("--------s:"+s);
                    }
                });

        /*
        Call<List<String>> call = retrofit.create(GetGithub.class).itemDatas("zhangkekekeke","RxJava_samples");
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                LogUtil.e("=----re:"+response.body());
                Observable.fromIterable(response.body())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                LogUtil.e("---网络结果:"+s);
                            }
                        });
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                    LogUtil.e("----e:"+t.getMessage());
            }
        });
        */
    }

    public interface GetGithub{
        @GET("/{owner}/{txt}/master/jsonData")
        Observable<List<String>> itemDatas(@Path("owner")String owner, @Path("txt")String repo);
         /*
            @GET("/{owner}/{txt}/blob/master/jsondata")
            Call<List<String>> itemDatas(@Path("owner")String owner, @Path("txt")String repo);

             */
    }

    private OkHttpClient initOkHttp() {
        return new OkHttpClient().newBuilder()
                .addInterceptor(new Logintercepter())//添加打印拦截器
                .build();
    }
}



