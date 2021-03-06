package me.ihainan.bu.app.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.ihainan.bu.app.R;
import me.ihainan.bu.app.models.ForumListGroup;
import me.ihainan.bu.app.models.Session;
import me.ihainan.bu.app.utils.network.BUApi;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 系统设置与全局变量
 */
public class BUApplication extends Application {
    /* Picasso 相关 */
    private static boolean initializedPicasso = false;

    /* 会话相关 */
    public static Session userSession = null;   // 用户会话实例，向服务器发送请求时候需要附上会话字符串
    public static String username, password;    // 用户名，密码，用于重新登陆

    /* 缓存相关 */
    private static ACache cache;
    private static OkHttpClient okHTTPClient;

    public final static String CACHE_USER_INFO = "CACHE_USER_INFO"; // 缓存的用户信息
    public final static String CACHE_POST_INNER_IMAGE = "CACHE_POST_INNER_IMAGE";   // 缓存的帖子内图片
    public final static String CACHE_LATEST_THREAD_FIRST_POST = "CACHE_LATEST_THREAD_FIRST_POST";   // 缓存的主页主题第一个回帖（用户获取背景图）
    public final static String CACHE_FAVORITE_FORUMS = "CACHE_FAVORITE_FORUMS"; // 缓存的收藏论文

    public static final int USER_INFO_CACHE_DAYS = 5;   // 用户信息缓存时间
    public final static int INNER_IMAGE_CACHE_DAYS = 3; // 图片缓存时间

    /**
     * 初始化缓存单例
     *
     * @param context 上下文
     */
    private synchronized static void initCache(Context context) {
        if (cache == null) cache = ACache.get(context);
    }

    /**
     * 获取缓存单例
     *
     * @param context 上下文
     * @return 缓存单例
     */
    public static ACache getCache(Context context) {
        if (cache == null) initCache(context);
        return cache;
    }

    /* 系统配置相关*/
    public final static Boolean IS_GOOGLE_PLAY_EDITION = false; // 是否是 Google Play Store 版本
    public final static int RETRY_LIMIT = 2;    // 重新登录尝试次数
    public final static int MAX_USER_NAME_LENGTH = 15;  // 列表用户名最长显示的长度
    public final static int HOT_TOPIC_THREAD = 30;      // 热门帖子阈值
    public final static int SWIPE_LAYOUT_TRIGGER_DISTANCE = 400;    // 手指在屏幕下拉多少距离会触发下拉刷新

    public final static int LOADING_HOME_PAGE_COUNT = 20;        // 一次最多 Loading 的主页动态数目
    public final static int LOADING_THREADS_COUNT = 10; // 一次最多 Loading 的主题数目
    public final static int LOADING_TIMELINE_COUNT = 10;        // 一次最多 Loading 的动态数目
    public final static int LOADING_SEARCH_RESULT_COUNT = 10;   // 一次最多 Loading 的搜索结果数目
    public final static int LOADING_FOLLOWING_COUNT = 20;       // 一次最多 Loading 的关注用户数目
    public final static int LOADING_FAVORITES_COUNT = 10;       // 一次最多 Loading 的收藏数目
    public final static int LOADING_NOTIFICATION_COUNT = 10;       // 一次最多 Loading 的通知数目
    public static Integer postListLoadingCount = 10;   // 一次最多 Loading 的帖子数目
    public final static String BUGLY_APP_ID = "900058475";  // BUGLY Application ID

    public final static int FETCH_UNREAD_COUNT_PERIOD = 300;       // 获取通知个数的时间间隔
    public final static String IMAGE_URL_PREFIX = "IMAGE_URL_PREFIX"; // 图片 URL 前缀，用于标记某个 URL 是图片

    public enum NETWORK_TYPE {
        IN_SCHOOL,
        OUT_SCHOOL,
    }

    // 系统配置
    public final static String CONF_OUT_HOST = "CONF_OUT_HOST";
    public final static String PREF_USER_NAME = "PREF_USER_NAME";
    public final static String CONF_SESSION_STR = "CONF_SESSION_STR";
    public final static String PREF_PASSWORD = "PREF_PASSWORD";
    public final static String PREF_NETWORK_TYPE = "PREF_NETWORK_TYPE";
    public final static String PREF_SAVE_DATA = "PREF_SAVE_DATA";
    public final static String PREF_DEBUG_MODE = "PREF_DEBUG_MODE";
    public final static String PREF_ENABLE_SPACE_MODE = "PREF_ENABLE_SPACE_MODE";
    public final static String PREF_UPLOAD_DATA = "PREF_UPLOAD_DATA";
    public final static String PREF_ENABLE_NOTIFY = "PREF_ENABLE_NOTIFY";
    public final static String PREF_ENABLE_REPLY_NOTIFY = "PREF_ENABLE_REPLY_NOTIFY";
    public final static String PREF_ENABLE_QUOTE_NOTIFY = "PREF_ENABLE_QUOTE_NOTIFY";
    public final static String PREF_ENABLE_AT_NOTIFY = "PREF_ENABLE_AT_NOTIFY";
    public final static String PREF_ENABLE_FOLLOW_NOTIFY = "PREF_ENABLE_FOLLOW_NOTIFY";
    public final static String PREF_HOME_PAGE_CLICK_EVENT = "PREF_HOME_PAGE_CLICK_EVENT";
    public final static String PREF_ENABLE_ADVANCED_EDITOR = "PREF_ENABLE_ADVANCED_EDITOR";
    public final static String PREF_ENABLE_SILENT_MODE = "PREF_ENABLE_SILENT_MODE";
    public final static String PREF_ENABLE_DISPLAY_DEVICE_INFO = "PREF_ENABLE_DISPLAY_DEVICE_INFO";
    public final static String PREF_POST_LIST_LOADING_COUNT = "PREF_POST_LIST_LOADING_COUNT";

    public final static Integer DEFAULT_FONT_SIZE = 15;
    public final static Integer DEFAULT_TITLE_FONT_SIZE = 16;
    public final static Integer DEFAULT_LINE_SPACING_EXTRA = 8;
    public final static Float DEFAULT_LINE_SPACING_MULTIPLIER = 1.2f;
    public final static String PREF_FONT_SIZE = "PREF_FONT_SIZE";
    public final static String PREF_FONT_TITLE_SIZE = "PREF_FONT_TITLE_SIZE";
    public final static String PREF_LINE_SPACING_EXTRA = "PREF_LINE_SPACING_EXTRA";
    public final static String PREF_LINE_SPACING_MULTIPLIER = "PREF_LINE_SPACING_MULTIPLIER";

    public final static String CACHED_FEEDBACK_EMAIL = "CACHED_FEEDBACK_EMAIL";

    public static Boolean saveDataMode = false; // 是否启动省流量模式
    public static Boolean uploadData = true;    // 是否自动上传数据
    public static NETWORK_TYPE networkType = NETWORK_TYPE.OUT_SCHOOL;   // 外网 / 内网

    public static Boolean enableNotify = true;  // 开启通知
    public static Boolean enableReplyNotify = true; // 开启回复提醒
    public static Boolean enableQuoteNotify = true; // 开启引用提醒
    public static Boolean enableAtNotify = true;    // 开启 @ 提醒
    public static Boolean enableFollowingNotify = false; // 开启关注提醒
    public static Boolean enableSilentMode = false; // 夜间免打扰模式

    public static Integer homePageClickEventType = 0;   // 0 表示进尾楼，1 表示进 1 楼
    public static Boolean enableAdvancedEditor = false; // 是否使用高级编辑器
    public static Boolean enableDisplayDeviceInfo = true;   // 是否显示回帖尾巴
    public static Boolean enableSpaceBetweenCNAndEN = false;    // 是否添加额外空格

    public static Boolean debugMode = true;  // 是否启动 debug 模式

    public static String outHost = null;    // 外网地址

    public static boolean hasUpdateFavor = false;

    public static Integer fontSize = DEFAULT_FONT_SIZE; // 字体大小
    public static Integer titleFontSize = DEFAULT_TITLE_FONT_SIZE; // 标题字体大小
    public static Integer lineSpacingExtra = DEFAULT_LINE_SPACING_EXTRA;    // 额外行间距
    public static Float lineSpacingMultiplier = DEFAULT_LINE_SPACING_MULTIPLIER;    // 额外行间距倍数

    public static String cachedFeedbackEmail = "";

    public static void readConfig(Context context) {
        getConfOutHost(context);
        getCacheUsername(context);
        getCachePassword(context);
        getCacheSession(context);
        getCacheNetworkType(context);
        getCacheDebugMode(context);
        getCacheUploadData(context);
        getCacheSaveDataMode(context);
        getCacheFontSize(context);
        getCacheTitleFontSize(context);
        getCacheLineSpacingExtra(context);
        getCacheLineSpacingMultiplier(context);
        getCacheEnableSpaceBetweenCNAndEN(context);
        getEnableAtNotify(context);
        getEnableReplyNotify(context);
        getEnableQuoteNotify(context);
        getEnableAtNotify(context);
        getEnableFollowNotify(context);
        getHomePageClickEventType(context);
        getEnableAdvancedEditor(context);
        getEnableSilentMode(context);
        getCachedFeedbackEmail(context);
        getPrefEnableDisplayDeviceInfo(context);
        getPostListLoadingCount(context);
    }

    public static boolean isInSchool() {
        return networkType == NETWORK_TYPE.IN_SCHOOL;
    }

    public static Integer getPostListLoadingCount(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        postListLoadingCount = prefs.getInt(PREF_POST_LIST_LOADING_COUNT, 10);
        return postListLoadingCount;
    }

    public static void setPostListLoadingCount(Context context) {
        Log.d(TAG, "setPostListLoadingCount >> " + postListLoadingCount);
        if (postListLoadingCount != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(PREF_POST_LIST_LOADING_COUNT, postListLoadingCount);
            editor.apply();
        }
    }

    public static Boolean getPrefEnableDisplayDeviceInfo(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        enableDisplayDeviceInfo = prefs.getBoolean(PREF_ENABLE_DISPLAY_DEVICE_INFO, true);
        return enableDisplayDeviceInfo;
    }

    public static void setPrefEnableDisplayDeviceInfo(Context context) {
        Log.d(TAG, "setPrefEnableDisplayDeviceInfo >> " + enableDisplayDeviceInfo);
        if (enableDisplayDeviceInfo != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_ENABLE_DISPLAY_DEVICE_INFO, enableDisplayDeviceInfo);
            editor.apply();
        }
    }

    public static String getCachedFeedbackEmail(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        cachedFeedbackEmail = prefs.getString(CACHED_FEEDBACK_EMAIL, "");
        return cachedFeedbackEmail;
    }

    public static void setCachedFeedbackEmail(Context context) {
        Log.d(TAG, "setCachedFeedbackEmail >> " + cachedFeedbackEmail);
        if (cachedFeedbackEmail != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(CACHED_FEEDBACK_EMAIL, cachedFeedbackEmail);
            editor.apply();
        }
    }

    public static Boolean getEnableSilentMode(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        enableSilentMode = prefs.getBoolean(PREF_ENABLE_SILENT_MODE, false);
        return enableSilentMode;
    }

    public static void setEnableSilentMode(Context context) {
        Log.d(TAG, "setEnableSilentMode >> " + enableSilentMode);
        if (enableSilentMode != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_ENABLE_SILENT_MODE, enableSilentMode);
            editor.apply();
        }
    }

    public static Boolean getEnableAdvancedEditor(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        enableAdvancedEditor = prefs.getBoolean(PREF_ENABLE_ADVANCED_EDITOR, false);
        return enableAdvancedEditor;
    }

    public static void setEnableAdvancedEditor(Context context) {
        Log.d(TAG, "setEnableAdvancedEditor >> " + enableAdvancedEditor);
        if (enableAdvancedEditor != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_ENABLE_ADVANCED_EDITOR, enableAdvancedEditor);
            editor.apply();
        }
    }

    public static Integer getHomePageClickEventType(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        homePageClickEventType = prefs.getInt(PREF_HOME_PAGE_CLICK_EVENT, 0);
        return homePageClickEventType;
    }

    public static void setHomePageClickEventType(Context context) {
        Log.d(TAG, "setHomePageClickEventType >> " + homePageClickEventType);
        if (homePageClickEventType != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(PREF_HOME_PAGE_CLICK_EVENT, homePageClickEventType);
            editor.apply();
        }
    }

    public static Boolean getEnableFollowNotify(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        enableFollowingNotify = prefs.getBoolean(PREF_ENABLE_FOLLOW_NOTIFY, false);
        return enableFollowingNotify;
    }

    public static void setEnableFollowNotify(Context context) {
        Log.d(TAG, "setEnableFollowNotify >> " + enableFollowingNotify);
        if (enableFollowingNotify != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_ENABLE_FOLLOW_NOTIFY, enableFollowingNotify);
            editor.apply();
        }
    }

    public static Boolean getEnableAtNotify(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        enableAtNotify = prefs.getBoolean(PREF_ENABLE_AT_NOTIFY, true);
        return enableAtNotify;
    }

    public static void setEnableAtNotify(Context context) {
        Log.d(TAG, "setEnableAtNotify >> " + enableAtNotify);
        if (enableAtNotify != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_ENABLE_AT_NOTIFY, enableAtNotify);
            editor.apply();
        }
    }

    public static Boolean getEnableQuoteNotify(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        enableQuoteNotify = prefs.getBoolean(PREF_ENABLE_QUOTE_NOTIFY, true);
        return enableQuoteNotify;
    }

    public static void setEnableQuoteNotify(Context context) {
        Log.d(TAG, "setEnableReplyNotify >> " + enableQuoteNotify);
        if (enableQuoteNotify != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_ENABLE_QUOTE_NOTIFY, enableQuoteNotify);
            editor.apply();
        }
    }

    public static Boolean getEnableReplyNotify(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        enableReplyNotify = prefs.getBoolean(PREF_ENABLE_REPLY_NOTIFY, true);
        return enableReplyNotify;
    }

    public static void setEnableReplyNotify(Context context) {
        Log.d(TAG, "setEnableReplyNotify >> " + enableReplyNotify);
        if (enableReplyNotify != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_ENABLE_REPLY_NOTIFY, enableReplyNotify);
            editor.apply();
        }
    }

    public static Boolean getEnableNotify(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        enableNotify = prefs.getBoolean(PREF_ENABLE_NOTIFY, true);
        return enableNotify;
    }

    public static void setEnableNotify(Context context) {
        Log.d(TAG, "setEnableNotify >> " + enableNotify);
        if (enableNotify != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_ENABLE_NOTIFY, enableNotify);
            editor.apply();
        }
    }

    public static Integer getCacheTitleFontSize(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        titleFontSize = prefs.getInt(PREF_FONT_TITLE_SIZE, DEFAULT_TITLE_FONT_SIZE);
        return titleFontSize;
    }

    public static void setCacheTitleFontSize(Context context) {
        Log.d(TAG, "setCacheTitleFontSize >> " + titleFontSize);
        if (titleFontSize != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(PREF_FONT_TITLE_SIZE, titleFontSize);
            editor.apply();
        }
    }

    public static Integer getCacheFontSize(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        fontSize = prefs.getInt(PREF_FONT_SIZE, DEFAULT_FONT_SIZE);
        return fontSize;
    }

    public static void setCacheFontSize(Context context) {
        Log.d(TAG, "setCacheFontSize >> " + fontSize);
        if (fontSize != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(PREF_FONT_SIZE, fontSize);
            editor.apply();
        }
    }

    public static Integer getCacheLineSpacingExtra(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        lineSpacingExtra = prefs.getInt(PREF_LINE_SPACING_EXTRA, DEFAULT_LINE_SPACING_EXTRA);
        return lineSpacingExtra;
    }

    public static void setCacheLineSpacingExtra(Context context) {
        Log.d(TAG, "setCacheLineSpacingExtra >> " + lineSpacingExtra);
        if (lineSpacingExtra != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(PREF_LINE_SPACING_EXTRA, lineSpacingExtra);
            editor.apply();
        }
    }

    public static Float getCacheLineSpacingMultiplier(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        lineSpacingMultiplier = prefs.getFloat(PREF_LINE_SPACING_MULTIPLIER, DEFAULT_LINE_SPACING_MULTIPLIER);
        return lineSpacingMultiplier;
    }

    public static void setCacheLineSpacingMultiplier(Context context) {
        Log.d(TAG, "setCacheLineSpacingMultiplier >> " + lineSpacingMultiplier);
        if (lineSpacingMultiplier != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat(PREF_LINE_SPACING_MULTIPLIER, lineSpacingMultiplier);
            editor.apply();
        }
    }

    public static String getConfOutHost(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        outHost = prefs.getString(CONF_OUT_HOST, null);
        return outHost;
    }

    public static void setConfOutHost(Context context) {
        Log.d(TAG, "setConfOutHost >> " + BUApplication.outHost);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CONF_OUT_HOST, BUApplication.outHost == null ? "" : BUApplication.outHost);
        editor.apply();
    }

    public static String getCacheUsername(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        username = prefs.getString(PREF_USER_NAME, null);
        return username;
    }

    public static void setCacheUserName(Context context) {
        Log.d(TAG, "setCacheUserName >> " + BUApplication.username);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_USER_NAME, BUApplication.username == null ? "" : BUApplication.username);
        editor.apply();
    }

    public static Session getCacheSession(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sessionStr = prefs.getString(CONF_SESSION_STR, null);
        if (sessionStr != null) {
            try {
                userSession = BUApi.MAPPER.readValue(sessionStr, Session.class);
            } catch (IOException e) {
                Log.e(TAG, "获取 Session 失败 " + sessionStr, e);
                userSession = null;
            }
        }

        return userSession;
    }

    public static void setCacheSession(Context context) {
        Log.d(TAG, "setCacheSession >> " + userSession);
        if (userSession != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(CONF_SESSION_STR, userSession.toString());
            editor.apply();
        }
    }

    public static String getCachePassword(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        password = prefs.getString(PREF_PASSWORD, null);
        return password;
    }

    public static void setCachePassword(Context context) {
        Log.d(TAG, "setCachePassword >> " + BUApplication.password);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_PASSWORD, BUApplication.password == null ? "" : BUApplication.password);
        editor.apply();
    }

    public static NETWORK_TYPE getCacheNetworkType(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String networkTypeStr = prefs.getString(PREF_NETWORK_TYPE, "");
        networkType = networkTypeStr.equals("") ? NETWORK_TYPE.OUT_SCHOOL : NETWORK_TYPE.valueOf(networkTypeStr);
        return networkType;
    }

    public static void setCacheNetworkType(Context context) {
        Log.d(TAG, "setCacheNetworkType >> " + networkType);
        if (networkType != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREF_NETWORK_TYPE, networkType.toString());
            editor.apply();
        }
    }

    public static Boolean getCacheSaveDataMode(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        saveDataMode = prefs.getBoolean(PREF_SAVE_DATA, false);
        return saveDataMode;
    }

    public static void setCacheSaveDataMode(Context context) {
        Log.d(TAG, "setCacheSaveDataMode >> " + saveDataMode);
        if (saveDataMode != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_SAVE_DATA, saveDataMode);
            editor.apply();
        }
    }

    public static Boolean getCacheEnableSpaceBetweenCNAndEN(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        enableSpaceBetweenCNAndEN = prefs.getBoolean(PREF_ENABLE_SPACE_MODE, false);
        return enableSpaceBetweenCNAndEN;
    }

    public static void setCacheEnableSpaceBetweenCNAndEN(Context context) {
        Log.d(TAG, "setCacheEnableSpaceBetweenCNAndEN >> " + enableSpaceBetweenCNAndEN);
        if (enableSpaceBetweenCNAndEN != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_ENABLE_SPACE_MODE, enableSpaceBetweenCNAndEN);
            editor.apply();
        }
    }

    public static Boolean getCacheDebugMode(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        debugMode = prefs.getBoolean(PREF_DEBUG_MODE, false);
        return debugMode;
    }

    public static void setCacheDebugMode(Context context) {
        Log.d(TAG, "setCacheDebugMode >> " + debugMode);
        if (debugMode != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_DEBUG_MODE, debugMode);
            editor.apply();
        }
    }

    public static Boolean getCacheUploadData(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        uploadData = prefs.getBoolean(PREF_UPLOAD_DATA, true);
        return uploadData;
    }

    public static void setUploadData(Context context) {
        Log.d(TAG, "setUploadData >> " + uploadData);
        if (uploadData != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_UPLOAD_DATA, uploadData);
            editor.apply();
        }
    }

    /* 论坛列表相关 */
    public static List<ForumListGroup> forumListGroupList;
    public final static int MAX_MOST_VISITED = 5;

    public static void makeForumGroupList(Context context) {
        forumListGroupList = new ArrayList<>();
        HashMap<Integer, ForumListGroup.ForumList> forumListHashMap = new HashMap<>();

        // 系统管理区
        List<ForumListGroup.ForumList> forumLists = new ArrayList<>();
        ForumListGroup.ForumList forumList = new ForumListGroup.ForumList("联盟公告板", 3, "file:///android_asset/forumicon/announce.gif");
        forumListHashMap.put(3, forumList);
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("联盟意见箱", 4, "file:///android_asset/forumicon/chest.gif");
        forumListHashMap.put(4, forumList);
        forumList.addSubForum(new ForumListGroup.SubForum("处罚通告", 121));
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("后台管理区", 170, "file:///android_asset/forumicon/zhPg_aW1nNDE=.jpg");
        forumListHashMap.put(170, forumList);
        forumList.addSubForum(new ForumListGroup.SubForum("测试专用板块", 177));
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("新手交流区", 92, "file:///android_asset/forumicon/newbie.gif");
        forumListHashMap.put(192, forumList);
        forumList.addSubForum(new ForumListGroup.SubForum("积分恢复申请", 120));
        forumLists.add(forumList);

        ForumListGroup forumListGroup = new ForumListGroup(forumLists, "系统管理区");
        forumListGroupList.add(forumListGroup);

        // 直通理工区
        forumLists = new ArrayList<>();
        forumList = new ForumListGroup.ForumList("校园求助热线", 108, "file:///android_asset/forumicon/handshake.gif");
        forumListHashMap.put(108, forumList);
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("叫卖场", 59, "file:///android_asset/forumicon/money.gif");
        forumListHashMap.put(59, forumList);
        forumList.addSubForum(new ForumListGroup.SubForum("联盟旺铺", 114));
        forumList.addSubForum(new ForumListGroup.SubForum("团购专区", 145));
        forumList.addSubForum(new ForumListGroup.SubForum("已完成交易记录", 93));
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("前程似锦", 83, "file:///android_asset/forumicon/scroll.gif");
        forumListHashMap.put(83, forumList);
        forumList.addSubForum(new ForumListGroup.SubForum("考研兄弟连", 117));
        forumList.addSubForum(new ForumListGroup.SubForum("兼职信息", 153));
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("海外BITer", 150, "file:///android_asset/forumicon/graduation.gif");
        forumListHashMap.put(150, forumList);
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("外语园地", 89, "file:///android_asset/forumicon/locale.gif");
        forumListHashMap.put(89, forumList);
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("知识海报", 151, "file:///android_asset/forumicon/93.gif");
        forumListHashMap.put(151, forumList);
        forumLists.add(forumList);

        forumListGroup = new ForumListGroup(forumLists, "直通理工区");
        forumListGroupList.add(forumListGroup);

        // 时尚生活区
        forumLists = new ArrayList<>();

        forumList = new ForumListGroup.ForumList("购前咨询", 167, "file:///android_asset/forumicon/CQMr_aGVscDE=.jpg");
        forumListHashMap.put(167, forumList);
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("硬件与数码时尚", 80, "file:///android_asset/forumicon/hwinfo.gif");
        forumListHashMap.put(80, forumList);
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("驴游四海", 168, "file:///android_asset/forumicon/tnT6_MTMxMDEy.jpg");
        forumListHashMap.put(168, forumList);
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("摄影与艺术", 116, "file:///android_asset/forumicon/cam.gif");
        forumListHashMap.put(116, forumList);
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("车行天下", 140, "file:///android_asset/forumicon/tzQr_Mjk3NTk3M18xNDQwMzcwNzFfMg==.jpg");
        forumListHashMap.put(140, forumList);
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("生活会馆", 96, "file:///android_asset/forumicon/cookie.gif");
        forumListHashMap.put(96, forumList);
        forumLists.add(forumList);

        forumListGroup = new ForumListGroup(forumLists, "时尚生活区");
        forumListGroupList.add(forumListGroup);

        // 技术讨论区
        forumLists = new ArrayList<>();
        forumList = new ForumListGroup.ForumList("网络技术与信息", 10, "file:///android_asset/forumicon/browser.gif");
        forumListHashMap.put(10, forumList);
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("GNU/Linux 交流区", 84, "file:///android_asset/forumicon/linux.gif");
        forumListHashMap.put(84, forumList);
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("嵌入式开发技术", 101, "file:///android_asset/forumicon/embedded.png");
        forumListHashMap.put(101, forumList);
        forumList.addSubForum(new ForumListGroup.SubForum("嵌入式 LiNUX 开发", 113));
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("程序员集中营", 32, "file:///android_asset/forumicon/text_color.gif");
        forumListHashMap.put(32, forumList);
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("软件使用与交流", 21, "file:///android_asset/forumicon/software.gif");
        forumListHashMap.put(21, forumList);
        forumList.addSubForum(new ForumListGroup.SubForum("新软交流区", 107));
        forumLists.add(forumList);

        forumListGroup = new ForumListGroup(forumLists, "技术讨论区");
        forumListGroupList.add(forumListGroup);

        // 苦中作乐区
        forumLists = new ArrayList<>();

        forumList = new ForumListGroup.ForumList("五环世界", 179, "file:///android_asset/forumicon/olympic.png");
        forumListHashMap.put(14, forumList);
        forumList.addSubForum(new ForumListGroup.SubForum("激情伦敦 (历史版面)", 152));
        forumList.addSubForum(new ForumListGroup.SubForum("聚焦北京 (历史版面)", 138));
        forumList.addSubForum(new ForumListGroup.SubForum("烽火雅典 (历史版面)", 103));
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("游戏人生", 22, "file:///android_asset/forumicon/game.gif");
        forumListHashMap.put(22, forumList);
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("影视天地", 23, "file:///android_asset/forumicon/movie.gif");
        forumListHashMap.put(23, forumList);
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("音乐殿堂", 25, "file:///android_asset/forumicon/music.gif");
        forumListHashMap.put(25, forumList);
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("灌水乐园", 14, "file:///android_asset/forumicon/water.gif");
        forumListHashMap.put(14, forumList);
        forumList.addSubForum(new ForumListGroup.SubForum("站庆专版", 65));
        forumList.addSubForum(new ForumListGroup.SubForum("导师风采", 175));
        forumList.addSubForum(new ForumListGroup.SubForum("个人展示区", 106));
        forumList.addSubForum(new ForumListGroup.SubForum("教工之家", 122));
        forumList.addSubForum(new ForumListGroup.SubForum("原创文学", 66));
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("贴图欣赏", 24, "file:///android_asset/forumicon/image.gif");
        forumListHashMap.put(24, forumList);
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("动漫天空", 27, "file:///android_asset/forumicon/mascot.gif");
        forumListHashMap.put(27, forumList);
        forumList.addSubForum(new ForumListGroup.SubForum("动漫美图", 63));
        forumList.addSubForum(new ForumListGroup.SubForum("日语学习交流版", 110));
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("体坛风云", 115, "file:///android_asset/forumicon/run.gif");
        forumListHashMap.put(115, forumList);
        forumList.addSubForum(new ForumListGroup.SubForum("舞动桑巴", 102));
        forumList.addSubForum(new ForumListGroup.SubForum("菠菜组内部版面", 143));
        forumLists.add(forumList);

        forumList = new ForumListGroup.ForumList("职场生涯", 124, "file:///android_asset/forumicon/businessmen.gif");
        forumListHashMap.put(124, forumList);
        forumLists.add(forumList);

        forumListGroup = new ForumListGroup(forumLists, "苦中作乐区");
        forumListGroupList.add(forumListGroup);

        // 联盟交流区
        forumLists = new ArrayList<>();

        forumList = new ForumListGroup.ForumList("资源分享区", 171, "file:///android_asset/forumicon/Dbfr_z8LU2DE=.jpg");
        forumListHashMap.put(171, forumList);
        forumList.addSubForum(new ForumListGroup.SubForum("联盟 FTP 专版", 79));
        forumList.addSubForum(new ForumListGroup.SubForum("分享专版", 174));
        forumList.addSubForum(new ForumListGroup.SubForum("索档专版", 15));
        forumList.addSubForum(new ForumListGroup.SubForum("新手 FTP 交流", 78));
        forumList.addSubForum(new ForumListGroup.SubForum("D2R小组内部版面", 112));
        forumList.addSubForum(new ForumListGroup.SubForum("联盟字幕组内部版面", 133));
        forumLists.add(forumList);

        forumListGroup = new ForumListGroup(forumLists, "联盟交流区");
        forumListGroupList.add(forumListGroup);
        for (ForumListGroup group : forumListGroupList) {
            for (ForumListGroup.ForumList forums : group.getChildItemList()) {
                if (forums.getChildItemList().size() > 0) {
                    forums.addSubForum(new ForumListGroup.SubForum("主板块", forums.getForumId()), 0);
                }
            }
        }

        // 收藏板块
        forumLists = new ArrayList<>();
        Map<Long, Boolean> favoriteForumsMap = (Map<Long, Boolean>) getCache(context).getAsObject(CACHE_FAVORITE_FORUMS);
        if (favoriteForumsMap == null || favoriteForumsMap.size() == 0) {
            favoriteForumsMap = new HashMap<>();
            favoriteForumsMap.put(3L, true);
            favoriteForumsMap.put(59L, true);
            favoriteForumsMap.put(14L, true);
        }

        Set<Long> favoriteForumIndex = favoriteForumsMap.keySet();

        for (Long index : favoriteForumIndex) {
            if (forumListHashMap.get(index.intValue()) != null) {
                forumList = forumListHashMap.get(index.intValue());
                forumLists.add(forumList);
            }
        }

        forumListGroup = new ForumListGroup(forumLists, "收藏板块");
        forumListGroupList.add(0, forumListGroup);
    }

    /**
     * 寻找一个子板块对应的主板块
     *
     * @param subForumID 子板块 ID
     * @return 子版块归属的主板块 ID
     */
    public static Long findMainForumID(Long subForumID) {
        for (ForumListGroup forumListGroup : BUApplication.forumListGroupList) {
            for (ForumListGroup.ForumList forumList : forumListGroup.getChildItemList()) {
                if (forumList.getForumId().equals(subForumID))
                    return subForumID;
                for (ForumListGroup.SubForum subForum : forumList.getChildItemList()) {
                    if (subForum.getSubForumId().equals(subForumID)) {
                        return forumList.getForumId();
                    }
                }
            }
        }

        return -1L;
    }

    /**
     * 根据板块 ID 获取得到板块名
     *
     * @param fid 板块 ID
     * @return 板块名
     */
    public static String findForumName(Long fid) {
        for (ForumListGroup forumListGroup : BUApplication.forumListGroupList) {
            for (ForumListGroup.ForumList forumList : forumListGroup.getChildItemList()) {
                if (forumList.getForumId().equals(fid)) {
                    return forumList.getForumName();
                } else {
                    for (ForumListGroup.SubForum subForum : forumList.getChildItemList()) {
                        if (subForum.getSubForumId().equals(fid)) {
                            return subForum.getSubForumName();
                        }
                    }
                }
            }
        }

        return "未知板块";
    }

    /**
     * 添加或者删除板块收藏
     *
     * @param context     上下文
     * @param mainForumId 主版块 ID
     * @param isAdd       是否是添加收藏
     */
    public static void addOrRemoveForumFavor(Context context, Long mainForumId, boolean isAdd) {
        HashMap<Long, Boolean> favoriteForumsMap = (HashMap<Long, Boolean>) getCache(context).getAsObject(BUApplication.CACHE_FAVORITE_FORUMS);
        if (favoriteForumsMap == null || favoriteForumsMap.size() == 0) {
            favoriteForumsMap = new HashMap<>();
        }

        if (isAdd) {
            favoriteForumsMap.put(mainForumId, true);
        } else {
            if (favoriteForumsMap.containsKey(mainForumId)) {
                favoriteForumsMap.remove(mainForumId);
            }
        }

        getCache(context).put(BUApplication.CACHE_FAVORITE_FORUMS, favoriteForumsMap);
    }

    /**
     * 获取板块收藏状态
     *
     * @param context     上下文
     * @param mainForumId 主版块 ID
     * @return 板块是否已经被收藏
     */
    public static boolean getForumFavorStatus(Context context, Long mainForumId) {
        HashMap<Long, Boolean> favoriteForumsMap = (HashMap<Long, Boolean>) getCache(context).getAsObject(BUApplication.CACHE_FAVORITE_FORUMS);
        return (favoriteForumsMap != null
                && favoriteForumsMap.size() != 0)
                && favoriteForumsMap.containsKey(mainForumId);
    }

    public final static Map<String, Boolean> badImages = new HashMap<>();

    /* 小米推送 */
    public static final String APP_ID = "2882303761517451788";
    public static final String APP_KEY = "5871745172788";
    public static final String TAG = "me.ihainan.bu.app";

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 配置 Picasso 缓存
     *
     * @param context 上细纹
     */
    public static void setupPicasso(final Context context) {
        if (initializedPicasso) {
            Log.w(TAG, "Picasso was already set");
            return;
        }

        if (context == null) {
            Log.w(TAG, "context is null");
            return;
        }

        File cacheDir = new File(context.getCacheDir(), "picasso-cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        long httpCacheSize = 50 * 1024 * 1024; // 50 MiB
        Cache cache = new Cache(cacheDir, httpCacheSize);
        okHTTPClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("referer", BUApi.getBaseURL())
                                .build();
                        return chain.proceed(newRequest);
                    }
                }).cache(cache).build();
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttp3Downloader(okHTTPClient));
        builder.listener(new Picasso.Listener() {

            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                Log.e(TAG, "failed to load image from " + uri, exception);
            }
        });

        Picasso built = builder.build();
        if (debugMode) {
            built.setIndicatorsEnabled(true);
            built.setLoggingEnabled(true);
        }

        Picasso.setSingletonInstance(built);
        initializedPicasso = true;
    }

    /**
     * 获取 Picasso 磁盘缓存
     *
     * @return 如果 OKHttpClient 尚未初始化，返回 null，否则返回磁盘缓存
     */
    public static Cache getPicassoCache() {
        if (okHTTPClient != null) return okHTTPClient.cache();
        else return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 删除缓存
        CommonUtils.deleteTmpDir(this);

        // 小米推送
        if (shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }

        LoggerInterface newLogger = new LoggerInterface() {

            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };

        Logger.setLogger(this, newLogger);

        // 自动更新配置
        Beta.autoCheckUpgrade = false;   // 禁用自动初始化更新
        Beta.smallIconId = R.drawable.ic_stat_bu;  // 状态栏图标

        Bugly.init(getApplicationContext(), BUApplication.BUGLY_APP_ID, BUApplication.debugMode);   // 初始化 SDK
        CrashReport.initCrashReport(getApplicationContext(), BUGLY_APP_ID, debugMode);  // 启动崩溃日志上传

        // 从缓存中读取数据
        // migrateCache(this);
        readConfig(this);
    }
}