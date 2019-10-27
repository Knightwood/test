package com.crystal.android.beatbox;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeatBox {
    private static final String TAG = "BeatBox";

    private static final String SOUNDS_FOLDER = "sample_sounds";
    private static final int MAX_SOUNDS = 5;

    private AssetManager mAssets;
    private List<Sound> mSounds = new ArrayList<>();
    private SoundPool mSoundPool;

    public BeatBox(Context context) {
        //获取asset
        mAssets = context.getAssets();
        //创建soundpool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            AudioAttributes audioAttributes = null;
            /*
             * setUsage用来设置用途(比如是游戏还是媒体),
             * setContentType用来设置内容类型(比如是视频还是音乐)*/
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(MAX_SOUNDS)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        }
        //执行loadSound()
        loadSounds();
    }

    private void loadSounds() {
        //把所有文件路径和名称存为Sound对象，然后把文件载入内存。
        String[] soundNames;
        try {
            soundNames = mAssets.list(SOUNDS_FOLDER);
            Log.d(TAG, "Found " + soundNames.length + " sounds");
        } catch (IOException ioe) {
            Log.e(TAG, "Could not list assets", ioe);
            return;
        }
        for (String filename : soundNames) {
            try {
                //路径加上文件名组成完整的路径，
                String assetPath = SOUNDS_FOLDER + "/" + filename;
                //用完整的路径new一个Sound对象
                Sound sound = new Sound(assetPath);
                //执行load()方法，载入内存
                load(sound);
                //把刚刚new出来的sound实例存入mSound这个ArrayList
                mSounds.add(sound);
            } catch (IOException ioe) {
                Log.e(TAG, "Could not load sound " + filename, ioe);
            }
        }
    }

    private void load(Sound sound) throws IOException {
        //获取afd，通过load(afd,1)获取id，把id存进sound对象
        AssetFileDescriptor afd = mAssets.openFd(sound.getAssetPath());
        int soundId = mSoundPool.load(afd, 1);
        sound.setSoundId(soundId);
    }

    public void play(Sound sound) {
        //上面已经处理过了，sound有了id，就可以直接执行play()方法播放音乐
        Integer soundId = sound.getSoundId();
        if (soundId == null) {
            return;
        }
        mSoundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }
    public void controlProcess(int id,float rate){
        mSoundPool.play(id,1.0f,1.0f,1,0,rate);
    }

    public void relase(){
        mSoundPool.release();
    }

    public List<Sound> getSounds() {
        return mSounds;
    }
}
