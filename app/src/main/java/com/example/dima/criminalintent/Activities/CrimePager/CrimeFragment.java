package com.example.dima.criminalintent.Activities.CrimePager;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.dima.criminalintent.Activities.CrimeList.CrimeListFragment;
import com.example.dima.criminalintent.Crimes.Crime;
import com.example.dima.criminalintent.Crimes.CrimeLab;
import com.example.dima.criminalintent.PictureUtils;
import com.example.dima.criminalintent.R;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

import static com.example.dima.criminalintent.Activities.CrimePager.DeleteCrimeFragment.EXTRA_DIALOG_RESULT;

/**
 * Created by Dima on 06.09.2016.
 */
public class CrimeFragment extends Fragment
                            implements  TextWatcher,
                                        CompoundButton.OnCheckedChangeListener,
                                        View.OnClickListener
{
    private Crime mCrime;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;
    private CheckBox mSolvedCheckBox;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private ImageView mDeleteCrimeImage;
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CRIME_DELETE = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;
    private static final int REQUEST_CRIME_PHOTO = 4;
    private CrimeFragment.Callbacks mCallbacks;

    public interface Callbacks{
        void onCrimeUpdated(Crime crime);
        void onCrimeDeleted(Crime crime);
    }

    //подключение фрагмента к активности
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (CrimeFragment.Callbacks) activity;
    }

    //отключение фрагмента от активности
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public static CrimeFragment newInstance(UUID uuid){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, uuid);
        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }

    //для выбора подозреваемого из списка контактов
    final Intent pickContact = new Intent(Intent.ACTION_PICK,
                                          ContactsContract.Contacts.CONTENT_URI);

    //для фотоснимка места преступления
    final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID id = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.getCrimeLab(getActivity()).getCrime(id);
        mPhotoFile = CrimeLab.getCrimeLab(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.getCrimeLab(getActivity()).updateCrime(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container, false );

        //если нет подходящей активности для выбора подозреваемого из списка контактов, отключить
        //+ кнопку выбора подозреваемого
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
            mCallSuspectButton.setEnabled(false);
        }

        //если определен путь для хранения фотоснимка и имеется походящая активность, задать
        //+ параметры интента
        boolean canTakePhoto =
                (mPhotoFile != null) && (captureImage.resolveActivity(packageManager) != null);
        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton = (ImageButton) view.findViewById(R.id.crime_camera);
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(this);

        mPhotoView = (ImageView) view.findViewById(R.id.crime_photo);
        mPhotoView.setOnClickListener(this);
        updatePhotoView();

        String formattedDate = DateFormat.getMediumDateFormat(getContext()).format(mCrime.getDate());
        mDateButton = (Button) view.findViewById(R.id.crime_date);
        mDateButton.setText(formattedDate);
        mDateButton.setOnClickListener(this);

        mReportButton = (Button) view.findViewById(R.id.crime_report_button);
        mReportButton.setOnClickListener(this);

        mSuspectButton = (Button) view.findViewById(R.id.crime_suspect_button);
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }
        mSuspectButton.setOnClickListener(this);

        mCallSuspectButton = (Button) view.findViewById(R.id.crime_call_suspect_button);
        mCallSuspectButton.setOnClickListener(this);

        mSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved) ;
        mSolvedCheckBox.setOnCheckedChangeListener(this);

        mTitleField = (EditText) view.findViewById(R.id.crime_title);
        mTitleField.addTextChangedListener(this);

        mTitleField.setText(mCrime.getTitle());
        mSolvedCheckBox.setChecked(mCrime.isSolved());

        mDeleteCrimeImage = (ImageView) view.findViewById(R.id.imageDeleteCrime);
        mDeleteCrimeImage.setOnClickListener(this);
        return view;
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        mCrime.setSolved(isChecked);
        updateCrime();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mCrime.setTitle(charSequence.toString());
        updateCrime();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.crime_date:
                FragmentManager fragmentManager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fragmentManager, DIALOG_DATE);
                break;
            case R.id.imageDeleteCrime:
                DialogFragment deleteCrimeDialog = new DeleteCrimeFragment();
                deleteCrimeDialog.setTargetFragment(CrimeFragment.this, REQUEST_CRIME_DELETE);
                deleteCrimeDialog.show(getFragmentManager(), "Crime delete");
                break;
            case R.id.crime_report_button:
                //отправить сообщение о преступлении через любое подходящее приложение
                ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_suspect))
                        .setChooserTitle(getString(R.string.send_report));
                intentBuilder.startChooser();
                break;
            case R.id.crime_suspect_button:
                startActivityForResult(pickContact, REQUEST_CONTACT);
                break;
            case R.id.crime_call_suspect_button:
                if (mCrime.getSuspect() != null) {
                    callSuspect();
                }
                break;
            case R.id.crime_camera:
                startActivityForResult(captureImage, REQUEST_PHOTO);
                break;
            case R.id.crime_photo:
                if (mPhotoFile == null || !mPhotoFile.exists()) {
                    return;
                }
                //вывести изображение в увеличенном виде
                CrimePictureFragment crimePictureDialog =
                                    CrimePictureFragment.getInstance(mPhotoFile.getPath());
                crimePictureDialog.setTargetFragment(CrimeFragment.this, REQUEST_CRIME_PHOTO);
                crimePictureDialog.show(getFragmentManager(), mPhotoFile.getName());
                break;
            default:
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!= Activity.RESULT_OK){
            return;
        }
        if(requestCode==REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            String formattedDate = DateFormat.getMediumDateFormat(getContext()).format(mCrime.getDate());
            mDateButton.setText(formattedDate.toString());
            updateCrime();
        }
        if(requestCode==REQUEST_CRIME_DELETE){
            boolean deleteConfirmed = data.getBooleanExtra(EXTRA_DIALOG_RESULT, false);
            if(deleteConfirmed){
                mCallbacks.onCrimeDeleted(mCrime);
//                CrimeLab.getCrimeLab(getActivity()).deleteCrime(mCrime.getId());
//                updateCrime();
//                getActivity().finish();
            }
        }
        if ((requestCode == REQUEST_CONTACT)&&(data!=null)) {
            Uri contactUri = data.getData();
            String [] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            Cursor c = getActivity().getContentResolver().query(contactUri,
                                                                queryFields,
                                                                null, null, null);
            try{
                if (c.getCount() == 0) {
                    return;
                }
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
                updateCrime();
            }
            finally {
                c.close();
            }

        }
        if (requestCode == REQUEST_PHOTO) {
            updatePhotoView();
            updateCrime();
        }
    }

    private String getCrimeReport(){
        String solvedString = null;
        if(mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        }
        else{
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if(suspect == null){
            suspect = getString(R.string.crime_report_no_suspect);
        }
        else{
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    private void callSuspect(){
        String phoneNumber = null;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        //получить идентификатор контакта
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(  CONTENT_URI,
                                                null,
                                                DISPLAY_NAME + " = ?",
                                                new String[]{mCrime.getSuspect()},
                                                null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return;
        }
        cursor.moveToFirst();
        String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
        int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
        if (hasPhoneNumber == 0) {
            cursor.close();
            return;
        }

        //получить телефонный номер
        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI,
                                                    null,
                                                    Phone_CONTACT_ID + " = ?",
                                                    new String[] { contact_id },
                                                    null);
        if(phoneCursor.getCount()==0){
            cursor.close();
            phoneCursor.close();
            return;
        }
        phoneCursor.moveToFirst();
        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
        Uri number = Uri.parse("tel:"+phoneNumber);
        cursor.close();
        phoneCursor.close();

        //запустить телефонное приложение
        Intent callContact = new Intent(Intent.ACTION_DIAL,
                                        number);
        startActivity(callContact);

    }

    private void updatePhotoView(){
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
            mPhotoView.setBackgroundColor(Color.GRAY);
        }
        else{
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private void updateCrime(){
        CrimeLab.getCrimeLab(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    public void setCrimeSolvedStateIn(boolean state){
        mSolvedCheckBox.setChecked(state);
    }

    public UUID getCrimeId() {
        return mCrime.getId();
    }
}
