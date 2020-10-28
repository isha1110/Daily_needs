package com.skinfotech.dailyneeds.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.SaveAddressRequest;
import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.skinfotech.dailyneeds.models.responses.LocationResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.skinfotech.dailyneeds.Constants.USER_ID;

public class AddNewAddressFragment extends BaseFragment {

    private Spinner mSelectLocation;
    private EditText enterAddressText;
    private EditText enterAddressText1;
    private EditText nameNewAddress;
    private EditText phoneNewAddress;
    private EditText cityAddress;
    private EditText stateAddress;
    private EditText pincodeAddress;
    private List<LocationResponse.LocationItem> responseList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_add_new_address, container, false);
        setupUI();
        mContentView.findViewById(R.id.confirm).setOnClickListener(view -> {
            if (chkValidations()) {
                saveAddressServerCall();
            }
        });
        getLocationsResponseServerCall();
        return mContentView;
    }

    private boolean chkValidations() {

        if (nameNewAddress.getText().toString().isEmpty()) {
            nameNewAddress.setError(getString(R.string.mandatory_field_message));
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

    private void setupUI() {
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().setHeaderTitle(mActivity.getString(R.string.add_address));
        ToolBarManager.getInstance().onBackPressed(this);
        mActivity.isToggleButtonEnabled(false);
        nameNewAddress = mContentView.findViewById(R.id.newAddressName);
        phoneNewAddress = mContentView.findViewById(R.id.newAddressPhone);
        enterAddressText = mContentView.findViewById(R.id.enterAddressText);
        enterAddressText1 = mContentView.findViewById(R.id.enterAddressText1);
        cityAddress = mContentView.findViewById(R.id.cityNewAddress);
        stateAddress = mContentView.findViewById(R.id.stateNewAddress);
        pincodeAddress = mContentView.findViewById(R.id.pincodeNewAddress);
        mSelectLocation = mContentView.findViewById(R.id.selectLocation);
    }

    private void saveAddressServerCall() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String name = nameNewAddress.getText().toString();
                    String mobile = phoneNewAddress.getText().toString();
                    String address = enterAddressText.getText().toString();
                    String address1 = enterAddressText1.getText().toString();
                    String city = cityAddress.getText().toString();
                    String state = stateAddress.getText().toString();
                    String pincode = pincodeAddress.getText().toString();
                    String location = mSelectLocation.getSelectedItem().toString();
                    SaveAddressRequest request = new SaveAddressRequest();
                    request.setmUserId(getStringDataFromSharedPref(USER_ID));
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
                        showToast(commonResponse.getErrorMessage());
                        if (commonResponse.getErrorCode().equalsIgnoreCase(Constants.SUCCESS)) {
                            launchFragment(new SelectAddressFragment(), false);
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    private void getLocationsResponseServerCall() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<LocationResponse> call = RetrofitApi.getAppServicesObject().getLocationsResponse();
                    final Response<LocationResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<LocationResponse> response) {
                if (response.isSuccessful()) {
                    LocationResponse commonResponse = response.body();
                    if (commonResponse != null && Constants.SUCCESS.equalsIgnoreCase(commonResponse.getErrorCode())) {
                        responseList.clear();
                        responseList = commonResponse.getLocationList();
                        String[] responseStringArray = new String[responseList.size()];
                        for (int position = 0; position < responseList.size(); position++) {
                            responseStringArray[position] = responseList.get(position).getLocationName();
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(mActivity, R.layout.custom_spinner, responseStringArray);
                        arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropbox);
                        mSelectLocation.setAdapter(arrayAdapter);
                    } else if (commonResponse != null) {
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
        mActivity.showCartIcon();
        mActivity.hideSearchIcon();
        mActivity.showBackButton();
    }
}
