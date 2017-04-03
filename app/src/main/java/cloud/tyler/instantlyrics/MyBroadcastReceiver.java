package cloud.tyler.instantlyrics;

/**
 * Created by tylerwoodfin on 4/2/17.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String song = intent.getStringExtra("artist") + " - " + intent.getStringExtra("track");
//        String artist = intent.getStringExtra("artist");
//        String album = intent.getStringExtra("album");
//        String track = intent.getStringExtra("track");

        Toast.makeText(context, song,
                Toast.LENGTH_LONG).show();

//        Intent i = new Intent("song");
//        intent.putExtra("song", song);
//        LocalBroadcastManager.getInstance(context).sendBroadcast(i);

        try {
            ScrollingActivity.getInstace().getLyrics(song);
        } catch (Exception e) {
            Toast.makeText(context, e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

}