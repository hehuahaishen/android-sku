package com.wuhenzhizao.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Log统一管理类
 */
public class LogUtils
{

    private LogUtils()
    {
		/* cannot be instantiated ：不能被实例化*/
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
    private static final String TAG = "shen";


    /**************************下面四个是默认tag的函数************************************/
    /**************************1个参数************************************/
    public static void i(String msg)
    {
        if (isDebug)
            Log.i(TAG, msg);
    }

    public static void d(String msg)
    {
        if (isDebug)
            Log.d(TAG, msg);
    }

    public static void e(String msg)
    {
        if (isDebug)
            Log.e(TAG, msg);
    }

    public static void v(String msg)
    {
        if (isDebug)
            Log.v(TAG, msg);
    }

    /**************************下面是传入自定义tag的函数************************************/
    /**************************两个参数************************************/
    public static void i(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg)
    {
        if (isDebug)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg)
    {
        if (isDebug)
            Log.e(tag, msg);
    }

    public static void v(String tag, String msg)
    {
        if (isDebug)
            Log.v(tag, msg);
    }


    /**************************1个参数************************************/
    public static void i(String msg, Throwable t) {
        if (isDebug)
            Log.i(TAG, msg + ":\n" + throwableInfo(t));
    }

    public static void d(String msg, Throwable t) {
        if (isDebug)
            Log.d(TAG, msg + ":\n" + throwableInfo(t));
    }

    public static void e(String msg, Throwable t) {
        if (isDebug)
            Log.e(TAG, msg + ":\n" + throwableInfo(t));
    }

    public static void v(String msg, Throwable t) {
        if (isDebug)
            Log.v(TAG, msg + ":\n" + throwableInfo(t));
    }

    /**
     * 打印这个错误出现在哪个地方
     * @param t
     * @return
     */
    public static String throwableInfo(Throwable t){
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        t.printStackTrace(printWriter);
        Throwable cause = t.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.flush();
        printWriter.close();
        String result = writer.toString();

        return result;
    }


    /*------- json --------*/
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static void printLine(String tag, boolean isTop) {
        if (isTop) {
            i(tag, "=\n╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            i(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }

    public static void printJson(String msg){
        printJson("shen", msg, "");
    }

    public static void printJson(String msg, String headString){
        printJson("shen", msg, headString);
    }

    public static void printJson(String tag, String msg, String headString) {

        String message;
        String tempMessage = "";

        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(4);//最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(4);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }

        // printLine(tag, true);

        message = headString + LINE_SEPARATOR + message;
        String[] lines = message.split(LINE_SEPARATOR);

        tempMessage = "=\n╔═══════════════════════════════════════════════════════════════════════════════════════\n";
        for (String line : lines) {
            // i(tag, "║ " + line);
            tempMessage += "║ " + line + "\n";
        }

        tempMessage += "╚═══════════════════════════════════════════════════════════════════════════════════════";
        i(tag, tempMessage);

        // printLine(tag, false);
    }


    public static void printJsonAll(String msg) {

        String message;
        String tempMessage = "";
        String tag = "shen";

        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(4);//最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(4);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }
        printLine(tag, true);
        message = "" + LINE_SEPARATOR + message;
        String[] lines = message.split(LINE_SEPARATOR);
        for (String line : lines) {
            i(tag, "║ " + line);
        }

        printLine(tag, false);
    }
}