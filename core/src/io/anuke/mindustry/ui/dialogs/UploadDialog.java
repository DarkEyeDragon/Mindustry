package io.anuke.mindustry.ui.dialogs;

import io.anuke.arc.Core;
import io.anuke.arc.Net;
import io.anuke.mindustry.Vars;
import io.anuke.mindustry.io.JsonIO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;


public class UploadDialog extends FloatingDialog {


    public static class JsonType {
        private int status;
        private String url;
        private int size;

        public int getStatus() {
            return status;
        }

        public String getUrl() {
            return url;
        }

        public long getSize() {
            return size;
        }
    }

    public UploadDialog() {
        super("$upload");
        shown(this::setup);
        onResize(this::setup);
        addCloseButton();
    }

    public void setup() {
        String fileName = "-1." + Vars.saveExtension;
        File file = Paths.get(Vars.saveDirectory.absolutePath(), fileName).toFile();
        Net.HttpRequest request = new Net.HttpRequest().url("http://localhost/mindustry/index.php?name=" + fileName);
        request.method(Net.HttpMethod.POST);
        try {
            FileInputStream inputStream = new FileInputStream(file);
            request.content(inputStream, file.length());
        } catch (FileNotFoundException e) {
            Vars.ui.showException(e.fillInStackTrace());
        }
        request.header("Content-Type", "multipart/form");
        request.header("Transfer-Encoding", "gzip");
        Core.net.http(request, handler -> {
                    JsonType jsonType = JsonIO.json().fromJson(JsonType.class, handler.getResultAsStream());
                    if (jsonType.getStatus() == 200) {
                        Vars.ui.showOkText("Upload", "Your campaign has been successfully uploaded to: " + jsonType.getUrl(), () -> {
                        });
                    } else {
                        Vars.ui.showOkText("Error", "Unable to upload campaign! \n The server returned: " + jsonType.getStatus(), () -> {
                        });
                    }
                },
                errorHandler -> Vars.ui.showException(errorHandler.fillInStackTrace()));
    }
}
