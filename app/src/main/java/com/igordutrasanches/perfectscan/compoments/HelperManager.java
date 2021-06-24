
package com.igordutrasanches.perfectscan.compoments;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.igordutrasanches.perfectscan.R;

import java.io.Closeable;
import java.io.IOException;
import java.util.Locale;

public final class HelperManager implements MediaPlayer.OnErrorListener, Closeable {

  private static final String TAG = HelperManager.class.getSimpleName();

  private static final float BEEP_VOLUME = 0.10f;
  private static final long VIBRATE_DURATION = 200L;

  private Context context;
  private final Activity activity;
  private MediaPlayer mediaPlayer;
  private boolean playBeep;
  private boolean vibrate;

  public HelperManager(Activity activity, Context context) {
    this.activity = activity;
    this.context = context;
    this.mediaPlayer = null;
    updatePrefs();
    locutorLoader();
  }

  private TextToSpeech locutor;

  public void falar(String dialogo) {
    locutor.speak(dialogo, TextToSpeech.QUEUE_FLUSH, null);
  }

  public void parar() {
    if (locutor != null) {
      locutor.stop();
      locutor.shutdown();
      locutorLoader();
    }
  }

  private void locutorLoader() {
    locutor = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
      @Override
      public void onInit(int status) {
        if (status != TextToSpeech.ERROR) {
          locutor.setLanguage(Locale.getDefault());
          locutor.setSpeechRate(0);
        }
      }
    });
  }
  public synchronized void updatePrefs() {
    playBeep = shouldBeep(context, activity);
    vibrate = Key.getVibrate(context);
    if (playBeep && mediaPlayer == null) {
      // The volume on STREAM_SYSTEM is not adjustable, and users found it too loud,
      // so we now play on the music stream.
      activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
      mediaPlayer = buildMediaPlayer(activity);
    }
  }

  public synchronized void playBeepSoundAndVibrate() {
    if (playBeep && mediaPlayer != null) {
      mediaPlayer.start();
    }
    if (vibrate) {
      Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
      vibrator.vibrate(VIBRATE_DURATION);
    }
  }

  private static boolean shouldBeep(Context context, Context activity) {
    boolean shouldPlayBeep = Key.getBeep(context);
    if (shouldPlayBeep) {
      // See if sound settings overrides this
      AudioManager audioService = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
      if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
        shouldPlayBeep = false;
      }
    }
    return shouldPlayBeep;
  }

  private MediaPlayer buildMediaPlayer(Context activity) {
    MediaPlayer mediaPlayer = new MediaPlayer();
    try (AssetFileDescriptor file = activity.getResources().openRawResourceFd(R.raw.beep)) {
      mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
      mediaPlayer.setOnErrorListener(this);
      mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
      mediaPlayer.setLooping(false);
      mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
      mediaPlayer.prepare();
      return mediaPlayer;
    } catch (IOException ioe) {
      Log.w(TAG, ioe);
      mediaPlayer.release();
      return null;
    }
  }

  @Override
  public synchronized boolean onError(MediaPlayer mp, int what, int extra) {
    if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
      // we are finished, so put up an appropriate error toast if required and finish
      activity.finish();
    } else {
      // possibly media player error, so release and recreate
      close();
      updatePrefs();
    }
    return true;
  }

  @Override
  public synchronized void close() {
    if (mediaPlayer != null) {
      mediaPlayer.release();
      mediaPlayer = null;
    }
  }

  public void getLocutor(String code, boolean isMy, boolean historical){
    if(Key.getLocutorResultView(context) || isMy && !historical){
      if (!code.trim().equals("")) {
        if (locutor.isSpeaking()) parar();
        else
          falar(code);
      }
    }
  }

}
