package com.example.dima.criminalintent.Activities.CrimePager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.dima.criminalintent.R;

/**
 * Created by Dima on 25.09.2016.
 */

//диалог подтверждения удаления преступления
public class DeleteCrimeFragment extends DialogFragment implements View.OnClickListener {

    Button mOkButton;
    Button mCancelButton;
    public static final String EXTRA_DIALOG_RESULT = "com.example.dima.criminalintent.crime_delete_dialog_result";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_delete_crime, null);
        mOkButton = (Button) view.findViewById(R.id.confirm_delete_crime);
        mOkButton.setOnClickListener(this);

        mCancelButton = (Button) view.findViewById(R.id.cancel_delete_crime);
        mCancelButton.setOnClickListener(this);

        return new AlertDialog.Builder(getActivity())
                .setTitle("Do you really want to delete crime?")
                .setView(view)
                .create();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.confirm_delete_crime:
                Toast.makeText(view.getContext(), "Ok", Toast.LENGTH_SHORT).show();
                sendResult(true);
                dismiss();
                break;
            case R.id.cancel_delete_crime:
                Toast.makeText(view.getContext(), "Cancel", Toast.LENGTH_SHORT).show();
                sendResult(false);
                dismiss();
                break;
            default:
                break;
        }

    }

    public void sendResult(boolean crimeDeleteConfirmed)
    {
        if(getTargetFragment() == null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DIALOG_RESULT, crimeDeleteConfirmed);
        getTargetFragment().onActivityResult(getTargetRequestCode() ,Activity.RESULT_OK, intent);
    }

}
