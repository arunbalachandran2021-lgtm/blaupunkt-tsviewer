package com.blaupunkt.tsviewer.settings;

public class ViewerSettings {

    private boolean autoPlay = true;
    private boolean muteAudio = false;
    private boolean fullscreen = false;

    public boolean isAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(boolean value) {
        autoPlay = value;
    }

    public boolean isMuteAudio() {
        return muteAudio;
    }

    public void setMuteAudio(boolean value) {
        muteAudio = value;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean value) {
        fullscreen = value;
    }
}
