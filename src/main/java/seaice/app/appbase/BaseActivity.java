package seaice.app.appbase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import butterknife.ButterKnife;
import seaice.app.appbase.view.NavBarView;

public abstract class BaseActivity extends AppCompatActivity {

    protected NavBarView mNavBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        if (hasNavBar()) {
            mNavBarView = (NavBarView) findViewById(R.id.app_base_nav_bar);
        }
        ButterKnife.inject(this);
        // Member的Injection
        if (needDagger()) {
            BaseApplication.inject(this);
        }
    }

    /* 重载此方法是跳转进入的Activity知道当前Activity的Title */
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (intent.getStringExtra("title") == null) {
            intent.putExtra("title", getTitle());
        }
        super.startActivityForResult(intent, requestCode);
    }

    /* 重载此方法是跳转进入的Activity知道当前Activity的Title */
    @Override
    public void startActivity(Intent intent) {
        if (intent.getStringExtra("title") == null) {
            intent.putExtra("title", getTitle());
        }
        super.startActivity(intent);
    }

    /* 子类必须设置页面的Layout */
    protected abstract int getLayoutResId();

    /* 子类可以重载此方法来让此Activity不做Dagger Injection */
    protected boolean needDagger() {
        return true;
    }

    /* 是否有Navigation Bar */
    protected boolean hasNavBar() {
        return true;
    }

    /* 当按下返回键时 */
    public void onBackPressed() {
        if (mNavBarView != null && mNavBarView.isPopupShowing()) {
            mNavBarView.dismissPopup();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (mNavBarView != null) {
                mNavBarView.triggerMenu();
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 辅助方法, 取得一个View
     *
     * @param id View的ID
     * @return View实例
     */
    protected View view(int id) {
        return findViewById(id);
    }
}
