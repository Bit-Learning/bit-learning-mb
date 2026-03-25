package github.psicodes.ktxpy

import android.content.Context
import android.os.Build
import com.google.android.material.color.DynamicColors
import timber.log.Timber
import java.io.File


class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Khởi tạo Timber - chỉ log trong debug build
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        DynamicColors.applyToActivitiesIfAvailable(this)

        PythonRuntimeDebug.dump(this)
    }
}

object PythonRuntimeDebug {

    fun dump(context: Context) {
        val nativeDir = context.applicationInfo.nativeLibraryDir
        val filesDir = context.filesDir.absolutePath

        val native = File(nativeDir)
        val pyLib = File(nativeDir, "libpython3.so")

        val usr1 = File("$filesDir/usr")
        val usr2 = File("$filesDir/files/usr")
        val usr3 = File("$filesDir/files/usr/lib")

        Timber.d("========== PYTHON RUNTIME DEBUG ==========")
        Timber.d("packageName = ${context.packageName}")
        Timber.d("device abi = ${Build.SUPPORTED_ABIS.joinToString()}")
        Timber.d("nativeLibraryDir = $nativeDir")
        Timber.d("filesDir = $filesDir")

        Timber.d("native dir exists = ${native.exists()}")
        Timber.d("native dir files = ${native.list()?.joinToString(", ") ?: "null"}")

        Timber.d("libpython3 exists = ${pyLib.exists()}")
        Timber.d("libpython3 path = ${pyLib.absolutePath}")
        Timber.d("libpython3 canRead = ${pyLib.canRead()}")
        Timber.d("libpython3 canExecute = ${pyLib.canExecute()}")

        Timber.d("usr exists = ${usr1.exists()} path=${usr1.absolutePath}")
        Timber.d("files/usr exists = ${usr2.exists()} path=${usr2.absolutePath}")
        Timber.d("files/usr/lib exists = ${usr3.exists()} path=${usr3.absolutePath}")

        Timber.d("filesDir children = ${File(filesDir).list()?.joinToString(", ") ?: "null"}")
        Timber.d("==========================================")
    }
}