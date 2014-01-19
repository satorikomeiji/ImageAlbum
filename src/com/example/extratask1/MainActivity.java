package com.example.extratask1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    public static final String ya_url = "http://api-fotki.yandex.ru/api/top/?limit=20";
    public static final String flickr_url = "http://www.flickr.com/services/api/";
    private GridView gridview;
    private final Context m_context= this;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        try {
            createResourceFiles();
        }
        catch (Exception e) {
            Log.e("IOError", "Error in creating resources");
            e.printStackTrace();
        }

        gridview = (GridView) findViewById(R.id.gridView);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.update_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void createResourceFiles() throws Exception {
        for (int i = 0; i < ImageAdapter.IMAGE_COUNT; i++) {
            String filePath = "mini_" + i;
            File file = new File(getFilesDir(), filePath);
            if(!file.exists()) {
                 //outStream;
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_no_data);
                FileOutputStream outStream = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();


            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.download:
                downloadImages();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void downloadImages() {
        new DownloadImages().execute(URLEncoder.encode(ya_url));
    }

    private class DownloadImages extends AsyncTask<String, Void, String> {
        private String connectionError;
        private String xmlError;
        private String status;

        @Override
        protected String doInBackground(String... params) {

            String url = params[0];
            final ArrayList<String> img_refs = new ArrayList<>();
//            index = 0;
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();

                DefaultHandler handler = new DefaultHandler() {

                    boolean item = false;
                    boolean img = false;
                    boolean description = false;

                    StringBuffer buffer;
                    public void startElement(String uri, String localName,
                                             String qName, Attributes attributes)
                            throws SAXException {
                        buffer = new StringBuffer();
                        //System.out.println("Start Element :" + qName);
                        System.out.println("StartTag: " + qName);
                        if (qName.equals("entry")) {
                            item = true;
                        }

                        if (qName.equals("f:img")) {
                            img = true;
                            if (attributes.getValue("size").equals("orig")) {
                                img_refs.add(attributes.getValue("href"));
                            };

                        }

                        if (qName.equals("description")) {
                            description = true;
                        }


                    }

                    public void endElement(String uri, String localName,
                                           String qName)
                            throws SAXException {
                        String all = buffer.toString();
                        //if (item && description && qName.equals("description")) {
//                            descriptions.get(index).add(all);
// item = false;
                            // description = false;
//                        }
                        System.out.println("EndTag: " + qName);
                        //System.out.println("End Element :" + qName);
                        if (qName.equals("entry")) {
                            item = false;
                        }

                        if (qName.equals("f:img")) {
                            img = false;
                        }

                        if (qName.equals("description")) {
                            description = false;
                        }

                    }

                    public void characters(char ch[], int start, int length)
                            throws SAXException {

                        //System.out.println(new String(ch, start, length));


                        //nif (item && title) {
                        //    System.out.println("First Name : "
//                                    + new String(ch, start, length));
                            //bfname = false;
//                            articles.get(index).add(new String(ch, start, length));
//                        }
  //                      else {
//
///
//n                            if(buffer != null) buffer.append(new String(ch, start, length));
//                        }

                        //System.out.println("Last Name : "
// + new String(ch, start, length));
                        //blname = false;
                        //d

                    }
                };
                for (String myurl : params) {
                    //String coding = index == 5 ? "CP1251" : "UTF-8";
                    String coding = "UTF-8";
                    System.out.println("Dowloading URL:" + myurl);
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(myurl);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    InputStream is = httpEntity.getContent();
                    Reader reader = new InputStreamReader(is,coding);
                    InputSource isource = new InputSource(reader);
                    isource.setEncoding(coding);
                    saxParser.parse(isource, handler);

                    is.close();
                    //index++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            int index = 0;
            try {
                for (String ref : img_refs) {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(ref);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    InputStream is = httpEntity.getContent();
                    String filePath = "mini_" + index;
                    File file = new File(getFilesDir(), filePath);
                    FileOutputStream outStream = new FileOutputStream(filePath);
                    Bitmap my_bmp = BitmapFactory.decodeStream(is);
                    my_bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    index++;
                    is.close();

                }
            } catch( Exception e) {
                    e.printStackTrace();
            }
            return "OK";
        }


        @Override
        protected void onPostExecute(String result) {

            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
            if ("OK".equals(result)) {
                gridview.setAdapter(new ImageAdapter(m_context));
            }
        }
        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }


}
