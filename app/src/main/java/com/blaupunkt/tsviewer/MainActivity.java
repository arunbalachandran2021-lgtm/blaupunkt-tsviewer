package com.blaupunkt.tsviewer;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

public class MainActivity extends Activity {

    private static final int PICK_VIDEO = 1001;

    private ExoPlayer player;
    private PlayerView playerView;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(this);
        title.setText("Blaupunkt 360° TS Viewer");
        title.setTextSize(24);
        title.setPadding(24, 24, 24, 16);

        status = new TextView(this);
        status.setText("Select a .TS video recording.");
        status.setTextSize(16);
        status.setPadding(24, 8, 24, 16);

        Button selectButton = new Button(this);
        selectButton.setText("Select .TS Video");

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
        root.addView(playerView, videoParams);

        setContentView(root);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVideo();
            }
        });
    }

    private void selectVideo() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // video/* lets Android's file picker show video recordings,
        // including TS files when the file provider exposes them as video.
        intent.setType("video/*");

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

            playVideo(uri);
        }
    }

    private void playVideo(Uri uri) {
        releasePlayer();

        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(uri);
        player.setMediaItem(mediaItem);

        status.setText("Loading video...");

        player.prepare();
        player.play();

        status.setText("Playing TS recording");
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }

        playerView.setPlayer(null);
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
