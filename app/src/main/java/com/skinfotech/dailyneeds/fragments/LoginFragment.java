package com.skinfotech.dailyneeds.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.LoginRequest;
import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;

import retrofit2.Call;
import retrofit2.Response;

public class LoginFragment extends BaseFragment {

    private TextInputEditText mUserEmailTextInputEditText;
    private TextInputEditText mUserPasswordTextInputEditText;
    private static final String TAG = "LoginFragment";
    private boolean mIsDoubleBackPress = false;
    private String email;
    private String password;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_login, container, false);
        ToolBarManager.getInstance().hideToolBar(mActivity, true);
        ToolBarManager.getInstance().setHeaderTitle("");
        mActivity.isToggleButtonEnabled(false);
        CardView cardView = mContentView.findViewById(R.id.appLogoCardView);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.blue));
        mUserEmailTextInputEditText = mContentView.findViewById(R.id.userEmail);
        mUserPasswordTextInputEditText = mContentView.findViewById(R.id.userPassword);
        return mContentView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginButton:
                email = mUserEmailTextInputEditText.getText().toString();
                password = mUserPasswordTextInputEditText.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (Utility.isEmpty(mUserEmailTextInputEditText)) {
                    mUserEmailTextInputEditText.setError(mActivity.getString(R.string.mandatory_field_message));
                    mUserEmailTextInputEditText.requestFocus();
                }
                else if (Utility.isEmpty(mUserPasswordTextInputEditText)) {
                    mUserPasswordTextInputEditText.setError(mActivity.getString(R.string.mandatory_field_message));
                    mUserPasswordTextInputEditText.requestFocus();
                } else if (mUserPasswordTextInputEditText.getText().length() < 6) {
                    mUserPasswordTextInputEditText.setError(getResources().getString(R.string.error_invalid_password));
                    mUserPasswordTextInputEditText.requestFocus();
                }else {
                    doLoginServerCall();
                }
                break;
            case R.id.signupTextView:
                launchFragment(new SignUpFragment(), false);
                break;
            case R.id.forgetPasswordTextView:
                launchFragment(new ForgotPasswordFragment(), true);
                break;
        }
    }

    private void doLoginServerCall() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LoginRequest request = new LoginRequest();
                    request.setEmailAddress(email);
                    request.setPassword(password);
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
                            storeStringDataInSharedPref(Constants.USER_ID, commonResponse.getUserId());
                            storeStringDataInSharedPref(Constants.USER_LOGIN_DONE, Constants.YES);
                            storeStringDataInSharedPref(Constants.USER_EMAIL, commonResponse.getUserEmail());
                            storeStringDataInSharedPref(Constants.USER_MOBILE, commonResponse.getUserMobile());
                            launchFragment(new HomeScreenFragment(), false);
                        }
                        showToast(commonResponse.getErrorMessage());
                    }
                }
                stopProgress();
            }
        }).start();
    }


    @Override
    public void onStart() {
        super.onStart();
        mActivity.hideBackButton();
        mActivity.hideCartIcon();
        mActivity.hideSearchIcon();
        hideKeyboard();
    }

    @Override
    public boolean onBackPressed() {
        if (mIsDoubleBackPress) {
            super.onBackPressedToExit(this);
            return true;
        }
        Snackbar.make(mContentView, getString(R.string.back_press_msg), Snackbar.LENGTH_SHORT).show();
        mIsDoubleBackPress = true;
        new Handler().postDelayed(() -> mIsDoubleBackPress = false, 1500);
        return true;
    }

}
