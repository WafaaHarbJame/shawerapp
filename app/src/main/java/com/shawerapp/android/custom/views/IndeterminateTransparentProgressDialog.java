
package com.shawerapp.android.custom.views;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.shawerapp.android.R;

public class IndeterminateTransparentProgressDialog extends Dialog {

    public static IndeterminateTransparentProgressDialog show(Context context) {
        return show(context, false);
    }

    public static IndeterminateTransparentProgressDialog show(Context context, boolean indeterminate) {
        return show(context, indeterminate, false, null);
    }

    public static IndeterminateTransparentProgressDialog show(Context context, boolean indeterminate, boolean cancelable) {
        return show(context, indeterminate, cancelable, null);
    }

    public static IndeterminateTransparentProgressDialog show(Context context, boolean indeterminate, boolean cancelable, OnCancelListener cancelListener) {
        IndeterminateTransparentProgressDialog dialog = new IndeterminateTransparentProgressDialog(context);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        /* The next line will add the ProgressBar to the dialog. */
        dialog.addContentView(new ProgressBar(context), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        dialog.show();

        return dialog;
    }

    private IndeterminateTransparentProgressDialog(Context context) {
        super(context, R.style.ThemeProgressDialog);
    }
}
