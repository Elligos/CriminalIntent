package com.example.dima.criminalintent.Activities.CrimeList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dima.criminalintent.Crimes.Crime;
import com.example.dima.criminalintent.Crimes.CrimeLab;
import com.example.dima.criminalintent.Activities.CrimePager.CrimePagerActivity;
import com.example.dima.criminalintent.R;

import java.util.List;

/**
 * Created by Dima on 08.09.2016.
 */
public class CrimeListFragment extends Fragment{
    private RecyclerView mCrimeRecyclerView;
    private LinearLayout mNoCrimeView;
    private CrimeAdapter mAdapter;
    private NoCrimeAdapter mNoCrimeAdapter;
    private boolean mSubtitleVisible = false;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private Callbacks mCallbacks;

    //определение интерфейса, который обязана имплементировать активность-хост
    public interface Callbacks{
        void onCrimeSelected(Crime crime);
        void onCrimeSolvedChanged(Crime crime);
    }

    @Override
    //действия при подключении фрагмента к активности-хосту
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    //действия при отсоединении фрагмента от активности-хоста
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_crime_list,container, false);
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));//назначить для
            //+ для RecyclerView любой LayoutManager, который будет заниматься размещением элементов
            //+ на экране
        if(savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE, false);
        }
        updateUI();
        return view;
    }

    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        int crimesSize = crimes.size();

        updateSubtitle();
        //если в списке пока нет преступлений, добавить элемент для создания нового преступления
        if(crimesSize==0){
            if(mNoCrimeAdapter == null) {
                mNoCrimeAdapter = new NoCrimeAdapter(crimeLab);
                mCrimeRecyclerView.setAdapter(mNoCrimeAdapter);
            }
            else{
                mNoCrimeAdapter.notifyDataSetChanged();
            }
            return;
        }
        //если есть, отобразить список преступлений
        if(mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else{
            /*TODO: можно улучшить, сделать обновление ролей только измененных преступлений
            int position = CrimeListActivity.getSelectedItemPosition();
            if(position != -1) {
                mAdapter.notifyItemChanged(position);
            }
            */
            //обновить все поля
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem sutitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            sutitleItem.setTitle(R.string.hide_subtitle);
        } else {
            sutitleItem.setTitle(R.string.show_subtitle);
        }
        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.getCrimeLab(getActivity()).addCrime(crime);
                updateUI();
                mCallbacks.onCrimeSelected(crime);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //обновить информацию в панели инструментов
    private void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.getCrimeLab(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);
        if(mSubtitleVisible==false){
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    //сохранить позицию преступления
    //TODO: переделать для сохранения изменений в нескольких преступлениях
    static public void storeLastSelectedItemPosition(int position){
        CrimeListActivity.storeSelectedItemPosition(position);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

}

class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
    private List<Crime> mCrimes;

    public CrimeAdapter(List<Crime> crimes){
        mCrimes = crimes;
    }


    @Override
    public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_item_crime,
                parent,
                false);
        return new CrimeHolder(view);
    }

    @Override
    public void onBindViewHolder(CrimeHolder holder, int position) {
        Crime crime = mCrimes.get(position);
        holder.bindCrime(crime);
    }

    @Override
    public int getItemCount() {
        return mCrimes.size();
    }

    public void setCrimes(List<Crime> crimes){
        mCrimes = crimes;
    }
}

class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener
{
    private TextView mTitleTextView;
    private TextView mDateTextView;
    private CheckBox mSolvedCheckBox;

    private Crime mCrime;

    private java.text.DateFormat mMediumDateFormat;
    private String mFormattedDate;

    private CrimeListFragment.Callbacks mCallbacks;

    public CrimeHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mCallbacks = (CrimeListFragment.Callbacks) itemView.getContext();
        mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
        mDateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
        mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_ceck_box);
        mSolvedCheckBox.setOnCheckedChangeListener(this);
        mMediumDateFormat = DateFormat.getMediumDateFormat(itemView.getContext());
    }

    public void bindCrime(Crime crime) {
        mCrime = crime;
        mTitleTextView.setText(mCrime.getTitle());
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mFormattedDate = mMediumDateFormat.format(mCrime.getDate());
        mDateTextView.setText(mCrime.getDate().toString());
    }

    @Override
    public void onClick(View view) {
        storeItemPosition(view);
        mCallbacks.onCrimeSelected(mCrime);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        mCrime.setSolved(isChecked);
        mCallbacks.onCrimeSolvedChanged(mCrime);
    }

    //сохранить позицию выбранного поля
    private void storeItemPosition(View view){
        int position = getAdapterPosition();
        CrimeListFragment.storeLastSelectedItemPosition(position);
    }
}