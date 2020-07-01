package com.crystal.dataautosave;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);

        String num = "123";
        System.out.println(Double.valueOf(num));

        student student;

        List<student> iop = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            student = new student(i + 10);
            iop.add(student);
        }
        for (student i:iop) {
            System.out.println(i.age);
        }
        PreferenceTools.pr();

    }

    class student {
        public student(int age) {
            this.age = age;
        }

        public int age = 24;
    }

    public static class PreferenceTools {

        static {
            System.out.println("static触发了");
        }

        public static void pr() {
            System.out.println("普通方法");
        }

        public static void pr2() {
            System.out.println("普通方法2");
        }

        {
            System.out.println("普通块");

        }

    }

}