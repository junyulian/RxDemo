package com.jun.rxdemo.utils;

import com.jun.rxdemo.bean.Person;

import java.util.ArrayList;
import java.util.List;

public class DataUtil {

    //耗时操作
    public static List<Integer> getData(){
        List<Integer>  list = new ArrayList<>();
        for(int i=0; i<10; i++){
            list.add(i);
            try {
                Thread.sleep(500);
                LogUtil.e("获取数据线程名称："+Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static List<Person> getPersonList(){
        List<Person> list = new ArrayList<>();

        Person p1 = new Person("zr",14,"history","dili");
        Person p2 = new Person("qy",18,"life","dream");
        list.add(p1);
        list.add(p2);
        return list;
    }
}
