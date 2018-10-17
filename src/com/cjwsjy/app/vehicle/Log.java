package com.cjwsjy.app.vehicle;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.androidquery.util.AQUtility;

/**
 * 封装android的Log类，使用一个总开关来是否打印日志，防止程序发布时因日志而泄漏信息
 *
 * @Copyright: Copyright (c)2013
 * @Company: 广州市道一信息有限公司
 * @author: Mr.y
 * @version: 1.0
 * @date: 2013-10-15 下午3:25:40
 * <p/>
 * Modification History:
 * Date         Author      Version     Description
 * ------------------------------------------------------------------
 * 2013-10-15      Mr.y          1.0       1.0 Version
 */
public final class Log {
    /**
     * 设置是否可以打印log，正式部署时请设置为false，以免打印日志泄漏信息
     */
    private static boolean debug = true;
    private static String TAG;
    public static boolean needServersYN = false;


    public static void setDebug(boolean debug) {
        Log.debug = debug;
        AQUtility.setDebug(debug);

    }

    public static boolean isDebug() {
        return debug;
    }

    public static void v(String msg) {
        if (debug) {
            String s = buildMessage(msg);
            android.util.Log.v(TAG, s);
        }
        sendOnServer(msg);
    }

    public static void v(String msg, Throwable thr) {
        if (debug) {
            String s = buildMessage(msg);
            android.util.Log.v(TAG, s, thr);
        }
        sendOnServer(msg + getStackMsg(thr));
    }

    /**
     * Send a DEBUG log message.
     *
     * @param msg
     */
    public static void d(String msg) {
        if (debug) {
            String s = buildMessage(msg);
            android.util.Log.d(TAG, s);
        }
        sendOnServer(msg);
    }

    public static void d(String msg, Throwable thr) {
        if (debug) {
            String s = buildMessage(msg);
            android.util.Log.d(TAG, s, thr);
        }
        sendOnServer(msg + getStackMsg(thr));
    }

    public static void d(String msg, String thr) {
        if (debug) {
            String s = buildMessage(thr);
            android.util.Log.d(msg, s);
        }
    }

    public static void i(String msg) {
        if (debug) {
            String s = buildMessage(msg);
            android.util.Log.i(TAG, s);
        }
        sendOnServer(msg);
    }

    public static void i(String msg, Throwable thr) {
        if (debug) {
            String s = buildMessage(msg);
            android.util.Log.i(TAG, s, thr);
        }
        sendOnServer(msg + getStackMsg(thr));
    }

    public static void i(String msg, String thr) {
        if (debug) {
            String s = buildMessage(thr);
            android.util.Log.i(msg, s);
        }
        sendOnServer(msg + thr);
    }

    public static void e(String msg) {
        if (debug) {
            String s = buildMessage(msg);
            android.util.Log.e(TAG, s);
        }
        sendOnServer(msg);
    }

    public static void w(String msg) {
        if (debug) {
            String s = buildMessage(msg);
            android.util.Log.w(TAG, s);
        }
        sendOnServer(msg);
    }

    public static void w(String msg, Throwable thr) {
        if (debug) {
            String s = buildMessage(msg);
            android.util.Log.w(TAG, s, thr);
        }
        sendOnServer(msg + getStackMsg(thr));
    }

    public static void w(Throwable thr) {
        if (debug) {
            String s = buildMessage("");
            android.util.Log.w(TAG, s, thr);
        }
        sendOnServer(getStackMsg(thr));
    }

    public static void e(String msg, Throwable thr) {
        if (debug) {
            String s = buildMessage(msg);
            android.util.Log.e(TAG, s, thr);
        }
        sendOnServer(msg + getStackMsg(thr));
    }

    public static void e(String msg, String thr) {
        if (debug) {
            String s = buildMessage(thr);
            android.util.Log.e(msg, s);
        }
        sendOnServer(msg + thr);
    }

    public static void sendOnServer(String content) {
        if (needServersYN&&!content.contains("\"cmdType\":\"LOG_SYN\"")&&!content.contains("\"cmdType\":\"PING\"")) {
            Map map = new ConcurrentHashMap();
            map.put("reportContent", content);
//            Constants.sessionManager.send(ReceiviType.LOG_SYN, BaseActivity.getCmdId(), "i dont need broadcast", map);
        }
    }

    protected static String buildMessage(String msg) {
        StackTraceElement caller = new Throwable().fillInStackTrace()
                .getStackTrace()[2];
        TAG = caller.getClassName();
        int index = TAG.lastIndexOf(".");
        if (index > 0) {
            TAG = TAG.substring(index + 1 < TAG.length() ? index + 1 : TAG.length());
        }
        return String.format("[%s %d]:%s", caller.getMethodName(), caller.getLineNumber(), msg);
    }

    public static String getStackMsg(Throwable ex) {
        try {
            File temp = File.createTempFile("dqdp-android-error", ".temp");
            if (temp == null)
                return "";
            PrintWriter writer = new PrintWriter(temp);
            ex.printStackTrace(writer);
            writer.flush();
            writer.close();
            Log.e(temp.getPath());
            FileReader reader = new FileReader(temp);
            StringBuilder sb = new StringBuilder();
            char[] bf = new char[1024];
            while (reader.read(bf) > -1) {
                sb.append(bf);
            }
            try {
                reader.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
