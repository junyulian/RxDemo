package com.jun.rxdemo.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jakewharton.rxbinding3.widget.RxTextView;
import com.jakewharton.rxbinding3.widget.TextViewTextChangeEvent;
import com.jun.rxdemo.R;
import com.jun.rxdemo.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class AutoTextFragment extends Fragment {
    private static AutoTextFragment fragment;
    private View rootView;
    private EditText inputEt;
    private ListView autoLV;
    private List<String> list = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    public AutoTextFragment(){

    }

    public static AutoTextFragment getInstance(){
        if(fragment == null){
            fragment = new AutoTextFragment();
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(rootView == null){
            rootView = inflater.inflate(R.layout.fragment_auto_text,container,false);
            inputEt = (EditText)rootView.findViewById(R.id.input_edit);
            autoLV = (ListView)rootView.findViewById(R.id.auto_lv);
        }


        initListener();

        return rootView;
    }

    private void initListener(){
        RxTextView.textChangeEvents(inputEt)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())//textchg事件源是从UI控件发出，所以需要在主线程
                .switchMap(new Function<TextViewTextChangeEvent, ObservableSource<List<String>>>() {//取消之前的操作  以最后一个操作为准 节约资源
                    @Override
                    public ObservableSource<List<String>> apply(TextViewTextChangeEvent textViewTextChangeEvent) throws Exception {
                        return getDataFormServer(textViewTextChangeEvent.getText().toString().trim())
                                .subscribeOn(Schedulers.io());//网络获取数据 在io线程执行
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> strings) throws Exception {
                        if(strings != null && strings.size()>0){
                            initAdapter(strings);
                        }

                    }
                });



    }

    private void initAdapter(List<String> tempList){
        if(adapter == null){
            adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,list);
            autoLV.setAdapter(adapter);
        }
        list.clear();
        list.addAll(tempList);
        adapter.notifyDataSetChanged();
    }

    private Observable getDataFormServer(String chgText){


        return Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(ObservableEmitter<List<String>> emitter) throws Exception {
                SystemClock.sleep(1000);
                List<String> list = new ArrayList<>();
                if(!TextUtils.isEmpty(chgText)){
                    for(int i=0; i<10; i++){
                        list.add("auto_text:"+chgText+i);
                    }
                }
                emitter.onNext(list);
                emitter.onComplete();
            }
        });

    }
}
