package com.citypark.utility;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

import com.citypark.R;

public class TimeLimitAlert extends BroadcastReceiver {
	
   @Override
    public void onReceive(Context context, Intent intent)
    {
	    Toast.makeText(context, R.string.time_limit_reached, Toast.LENGTH_SHORT).show();
	    
	    //send off alarm sound
	    Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
	     if(alert == null){
	         // alert is null, using backup
	         alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	         if(alert == null){  // I can't see this ever being null (as always have a default notification) but just in case
	             // alert backup is null, using 2nd backup
	             alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);               
	         }
	     }
	     
	     MediaPlayer mMediaPlayer = new MediaPlayer();
	     try {
			mMediaPlayer.setDataSource(context, alert);

		     final AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		     if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
		    	 mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
		    	 mMediaPlayer.prepare();
		    	 mMediaPlayer.start();
		      }
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
}
 