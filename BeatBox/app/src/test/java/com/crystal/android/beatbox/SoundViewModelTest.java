package com.crystal.android.beatbox;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SoundViewModelTest {
    private BeatBox mBeatBox;
    private Sound mSound;
    private SoundViewModel mSubject;

    @Before
    public void setUp() throws Exception {
        mBeatBox = mock(BeatBox.class);
        mSound = new Sound("assetPath");
        mSubject = new SoundViewModel(mBeatBox);
        mSubject.setSound(mSound);
    }

    @Test
    public void exposesSoundNameAsTitle() {
        Assert.assertThat(mSubject.getTitle(), is(mSound.getName()));
    }

    @Test
    public void callsBeatBoxPlayOnButtonClicked() {
        mSubject.onButtonClicked();
        /*
        * 调用verify(Object)方法，确认onButtonClicked()方法调用了
        * BeatBox.play(Sound)方法。
        * 调用verify(mBeatBox)方法就是说：“我要验证mBeatBox对象的某个方法是否调用了。”紧
        * 跟的mBeatBox.play(mSound)方法是说：“验证这个方法是这样调用的。
        * ”所以，合起来就是说： “验证以mSound作为参数，调用了mBeatBox对象的play(...)方法。”*/
        verify(mBeatBox).play(mSound);
    }
}