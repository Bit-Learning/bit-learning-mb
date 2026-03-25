package github.psicodes.ktxpy.utils

import android.content.Context
import android.os.Build
import timber.log.Timber
import java.io.File

object Commands {
    
    private fun findPythonExecutable(context: Context): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        
        // Try the nativeLibraryDir first
        var pythonExec = File(appLibDirPath, "libpython3.so")
        if (pythonExec.exists() && pythonExec.canExecute()) {
            Timber.d("Found libpython3.so at: ${pythonExec.absolutePath}")
            return pythonExec.absolutePath
        }
        
        // Fallback: search in common ABI directories
        val abiDirs = listOf(
            "arm64-v8a", "armeabi-v7a", "x86_64", "x86", "arm64", "armeabi"
        )
        
        // Check in /data/app/.../lib/[abi]/
        val appDir = File(appLibDirPath).parentFile?.parentFile
        if (appDir != null) {
            for (abi in abiDirs) {
                pythonExec = File(appDir, "lib/$abi/libpython3.so")
                if (pythonExec.exists() && pythonExec.canExecute()) {
                    Timber.d("Found libpython3.so at: ${pythonExec.absolutePath}")
                    return pythonExec.absolutePath
                }
            }
        }
        
        // Last resort: use the path as-is and let it fail with better error
        Timber.e("libpython3.so not found! Tried: $appLibDirPath")
        return "$appLibDirPath/libpython3.so"
    }
    
    fun getBasicCommand(context: Context): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        val pythonBuildDirPath = "$appFileDirPath/files/usr"
        val pythonLibDirPath = "$pythonBuildDirPath/lib"
        val pythonExecPath = findPythonExecutable(context)
        val pythonExecDir = File(pythonExecPath).parent ?: appLibDirPath
        val aliasCommand = "alias python=\"$pythonExecPath\" && alias pip=\"$pythonExecPath -m pip\""
        
        // Diagnostic logging
        Timber.d("=== Architecture Diagnostics ===")
        Timber.d("Device ABI: ${Build.SUPPORTED_ABIS.joinToString()}")
        Timber.d("Primary ABI: ${Build.SUPPORTED_ABIS[0]}")
        Timber.d("App native lib dir: $appLibDirPath")
        Timber.d("Python build dir: $pythonBuildDirPath")
        Timber.d("Python lib dir: $pythonLibDirPath")
        Timber.d("Python exec path: $pythonExecPath")
        Timber.d("Python exec dir: $pythonExecDir")
        
        return "export PYTHONHOME=$pythonBuildDirPath && export LD_LIBRARY_PATH=\"$pythonExecDir:$pythonLibDirPath:\$LD_LIBRARY_PATH\" && $aliasCommand && clear"
    }
    fun getInterpreterCommand(context: Context, filePath: String): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        val pythonBuildDirPath = "$appFileDirPath/files/usr"
        val pythonLibDirPath = "$pythonBuildDirPath/lib"
        val pythonExecPath = findPythonExecutable(context)
        val pythonExecDir = File(pythonExecPath).parent ?: appLibDirPath
        
        // Diagnostic logging
        Timber.d("=== Execute Code Diagnostics ===")
        Timber.d("Device ABI: ${Build.SUPPORTED_ABIS.joinToString()}")
        Timber.d("App native lib dir: $appLibDirPath")
        Timber.d("Python lib dir: $pythonLibDirPath")
        Timber.d("Python exec path: $pythonExecPath")
        Timber.d("Python exec dir: $pythonExecDir")
        Timber.d("File to execute: $filePath")
        
        return "export PYTHONHOME=$pythonBuildDirPath && export LD_LIBRARY_PATH=\"$pythonExecDir:$pythonLibDirPath:\$LD_LIBRARY_PATH\" && clear && $pythonExecPath $filePath && echo '[Enter to Exit]' && read junk && exit"
    }

    fun getPythonShellCommand(context: Context): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        val pythonBuildDirPath = "$appFileDirPath/files/usr"
        val pythonLibDirPath = "$pythonBuildDirPath/lib"
        val pythonExecPath = findPythonExecutable(context)
        val pythonExecDir = File(pythonExecPath).parent ?: appLibDirPath
        
        // Diagnostic logging
        Timber.d("=== Python Shell Diagnostics ===")
        Timber.d("Device ABI: ${Build.SUPPORTED_ABIS.joinToString()}")
        Timber.d("App native lib dir: $appLibDirPath")
        Timber.d("Python exec path: $pythonExecPath")
        Timber.d("Python exec dir: $pythonExecDir")
        
        return "export PYTHONHOME=$pythonBuildDirPath && export LD_LIBRARY_PATH=\"$pythonExecDir:$pythonLibDirPath:\$LD_LIBRARY_PATH\" && clear && $pythonExecPath && echo '[Enter to Exit]' && read junk && exit"
    }
}