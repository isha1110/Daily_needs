package com.skinfotech.dailyneeds;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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

import com.skinfotech.dailyneeds.adapters.CustomExpandableAdapter;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.fragments.BaseFragment;
import com.skinfotech.dailyneeds.fragments.CartFragment;
import com.skinfotech.dailyneeds.fragments.FilterFragment;
import com.skinfotech.dailyneeds.fragments.HomeScreenFragment;
import com.skinfotech.dailyneeds.fragments.LoginFragment;
import com.skinfotech.dailyneeds.fragments.MyOrderFragment;
import com.skinfotech.dailyneeds.fragments.MyProfileFragment;
import com.skinfotech.dailyneeds.fragments.MyWishListFragment;
import com.skinfotech.dailyneeds.fragments.ProductCategoryFragment;
import com.skinfotech.dailyneeds.fragments.SearchFragment;
import com.skinfotech.dailyneeds.fragments.SignUpFragment;
import com.skinfotech.dailyneeds.models.MenuModel;
import com.skinfotech.dailyneeds.models.responses.ProfileResponse;
import com.skinfotech.dailyneeds.models.responses.SideNavigationResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import com.google.android.material.navigation.NavigationView;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Response;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.skinfotech.dailyneeds.Constants.SUCCESS;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PaymentResultListener {

    private ActionBarDrawerToggle mToggleButton;
    private DrawerLayout mSideNavigationDrawer;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<MenuModel> mSideNavigationHeaderList = new ArrayList<>();
    private HashMap<MenuModel, List<MenuModel>> mSideNavigationMap = new HashMap<>();
    private Checkout mCheckoutInstance = new Checkout();
    private static final String TAG = "HomeActivity";
    private View headerView;
    private List<SideNavigationResponse.NavigationOuterItem> mNavigationOuterList = new ArrayList<>();
    private String[] mPermissionArray = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

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
        headerView = navigationView.getHeaderView(0);
        mToggleButton = new ActionBarDrawerToggle(
                this, mSideNavigationDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mSideNavigationDrawer.addDrawerListener(mToggleButton);
        mToggleButton.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorWhite));
        mToggleButton.syncState();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger_resize_icon);//your icon here
        navigationView.setNavigationItemSelectedListener(this);
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE);
        if (Constants.YES.equalsIgnoreCase(preferences.getString(Constants.USER_LOGIN_DONE, Constants.NO))) {
            launchFragment(new HomeScreenFragment(), false);
        } else {
            launchFragment(new LoginFragment(), false);
        }
        expandableListView = findViewById(R.id.expandableListView);
        fetchSideNavigationDataServerCall();
        findViewById(R.id.searchImage).setOnClickListener(v -> launchFragment(new SearchFragment(), true));
    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(mPermissionArray[0]) != PackageManager.PERMISSION_GRANTED)
                    && (checkSelfPermission(mPermissionArray[1]) != PackageManager.PERMISSION_GRANTED)
                    && (checkSelfPermission(mPermissionArray[2]) != PackageManager.PERMISSION_GRANTED)) {
                if (shouldShowRequestPermissionRationale(mPermissionArray[2])) {
                    showToast("Camera permission is needed for to Capture Profile photo");
                }
                requestPermissions(mPermissionArray, Constants.PERMISSION_REQUEST_CODE);
            }
        }
    }

    public void isToggleButtonEnabled(boolean isEnable) {
        mToggleButton.setDrawerIndicatorEnabled(isEnable);
    }

    private void populateExpandableList() {
        expandableListAdapter = new CustomExpandableAdapter(this, mSideNavigationHeaderList, mSideNavigationMap);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            setListViewHeight(parent, groupPosition);
            if (mSideNavigationHeaderList.get(groupPosition).root) {
                switch (groupPosition) {
                    case 0:
                    case 6:
                    case 5:
                    case 4:
                    case 3:
                    case 2:
                    case 1:
                    case 7:
                        break;
                }
            }
            return false;
        });
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            if (mSideNavigationMap.get(mSideNavigationHeaderList.get(groupPosition)) != null) {
                switch (childPosition) {
                    case 0:
                    case 15:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 16:
                        MenuModel headerItem = mSideNavigationHeaderList.get(groupPosition);
                        List<MenuModel> menuModelList = mSideNavigationMap.get(headerItem);
                        String idFromSideNavigation = headerItem.menuId + Constants.SPLIT + menuModelList.get(childPosition).menuId;
                        launchFragment(new ProductCategoryFragment(idFromSideNavigation, Constants.IModes.SIDE_NAVIGATION), false);
                        onBackPressed();
                        break;
                }
            }
            return false;
        });
    }

    private void prepareNewMenuData() {
        mSideNavigationHeaderList.clear();
        mSideNavigationMap.clear();
        for (SideNavigationResponse.NavigationOuterItem item : mNavigationOuterList) {
            MenuModel menuModel = new MenuModel(item.getOuterItemName(),
                    true,
                    true,
                    item.getOuterItemImage(),
                    R.drawable.ic_keyboard_arrow_offwhite,
                    item.getOuterItemId());
            int childCount = 0;
            List<MenuModel> childModelsList = new ArrayList<>();
            for (SideNavigationResponse.NavigationInnerItem innerItem : item.getInnerNavigationList()) {
                MenuModel innerMenuModel = new MenuModel(innerItem.getInnerItemName(),
                        childCount == 0,
                        childCount == 0,
                        "",
                        R.drawable.ic_keyboard_arrow_offwhite,
                        innerItem.getInnerItemId());
                childCount++;
                childModelsList.add(innerMenuModel);
            }
            mSideNavigationMap.put(menuModel, childModelsList);
            mSideNavigationHeaderList.add(menuModel);
        }
        populateExpandableList();
    }

    private void fetchSideNavigationDataServerCall() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<SideNavigationResponse> call = RetrofitApi.getAppServicesObject().fetchSideNavigation();
                    final Response<SideNavigationResponse> response = call.execute();
                    runOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    showToast(e.getMessage());
                }
            }

            private void handleResponse(Response<SideNavigationResponse> response) {
                if (response.isSuccessful()) {
                    SideNavigationResponse navigationResponse = response.body();
                    if (navigationResponse != null) {
                        if (navigationResponse.getErrorCode().equalsIgnoreCase(SUCCESS)) {
                            mNavigationOuterList = navigationResponse.getNavigationList();
                            if (Utility.isNotEmpty(mNavigationOuterList)) {
                                prepareNewMenuData();
                            }
                        }
                    }
                }
            }
        }).start();
    }

    public void hideBackButton() {
        findViewById(R.id.backButtonToolbar).setVisibility(View.GONE);
    }

    public void showBackButton() {
        findViewById(R.id.backButtonToolbar).setVisibility(View.VISIBLE);
    }

    public void showFilterIcon() {
        findViewById(R.id.filterImageIcon).setVisibility(View.VISIBLE);
    }

    public void hideFilterIcon() {
        findViewById(R.id.filterImageIcon).setVisibility(View.GONE);
    }

    public void hideCartIcon() {
        findViewById(R.id.cartCountTextView).setVisibility(View.GONE);
        findViewById(R.id.cartImageView).setVisibility(View.GONE);
        findViewById(R.id.searchImage).setVisibility(View.GONE);
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
        findViewById(R.id.searchImage).setVisibility(View.VISIBLE);
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
        TextView userName = headerView.findViewById(R.id.userName);
        ImageView userProfile = headerView.findViewById(R.id.userProfile);
        userName.setText(Utility.toCamelCase(response.getUserName()));
        String imageStr = response.getUserImage();
        if (Utility.isNotEmpty(imageStr)) {
            Picasso.get().load(imageStr).placeholder(R.drawable.profile_orange_icon).into(userProfile);
        }
        userProfile.setOnClickListener(view -> {
            onBackPressed();
            launchFragment(new MyProfileFragment(), true);
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.getItemId();
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
            case R.id.sign_up:
                mSideNavigationDrawer.closeDrawer(GravityCompat.START);
                launchFragment(new SignUpFragment(), false);
                break;
            case R.id.log_in:
                mSideNavigationDrawer.closeDrawer(GravityCompat.START);
                launchFragment(new LoginFragment(), false);
                break;
            case R.id.logout:
                mSideNavigationDrawer.closeDrawer(GravityCompat.START);
                getCurrentFragment().storeStringDataInSharedPref(Constants.USER_LOGIN_DONE, Constants.NO);
                getCurrentFragment().storeStringDataInSharedPref(Constants.USER_ID, "");
                launchFragment(new LoginFragment(), false);
                break;
            case R.id.cartImageView:
                launchFragment(new CartFragment(), true);
                break;
            case R.id.homePageTextView:
                launchFragment(new HomeScreenFragment(), false);
                onBackPressed();
                break;
            case R.id.myOrdersTextView:
                launchFragment(new MyOrderFragment(), true);
                onBackPressed();
                break;
            case R.id.myWishlistTextView:
                launchFragment(new MyWishListFragment(), true);
                onBackPressed();
                break;
            case R.id.profileSettingTextView:
                launchFragment(new MyProfileFragment(), true);
                onBackPressed();
                break;
            case R.id.filterImageIcon:
                launchFragment(new FilterFragment(), true);
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
}
