package com.jun.rxdemo.utils;

import android.util.Log;

import com.jun.rxdemo.BuildConfig;

/**
 * 在调试模式 和 日志级别小于设置级别的 日志可输出
 */
public class LogUtil {

    /** 日志输出级别NONE */
    public static final int LEVEL_NONE = 0;
    /** 日志输出级别E */
    public static final int LEVEL_ERROR =1;
    /** 日志输出级别W */
    public static final int LEVEL_WARN = 2;
    /** 日志输出级别I */
    public static final int LEVEL_INFO = 3;
    /** 日志输出级别D */
    public static final int LEVEL_DEBUG = 4;
    /** 日志输出级别V */
    public static final int LEVEL_VERBOSE = 5;

    /**输出log级别 */
    private static int mDebuggable = LEVEL_VERBOSE;

    private static boolean isDebug = BuildConfig.DEBUG;

    /**
     * 是否可以输出日志
     * 只在调试模式和日志级别合规才充许输出日志
     * @return
     */
    public static boolean isLog(){
        return isDebug && mDebuggable >= LEVEL_VERBOSE;
    }

    public static void e(String TAG, String msg){
        if(isLog()){
            Log.e(TAG,log(msg));
        }
    }

    public static void w(String TAG, String msg){
        if(isLog()){
            Log.w(TAG,log(msg));
        }
    }

    public static void i(String TAG, String msg){
        if(isLog()){
            Log.i(TAG,log(msg));
        }
    }

    public static void d(String TAG, String msg){
        if(isLog()){
            Log.d(TAG,log(msg));
        }
    }

    public static void v(String TAG, String msg){
        if(isLog()){
            Log.v(TAG,log(msg));
        }
    }

    public static void e(String msg){
        e(getClassName(),msg);
    }

    public static void w(String msg){
        w(getClassName(),msg);
    }

    public static void i(String msg){
        i(getClassName(),msg);
    }

    public static void d(String msg){
        d(getClassName(),msg);
    }

    public static void v(String msg){
        v(getClassName(),msg);
    }

    /**
     * 获取当前调用日志方法的类名
     * @return
     */
    private static String getClassName(){
        //0代表本身的方法名  1是直接调用方法的方法名  2是上上个调用方法的方法名
        StackTraceElement thisMethodStack = Thread.currentThread().getStackTrace()[2];
        String className = thisMethodStack.getClassName();
        int lastIndex = className.lastIndexOf(".");
        className = className.substring(lastIndex+1);
        int i = className.indexOf("$");//剔除匿名内部类名
        return i == -1? className : className.substring(0,i);
    }

    /**
     * 打印log 行数位置
     * @param message
     * @return
     */
    private static String log(String message){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement targetElement = null;
        if(!stackTrace[4].getClassName().endsWith("LogUtil")){
            targetElement = stackTrace[4];
        }else{
            targetElement = stackTrace[5];
        }
        String className = targetElement.getClassName();
        className = className.substring(className.lastIndexOf(".")+1)+".java";
        int lineNumber = targetElement.getLineNumber();
        if(lineNumber < 0){
            lineNumber = 0;
        }
        return "("+className+":"+lineNumber+")"+message;
    }

}
