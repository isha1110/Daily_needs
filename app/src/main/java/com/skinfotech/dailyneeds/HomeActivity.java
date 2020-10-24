package com.skinfotech.dailyneeds;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.fragments.BaseFragment;
import com.skinfotech.dailyneeds.fragments.CartFragment;
import com.skinfotech.dailyneeds.fragments.CategoryListFrgament;
import com.skinfotech.dailyneeds.fragments.HomeScreenFragment;
import com.skinfotech.dailyneeds.fragments.LoginFragment;
import com.skinfotech.dailyneeds.fragments.MyOrderFragment;
import com.skinfotech.dailyneeds.fragments.SearchFragment;
import com.skinfotech.dailyneeds.fragments.SelectAddressFragment;
import com.skinfotech.dailyneeds.models.requests.AddressResponse;
import com.skinfotech.dailyneeds.models.requests.CommonRequest;
import com.skinfotech.dailyneeds.models.requests.DefaultAddressRequest;
import com.skinfotech.dailyneeds.models.requests.SaveAddressRequest;
import com.skinfotech.dailyneeds.models.responses.CommonDetailsResponse;
import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.google.android.material.navigation.NavigationView;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Response;


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
    private TextView mCartCount;
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
        navigationView.setNavigationItemSelectedListener(this);
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        if (Constants.YES.equalsIgnoreCase(preferences.getString(Constants.USER_LOGIN_DONE, Constants.NO))) {
            launchFragment(new HomeScreenFragment(), false);
        } else {
            launchFragment(new LoginFragment(), false);
        }
        findViewById(R.id.searchTextView).setOnClickListener(v -> launchFragment(new SearchFragment(), true));
        findViewById(R.id.constraintContainer).setOnClickListener(v -> {
            fetchAddressServerCall();
            bottomSheetDialog.show();
        });
        addressBottomSheet();
    }
    public void addressBottomSheet(){
        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottomsheet_select_address_list);
        selectAddressRecyclerView = bottomSheetDialog.findViewById(R.id.selectAddressListRecycler);
        mSelectAddressListAdapter = new SelectAddressListAdapter();
        selectAddressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectAddressRecyclerView.setAdapter(mSelectAddressListAdapter);
        fetchAddressServerCall();
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
        mSelectLocation.setPrompt(this.getString(R.string.select_location));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.locationList, R.layout.custom_spinner);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropbox);
        mSelectLocation.setAdapter(adapter);
        makeDefaultTextView.setOnClickListener(v -> {
                if (Utility.isEmpty(mSelectedAddressId)) {
                    showToast(getString(R.string.default_address_msg));
                    return;
                }
                makeDefaultAddress(mSelectedAddressId);
        });

    }
    private boolean chkValidations() {

        if(nameNewAddress.getText().toString().isEmpty()){
            nameNewAddress.setError(getString(R.string.mandatory_address1_field));
            nameNewAddress.requestFocus();
            return false;
        }
        if(phoneNewAddress.getText().toString().length() < 10 || phoneNewAddress.getText().toString().length() > 10){
            phoneNewAddress.setError(getString(R.string.length_mobile_field));
            phoneNewAddress.requestFocus();
            return false;
        }
        if(enterAddressText.getText().toString().isEmpty()){
            enterAddressText.setError(getString(R.string.mandatory_address1_field));
            enterAddressText.requestFocus();
            return false;
        }
        if(enterAddressText1.getText().toString().isEmpty()){
            enterAddressText1.setError(getString(R.string.mandatory_address2_field));
            enterAddressText1.requestFocus();
            return false;
        }
        if(cityAddress.getText().toString().isEmpty()){
            cityAddress.setError(getString(R.string.mandatory_city_field));
            cityAddress.requestFocus();
            return false;
        }
        if(stateAddress.getText().toString().isEmpty()){
            stateAddress.setError(getString(R.string.mandatory_state_field));
            stateAddress.requestFocus();
            return false;
        }
        if(pincodeAddress.getText().toString().length() > 6 || pincodeAddress.getText().toString().length() < 6){
            pincodeAddress.setError(getString(R.string.length_pincode_field));
            pincodeAddress.requestFocus();
            return false;
        }
        return true;
    }
    private void saveAddressServerCall() {
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
                    SaveAddressRequest request = new SaveAddressRequest();
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
                            //addressGroup.setVisibility(Utility.isEmpty(mAddressResponseList) ? View.GONE : View.VISIBLE);
                            mSelectAddressListAdapter.setAddressList(mAddressResponseList);
                            mSelectAddressListAdapter.notifyDataSetChanged();
                            showToast(addressResponse.getErrorMessage());
                            launchFragment(new HomeScreenFragment(),false);
                            bottomSheetDialog.dismiss();
                        }
                    }
                }
            }
        }).start();
    }

    public void isToggleButtonEnabled(boolean isEnable) {
        mToggleButton.setDrawerIndicatorEnabled(isEnable);
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
            options.put("image", R.drawable.app_logo);
            options.put("currency", "INR");
            options.put("amount", finalAmountToBePaid);
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
        locationNameHeader.setText(Utility.toCamelCase(response.getLocation()));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_logout:
                mSideNavigationDrawer.closeDrawer(GravityCompat.START);
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

    @Override
    public void onBackPressed() {
        if (mSideNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
            mSideNavigationDrawer.closeDrawer(GravityCompat.START);
            return;
        }
        BaseFragment current = getCurrentFragment();
        if (current.onBackPressed()) {
            // To flip between view in personalize card fragment onBackPressed
            return;
        }
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            super.onBackPressed();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cartImageView:
                launchFragment(new CartFragment(), true);
                break;
            case R.id.categoryTextView:
                launchFragment(new CategoryListFrgament(),true);
                break;
            case R.id.confirm:
                if(chkValidations()){
                    saveAddressServerCall();
                }
                break;
            case R.id.addNewAddressCheckout:
                if(formConstraintLayout.getVisibility() == View.GONE){
                    formConstraintLayout.setVisibility(View.VISIBLE);
                    selectAddressRecyclerView.setVisibility(View.GONE);
                    makeDefaultTextView.setVisibility(View.GONE);
                    selectDeliveryAddress.setText(getString(R.string.add_new_address));
                    addNewAddressCheckout.setText(getString(R.string.select_delivery_address));
                }
                else {
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
        /*BaseFragment current = getCurrentFragment();
        if (null != current) {
            manager.popBackStackImmediate();
        }*/
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
    /*private void fetchAddressServerCall() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String userId = getCurrentFragment().getStringDataFromSharedPref(Constants.USER_ID);
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
                    AddressResponse addressResponse = response.body();
                    if (addressResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(addressResponse.getErrorCode())) {
                            List<AddressResponse.AddressItem> addressList = addressResponse.getAddressList();
                            mSelectAddressListAdapter.setAddressList(addressList);
                            mSelectAddressListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }).start();
    }*/

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
                    AddressResponse addressResponse = response.body();
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


    /*private class SelectAddressListAdapter extends RecyclerView.Adapter<SelectAddressListAdapter.RecyclerViewHolder> {
        private List<AddressResponse.AddressItem> mAddressList;

        public void setAddressList(List<AddressResponse.AddressItem> addressList) {
            mAddressList = addressList;
        }

        SelectAddressListAdapter(List<AddressResponse.AddressItem> list) {
            mAddressList = list;
        }
        //private int lastSelectedPosition = -1;

        @NonNull
        @Override
        public SelectAddressListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_address_list, parent, false);
            return new SelectAddressListAdapter.RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SelectAddressListAdapter.RecyclerViewHolder holder, int position) {
           // holder.selectAddressListButton.setChecked(lastSelectedPosition == position);
            AddressResponse.AddressItem currentItem = mAddressList.get(position);
            holder.mRadioButton.setText(currentItem.getNameStr()+"/"+currentItem.getMobileStr());
            holder.addressTextView.setText(currentItem.getAddressStr()+currentItem.getLocationStr());
            holder.mRadioButton.setChecked(currentItem.isDefaultAddress());
        }

        @Override
        public int getItemCount() {
            return 2;
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {
            private RadioButton mRadioButton;
            private TextView addressTextView;
            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                *//*selectAddressListButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastSelectedPosition = getAdapterPosition();
                        notifyDataSetChanged();

                    }
                });*//*
                mRadioButton = itemView.findViewById(R.id.checkBox);
                addressTextView = itemView.findViewById(R.id.addressTextView);
                mRadioButton.setOnClickListener(v -> {
                    mSelectedAddressId = mAddressList.get(getAdapterPosition()).getAddressId();
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
    }*/
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
            holder.selectAddressListButton.setText(currentItem.getNameStr()+"/"+currentItem.getMobileStr());
            holder.addressTextView.setText(currentItem.getAddressStr()+currentItem.getLocationStr());
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

            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                selectAddressListButton = itemView.findViewById(R.id.checkBox);
                addressTextView = itemView.findViewById(R.id.addressTextView);
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
}
