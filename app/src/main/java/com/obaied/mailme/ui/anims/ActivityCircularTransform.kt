package com.joseph.mailme.ui.anims

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.transition.Transition
import android.transition.TransitionValues
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.ViewOutlineProvider

/**
 * Created by ab on 19.10.17.
 */
@SuppressLint("SupportAnnotationUsage")
class ActivityCircularTransform() :
        Transition() {
    private var color: Int = 0
    private var iconResId: Int = 0

    constructor(@ColorInt color: Int,
                @DrawableRes icon: Int) : this() {
        this.color = color
        this.iconResId = icon
        duration = DEFAULT_DURATION
    }

    companion object {
        private val EXTRA_COLOR = "EXTRA_COLOR"
        private val EXTRA_ICON = "EXTRA_ICON"
        private val PROP_BOUNDS = "mailme:ActivityCircularTransform:bounds"
        private val DEFAULT_DURATION = 350L

        /**
         * Create a [ActivityCircularTransform] from the supplied `activity` extras and set as its
         * shared element enter/return transition.
         */
        fun setup(activity: Activity, target: View?): Boolean {
            val intent = activity.intent
            if (!intent.hasExtra(EXTRA_COLOR)
                    || !intent.hasExtra(EXTRA_ICON)) {
                return false
            }

            val color = intent.getIntExtra(EXTRA_COLOR, Color.TRANSPARENT)
            val icon = intent.getIntExtra(EXTRA_ICON, -1)
            val sharedEnter = ActivityCircularTransform(color, icon)
            if (target != null) {
                sharedEnter.addTarget(target)
            }
            activity.window.sharedElementEnterTransition = sharedEnter
            return true
        }

        fun addExtras(intent: Intent,
                      @ColorInt fabColor: Int,
                      @DrawableRes icon: Int) {
            intent.putExtra(EXTRA_COLOR, fabColor)
            intent.putExtra(EXTRA_ICON, icon)
        }
    }

    override fun captureStartValues(transitionValues: TransitionValues?) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues?) {
        captureValues(transitionValues)
    }

    override fun createAnimator(sceneRoot: ViewGroup,
                                startValues: TransitionValues?,
                                endValues: TransitionValues?): Animator? {
        if (startValues == null || endValues == null) return null

        val startBounds = startValues.values[PROP_BOUNDS] as Rect
        val endBounds = endValues.values[PROP_BOUNDS] as Rect
        val fromFab = endBounds.width() >= startBounds.width()

        val activityBounds = if (fromFab) endBounds else startBounds

        val endView = endValues.view

        if (!fromFab) {
            // Force the measurement of the dialog in its current, starting position.
            // It is a bit weird, but that's how the system works now
            endView.measure(
                    View.MeasureSpec.makeMeasureSpec(startBounds.width(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(startBounds.height(), View.MeasureSpec.EXACTLY)
            )
            endView.layout(startBounds.left, startBounds.top, startBounds.right, startBounds.bottom)
        }

        //Color fade animation
        val colorFadeAnimator = makeColorFadeAnimator(sceneRoot.context, activityBounds, fromFab, endView)

        // Circular reveal
        val circularRevealAnimator = makeCircularRevealAnimator(sceneRoot.context, fromFab, endView, startBounds, endBounds)

        val elevationAnimator = makeElevationAnimator(fromFab, endView, sceneRoot.context)

        val transition = AnimatorSet()
        transition.playTogether(
                colorFadeAnimator,
                circularRevealAnimator
        )
        elevationAnimator?.let { transition.play(elevationAnimator) }
        if (fromFab) {
            transition.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    endView.overlay.clear()
                }
            })
        }

        return transition
    }

    private fun makeCircularRevealAnimator(context: Context,
                                           fromFab: Boolean,
                                           endView: View,
                                           startBounds: Rect,
                                           endBounds: Rect): Animator {
        val circularRevealAnimator: Animator
        if (fromFab) {
            circularRevealAnimator = ViewAnimationUtils.createCircularReveal(
                    endView,
                    startBounds.centerX(),
                    startBounds.centerY() - startBounds.height(),
                    (startBounds.width() / 2).toFloat(),
                    Math.hypot((endBounds.width() / 2).toDouble(), (endBounds.height() / 2).toDouble()).toFloat() * 2.0f
            )
            circularRevealAnimator.interpolator = AnimUtils.getFastOutLinearInInterpolator(context)

        } else {
            circularRevealAnimator = ViewAnimationUtils.createCircularReveal(
                    endView,
                    endBounds.centerX(),
                    endBounds.centerY() - endBounds.height(),
                    Math.hypot((startBounds.width() / 2).toDouble(), (startBounds.height() / 2).toDouble()).toFloat() * 2.0f,
                    (endBounds.width() / 2).toFloat()
            )
            circularRevealAnimator.interpolator = AnimUtils.getLinearOutSlowInInterpolator(context)

            circularRevealAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    endView.outlineProvider = object : ViewOutlineProvider() {
                        override fun getOutline(view: View, outline: Outline) {
                            val left = endBounds.left
                            val top = endBounds.top - endBounds.height()
                            val bottom = endBounds.bottom - endBounds.height()
                            val right = endBounds.right
                            outline.setOval(
                                    left, top, right, bottom)
                            view.clipToOutline = true
                        }
                    }
                }
            })
        }
        return circularRevealAnimator
    }

    private fun makeElevationAnimator(fromFab: Boolean, endView: View, context: Context): Animator? {
        // Work around issue with elevation shadows. At the end of the return transition the shared
        // element's shadow is drawn twice (by each activity) which is jarring. This workaround
        // still causes the shadow to snap, but it's better than seeing it double drawn.
        var elevation: Animator? = null
        if (!fromFab) {
            elevation = ObjectAnimator.ofFloat(endView, View.TRANSLATION_Z, -endView.elevation)
//            elevation.duration = animatorDuration
            elevation.interpolator = AnimUtils.getFastOutSlowInInterpolator(context)
        }
        return elevation
    }

    private fun makeColorFadeAnimator(context: Context, bounds: Rect, fromFab: Boolean, endView: View): ObjectAnimator? {
        val fabColor = ColorDrawable(color)
        fabColor.setBounds(0, 0, bounds.width(), bounds.height())
        if (!fromFab) fabColor.alpha = 0 //Reset alpha animation on the way back
        endView.overlay.add(fabColor)

        val colorFade = ObjectAnimator.ofInt(fabColor, "alpha", if (fromFab) 0 else 255)
        if (!fromFab) {
//            colorFade.startDelay = animatorDuration
        }
//        colorFade.duration = animatorDuration
        colorFade.interpolator = AnimUtils.getFastOutSlowInInterpolator(context);
        return colorFade
    }

    private fun captureValues(transitionValues: TransitionValues?) {
        val view = transitionValues?.view ?: return
        if (view.width <= 0 || view.height <= 0) return
        if (view !is ViewGroup) {
            return
        }

        transitionValues.values.put(
                PROP_BOUNDS,
                Rect(
                        view.left,
                        view.top,
                        view.right,
                        view.bottom
                )
        )
    }
}