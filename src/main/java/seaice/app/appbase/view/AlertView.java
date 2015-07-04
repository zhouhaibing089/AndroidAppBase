package seaice.app.appbase.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import seaice.app.appbase.R;

/**
 * 警告框
 *
 * @author zhb
 */
public class AlertView extends Dialog {

    public AlertView(Context context) {
        super(context);
    }

    public AlertView(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        /**
         * Context对象
         */
        Context mContext;
        /**
         * AlertView的标题
         */
        String mTitle;
        /**
         * AlertView的消息
         */
        String mMessage;
        /**
         * AlertView的主体View
         */
        View mContentView;
        /**
         * 左按钮文字
         */
        String mLeftButtonText;
        /**
         * 右按钮文字
         */
        String mRightButtonText;
        /**
         * 左边按钮监听事件
         */
        View.OnClickListener mLeftClickListener;
        /**
         * 右边按钮监听事件
         */
        View.OnClickListener mRightClickListener;

        public static Builder with(Context context) {
            return new Builder(context);
        }

        private Builder(Context context) {
            this.mContext = context;
        }

        public Builder title(String title) {
            mTitle = title;
            return this;
        }

        public Builder title(int titleResId) {
            mTitle = mContext.getString(titleResId);
            return this;
        }

        public Builder message(String message) {
            mMessage = message;
            return this;
        }

        public Builder message(int messageResId) {
            mMessage = mContext.getString(messageResId);
            return this;
        }

        public Builder content(View contentView) {
            mContentView = contentView;
            return this;
        }

        public Builder positive(int textResId, View.OnClickListener listener) {
            return positive(mContext.getString(textResId), listener);
        }

        public Builder positive(String text, View.OnClickListener listener) {
            this.mRightButtonText = text;
            this.mRightClickListener = listener;
            return this;
        }

        public Builder negative(int textResId, View.OnClickListener listener) {
            return negative(mContext.getString(textResId), listener);
        }

        public Builder negative(String text, View.OnClickListener listener) {
            this.mLeftButtonText = text;
            this.mLeftClickListener = listener;
            return this;
        }

        /**
         * 创建AlertView实例
         *
         * @return AlertView
         */
        public AlertView create() {
            final AlertView alertView = new AlertView(mContext, R.style.AlertView);
            View rootView = LayoutInflater.from(mContext).inflate(R.layout.dialog_alert, null);
            alertView.addContentView(rootView, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            TextView titleView = (TextView) rootView.findViewById(R.id.app_base_alert_title);
            ViewGroup contentView = (ViewGroup) rootView.findViewById(R.id.app_base_alert_content);
            TextView messageView = (TextView) rootView.findViewById(R.id.app_base_alert_message);

            if (mTitle != null) {
                titleView.setText(mTitle);
            }
            if (mMessage != null) {
                messageView.setText(mMessage);
            }
            // 如果设置了ContentView, 就把MessageView取消掉
            if (mContentView != null) {
                contentView.removeView(messageView);
                contentView.addView(mContentView, new RelativeLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            }
            // 按钮设定
            Button leftButton = (Button) rootView.findViewById(R.id.app_base_alert_left_button);
            Button rightButton = (Button) rootView.findViewById(R.id.app_base_alert_right_button);
            leftButton.setText(mLeftButtonText);
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLeftClickListener.onClick(v);
                    alertView.dismiss();
                }
            });
            rightButton.setText(mRightButtonText);
            rightButton.setOnClickListener(mRightClickListener);

            // 其他设定
            alertView.setCancelable(false);
            return alertView;
        }

    }
}
