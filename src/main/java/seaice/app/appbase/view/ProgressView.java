package seaice.app.appbase.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import seaice.app.appbase.R;

public class ProgressView extends Dialog {

    public ProgressView(Context context) {
        super(context);
    }

    public ProgressView(Context context, int theme) {
        super(context, theme);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        ImageView imageView = (ImageView) findViewById(R.id.app_base_progress_spinner);
        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
        spinner.start();
    }

    public void setMessage(CharSequence message) {
        if (message != null && message.length() > 0) {
            findViewById(R.id.app_base_progress_text).setVisibility(View.VISIBLE);
            TextView txt = (TextView) findViewById(R.id.app_base_progress_text);
            txt.setText(message);
            txt.invalidate();
        }
    }

    public static ProgressView show(Context context, CharSequence message, boolean cancelable,
                                    OnCancelListener cancelListener) {
        ProgressView dialog = new ProgressView(context, R.style.ProgressView);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_progress);
        if (message == null || message.length() == 0) {
            dialog.findViewById(R.id.app_base_progress_text).setVisibility(View.GONE);
        } else {
            TextView txt = (TextView) dialog.findViewById(R.id.app_base_progress_text);
            txt.setText(message);
        }
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.2f;
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        return dialog;
    }
}
