package com.dolphin.thegigisup.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dolphin.thegigisup.*;
import com.dolphin.thegigisup.api.ApiTask;
import com.dolphin.thegigisup.api.Refreshable;
import com.dolphin.thegigisup.api.Runner;
import com.dolphin.thegigisup.api.ServiceInterface;
import com.makeramen.roundedimageview.RoundedImageView;
import retrofit.client.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Settings page to allow the user to change their profile information
 *
 * @author Team Dolphin 04/04/15.
 */
public class SettingsFragment extends Fragment
        implements Refreshable, View.OnClickListener{

    private LinearLayout loggedinLayout, loggedoutLayout;
    private Button usernameButton, emailButton, passwordButton, pictureButton;
    private TextView usernameTextFinal, emailTextFinal, usernameText, emailText;
    private RoundedImageView profilePicture, navProfilePicture;
    private Bitmap selectedImageBitmap;

    private final Runner.Scope taskScope = new Runner.Scope();
    private Runner runner;
    private ServiceInterface service;

    public static final int SELECT_PHOTO = 100;
    public static final int CHANGE_USERNAME = 1;
    public static final int CHANGE_EMAIL = 2;
    public static final int CHANGE_PASSWORD = 3;

    public static final String MYPREFERENCES = "MyPreferences" ;
    public static final String USERNAME = "Username";
    public static final String TOKEN = "Token";
    public static final String EMAIL = "Email";
    public static final String USERID = "UserID";
    public static final String USERIMAGE = "UserImage";
    private SharedPreferences sharedPreferences;

    private String username, email, accessToken;
    private int userID;

    /**
     * On creation of the view, find and set the appropriate information when
     * the page starts
     *
     * @return The created view v
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_frag, container, false);

        //Get relevant textviews and the current username/email info
        usernameText = (TextView) getActivity().findViewById(
                                                        R.id.act_iv_user_name);
        emailText = (TextView) getActivity().findViewById(
                                                        R.id.act_iv_user_email);
        usernameTextFinal = (TextView) v.findViewById(R.id.set_tv_username);
        emailTextFinal = (TextView) v.findViewById(R.id.set_tv_email);
        //Find the buttons and set on click listeners
        usernameButton = (Button) v.findViewById(R.id.set_bt_changeusername);
        usernameButton.setOnClickListener(this);
        emailButton = (Button) v.findViewById(R.id.set_bt_changeemail);
        emailButton.setOnClickListener(this);
        passwordButton = (Button) v.findViewById(R.id.set_bt_changepassword);
        passwordButton.setOnClickListener(this);
        pictureButton = (Button) v.findViewById(R.id.set_bt_changepicture);
        pictureButton.setOnClickListener(this);
        //Find the layout to show if the user is logged in or out
        loggedinLayout = (LinearLayout)v.findViewById(
                                            R.id.set_ll_userloggedinlayout);
        loggedoutLayout = (LinearLayout)v.findViewById(
                                            R.id.set_ll_userloggedoutlayout);
        //Find the profile pictures to update
        profilePicture = (RoundedImageView)v.findViewById(
                                            R.id.set_iv_profile_image);
        navProfilePicture = (RoundedImageView) getActivity().findViewById(
                                            R.id.act_iv_user_image);

        sharedPreferences = getActivity().getSharedPreferences(
                                          MYPREFERENCES, Context.MODE_PRIVATE);

        // If the user is logged in, get the user information and log them in
        // on the settings page
        if (sharedPreferences.contains(USERNAME)) {
            username = sharedPreferences.getString(USERNAME, null);
            email = sharedPreferences.getString(EMAIL, null);
            accessToken = sharedPreferences.getString(TOKEN, null);
            userID = sharedPreferences.getInt(USERID, -1);
            // If the user has a saved profile picture, update it
            if (sharedPreferences.contains(USERIMAGE)) {
                String bitmapString = sharedPreferences.getString(
                                                             USERIMAGE, null);
                Bitmap image = StringToBitMap(bitmapString);
                profilePicture.setImageBitmap(image);
            }
            userLoggedIn();
        }
        // If the user is logged out disable username
        if (!sharedPreferences.contains(USERNAME)) {
            userLoggedOut();
        }
        return v;
    }

    /**
     * Set the methods for on click for the user to change their username,
     * password, email or profile picture
     *
     * @param v The clicked view v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.set_bt_changeusername):
                final AlertDialog.Builder usernameAlert =
                                       new AlertDialog.Builder(getActivity());

                usernameAlert.setTitle("Change username");
                usernameAlert.setMessage("Enter your new username here");

                // Set an EditText view to get user input
                final EditText usernameInput = new EditText(getActivity());
                usernameInput.setTextColor(
                                android.content.res.ColorStateList.valueOf(
                                                R.color.color_darker_gray));
                usernameInput.setGravity(Gravity.CENTER_HORIZONTAL);
                usernameAlert.setView(usernameInput);

                usernameAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        UpdateInformation task = new UpdateInformation(
                                service, CHANGE_USERNAME, userID, accessToken,
                                usernameInput.getText().toString());
                        runner.run(task, taskScope);
                    }
                });

                usernameAlert.setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                    }
                });
                usernameAlert.show();
                break;

            case (R.id.set_bt_changeemail):
                final AlertDialog.Builder emailAlert =
                                    new AlertDialog.Builder(getActivity());

                emailAlert.setTitle("Change email");
                emailAlert.setMessage("Enter your new email here");

                // Set an EditText view to get user input
                final EditText emailInput = new EditText(getActivity());
                emailInput.setTextColor(
                                android.content.res.ColorStateList.valueOf(
                                                R.color.color_darker_gray));

                emailInput.setGravity(Gravity.CENTER_HORIZONTAL);
                emailAlert.setView(emailInput);

                emailAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (isValidEmail(emailInput.getText().toString())) {
                            UpdateInformation task = new UpdateInformation(
                                    service, CHANGE_EMAIL, userID, accessToken,
                                    emailInput.getText().toString());
                            runner.run(task, taskScope);
                        }
                        else if (!isValidEmail(
                                            emailInput.getText().toString())) {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Please enter a valid email",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
                emailAlert.setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                    }
                });
                emailAlert.show();
                break;

            case (R.id.set_bt_changepassword):
                final AlertDialog.Builder passwordAlert =
                                        new AlertDialog.Builder(getActivity());
                passwordAlert.setTitle("Change password");
                passwordAlert.setMessage("Enter your new password here");
                // Set an EditText view to get user input
                final EditText passwordInput = new EditText(getActivity());
                passwordInput.setTextColor(
                                android.content.res.ColorStateList.valueOf(
                                                R.color.color_darker_gray));
                passwordInput.setGravity(Gravity.CENTER_HORIZONTAL);
                passwordAlert.setView(passwordInput);

                passwordAlert.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        UpdateInformation task = new UpdateInformation(
                                service, CHANGE_PASSWORD, userID, accessToken,
                                passwordInput.getText().toString());
                        runner.run(task, taskScope);
                    }
                });

                passwordAlert.setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                    }
                });
                passwordAlert.show();
                break;

            case (R.id.set_bt_changepicture):
                Fragment fragment = this;
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                photoPickerIntent.setType("image/*");
                fragment.startActivityForResult(
                                              photoPickerIntent, SELECT_PHOTO);
                break;

            default:
                break;
        }
    }

    /**
     * Make sure the settings fragment shows the user is logged out and
     * doesn't allow the user to change any profile settings
     */
    public void userLoggedOut() {
        //Set the appropriate UI information for the user being logged out
        loggedinLayout.setVisibility(View.GONE);
        loggedoutLayout.setVisibility(View.VISIBLE);

        usernameButton.setEnabled(false);
        emailButton.setEnabled(false);
        passwordButton.setEnabled(false);
        pictureButton.setEnabled(false);

        profilePicture.setImageDrawable(getResources().getDrawable(
                                                            R.drawable.event));
    }

    /**
     * Put the user information into the correct places on the settings page
     * if the user is logged in
     */
    public void userLoggedIn() {
        //Set the appropriate UI information for the user being logged in
        usernameTextFinal.setText("Username:   "+username+"\n");
        emailTextFinal.setText("Email:   "+email+"\n");

        loggedinLayout.setVisibility(View.VISIBLE);
        loggedoutLayout.setVisibility(View.GONE);

        usernameButton.setEnabled(true);
        emailButton.setEnabled(true);
        passwordButton.setEnabled(true);
        pictureButton.setEnabled(true);
    }

    /**
     * On refresh, get the runner and serviceInterface objects for use in
     * making the API user update calls
     */
    @Override
    public void refresh(Runner runner, ServiceInterface service) {
        this.runner = runner;
        this.service = service;
    }

    /**
     * On a result, if the user has chosen to select an image for a profile
     * picture, create a small, appropriately rotate bitmap and return it
     * to the user, updating shared preferences to save the image
     */
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == Activity.RESULT_OK){
                    //Get the selected image URI and move to the image in the
                    // gallery
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn =
                                          {MediaStore.Images.Thumbnails.DATA};

                    Cursor cursor = getActivity().getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    //Get the images rotation stored by the camera and rotate
                    // the image if necessary
                    int orientation = 0;
                    try {
                        ExifInterface exif = new ExifInterface(filePath);
                        orientation = exif.getAttributeInt(
                                            ExifInterface.TAG_ORIENTATION, 1);
                    } catch (IOException e) {}

                    selectedImageBitmap = BitmapFactory.decodeFile(filePath);
                    if (orientation == 6) {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        selectedImageBitmap = Bitmap.createBitmap(
                                            selectedImageBitmap, 0, 0,
                                            selectedImageBitmap.getWidth(),
                                            selectedImageBitmap.getHeight(),
                                            matrix, true);
                    }

                    //Create a scaled down version of the rotated image and set
                    // the profile picture in the nav drawer
                    selectedImageBitmap = Bitmap.createScaledBitmap(
                            selectedImageBitmap,
                            selectedImageBitmap.getWidth()/10,
                            selectedImageBitmap.getHeight()/10, false);

                    profilePicture.setImageBitmap(selectedImageBitmap);
                    navProfilePicture.setImageBitmap(selectedImageBitmap);

                    String bitmapString = BitMapToString(selectedImageBitmap);
                    sharedPreferences.edit().putString(
                                            USERIMAGE,bitmapString).commit();
                }
        }
    }

    /**
     * Update user information, either username, email or password, and send
     * this information in a POST to the API
     */
    private class UpdateInformation extends ApiTask<Response> {
        private String updatedInfo, accessToken;
        private int userID, option;

        public UpdateInformation(ServiceInterface service, int option,
                                 int userID, String accessToken,
                                 String updatedInfo) {
            super(service);
            this.option = option;
            this.userID = userID;
            this.accessToken = accessToken;
            this.updatedInfo = updatedInfo;
        }

        /**
         * Query the API to update the user info given, depending on the option
         * they chose
         *
         * @param service A serviceInterface object to interact with the API
         * @return A confirmation response
         */
        @Override
        public Response doQuery(ServiceInterface service) {
            //Provide the API with the userID, access token and the updated info
            if (option == CHANGE_USERNAME) {
                String usernameJson = "{\"username\": \"" + updatedInfo + "\"}";
                return service.updateUserInfo(
                        userID, accessToken, usernameJson);
            }
            if (option == CHANGE_EMAIL) {
                String emailJson = "{\"email\": \"" + updatedInfo + "\"}";
                return service.updateUserInfo(
                        userID, accessToken, emailJson);
            }
            if (option == CHANGE_PASSWORD) {
                String passwordJson = "{\"password\": \"" + updatedInfo + "\"}";
                return service.updateUserInfo(
                        userID, accessToken, passwordJson);
            }
            else { return null; }
        }

        /**
         * Depending on the option chosen by the user, give a successful
         * toast message and update shared preferences
         *
         * @param response A confirmation response object from the API call
         */
        @Override
        public void done(Response response) {
            //Update the app and navbar respectively
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (option == CHANGE_USERNAME) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Username successfully changed to "+updatedInfo,
                        Toast.LENGTH_LONG)
                        .show();
                usernameTextFinal.setText("Username:   "+updatedInfo+"\n");
                usernameText.setText(updatedInfo);
                editor.putString(USERNAME, updatedInfo);
                editor.commit();
            }
            if (option == CHANGE_EMAIL) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Email successfully changed to " + updatedInfo,
                        Toast.LENGTH_LONG)
                        .show();
                emailTextFinal.setText("Email:   "+updatedInfo+"\n");
                emailText.setText(updatedInfo);
                editor.putString(EMAIL, updatedInfo);
                editor.commit();
            }
            if (option == CHANGE_PASSWORD) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Successfully changed password",
                        Toast.LENGTH_LONG)
                        .show();
            }
        }

        /**
         * If the API call fails, print the appropriate toast message to the
         * screen
         */
        @Override
        public void failed(Exception e) {
            //Send appropriate error messages if failed
            if (option == CHANGE_USERNAME) {
                Toast.makeText(getActivity().getApplicationContext(),
                    "Error: Username already exists",
                    Toast.LENGTH_LONG)
                    .show();
            }
            if (option == CHANGE_EMAIL) {
                Toast.makeText(getActivity().getApplicationContext(),
                    "Error: Email already used",
                    Toast.LENGTH_LONG)
                    .show();
            }
        }
    }

    /**
     * Check if an email string is a valid email string
     *
     * @param target A given character sequence
     * @return True if the email is valid, false if not
     */
    public static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * Convert the user image bitmap to a string for storage purposes
     *
     * @param bitmap The user image bitmap
     * @return A base-64 string representing the given bitmap
     */
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    /**
     * Convert a base-64 string to a bitmap
     *
     * @param encodedString An encoded bitmap string
     * @return Bitmap (from given string)
     */
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
