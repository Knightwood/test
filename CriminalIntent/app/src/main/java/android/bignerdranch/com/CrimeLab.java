package android.bignerdranch.com;

import android.bignerdranch.com.database.CrimeBaseHelper;
import android.bignerdranch.com.database.CrimeCursorWrapper;
import android.bignerdranch.com.database.CrimeDbSchema;
import android.bignerdranch.com.database.CrimeDbSchema.CrimeTable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    //private List<Crime> mCrimes;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context context){
//context参数在第14章用到
        //mCrimes = new ArrayList<>();
        /*
        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0); // Every other one
            mCrimes.add(crime);
        }
        */

        mContext=context.getApplicationContext();
        mDatabase=new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public static CrimeLab get(Context context){
        if(sCrimeLab==null){
            sCrimeLab=new CrimeLab(context);
        }
        return sCrimeLab;

    }

    public List<Crime> getCrimes(){

        //return mCrimes;
        List<Crime> crimes =new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null,null);

        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID id){
        /*for(Crime crime:mCrimes){
            if(crime.getId().equals(id)){
                return crime;
            }
        }*/
        CrimeCursorWrapper cursor = queryCrimes(CrimeTable.Cols.UUID + " = ?", new String[] { id.toString() });
        try{
            if(cursor.getCount()==0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        }finally {
            cursor.close();
        }
        //return null;
    }
    public void addCrime(Crime c){
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME,null,values);
        //mCrimes.add(c);
    }
    public void deleteCrime(Crime c){
        String s=c.getId().toString();
        mDatabase.delete(CrimeTable.NAME,CrimeTable.Cols.UUID+"=?",new String[]{s});

    }
    private static ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID,crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED,crime.isSolved()? 1:0);
        values.put(CrimeTable.Cols.SUSPECT,crime.getSuspect());
        return values;
    }
    public void updateCrime(Crime crime){
        String uuidString = crime.getId().toString();
        ContentValues values=getContentValues(crime);
        mDatabase.update(CrimeTable.NAME,values,CrimeTable.Cols.UUID + "= ?",new String[] { uuidString });
    /*
    * Cursor c = db.rawQuery("select * from user where id=?",new Stirng[]{"1"});
    * select语句。
     * table: the table to update in
     *
     * values:   a map from column names to new column values. null is a
     *            valid value that will be translated to NULL.
     *
     * where:     String:Clause the optional WHERE clause to apply when updating.
     *            Passing null will update all rows.
     *
     * whereArgs: String:You may include ?s in the where clause, which
     *            will be replaced by the values from whereArgs. The values
     *            will be bound as Strings
     * */
    }
    //private Cursor queryCrimes(String whereClause,String[] whereArgs){
      private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        //return cursor;
          return new CrimeCursorWrapper(cursor);
    }
}
