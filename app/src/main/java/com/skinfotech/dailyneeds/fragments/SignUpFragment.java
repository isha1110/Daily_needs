package com.skinfotech.dailyneeds.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
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
import com.skinfotech.dailyneeds.models.requests.RegistrationRequest;
import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;

import retrofit2.Call;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class SignUpFragment extends BaseFragment {

    private boolean mIsDoubleBackPress = false;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText userNameInput;
    private TextInputEditText mobileInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_signup, container, false);
        ToolBarManager.getInstance().hideToolBar(mActivity, true);
        ToolBarManager.getInstance().setHeaderTitle("");
        mActivity.isToggleButtonEnabled(false);
        emailInput = mContentView.findViewById(R.id.userEmail);
        passwordInput = mContentView.findViewById(R.id.userPassword);
        userNameInput = mContentView.findViewById(R.id.userName);
        mobileInput = mContentView.findViewById(R.id.userMobileNumber);
        CardView cardView = mContentView.findViewById(R.id.appLogoCardView);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.blue));
        return mContentView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signupButton:
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (Utility.isEmpty(userNameInput)) {
                    userNameInput.setError(getResources().getString(R.string.mandatory_field_message));
                    userNameInput.requestFocus();
                }
                else if (Utility.isEmpty(emailInput)) {
                    emailInput.setError(getResources().getString(R.string.mandatory_field_message));
                    emailInput.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput.getText().toString()).matches()) {
                    emailInput.setError(getResources().getString(R.string.invalid_email));
                    emailInput.requestFocus();
                }
                else if (Utility.isEmpty(mobileInput)) {
                    mobileInput.setError(getResources().getString(R.string.mandatory_field_message));
                    mobileInput.requestFocus();
                }else if (mobileInput.getText().length() < 10) {
                    mobileInput.setError(getResources().getString(R.string.error_invalid_mobileNumber));
                    mobileInput.requestFocus();
                }
                else if (Utility.isEmpty(passwordInput)) {
                    passwordInput.setError(getResources().getString(R.string.mandatory_field_message));
                    passwordInput.requestFocus();
                } else if (passwordInput.getText().length() < 6) {
                    passwordInput.setError(getResources().getString(R.string.error_invalid_password));
                    passwordInput.requestFocus();
                }else {
                    doRegistrationServerCall();
                }
                break;
            case R.id.signupTextView:
                launchFragment(new LoginFragment(), false);
                break;
        }
    }
    private void doRegistrationServerCall() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String email = emailInput.getText().toString();
                    String password = passwordInput.getText().toString();
                    String name = userNameInput.getText().toString();
                    String mobile = mobileInput.getText().toString();
                    RegistrationRequest request = new RegistrationRequest();
                    request.setUserName(name);
                    request.setEmailAddress(email);
                    request.setUserMobile(mobile);
                    request.setPassword(password);
                    Call<CommonResponse> call = RetrofitApi.getAppServicesObject().registration(request);
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