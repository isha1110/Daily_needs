package com.skinfotech.dailyneeds.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.ForgotPasswordRequest;
import com.skinfotech.dailyneeds.models.requests.VerifyEmailRequest;
import com.skinfotech.dailyneeds.models.requests.VerifyOtpRequest;
import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class ForgotPasswordFragment extends BaseFragment {

    private EditText otpField1;
    private EditText otpField2;
    private EditText otpField3;
    private EditText otpField4;
    private EditText emailInput;
    private EditText newPasswordInput;
    private EditText confirmNewPasswordInput;
    private ConstraintLayout mOtpContainer;
    private ConstraintLayout mChangePasswordContainer;
    private static final String TAG = "ForgotPasswordFragment";
    private Button mVerifyOtp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().setHeaderTitle(mActivity.getString(R.string.forgetPassword));
        ToolBarManager.getInstance().onBackPressed(this);
        mActivity.isToggleButtonEnabled(false);
        emailInput = mContentView.findViewById(R.id.userEmail_number);
        newPasswordInput = mContentView.findViewById(R.id.newPassword);
        confirmNewPasswordInput = mContentView.findViewById(R.id.confirmNewPassword);
        mOtpContainer = mContentView.findViewById(R.id.otpContainer);
        mChangePasswordContainer = mContentView.findViewById(R.id.changePasswordContainer);
        otpField1 = mContentView.findViewById(R.id.otp1);
        otpField1.addTextChangedListener(new GenericTextWatcher(otpField1));
        otpField2 = mContentView.findViewById(R.id.otp2);
        otpField2.addTextChangedListener(new GenericTextWatcher(otpField2));
        otpField3 = mContentView.findViewById(R.id.otp3);
        otpField3.addTextChangedListener(new GenericTextWatcher(otpField3));
        otpField4 = mContentView.findViewById(R.id.otp4);
        otpField4.addTextChangedListener(new GenericTextWatcher(otpField4));
        mVerifyOtp = mContentView.findViewById(R.id.verifyOtp);
        mVerifyOtp.setOnClickListener(this);
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "beforeTextChanged: ");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "onTextChanged: ");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                emailInput.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                mOtpContainer.setVisibility(View.GONE);
                mChangePasswordContainer.setVisibility(View.GONE);
            }
        });
        return mContentView;
    }

    private void freezeValuesOfViews() {
        otpField1.setEnabled(false);
        otpField2.setEnabled(false);
        otpField3.setEnabled(false);
        otpField4.setEnabled(false);
    }

    public class GenericTextWatcher implements TextWatcher {

        private View view;

        GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            /*
             *
             * */
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            /*
             *
             * */
        }

        @Override
        public void afterTextChanged(Editable s) {
            String otpText = s.toString();
            switch (view.getId()) {
                case R.id.otp1:
                    if (otpText.length() == 1) {
                        otpField2.requestFocus();
                    }
                    break;
                case R.id.otp2:
                    if (otpText.length() == 1) {
                        otpField3.requestFocus();
                    } else if (otpText.length() == 0) {
                        otpField1.requestFocus();
                    }
                    break;
                case R.id.otp3:
                    if (otpText.length() == 1) {
                        otpField4.requestFocus();
                    } else if (otpText.length() == 0) {
                        otpField2.requestFocus();
                    }
                    break;
                case R.id.otp4:
                    if (otpText.length() == 0) {
                        otpField3.requestFocus();
                    }
                    verifyOtpServerCall();
                    break;
                default:
                    break;
            }
        }

        private void verifyOtpServerCall() {
            final String finalEnteredOtp = otpField1.getText().toString() + otpField2.getText().toString() + otpField3.getText().toString()
                    + otpField4.getText().toString();
            String emailStr = emailInput.getText().toString();
            if (Utility.isEmpty(emailStr)) {
                emailInput.setError(getString(R.string.mandatory_field_message));
                emailInput.requestFocus();
                return;
            }
            showProgress();
            new Thread(() -> {
                try {
                    Call<CommonResponse> call = RetrofitApi.getAppServicesObject().getVerifyOtpResponse(new VerifyOtpRequest(finalEnteredOtp, emailStr));
                    final Response<CommonResponse> response = call.execute();
                    updateOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            CommonResponse commonResponse = response.body();
                            mVerifyOtp.requestFocus();
                            if (commonResponse != null && commonResponse.getErrorCode().equalsIgnoreCase(Constants.SUCCESS)) {
                                mChangePasswordContainer.setVisibility(View.VISIBLE);
                                freezeValuesOfViews();
                            } else if (commonResponse != null) {
                                showToast(commonResponse.getErrorMessage());
                            }
                        }
                        stopProgress();
                    });
                } catch (IOException e) {
                    stopProgress();
                    Log.e(TAG, e.getMessage(), e);
                }
            }).start();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButton:
                launchFragment(new LoginFragment(), true);
                break;
            case R.id.verifyButton:
                if (Utility.isEmpty(emailInput)) {
                    emailInput.setError(getString(R.string.mandatory_field_message));
                    emailInput.requestFocus();
                    return;
                }
                verifyEmailServerCall();
                break;
            case R.id.generatePassword:
                String passwordStr = newPasswordInput.getText().toString().trim();
                String oldPasswordStr = confirmNewPasswordInput.getText().toString().trim();
                if (Utility.isEmpty(passwordStr)) {
                    newPasswordInput.setError(getString(R.string.mandatory_field_message));
                    newPasswordInput.requestFocus();
                    return;
                }
                if (Utility.isEmpty(oldPasswordStr)) {
                    confirmNewPasswordInput.setError(getString(R.string.mandatory_field_message));
                    confirmNewPasswordInput.requestFocus();
                    return;
                }
                if (!passwordStr.equals(oldPasswordStr)) {
                    confirmNewPasswordInput.setError(getString(R.string.password_matching_msg));
                    confirmNewPasswordInput.requestFocus();
                    return;
                }
                updatePasswordServerCall(passwordStr);
                break;
        }
    }

    private void verifyEmailServerCall() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String email = emailInput.getText().toString();
                    Call<CommonResponse> call = RetrofitApi.getAppServicesObject().verifyEmail(new VerifyEmailRequest(email));
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
                            Drawable drawable = ContextCompat.getDrawable(mActivity, R.drawable.ic_correct);
                            emailInput.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                            emailInput.setEnabled(false);
                            mOtpContainer.setVisibility(View.VISIBLE);
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
        mActivity.showBackButton();
        mActivity.hideCartIcon();
    }

    private void updatePasswordServerCall(String passwordStr) {
        showProgress();
        new Thread(() -> {
            try {
                String email = emailInput.getText().toString();
                ForgotPasswordRequest request = new ForgotPasswordRequest();
                request.setEmailAddress(email);
                request.setNewPassword(passwordStr);
                Call<CommonResponse> call = RetrofitApi.getAppServicesObject().updatePassword(request);
                final Response<CommonResponse> response = call.execute();
                updateOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        CommonResponse commonResponse = response.body();
                        mVerifyOtp.requestFocus();
                        if (commonResponse != null) {
                            if (commonResponse.getErrorCode().equalsIgnoreCase(Constants.SUCCESS)) {
                                launchFragment(new LoginFragment(), true);
                            }
                            showToast(commonResponse.getErrorMessage());
                        }
                    }
                    stopProgress();
                });
            } catch (IOException e) {
                stopProgress();
                Log.e(TAG, e.getMessage(), e);
            }
        }).start();
    }
}
