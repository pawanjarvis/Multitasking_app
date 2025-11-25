package com.example.multiwindowapp;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import java.util.List;
import java.util.UUID;
import com.example.multiwindowapp.R;

public class FloatingWindow {
    private Context context;
    private WindowManager windowManager;
    private View floatingView;
    private WindowManager.LayoutParams params;
    private String windowId;
    private boolean isMinimized = false;
    private List<FloatingWindow> floatingWindows;
    
    public FloatingWindow(Context context, String contentUrl, List<FloatingWindow> windowsList) {
        this.context = context;
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.windowId = UUID.randomUUID().toString();
        this.floatingWindows = windowsList;
        
        initView(contentUrl);
    }
    
    private void initView(String contentUrl) {
        LayoutInflater inflater = LayoutInflater.from(context);
        floatingView = inflater.inflate(R.layout.floating_window_layout, null);
        
        // विंडो पैरामीटर्स
        params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ?
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        );
        
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;
        
        setupWindowControls();
        loadContent(contentUrl);
        
        windowManager.addView(floatingView, params);
    }
    
    private void setupWindowControls() {
        ImageButton btnClose = floatingView.findViewById(R.id.btn_close);
        ImageButton btnMinimize = floatingView.findViewById(R.id.btn_minimize);
        LinearLayout header = floatingView.findViewById(R.id.header);
        
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        
        btnMinimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMinimize();
            }
        });
        
        // ड्रैग फंक्शनलिटी
        header.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingView, params);
                        return true;
                }
                return false;
            }
        });
    }
    
    private void loadContent(String contentUrl) {
        WebView webView = floatingView.findViewById(R.id.content_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://www.google.com"); // Default URL
    }
    
    public void toggleMinimize() {
        isMinimized = !isMinimized;
        View content = floatingView.findViewById(R.id.content_view);
        
        if (isMinimized) {
            content.setVisibility(View.GONE);
            params.width = 300;
            params.height = 100;
        } else {
            content.setVisibility(View.VISIBLE);
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        
        windowManager.updateViewLayout(floatingView, params);
    }
    
    public void close() {
        if (floatingView != null && windowManager != null) {
            windowManager.removeView(floatingView);
        }
        if (floatingWindows != null) {
            floatingWindows.remove(this);
        }
    }
}