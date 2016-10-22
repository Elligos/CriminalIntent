package com.example.dima.criminalintent.Activities.CrimeList;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dima.criminalintent.Activities.CrimePager.CrimePagerActivity;
import com.example.dima.criminalintent.Crimes.Crime;
import com.example.dima.criminalintent.Crimes.CrimeLab;
import com.example.dima.criminalintent.R;

import java.util.List;

/**
 * Created by Dima on 25.09.2016.
 */

public class NoCrimeAdapter extends RecyclerView.Adapter<NoCrimeHolder> {

    private List<Crime> mCrimes;
    private Crime mEmptyCrime;
    private CrimeLab mCrimeLab;

    public NoCrimeAdapter(CrimeLab crimeLab){
        mCrimeLab = crimeLab;
    }

    @Override
    public NoCrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext()/*getActivity()*/);
        View view = layoutInflater.inflate(R.layout.list_item_no_crime, parent, false);
        return new NoCrimeHolder(view);
    }

    @Override
    public void onBindViewHolder(NoCrimeHolder holder, int position) {
        holder.bindWithEmptyCrime(mCrimeLab);
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}


class NoCrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    Crime mNewCrime;
    CrimeLab mCrimeLab;

    public NoCrimeHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mNewCrime = new Crime();
        mCrimeLab.addCrime(mNewCrime);
        Intent intent = CrimePagerActivity.newIntent(view.getContext(), mNewCrime.getId());
        view.getContext().startActivity(intent);
    }

    public void bindWithEmptyCrime(CrimeLab crimeLab){
        mCrimeLab = crimeLab;
    }

}