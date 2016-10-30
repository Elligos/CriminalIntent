package com.example.dima.criminalintent.Crimes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.example.dima.criminalintent.database.CrimeBaseHelper;
import com.example.dima.criminalintent.database.CrimeDbSchema;
import com.example.dima.criminalintent.database.CrimeDbSchema.CrimeTable;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Dima on 07.09.2016.
 */
public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;
    private SQLiteDatabase mDatabase;
    private Context mContext;

    public static CrimeLab getCrimeLab(Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
        mCrimes = new ArrayList<>();
    }

    public Crime getCrime(UUID id){
        Crime crime;
        CrimeCursorWrapper cursor = queryCrimes(CrimeTable.Cols.UUID + " = ?",
                new String[]{id.toString()});

        try{
            if(cursor.getCount() == 0){
                return null;
            }
            cursor.moveToFirst();
            crime = cursor.getCrime();
            return crime;
        }
        finally {
            cursor.close();
        }
//        for(Crime mCrime : mCrimes){
//            if(mCrime.getId().equals(id)){
//                return mCrime;
//            }
//        }
//        return null;
    }

    public void updateCrime(Crime crime)
    {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeTable.Name, values, CrimeTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    public boolean deleteCrime(UUID id){
        mDatabase.delete(CrimeTable.Name,
                CrimeTable.Cols.UUID + " = ?",
                new String[]{id.toString()});
        return true;
    }

    public void addCrime(Crime c){
//        mCrimes.add(c);

        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.Name, null, values);

    }

    public List<Crime> getCrimes(){
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);//получить все преступления из БД

        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }
        finally {
            cursor.close();
        }
        return crimes;
    }

    private static ContentValues getContentValues(Crime crime){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CrimeTable.Cols.UUID, crime.getId().toString());
        contentValues.put(CrimeTable.Cols.TITLE, crime.getTitle());
        contentValues.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        contentValues.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        contentValues.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());

        return contentValues;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String [] whereArgs)
    {
        Cursor cursor = mDatabase.query(
                CrimeTable.Name,//table
                null,//columns
                whereClause,
                whereArgs,
                null,//groupBy
                null,//having
                null);//orderBy
        return new CrimeCursorWrapper(cursor);
    }

    //получить путь, по которому должен располагаться фотоснимок места преступления
    public File getPhotoFile(Crime crime){
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFilesDir == null) {
            return null;
        }
        return new File(externalFilesDir, crime.getPhotoFilename());
    }
}
