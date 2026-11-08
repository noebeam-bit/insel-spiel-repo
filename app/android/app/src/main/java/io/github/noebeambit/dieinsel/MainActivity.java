package io.github.noebeambit.dieinsel;

import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(InselDateiPlugin.class);
        super.onCreate(savedInstanceState);
        // Zurück-Taste: offenes Blatt schließen, sonst App in den Hintergrund (nicht beenden).
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (bridge == null || bridge.getWebView() == null) {
                    moveTaskToBack(true);
                    return;
                }
                bridge.getWebView().evaluateJavascript(
                    "(function(){var s=document.getElementById('sheet');" +
                    "if(s&&!s.classList.contains('hidden')){var c=document.getElementById('sheetClose');if(c){c.click();return 1;}}" +
                    "return 0;})()",
                    value -> {
                        if (!"1".equals(value)) moveTaskToBack(true);
                    });
            }
        });
    }
}
