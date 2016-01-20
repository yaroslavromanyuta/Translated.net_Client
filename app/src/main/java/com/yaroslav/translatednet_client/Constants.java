package com.yaroslav.translatednet_client;


public final class Constants {
    public static final String LOG_TAG = "translClient";
    static final public String DB_NAME = "translationsdatabase";
    public static final String TRANSL_TABLE = "translations";
    public static final int DB_VERSION = 1;
    public static final String TRANSL_ID = "_id";
    public static final String TRANSL_FROM = "transl_from";
    public static final String TRANSL_TO = "transl_to";
    public static final String LANG_FROM = "lang_from";
    public static final String LANG_TO = "lang_to";
    public static final String AUTHORITY = "com.yaroslav.providers.TranslateBase";
    public static final String TRANSL_PATH = "translations";

    public static final String BASE_URL = "http://api.mymemory.translated.net";

    private Constants(){

    }
}
