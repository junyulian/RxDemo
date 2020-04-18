package com.jun.rxdemo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxAdapterView;
import com.jun.rxdemo.R;
import com.jun.rxdemo.adapter.BindingAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class SimpleClickFragment extends Fragment {

    private static SimpleClickFragment fragment;
    private BindingAdapter mRecyclerAdapter;
    public CompositeDisposable mCompositeDisposable;
    private Unbinder mUnbinder;


    private View rootView;
    private List<String> mList;

    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;


    @BindView(R.id.click_btn)
    Button click_btn;


    private SimpleClickFragment(){

    }

    public static SimpleClickFragment getInstance(){
        if(fragment == null){
            fragment = new SimpleClickFragment();
        }
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //创建模拟数据
        initData();
        mCompositeDisposable = new CompositeDisposable();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(rootView == null){
            rootView = inflater.inflate(R.layout.fragment_simple_click,null);
        }
        mUnbinder = ButterKnife.bind(this,rootView);
        initAdapter();
        initListener();
        return rootView;
    }

    private int i = 0;
    private void initListener() {
        addDisposable(
                RxView.clicks(click_btn)
                        .throttleFirst(500, TimeUnit.MILLISECONDS)//300ms内只处理第一次事件 防重复点击
                        .subscribe(new Consumer<Unit>() {
                            @Override
                            public void accept(Unit unit) throws Exception {
                                Snackbar.make(click_btn,"发送了"+(++i)+"次事件",Snackbar.LENGTH_LONG).show();
                            }
                        })
        );

        addDisposable(
              RxView.longClicks(click_btn)
                    .subscribe(new Consumer<Unit>() {
                        @Override
                        public void accept(Unit unit) throws Exception {
                            Snackbar.make(click_btn,"Long Click",Snackbar.LENGTH_SHORT).show();
                        }
                    })
        );

        //列表点击  只对listview有效


    }

    private void initAdapter(){
        mRecyclerAdapter = new BindingAdapter(mList,getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    private void initData(){
        mList = new ArrayList<>();
        for(int i=0; i<10; i++){
            mList.add("item"+i);
        }
    }


    /**
     * 添加订阅
     */
    public void addDisposable(Disposable mDisposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(mDisposable);
    }

    /**
     * 取消所有订阅
     */
    public void clearDisposable() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        clearDisposable();
        mUnbinder.unbind();
    }
}
