package com.skinfotech.dailyneeds.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.LoginRequest;
import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Response;

public class LoginFragment extends BaseFragment {

    private TextInputEditText mUserEmailTextInputEditText;
    private TextInputEditText mUserPasswordTextInputEditText;
    private TextView loginWithFacebook;
    private TextView loginwithGoogle;
    private static final String TAG = "LoginFragment";
    private boolean mIsDoubleBackPress = false;
    private String email;
    private String password;

    //FirebaseAuth Instance
    private FirebaseAuth mAuth;
    //Callback Manager for the FirebaseAuth
    private CallbackManager mCallbackManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_login, container, false);
        ToolBarManager.getInstance().hideToolBar(mActivity, true);
        ToolBarManager.getInstance().setHeaderTitle("");
        mActivity.isToggleButtonEnabled(false);
        FacebookSdk.sdkInitialize(mActivity.getApplicationContext());
        //intialising auth instance
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        loginwithGoogle = mContentView.findViewById(R.id.googleLoginTextView);
        loginWithFacebook = mContentView.findViewById(R.id.facebookLoginTextView);
        loginWithFacebook.setOnClickListener(this);

        loginwithGoogle.setOnClickListener(this);
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
            case R.id.facebookLoginTextView:
                signInWithFacebook();
                break;
            case R.id.googleLoginTextView:
                signInWithGoogle();
                break;
        }
    }

    private void signInWithGoogle() {
        Toast.makeText(mActivity, "in Development Login with Google", Toast.LENGTH_SHORT).show();
    }

    private void signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(mActivity, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FacebookSuccess", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("FacebookCancel", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FacebookError", "facebook:onError", error);
                // ...
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(mActivity, "Login Successfull!", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(mActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
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
