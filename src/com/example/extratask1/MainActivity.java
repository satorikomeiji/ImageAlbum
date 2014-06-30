package com.example.extratask1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    public static final String WOE_ID = "753692";
    public static final String searched_text = "Antoni Gaudí";
    public static final String API_KEY =  "f15410a19ee77eae8a6dff213318cb98";
    public static final String SECRET = "e667170799eccdea";
    public static final String ya_url = "http://api-fotki.yandex.ru/api/top/?limit=20";
    public static String flickr_url;
    private GridView gridview;
    private final Context m_context= this;
    public static Context put_context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            flickr_url =  "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key="
                + API_KEY + "&text="
                + URLEncoder.encode(searched_text, "UTF-8") + "&woe_id="
                + WOE_ID + "&per_page="
                    + ImageAdapter.IMAGE_COUNT + "&sort=relevance";
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported encoding",e);
        }

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
                Intent intent = new Intent(m_context, FullImageActivity.class);
                intent.putExtra("position", position);
                put_context = m_context;
                startActivity(intent);
                //Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
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
        new DownloadImages().execute(flickr_url);
    }
    private static String construct_ref(String photo_id, String photo_secret, String photo_server, String photo_farm, String option) {
        return "https://farm" + photo_farm + ".staticflickr.com/" + photo_server+ "/" + photo_id + "_" + photo_secret + "_" + option + ".jpg";
    }

    private class DownloadImages extends AsyncTask<String, Void, String> {
        private String connectionError;
        private String xmlError;
        private String status;


        @Override
        protected String doInBackground(String... params) {

            String url = params[0];
            final ArrayList<String> img_refs = new ArrayList<>();
            final ArrayList<String> orig_img_refs = new ArrayList<>();
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
                        if (qName.equals("photos")) {
                            item = true;
                        }

                        if (qName.equals("photo")) {
                            img = true;
                            if (attributes.getValue("ispublic").equals("1")) {
                                String photo_id = attributes.getValue("id");
                                String photo_secret = attributes.getValue("secret");
                                String photo_server = attributes.getValue("server");
                                String photo_farm = attributes.getValue("farm");

                                //img_refs.add(attributes.getValue("href"));
                                //orig_img_refs.add(construct_ref(photo_id,photo_secret, photo_server, photo_farm, "b"));
                                img_refs.add(construct_ref(photo_id,photo_secret, photo_server, photo_farm, "q"));

                            };
                            //if (attributes.getValue("size").equals("XL")) {
//                                orig_img_refs.add(attributes.getValue("href"));
//                            }
                        }
                        //if (qName.equals("content")) {
//                            orig_img_refs.add(attributes.getValue("src"));
//                        }

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
                        if (qName.equals("photos")) {
                            item = false;
                        }

                        if (qName.equals("photo")) {
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
                    File file = new File(m_context.getFilesDir(), filePath);
                    FileOutputStream outStream = new FileOutputStream(file);
                    //Bitmap my_bmp = BitmapFactory.decodeStream(is);
                    //my_bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);

                    }
                    index++;
                    is.close();
                    outStream.close();

                }
                index = 0;
                for (String ref : orig_img_refs) {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(ref);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    InputStream is = httpEntity.getContent();
                    String filePath = "orig_" + index;
                    File file = new File(m_context.getFilesDir(), filePath);
                    FileOutputStream outStream = new FileOutputStream(file);
               //     Bitmap my_bmp = BitmapFactory.decodeStream(is);
                 //   my_bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    byte[] buffer = new byte[1024 * 128];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                    index++;
                    is.close();
                    outStream.close();

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
                Toast.makeText(m_context, "Finished Loading", 200).show();
            }
        }
        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }


}
