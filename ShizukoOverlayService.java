package com.nyx.nullbot;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class ShizukoOverlayService extends Service {
    private WindowManager wm;
    private View overlay;
    private ESPView espView;
    private boolean aimbotOn = false, spinOn = false;

    @Override public IBinder onBind(Intent i) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        // ==========================================
        // INTERFACE MODERNA (SHIZUKO)
        // ==========================================
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(40, 40, 40, 40);
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xF00A0A0A); // Preto profundo
        bg.setCornerRadius(60f);
        bg.setStroke(6, 0xFFFF007F); // Borda Neon Rosa
        box.setBackground(bg);

        TextView title = new TextView(this);
        title.setText("⚡ S H I Z U K O");
        title.setTextColor(0xFFFF007F);
        title.setTextSize(16f);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        title.setPadding(0, 0, 0, 30);
        box.addView(title);

        // Botão AIM SILENT (FOV)
        TextView btnAim = createBtn("AIM SILENT: OFF");
        btnAim.setOnClickListener(v -> {
            aimbotOn = !aimbotOn;
            btnAim.setText(aimbotOn ? "AIM SILENT: ON" : "AIM SILENT: OFF");
            btnAim.setBackgroundColor(aimbotOn ? 0xFF00E676 : 0xFFB71C1C);
            ShizukoAimService.aimEnabled = aimbotOn;
            espView.setAimEnabled(aimbotOn); // Ativa o círculo na tela
        });
        box.addView(btnAim);

        // Botão ESP
        TextView btnEsp = createBtn("ESP BOX / LINE: OFF");
        btnEsp.setOnClickListener(v -> {
            ShizukoAimService.espEnabled = !ShizukoAimService.espEnabled;
            btnEsp.setText(ShizukoAimService.espEnabled ? "ESP BOX / LINE: ON" : "ESP BOX / LINE: OFF");
            btnEsp.setBackgroundColor(ShizukoAimService.espEnabled ? 0xFF00E676 : 0xFFB71C1C);
            espView.setEspEnabled(ShizukoAimService.espEnabled);
        });
        box.addView(btnEsp);

        // Botão SPIN BOT
        TextView btnSpin = createBtn("SPIN BOT: OFF");
        btnSpin.setOnClickListener(v -> {
            spinOn = !spinOn;
            btnSpin.setText(spinOn ? "SPIN BOT: ON" : "SPIN BOT: OFF");
            btnSpin.setBackgroundColor(spinOn ? 0xFF00E676 : 0xFFB71C1C);
            ShizukoAimService.spinEnabled = spinOn;
        });
        box.addView(btnSpin);

        // Slider de Velocidade do Spin
        TextView spinLabel = new TextView(this);
        spinLabel.setText("SPIN SPEED: 5");
        spinLabel.setTextColor(Color.WHITE);
        spinLabel.setTextSize(12f);
        spinLabel.setTypeface(Typeface.MONOSPACE);
        spinLabel.setPadding(0, 20, 0, 10);
        box.addView(spinLabel);

        SeekBar spinSlider = new SeekBar(this);
        spinSlider.setMax(10);
        spinSlider.setProgress(5);
        spinSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 1) progress = 1;
                spinLabel.setText("SPIN SPEED: " + progress);
                ShizukoAimService.spinSpeed = progress;
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        box.addView(spinSlider);

        // Configuração da Janela
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.TOP | Gravity.START;
        lp.x = 40; lp.y = 200;
        wm.addView(box, lp);
        overlay = box;

        // Arrastar pelo título
        title.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = lp.x; initialY = lp.y;
                        initialTouchX = event.getRawX(); initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        lp.x = initialX + (int) (event.getRawX() - initialTouchX);
                        lp.y = initialY + (int) (event.getRawY() - initialTouchY);
                        wm.updateViewLayout(box, lp);
                        return true;
                }
                return false;
            }
        });

        // ==========================================
        // OVERLAY DO ESP E DO CÍRCULO (AIM FOV)
        // ==========================================
        espView = new ESPView(this);
        WindowManager.LayoutParams espLp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        wm.addView(espView, espLp);
    }

    private TextView createBtn(String text) {
        TextView btn = new TextView(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(12f);
        btn.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        btn.setGravity(Gravity.CENTER);
        btn.setPadding(20, 25, 20, 25);
        btn.setBackgroundColor(0xFFB71C1C);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 15);
        btn.setLayoutParams(params);
        return btn;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlay != null) wm.removeView(overlay);
        if (espView != null) wm.removeView(espView);
    }
    }
