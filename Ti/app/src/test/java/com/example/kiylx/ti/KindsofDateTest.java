package com.example.kiylx.ti;

import com.example.kiylx.ti.model.KindsofDate;

import org.junit.Test;

public class KindsofDateTest {

    @Test
    public void name() {
        System.out.println(KindsofDate.MONTH1);
    }

    @Test
    public void compareTo() {
    }

    @Test
    public void valueOf() {
        System.out.println(KindsofDate.valueOf("THISWEEK"));
    }
}