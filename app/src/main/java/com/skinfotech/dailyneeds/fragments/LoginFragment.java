package com.skinfotech.dailyneeds.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.LoginRequest;
import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

import static com.skinfotech.dailyneeds.Constants.OTP;

public class LoginFragment extends BaseFragment {

    private TextInputEditText mobileNumber;
    private static final String TAG = "LoginFragment";
    private EditText otpField1;
    private EditText otpField2;
    private EditText otpField3;
    private EditText otpField4;
    private Button mVerifyOtp;
    private boolean mIsDoubleBackPress = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_login, container, false);
        ToolBarManager.getInstance().hideToolBar(mActivity, true);
        ToolBarManager.getInstance().setHeaderTitle("");
        mActivity.isToggleButtonEnabled(false);
        mobileNumber = mContentView.findViewById(R.id.userPhoneNumber);
       /* otpField1 = mContentView.findViewById(R.id.otp1);
        otpField1.addTextChangedListener(new GenericTextWatcher(otpField1));
        otpField2 = mContentView.findViewById(R.id.otp2);
        otpField2.addTextChangedListener(new GenericTextWatcher(otpField2));
        otpField3 = mContentView.findViewById(R.id.otp3);
        otpField3.addTextChangedListener(new GenericTextWatcher(otpField3));
        otpField4 = mContentView.findViewById(R.id.otp4);
        otpField4.addTextChangedListener(new GenericTextWatcher(otpField4));
        mVerifyOtp = mContentView.findViewById(R.id.verifyOtp);
        mVerifyOtp.setOnClickListener(this);*/
        return mContentView;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()== R.id.nextTextView) {
                if (Utility.isEmpty(mobileNumber)) {
                mobileNumber.setError(mActivity.getString(R.string.mandatory_field_message));
                mobileNumber.requestFocus();
            }
            verifyMobileNumberOnServer();
        }
    }

    private void verifyMobileNumberOnServer() {
        new Thread(() -> {
            try {
                String mobileNumberStr = mobileNumber.getText().toString();
                Call<CommonResponse> responseCall = RetrofitApi.getAppServicesObject().login(new LoginRequest(mobileNumberStr));
                final Response<CommonResponse> commonResponse = responseCall.execute();
                updateOnUiThread(() -> {
                    if (commonResponse.isSuccessful()) {
                        CommonResponse response = commonResponse.body();
                        if (response != null) {
                            if (Constants.SUCCESS.equalsIgnoreCase(response.getErrorCode())) {
                                launchFragment(new OtpFragment(mobileNumberStr), false);
                                storeStringDataInSharedPref(OTP , response.getOtp());
                            }
                        }
                    }
                    stopProgress();
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
        }).start();
    }

   /* public class GenericTextWatcher implements TextWatcher {

        private View view;

        GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            *//*
             *
             * *//*
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            *//*
             *
             * *//*
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
                            mVerifyOtp.requestFocus();
                            CommonResponse commonResponse = response.body();
                            if (commonResponse != null && commonResponse.getErrorCode().equalsIgnoreCase(Constants.SUCCESS)) {
                                storeStringDataInSharedPref(Constants.USER_ID , commonResponse.getUserId());
                                storeStringDataInSharedPref(Constants.USER_LOGIN_DONE , Constants.YES);
                                mVerifyOtp.callOnClick();
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
    }*/

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
