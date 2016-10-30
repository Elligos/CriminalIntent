package com.example.dima.criminalintent.Activities.CrimePager;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.dima.criminalintent.PictureUtils;
import com.example.dima.criminalintent.R;

/**
 * Created by Dima on 10.10.2016.
 */


//фрагмент для показа изображения в увеличенном виде
public class CrimePictureFragment extends DialogFragment implements View.OnClickListener{
    private static final String ARG_PHOTO_PATH = "photo_path";
    ImageView mPhotoView;

    static public CrimePictureFragment getInstance(String path){
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO_PATH, path);

        CrimePictureFragment fragment = new CrimePictureFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_crime_photo, null);
        String path = (String) getArguments().getSerializable(ARG_PHOTO_PATH);

        Bitmap bitmap = PictureUtils.getScaledBitmap(path, getActivity());

        mPhotoView = (ImageView) view.findViewById(R.id.full_crime_photo_view);
        mPhotoView.setImageBitmap(bitmap);
        mPhotoView.setOnClickListener(this);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.full_crime_photo_view) {
            Intent intent = new Intent();
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            dismiss();
        }
    }
}
