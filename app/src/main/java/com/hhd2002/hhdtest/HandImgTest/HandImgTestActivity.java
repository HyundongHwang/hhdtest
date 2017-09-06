package com.hhd2002.hhdtest.HandImgTest;

import java.util.*;

import android.content.*;
import android.graphics.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.*;


public class HandImgTestActivity
		extends AppCompatActivity {

	public class Vertex {
		float _x;
		float _y;
		boolean _draw;

		public Vertex(float x, float y, boolean draw) {
			_x = x;
			_y = y;
			_draw = draw;
		}
	}

	public class MyView extends View {

		Paint _paint;

		public MyView(Context context) {
			super(context);

			_paint = new Paint();
			_paint.setColor(Color.BLACK);
			_paint.setStrokeWidth(3);
			_paint.setAntiAlias(true);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			for (int i = 0; i < _vertexList.size(); i++) {
				if (_vertexList.get(i)._draw) {
					canvas.drawLine(_vertexList.get(i - 1)._x,
							_vertexList.get(i - 1)._y, _vertexList.get(i)._x,
							_vertexList.get(i)._y, _paint);
				}
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				_vertexList.add(new Vertex(event.getX(), event.getY(), false));
				return true;
			}
			
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				_vertexList.add(new Vertex(event.getX(), event.getY(), true));
				invalidate();
				return true;
			}

			return false;
		}

	}

	ArrayList<Vertex> _vertexList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		_vertexList = new ArrayList<HandImgTestActivity.Vertex>();
		MyView mv = new MyView(this);
		setContentView(mv);
	}

}
