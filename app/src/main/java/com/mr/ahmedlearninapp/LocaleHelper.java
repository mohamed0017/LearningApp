package com.mr.ahmedlearninapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Locale;

/**
 * This class is used to change your application locale and persist this change for the next time
 * that your app is going to be used.
 * <p/>
 * You can also change the locale of your application on the fly by using the setLocale method.
 * <p/>
 * Created by gunhansancar on 07/10/15.
 */
public class LocaleHelper {

    public static final String ENGLISH_LANGUAGE = "en";
    public static final String ARABIC_LANGUAGE = "ar";
    public static final String CHINEASE_LANGUAGE = "zh";
    // public static final String DEFAULT_LANGUAGE = "en";
    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"; // this value is in BaseRepository

//    public static Context onAttach(Context context) {
//        String lang = getPersistedData(context/*, Locale.getDefault().getLanguage()*/);
//        return setLocale(context, lang);
//    }

    public static Context onAttach(Context context) {
        String lang = getPersistedData(context/*, defaultLanguage*/);
        if (lang.isEmpty()) {
            String language = getDeviceLang();
            persist(context, language);
            return setLocale(context, language);
        } else
            return setLocale(context, lang);
    }

    public static Context onAttach(Context context, String lang) {
        if (lang.isEmpty()) {
            String language = getDeviceLang();
            persist(context, language);
            return setLocale(context, language);
        } else
            return setLocale(context, lang);
    }

    private static String getDeviceLang() {
        if (Locale.getDefault().toString().contains("zh")) {
            return "zh";
        } else return Locale.getDefault().getLanguage();
    }

    public static String getLanguage(Context context) {
        return getPersistedData(context/*, Locale.getDefault().getLanguage()*/);
    }

    public static Context setLocale(Context context, String language) {
        //persist(context, language);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }

        return updateResourcesLegacy(context, language);
    }

    public static void saveSelectedLang(Context context, String language) {
        persist(context, language);
    }

    public static Context changeLocaleWithoutPersistence(Context context, String language) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }

        return updateResourcesLegacy(context, language);
    }

    private static String getPersistedData(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SELECTED_LANGUAGE, "");
    }

    private static void persist(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);

        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        context.getResources().updateConfiguration(configuration,
                context.getResources().getDisplayMetrics());
        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);

        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }
}