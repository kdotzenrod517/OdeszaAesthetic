package com.kdotz.videodemo;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    VideoView videoView;
    private static int currentVideo = 0;
    private List<String> fileData = new ArrayList<String>();
    MediaPlayer mediaPlayer;
    AudioManager audioManager;

    public void play(View view) {
        mediaPlayer.start();
    }

    public void pause(View view) {
        mediaPlayer.pause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        final int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        final int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mediaPlayer = MediaPlayer.create(this, R.raw.odesza);
        final SeekBar volumeControl = (SeekBar) findViewById(R.id.volumeSeekBar);
        final SeekBar scrubControl = (SeekBar) findViewById(R.id.scrubSeekBar);
        volumeControl.setMax(maxVolume);
        volumeControl.setProgress(currentVolume);

        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("Seekbar changed", Integer.toString(progress));
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        scrubControl.setMax(mediaPlayer.getDuration());
        scrubControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.start();
            }
        });

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                scrubControl.setProgress(mediaPlayer.getCurrentPosition());
            }
        }, 0, 1000);

        fileData.add("android.resource://" + getPackageName() + "/" + R.raw.flower);
        fileData.add("android.resource://" + getPackageName() + "/" + R.raw.waterfall);
        fileData.add("android.resource://" + getPackageName() + "/" + R.raw.bird);
        fileData.add("android.resource://" + getPackageName() + "/" + R.raw.space);
        fileData.add("android.resource://" + getPackageName() + "/" + R.raw.ocean);
        fileData.add("android.resource://" + getPackageName() + "/" + R.raw.snow);
        fileData.add("android.resource://" + getPackageName() + "/" + R.raw.grain);
        fileData.add("android.resource://" + getPackageName() + "/" + R.raw.aerial);

        videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.bird);

        MediaController mediaController = new MediaController(this);
        mediaController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentVideo != fileData.size()) {
                    Uri nextUri = Uri.parse(fileData.get(currentVideo++));
                    videoView.setVideoURI(nextUri);
                    videoView.start();
                } else {
                    currentVideo = 0;
                    Uri nextUri = Uri.parse(fileData.get(currentVideo++));
                    videoView.setVideoURI(nextUri);
                    videoView.start();
                }
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentVideo != 0 && currentVideo != fileData.size()) {
                    Uri nextUri = Uri.parse(fileData.get(currentVideo--));
                    videoView.setVideoURI(nextUri);
                    videoView.start();
                } else {
                    currentVideo = fileData.size() - 1;
                    Uri nextUri = Uri.parse(fileData.get(currentVideo));
                    videoView.setVideoURI(nextUri);
                    videoView.start();
                }
            }
        });
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!(currentVideo < fileData.size())) {
                    return;
                }
                Uri nextUri = Uri.parse(fileData.get(currentVideo++));
                videoView.setVideoURI(nextUri);
                videoView.start();

                if (currentVideo == fileData.size()) {
                    currentVideo = 0;
                }
            }
        });
    }
}
