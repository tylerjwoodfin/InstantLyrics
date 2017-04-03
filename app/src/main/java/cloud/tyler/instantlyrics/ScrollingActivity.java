package cloud.tyler.instantlyrics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ScrollingActivity extends AppCompatActivity
{

    //Class Variables
    String lyricsString = "No Lyrics Found";
    boolean urlFound = false;
    private static ScrollingActivity ins;
    String currentSong = "Baby Got Back - Sir Mix-A-Lot";

    public static ScrollingActivity getInstace()
    {
        return ins;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ins = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    // Handling the received Intents for the "song" event
    private BroadcastReceiver songReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            toast("Received Song: " + intent.getStringExtra("song") + intent.getDataString());
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // This registers mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(songReceiver,
                        new IntentFilter("song"));
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(songReceiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Displays a toast on Android, as well as a System out
     * @param s, the string required
     */
    public void toast(String s)
    {
        System.out.println(s);
        Toast toast = Toast.makeText(ScrollingActivity.this, s, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Gets the lyrics for the provided song.
     */
    public void getLyrics(String s)
    {
        String oldSong = currentSong;
        currentSong = s;

        //Intents are received multiple times- don't try to refresh lyrics unnecessarily.
        if(oldSong.matches(currentSong))
        {
            return;
        }

        try
        {
            (new ParseURL() ).execute(new String[]{"http://search.azlyrics.com/search.php?q=" + s});
            TextView lyrics = (TextView)findViewById(R.id.lyrics);
            lyrics.setText(lyricsString);
        } catch (Exception e)
        {
            String error = Log.getStackTraceString(e);
            toast(error);
        }
    }

    private class ParseURL extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder buffer = new StringBuilder();
            try {
                Log.d("JSwa", "Connecting to ["+strings[0]+"]");
                Document doc  = Jsoup.connect(strings[0]).get();
                Log.d("JSwa", "Connected to ["+strings[0]+"]");
                // Get document (HTML page) title
                String title = doc.title();
                String body = doc.html();
                Log.d("JSwA", "Title ["+title+"]");
                buffer.append("Title: " + title + "\r\n");
                buffer.append("Body: " + body + "\r\n");

                // Get meta info
                Elements metaElems = doc.select("meta");
                buffer.append("META DATA\r\n");
                for (Element metaElem : metaElems) {
                    String name = metaElem.attr("name");
                    String content = metaElem.attr("content");
                    buffer.append("name ["+name+"] - content ["+content+"] \r\n");
                }

                Elements topicList = doc.select("h2.topic");
                buffer.append("Topic list\r\n");
                for (Element topic : topicList) {
                    String data = topic.text();

                    buffer.append("Data ["+data+"] \r\n");
                }
            }
            catch(Throwable t) {
                t.printStackTrace();
            }


            lyricsString = buffer.toString();

            return buffer.toString();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //Parse HTML

            if(!urlFound)
            {

                if(s.contains("1. <a href="))
                {

                    s = s.split("1. <a href=\"")[1];
                    s = s.split("\"")[0];
                    //s is url
                    urlFound = true;

                    (new ParseURL() ).execute(new String[]{s}); //Get lyrics from URL
                }
                else
                {
                    //Display Error
                    TextView lyrics = (TextView)findViewById(R.id.lyrics);
                    lyrics.setText("Lyrics are unavailable for this song.");
                }
            }
            else
            {
                s = s.split("that. -->")[1];
                s = s.split("<!-- Mx")[0];

                s = Html.fromHtml(s).toString();

                //Display Lyrics
                TextView lyrics = (TextView)findViewById(R.id.lyrics);
                lyrics.setText(s);
            }
        }
    }

}