package com.obaied.mailme.ui.anims

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
import android.support.v4.content.ContextCompat
import android.transition.ArcMotion
import android.transition.Transition
import android.transition.TransitionValues
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import com.obaied.mailme.util.d

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
        pathMotion = ArcMotion()
    }

    companion object {
        private val EXTRA_COLOR = "EXTRA_COLOR"
        private val EXTRA_ICON = "EXTRA_ICON"
        private val PROP_BOUNDS = "mailme:ActivityCircularTransform:bounds"
        private val DEFAULT_DURATION = 240L

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
        d { "captureStartValues" }
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues?) {
        d { "captureEndValues" }
        captureValues(transitionValues)
    }

    override fun createAnimator(sceneRoot: ViewGroup,
                                startValues: TransitionValues?,
                                endValues: TransitionValues?): Animator? {
        if (startValues == null || endValues == null) return null

        val startBounds = startValues.values[PROP_BOUNDS] as Rect
        val endBounds = endValues.values[PROP_BOUNDS] as Rect
        val fromFab = endBounds.width() >= startBounds.width()

        val fabBounds = if (fromFab) startBounds else endBounds
        val activityBounds = if (fromFab) endBounds else startBounds

        val endView = endValues.view
        val startView = startValues.view

//        d { "createAnimator(): startView: [${startValues.view.javaClass.name}] | endView: [${endValues.view.javaClass.name}] | fromFab: [$fromFab]" }
//        d { "createAnimator(): startBounds: [${startBounds.width()}] | endView: [${endBounds.width()}] " }

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

        //iconResId fade animation
//        val iconFadeAnimator = makeIconFadeAnimator(sceneRoot.context, activityBounds, fromFab, endView)

        // Translation animation
//        val translateAnimator = makeTranslateAnimator(sceneRoot.context, startBounds, endBounds, fromFab, endView)

        // Circular reveal
        val circularRevealAnimator: Animator = makeCircularRevealAnimator(sceneRoot.context, fromFab, endView, startView, startBounds, endBounds, fabBounds)

//        val fadeContentsAnimator: List<Animator>? = makeFadeContentAnimators(sceneRoot.context, endView, fromFab)

        val elevationAnimator = makeElevationAnimator(fromFab, endView, sceneRoot.context)

        val transition = AnimatorSet()
        transition.playTogether(
                colorFadeAnimator,
                //                iconFadeAnimator,
                //                translateAnimator,
                circularRevealAnimator
        )
//        fadeContentsAnimator?.let { transition.playTogether(it) }
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
                                           startView: View,
                                           startBounds: Rect,
                                           endBounds: Rect,
                                           fabBounds: Rect): Animator {
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
                            val left = (view.width - fabBounds.width()) / 2
                            val top = (view.height - fabBounds.height()) / 2
                            outline.setOval(
                                    left, top, left + fabBounds.width(), top + fabBounds.height())
                            view.clipToOutline = true
                        }
                    }
                }
            })
        }
        return circularRevealAnimator
    }


    private fun makeIconFadeAnimator(context: Context, endBounds: Rect, fromFab: Boolean, endView: View): Animator? {
        val iconDrawable = ContextCompat.getDrawable(context, iconResId).mutate()
        if (!fromFab) iconDrawable.alpha = 0

        iconDrawable.setBounds(
                (endBounds.width() - iconDrawable.intrinsicWidth) / 2,
                (endBounds.height() - iconDrawable.intrinsicHeight) / 2,
                ((endBounds.width() - iconDrawable.intrinsicWidth) / 2) + iconDrawable.intrinsicWidth,
                ((endBounds.height() - iconDrawable.intrinsicHeight) / 2) + iconDrawable.intrinsicHeight
        )

        endView.overlay.add(iconDrawable)

        val iconFade = ObjectAnimator.ofInt(iconDrawable, "alpha", if (fromFab) 0 else 255)
        iconFade.interpolator = AnimUtils.getFastOutSlowInInterpolator(context)
        return iconFade
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

    private fun makeFadeContentAnimators(context: Context, endView: View, fromFab: Boolean): List<Animator>? {
        var fadeContents: List<Animator>? = null
        if (endView is ViewGroup) {
            fadeContents = ArrayList(endView.childCount)
            for (i in endView.childCount - 1 downTo 0) {
                val child = endView.getChildAt(i)
                val fade = ObjectAnimator.ofFloat(child, View.ALPHA, if (fromFab) 1f else 0f)
                if (fromFab) {
                    child.alpha = 0f
                }
//                fade.duration = animatorDuration
                fade.interpolator = AnimUtils.getFastOutSlowInInterpolator(context)
                fadeContents.add(fade)
            }
        }
        return fadeContents
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

    private fun makeTranslateAnimator(context: Context, startBounds: Rect, endBounds: Rect, fromFab: Boolean, endView: View): ObjectAnimator? {
        val translationX = startBounds.centerX() - endBounds.centerX()
        val translationY = startBounds.centerY() - endBounds.centerY()
        if (fromFab) {
            endView.translationX = translationX.toFloat()
            endView.translationY = translationY.toFloat()
        }
        val translateAnimator = ObjectAnimator.ofFloat(
                endView,
                View.TRANSLATION_X,
                View.TRANSLATION_Y,
                if (fromFab) pathMotion.getPath(translationX.toFloat(), translationY.toFloat(), 0.0F, 0.0F)
                else pathMotion.getPath(0.0f, 0.0f, -translationX.toFloat(), -translationY.toFloat())
        )
//        translateAnimator.duration = animatorDuration
        translateAnimator.interpolator = AnimUtils.getFastOutSlowInInterpolator(context);

        return translateAnimator
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