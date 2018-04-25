package org.traccar.client.Profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.traccar.client.MainActivity;
import org.traccar.client.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.blurry.Blurry;

public class ProfileFragment extends Fragment {

    CircleImageView profileImage;
    String img;
    ImageView imageContainer;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v  =inflater.inflate(R.layout.fragment_profile,container,false);

        profileImage = v.findViewById(R.id.fragment_profile_image);
        ImageView addImage = v.findViewById(R.id.fragment_profile_add_image);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int MyVersion = Build.VERSION.SDK_INT;
                if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {

                    ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "انتخاب تصویر پروفایل"), 1);
                }

            }
        });

        // Init views
        imageContainer = v.findViewById(R.id.fragment_profile_image_container);
        Typeface sansLight = Typeface.createFromAsset(getContext().getAssets(),"fonts/sanslight.ttf");
        Typeface sansMedium = Typeface.createFromAsset(getContext().getAssets(),"fonts/sansmedium.ttf");
        TextInputLayout nameTIL = v.findViewById(R.id.fragment_profile_name_til);
        TextInputLayout lastNameTIL = v.findViewById(R.id.fragment_profile_lastname_til);
        TextInputLayout phoneTIL = v.findViewById(R.id.fragment_profile_phone_til);

        final EditText name = v.findViewById(R.id.fragment_profile_name);
        final EditText lastName = v.findViewById(R.id.fragment_profile_lastname);
        final EditText phone = v.findViewById(R.id.fragment_profile_phone);

        TextView subject = v.findViewById(R.id.fragment_profile_subject);
        TextView deviceSubject = v.findViewById(R.id.fragment_profile_phone_name_tv);
        TextView device = v.findViewById(R.id.fragment_profile_phone_name);
        device.setTypeface(sansLight);
        deviceSubject.setTypeface(sansMedium);
        subject.setTypeface(sansMedium);

        Button submit = v.findViewById(R.id.fragment_profile_submit);

        nameTIL.setTypeface(sansLight);
        lastNameTIL.setTypeface(sansLight);
        phoneTIL.setTypeface(sansLight);
        name.setTypeface(sansLight);
        lastName.setTypeface(sansLight);
        phone.setTypeface(sansLight);
        submit.setTypeface(sansMedium);

        // get phone model
        device.setText(Build.MODEL);

        // submit form
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserItem user = new UserItem();
                if(name.getText().toString().equals("") || lastName.getText().toString().equals("") || phone.getText().toString().equals("") || img.equals("")){
                    Toast.makeText(getContext(),"لطفا اطلاعات خواسته شده را تکمیل کنید",Toast.LENGTH_LONG).show();
                }else{
                    user.setDevice(Build.MODEL);
                    user.setImg(img);
                    user.setLast(lastName.getText().toString());
                    user.setName(name.getText().toString());
                    user.setPhone(phone.getText().toString());
                    DbHelper helper = new DbHelper(getContext());
                    helper.insertUser(user);
                    Toast.makeText(getContext(),"اطلاعات کاربری شما ثبت شد",Toast.LENGTH_LONG).show();
                    ((MainActivity)getContext()).updateDrawerProfile();
                }
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            Uri selectedImage = data.getData();
                if (selectedImage != null && selectedImage.toString().length() > 0) {
                    try {
                        final String extractUriFrom = selectedImage.toString();
                        String firstExtraction = extractUriFrom.contains("com.google.android.apps.photos.contentprovider") ? extractUriFrom.split("/1/")[1] : extractUriFrom;
                        firstExtraction = firstExtraction.contains("/ACTUAL") ? firstExtraction.replace("/ACTUAL", "") : firstExtraction;

                        String secondExtraction = URLDecoder.decode(firstExtraction, "UTF-8");
                        selectedImage = Uri.parse(secondExtraction);
                        img = getRealPathFromURI_API19(getContext(),selectedImage);
                        Bitmap bitmap = BitmapFactory.decodeFile(img);
                        profileImage.setImageBitmap(bitmap);

                        //Blur container's background
                        Blurry.with(getContext()).from(bitmap).into(imageContainer);
                        Log.e("PATH",getRealPathFromURI_API19(getContext(),selectedImage)+" ");
                    } catch (UnsupportedEncodingException ignored) {

                    } catch (Exception ignored) {
                        Log.e("EXCEPTION",ignored.getLocalizedMessage());
                    }
            }
        }
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if(cursor != null){
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }


}
