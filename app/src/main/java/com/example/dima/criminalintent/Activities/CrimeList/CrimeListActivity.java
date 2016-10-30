package com.example.dima.criminalintent.Activities.CrimeList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.dima.criminalintent.Activities.CrimePager.CrimeFragment;
import com.example.dima.criminalintent.Activities.CrimePager.CrimePagerActivity;
import com.example.dima.criminalintent.Activities.SingleFragmentActivity;
import com.example.dima.criminalintent.Crimes.Crime;
import com.example.dima.criminalintent.Crimes.CrimeLab;
import com.example.dima.criminalintent.R;

/**
 * Created by Dima on 08.09.2016.
 */
public class CrimeListActivity extends SingleFragmentActivity
            implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks
{
    private static int sSelectedItemIndex = -1;
    private CrimeFragment mDetailFragment;

    public static void storeSelectedItemPosition(int itemPosition){
        sSelectedItemIndex = itemPosition;
    }

    public static int getSelectedItemPosition(){
        return sSelectedItemIndex;
    }

    @Override
    //реализация функции создания фрагмента
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //если приложение запущено на планшете
        if (findViewById(R.id.detail_fragment_container) != null) {
            mDetailFragment = (CrimeFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.detail_fragment_container);
            if(mDetailFragment == null){
                return;
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, mDetailFragment)
                    .commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    //переопределение функции получения идентификатора макета, который должна заполнить активность
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    //реализация функции обработки выбора преступления из списка фрагмента CrimeListFragment
    public void onCrimeSelected(Crime crime) {
        //если приложение запущено на смартфоне
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        }
        //если приложение запущено на планшете
        else{
            mDetailFragment = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, mDetailFragment)
                    .commit();
        }
    }

    @Override
    public void onCrimeSolvedChanged(Crime crime) {
        //обновить статус преступления
        CrimeLab.getCrimeLab(this).updateCrime(crime);
        if(mDetailFragment != null){
            //если фрагмент детализированного представления
            if(mDetailFragment.getCrimeId().equals(crime.getId())){
                mDetailFragment.setCrimeSolvedStateIn(crime.isSolved());
            }
        }
    }


    @Override
    //callback для обновления списка записи в главном фрагменте
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
                                            .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

    @Override
    public void onCrimeDeleted(Crime crime) {
        CrimeLab.getCrimeLab(this).deleteCrime(crime.getId());
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
        if (mDetailFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(mDetailFragment)
                    .commit();
            mDetailFragment = null;
        }
    }


}
