package com.example.kiylx.ti;

import com.example.kiylx.ti.model.WebPage_Info;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.SimpleTimeZone;

import static org.junit.Assert.*;

public class AboutHistoryTest {

    @Before
    public void setUp() throws Exception {
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
        genItem();


    }

    void genItem() {
        ArrayList<WebPage_Info> tmp=new ArrayList<>();
        String[] datearr = new String[]{"2019-06-03","2019-06-05","2019-07-01","2019-09-08","2019-09-11"};
        for (int i=0;i<10;i++){
            tmp.add(new WebPage_Info("title"+1,"null",datearr[i]));
        }
    }
}