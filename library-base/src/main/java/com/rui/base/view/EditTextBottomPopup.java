package com.rui.base.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.rui.base.R;

/**
 * Description: 自定义带有输入框的Bottom弹窗
 * Create by dance, at 2019/2/27
 */
public class EditTextBottomPopup extends BottomPopupView {

    public EditTextBottomPopup(@NonNull Context context, OnInputConfirmListener inputConfirmListener) {
        super(context);
        this.inputConfirmListener = inputConfirmListener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.edittext_bottom_popup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onShow() {
        super.onShow();
        EditText etComment = findViewById(R.id.et_comment);
        findViewById(R.id.btn_finish).setOnClickListener(v -> {
            String content = etComment.getText().toString().trim();
            if (!TextUtils.isEmpty(content)) {
                if (inputConfirmListener != null) {
                    inputConfirmListener.onConfirm(content);
                }
                etComment.setText("");
                dismiss();
            }
        });


    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
    }

    OnInputConfirmListener inputConfirmListener;

}
