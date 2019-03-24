package com.sscl.basesample.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;

import com.sscl.baselibrary.activity.BaseWelcomeActivity;
import com.sscl.baselibrary.utils.CrashHandler;
import com.sscl.baselibrary.utils.ToastUtil;
import com.sscl.basesample.MainActivity;
import com.sscl.basesample.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;


/**
 * 欢迎页 在这个界面可以进行权限请求等`
 *
 * @author alm
 * @date 2017/10/13
 */
public class WelcomeActivity extends BaseWelcomeActivity {
    private static final int REQUEST_CODE_SETTING = 1;
    private Action<List<String>> onGrantedListener = new Action<List<String>>() {
        @Override
        public void onAction(List<String> grantPermissions) {
            toNext();
        }
    };
    private Action<List<String>> onDeniedListener = new Action<List<String>>() {
        @Override
        public void onAction(List<String> deniedPermissions) {
            if (!AndPermission.hasAlwaysDeniedPermission(WelcomeActivity.this, deniedPermissions)) {
                ToastUtil.toastL(WelcomeActivity.this, R.string.no_permission_exits);
                finish();
            } else {
                List<String> strings = Permission.transformText(WelcomeActivity.this, deniedPermissions);
                String permissionText = TextUtils.join(",\n", strings);
                new AlertDialog.Builder(WelcomeActivity.this)
                        .setTitle(R.string.no_permission)
                        .setMessage(permissionText)
                        .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AndPermission.with(WelcomeActivity.this)
                                        .runtime()
                                        .setting()
                                        .start(REQUEST_CODE_SETTING);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        }
    };
    private Rationale<List<String>> rationaleListener = new Rationale<List<String>>() {
        @Override
        public void showRationale(Context context, List<String> data, RequestExecutor executor) {
            List<String> strings = Permission.transformText(WelcomeActivity.this, data);
            String permissionText = TextUtils.join(",\n", strings);
            new AlertDialog.Builder(WelcomeActivity.this)
                    .setTitle(R.string.no_permission)
                    .setMessage(permissionText)
                    .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AndPermission.with(WelcomeActivity.this)
                                    .runtime()
                                    .setting()
                                    .start(REQUEST_CODE_SETTING);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    };

    @Override
    protected void doAfterAnimation() {
        requestPermission();
    }

    /**
     * 设置ImageView的图片资源
     *
     * @return 图片资源ID
     */
    @Override
    protected int setImageViewSource() {
        return 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SETTING:
                requestPermission();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void toNext() {
        CrashHandler.getInstance().init(WelcomeActivity.this.getApplicationContext());
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void requestPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted(onGrantedListener)
                .onDenied(onDeniedListener)
                .rationale(rationaleListener)
                .start();
    }
}
