package com.example.tornado.mymusicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import com.example.tornado.mymusicplayer.MusicService.MusicBinder;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;

import java.util.Random;
import android.app.Notification;
import android.app.PendingIntent;

/**
 * Created by tornado on 10/10/2018.
 */

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{

    private boolean shuffle=false;
    private Random rand;

    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;

    private final IBinder musicBind = new MusicBinder();

    private String songTitle="";
    private static final int NOTIFY_ID=1;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }



    public void onCreate(){
        //create the service
        super.onCreate();
//initialize position
        songPosn=0;
//create player
        player = new MediaPlayer();

        initMusicPlayer();

        rand=new Random();
    }

//    initialize the MediaPlayer class
    public void initMusicPlayer(){
        //set player properties
//The wake lock will let playback continue when the device becomes idle and we set the stream type to music
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }




//    add a method to the Service class to pass the list of songs from the Activity
    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }


    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }


    public void playSong(){
        //play a song
        player.reset();

        //get song
        Song playSong = songs.get(songPosn);

        songTitle=playSong.getTitle();


//get id
        long currSong = playSong.getId();
//set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);


        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();
    }





    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }


    public void playPrev(){
//        songPosn--;
//        if(songPosn&lt=0)
//            songPosn=songs.size()-1;
//        playSong();
        songPosn--;
        if(songPosn<=0)
            songPosn=songs.size()-1;
        playSong();
    }


    //skip to next
    public void playNext(){
//        songPosn++;
//        if(songPosn&gt=songs.size())
//            songPosn=0;
//        playSong();
        if(shuffle){
            int newSong = songPosn;
            while(newSong==songPosn){
                newSong=rand.nextInt(songs.size());
            }
            songPosn=newSong;
        }
        else{
            songPosn++;
            if(songPosn==songs.size()) songPosn=0;
        }
        playSong();

    }





    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }











    public void setSong(int songIndex){
        songPosn=songIndex;
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition()==0){
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
//                .setSmallIcon(R.drawable.play)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
               Notification not = builder.build();

               startForeground(NOTIFY_ID, not);

    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

}
