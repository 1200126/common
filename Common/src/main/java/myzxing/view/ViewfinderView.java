package myzxing.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.hzy.common.R;

import java.util.ArrayList;
import java.util.List;

import myzxing.camera.CameraManager;


public final class ViewfinderView extends View {

	private static final int[] SCANNER_ALPHA = { 0, 64, 128, 192, 255, 192, 128, 64 };
	private static final int CURRENT_POINT_OPACITY = 0xA0;
	private static final int MAX_RESULT_POINTS = 2000;
	private static final long ANIMATION_DELAY = 10L;
	private static final int OPAQUE = 0xFF;
	private CameraManager cameraManager;
	private final Paint paint;
	private Bitmap resultBitmap;
	private final int maskColor;
	private final int resultColor;
	private int scannerAlpha;
	private final int resultPointColor;
	private List<ResultPoint> possibleResultPoints;
	private List<ResultPoint> lastPossibleResultPoints;
	/**
	 * 中间那条线每次刷新移动的距离
	 */
	private static final int SPEEN_DISTANCE = 10;

	private int i = 0;// 添加的
	private Rect mRect;// 扫描线填充边界
	private GradientDrawable mDrawable;// 采用渐变图作为扫描线
	private Drawable lineDrawable;// 采用图片作为扫描线

	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);

		// GradientDrawable、lineDrawable
		mRect = new Rect();
		int left = Color.parseColor("#99CC33");
		int center = Color.parseColor("#00CC00");
		int right = Color.parseColor("#99CC33");
		lineDrawable = getResources().getDrawable(R.drawable.scan_code_line);
		mDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
				new int[] { left, left, center, right, right });

		scannerAlpha = 0;
		resultPointColor = resources.getColor(R.color.possible_result_points);
		possibleResultPoints = new ArrayList<ResultPoint>(5);
		lastPossibleResultPoints = null;
	}

	public void setCameraManager(CameraManager cameraManager) {
		this.cameraManager = cameraManager;
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (cameraManager == null) {
			return;
		}

		Rect frame = cameraManager.getFramingRect();
		if (frame == null) {
			return;
		}

		// 绘制遮掩层
		drawCover(canvas, frame);

		if (resultBitmap != null) {
			// 在扫描框中画出预览图
			paint.setAlpha(CURRENT_POINT_OPACITY);
			canvas.drawBitmap(resultBitmap, null, frame, paint);
		} else {

			// 画扫描框边上的角
			drawRectEdges(canvas, frame);

			// 绘制扫描线
			drawScanningLine(canvas, frame);

//			List<ResultPoint> currentPossible = possibleResultPoints;
//			Collection<ResultPoint> currentLast = lastPossibleResultPoints;
//			if (currentPossible.isEmpty()) {
//				lastPossibleResultPoints = null;
//			} else {
//				possibleResultPoints = new ArrayList<ResultPoint>(5);
//				lastPossibleResultPoints = currentPossible;
//				paint.setAlpha(OPAQUE);
//				paint.setColor(resultPointColor);
//				for (ResultPoint point : currentPossible) {
//					canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
//					MUtils.log("frame.left>>"+frame.left +"  "+ point.getX() +  "  " +frame.top +"  "+point.getY());
//				}
//			}
//			if (currentLast != null) {
//				paint.setAlpha(OPAQUE / 2);
//				paint.setColor(resultPointColor);
//				for (ResultPoint point : currentLast) {
//					canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
//				}
//			}

			// 重复执行扫描框区域绘制(画四个角及扫描线)
			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
		}
	}

	public void drawViewfinder() {
		Bitmap resultBitmap = this.resultBitmap;
		this.resultBitmap = null;
		if (resultBitmap != null) {
			resultBitmap.recycle();
		}
		invalidate();
	}

	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		List<ResultPoint> points = possibleResultPoints;
		synchronized (points) {
			points.add(point);
			int size = points.size();
			if (size > MAX_RESULT_POINTS) {
				points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
			}
		}
	}

	public void recycleLineDrawable() {
		if (mDrawable != null) {
			mDrawable.setCallback(null);
		}
		if (lineDrawable != null) {
			lineDrawable.setCallback(null);
		}
	}

	/**
	 * 绘制遮掩层
	 * 
	 * @param canvas
	 * @param frame
	 */
	private void drawCover(Canvas canvas, Rect frame) {
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		// 画扫描框外部的暗色背景
		// 设置蒙板颜色
		paint.setColor(resultBitmap != null ? resultColor : maskColor);
		// 头部
		canvas.drawRect(0, 0, width, frame.top, paint);
		// 左边
		canvas.drawRect(0, frame.top, frame.left, frame.bottom, paint);
		// 右边
		canvas.drawRect(frame.right, frame.top, width, frame.bottom, paint);
		// 底部
		canvas.drawRect(0, frame.bottom, width, height, paint);
	}

	/**
	 * 描绘方形的四个角
	 * 
	 * @param canvas
	 * @param frame
	 */
	private void drawRectEdges(Canvas canvas, Rect frame) {
		// 画出四个角
		paint.setColor(Color.parseColor("#00CC00"));
		// 左上角
		canvas.drawRect(frame.left, frame.top, frame.left + 15, frame.top + 5, paint);
		canvas.drawRect(frame.left, frame.top, frame.left + 5, frame.top + 15, paint);
		// 右上角
		canvas.drawRect(frame.right - 15, frame.top, frame.right, frame.top + 5, paint);
		canvas.drawRect(frame.right - 5, frame.top, frame.right, frame.top + 15, paint);
		// 左下角
		canvas.drawRect(frame.left, frame.bottom - 5, frame.left + 15, frame.bottom, paint);
		canvas.drawRect(frame.left, frame.bottom - 15, frame.left + 5, frame.bottom, paint);
		// 右下角
		canvas.drawRect(frame.right - 15, frame.bottom - 5, frame.right, frame.bottom, paint);
		canvas.drawRect(frame.right - 5, frame.bottom - 15, frame.right, frame.bottom, paint);
	}

	/**
	 * 绘制扫描线
	 * 
	 * @param canvas
	 * @param frame
	 *            扫描框
	 */
	private void drawScanningLine(Canvas canvas, Rect frame) {
		// 在扫描框中画出模拟扫描的线条
		// 设置扫描线条颜色为绿色
		paint.setColor(Color.parseColor("#00CC00"));
		// 设置绿色线条的透明值
		paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
		// 透明度变化
		scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;

		// 将扫描线修改为上下走的线
		if ((i += SPEEN_DISTANCE) < frame.bottom - frame.top) {
			/* 以下为图片作为扫描线 */
			mRect.set(frame.left - 6, frame.top + i - 6, frame.right + 6, frame.top + 6 + i);
			lineDrawable.setBounds(mRect);
			lineDrawable.draw(canvas);
			// 刷新
			invalidate();
		} else {
			i = 0;
		}
	}

}
