package android.bignerdranch.com.database;

import android.bignerdranch.com.Crime;
import android.bignerdranch.com.database.CrimeDbSchema.CrimeTable;
import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Crime getCrime(){
        /*getString(int columnIndex);   //数据库的一个操作方法，返回指定行的元素
        getColumnIndex返回指定行的最开始的第0个元素，或是不存在时返回-1*/
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date=getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect=getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        Crime crime=new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved !=0);
        crime.setSuspect(suspect);

        return crime;

        //return null;
    }
}
