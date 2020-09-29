package com.skinfotech.dailyneeds.fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.constant.ToolBarManager;
import com.skinfotech.dailyneeds.models.requests.CommonRequest;
import com.skinfotech.dailyneeds.models.requests.ProfileRequest;
import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.skinfotech.dailyneeds.models.responses.ProfileResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import com.squareup.picasso.Picasso;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.skinfotech.dailyneeds.Constants.USER_ID;

public class MyProfileFragment extends BaseFragment {

    private static final String TAG = "MyProfileFragment";
    private ImageView mUserProfileImageView;
    private String mEncodedProfileImage = "";
    private String mLastProfilePhotoStr = "";
    private EditText usernameEditText;
    private TextView usernameTextView;
    private TextView UpdateUsernameTextView;
    private String userNameStr;
    private ProfileResponse profileResponse;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_my_profile, container, false);
        ToolBarManager.getInstance().hideToolBar(mActivity, true);
        mUserProfileImageView = mContentView.findViewById(R.id.userProfile);
        usernameEditText = mContentView.findViewById(R.id.editTextPersonName);
        UpdateUsernameTextView = mContentView.findViewById(R.id.updateNameTextView);
        fetchProfileServerCall();
        return mContentView;
    }

    private void fetchProfileServerCall() {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<ProfileResponse> call = RetrofitApi.getAppServicesObject().fetchProfile(new CommonRequest(getStringDataFromSharedPref(USER_ID)));
                    final Response<ProfileResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    profileResponse = response.body();
                    if (profileResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(profileResponse.getErrorCode())) {
                            setupProfileFromResponse(profileResponse);
                            mActivity.updateSideNavigationHeaderProfile(profileResponse);
                        }
                    }
                }
                stopProgress();
            }
        }).start();
    }

    private void setupProfileFromResponse(ProfileResponse profileResponse) {
        TextView emailTextView = mContentView.findViewById(R.id.userPhoneNumber);
        TextView mobileTextView = mContentView.findViewById(R.id.phoneNumber);
        TextView addressTextView = mContentView.findViewById(R.id.address);
        usernameTextView = mContentView.findViewById(R.id.userNameTextView);
        usernameTextView.setText(profileResponse.getUserName());
        usernameEditText.setText(profileResponse.getUserName());
        emailTextView.setText(profileResponse.getUserEmail());
        mobileTextView.setText(profileResponse.getMobileNumber());
        addressTextView.setText(profileResponse.getPrimaryAddress());
        mLastProfilePhotoStr = profileResponse.getUserImage();
        if (Utility.isNotEmpty(mLastProfilePhotoStr)) {
            /*Bitmap bitmap = Utility.getImageInBitmap(mLastProfilePhotoStr);
            if (null != bitmap) {
                mUserProfileImageView.setImageBitmap(bitmap);
            }*/
            Picasso.get().load(mLastProfilePhotoStr).placeholder(R.drawable.profile_orange_icon).into(mUserProfileImageView);
        }
    }

    private void showProfilePhotoUploadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(getString(R.string.upload_image_message));
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", (dialogInterface, i) -> uploadFile());
        builder.setNegativeButton("No", (dialogInterface, i) -> {
            if (Utility.isEmpty(mLastProfilePhotoStr)) {
                Picasso.get().load(R.drawable.profile_orange_icon).into(mUserProfileImageView);
            } else {
                /*Bitmap bitmap = Utility.getImageInBitmap(mLastProfilePhotoStr);
                if (null != bitmap) {
                    mUserProfileImageView.setImageBitmap(bitmap);
                }*/
                Picasso.get().load(mLastProfilePhotoStr).placeholder(R.drawable.profile_orange_icon).into(mUserProfileImageView);
            }
            dialogInterface.dismiss();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.IImagePickConstants.CAMERA) {
                setImageToBitmap();
                showProfilePhotoUploadDialog();
            } else if (requestCode == Constants.IImagePickConstants.GALLERY && null != data) {
                Uri selectedImage = data.getData();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), selectedImage);
                    mUserProfileImageView.setImageBitmap(bitmap);
                    sProfilePhotoFile = new File(getRealPathFromURI(getImageUri(bitmap)));
                    showProfilePhotoUploadDialog();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), bitmap, "profile_photo", null);
        return Uri.parse(path);
    }

    private String getRealPathFromURI(Uri uri) {
        ContentResolver contentResolver = mActivity.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        cursor = contentResolver.query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.openMultipleAddressLayout:
                launchFragment(new SelectAddressFragment(), true);
                break;
            case R.id.openChangePassword:
                launchFragment(new ChangePasswordFragment(), true);
                break;
            case R.id.openOrderLayout:
                launchFragment(new MyOrderFragment(), true);
                break;
            case R.id.backButton:
                launchFragment(new HomeScreenFragment(), false);
                break;
            case R.id.userProfile:
                showImageDialog(mLastProfilePhotoStr);
                break;
            case R.id.openMyWishlistLayout:
                launchFragment(new MyWishListFragment(), true);
                break;
            case R.id.editProfile:
                if (isPermissionGranter()) {
                    sProfilePhotoFile = null;
                    sProfilePhotoImagePath = null;
                    selectImage();
                } else {
                    mActivity.requestPermission();
                }
                break;
            case R.id.editNameImage:
                usernameEditText.setVisibility(View.VISIBLE);
                usernameTextView.setVisibility(View.GONE);
                UpdateUsernameTextView.setVisibility(View.VISIBLE);
                break;
            case R.id.updateNameTextView:
                userNameStr = usernameEditText.getText().toString();
                if (Utility.isEmpty(userNameStr)) {
                    usernameEditText.setError(getString(R.string.mandatory_field_message));
                    usernameEditText.requestFocus();
                    return;
                }
                saveProfileServerCall();
                UpdateUsernameTextView.setVisibility(View.GONE);
                break;
        }
    }

    private void saveProfileServerCall() {
        hideKeyboard();
        showProgress();
        new Thread(() -> {
            try {
                String userId = getStringDataFromSharedPref(USER_ID);
                Call<CommonResponse> call = RetrofitApi.getAppServicesObject().saveProfileResponse(new ProfileRequest(userNameStr, userId));
                final Response<CommonResponse> response = call.execute();
                updateOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        CommonResponse commonResponse = response.body();
                        if (commonResponse != null) {
                            showToast(commonResponse.getErrorMessage());
                            if (Constants.SUCCESS.equalsIgnoreCase(commonResponse.getErrorCode())) {
                                usernameEditText.setVisibility(View.GONE);
                                stopProgress();
                                fetchProfileServerCall();
                                setupProfileFromResponse(profileResponse);
                                usernameTextView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    stopProgress();
                });
            } catch (IOException e) {
                updateOnUiThread(this::stopProgress);
                Log.e(TAG, e.getMessage(), e);
            }
        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.showBackButton();
        mActivity.hideCartIcon();
        hideKeyboard();
        mActivity.isToggleButtonEnabled(true);
        mActivity.hideFilterIcon();
    }

    private void uploadFile() {
        if (sProfilePhotoFile == null) {
            showToast("Something went wrong");
            return;
        }
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), sProfilePhotoFile);
                    MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("profilePhoto", sProfilePhotoFile.getName(), requestBody);
                    RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), getStringDataFromSharedPref(USER_ID));
                    Call<ProfileResponse> call = RetrofitApi.getAppServicesObject().uploadAudioFile(fileToUpload, userIdBody);
                    final Response<ProfileResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            private void handleResponse(Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    ProfileResponse profileResponse = response.body();
                    if (profileResponse != null) {
                        if (Constants.SUCCESS.equalsIgnoreCase(profileResponse.getErrorCode())) {
                            setupProfileFromResponse(profileResponse);
                            mActivity.updateSideNavigationHeaderProfile(profileResponse);
                        }
                        showToast(profileResponse.getErrorMessage());
                    }
                }
                stopProgress();
            }
        }).start();
    }

    private void setImageToBitmap() {
        // Get the dimensions of the View
        int targetW = mUserProfileImageView.getWidth();
        int targetH = mUserProfileImageView.getHeight();
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(sProfilePhotoImagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH));
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(sProfilePhotoImagePath, bmOptions);
        mUserProfileImageView.setImageBitmap(bitmap);
    }
}
