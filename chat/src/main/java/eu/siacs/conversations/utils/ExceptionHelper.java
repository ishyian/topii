package eu.siacs.conversations.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.yourbestigor.chat.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import eu.siacs.conversations.Config;
import eu.siacs.conversations.entities.Account;
import eu.siacs.conversations.services.XmppConnectionService;
import eu.siacs.conversations.ui.XmppActivity;

public class ExceptionHelper {

    private static final String FILENAME = "stacktrace.txt";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    public static void init(Context context) {
        if (Thread.getDefaultUncaughtExceptionHandler() instanceof ExceptionHandler) {
            return;
        }
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(context));
    }

    @SuppressLint("QueryPermissionsNeeded")
    public static boolean checkForCrash(XmppActivity activity) {
        try {
            final XmppConnectionService service = activity == null ? null : activity.xmppConnectionService;
            if (service == null) {
                return false;
            }
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
            boolean neverSend = preferences.getBoolean("never_send", false);
            if (neverSend || Config.BUG_REPORTS == null) {
                return false;
            }
            final Account account = AccountUtils.getFirstEnabled(service);
            if (account == null) {
                return false;
            }
            FileInputStream file = activity.openFileInput(FILENAME);
            InputStreamReader inputStreamReader = new InputStreamReader(file);
            BufferedReader stacktrace = new BufferedReader(inputStreamReader);
            final StringBuilder report = new StringBuilder();
            PackageManager pm = activity.getPackageManager();
            PackageInfo packageInfo;
            try {
                packageInfo = pm.getPackageInfo(activity.getPackageName(), PackageManager.GET_SIGNATURES);
                final String versionName = packageInfo.versionName;
                final int versionCode = packageInfo.versionCode;
                final int version = versionCode > 10000 ? (versionCode / 100) : versionCode;
                report.append(String.format(Locale.ROOT, "Version: %s(%d)", versionName, version)).append('\n');
                report.append("Last Update: ").append(DATE_FORMAT.format(new Date(packageInfo.lastUpdateTime))).append('\n');
                Signature[] signatures = packageInfo.signatures;
                if (signatures != null && signatures.length >= 1) {
                    report.append("SHA-1: ").append(CryptoHelper.getFingerprintCert(packageInfo.signatures[0].toByteArray())).append('\n');
                }
                report.append('\n');
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            String line;
            while ((line = stacktrace.readLine()) != null) {
                report.append(line);
                report.append('\n');
            }
            file.close();
            activity.deleteFile(FILENAME);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(activity.getString(R.string.crash_report_title, activity.getString(R.string.app_name)));
            builder.setMessage(activity.getString(R.string.crash_report_message, activity.getString(R.string.app_name)));
            builder.setPositiveButton(activity.getText(R.string.send_now), (dialog, which) -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"yourbestigor@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Crash");
                intent.putExtra(Intent.EXTRA_TEXT, report.toString());
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    activity.startActivity(intent);
                }
                Log.d(Config.LOGTAG, "using account=" + account.getJid().asBareJid() + " to send in stack trace");
            });
            builder.setNegativeButton(activity.getText(R.string.send_never), (dialog, which) -> preferences.edit().putBoolean("never_send", true).apply());
            builder.create().show();
            return true;
        } catch (final IOException ignored) {
            return false;
        }
    }


    static void writeToStacktraceFile(Context context, String msg) {
        try {
            OutputStream os = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            os.write(msg.getBytes());
            os.flush();
            os.close();
        } catch (IOException ignored) {
        }
    }
}
