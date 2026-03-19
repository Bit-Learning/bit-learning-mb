package github.psicodes.ktxpy.utils

import android.content.Context
import android.os.Build
import timber.log.Timber

object Commands {
    
    fun getBasicCommand(context: Context): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        val pythonBuildDirPath = "$appFileDirPath/files/usr"
        val pythonLibDirPath = "$pythonBuildDirPath/lib"
        val pythonExecPath = "libpython3.so"
        val aliasCommand = "alias python=\"$pythonExecPath\" && alias pip=\"$pythonExecPath -m pip\""
        
        // Diagnostic logging
        Timber.d("=== Architecture Diagnostics ===")
        Timber.d("Device ABI: ${Build.SUPPORTED_ABIS.joinToString()}")
        Timber.d("Primary ABI: ${Build.SUPPORTED_ABIS[0]}")
        Timber.d("App native lib dir: $appLibDirPath")
        Timber.d("Python build dir: $pythonBuildDirPath")
        Timber.d("Python lib dir: $pythonLibDirPath")
        Timber.d("Python exec path: $pythonExecPath")
        
        return "export PYTHONHOME=$pythonBuildDirPath && export LD_LIBRARY_PATH=\"$pythonLibDirPath:\$LD_LIBRARY_PATH\" && $aliasCommand && clear"
    }
    fun getInterpreterCommand(context: Context, filePath: String): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        val pythonBuildDirPath = "$appFileDirPath/files/usr"
        val pythonLibDirPath = "$pythonBuildDirPath/lib"
        val pythonExecPath = "libpython3.so"
        
        // Diagnostic logging
        Timber.d("=== Execute Code Diagnostics ===")
        Timber.d("Device ABI: ${Build.SUPPORTED_ABIS.joinToString()}")
        Timber.d("App native lib dir: $appLibDirPath")
        Timber.d("Python lib dir: $pythonLibDirPath")
        Timber.d("Python exec path: $pythonExecPath")
        Timber.d("File to execute: $filePath")
        
        return "export PYTHONHOME=$pythonBuildDirPath && export LD_LIBRARY_PATH=\"$pythonLibDirPath:\$LD_LIBRARY_PATH\" && clear && $pythonExecPath $filePath && echo '[Enter to Exit]' && read junk && exit"
    }

    fun getPythonShellCommand(context: Context): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        val pythonBuildDirPath = "$appFileDirPath/files/usr"
        val pythonLibDirPath = "$pythonBuildDirPath/lib"
        val pythonExecPath = "libpython3.so"
        
        // Diagnostic logging
        Timber.d("=== Python Shell Diagnostics ===")
        Timber.d("Device ABI: ${Build.SUPPORTED_ABIS.joinToString()}")
        Timber.d("App native lib dir: $appLibDirPath")
        Timber.d("Python exec path: $pythonExecPath")
        
        return "export PYTHONHOME=$pythonBuildDirPath && export LD_LIBRARY_PATH=\"$pythonLibDirPath:\$LD_LIBRARY_PATH\" && clear && $pythonExecPath && echo '[Enter to Exit]' && read junk && exit"
    }
}