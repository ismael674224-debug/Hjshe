package com.nyx.nullbot;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;

public class ShizukoAimService extends AccessibilityService {
    public static boolean aimEnabled = false;
    public static boolean espEnabled = false;
    public static boolean spinEnabled = false;
    public static int spinSpeed = 5; // 1 a 10 (controlado pelo slider)

    private static ShizukoAimService instance;

    public static ShizukoAimService get() { return instance; }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        startSpinThread();
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent e) {}
    @Override public void onInterrupt() {}

    // Thread do Spin Bot (Faz o boneco girar sozinho)
    private void startSpinThread() {
        new Thread(() -> {
            while (true) {
                if (spinEnabled) {
                    // O analógico fica no canto inferior esquerdo
                    float joyX = 200f; 
                    float joyY = 1300f; 

                    // Faz um círculo com o toque pra girar o boneco
                    Path p = new Path();
                    p.moveTo(joyX, joyY);
                    p.lineTo(joyX + 80, joyY + 50);
                    p.lineTo(joyX, joyY + 100);
                    p.lineTo(joyX - 80, joyY + 50);
                    p.close();

                    // A velocidade é baseada no slider (quanto maior a velocidade, menor o delay)
                    long duration = 500 - (spinSpeed * 40); 

                    GestureDescription g = new GestureDescription.Builder()
                            .addStroke(new GestureDescription.StrokeDescription(p, 0, duration))
                            .build();
                    dispatchGesture(g, null, null);
                }
                try {
                    // Delay baseado na velocidade
                    Thread.sleep(500 - (spinSpeed * 40));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Função pra puxar a mira pra cabeça do inimigo (chamada pelo detector)
    public void pullAim(float x, float y) {
        if (!aimEnabled) return;
        Path p = new Path();
        p.moveTo(x, y); // Simula o toque na cabeça do inimigo
        GestureDescription g = new GestureDescription.Builder()
                .addStroke(new GestureDescription.StrokeDescription(p, 0, 40))
                .build();
        dispatchGesture(g, null, null);
    }
}
