package com.skinfotech.dailyneeds.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.LoginRequest;
import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import com.google.android.material.snackbar.Snackbar;
import retrofit2.Call;
import retrofit2.Response;

public class LoginFragment extends BaseFragment {

    private boolean mIsDoubleBackPress = false;
    private EditText emailInput;
    private boolean isEmailValid;
    private boolean isPasswordValid;
    private static final String TAG = "LoginFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_login, container, false);
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().onBackPressed(this);
        mActivity.isToggleButtonEnabled(false);
        emailInput = mContentView.findViewById(R.id.userPhoneNumber);
        return mContentView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
           /* case R.id.userLogin:
                if (Utility.isEmpty(emailInput)) {
                    emailInput.setError(mActivity.getString(R.string.mandatory_field_message));
                    emailInput.requestFocus();
                    isEmailValid = false;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput.getText().toString()).matches()) {
                    emailInput.setError(getResources().getString(R.string.error_invalid_email));
                    emailInput.requestFocus();
                    isEmailValid = false;
                } else {
                    isEmailValid = true;
                }
                *//*if (Utility.isEmpty(passwordInput)) {
                    passwordInput.setError(mActivity.getString(R.string.mandatory_field_message));
                    passwordInput.requestFocus();
                    isPasswordValid = false;
                } else if (passwordInput.getText().length() < 6) {
                    passwordInput.setError(getResources().getString(R.string.error_invalid_password));
                    passwordInput.requestFocus();
                    isPasswordValid = false;
                } else {
                    isPasswordValid = true;
                }*//*
                if (isEmailValid && isPasswordValid) {
                    doLoginServerCall();
                }
                break;*/
        }
    }

    private void doLoginServerCall() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String email = emailInput.getText().toString();
                    LoginRequest request = new LoginRequest();
                    request.setEmailAddress(email);
                    Call<CommonResponse> call = RetrofitApi.getAppServicesObject().login(request);
                    final Response<CommonResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<CommonResponse> response) {
                if (response.isSuccessful()) {
                    CommonResponse commonResponse = response.body();
                    if (commonResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(commonResponse.getErrorCode())) {
                            storeStringDataInSharedPref(Constants.USER_ID , commonResponse.getUserId());
                            storeStringDataInSharedPref(Constants.USER_LOGIN_DONE , Constants.YES);
                            launchFragment(new HomeScreenFragment(), false);
                        }
                        showToast(commonResponse.getErrorMessage());
                    }
                }
                stopProgress();
            }
        }).start();
    }

    private boolean validateEmailAddress(EditText editText) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String email = editText.getText().toString().toLowerCase().trim();
        if (email.isEmpty() && email.matches(emailPattern)) {
            Toast.makeText(mActivity, "valid email address", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(mActivity, "Invalid email address", Toast.LENGTH_SHORT).show();
            emailInput.requestFocus();
            return false;
        }
    }

    /*@Override
    public boolean onBackPressed() {
        if (mIsDoubleBackPress) {
            super.onBackPressedToExit(this);
            return true;
        }
        Snackbar.make(mContentView, getString(R.string.back_press_msg), Snackbar.LENGTH_SHORT).show();
        mIsDoubleBackPress = true;
        new Handler().postDelayed(() -> mIsDoubleBackPress = false, 1500);
        return true;
    }*/

    @Override
    public void onStart() {
        super.onStart();
        mActivity.showBackButton();
        mActivity.hideCartIcon();
        hideKeyboard();
    }

}
