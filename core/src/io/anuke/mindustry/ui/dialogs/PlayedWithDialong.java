package io.anuke.mindustry.ui.dialogs;

import io.anuke.arc.scene.ui.ScrollPane;
import io.anuke.arc.scene.ui.layout.Table;
import io.anuke.mindustry.gen.Icon;
import io.anuke.mindustry.gen.Tex;
import io.anuke.mindustry.net.Administration;

import static io.anuke.mindustry.Vars.*;

public class PlayedWithDialong extends FloatingDialog {

    public PlayedWithDialong() {
        super("$stats.playedwith");

        addCloseButton();
    }

    private void setup(){
        cont.clear();

        float w = 400f, h = 80f;

        Table table = new Table();

        ScrollPane pane = new ScrollPane(table);
        pane.setFadeScrollBars(false);

        if(netServer.admins.getBanned().size == 0){
            table.add("$server.bans.none");
        }

        for(Administration.PlayerInfo info : netServer.admins.getBanned()){
            Table res = new Table(Tex.button);
            res.margin(14f);

            res.labelWrap("IP: [LIGHT_GRAY]" + info.lastIP + "\n[]Name: [LIGHT_GRAY]" + info.lastName).width(w - h - 24f);
            res.add().growX();
            res.addImageButton(Icon.cancel, () -> {
                ui.showConfirm("$confirm", "$confirmunban", () -> {
                    netServer.admins.unbanPlayerID(info.id);
                    setup();
                });
            }).size(h).pad(-14f);

            table.add(res).width(w).height(h);
            table.row();
        }

        cont.add(pane);
    }
}
