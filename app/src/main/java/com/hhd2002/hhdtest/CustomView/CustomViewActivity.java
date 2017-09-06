package com.hhd2002.hhdtest.CustomView;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hhd2002.hhdtest.R;

public class CustomViewActivity
        extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.layout_custom_view);
        MyView view = new MyView(this);
        this.setContentView(view);
    }

    public class MyView extends View {

        Paint paint;
        Bitmap bit;
        Rect dst;

        public MyView(Context context) {
            super(context);
            this.paint = new Paint();
            this.paint.setColor(Color.LTGRAY);

            Resources res = this.getResources();
            BitmapDrawable bd = (BitmapDrawable) res.getDrawable(R.drawable.f13965592221_74252f51c1_z);
            this.bit = bd.getBitmap();

            this.dst = new Rect(300, 300, 1000, 1000);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(Color.BLUE);
            canvas.drawCircle(100, 100, 80, this.paint);
            canvas.drawBitmap(this.bit, null, this.dst, null);
        }
    }
}
