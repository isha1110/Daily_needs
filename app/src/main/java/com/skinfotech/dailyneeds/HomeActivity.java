package com.skinfotech.dailyneeds;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.fragments.AddNewAddressFragment;
import com.skinfotech.dailyneeds.fragments.BaseFragment;
import com.skinfotech.dailyneeds.fragments.CartFragment;
import com.skinfotech.dailyneeds.fragments.CategoryListFrgament;
import com.skinfotech.dailyneeds.fragments.HomeScreenFragment;
import com.skinfotech.dailyneeds.fragments.LoginFragment;
import com.skinfotech.dailyneeds.fragments.MyOrderFragment;
import com.skinfotech.dailyneeds.fragments.SearchFragment;
import com.skinfotech.dailyneeds.fragments.SelectAddressFragment;
import com.skinfotech.dailyneeds.models.MenuModel;
import com.skinfotech.dailyneeds.models.responses.ProfileResponse;
import com.skinfotech.dailyneeds.models.responses.SideNavigationResponse;
import com.google.android.material.navigation.NavigationView;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.skinfotech.dailyneeds.Constants.IS_SKIP_LOGIN;
import static com.skinfotech.dailyneeds.Constants.NO;
import static com.skinfotech.dailyneeds.Constants.SHARED_PREF_NAME;
import static com.skinfotech.dailyneeds.Constants.USER_ID;
import static com.skinfotech.dailyneeds.Constants.USER_LOGIN_DONE;
import static com.skinfotech.dailyneeds.Constants.USER_TYPE;
import static com.skinfotech.dailyneeds.Constants.YES;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PaymentResultListener,RadioGroup.OnCheckedChangeListener {

    private ActionBarDrawerToggle mToggleButton;
    private DrawerLayout mSideNavigationDrawer;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<MenuModel> mSideNavigationHeaderList = new ArrayList<>();
    private HashMap<MenuModel, List<MenuModel>> mSideNavigationMap = new HashMap<>();
    private Checkout mCheckoutInstance = new Checkout();
    private static final String TAG = "HomeActivity";
    private View headerView;
    private BottomSheetDialog bottomSheetDialog;
    private RecyclerView selectAddressRecyclerView;
    private List<SideNavigationResponse.NavigationOuterItem> mNavigationOuterList = new ArrayList<>();
    private String[] mPermissionArray = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private SelectAddressListAdapter mSelectAddressListAdapter;
    private String mSelectedPaymentMode = "";
    private String mSelectedAddressId = "";
    private RadioButton onlineRadioButton;
    private RadioButton cashRadioButton;

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
        Menu menu = navigationView.getMenu();
        MenuItem logoutItem = menu.findItem(R.id.nav_login);
        SharedPreferences preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        if (YES.equalsIgnoreCase(preferences.getString(USER_LOGIN_DONE, NO))) {
            logoutItem.setTitle("Login");
        }
        launchFragment(new HomeScreenFragment(), false);
        headerView = navigationView.getHeaderView(0);
        mToggleButton = new ActionBarDrawerToggle(
                this, mSideNavigationDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mSideNavigationDrawer.addDrawerListener(mToggleButton);
        mToggleButton.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite));
        mToggleButton.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        findViewById(R.id.searchTextView).setOnClickListener(v -> launchFragment(new SearchFragment(), true));
        findViewById(R.id.constraintContainer).setOnClickListener(v -> bottomSheetDialog.show());
        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottomsheet_select_address_list);
        bottomSheetDialog.findViewById(R.id.addNewAddressLayout).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            launchFragment(new AddNewAddressFragment(),true);
        });
        selectAddressRecyclerView = bottomSheetDialog.findViewById(R.id.selectAddressRecycler);
        mSelectAddressListAdapter = new SelectAddressListAdapter();
        selectAddressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectAddressRecyclerView.setAdapter(mSelectAddressListAdapter);
    }
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.onlinePayment:
                mSelectedPaymentMode = onlineRadioButton.getText().toString();
                break;
            case R.id.cashPayment:
                mSelectedPaymentMode = cashRadioButton.getText().toString();
                break;
        }
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
            options.put("image", "https://desibazaar.co.in/panel/assets/images/razorpay.jpg");
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

    public void updateSideNavigationHeaderProfile(ProfileResponse response) {
        /*TextView userName = headerView.findViewById(R.id.userName);
        ImageView userProfile = headerView.findViewById(R.id.userProfile);
        userName.setText(Utility.toCamelCase(response.getUserName()));
        String imageStr = response.getUserImage();
        if (Utility.isNotEmpty(imageStr)) {
            Picasso.get().load(imageStr).placeholder(R.drawable.profile_orange_icon).into(userProfile);
        }
        userProfile.setOnClickListener(view -> {
            onBackPressed();
            launchFragment(new MyProfileFragment(), true);
        });*/
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
        switch (item.getItemId()) {
            case R.id.nav_login:
                showLoginDialog();

                //launchFragment(new LoginFragment(), true);
                break;
            case R.id.nav_my_cart:
                if (sharedPreferences.getString(IS_SKIP_LOGIN, NO).equalsIgnoreCase(YES)) {
                    showLoginDialog();
                } else {
                    launchFragment(new CartFragment(), true);
                }
                break;
            case R.id.nav_myOrders:
                if (sharedPreferences.getString(IS_SKIP_LOGIN, NO).equalsIgnoreCase(YES)) {
                    showLoginDialog();
                } else {
                    launchFragment(new MyOrderFragment(), true);
                }
                break;
            case R.id.nav_myAddress:
                if (sharedPreferences.getString(IS_SKIP_LOGIN, NO).equalsIgnoreCase(YES)) {
                    showLoginDialog();
                } else {
                    launchFragment(new SelectAddressFragment(), true);
                }
                break;
            default:
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void showLoginDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to Login?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> logout())
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void logout() {
        SharedPreferences.Editor preferencesEditor = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE).edit();
        preferencesEditor.putString(USER_LOGIN_DONE, Constants.NO);
        preferencesEditor.putString(USER_ID, "");
        preferencesEditor.putString(USER_TYPE, "");
        preferencesEditor.apply();
        launchFragment(new LoginFragment(), true);
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

    private void setListViewHeight(ExpandableListView listView,
                                   int group) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += groupItem.getMeasuredHeight();
            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                    totalHeight += listItem.getMeasuredHeight();
                }
            }
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10) {
            height = 200;
        }
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    private class SelectAddressListAdapter extends RecyclerView.Adapter<SelectAddressListAdapter.RecyclerViewHolder> {
        /*private List<AddressResponse.AddressItem> mAddressList = new ArrayList<>();

        public void setAddressList(List<AddressResponse.AddressItem> addressList) {
            mAddressList = addressList;
        }*/
        private int lastSelectedPosition = -1;

        @NonNull
        @Override
        public SelectAddressListAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_address_bottom_sheet_item, parent, false);
            return new SelectAddressListAdapter.RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SelectAddressListAdapter.RecyclerViewHolder holder, int position) {
            holder.selectAddressListButton.setChecked(lastSelectedPosition == position);
            /*AddressResponse.AddressItem currentItem = mAddressList.get(position);
            holder.selectAddressListButton.setText(currentItem.getAddressStr());
            holder.selectAddressListButton.setChecked(currentItem.isDefaultAddress());
            if (currentItem.isDefaultAddress()) {
               // mSelectedAddressId = currentItem.getAddressId();
            }*/
        }

        @Override
        public int getItemCount() {
            return 2;
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            private RadioButton selectAddressListButton;
            RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                selectAddressListButton = itemView.findViewById(R.id.selectAddressButton);
                selectAddressListButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastSelectedPosition = getAdapterPosition();
                        notifyDataSetChanged();

                    }
                });
                /*selectAddressListButton = itemView.findViewById(R.id.selectAddressButton);
                selectAddressListButton.setOnClickListener(view -> {
                    setDefaultValueToAddressList();
                    AddressResponse.AddressItem currentItem = mAddressList.get(getAdapterPosition());
                    currentItem.setDefaultAddress(true);
                    notifyDataSetChanged();
                });*/
            }

           /* private void setDefaultValueToAddressList() {
                for (AddressResponse.AddressItem addressItem : mAddressList) {
                    addressItem.setDefaultAddress(false);
                }
            }*/
        }
    }
}
