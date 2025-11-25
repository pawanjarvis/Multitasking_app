package com.example.multiwindowapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<FloatingWindow> floatingWindows;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        floatingWindows = new ArrayList<>();
        
        setupUI();
        checkOverlayPermission();
    }
    
    private void setupUI() {
        Button btnNewWindow = findViewById(R.id.btn_new_window);
        Button btnShowAll = findViewById(R.id.btn_show_all);
        
        btnNewWindow.setOnClickListener(v -> createNewFloatingWindow());
        btnShowAll.setOnClickListener(v -> showAllWindows());
    }
    
    private void checkOverlayPermission() {
        if (!PermissionManager.hasOverlayPermission(this)) {
            PermissionManager.requestOverlayPermission(this, 1001);
        }
    }
    
    private void createNewFloatingWindow() {
        if (PermissionManager.hasOverlayPermission(this)) {
            if (floatingWindows.size() < 10) {
                FloatingWindow newWindow = new FloatingWindow(this, "https://example.com", floatingWindows);
                floatingWindows.add(newWindow);
                Toast.makeText(this, "नई विंडो खुल गई", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "अधिकतम 10 विंडो खोल सकते हैं", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "फ्लोटिंग विंडो के लिए परमिशन आवश्यक है", Toast.LENGTH_LONG).show();
            checkOverlayPermission();
        }
    }
    
    private void showAllWindows() {
        RecyclerView windowsList = findViewById(R.id.windows_list);
        if (windowsList.getVisibility() == View.VISIBLE) {
            windowsList.setVisibility(View.GONE);
        } else {
            windowsList.setVisibility(View.VISIBLE);
            // TODO: Windows list adapter setup
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (PermissionManager.hasOverlayPermission(this)) {
                createNewFloatingWindow();
            } else {
                Toast.makeText(this, "परमिशन denied! ऐप काम नहीं करेगा", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // सभी विंडोज बंद करें
        if (floatingWindows != null) {
            for (FloatingWindow window : floatingWindows) {
                window.close();
            }
            floatingWindows.clear();
        }
    }
}