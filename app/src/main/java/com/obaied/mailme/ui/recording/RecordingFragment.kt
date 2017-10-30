package com.obaied.mailme.ui.recording

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.obaied.mailme.R
import com.obaied.mailme.data.local.PrefManager
import com.obaied.mailme.ui.anims.ActivityCircularTransform
import com.obaied.mailme.ui.base.BaseActivity
import com.obaied.mailme.ui.base.BasePermissionsFragment
import com.obaied.mailme.ui.recording_service.RecordingService_ClientController
import com.obaied.mailme.util.d
import kotlinx.android.synthetic.main.activity_recording_new.*
import kotlinx.android.synthetic.main.fragment_recording_new.*
import javax.inject.Inject

/**
 * Created by ab on 10.10.17.
 */
class RecordingFragment :
        BasePermissionsFragment(),
        RecordingMvpView,
        RecordingService_ClientController.ControllerListener {
    @Inject lateinit var presenter: RecordingPresenter
    @Inject lateinit var prefManager: PrefManager

    private var fragmentListener: RecordingFragmentListener? = null
    private val recordingServiceClientController =
            RecordingService_ClientController(this)

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (activity is RecordingFragmentListener) {
            fragmentListener = activity as RecordingFragmentListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ActivityCircularTransform.setup(activity, fragment_container)

        return inflater?.inflate(R.layout.fragment_recording_new, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resetUi()

        presenter.attachView(this)
    }

    override fun onStart() {
        super.onStart()

        recordingServiceClientController.onStart()
    }

    override fun onStop() {
        super.onStop()

        recordingServiceClientController.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun onDetach() {
        super.onDetach()
        fragmentListener = null
    }

    override fun showErrorMessage(message: String) {
        toastLog(message)
    }

    override fun fromClientController_OnRecordingStarted() {
        d { "fromClientController_OnRecordingStarted" }
        btn_stop_recording.text = "Stop Recording"
    }

    override fun fromClientController_OnRecordingStopped() {
        d { "fromClientController_OnRecordingStopped" }
        // Show the user with a pop-up to save or discard the recording
        val noteHint = getTempRecordingPath()
                .split("/")
                .last()
                .removeSuffix(BaseActivity.THREEGP)

        SaveRecordingDialog(activity, noteHint, object : SaveRecordingDialog.onClick {
            override fun onClick_discard() {
                val tempPath = getTempRecordingPath()

                presenter.clearTempRecording(tempPath)
            }

            override fun onClick_save(noteName: String) {
                var to = BaseActivity.getVoiceNotesDir() + "/$noteName"

                // If "to" does not have an extension, add an extension
                if (!to.endsWith(BaseActivity.THREEGP))
                    to += BaseActivity.THREEGP

                val from = getTempRecordingPath()
                d { "to [$to] || from [$from]" }

                presenter.renameLastRecording(to, from)
            }
        }).show()
    }

    override fun tempRecordingCleared() {
        toastLog("File discarded")
        resetUi()
        activity.finishAfterTransition()
    }

    override fun onRecordingRenamed() {
        toastLog("File saved")
        resetUi()
        activity.finishAfterTransition()
    }

    private fun resetUi() {
        btn_stop_recording.let {
            if (recordingServiceClientController.isServiceRunning()) {
                it.text = getString(R.string.btn_stop_recording)
            } else {
                it.text = getString(R.string.btn_start_recording)
            }

            it.setOnClickListener {
                if (!didGrantPermissions()) {
                    return@setOnClickListener
                }

                recordingServiceClientController.toggleRecording(activity,
                        getTempRecordingPath())
            }
        }
    }

    private fun getTempRecordingPath(): String =
            prefManager.read(activity, getString(R.string.preference_temp_recording_path))

    interface RecordingFragmentListener
}