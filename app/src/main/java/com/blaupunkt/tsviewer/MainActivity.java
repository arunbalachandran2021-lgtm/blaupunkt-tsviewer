package com.blaupunkt.tsviewer;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.blaupunkt.tsviewer.view.ViewController;
import com.blaupunkt.tsviewer.view.ViewMode;

public class MainActivity extends Activity {

    private static final int PICK_VIDEO = 1001;

    private ExoPlayer player;
    private PlayerView playerView;
    private TextView status;
    private TextView fileInfo;

    private ViewController viewController;

    private float lastTouchX;
    private float lastTouchY;
    private boolean dragging = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewController = new ViewController();

        buildInterface();
    }

    private void buildInterface() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Blaupunkt TS Viewer");
        title.setTextSize(24);

        fileInfo = new TextView(this);
        fileInfo.setText("No recording selected.");
        fileInfo.setTextSize(15);

        status = new TextView(this);
        status.setText("Ready.");
        status.setTextSize(15);

        Button selectButton = new Button(this);
        selectButton.setText("Select TS Recording");

        Spinner viewModeSpinner = new Spinner(this);

        String[] modes = {
                "Original",
                "Dewarp",
                "Panorama",
                "360 View"
        };

        ArrayAdapter<String> modeAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        modes
                );

        modeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        viewModeSpinner.setAdapter(modeAdapter);

        Button zoomInButton = new Button(this);
        zoomInButton.setText("Zoom +");

        Button zoomOutButton = new Button(this);
        zoomOutButton.setText("Zoom -");

        Button resetButton = new Button(this);
        resetButton.setText("Reset View");

        Button fullscreenButton = new Button(this);
        fullscreenButton.setText("Fullscreen");

        playerView = new PlayerView(this);
        playerView.setUseController(true);

        LinearLayout.LayoutParams videoParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1
                );

        root.addView(title);
        root.addView(fileInfo);
        root.addView(status);
        root.addView(selectButton);
        root.addView(viewModeSpinner);
        root.addView(zoomInButton);
        root.addView(zoomOutButton);
        root.addView(resetButton);
        root.addView(fullscreenButton);
        root.addView(playerView, videoParams);

        setContentView(root);

        selectButton.setOnClickListener(v -> selectVideo());

        zoomInButton.setOnClickListener(v -> {
            viewController.setZoom(
                    viewController.getZoom() + 0.5f
            );

            updateViewStatus();
        });

        zoomOutButton.setOnClickListener(v -> {
            viewController.setZoom(
                    viewController.getZoom() - 0.5f
            );

            updateViewStatus();
        });

        resetButton.setOnClickListener(v -> {
            viewController.resetView();
            updateViewStatus();
        });

        fullscreenButton.setOnClickListener(
                v -> toggleFullscreen()
        );

        viewModeSpinner.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        ViewMode mode;

                        switch (position) {
                            case 1:
                                mode = ViewMode.DEWARP;
                                break;

                            case 2:
                                mode = ViewMode.PANORAMA;
                                break;

                            case 3:
                                mode = ViewMode.VIEW_360;
                                break;

                            default:
                                mode = ViewMode.ORIGINAL;
                                break;
                        }

                        viewController.setMode(mode);

                        if (mode == ViewMode.DEWARP &&
                                !viewController.isDewarpAvailable()) {

                            status.setText(
                                    "Dewarp calibration pending TS analysis."
                            );

                        } else {

                            status.setText(
                                    "View mode: " + mode.name()
                            );
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent) {
                    }
                }
        );

        playerView.setOnTouchListener(
                (v, event) -> handleTouch(event)
        );
    }

    private boolean handleTouch(MotionEvent event) {

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:

                lastTouchX = event.getX();
                lastTouchY = event.getY();
                dragging = true;

                return true;

            case MotionEvent.ACTION_MOVE:

                if (dragging) {

                    float dx = event.getX() - lastTouchX;
                    float dy = event.getY() - lastTouchY;

                    viewController.rotate(dx, dy);

                    lastTouchX = event.getX();
                    lastTouchY = event.getY();

                    updateViewStatus();
                }

                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                dragging = false;

                return true;

            default:
                return true;
        }
    }

    private void selectVideo() {

        Intent intent = new Intent(
                Intent.ACTION_OPEN_DOCUMENT
        );

        intent.addCategory(
                Intent.CATEGORY_OPENABLE
        );

        intent.setType("*/*");

        startActivityForResult(
                intent,
                PICK_VIDEO
        );
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode == PICK_VIDEO &&
                resultCode == RESULT_OK &&
                data != null &&
                data.getData() != null) {

            Uri uri = data.getData();

            try {

                getContentResolver().takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );

            } catch (Exception ignored) {
            }

            String name = uri.getLastPathSegment();

            fileInfo.setText(
                    "Recording: " + name
            );

            playRecording(uri);
        }
    }

    private void playRecording(Uri uri) {

        releasePlayer();

        player = new ExoPlayer.Builder(this)
                .build();

        playerView.setPlayer(player);

        player.addListener(
                new Player.Listener() {

                    @Override
                    public void onPlaybackStateChanged(
                            int state) {

                        if (state ==
                                Player.STATE_BUFFERING) {

                            status.setText(
                                    "Buffering recording..."
                            );

                        } else if (state ==
                                Player.STATE_READY) {

                            status.setText(
                                    "Recording ready."
                            );

                        } else if (state ==
                                Player.STATE_ENDED) {

                            status.setText(
                                    "Playback finished."
                            );
                        }
                    }

                    @Override
                    public void onPlayerError(
                            PlaybackException error) {

                        status.setText(
                                "Playback error: "
                                        + error.errorCode
                        );
                    }
                }
        );

        MediaItem mediaItem =
                MediaItem.fromUri(uri);

        player.setMediaItem(mediaItem);

        player.prepare();

        player.play();
    }

    private void updateViewStatus() {

        status.setText(
                "Mode: "
                        + viewController
                        .getCurrentMode()
                        .name()
                        + " | Zoom: "
                        + String.format(
                                "%.1fx",
                                viewController.getZoom()
                        )
        );
    }

    private void toggleFullscreen() {

        int flags =
                getWindow()
                        .getDecorView()
                        .getSystemUiVisibility();

        if (flags == 0) {

            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    );

        } else {

            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(0);
        }
    }

    private void releasePlayer() {

        if (player != null) {

            player.release();
            player = null;
        }

        if (playerView != null) {

            playerView.setPlayer(null);
        }
    }

    @Override
    protected void onStop() {

        super.onStop();

        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onDestroy() {

        releasePlayer();

        super.onDestroy();
    }
}
