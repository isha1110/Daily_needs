package com.skinfotech.dailyneeds.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.skinfotech.dailyneeds.Constants;
import com.skinfotech.dailyneeds.HomeActivity;
import com.skinfotech.dailyneeds.R;
import com.skinfotech.dailyneeds.Utility;
import com.skinfotech.dailyneeds.models.requests.CommonProductRequest;
import com.skinfotech.dailyneeds.models.responses.CommonResponse;
import com.skinfotech.dailyneeds.retrofit.RetrofitApi;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Response;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.skinfotech.dailyneeds.Constants.SHARED_PREF_NAME;
import static com.skinfotech.dailyneeds.Constants.SUCCESS;

public class BaseFragment extends Fragment implements View.OnClickListener {

    protected HomeActivity mActivity;
    protected View mContentView;
    private ProgressDialog mProgressDialog;
    private String[] mPermissionArray = new String[]{
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    };
    static String sProfilePhotoImagePath = "";
    static File sProfilePhotoFile = null;
    static String sCategoryParentId = "";
    static String sSubCategoryParentId = "";
    static String sSubSubCategoryParentId = "";

    void showProgress() {
        mActivity.runOnUiThread(() -> {
            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setMessage(getString(R.string.please_wait));
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();
        });
    }

    void stopProgress() {
        mActivity.runOnUiThread(() -> {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context activity) {
        super.onAttach(activity);
        mActivity = (HomeActivity) activity;
    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = mActivity.getCurrentFocus();
        if (view == null) {
            view = new View(mActivity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    void addToCartServerCall(CommonProductRequest request) {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<CommonResponse> call = RetrofitApi.getAppServicesObject().addToCart(request);
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
                        if (commonResponse.getErrorCode().equalsIgnoreCase(SUCCESS)) {
                            cartAddedSuccessCallBack();
                        }
                        showToast(commonResponse.getErrorMessage());
                    }
                }
                stopProgress();
            }
        }).start();
    }

    protected void cartAddedSuccessCallBack() {
    }

    protected void cartRemovedSuccessCallBack() {
    }

    void removeFromCartServerCall(CommonProductRequest request) {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<CommonResponse> call = RetrofitApi.getAppServicesObject().removeFromCart(request);
                    final Response<CommonResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                }
            }

            private void handleResponse(Response<CommonResponse> response) {
                stopProgress();
                if (response.isSuccessful()) {
                    CommonResponse commonResponse = response.body();
                    if (commonResponse != null) {
                        if (commonResponse.getErrorCode().equalsIgnoreCase(SUCCESS)) {
                            cartRemovedSuccessCallBack();
                        }
                        showToast(commonResponse.getErrorMessage());
                    }
                }
            }
        }).start();
    }

    protected void onWishListSuccessServerCall(CommonResponse commonResponse) {
    }

    void wishListServerCall(CommonProductRequest request) {
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<CommonResponse> call = RetrofitApi.getAppServicesObject().doWishList(request);
                    final Response<CommonResponse> response = call.execute();
                    updateOnUiThread(() -> handleResponse(response));
                } catch (Exception e) {
                    stopProgress();
                    showToast(e.getMessage());
                }
            }

            private void handleResponse(Response<CommonResponse> response) {
                stopProgress();
                if (response.isSuccessful()) {
                    CommonResponse commonResponse = response.body();
                    if (commonResponse != null) {
                        if (commonResponse.getErrorCode().equalsIgnoreCase(Constants.SUCCESS)) {
                            onWishListSuccessServerCall(commonResponse);
                        }
                        showToast(commonResponse.getErrorMessage());
                    }
                }
            }
        }).start();
    }

    void updateOnUiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public void storeStringDataInSharedPref(String keyName, String value) {
        if (getActivity() != null) {
            SharedPreferences.Editor editor = mActivity.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE).edit();
            editor.putString(keyName, value);
            editor.apply();
        }
    }

    String getStringDataFromSharedPref(String keyName) {
        SharedPreferences prefs = mActivity.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        return prefs.getString(keyName, "");
    }

    void showToast(String msg) {
        updateOnUiThread(() -> mActivity.showToast(msg));
    }

    @Override
    public void onClick(View view) {
        /*
         * Just a override method to invoke the back pressed of the fragments
         * */
    }

    public void onBackPressed(BaseFragment fragment) {
        if (fragment != null && fragment.getActivity() != null) {
            fragment.getActivity().onBackPressed();
        }
    }

    protected void onBackPressedToExit(BaseFragment fragment) {
        if (fragment != null) {
            mActivity.finish();
        }
    }

    public boolean onBackPressed() {
        return false;
    }

    void launchFragment(Fragment fragment, boolean addBackStack) {
        mActivity.launchFragment(fragment, addBackStack);
    }

    void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Add Photo!");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                TakePictureFromCameraIntent();
            } else if (options[item].equals("Choose from Gallery")) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Pick Image"), Constants.IImagePickConstants.GALLERY);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void TakePictureFromCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            // Create the File where the photo should go
            sProfilePhotoFile = null;
            try {
                sProfilePhotoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (sProfilePhotoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mActivity,
                                                          "com.skinfotech.desibazaar.android.fileprovider",
                                                          sProfilePhotoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Constants.IImagePickConstants.CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        sProfilePhotoImagePath = image.getAbsolutePath();
        return image;
    }

    boolean isPermissionGranter() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (mActivity.checkSelfPermission(mPermissionArray[0]) == PackageManager.PERMISSION_GRANTED)
                && (mActivity.checkSelfPermission(mPermissionArray[1]) == PackageManager.PERMISSION_GRANTED)
                && (mActivity.checkSelfPermission(mPermissionArray[2]) == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    public void onPaymentSuccess(String txn) {
    }

    public void onPaymentError(int i, String s) {
    }

    void showImageDialog(String imageStr) {
        final Dialog dialog = new Dialog(mActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.image_dialog);
        ImageView imageView = dialog.findViewById(R.id.imageView);
        if (Utility.isEmpty(imageStr)) {
            return;
        } else {
            Picasso.get().load(imageStr).placeholder(R.drawable.default_image).into(imageView);
        }
        dialog.show();
    }
}
