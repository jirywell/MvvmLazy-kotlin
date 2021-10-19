package com.rui.base.view;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.lxj.xpopup.core.CenterPopupView;
import com.rui.base.R;

/**
 * Description: 自定义状态提示框
 * Create by zjr, at 2019/2/27
 */
public class HintPopup extends CenterPopupView {
    public final static int ICON_FINISH = R.drawable.finish_ic;
    public final static int ICON_ERROR = R.drawable.error_ic;
    public final static int ICON_WARNING = R.drawable.warning_ic;
    private TextView mMessageView;
    private ImageView mIconView;
    private int mDuration = 500;
    private int iconId = 0;
    private CharSequence text = "";

    public HintPopup(@NonNull Context context) {

        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.hint_dialog;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mMessageView = findViewById(R.id.tv_hint_message);
        mIconView = findViewById(R.id.iv_status_icon);
        setup();
    }

    public HintPopup setIcon(@DrawableRes int iconId) {
        this.iconId = iconId;
        setup();
        return this;
    }

    public HintPopup setDuration(int duration) {
        mDuration = duration;
        return this;
    }

    public HintPopup setMessage(@StringRes int id) {
        return setMessage(getContext().getString(id));
    }

    public HintPopup setMessage(CharSequence text) {
        this.text = text;
        setup();
        return this;
    }

    protected void setup() {
        if (mMessageView == null || mIconView == null) return;
        post(() -> {
            if (iconId != 0)
                mIconView.setImageResource(iconId);
            if (!TextUtils.isEmpty(text))
                mMessageView.setText(text);
        });
    }

    @Override
    protected void onShow() {
        super.onShow();
        postDelayed(() -> {
            dismiss();
        }, mDuration);
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
    }

}
