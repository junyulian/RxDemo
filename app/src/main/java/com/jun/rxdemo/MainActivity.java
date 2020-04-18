package com.jun.rxdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.jun.rxdemo.activity.ClockActivity;
import com.jun.rxdemo.activity.LeakCanaryActivity;
import com.jun.rxdemo.activity.RetrofitActivity;
import com.jun.rxdemo.activity.RxBindingDemoActivity;
import com.jun.rxdemo.bean.Person;
import com.jun.rxdemo.utils.DataUtil;
import com.jun.rxdemo.utils.LogUtil;

import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Looper myHandlerLooper;

    class MyHandler extends HandlerThread{

        public MyHandler(String name) {
            super(name);
        }

        @Override
        public void run() {
            LogUtil.e("----线程："+Thread.currentThread().getName());
            super.run();
        }
    }

    private Handler handler = new Handler(new Handler.Callback(){

        @Override
        public boolean handleMessage(@NonNull Message message) {
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_01,R.id.btn_02,R.id.btn_03,R.id.btn_04,R.id.btn_05,
            R.id.btn_06,R.id.btn_07,R.id.btn_08,R.id.btn_09,R.id.btn_10,
            R.id.btn_11,R.id.btn_12,R.id.btn_13})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_01:
                test01();
                break;
            case R.id.btn_02:
                test02();
                break;
            case R.id.btn_03:
                test03();
                break;
            case R.id.btn_04:
                test04();
                break;
            case R.id.btn_05:
                test05();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        test05();
//                    }
//                }).start();
                break;
            case R.id.btn_06:
                test06();
                break;
            case R.id.btn_07:
                test07();
                break;
            case R.id.btn_08://RxAndroid测试
                test08();
                break;
            case R.id.btn_09://RxAndroid测试
                test09();
                break;
            case R.id.btn_10://RxBinding
                test10();
                break;
            case R.id.btn_11://Retrofit
                test11();
            case R.id.btn_12://LeakCanary
                test12();
            case R.id.btn_13://时钟
                test13();
                break;

        }
    }

    private void test13() {
        Intent intent = new Intent(this, ClockActivity.class);
        startActivity(intent);
    }

    private void test12() {
        Intent intent = new Intent(this, LeakCanaryActivity.class);
        startActivity(intent);
    }

    private void test11() {
        Intent intent = new Intent(this, RetrofitActivity.class);
        startActivity(intent);
    }

    private void test10() {
        Intent intent = new Intent(this, RxBindingDemoActivity.class);
        startActivity(intent);
    }

    private void test09() {
        MyHandler myHandler = new MyHandler("MainActivity");
        myHandler.start();
        myHandlerLooper = myHandler.getLooper();
        sendData09();
    }

    private void sendData09(){
        initObservable().subscribeOn(AndroidSchedulers.from(myHandlerLooper))//执行在io线程
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        handler.sendEmptyMessage(0);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        StringBuffer buffer = new StringBuffer();
                        buffer.append(s);
                        LogUtil.e("--res:"+buffer.toString());
                        LogUtil.e(s+"--"+Thread.currentThread().getName());
                    }
                });

    }


    //RxAndroid测试
    private void test08() {
        sendData();
    }

    private void sendData(){
        initObservable().subscribeOn(Schedulers.io())//执行在io线程
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        StringBuffer buffer = new StringBuffer();
                        buffer.append(s);
                        LogUtil.e("--res:"+buffer.toString());
                        LogUtil.e(s+"--"+Thread.currentThread().getName());
                    }
                });

    }

    private Observable<String> initObservable() {
        //return Observable.just("one","tow","three","four","five");
        //耗时操作
        return Observable.defer(new Callable<ObservableSource<String>>() {
            @Override
            public ObservableSource<String> call() throws Exception {
                LogUtil.e("--rxandroid thread:"+Thread.currentThread().getName());
                try {
                    Thread.sleep(2000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return Observable.just("one","tow","three","four","five");
            }
        });
    }

    private void test07() {

        Observable.fromIterable(DataUtil.getPersonList())
                .flatMap(new Function<Person, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Person person) throws Exception {
                        LogUtil.e("---person:"+person.getName());
                        LogUtil.e("----203 Thread:"+Thread.currentThread().getName());
                        return Observable.fromIterable(person.getBooks());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>(){
                    @Override
                    public void accept(String s) throws Exception {
                        LogUtil.e("----212 Thread:"+Thread.currentThread().getName());
                        LogUtil.e("---book:"+s);
                    }
                });

    }

    private void test06() {
        Integer[] arry = new Integer[]{1,2,3,4};
        Observable.fromArray(arry)
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return "zr"+integer;
                    }
                }).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        LogUtil.e("---映射："+s);
                    }
        });

    }

    private void test05() {
        LogUtil.e("---方法运行线程："+Thread.currentThread().getName());
        Observable.fromIterable(DataUtil.getData())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        LogUtil.e("--accept:"+Thread.currentThread().getName());
                        LogUtil.e("---接收："+integer);
                    }
                });
    }

    private void test04() {
        String[] strs =  new String[]{"hello","lsq","back"};
        Observable note = Observable.fromArray(strs);

        Observer<String> reader = new Observer<String>() {
            private Disposable d;
            @Override
            public void onSubscribe(Disposable d) {
                LogUtil.e("---onSubscribe");
                this.d = d;
            }

            @Override
            public void onNext(String s) {
                LogUtil.e("---onNext"+s);
                if("章节2".equals(s)){
                    d.dispose();
                }
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e("---onError"+e.getMessage());
            }

            @Override
            public void onComplete() {
                LogUtil.e("---onComplete");
            }
        };
        note.subscribe(reader);

    }

    private void test03() {

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                LogUtil.e("---start1");
                emitter.onNext(123);
                LogUtil.e("---start2");
                Thread.sleep(2000);
                LogUtil.e("---start3");
                emitter.onNext(456);
                LogUtil.e("---start4");
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())//执行在io线程
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        LogUtil.e("-----接受：" + integer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.e("---error:" + throwable.getMessage());
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        LogUtil.e("----------run");
                    }
                });

    }

    private void test02(){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("章-节1");
                //io线程
                LogUtil.e("---thread:"+Thread.currentThread().getName());
                emitter.onNext("章-节2");
                emitter.onNext("章-节3");
                emitter.onComplete();
            }
        }).observeOn(AndroidSchedulers.mainThread())//回调在主线程
          .subscribeOn(Schedulers.io())//执行在io线程
          .subscribe(new Observer<String>() {
              //主线程
              @Override
              public void onSubscribe(Disposable d) {

                  LogUtil.e("---onSubscribe");
              }

              @Override
              public void onNext(String s) {
                  LogUtil.e("--thread:"+Thread.currentThread().getName());
                  LogUtil.e("---onNext"+s);
              }

              @Override
              public void onError(Throwable e) {
                  LogUtil.e("---onError"+e.getMessage());
              }

              @Override
              public void onComplete() {
                  LogUtil.e("---onComplete");
              }
          });


    }

    private void test01(){
        Observable novel = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("章节1");
                emitter.onNext("章节2");
                emitter.onNext("章节3");
                //emitter.onComplete();
            }
        });

        Observer<String> reader = new Observer<String>() {
            private Disposable d;
            @Override
            public void onSubscribe(Disposable d) {
                LogUtil.e("---onSubscribe");
                this.d = d;
            }

            @Override
            public void onNext(String s) {
                LogUtil.e("---onNext"+s);
                if("章节2".equals(s)){
                    d.dispose();
                }
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e("---onError"+e.getMessage());
            }

            @Override
            public void onComplete() {
                LogUtil.e("---onComplete");
            }
        };
        novel.subscribe(reader);
        /*
        novel.subscribe(new Consumer<String>() {
            @Override
            public void accept(String o) throws Exception {
                LogUtil.e("---onNext"+o);
            }
        });

         */
    }

}
