public class MainActivity extends AppCompatActivity {
    // ... existing code ...
    
    private void checkOverlayPermission() {
        if (!PermissionManager.hasOverlayPermission(this)) {
            PermissionManager.requestOverlayPermission(this, 1001);
        }
    }
    
    private void createNewFloatingWindow() {
        if (PermissionManager.hasOverlayPermission(this)) {
            FloatingWindow newWindow = new FloatingWindow(this, "https://example.com");
            floatingWindows.add(newWindow);
        } else {
            Toast.makeText(this, "Overlay permission required!", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showAllWindows() {
        RecyclerView windowsList = findViewById(R.id.windows_list);
        windowsList.setVisibility(windowsList.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (PermissionManager.hasOverlayPermission(this)) {
                createNewFloatingWindow();
            }
        }
    }
}