package com.skinfotech.dailyneeds;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.fragments.BaseFragment;
import com.skinfotech.dailyneeds.fragments.CartFragment;
import com.skinfotech.dailyneeds.fragments.CategoryListFrgament;
import com.skinfotech.dailyneeds.fragments.HomeScreenFragment;
import com.skinfotech.dailyneeds.fragments.LoginFragment;
import com.skinfotech.dailyneeds.fragments.MyOrderFragment;
import com.skinfotech.dailyneeds.fragments.SearchFragment;
import com.skinfotech.dailyneeds.fragments.SelectAddressFragment;
import com.skinfotech.dailyneeds.fragments.SignUpFragment;
import com.skinfotech.dailyneeds.models.requests.AddressResponse;
import com.skinfotech.dailyneeds.models.requests.CommonRequest;
import com.skinfotech.dailyneeds.models.requests.DefaultAddressRequest;
import com.skinfotech.dailyneeds.models.requests.SaveAddressRequest;
import com.skinfotech.dailyneeds.models.requests.TokenRequest;
import com.skinfotech.dailyneeds.models.responses.CommonDetailsResponse;
import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.skinfotech.dailyneeds.models.responses.LocationResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Response;

import static com.skinfotech.dailyneeds.Constants.SHARED_PREF_NAME;
import static com.skinfotech.dailyneeds.Constants.USER_EMAIL;
import static com.skinfotech.dailyneeds.Constants.USER_MOBILE;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PaymentResultListener {

    private ActionBarDrawerToggle mToggleButton;
    private DrawerLayout mSideNavigationDrawer;
    private Checkout mCheckoutInstance = new Checkout();
    private static final String TAG = "HomeActivity";
    private View headerView;
    private BottomSheetDialog bottomSheetDialog;
    private RecyclerView selectAddressRecyclerView;
    private SelectAddressListAdapter mSelectAddressListAdapter;
    private String mSelectedAddressId = "";
    private TextView selectDeliveryAddress;
    private TextView addNewAddressCheckout;
    private ConstraintLayout formConstraintLayout;
    private Spinner mSelectLocation;
    private EditText enterAddressText;
    private EditText enterAddressText1;
    private EditText nameNewAddress;
    private EditText phoneNewAddress;
    private EditText cityAddress;
    private EditText stateAddress;
    private EditText pincodeAddress;
    private TextView makeDefaultTextView;
    private List<AddressResponse.AddressItem> mAddressResponseList = new ArrayList<>();
    private AddressResponse addressResponse;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        Toolbar toolbar = findViewById(R.id.toolbarLayout);
        setSupportActionBar(toolbar);
        ToolBarManager.getInstance().setupToolbar(toolbar);
        findViewById(R.id.backButtonToolbar).setVisibility(View.GONE);
        mSideNavigationDrawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        launchFragment(new LoginFragment(), false);
        headerView = navigationView.getHeaderView(0);
        mToggleButton = new ActionBarDrawerToggle(
                this, mSideNavigationDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mSideNavigationDrawer.addDrawerListener(mToggleButton);
        mToggleButton.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite));
        mToggleButton.syncState();
        mAuth = FirebaseAuth.getInstance();
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    String fcmToken = task.getResult().getToken();
                    Log.d(TAG, "onComplete: " + fcmToken);
                    saveFcmTokenOnServer(fcmToken);
                }
            }

            private void saveFcmTokenOnServer(final String fcmToken) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String userId = getCurrentFragment().getStringDataFromSharedPref(Constants.USER_ID);
                            TokenRequest request = new TokenRequest(userId, fcmToken);
                            Call<CommonResponse> call = RetrofitApi.getAppServicesObject().saveFcmTokenServerCall(request);
                            final Response<CommonResponse> response = call.execute();
                            handleFcmResponse(response);
                        } catch (final Exception e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }

                    private void handleFcmResponse(Response<CommonResponse> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "handleFcmResponse: Successful");
                        }
                    }
                }).start();
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        SharedPreferences preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        if (Constants.YES.equalsIgnoreCase(preferences.getString(Constants.USER_LOGIN_DONE, Constants.NO))) {
            if(!preferences.getString(Constants.USER_MODE, "").equals(Constants.AuthModes.EMAIL_AUTH)){
                if(mAuth.getCurrentUser() != null){
                    launchFragment(new HomeScreenFragment(), false);
                }
                else {
                    launchFragment(new LoginFragment(), false);
                }
            }
            else{
                launchFragment(new HomeScreenFragment(), false);
            }
        } else {
            launchFragment(new LoginFragment(), false);
        }
        fetchAddressServerCall();
        findViewById(R.id.searchTextView).setOnClickListener(v -> launchFragment(new SearchFragment(), true));
        findViewById(R.id.constraintContainer).setOnClickListener(v -> {
            fetchAddressServerCall();
            bottomSheetDialog.show();
        });
        addressBottomSheet();
    }


    public void addressBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottomsheet_select_address_list);
        selectAddressRecyclerView = bottomSheetDialog.findViewById(R.id.selectAddressListRecycler);
        mSelectAddressListAdapter = new SelectAddressListAdapter();
        selectAddressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectAddressRecyclerView.setAdapter(mSelectAddressListAdapter);
        selectDeliveryAddress = bottomSheetDialog.findViewById(R.id.selectDeliveryAddress);
        addNewAddressCheckout = bottomSheetDialog.findViewById(R.id.addNewAddressCheckout);
        selectAddressRecyclerView = bottomSheetDialog.findViewById(R.id.selectAddressListRecycler);
        selectAddressRecyclerView.setVisibility(View.VISIBLE);
        formConstraintLayout = bottomSheetDialog.findViewById(R.id.newAddressConstraintBottomSheet);
        formConstraintLayout.setVisibility(View.GONE);
        nameNewAddress = bottomSheetDialog.findViewById(R.id.newAddressName);
        makeDefaultTextView = bottomSheetDialog.findViewById(R.id.makeDefaultButton);
        phoneNewAddress = bottomSheetDialog.findViewById(R.id.newAddressPhone);
        enterAddressText = bottomSheetDialog.findViewById(R.id.enterAddressText);
        enterAddressText1 = bottomSheetDialog.findViewById(R.id.enterAddressText1);
        cityAddress = bottomSheetDialog.findViewById(R.id.cityNewAddress);
        stateAddress = bottomSheetDialog.findViewById(R.id.stateNewAddress);
        pincodeAddress = bottomSheetDialog.findViewById(R.id.pincodeNewAddress);
        mSelectLocation = bottomSheetDialog.findViewById(R.id.selectLocation);
        getLocationsResponseServerCall();
        makeDefaultTextView.setOnClickListener(v -> {
            if (Utility.isEmpty(mSelectedAddressId)) {
                showToast(getString(R.string.default_address_msg));
                return;
            }
            makeDefaultAddress(mSelectedAddressId);
        });

    }

    private boolean chkValidations() {

        if (nameNewAddress.getText().toString().isEmpty()) {
            nameNewAddress.setError(getString(R.string.mandatory_address1_field));
            nameNewAddress.requestFocus();
            return false;
        }
        if (phoneNewAddress.getText().toString().length() < 10 || phoneNewAddress.getText().toString().length() > 10) {
            phoneNewAddress.setError(getString(R.string.length_mobile_field));
            phoneNewAddress.requestFocus();
            return false;
        }
        if (enterAddressText.getText().toString().isEmpty()) {
            enterAddressText.setError(getString(R.string.mandatory_address1_field));
            enterAddressText.requestFocus();
            return false;
        }
        if (enterAddressText1.getText().toString().isEmpty()) {
            enterAddressText1.setError(getString(R.string.mandatory_address2_field));
            enterAddressText1.requestFocus();
            return false;
        }
        if (cityAddress.getText().toString().isEmpty()) {
            cityAddress.setError(getString(R.string.mandatory_city_field));
            cityAddress.requestFocus();
            return false;
        }
        if (stateAddress.getText().toString().isEmpty()) {
            stateAddress.setError(getString(R.string.mandatory_state_field));
            stateAddress.requestFocus();
            return false;
        }
        if (pincodeAddress.getText().toString().length() > 6 || pincodeAddress.getText().toString().length() < 6) {
            pincodeAddress.setError(getString(R.string.length_pincode_field));
            pincodeAddress.requestFocus();
            return false;
        }
        return true;
    }

    private void saveAddressServerCall(String mode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String userId = getCurrentFragment().getStringDataFromSharedPref(Constants.USER_ID);
                    String name = nameNewAddress.getText().toString();
                    String mobile = phoneNewAddress.getText().toString();
                    String address = enterAddressText.getText().toString();
                    String address1 = enterAddressText1.getText().toString();
                    String city = cityAddress.getText().toString();
                    String state = stateAddress.getText().toString();
                    String pincode = pincodeAddress.getText().toString();
                    String location = mSelectLocation.getSelectedItem().toString();
                    SaveAddressRequest request = new SaveAddressRequest(mode);
                    request.setmUserId(userId);
                    request.setName(name);
                    request.setPhoneNumber(mobile);
                    request.setmAddress(address);
                    request.setmAddress1(address1);
                    request.setmLocation(location);
                    request.setCity(city);
                    request.setState(state);
                    request.setPincode(pincode);
                    Call<CommonResponse> call = RetrofitApi.getAppServicesObject().saveAddress(request);
                    final Response<CommonResponse> response = call.execute();
                    runOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    showToast(e.getMessage());
                }
            }

            private void handleResponse(Response<CommonResponse> response) {
                if (response.isSuccessful()) {
                    CommonResponse commonResponse = response.body();
                    if (commonResponse != null) {
                        showToast(commonResponse.getErrorMessage());
                        if (commonResponse.getErrorCode().equalsIgnoreCase(Constants.SUCCESS)) {
                                Toast.makeText(getApplicationContext(), commonResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                                fetchAddressServerCall();
                                formConstraintLayout.setVisibility(View.GONE);
                                selectAddressRecyclerView.setVisibility(View.VISIBLE);
                                makeDefaultTextView.setVisibility(View.VISIBLE);
                                selectDeliveryAddress.setText(getString(R.string.select_delivery_address));
                                addNewAddressCheckout.setText(getString(R.string.add_new_address));

                        }
                    }
                }
            }
        }).start();
    }

    private void makeDefaultAddress(String addressId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String userId = getCurrentFragment().getStringDataFromSharedPref(Constants.USER_ID);
                    DefaultAddressRequest request = new DefaultAddressRequest();
                    request.setUserId(userId);
                    request.setAddressId(addressId);
                    Call<AddressResponse> call = RetrofitApi.getAppServicesObject().makeDefaultAddress(request);
                    final Response<AddressResponse> response = call.execute();
                    runOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    showToast(e.getMessage());
                }
            }

            private void handleResponse(Response<AddressResponse> response) {
                if (response.isSuccessful()) {
                    AddressResponse addressResponse = response.body();
                    if (addressResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(addressResponse.getErrorCode())) {
                            if (!Utility.isEmpty(mAddressResponseList)) {
                                mAddressResponseList.clear();
                            }
                            mAddressResponseList = addressResponse.getAddressList();
                            mSelectAddressListAdapter.setAddressList(mAddressResponseList);
                            mSelectAddressListAdapter.notifyDataSetChanged();
                            showToast(addressResponse.getErrorMessage());
                            launchFragment(new HomeScreenFragment(), false);
                            bottomSheetDialog.dismiss();
                        }
                    }
                }
            }
        }).start();
    }

    public void isToggleButtonEnabled(boolean isEnable) {
        mToggleButton.setDrawerIndicatorEnabled(isEnable);
        if (isEnable) {
            mSideNavigationDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mSideNavigationDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    public void hideBackButton() {
        findViewById(R.id.backButtonToolbar).setVisibility(View.GONE);
    }

    public void showBackButton() {
        findViewById(R.id.backButtonToolbar).setVisibility(View.VISIBLE);
    }

    public void hideCartIcon() {
        findViewById(R.id.cartCountTextView).setVisibility(View.GONE);
        findViewById(R.id.cartImageView).setVisibility(View.GONE);
    }

    public void startPayment(String cartPayableAmountStr) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        final Activity activity = this;
        try {
            if (BuildConfig.DEBUG) {
                mCheckoutInstance.setKeyID(getString(R.string.razor_pay_test_key));
            } else {
                mCheckoutInstance.setKeyID(getString(R.string.razor_pay_live_key));
            }
            double priceDouble = Double.parseDouble(cartPayableAmountStr);
            double finalAmount = Double.parseDouble(new DecimalFormat("##.##").format(priceDouble));
            String finalAmountToBePaid = String.valueOf(finalAmount * 100).contains(".") ? String.valueOf(finalAmount * 100).substring(0, String.valueOf(finalAmount * 100).indexOf('.')) : String.valueOf(finalAmount * 100);
            JSONObject options = new JSONObject();
            options.put("name", getString(R.string.app_name));
            options.put("description", "Reference No. #" + new Random(6).nextInt());
            options.put("image", "https://dailyneedsandsweets.com/panel/assets/images/logo.png");
            options.put("currency", "INR");
            options.put("amount", finalAmountToBePaid);
            options.put("prefill.email", prefs.getString(USER_EMAIL, ""));
            options.put("prefill.contact", prefs.getString(USER_MOBILE, ""));
            mCheckoutInstance.open(activity, options);
        } catch (Exception e) {

            Log.e(TAG, "Error in starting Razor-pay Checkout", e);
        }
    }

    public void showCartIcon() {
        findViewById(R.id.cartCountTextView).setVisibility(View.VISIBLE);
        findViewById(R.id.cartImageView).setVisibility(View.VISIBLE);
    }

    public void showSearchIcon() {
        findViewById(R.id.constraintLayout8).setVisibility(View.VISIBLE);
        findViewById(R.id.editImage).setVisibility(View.VISIBLE);
        findViewById(R.id.deliveryLocationTextView).setVisibility(View.VISIBLE);
    }

    public void hideSearchIcon() {
        findViewById(R.id.constraintLayout8).setVisibility(View.GONE);
        findViewById(R.id.editImage).setVisibility(View.GONE);
        findViewById(R.id.deliveryLocationTextView).setVisibility(View.GONE);
    }

    public void setCartCount(String count) {
        TextView view = findViewById(R.id.cartCountTextView);
        if (count == null) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
            view.setText(count);
        }
    }

    public void updateLocationHeaderAddress(CommonDetailsResponse response) {
        TextView locationNameHeader = findViewById(R.id.AppTitle);
        TextView mUserNameTextView = headerView.findViewById(R.id.userNameTextView);
        TextView mCustomerSupportMobileNumber = findViewById(R.id.customerSupportMobileNumber);
        locationNameHeader.setText(Utility.toCamelCase(response.getLocation()));
        mUserNameTextView.setText(response.getNameStr());
        mCustomerSupportMobileNumber.setText(response.getMobileNumber());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                mSideNavigationDrawer.closeDrawer(GravityCompat.START);
                if(!getCurrentFragment().getStringDataFromSharedPref(Constants.USER_MODE).equals(Constants.AuthModes.EMAIL_AUTH)){
                    mAuth.signOut();
                }
                getCurrentFragment().storeStringDataInSharedPref(Constants.USER_MODE, "");
                getCurrentFragment().storeStringDataInSharedPref(Constants.USER_LOGIN_DONE, Constants.NO);
                getCurrentFragment().storeStringDataInSharedPref(Constants.USER_ID, "");
                launchFragment(new LoginFragment(), false);
                break;
            case R.id.nav_my_cart:
                launchFragment(new CartFragment(), true);
                break;
            case R.id.nav_myOrders:
                launchFragment(new MyOrderFragment(), true);
                break;
            case R.id.nav_myAddress:
                launchFragment(new SelectAddressFragment(), true);
                break;
            default:
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showToast(final String msg) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show());
    }

    private boolean mIsDoubleBackPress = false;

    @Override
    public void onBackPressed() {
        if (mSideNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
            mSideNavigationDrawer.closeDrawer(GravityCompat.START);
            return;
        }

        Fragment myFragment = getSupportFragmentManager().findFragmentById(R.id.homeFrame);
        if (myFragment != null && myFragment instanceof HomeScreenFragment) {
            if (mIsDoubleBackPress) {
                super.onBackPressed();
            }
            Snackbar.make(headerView, getString(R.string.back_press_msg), Snackbar.LENGTH_SHORT).show();
            mIsDoubleBackPress = true;
            new Handler().postDelayed(() -> mIsDoubleBackPress = false, 1500);
        } else if (myFragment != null && myFragment instanceof LoginFragment) {
            if (mIsDoubleBackPress) {
                super.onBackPressed();
            }
            Snackbar.make(headerView, getString(R.string.back_press_msg), Snackbar.LENGTH_SHORT).show();
            mIsDoubleBackPress = true;
            new Handler().postDelayed(() -> mIsDoubleBackPress = false, 1500);
        } else if (myFragment != null && myFragment instanceof SignUpFragment) {
            if (mIsDoubleBackPress) {
                super.onBackPressed();
            }
            Snackbar.make(headerView, getString(R.string.back_press_msg), Snackbar.LENGTH_SHORT).show();
            mIsDoubleBackPress = true;
            new Handler().postDelayed(() -> mIsDoubleBackPress = false, 1500);
        } else {
            BaseFragment current = getCurrentFragment();
            if (current.onBackPressed()) {
                return;
            }

            FragmentManager manager = getSupportFragmentManager();
            if (manager.getBackStackEntryCount() > 0) {
                super.onBackPressed();
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cartImageView:
                launchFragment(new CartFragment(), true);
                break;
            case R.id.shareTextView:
                shareApplication();
                break;
            case R.id.rateUsTextView:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.skinfotech.dailyneeds"));
                startActivity(intent);
                break;
            case R.id.categoryTextView:
                launchFragment(new CategoryListFrgament(), true);
                break;
            case R.id.confirm:
                if (chkValidations()) {
                    saveAddressServerCall(Constants.AddressModes.NEW_ADDRESS);
                }
                break;
            case R.id.addNewAddressCheckout:
                if (formConstraintLayout.getVisibility() == View.GONE) {
                    formConstraintLayout.setVisibility(View.VISIBLE);
                    selectAddressRecyclerView.setVisibility(View.GONE);
                    makeDefaultTextView.setVisibility(View.GONE);
                    selectDeliveryAddress.setText(getString(R.string.add_new_address));
                    addNewAddressCheckout.setText(getString(R.string.select_delivery_address));
                } else {
                    formConstraintLayout.setVisibility(View.GONE);
                    makeDefaultTextView.setVisibility(View.VISIBLE);
                    selectAddressRecyclerView.setVisibility(View.VISIBLE);
                    selectDeliveryAddress.setText(getString(R.string.select_delivery_address));
                    addNewAddressCheckout.setText(getString(R.string.add_new_address));
                }
                break;
            default:
                BaseFragment fragment = getCurrentFragment();
                if (null != fragment) {
                    fragment.onClick(v);
                }
                break;
        }
    }

    public BaseFragment getCurrentFragment() {
        FragmentManager mgr = getSupportFragmentManager();
        List<Fragment> list = mgr.getFragments();
        int count = mgr.getBackStackEntryCount();
        if (0 == count) {
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof BaseFragment) {
                        return (BaseFragment) list.get(i);
                    }
                }
            }
            return null;
        }
        FragmentManager.BackStackEntry entry = mgr.getBackStackEntryAt(count - 1);
        return (BaseFragment) mgr.findFragmentByTag(entry.getName());
    }

    public void launchFragment(final Fragment fragment, final boolean addBackStack) {
        runOnUiThread(() -> doSwitchToScreen(fragment, addBackStack));
    }

    private void doSwitchToScreen(Fragment fragment, boolean addToBackStack) {
        if (null == fragment) {
            return;
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        String fragmentTag = fragment.getClass().getCanonicalName();
        try {
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            fragmentTransaction.replace(R.id.homeFrame, fragment, fragmentTag);
            if (addToBackStack) {
                fragmentTransaction.addToBackStack(fragmentTag);
            }
            fragmentTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            Log.e("doSwitchToScreen ", e.getMessage(), e);
            try {
                fragmentTransaction.replace(R.id.homeFrame, fragment, fragmentTag);
                if (addToBackStack) {
                    fragmentTransaction.addToBackStack(fragmentTag);
                }
                fragmentTransaction.commitAllowingStateLoss();
            } catch (Exception e2) {
                Log.e("doSwitchToScreen", e.getMessage(), e);
            }
        }
    }

    @Override
    public void onPaymentSuccess(String txnId) {
        if (getCurrentFragment() != null) {
            getCurrentFragment().onPaymentSuccess(txnId);
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        if (getCurrentFragment() != null) {
            getCurrentFragment().onPaymentError(i, s);
        }
    }

    private void fetchAddressServerCall() {
        BaseFragment currentFragment = getCurrentFragment();
        if (null == currentFragment) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String userId = currentFragment.getStringDataFromSharedPref(Constants.USER_ID);
                    Call<AddressResponse> call = RetrofitApi.getAppServicesObject().fetchAddress(new CommonRequest(userId));
                    final Response<AddressResponse> response = call.execute();
                    runOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<AddressResponse> response) {
                if (response.isSuccessful()) {
                    addressResponse = response.body();
                    if (addressResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(addressResponse.getErrorCode())) {
                            if (!Utility.isEmpty(mAddressResponseList)) {
                                mAddressResponseList.clear();
                            }
                            mAddressResponseList = addressResponse.getAddressList();
                            mSelectAddressListAdapter.setAddressList(mAddressResponseList);
                            mSelectAddressListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }).start();
    }

    private void getLocationsResponseServerCall() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<LocationResponse> call = RetrofitApi.getAppServicesObject().getLocationsResponse();
                    final Response<LocationResponse> response = call.execute();
                    runOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<LocationResponse> response) {
                if (response.isSuccessful()) {
                    LocationResponse commonResponse = response.body();
                    if (commonResponse != null && Constants.SUCCESS.equalsIgnoreCase(commonResponse.getErrorCode())) {
                        List<LocationResponse.LocationItem> responseList = new ArrayList<>();
                        responseList.clear();
                        responseList = commonResponse.getLocationList();
                        String[] responseStringArray = new String[responseList.size()];
                        for (int position = 0; position < responseList.size(); position++) {
                            responseStringArray[position] = responseList.get(position).getLocationName();
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(HomeActivity.this, R.layout.custom_spinner, responseStringArray);
                        arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropbox);
                        mSelectLocation.setAdapter(arrayAdapter);
                    } else if (commonResponse != null) {
                        showToast(commonResponse.getErrorMessage());
                    }
                }
            }
        }).start();
    }

    private class SelectAddressListAdapter extends RecyclerView.Adapter<SelectAddressListAdapter.RecyclerViewHolder> {

        private List<AddressResponse.AddressItem> mAddressList = new ArrayList<>();

        public void setAddressList(List<AddressResponse.AddressItem> addressList) {
            mAddressList = addressList;
        }

        @NonNull
        @Override
        public SelectAddressListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_address_list, parent, false);
            return new SelectAddressListAdapter.RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SelectAddressListAdapter.RecyclerViewHolder holder, int position) {
            AddressResponse.AddressItem currentItem = mAddressList.get(position);
            holder.selectAddressListButton.setText(currentItem.getNameStr() + getString(R.string.back_slash) + currentItem.getMobileStr());
            holder.addressTextView.setText(currentItem.getAddressStr() + currentItem.getLocationStr());
            holder.selectAddressListButton.setChecked(currentItem.isDefaultAddress());
            if (currentItem.isDefaultAddress()) {
                mSelectedAddressId = currentItem.getAddressId();
            }
        }

        @Override
        public int getItemCount() {
            return mAddressList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private RadioButton selectAddressListButton;
            private TextView addressTextView;
            private TextView mRemoveTextView;
            private TextView mEditTextView;

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                selectAddressListButton = itemView.findViewById(R.id.checkBox);
                addressTextView = itemView.findViewById(R.id.addressTextView);
                mRemoveTextView = itemView.findViewById(R.id.removeTextView);
                mRemoveTextView.setVisibility(View.GONE);
                mEditTextView = itemView.findViewById(R.id.editTextView);
                mEditTextView.setVisibility(View.GONE);
                selectAddressListButton.setOnClickListener(view -> {
                    setDefaultValueToAddressList();
                    AddressResponse.AddressItem currentItem = mAddressList.get(getAdapterPosition());
                    currentItem.setDefaultAddress(true);
                    notifyDataSetChanged();
                });
            }

            private void setDefaultValueToAddressList() {
                for (AddressResponse.AddressItem addressItem : mAddressList) {
                    addressItem.setDefaultAddress(false);
                }
            }
        }
    }

    public void shareApplication() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String shareMessage = "";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception ignore) {
            /*
             * do nothing
             * */
        }
    }
}
