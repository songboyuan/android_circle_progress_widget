package com.dynasty.song.progresswidgetexample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;


/**
 * This widget is base on an open source project
 * https://github.com/passsy/android-HoloCircularProgressBar
 *
 * @author Pascal.Welsch
 * @version 1.3 (03.10.2014)
 * @since 05.03.2013
 *
 * We make some customize to make this support second progress
 * circle and implement a new style with better look.
 *
 */
public class HoloCircularProgressBar extends View {

	/**
	 * TAG constant for logging
	 */
	private static final String TAG = HoloCircularProgressBar.class.getSimpleName();

	/**
	 * used to save the super state on configuration change
	 */
	private static final String INSTANCE_STATE_SAVEDSTATE = "saved_state";

	/**
	 * used to save the progress on configuration changes
	 */
	private static final String INSTANCE_STATE_PROGRESS = "progress";

	/**
	 * used to save the marker progress on configuration changes
	 */
	private static final String INSTANCE_STATE_SECOND_PROGRESS = "second_progress";

	/**
	 * used to save the background color of the progress
	 */
	private static final String INSTANCE_STATE_PROGRESS_BACKGROUND_COLOR
			= "progress_background_color";

	/**
	 * used to save the color of the progress
	 */
	private static final String INSTANCE_STATE_PROGRESS_COLOR = "progress_color";

	/**
	 * used to save and restore the visibility of the thumb in this instance
	 */
	private static final String INSTANCE_STATE_SECOND_PROGRESS_VISIBLE = "second_visible";


	/**
	 * used to save the background color of the progress
	 */
	private static final String INSTANCE_STATE_SECOND_PROGRESS_BACKGROUND_COLOR
			= "second_progress_background_color";

	/**
	 * used to save the color of the progress
	 */
	private static final String INSTANCE_STATE_SECOND_PROGRESS_COLOR = "second_progress_color";


	/**
	 * The rectangle enclosing the circle.
	 */
	private final RectF mCircleBounds = new RectF();


	/**
	 * the paint for the background.
	 */
	private Paint mBackgroundColorPaint = new Paint();
	private Paint mSecondBackgroundColorPaint = new Paint();

	/**
	 * The stroke width used to paint the circle.
	 */
	private int mCircleStrokeWidth = 10;

	/**
	 * The gravity of the view. Where should the Circle be drawn within the given bounds
	 * <p/>
	 * {@link #computeInsets(int, int)}
	 */
	private int mGravity = Gravity.CENTER;

	/**
	 * The Horizontal inset calcualted in {@link #computeInsets(int, int)} depends on {@link
	 * #mGravity}.
	 */
	private int mHorizontalInset = 0;

	/**
	 * true if not all properties are set. then the view isn't drawn and there are no errors in the
	 * LayoutEditor
	 */
	private boolean mIsInitializing = true;

	/**
	 * true if show the second progress
	 */
	private boolean mSecondProgressEnabled = false;


	/**
	 * The current progress.
	 */
	private float mProgress = 0.0f;
	private float mSecondProgress = 0.0f;

	/**
	 * The color of the progress background.
	 */
	private int mProgressBackgroundColor;
	private int mSecondProgressBackgroundColor;

	/**
	 * the color of the progress.
	 */
	private int mProgressColor;
	private int mSecondProgressColor;

	/**
	 * paint for the progress.
	 */
	private Paint mProgressColorPaint;
	private Paint mSecondProgressColorPaint;

	private float mProgressTarget;
	private static final int AMIMATION_TARGET_FIRST=0;
	private static final int AMIMATION_TARGET_SECOND=1;



	/**
	 * Radius of the circle
	 * <p/>
	 * <p> Note: (Re)calculated in {@link #onMeasure(int, int)}. </p>
	 */
	private float mRadius;


	/**
	 * The Translation offset x which gives us the ability to use our own coordinates system.
	 */
	private float mTranslationOffsetX;

	/**
	 * The Translation offset y which gives us the ability to use our own coordinates system.
	 */
	private float mTranslationOffsetY;

	/**
	 * The Vertical inset calcualted in {@link #computeInsets(int, int)} depends on {@link
	 * #mGravity}..
	 */
	private int mVerticalInset = 0;

	/**
	 * Instantiates a new holo circular progress bar.
	 *
	 * @param context the context
	 */
	public HoloCircularProgressBar(final Context context) {
		this(context, null);
	}

	/**
	 * Instantiates a new holo circular progress bar.
	 *
	 * @param context the context
	 * @param attrs   the attrs
	 */
	public HoloCircularProgressBar(final Context context, final AttributeSet attrs) {
		this(context, attrs, R.attr.circularProgressBarStyle);
	}

	/**
	 * Instantiates a new holo circular progress bar.
	 *
	 * @param context  the context
	 * @param attrs    the attrs
	 * @param defStyle the def style
	 */
	public HoloCircularProgressBar(final Context context, final AttributeSet attrs,
                                   final int defStyle) {
		super(context, attrs, defStyle);

		// load the styled attributes and set their properties
		final TypedArray attributes = context
				.obtainStyledAttributes(attrs, R.styleable.HoloCircularProgressBar,
						defStyle, 0);
		if (attributes != null) {
			try {
				setProgressColor(attributes
						.getColor(R.styleable.HoloCircularProgressBar_progress_color, Color.CYAN));
				setProgressBackgroundColor(attributes
						.getColor(R.styleable.HoloCircularProgressBar_progress_background_color,
								Color.GREEN));
				setProgress(
						attributes.getFloat(R.styleable.HoloCircularProgressBar_progress, 0.0f));
				setWheelSize((int) attributes
						.getDimension(R.styleable.HoloCircularProgressBar_stroke_width, 10));

				setSecondProgressEnabled(attributes
						.getBoolean(R.styleable.HoloCircularProgressBar_second_visible, false));

				setSecondProgress(
						attributes.getFloat(R.styleable.HoloCircularProgressBar_second_progress,
								0.0f));

				setSecondProgressColor(attributes
						.getColor(R.styleable.HoloCircularProgressBar_second_progress_color, Color.CYAN));
				setSecondProgressBackgroundColor(attributes
						.getColor(R.styleable.HoloCircularProgressBar_second_progress_background_color,
								Color.GREEN));

				mGravity = attributes
						.getInt(R.styleable.HoloCircularProgressBar_android_gravity,
								Gravity.CENTER);
			} finally {
				// make sure recycle is always called.
				attributes.recycle();
			}
		}

		updateBackgroundColor();
		updateProgressColor();

		// the view has now all properties and can be drawn
		mIsInitializing = false;

	}

	@Override
	protected void onDraw(final Canvas canvas) {

		// All of our positions are using our internal coordinate system.
		// Instead of translating
		// them we let Canvas do the work for us.
		canvas.translate(mTranslationOffsetX, mTranslationOffsetY);

		final float progressRotation = 360 * mProgress;


		canvas.drawArc(mCircleBounds, 270, -(360 - progressRotation), false,
				mBackgroundColorPaint);

		canvas.drawArc(mCircleBounds, 270, progressRotation, false,
				mProgressColorPaint);

		if (mSecondProgressEnabled) {
			final float secondRotation = 360 * mSecondProgress;
			RectF tempRect = new RectF();
			tempRect.set(mCircleBounds.top + mCircleStrokeWidth,
					mCircleBounds.left + mCircleStrokeWidth,
					mCircleBounds.bottom - mCircleStrokeWidth,
					mCircleBounds.right - mCircleStrokeWidth);

			canvas.drawArc(tempRect, 270, -(360 - secondRotation), false,
					mSecondBackgroundColorPaint);

			// draw the progress or a full circle if overdraw is true
			canvas.drawArc(tempRect, 270, secondRotation, false,
					mSecondProgressColorPaint);
		}
	}

	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
		final int height = getDefaultSize(
				getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom(),
				heightMeasureSpec);
		final int width = getDefaultSize(
				getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight(),
				widthMeasureSpec);

		final int diameter;
		if (heightMeasureSpec == MeasureSpec.UNSPECIFIED) {
			// ScrollView
			diameter = width;
			computeInsets(0, 0);
		} else if (widthMeasureSpec == MeasureSpec.UNSPECIFIED) {
			// HorizontalScrollView
			diameter = height;
			computeInsets(0, 0);
		} else {
			// Default
			diameter = Math.min(width, height);
			computeInsets(width - diameter, height - diameter);
		}

		setMeasuredDimension(diameter, diameter);

		final float halfWidth = diameter * 0.5f;

		// width of the drawed circle (+ the drawedThumb)
		final float drawedWith;
		drawedWith = mCircleStrokeWidth / 2f;

		// -0.5f for pixel perfect fit inside the viewbounds
		mRadius = halfWidth - drawedWith - 0.5f;

		mCircleBounds.set(-mRadius, -mRadius, mRadius, mRadius);

		mTranslationOffsetX = halfWidth + mHorizontalInset;
		mTranslationOffsetY = halfWidth + mVerticalInset;

	}

	@Override
	protected void onRestoreInstanceState(final Parcelable state) {
		if (state instanceof Bundle) {
			final Bundle bundle = (Bundle) state;
			setProgress(bundle.getFloat(INSTANCE_STATE_PROGRESS));
			setSecondProgress(bundle.getFloat(INSTANCE_STATE_SECOND_PROGRESS));

			final int progressColor = bundle.getInt(INSTANCE_STATE_PROGRESS_COLOR);
			if (progressColor != mProgressColor) {
				mProgressColor = progressColor;
				updateProgressColor();
			}

			final int progressBackgroundColor = bundle
					.getInt(INSTANCE_STATE_PROGRESS_BACKGROUND_COLOR);
			if (progressBackgroundColor != mProgressBackgroundColor) {
				mProgressBackgroundColor = progressBackgroundColor;
				updateBackgroundColor();
			}

			final int secondProgressColor = bundle.getInt(INSTANCE_STATE_SECOND_PROGRESS_COLOR);
			if (secondProgressColor != mSecondProgressColor) {
				mSecondProgressColor = secondProgressColor;
				updateProgressColor();
			}

			final int secondProgressBackgroundColor = bundle
					.getInt(INSTANCE_STATE_SECOND_PROGRESS_BACKGROUND_COLOR);
			if (secondProgressBackgroundColor != mSecondProgressBackgroundColor) {
				mSecondProgressBackgroundColor = secondProgressBackgroundColor;
				updateBackgroundColor();
			}

			mSecondProgressEnabled = bundle.getBoolean(INSTANCE_STATE_SECOND_PROGRESS_VISIBLE);

			super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE_SAVEDSTATE));
			return;
		}

		super.onRestoreInstanceState(state);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Bundle bundle = new Bundle();
		bundle.putParcelable(INSTANCE_STATE_SAVEDSTATE, super.onSaveInstanceState());
		bundle.putFloat(INSTANCE_STATE_PROGRESS, mProgress);
		bundle.putInt(INSTANCE_STATE_PROGRESS_COLOR, mProgressColor);
		bundle.putInt(INSTANCE_STATE_PROGRESS_BACKGROUND_COLOR, mProgressBackgroundColor);
		bundle.putBoolean(INSTANCE_STATE_SECOND_PROGRESS_VISIBLE, mSecondProgressEnabled);
		bundle.putFloat(INSTANCE_STATE_SECOND_PROGRESS, mSecondProgress);
		bundle.putInt(INSTANCE_STATE_SECOND_PROGRESS_COLOR, mSecondProgressColor);
		bundle.putInt(INSTANCE_STATE_SECOND_PROGRESS_BACKGROUND_COLOR, mSecondProgressBackgroundColor);

		return bundle;
	}


	/**
	 * Sets the marker progress.
	 *
	 * @param progress the new marker progress
	 */
	public void setSecondProgress(final float progress) {

		float target;
		if(progress > 1)
			target = 1.0f;
		else if ( progress <= 0 ){
			target = 0.0f;
		} else {
			target = progress;
		}

		if (!mIsInitializing && mSecondProgress != target) {
			if(target > 0.01f) {
				AnimationTask animationTask = new AnimationTask(AMIMATION_TARGET_SECOND, target);
				animationTask.execute(Float.valueOf(0));
			} else {
				mSecondProgress = target;
				invalidate();
			}
		} else {
			mSecondProgress = target;
		}
	}

	/**
	 * Sets the progress.
	 *
	 * @param progress the new progress
	 */
	public void setProgress(final float progress) {

		float target;
		if(progress > 1)
			target = 1.0f;
		else if ( progress <= 0 ){
			target = 0.0f;
		}else {
			target = progress;
		}

		if (!mIsInitializing && mProgress != target) {
			if(target > 0.01f) {
				AnimationTask animationTask = new AnimationTask(AMIMATION_TARGET_FIRST, target);
				animationTask.execute(Float.valueOf(0));
			} else {
				mProgress = target;
				invalidate();
			}
		} else {
			mProgress = target;
		}
	}


	class AnimationTask extends AsyncTask<Float, Float, Float> {

		int type;
		float target;
		AnimationTask(int type, float target) {
			this.type = type;
			this.target = target;
		}

		@Override
		protected Float doInBackground(Float... floats) {
			try {
				float start = 0;
				while (start < target) {
					start += target/30f;
					publishProgress(start);
					Thread.sleep(8);
				}
			} catch (Exception e) {
				Log.e(TAG, "errors in progress thread : " + e.getMessage(), e);
				publishProgress(target);
			}
			return target;
		}

		@Override
		protected void onPostExecute(Float result) {
			super.onPostExecute(result);
			if(type == AMIMATION_TARGET_FIRST) {
				mProgress = result.floatValue();
			} else {
				mSecondProgress = result.floatValue();
			}
			invalidate();
		}

		@Override
		protected void onProgressUpdate(Float... values) {
			super.onProgressUpdate(values);
			if(type == AMIMATION_TARGET_FIRST) {
				mProgress = values[0].floatValue();
			} else {
				mSecondProgress = values[0].floatValue();
			}
			invalidate();
		}
	}


	/**
	 * Sets the progress background color.
	 *
	 * @param color the new progress background color
	 */
	public void setProgressBackgroundColor(final int color) {
		mProgressBackgroundColor = color;

		updateBackgroundColor();
	}

	/**
	 * Sets the progress color.
	 *
	 * @param color the new progress color
	 */
	public void setProgressColor(final int color) {
		mProgressColor = color;

		updateProgressColor();
	}


	/**
	 * Sets the progress background color.
	 *
	 * @param color the new progress background color
	 */
	public void setSecondProgressBackgroundColor(final int color) {
		mSecondProgressBackgroundColor = color;
		updateBackgroundColor();
	}

	/**
	 * Sets the progress color.
	 *
	 * @param color the new progress color
	 */
	public void setSecondProgressColor(final int color) {
		mSecondProgressColor = color;
		updateProgressColor();
	}

	/**
	 * shows or hides the thumb of the progress bar
	 *
	 * @param enabled true to show the thumb
	 */
	public void setSecondProgressEnabled(final boolean enabled) {
		mSecondProgressEnabled = enabled;
	}

	/**
	 * Sets the wheel size.
	 *
	 * @param dimension the new wheel size
	 */
	public void setWheelSize(final int dimension) {
		mCircleStrokeWidth = dimension;

		// update the paints
		updateBackgroundColor();
		updateProgressColor();
	}

	/**
	 * Compute insets.
	 * <p/>
	 * <pre>
	 *  ______________________
	 * |_________dx/2_________|
	 * |......| /'''''\|......|
	 * |-dx/2-|| View ||-dx/2-|
	 * |______| \_____/|______|
	 * |________ dx/2_________|
	 * </pre>
	 *
	 * @param dx the dx the horizontal unfilled space
	 * @param dy the dy the horizontal unfilled space
	 */
	@SuppressLint("NewApi")
	private void computeInsets(final int dx, final int dy) {
		int absoluteGravity = mGravity;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			absoluteGravity = Gravity.getAbsoluteGravity(mGravity, getLayoutDirection());
		}

		switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
			case Gravity.LEFT:
				mHorizontalInset = 0;
				break;
			case Gravity.RIGHT:
				mHorizontalInset = dx;
				break;
			case Gravity.CENTER_HORIZONTAL:
			default:
				mHorizontalInset = dx / 2;
				break;
		}
		switch (absoluteGravity & Gravity.VERTICAL_GRAVITY_MASK) {
			case Gravity.TOP:
				mVerticalInset = 0;
				break;
			case Gravity.BOTTOM:
				mVerticalInset = dy;
				break;
			case Gravity.CENTER_VERTICAL:
			default:
				mVerticalInset = dy / 2;
				break;
		}
	}

	/**
	 * updates the paint of the background
	 */
	private void updateBackgroundColor() {
		mBackgroundColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBackgroundColorPaint.setColor(mProgressBackgroundColor);
		mBackgroundColorPaint.setStyle(Paint.Style.STROKE);
		mBackgroundColorPaint.setStrokeWidth(mCircleStrokeWidth);

		mSecondBackgroundColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mSecondBackgroundColorPaint.setColor(mSecondProgressBackgroundColor);
		mSecondBackgroundColorPaint.setStyle(Paint.Style.STROKE);
		mSecondBackgroundColorPaint.setStrokeWidth(mCircleStrokeWidth);

		invalidate();
	}

	/**
	 * updates the paint of the progress and the thumb to give them a new visual style
	 */
	private void updateProgressColor() {
		mProgressColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mProgressColorPaint.setColor(mProgressColor);
		mProgressColorPaint.setStyle(Paint.Style.STROKE);
		mProgressColorPaint.setStrokeCap(Paint.Cap.ROUND);
		mProgressColorPaint.setStrokeWidth(mCircleStrokeWidth);

		mSecondProgressColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mSecondProgressColorPaint.setColor(mSecondProgressColor);
		mSecondProgressColorPaint.setStyle(Paint.Style.STROKE);
		mSecondProgressColorPaint.setStrokeCap(Paint.Cap.ROUND);
		mSecondProgressColorPaint.setStrokeWidth(mCircleStrokeWidth);

		invalidate();
	}

}
