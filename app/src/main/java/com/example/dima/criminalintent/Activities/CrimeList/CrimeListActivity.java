package com.example.dima.criminalintent.Activities.CrimeList;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.dima.criminalintent.Activities.CrimePager.CrimeFragment;
import com.example.dima.criminalintent.Activities.CrimePager.CrimePagerActivity;
import com.example.dima.criminalintent.Activities.SingleFragmentActivity;
import com.example.dima.criminalintent.Crimes.Crime;
import com.example.dima.criminalintent.R;

/**
 * Created by Dima on 08.09.2016.
 */
public class CrimeListActivity extends SingleFragmentActivity
            implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks
{
    private static int sSelectedItemIndex = -1;

    public static void storeSelectedItemPosition(int itemPosition){
        sSelectedItemIndex = itemPosition;
    }

    public static int getSelectedItemPosition(){
        return sSelectedItemIndex;
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        }
        else{
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
                                            .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
