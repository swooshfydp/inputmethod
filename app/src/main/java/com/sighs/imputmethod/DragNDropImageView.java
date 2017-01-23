package com.sighs.imputmethod;

import android.content.Context;
import android.graphics.Rect;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
    private final RelativeLayout layout;
    private ImageView placeHolder;
    private OnDropEventListener listener;

    // Constructor
    public DragNDropImageView(Context context, int dragResource, int sitResource
            ,RelativeLayout layout) {
        super(context);
        this.listener = null;
        this.draggableResource = dragResource;
        this.sittingResource = sitResource;
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
                Rect rect = new Rect();
                this.getGlobalVisibleRect(rect);
                this.listener.onItemDrop(rect);
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

    private void createPlaceHolder() {
        // Change the image to the one that is draggable
        this.setImageResource(this.draggableResource);
        // Create a new image for the placeholder
        // The placeholder image should be the sitting image
        placeHolder = new ImageView(this.getContext());
        placeHolder.setImageResource(this.sittingResource);
        placeHolder.setX(xOrigin);
        placeHolder.setY(yOrigin);
        layout.addView(placeHolder);
    }

    private void removePlaceHolder() {
        this.setImageResource(this.sittingResource);
        layout.removeView(placeHolder);
    }
}