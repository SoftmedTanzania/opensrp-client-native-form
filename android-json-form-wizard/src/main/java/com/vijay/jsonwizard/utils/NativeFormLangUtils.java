package com.vijay.jsonwizard.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

import static com.vijay.jsonwizard.constants.JsonFormConstants.MLS.PROPERTIES_FILE_NAME;

public class NativeFormLangUtils {

    public static String getLanguage(Context context) {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(PreferenceManager.getDefaultSharedPreferences(context));
        return allSharedPreferences.fetchLanguagePreference();
    }

    private static Locale getLocale(Context context) {
        return new Locale(getLanguage(context));
    }

    public static Context setAppLocale(Context ctx, String language) {
        Locale locale = new Locale(language);

        Resources res = ctx.getResources();
        Configuration conf = res.getConfiguration();
        conf.setLocale(locale);
        Context context;
        try {
            context = ctx.createConfigurationContext(conf);
        } catch (Exception e) {
            context = ctx;
            Log.d("LangUtils", e.toString());
        }

        return context;
    }

    /**
     * Performs translation on an interpolated {@param str}
     * i.e. a String containing tokens in the format {{string_name}},
     * replacing these tokens with their corresponding values for the current Locale
     *
     * @param str
     * @return
     */
    public static String getTranslatedString(String str) {
        return getTranslatedString(str, null);
    }

    /**
     * Performs translation on an interpolated {@param str}
     * i.e. a String containing tokens in the format {{string_name}},
     * replacing these tokens with their corresponding values for the current Locale.
     *
     * It attempts to fetch the default Locale from shared preferences.
     *
     * @param str
     * @return
     */
    public static String getTranslatedString(String str, Context context) {
        String translationsFileName = getTranslationsFileName(str);
        if (translationsFileName.isEmpty()) {
            Timber.e("Could not translate the String. Translation file name is not specified!");
            return str;
        }
        Locale currLocale = context == null ? Locale.getDefault() : getLocale(context);
        return translateString(str, ResourceBundle.getBundle(translationsFileName, currLocale));
    }

    /**
     * Performs translation on an interpolated {@param str}
     * i.e. a String containing tokens in the format {{string_name}},
     * replacing these tokens with their corresponding values for the current Locale.
     *
     * It attempts to fetch the default Locale from shared preferences and allows specifying an
     * alternative file path to the translation property files at {@param propertyFilesFolderPath}.
     *
     * @param str
     * @return
     */
    public static String getTranslatedString(String str, Context context, String propertyFilesFolderPath) {
        String translatedString = str;
        try {
            Locale currLocale = context == null ? Locale.getDefault() : getLocale(context);
            ResourceBundle mlsResourceBundle = ResourceBundle.getBundle(getTranslationsFileName(str),
                    currLocale, getPathURL(propertyFilesFolderPath));
            translatedString = translateString(str, mlsResourceBundle);
        } catch (MalformedURLException e) {
            Timber.e(e);
        }
        return translatedString;
    }

    /**
     * Helper method that performs regex matching to replace placeholders with String literals
     *
     * @param str
     * @param mlsResourceBundle
     * @return
     */
    private static String translateString(String str, ResourceBundle mlsResourceBundle) {
        StringBuffer stringBuffer = new StringBuffer();
        Pattern interpolatedStringPattern = Pattern.compile("\\{\\{([a-zA-Z_0-9\\.]+)\\}\\}");
        Matcher matcher = interpolatedStringPattern.matcher(str);
        while (matcher.find()) {
            String replacement = Matcher.quoteReplacement(mlsResourceBundle.getString(matcher.group(1))
                            .replace("\n", "\\n")); // ensures \n is preserved in a String
            matcher.appendReplacement(stringBuffer, replacement);
        }
        matcher.appendTail(stringBuffer);

        return stringBuffer.toString();
    }

    /**
     * Creates a {@link URLClassLoader} from a String {@param path}
     *
     * @param path
     * @return
     * @throws MalformedURLException
     */
    private static URLClassLoader getPathURL(String path) throws MalformedURLException {
        File file = new File(path);
        URL[] urls = {file.toURI().toURL()};
        return new URLClassLoader(urls);
    }

    /**
     * Gets the name of the translation file to be applied to the {@param str}
     *
     * @param str
     * @return
     */
    public static String getTranslationsFileName(String str) {
        Pattern propertiesFileNamePattern = Pattern.compile("\"?" + PROPERTIES_FILE_NAME + "\"?: ?\"([a-zA-Z_0-9\\.]+)\"");
        Matcher matcher = propertiesFileNamePattern.matcher(str);
        return matcher.find() ? matcher.group(1) : "";
    }
}
