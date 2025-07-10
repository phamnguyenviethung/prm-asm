package com.example.myapplication.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.util.HubSpotChatManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DraggableChatButton extends FloatingActionButton implements View.OnTouchListener {

    private static final String TAG = "DraggableChatButton";
    private float dX, dY;
    private boolean isDragging = false;
    private static final float CLICK_DRAG_TOLERANCE = 10f; // Distance in pixels
    private float downRawX, downRawY;
    private float upRawX, upRawY;

    public DraggableChatButton(@NonNull Context context) {
        super(context);
        init();
    }

    public DraggableChatButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DraggableChatButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnTouchListener(this);
        setClickable(true);
        setFocusable(true);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

        int action = motionEvent.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downRawX = motionEvent.getRawX();
                downRawY = motionEvent.getRawY();
                dX = view.getX() - downRawX;
                dY = view.getY() - downRawY;
                isDragging = false;
                return true; // Consume the event

            case MotionEvent.ACTION_MOVE:
                int viewWidth = view.getWidth();
                int viewHeight = view.getHeight();

                View viewParent = (View) view.getParent();
                int parentWidth = viewParent.getWidth();
                int parentHeight = viewParent.getHeight();

                float newX = motionEvent.getRawX() + dX;
                float newY = motionEvent.getRawY() + dY;

                // Check if we've moved enough to consider this a drag
                float deltaX = Math.abs(motionEvent.getRawX() - downRawX);
                float deltaY = Math.abs(motionEvent.getRawY() - downRawY);
                if (deltaX > CLICK_DRAG_TOLERANCE || deltaY > CLICK_DRAG_TOLERANCE) {
                    isDragging = true;
                }

                // Don't let the FAB go beyond the bounds of the parent
                newX = Math.max(layoutParams.leftMargin, newX); // Don't allow the FAB past the left hand side of the parent
                newX = Math.min(parentWidth - viewWidth - layoutParams.rightMargin, newX); // Don't allow the FAB past the right hand side of the parent
                newY = Math.max(layoutParams.topMargin, newY); // Don't allow the FAB past the top of the parent
                newY = Math.min(parentHeight - viewHeight - layoutParams.bottomMargin, newY); // Don't allow the FAB past the bottom of the parent

                view.animate()
                        .x(newX)
                        .y(newY)
                        .setDuration(0)
                        .start();
                return true; // Consume the event

            case MotionEvent.ACTION_UP:
                upRawX = motionEvent.getRawX();
                upRawY = motionEvent.getRawY();

                float upDX = upRawX - downRawX;
                float upDY = upRawY - downRawY;

                // If it was a small movement, treat it as a click
                if (Math.abs(upDX) < CLICK_DRAG_TOLERANCE && Math.abs(upDY) < CLICK_DRAG_TOLERANCE && !isDragging) {
                    // This was a click, not a drag
                    performClick();
                    return true;
                } else {
                    // This was a drag - snap to edge
                    snapToEdge(view);
                    return true; // Consume the event
                }

            default:
                return super.onTouchEvent(motionEvent);
        }
    }

    private void snapToEdge(View view) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        View viewParent = (View) view.getParent();
        int parentWidth = viewParent.getWidth();
        int viewWidth = view.getWidth();

        float currentX = view.getX();
        float centerX = parentWidth / 2f;

        // Snap to the closest edge (left or right)
        float targetX;
        if (currentX < centerX) {
            // Snap to left edge
            targetX = layoutParams.leftMargin;
        } else {
            // Snap to right edge
            targetX = parentWidth - viewWidth - layoutParams.rightMargin;
        }

        view.animate()
                .x(targetX)
                .setDuration(300)
                .start();
    }

    @Override
    public boolean performClick() {
        super.performClick();
        // Handle the chat button click
        openHubSpotChat();
        return true;
    }

    private void openHubSpotChat() {
        try {
            HubSpotChatManager.getInstance().openChat(getContext());
            Log.d(TAG, "HubSpot chat opened successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to open HubSpot chat", e);
            // Fallback to showing a toast if HubSpot chat fails
            Toast.makeText(getContext(), "Chat support temporarily unavailable", Toast.LENGTH_SHORT).show();
        }
    }
}
