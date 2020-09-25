package com.skinfotech.dailyneeds.fragments;

import android.os.Bundle;
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
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.SaveAddressRequest;
import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;

import retrofit2.Call;
import retrofit2.Response;

import static com.skinfotech.dailyneeds.Constants.USER_ID;

public class AddNewAddressFragment extends BaseFragment {

    private Spinner mSelectLocation;
    private EditText enterAddressText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_add_new_address, container, false);
        ToolBarManager.getInstance().hideToolBar(mActivity, false);
        ToolBarManager.getInstance().setHeaderTitle(mActivity.getString(R.string.add_address));
        mActivity.isToggleButtonEnabled(true);
        enterAddressText = mContentView.findViewById(R.id.enterAddressText);
        mSelectLocation = mContentView.findViewById(R.id.selectLocation);
        mSelectLocation.setPrompt(mActivity.getString(R.string.select_location));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mActivity, R.array.locationList, R.layout.custom_spinner);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropbox);
        mSelectLocation.setAdapter(adapter);
        mContentView.findViewById(R.id.confirm).setOnClickListener(view -> {
            if (Utility.isEmpty(enterAddressText)) {
                enterAddressText.setError(getString(R.string.mandatory_field_message));
                enterAddressText.requestFocus();
                return;
            }
            saveAddressServerCall();
        });
        return mContentView;
    }

    private void saveAddressServerCall() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String address = enterAddressText.getText().toString();
                    String location = mSelectLocation.getSelectedItem().toString();
                    SaveAddressRequest request = new SaveAddressRequest();
                    request.setUserId(getStringDataFromSharedPref(USER_ID));
                    request.setAddress(address);
                    request.setLocation(location);
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
                            launchFragment(new SelectAddressFragment(), true);
                        }
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
        mActivity.hideFilterIcon();
    }
}
