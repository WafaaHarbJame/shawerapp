
package com.shawerapp.android.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;

import com.shawerapp.android.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import solid.stream.Stream;
import timber.log.Timber;

import static solid.collectors.ToList.toList;

/**
 * Created by Ernest on 4/2/2015.
 */
public class CommonUtils {

    public enum POSITION {
        START, END, MIDDLE
    }

    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean lengthOfTextIs(String str, int length) {
        return str.length() >= length;
    }

    public static boolean textMatch(POSITION position, String str, String strToMatch) {
        switch (position) {
            case START:
                return str.startsWith(strToMatch);
            case END:
                return str.endsWith(strToMatch);
            case MIDDLE:
                return str.contains(strToMatch);
            default:
                return false;
        }
    }

    public static boolean allFieldsFilled(EditText... fields) {
        boolean allFieldsFilled = true;
        for (EditText field : fields) {
            boolean isNotEmpty = isNotEmpty(field.getText().toString());
            if (!isNotEmpty) {
                field.setError(field.getContext().getString(R.string.error_should_not_be_empty));
            }
            allFieldsFilled = isNotEmpty && allFieldsFilled;
        }

        return allFieldsFilled;
    }

    public static boolean allFieldsFilled(TextInputLayout... fields) {
        boolean allFieldsFilled = true;
        Context context;
        for (TextInputLayout field : fields) {
            context = field.getContext();
            boolean isNotEmpty = isNotEmpty(field.getEditText().getText().toString());
            if (!isNotEmpty) {
                field.setErrorEnabled(true);
                field.setError(field.getContext().getString(R.string.error_should_not_be_empty));
            }
            allFieldsFilled = isNotEmpty && allFieldsFilled;
        }

        return allFieldsFilled;
    }

    public static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

    public static String getExceptionString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    public static String getMimeType(String filePath) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static boolean hasConnection(Context context) {
        ConnectivityManager mConnectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mActiveNetwork = mConnectivityManager.getActiveNetworkInfo();

        if (mActiveNetwork != null && mActiveNetwork.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }

    public static byte[] getMediaBytes(String filePath) {
        File file = new File(filePath);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }

        String filenameArray[] = filename.split("\\.");
        String extension = filenameArray[filenameArray.length - 1];
        return extension;
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        if (context != null) {
            try {
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                    List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
                    for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                        if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                            for (String activeProcess : processInfo.pkgList) {
                                if (activeProcess.equals(context.getPackageName())) {
                                    isInBackground = false;
                                }
                            }
                        }
                    }
                } else {
                    List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                    ComponentName componentInfo = taskInfo.get(0).topActivity;
                    if (componentInfo.getPackageName().equals(context.getPackageName())) {
                        isInBackground = false;
                    }
                }

            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return isInBackground;
    }

    public static String getRealPathFromURI(Context context, String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public static boolean isFileExist(Context context, String fileName) {
        File file = new File(context.getApplicationContext().getCacheDir(), fileName);
        return file.exists() && file.isFile();
    }

    public static File getFile(Context context, String fileName) {
        return new File(context.getApplicationContext().getCacheDir(), fileName);
    }

    public static void showKeyboard(EditText editText, Activity activity) {
        editText.postDelayed(() -> {
            InputMethodManager keyboard = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(editText, 0);
        }, 200);
    }

    public static void hideKeyBoard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static boolean isKeyboardShown(ViewGroup viewGroup) {
        Rect r = new Rect();
        try {
            viewGroup.getWindowVisibleDisplayFrame(r);
        } catch (Exception x) {

        }
        int screenHeight = viewGroup.getRootView().getHeight();
        int keypadHeight = screenHeight - r.bottom;

        boolean isKeyboardShown = false;
        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
            isKeyboardShown = keypadHeight > screenHeight * 0.15;
        }

        return isKeyboardShown;
    }

    public static String getCurrentISO(Context context) {
        String countryIsoCode = "";

        TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (telephonyManager != null && !(telephonyManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT)) {
            countryIsoCode = telephonyManager.getNetworkCountryIso();
        } else {
            countryIsoCode = Locale.getDefault().getCountry();
        }

        return countryIsoCode;
    }

    public static List<String> getCountries() {
        List<String> countryNames = new ArrayList<>();
        for (String countryCode : Locale.getISOCountries()) {
            if (countryCode.equals("SA") || countryCode.equals("DZ") || countryCode.equals("BH") ||
                    countryCode.equals("DJ") || countryCode.equals("EG") || countryCode.equals("ET") ||
                    countryCode.equals("IR") || countryCode.equals("IQ") || countryCode.equals("JO") ||
                    countryCode.equals("KW") || countryCode.equals("LB") || countryCode.equals("LY") ||
                    countryCode.equals("MA") || countryCode.equals("OM") || countryCode.equals("PS") ||
                    countryCode.equals("QA") || countryCode.equals("SO") || countryCode.equals("SS") ||
                    countryCode.equals("SD") || countryCode.equals("SY") || countryCode.equals("TN") ||
                    countryCode.equals("TR") || countryCode.equals("AE") || countryCode.equals("YE")) {
                Locale locale = new Locale("", countryCode);
                String countryName = locale.getDisplayCountry();
                if (isEmpty(countryName)) {
                    countryName = "Unidentified";
                }
                countryNames.add(countryName);
            }
        }
        return Stream.stream(countryNames)
                .filter(value -> !value.equalsIgnoreCase("Israel"))
                .sort((o1, o2) -> {
                    if (o1.equalsIgnoreCase("Saudi Arabia")) {
                        return -1;
                    }

                    if (o2.equalsIgnoreCase("Saudi Arabia")) {
                        return 1;
                    }

                    return o1.compareToIgnoreCase(o2);
                })
                .collect(toList());
    }

    public static int dp2px(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static int getStatusBarHeight(Context context) {
        // 一般是25dp
        int height = dp2px(context, 20);
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = context.getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }

    public static boolean isNavigationBarShow(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (menu || back) {
                return false;
            } else {
                return true;
            }
        }
    }

    public static int getNavigationBarHeight(Activity activity) {
        if (!isNavigationBarShow(activity))
            return 0;
        int height = 0;
        Resources resources = activity.getResources();
        //获取NavigationBar的高度
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0)
            height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }

    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                Timber.e(getExceptionString(e));
            }
        }

        return size;
    }

    public static String formatNumber(long number) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
        return decimalFormat.format(number);
    }

    public static boolean isRTL() {
        return TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }
}
