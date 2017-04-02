package cloud.tyler.instantlyrics;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    String lyricsString = "No Lyrics Found";
    String query = "Baby Got Back - Sir Mix-a-lot";
    boolean urlFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        getLyrics();
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
     * @param s
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
    public void getLyrics()
    {
        try
        {
            (new ParseURL() ).execute(new String[]{"http://search.azlyrics.com/search.php?q=" + query});
            TextView lyrics = (TextView)findViewById(R.id.lyrics);
            lyrics.setText(lyricsString);
        } catch (Exception e)
        {
            String s = Log.getStackTraceString(e);
            toast(s);
        }
    }

    private class ParseURL extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuffer buffer = new StringBuffer();
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

                s = s.split("1. <a href=\"")[1];
                s = s.split("\"")[0];
                //s is url
                urlFound = true;

                (new ParseURL() ).execute(new String[]{s}); //Get lyrics from URL
            }
            else
            {
                s = s.split("that. -->")[1];
                s = s.split("<!-- Mx")[0];

                //Display Lyrics
                TextView lyrics = (TextView)findViewById(R.id.lyrics);
                lyrics.setText(s);
            }
        }
    }
}