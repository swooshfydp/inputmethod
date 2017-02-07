package com.sighs.imputmethod.customviews;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sighs.imputmethod.models.Coordinates;

/**
 * Created by stuart on 1/16/17.
 */

/*
    Drag 'n' Dropable Images that are used in the Pager and Grid View

    EXAMPLE USAGE:
    Call this new class to set up an image on the screen
    private class CashImage implements OnDropEventListener {
        private final DragNDropImageView dndImage;
        private final RelativeLayout layout;
        public CashImage(Context context, RelativeLayout layout) {
            dndImage = new DragNDropImageView(context, R.mipmap.android_heads,
                    R.mipmap.ic_launcher, layout);
            this.layout = layout;
            dndImage.setImageResource(R.mipmap.android_heads);
            dndImage.setDraggable();
            dndImage.setMaxHeight(2);
            dndImage.setMaxWidth(2);
            dndImage.setOnDropEventListener(this);
            layout.addView(dndImage);
        }

        @Override
        public void onItemDrop(Rect rect) {
            TextView text = (TextView) layout.findViewById(R.id.txtTesting);
            final Rect tRect = new Rect();
            text.getGlobalVisibleRect(tRect);
            if(tRect.contains(rect)) {
                Toast.makeText(getApplicationContext(), "Dragged", Toast.LENGTH_SHORT);
            }
        }
    }
*/
public class DragNDropImageView extends ImageView implements View.OnTouchListener {
    private final int draggableResource;
    private final int sittingResource;
    private float xDelta;
    private float yDelta;
    private float xOrigin;
    private float yOrigin;
    private int lastAction = MotionEvent.ACTION_UP;
    private final ViewGroup layout;
    private ImageView placeHolder;
    private OnDropEventListener listener;
    private int width = 100;
    private int height = 100;

    // Constructor
    public DragNDropImageView(Context context, int dragResource, int sitResource
            ,ViewGroup layout) {
        super(context);
        this.listener = null;
        this.draggableResource = dragResource;
        this.sittingResource = sitResource;
        this.setImageBitmap(decodeSampledBitmapFromResource(getResources(), sitResource, this.width, this.height));
        this.layout = layout;
    }

    // Make the Image Draggable
    public void setDraggable() {
        xOrigin = this.getX();
        yOrigin = this.getY();
        this.setOnTouchListener(this);
    }

    // Change the Origin of the image, this sets where then image returns to
    public void setOrigin(float x, float y) {
        this.xOrigin = x;
        this.yOrigin = y;
    }

    public void setDimensions(int w, int h) {
        this.width = w;
        this.height = h;
        this.setImageBitmap(decodeSampledBitmapFromResource(getResources(), this.sittingResource,
                this.width, this.height));
    }

    // Set the Drop Listener so we can have custom events on eah location
    public void setOnDropEventListener(OnDropEventListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case (MotionEvent.ACTION_DOWN):
                xDelta = view.getX() - event.getRawX();
                yDelta = view.getY() - event.getRawY();
                // Create a placeholder when you first touch the image
                if(lastAction == MotionEvent.ACTION_UP) {
                    createPlaceHolder();
                }
                lastAction = MotionEvent.ACTION_DOWN;
                return true;
            case (MotionEvent.ACTION_MOVE):
                view.setY(event.getRawY() + yDelta);
                view.setX(event.getRawX() + xDelta);
                lastAction = MotionEvent.ACTION_MOVE;
                return true;
            case (MotionEvent.ACTION_UP):
                // When you let go of the image trigger the event and send the
                // image location
                Coordinates c = new Coordinates((int)(event.getX()),
                        (int) (event.getY()), this.getWidth(), this.getHeight());
                if(this.listener != null) this.listener.onItemDrop(view, c);
                // Reset the image location
                view.setX(xOrigin);
                view.setY(yOrigin);
                // Remove the placeholder
                removePlaceHolder();
                lastAction = MotionEvent.ACTION_UP;
                return false;
        }
        return false;
    }

    private Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                          int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private void createPlaceHolder() {
        // Change the image to the one that is draggable
        this.setImageBitmap(decodeSampledBitmapFromResource(getResources(),
                this.draggableResource, this.width, this.height));
        // Create a new image for the placeholder
        // The placeholder image should be the sitting image
        placeHolder = new ImageView(this.getContext());
        placeHolder.setImageBitmap(decodeSampledBitmapFromResource(getResources(),
                this.sittingResource, this.width, this.height));
        placeHolder.setX(xOrigin);
        placeHolder.setY(yOrigin);
        layout.addView(placeHolder);
    }

    private void removePlaceHolder() {
        this.setImageBitmap(decodeSampledBitmapFromResource(getResources(),
                this.sittingResource, this.width, this.height));
        layout.removeView(placeHolder);
    }
}