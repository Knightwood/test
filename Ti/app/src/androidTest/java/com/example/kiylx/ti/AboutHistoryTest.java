package com.example.kiylx.ti;

import android.content.Context;

import androidx.test.InstrumentationRegistry;

import com.example.kiylx.ti.trash.AboutHistory;
import com.example.kiylx.ti.mvp.model.WebPage_Info;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class AboutHistoryTest {
    private AboutHistory mAboutHistory;
    Context mContext;
    @Before
    public void setUp() throws Exception {
        mContext= InstrumentationRegistry.getContext();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void get() {
    }

    @Test
    public void getHistoryInfo() {
    }

    @Test
    public void getHistoryInfos() {
    }

    @Test
    public void addToDataBase() {
    }

    @Test
    public void update() {
    }

    @Test
    public void delete() {
    }

    @Test
    public void deleteAll() {
    }

    @Test
    public void getInfoFromDate() {
        mAboutHistory=AboutHistory.get(mContext);
        ArrayList<WebPage_Info> items= genItem();
        for (WebPage_Info i:items
             ) {
            mAboutHistory.addToDataBase(i);

        }

    }

    private ArrayList<WebPage_Info> genItem() {
        ArrayList<WebPage_Info> tmp=new ArrayList<>();
        String[] datearr = new String[]{"2019-06-03","2019-06-05","2019-07-01","2019-09-08","2019-09-11"};
        for (int i=0;i<10;i++){
            tmp.add(new WebPage_Info("title"+1,"null",null,1,datearr[i]));
        }
        return tmp;
    }
}