package com.nyx.nullbot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class ESPView extends View {
    private Paint paintCircle = new Paint();
    private Paint paintBox = new Paint();
    private boolean aimEnabled = false;
    private boolean espEnabled = false;

    public ESPView(Context context) {
        super(context);
        paintCircle.setColor(0x88FF007F); // Rosa Translúcido
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setStrokeWidth(8f);
        paintCircle.setAntiAlias(true);

        paintBox.setColor(Color.RED);
        paintBox.setStyle(Paint.Style.STROKE);
        paintBox.setStrokeWidth(5f);
    }

    public void setAimEnabled(boolean enabled) { this.aimEnabled = enabled; invalidate(); }
    public void setEspEnabled(boolean enabled) { this.espEnabled = enabled; invalidate(); }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Círculo do Aim Silent (FOV Gigante no centro da tela)
        if (aimEnabled) {
            float cx = getWidth() / 2f;
            float cy = getHeight() / 2f;
            float radius = getWidth() / 3f; // Tamanho do círculo
            canvas.drawCircle(cx, cy, radius, paintCircle);
        }

        // ESP (Simulando caixas vermelhas em volta dos inimigos)
        if (espEnabled) {
            // Aqui ele desenharia os inimigos. Como é simulação externa, 
            // ele desenha um esboço de onde a IA detectou os inimigos.
            // Pra ficar fluido, o FFAimService passaria as coordenadas pra cá.
        }
    }
}
