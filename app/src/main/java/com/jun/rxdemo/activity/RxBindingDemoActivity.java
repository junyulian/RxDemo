package com.jun.rxdemo.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.rxbinding3.appcompat.RxToolbar;
import com.jun.rxdemo.R;
import com.jun.rxdemo.fragment.AutoTextFragment;
import com.jun.rxdemo.fragment.CheckoutboxFragment;
import com.jun.rxdemo.fragment.SimpleClickFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class RxBindingDemoActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.rg_01)
    RadioGroup rg_01;

    @BindView(R.id.rx_toolbar)
    Toolbar rx_toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxbinding);
        ButterKnife.bind(this);
        initToolbar();
        rg_01.setOnCheckedChangeListener(this);
    }

    private void initToolbar() {
        setSupportActionBar(rx_toolbar);

        //显示返回按钮
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        RxToolbar.itemClicks(rx_toolbar)
                .subscribe(new Consumer<MenuItem>() {
                    @Override
                    public void accept(MenuItem menuItem) throws Exception {
                        Snackbar.make(rx_toolbar,"--"+menuItem.getTitle(),Snackbar.LENGTH_SHORT).show();
                    }
                });

        //返回按钮  需设置才显示
        RxToolbar.navigationClicks(rx_toolbar)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        Snackbar.make(rx_toolbar,"--back",Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rx_binding_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        switch (i){
            case R.id.simple_click_button:

                fragmentManager.beginTransaction()
                        .replace(R.id.mLayout, SimpleClickFragment.getInstance())
                        .commit();
                break;

            case R.id.bq_button:
                fragmentManager.beginTransaction()
                        .replace(R.id.mLayout, AutoTextFragment.getInstance())
                        .commit();
                break;
            case R.id.cb_page:
                fragmentManager.beginTransaction()
                        .replace(R.id.mLayout, CheckoutboxFragment.getInstance())
                        .commit();
                break;
        }
    }
}
