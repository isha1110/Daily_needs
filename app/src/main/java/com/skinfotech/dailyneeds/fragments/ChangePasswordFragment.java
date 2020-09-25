package com.skinfotech.dailyneeds.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.ChangePasswordRequest;
import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import retrofit2.Call;
import retrofit2.Response;

import static com.skinfotech.dailyneeds.Constants.USER_ID;

public class ChangePasswordFragment extends BaseFragment {

    private static final String TAG = "ChangePasswordFragment";
    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private EditText confirmNewPasswordEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_change_password, container, false);
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().setHeaderTitle(mActivity.getString(R.string.change_password));
        ToolBarManager.getInstance().onBackPressed(this);
        mActivity.isToggleButtonEnabled(false);
        oldPasswordEditText = mContentView.findViewById(R.id.oldPassword);
        newPasswordEditText = mContentView.findViewById(R.id.newPassword);
        confirmNewPasswordEditText = mContentView.findViewById(R.id.confirmNewPassword);
        confirmNewPasswordEditText.setEnabled(false);
        newPasswordEditText.addTextChangedListener(new TextWatcher() {
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
                confirmNewPasswordEditText.setEnabled(!Utility.isEmpty(editable.toString()));
            }
        });
        confirmNewPasswordEditText.addTextChangedListener(new TextWatcher() {
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
                String text = editable.toString();
                if (!Utility.isEmpty(text)) {
                    String newPasswordStr = newPasswordEditText.getText().toString();
                    Drawable drawableLeft = ContextCompat.getDrawable(mActivity, R.drawable.password_icon);
                    if (text.length() == newPasswordStr.length() && text.equals(newPasswordStr)) {
                        Drawable drawable = ContextCompat.getDrawable(mActivity, R.drawable.ic_correct);
                        confirmNewPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawable, null);
                    } else {
                        confirmNewPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
                    }
                }
            }
        });
        return mContentView;
    }

    private void changePasswordServerCall() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String oldPassword = oldPasswordEditText.getText().toString();
                    String newPassword = newPasswordEditText.getText().toString();
                    ChangePasswordRequest request = new ChangePasswordRequest();
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    request.setOldPassword(oldPassword);
                    request.setNewPassword(newPassword);
                    Call<CommonResponse> call = RetrofitApi.getAppServicesObject().changePassword(request);
                    final Response<CommonResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                }
            }

            private void handleResponse(Response<CommonResponse> response) {
                if (response.isSuccessful()) {
                    CommonResponse commonResponse = response.body();
                    if (commonResponse != null) {
                        if (commonResponse.getErrorCode().equalsIgnoreCase(Constants.SUCCESS)) {
                            newPasswordEditText.setText("");
                            oldPasswordEditText.setText("");
                            confirmNewPasswordEditText.setText("");
                            launchFragment(new MyProfileFragment(),true);
                        }
                        showToast(commonResponse.getErrorMessage());
                    }
                }
                stopProgress();
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        if (R.id.confirmPassword == view.getId()) {
            String oldPasswordStr = oldPasswordEditText.getText().toString();
            String newPasswordStr = newPasswordEditText.getText().toString();
            String confirmNewPasswordStr = confirmNewPasswordEditText.getText().toString();
            if (Utility.isEmpty(oldPasswordStr)) {
                oldPasswordEditText.setError(getString(R.string.mandatory_field_message));
                oldPasswordEditText.requestFocus();
                return;
            }
            if (Utility.isEmpty(newPasswordStr)) {
                newPasswordEditText.setError(getString(R.string.mandatory_field_message));
                newPasswordEditText.requestFocus();
                return;
            }
            if (Utility.isEmpty(confirmNewPasswordStr)) {
                confirmNewPasswordEditText.setError(getString(R.string.mandatory_field_message));
                confirmNewPasswordEditText.requestFocus();
                return;
            }
            if (!newPasswordStr.equals(confirmNewPasswordStr)) {
                confirmNewPasswordEditText.setError(getString(R.string.password_mismatch_msg));
                confirmNewPasswordEditText.requestFocus();
                return;
            }
            changePasswordServerCall();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.showBackButton();
        mActivity.hideCartIcon();
        mActivity.hideFilterIcon();
    }
}
