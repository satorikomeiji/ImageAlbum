package com.example.extratask1;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by satori on 1/18/14.
 */
public class ImageAdapter extends BaseAdapter {
    public static final int IMAGE_COUNT = 64;
    private Context mContext;
    private Bitmap[] bitmaps;


    public ImageAdapter(Context c) {
        mContext = c;
        bitmaps = new Bitmap[IMAGE_COUNT];
        fillBitmaps(bitmaps);

    }

    public int getCount() {
        return IMAGE_COUNT;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        int orientation = mContext.getResources().getConfiguration().orientation;

        //if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            int img_size;
            int padding_size;
            if (orientation == Configuration.ORIENTATION_PORTRAIT)  {
                int parentWidth = parent.getWidth();

                img_size = (35 * parentWidth) / 100;
                padding_size = (parentWidth - 2 * img_size) / 3;

                imageView.setLayoutParams(new GridView.LayoutParams(img_size + (3 * padding_size) / 2, img_size + (2 * padding_size)));
                if (position % 2 == 0) {
                    imageView.setPadding(padding_size, padding_size, padding_size / 2, padding_size);
                }
                else {
                    imageView.setPadding(padding_size / 2, padding_size, padding_size, padding_size);
                }


            }
            else /* (orientation == Configuration.ORIENTATION_LANDSCAPE)*/ {
                int parentWidth = width;

                img_size = (20 * parentWidth) / 100;
                padding_size = (parentWidth - 4 * img_size) / 5;
                switch (position % 4) {
                    case 0:
                    {imageView.setPadding(padding_size, padding_size, padding_size / 2, padding_size);
                        imageView.setLayoutParams(new GridView.LayoutParams(img_size + (3 * padding_size) / 2, img_size + (2 * padding_size)));
                        break;}
                    case 1:    {
                        imageView.setPadding(padding_size / 2, padding_size, padding_size / 2, padding_size);
                        imageView.setLayoutParams(new GridView.LayoutParams(img_size + padding_size, img_size + (2 * padding_size)));
                        break;  }
                    case 2:      {
                        imageView.setPadding(padding_size / 2, padding_size, padding_size / 2, padding_size);
                        imageView.setLayoutParams(new GridView.LayoutParams(img_size + padding_size, img_size + (2 * padding_size)));
                        break;    }
                    case 3:        {
                        imageView.setPadding(padding_size / 2, padding_size, padding_size, padding_size);
                        imageView.setLayoutParams(new GridView.LayoutParams(img_size + (3 * padding_size) / 2, img_size + (2 * padding_size)));
                        break;      }
                }







            }


            imageView.setScaleType(ImageView.ScaleType.FIT_XY);


      //  } else {
     //       imageView = (ImageView) convertView;
      //  }
        imageView.setImageBitmap(getMiniImageBitmap(position));
        //imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }
    private Bitmap getMiniImageBitmap(int position) {


        return bitmaps[position];

    }

    private void fillBitmaps(Bitmap[] bms) {
        for (int i = 0; i < IMAGE_COUNT; i++) {
            bms[i] = BitmapFactory.decodeFile(mContext.getFilesDir().getPath() + "/mini_" + i);
        }

    }




}