package com.jun.rxdemo.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jakewharton.rxbinding3.widget.RxCompoundButton;
import com.jun.rxdemo.R;

import io.reactivex.functions.Consumer;

public class CheckoutboxFragment extends Fragment {

    private static CheckoutboxFragment fragment;
    private View rootView;
    private CheckBox checkBox;
    private Button button;

    public CheckoutboxFragment(){

    }

    public static CheckoutboxFragment getInstance(){
        if(fragment == null){
            fragment = new CheckoutboxFragment();
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(rootView == null){
            rootView = inflater.inflate(R.layout.fragment_check_box,container,false);
            checkBox = (CheckBox)rootView.findViewById(R.id.cb_read);
            button = (Button)rootView.findViewById(R.id.btn_register);
        }

        initListener();

        return rootView;
    }

    private void initListener() {

        RxCompoundButton.checkedChanges(checkBox)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        button.setEnabled(aBoolean);
                        button.setBackgroundColor(aBoolean? Color.RED:Color.GRAY);
                    }
                });

    }


}
