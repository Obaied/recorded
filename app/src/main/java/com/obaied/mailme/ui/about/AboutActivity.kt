package com.obaied.mailme.ui.about

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import com.obaied.mailme.R
import com.obaied.mailme.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity :
        BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        text_about.let {
            it.text = (if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Html.fromHtml(getString(R.string.about), Html.FROM_HTML_MODE_LEGACY);
            } else {
                Html.fromHtml(getString(R.string.about))
            })

            it.movementMethod = LinkMovementMethod.getInstance();
        }

        root_layout.let {
            it.setOnClickListener {
                this.finishAfterTransition()
            }
        }
    }
}