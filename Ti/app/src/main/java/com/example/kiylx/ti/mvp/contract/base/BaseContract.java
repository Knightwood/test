package com.example.kiylx.ti.mvp.contract.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.kiylx.ti.model.WebPage_Info;

import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/11 0:04
 */
public interface BaseContract {
    interface View {

        void showProgressDialog(String content);

        void dismissProgressDialog();

        ProgressDialog getProgressDialog(String content);

        void showTipDialog(String content);

        void showConfirmDialog(String msn, String title, String confirmText, DialogInterface.OnClickListener confirmListener);

        void showToast(String message);

        void showInfoToast(String message);

        void showSuccessToast(String message);

        void showErrorToast(String message);

        void showWarningToast(String message);

        void showLoading();

        void hideLoading();

        void showLoginPage();

        void share(String s);

        void saveBookmark(WebPage_Info info);

        void showSnackbar(android.view.View view, String actionText,String text, Object... args);

    }

    interface Presenter<V extends BaseContract.View>{

        void onSaveInstanceState(Bundle outState);

        void onRestoreInstanceState(Bundle outState);

        void attachView(@NonNull V view);

        void detachView();

        /**
         * view initialized, you can init view data
         */
        void onViewInitialized();

        @Nullable
        Context getContext();
    }
}
