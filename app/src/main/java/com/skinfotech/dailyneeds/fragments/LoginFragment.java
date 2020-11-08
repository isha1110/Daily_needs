package com.skinfotech.dailyneeds.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TextView;

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
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.LoginRequest;
import com.skinfotech.dailyneeds.models.requests.RegistrationRequest;
import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Response;

public class LoginFragment extends BaseFragment {

    private TextInputEditText mUserEmailTextInputEditText;
    private TextInputEditText mUserPasswordTextInputEditText;
    private LoginButton loginWithFacebook;
    private SignInButton loginwithGoogle;
    private static final String TAG = "LoginFragment";
    private String email;
    private String password;
    private String name;
    private String mobile;
    //FirebaseAuth Instance
    private FirebaseAuth mAuth;
    //Callback Manager for the FirebaseAuth
    private CallbackManager mCallbackManager;
    //Google SignIN Client
    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 69;

    private Button mLoginButton;
    private TextView mSignupTextView;
    private TextView mForgetPasswordTextView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_login, container, false);
        ToolBarManager.getInstance().hideToolBar(mActivity, true);
        ToolBarManager.getInstance().setHeaderTitle("");
        mActivity.isToggleButtonEnabled(false);
        FacebookSdk.sdkInitialize(mActivity);
        //intialising auth instance
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mActivity.getApplicationContext(), gso);

        loginwithGoogle = mContentView.findViewById(R.id.googleLoginTextView);
        loginWithFacebook = mContentView.findViewById(R.id.facebookLoginTextView);

        loginWithFacebook.setOnClickListener(this);
        loginWithFacebook.setFragment(this);
        loginwithGoogle.setOnClickListener(this);
        CardView cardView = mContentView.findViewById(R.id.appLogoCardView);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.blue));
        mUserEmailTextInputEditText = mContentView.findViewById(R.id.userEmail);
        mUserPasswordTextInputEditText = mContentView.findViewById(R.id.userPassword);
        mLoginButton = mContentView.findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(this);
        mSignupTextView = mContentView.findViewById(R.id.signupTextView);
        mSignupTextView.setOnClickListener(this);
        mForgetPasswordTextView = mContentView.findViewById(R.id.forgetPasswordTextView);
        mForgetPasswordTextView.setOnClickListener(this);
        return mContentView;
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginButton:
                email = mUserEmailTextInputEditText.getText().toString();
                password = mUserPasswordTextInputEditText.getText().toString();
            
//                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            
                if (Utility.isEmpty(mUserEmailTextInputEditText)) {
                    mUserEmailTextInputEditText.setError(mActivity.getString(R.string.mandatory_field_message));
                    mUserEmailTextInputEditText.requestFocus();
                    return;
                }
                if (Utility.isEmpty(mUserPasswordTextInputEditText)) {
                    mUserPasswordTextInputEditText.setError(mActivity.getString(R.string.mandatory_field_message));
                    mUserPasswordTextInputEditText.requestFocus();
                    return;
                }
                if (mUserPasswordTextInputEditText.getText().length() < 6) {
                    mUserPasswordTextInputEditText.setError(getResources().getString(R.string.error_invalid_password));
                    mUserPasswordTextInputEditText.requestFocus();
                    return;
                }
                    doLoginServerCall();
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
        showProgress();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signInWithFacebook() {
        showProgress();
        LoginManager.getInstance().logInWithReadPermissions(mActivity, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {
                Toast.makeText(mActivity, "Authentication Cancelled by the User!", Toast.LENGTH_SHORT).show();
                stopProgress();
            }
            @Override
            public void onError(FacebookException error) {
                Toast.makeText(mActivity, "Oops! Something Went Wrong.", Toast.LENGTH_SHORT).show();
                stopProgress();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN && checkGoogleLogin()) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(mActivity, "Successfully Login", Toast.LENGTH_SHORT).show();
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            Toast.makeText(mActivity, "Login Failed", Toast.LENGTH_SHORT).show();
            stopProgress();
        }
    }

    private Boolean checkGoogleLogin() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        return account != null;
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        name = user.getDisplayName();
                        mobile = user.getPhoneNumber();
                        email = user.getEmail();
                        doSocialLoginRequest(Constants.AuthModes.GOOGLE_AUTH);
                    } else {
                        Toast.makeText(mActivity, "Oops! Something Went Wrong.",
                                Toast.LENGTH_SHORT).show();
                        stopProgress();
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(mActivity, "Login Successfull!", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        name = user.getDisplayName();
                        mobile = user.getPhoneNumber();
                        email = user.getEmail();
                        doSocialLoginRequest(Constants.AuthModes.FACEBOOK_AUTH);
                    } else {
                        Toast.makeText(mActivity, "Oops! Something Went Wrong.",
                                Toast.LENGTH_SHORT).show();
                        stopProgress();
                    }
                });
    }

    private void doSocialLoginRequest(String authMode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RegistrationRequest request = new RegistrationRequest();
                    request.setUserName(name);
                    request.setEmailAddress(email);
                    request.setUserMobile(mobile);
                    request.setmMode(authMode);
                    Call<CommonResponse> call = RetrofitApi.getAppServicesObject().registration(request);
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
                        if (Constants.SUCCESS.equalsIgnoreCase(commonResponse.getErrorCode())) {
                            storeStringDataInSharedPref(Constants.USER_ID , commonResponse.getUserId());
                            storeStringDataInSharedPref(Constants.USER_LOGIN_DONE , Constants.YES);
                            storeStringDataInSharedPref(Constants.USER_EMAIL, commonResponse.getUserEmail());
                            storeStringDataInSharedPref(Constants.USER_MOBILE, commonResponse.getUserMobile());
                            storeStringDataInSharedPref(Constants.USER_MODE, authMode);
                            launchFragment(new HomeScreenFragment(), false);
                        }
                        showToast(commonResponse.getErrorMessage());
                    }
                }
                stopProgress();
            }
        }).start();
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
                            storeStringDataInSharedPref(Constants.USER_MODE, Constants.AuthModes.EMAIL_AUTH);
                            launchFragment(new HomeScreenFragment(), false);
                        }
                        showToast(commonResponse.getErrorMessage());
                    }
                }
                stopProgress();
            }
        }).start();
    }
}
