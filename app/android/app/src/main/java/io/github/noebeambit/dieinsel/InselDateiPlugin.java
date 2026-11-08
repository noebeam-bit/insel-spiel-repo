package io.github.noebeambit.dieinsel;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/** Speichert den Spielstand als Textdatei in „Downloads“ (im Browser übernimmt das der Download-Link). */
@CapacitorPlugin(name = "InselDatei")
public class InselDateiPlugin extends Plugin {
    @PluginMethod
    public void save(PluginCall call) {
        String name = call.getString("filename", "die-insel.txt");
        String data = call.getString("data", "");
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                ContentValues v = new ContentValues();
                v.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
                v.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
                v.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                Uri uri = getContext().getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, v);
                if (uri == null) throw new Exception("kein Platz in Downloads");
                try (OutputStream out = getContext().getContentResolver().openOutputStream(uri)) {
                    out.write(bytes);
                }
                toast("Gespeichert in Downloads: " + name);
            } else {
                // ältere Android-Versionen: über „Teilen“ weitergeben (z. B. an Dateien oder Drive)
                File f = new File(getContext().getCacheDir(), name);
                try (FileOutputStream out = new FileOutputStream(f)) {
                    out.write(bytes);
                }
                Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", f);
                Intent send = new Intent(Intent.ACTION_SEND);
                send.setType("text/plain");
                send.putExtra(Intent.EXTRA_STREAM, uri);
                send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                getActivity().startActivity(Intent.createChooser(send, "Spielstand sichern"));
            }
            JSObject ret = new JSObject();
            ret.put("ok", true);
            call.resolve(ret);
        } catch (Exception e) {
            toast("Speichern ging nicht. Nimm „Code kopieren“.");
            call.reject(e.getMessage());
        }
    }

    private void toast(String text) {
        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show());
    }
}
