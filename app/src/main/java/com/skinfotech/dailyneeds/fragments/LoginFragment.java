package com.skinfotech.dailyneeds.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.LoginRequest;
import com.skinfotech.dailyneeds.models.requests.VerifyOtpRequest;
import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class LoginFragment extends BaseFragment {

    private TextInputEditText mobileNumber;
    private boolean isEmailValid;
    private static final String TAG = "LoginFragment";
    private ConstraintLayout constraintLayout1;
    private ConstraintLayout constraintLayout;
    private EditText otpField1;
    private EditText otpField2;
    private EditText otpField3;
    private EditText otpField4;
    private Button mVerifyOtp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_login, container, false);
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().setHeaderTitle("");
        ToolBarManager.getInstance().onBackPressed(this);
        mActivity.isToggleButtonEnabled(false);
        mobileNumber = mContentView.findViewById(R.id.userPhoneNumber);
        constraintLayout1 = mContentView.findViewById(R.id.constraintContainer);
        constraintLayout = mContentView.findViewById(R.id.constraintLayout);
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
        return mContentView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nextTextView:
                if (Utility.isEmpty(mobileNumber)) {
                    mobileNumber.setError(mActivity.getString(R.string.mandatory_field_message));
                    mobileNumber.requestFocus();
                }
                verifyMobileNumberOnServer();
                break;
        }
    }
    private void verifyMobileNumberOnServer() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    String mobileNumberStr = mobileNumber.getText().toString();
                    Call<CommonResponse> responseCall = RetrofitApi.getAppServicesObject().login(new LoginRequest(mobileNumberStr));
                    final Response<CommonResponse> commonResponse = responseCall.execute();
                    updateOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (commonResponse.isSuccessful()) {
                                CommonResponse response = commonResponse.body();
                                if (response != null && !response.getErrorCode().equalsIgnoreCase(Constants.SUCCESS)) {
                                    storeStringDataInSharedPref(Constants.OTP , response.getOtp());
                                    constraintLayout.setVisibility(View.GONE);
                                    constraintLayout1.setVisibility(View.VISIBLE);
                                } else if (response != null) {
                                   // showToast(mActivity.getString(R.string.msg_no_mobile_number_registered));
                                }
                            }
                            stopProgress();
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                    updateOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopProgress();
                        }
                    });
                }
            }
        }).start();
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
            String mobileNumberStr = mobileNumber.getText().toString();
            showProgress();
            new Thread(() -> {
                try {
                    Call<CommonResponse> call = RetrofitApi.getAppServicesObject().getVerifyOtpResponse(new VerifyOtpRequest(finalEnteredOtp,mobileNumberStr));
                    final Response<CommonResponse> response = call.execute();
                    updateOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            CommonResponse commonResponse = response.body();
                            if (commonResponse != null && commonResponse.getErrorCode().equalsIgnoreCase(Constants.SUCCESS)) {
                                storeStringDataInSharedPref(Constants.USER_ID , commonResponse.getUserId());
                                storeStringDataInSharedPref(Constants.USER_LOGIN_DONE , Constants.YES);
                                launchFragment(new HomeScreenFragment(),false);
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
    public void onStart() {
        super.onStart();
        mActivity.showBackButton();
        mActivity.hideCartIcon();
        mActivity.hideSearchIcon();
        hideKeyboard();
    }
}
