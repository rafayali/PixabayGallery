package com.rafay.gallery.common.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * ImageView which keeps 1:1 aspect ratio by width.
 */
class SquaredImageView(
    context: Context,
    attr: AttributeSet?,
    defStyleAtr: Int,
) : AppCompatImageView(context, attr, defStyleAtr) {
    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attr: AttributeSet) : this(
        context,
        attr,
        0,
    )

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(measuredWidth, measuredWidth)
    }
}
