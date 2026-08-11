package com.blaupunkt.tsviewer;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import com.blaupunkt.tsviewer.view.ViewController;
import com.blaupunkt.tsviewer.view.ViewMode;

import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

public class MainActivity extends Activity {

    private static final int PICK_VIDEO = 1001;

    private ExoPlayer player;
    private PlayerView playerView;
    private TextView status;
    private ViewController viewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewController = new ViewController();
        buildInterface();
    }

    private void buildInterface() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 20, 20, 20);

        TextView title = new TextView(this);
        title.setText("Blaupunkt TS Viewer");
        title.setTextSize(24);

        status = new TextView(this);
        status.setText("No recording selected.");
        status.setTextSize(16);

        Button selectButton = new Button(this);
        selectButton.setText("Select TS Recording");

        Button fullscreenButton = new Button(this);
        fullscreenButton.setText("Fullscreen");

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

                        if (mode == ViewMode.DEWARP && !viewController.isDewarpAvailable()) {
                            status.setText(
                                    "Dewarp calibration will be enabled after TS analysis."
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

        playerView = new PlayerView(this);
        playerView.setUseController(true);

        LinearLayout.LayoutParams videoParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1
                );

        root.addView(title);
        root.addView(status);
        root.addView(selectButton);
        root.addView(fullscreenButton);
        root.addView(viewModeSpinner);
        root.addView(playerView, videoParams);

        setContentView(root);

        selectButton.setOnClickListener(v -> selectVideo());

        fullscreenButton.setOnClickListener(v -> toggleFullscreen());
    }

    private void selectVideo() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        startActivityForResult(intent, PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

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

            playRecording(uri);
        }
    }

    private void playRecording(Uri uri) {

        releasePlayer();

        player = new ExoPlayer.Builder(this).build();

        playerView.setPlayer(player);

        player.addListener(new Player.Listener() {

            @Override
            public void onPlaybackStateChanged(int state) {

                if (state == Player.STATE_BUFFERING) {
                    status.setText("Buffering recording...");
                } else if (state == Player.STATE_READY) {
                    status.setText("Recording ready.");
                } else if (state == Player.STATE_ENDED) {
                    status.setText("Playback finished.");
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                status.setText(
                        "Playback error: " + error.errorCode
                );
            }
        });

        MediaItem mediaItem = MediaItem.fromUri(uri);

        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    private void toggleFullscreen() {

        if (getWindow().getDecorView().getSystemUiVisibility() == 0) {

            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );

        } else {

            getWindow().getDecorView().setSystemUiVisibility(0);
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
