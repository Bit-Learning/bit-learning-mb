package github.psicodes.ktxpy.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.activity.enableEdgeToEdge
import androidx.core.content.res.ResourcesCompat
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.termux.terminal.BuildConfig
import com.termux.terminal.TerminalEmulator
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient
import com.termux.view.TerminalRenderer
import com.termux.view.TerminalView
import com.termux.view.TerminalViewClient
import github.psicodes.ktxpy.R
import github.psicodes.ktxpy.ui.theme.KtxPyTheme
import github.psicodes.ktxpy.utils.Commands
import github.psicodes.ktxpy.utils.Keys
import timber.log.Timber
import java.io.File
import java.lang.ref.WeakReference


class TermActivity : ComponentActivity(),TerminalViewClient {
    private lateinit var mTermView : TerminalView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{
            KtxPyTheme {
                TermScreen()
            }
        }
        onBackPressedDispatcher.addCallback(this,true) {
            mTermView.mTermSession.finishIfRunning()
            finish()
        }
    }

    @Composable
    fun TermScreen()
    {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            factory = { context ->
                mTermView = TerminalView(context , null)
                Timber.d("Terminal has been created")
                mTermView.attachSession(getTerminalSession())
                mTermView.setTerminalViewClient(this)
                mTermView.mRenderer= TerminalRenderer(20,ResourcesCompat.getFont(this, R.font.jetbrainsmono_medium)!!)
                val fileName=intent.getStringExtra(Keys.KEY_FILE_PATH)
                if(fileName!=null){
                    val command = fileName.let { Commands.getInterpreterCommand(this, it) }
                    Timber.d("Command is $command")
                    Handler(Looper.getMainLooper()).post {
                        mTermView.mTermSession.write("$command\r")
                    }
                }
                else if (intent.getBooleanExtra(Keys.IS_SHELL_MODE_KEY,false))
                {
                    Handler(Looper.getMainLooper()).post {
                        mTermView.mTermSession.write("${Commands.getPythonShellCommand(this)}\r")
                    }
                }
                else {
                    Handler(Looper.getMainLooper()).post {
                        mTermView.mTermSession.write("${Commands.getBasicCommand(this)}\r")
                    }
                }
                mTermView.setBackgroundColor(this.getColor(R.color.terminal_colour))
                mTermView
            }
        )
    }

    private fun getTerminalSession(): TerminalSession {
        val cwd = filesDir.absolutePath
        var shell = "/bin/sh"
        if (File("/bin/sh").exists().not())
        {
            shell="/system/bin/sh"
        }
        return TerminalSession(
            shell,
            cwd, arrayOf<String>(),
            arrayOf(),
            TerminalEmulator.DEFAULT_TERMINAL_TRANSCRIPT_ROWS,
            getTermSessionClient()
        )
    }
    override fun logError(tag: String?, message: String?) {
        message?.let { Timber.tag(tag ?: "TermActivity").e(it) }
    }

    override fun logWarn(tag: String?, message: String?) {
        message?.let { Timber.tag(tag ?: "TermActivity").w(it) }
    }

    override fun logInfo(tag: String?, message: String?) {
        message?.let { Timber.tag(tag ?: "TermActivity").i(it) }
    }

    override fun logDebug(tag: String?, message: String?) {
        message?.let { Timber.tag(tag ?: "TermActivity").d(it) }
    }

    override fun logVerbose(tag: String?, message: String?) {
        message?.let { Timber.tag(tag ?: "TermActivity").v(it) }
    }

    override fun logStackTraceWithMessage(
        tag: String?,
        message: String?,
        e: Exception?
    ) {
        e?.let { Timber.tag(tag ?: "TermActivity").e(it, message ?: "") }
    }

    override fun logStackTrace(tag: String?, e: Exception?) {
        e?.let { Timber.tag(tag ?: "TermActivity").e(it) }
    }
    override fun onScale(scale: Float): Float {
        return scale
    }

    override fun onSingleTapUp(e: MotionEvent?) {
        if (mTermView.mTermSession.isRunning) {
            mTermView.requestFocus()
            KeyboardUtils.showSoftInput(mTermView)
        }
    }

    override fun shouldBackButtonBeMappedToEscape(): Boolean { return false }

    override fun shouldEnforceCharBasedInput(): Boolean {
        return true
    }

    override fun shouldUseCtrlSpaceWorkaround(): Boolean {
        return false
    }

    override fun isTerminalViewSelected(): Boolean {
        return true
    }

    override fun copyModeChanged(copyMode: Boolean) { }

    override fun onKeyDown(keyCode: Int, e: KeyEvent?, session: TerminalSession?): Boolean {
        return false
    }

    override fun onKeyUp(keyCode: Int, e: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mTermView.mTermSession.isRunning) {
                mTermView.mTermSession.finishIfRunning()
                finish()
            }
            return true
        }
        return false
    }

    override fun onLongPress(event: MotionEvent?): Boolean {
        return false
    }

    override fun readControlKey(): Boolean {
        return false
    }

    override fun readAltKey(): Boolean {
        return false
    }

    override fun readShiftKey(): Boolean {
        return false
    }

    override fun readFnKey(): Boolean {
        return false
    }

    override fun onCodePoint(
        codePoint: Int,
        ctrlDown: Boolean,
        session: TerminalSession?
    ): Boolean {
        return false
    }
    private fun getTermSessionClient() : TerminalSessionClient
    {
        val weakActivityReference = WeakReference(this)
        return object : TerminalSessionClient {
            override fun onTextChanged(changedSession: TerminalSession) {
                runOnUiThread {
                    weakActivityReference.get()?.mTermView?.onScreenUpdated()
                }
            }

            override fun onTitleChanged(updatedSession: TerminalSession) {  }

            override fun onSessionFinished(finishedSession: TerminalSession) {
                runOnUiThread{
                    weakActivityReference.get()?.mTermView?.let {
                        KeyboardUtils.hideSoftInput(it)
                        it.mTermSession?.finishIfRunning()
                    }
                    finish()
                }
            }

            override fun onCopyTextToClipboard(session: TerminalSession, text: String?) {
                ClipboardUtils.copyText(text)
            }

            override fun onPasteTextFromClipboard(session: TerminalSession?) {
                runOnUiThread{
                    val clip = ClipboardUtils.getText().toString()
                    if (clip.trim { it <= ' ' }
                            .isNotEmpty() && weakActivityReference.get()?.mTermView?.mEmulator != null) {
                        weakActivityReference.get()?.mTermView?.mEmulator?.paste(clip)
                    }
                }
            }

            override fun onBell(session: TerminalSession) { }

            override fun onColorsChanged(changedSession: TerminalSession) { }

            override fun onTerminalCursorStateChange(state: Boolean) { }

            override fun getTerminalCursorStyle(): Int {
                return TerminalEmulator.TERMINAL_CURSOR_STYLE_UNDERLINE
            }

            override fun logError(tag: String?, message: String?) {
                message?.let { Timber.tag(tag ?: "TermSession").e(it) }
            }

            override fun logWarn(tag: String?, message: String?) {
                message?.let { Timber.tag(tag ?: "TermSession").w(it) }
            }

            override fun logInfo(tag: String?, message: String?) {
                message?.let { Timber.tag(tag ?: "TermSession").i(it) }
            }

            override fun logDebug(tag: String?, message: String?) {
                message?.let { Timber.tag(tag ?: "TermSession").d(it) }
            }

            override fun logVerbose(tag: String?, message: String?) {
                message?.let { Timber.tag(tag ?: "TermSession").v(it) }
            }

            override fun logStackTraceWithMessage(
                tag: String?,
                message: String?,
                e: Exception?
            ) {
                e?.let { Timber.tag(tag ?: "TermSession").e(it, message ?: "") }
            }

            override fun logStackTrace(tag: String?, e: Exception?) {
                e?.let { Timber.tag(tag ?: "TermSession").e(it) }
            }

        }
    }
    override fun onEmulatorSet() { }
}
/*
H=$PATH:/data/app/~~Hfk1Sq2A4XNtLfCsC9OZSQ==/github.psicodes.ktxpy-oxz5xUgjGGvDM-ewPK-G0A==/lib/arm64 && export PYTHONHOME=/data/user/0/github.psicodes.ktxpy/files/files/usr && /data/app/~~Hfk1Sq2A4XNtLfCsC9OZSQ==/github.psicodes.ktxpy-oxz5xUgjGGvDM-ewPK-G0A==/lib/arm64 && export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:" && export LD_LIBRARY_PATH="$LD_LIBRARY_PATH/data/user/0/github.psicodes.ktxpy/files/files/usr/lib" && clear && libpython3.so /data/user/0/github.psicodes.ktxpy/files/pythonFiles/ok.py && echo '[Enter to Exit]' && read junk && exit
 */