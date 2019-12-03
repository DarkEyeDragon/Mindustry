package io.anuke.mindustry.ui.dialogs;

public class PlayedWithDialog extends FloatingDialog {

    public PlayedWithDialog() {
        super("$stats.playedwith");

        addCloseButton();

        shown(this::setup);
    }

    private void setup(){
        
    }
}
