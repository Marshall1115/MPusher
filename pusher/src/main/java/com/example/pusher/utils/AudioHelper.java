package com.example.pusher.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;

import com.example.pusher.pusher.PusherEnv;

import java.io.DataOutputStream;

import static android.media.AudioFormat.CHANNEL_CONFIGURATION_STEREO;

/**
 * Created by Administrator on 2017/9/21.
 */

public class AudioHelper {
    private int bufferSize;
    private AudioRecord audioRecord;
    private AudioManager audioManager;
    private DataOutputStream dos;
    private int sampleRate;
    private int channels;
    private OnCollectAudioDataListener listener;

    public AudioHelper(int sampleRate, int channels) {
        this.sampleRate = sampleRate;
        this.channels = channels;
        if (channels == 1) {//单声道
            this.channels = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        } else {
            this.channels = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
        }
        init();
    }

    public void init() {
        audioRecord = getAudioRecord();
        //添加回声消除模块
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            audioManager = (AudioManager) PusherEnv.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);//通化模式
            audioManager.setSpeakerphoneOn(true);//设置为打开扬声器。免提电话,false使用听筒播放声音
            int audioSessionId = audioRecord.getAudioSessionId();
            boolean supportAcousticEchoCanceler = AcousticEchoCancelerUtils.isDeviceSupport();
            if (supportAcousticEchoCanceler) {
                AcousticEchoCancelerUtils.initAEC(audioSessionId);
                AcousticEchoCancelerUtils.setAECEnabled(true);
            }
        }
    }

    public boolean startRecord() {
        collectAudio();
        audioRecord.startRecording();
//        String input = new File(Environment.getExternalStorageDirectory(), "record.pcm").getAbsolutePath();
//        try {
//            dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File(input))));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        return true;
    }

    @TargetApi(18)
    public AudioRecord getAudioRecord() {
        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        int audioSource = MediaRecorder.AudioSource.MIC;
        bufferSize = AudioRecord.getMinBufferSize(sampleRate, channels, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(audioSource, sampleRate,
                channels, audioEncoding, bufferSize);
        return audioRecord;
    }

    private void collectAudio() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                //（采样率*通道数*采样位数/8（比特换算成字节，一字节8比特） ）=字节数  网上有说法指2048个采样算一帧PCM
                int PCMBufferSize = 1024 * 16 / 8;
                if (channels == CHANNEL_CONFIGURATION_STEREO) {//双声道
                    PCMBufferSize = PCMBufferSize * 2;//一秒的声音需要2k的存储空间
                }
                byte[] buffer = new byte[PCMBufferSize];
                while (!Thread.interrupted()) {
                    int length = buffer.length;
                    int bufferReadResult = audioRecord.read(buffer, 0, length);
                    if (listener != null) {
                        listener.onCollectAudioData(buffer, length);
                    }
//                    try {
//                        dos.write(buffer, 0, bufferReadResult);
//                    } catch (IOException e) {
//                    }
                }
            }
        }.start();
    }


    public boolean release() {
        if (audioManager != null) {
            audioManager.setMode(AudioManager.MODE_NORMAL);//通化模式
        }
        audioRecord.stop();
        audioRecord.release();
        AcousticEchoCancelerUtils.release();
        return true;
    }

    public void setOnCollectAudioDataListener(OnCollectAudioDataListener listener) {
        this.listener = listener;
    }

    public interface OnCollectAudioDataListener {
        void onCollectAudioData(byte[] buffer, int len);
    }
}
