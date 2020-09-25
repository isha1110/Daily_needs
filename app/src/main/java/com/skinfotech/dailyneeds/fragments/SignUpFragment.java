package com.skinfotech.dailyneeds.fragments;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.RegistrationRequest;
import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import retrofit2.Call;
import retrofit2.Response;

public class SignUpFragment extends BaseFragment {

    private boolean mIsDoubleBackPress = false;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText userNameInput;
    private EditText mobileInput;
    private boolean isEmailValid;
    private boolean isUsernameValid;
    private boolean isPasswordValid;
    private boolean isMobileValid;
    private static final String TAG = "SignUpFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_signup, container, false);
        ToolBarManager.getInstance().hideToolBar(mActivity, true);
        mActivity.isToggleButtonEnabled(false);
        emailInput = mContentView.findViewById(R.id.userEmail);
        passwordInput = mContentView.findViewById(R.id.password);
        userNameInput = mContentView.findViewById(R.id.userName);
        mobileInput = mContentView.findViewById(R.id.mobileNumber);
        return mContentView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginTextView:
                launchFragment(new LoginFragment(), false);
                break;
            case R.id.createAccount:
                if (Utility.isEmpty(userNameInput)) {
                    userNameInput.setError(getResources().getString(R.string.mandatory_field_message));
                    userNameInput.requestFocus();
                    isUsernameValid = false;
                } else {
                    isUsernameValid = true;
                }
                if (Utility.isEmpty(emailInput)) {
                    emailInput.setError(getResources().getString(R.string.mandatory_field_message));
                    emailInput.requestFocus();
                    isEmailValid = false;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput.getText().toString()).matches()) {
                    emailInput.setError(getResources().getString(R.string.error_invalid_email));
                    emailInput.requestFocus();
                    isEmailValid = false;
                } else {
                    isEmailValid = true;
                }
                if (Utility.isEmpty(mobileInput)) {
                    mobileInput.setError(getResources().getString(R.string.mandatory_field_message));
                    mobileInput.requestFocus();
                    isMobileValid = false;
                } else {
                    isMobileValid = true;
                }
                if (Utility.isEmpty(passwordInput)) {
                    passwordInput.setError(getResources().getString(R.string.mandatory_field_message));
                    passwordInput.requestFocus();
                    isPasswordValid = false;
                } else if (passwordInput.getText().length() < 6) {
                    passwordInput.setError(getResources().getString(R.string.error_invalid_password));
                    passwordInput.requestFocus();
                    isPasswordValid = false;
                } else {
                    isPasswordValid = true;
                }
                if (isEmailValid && isPasswordValid && isUsernameValid && isMobileValid) {
                    doRegistrationServerCall();
                }
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
                    request.setEmailAddress(email);
                    request.setPassword(password);
                    request.setUserName(name);
                    request.setUserMobile(mobile);
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
        hideKeyboard();
    }
}
