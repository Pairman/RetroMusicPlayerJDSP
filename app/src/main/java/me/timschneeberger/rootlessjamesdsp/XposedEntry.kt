package me.timschneeberger.rootlessjamesdsp

import android.media.AudioTrack
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedEntry : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "code.name.monkey.retromusic") return

        XposedBridge.log("JamesDSP: Hooking RetroMusicPlayer")

        XposedHelpers.findAndHookMethod(
            AudioTrack::class.java,
            "write",
            ByteArray::class.java,
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val data = param.args[0] as ByteArray
                    val offset = param.args[1] as Int
                    val size = param.args[2] as Int

                    // 示例 DSP 处理逻辑：简单变反相
                    for (i in offset until offset + size) {
                        data[i] = (data[i].toInt() xor 0xFF).toByte()
                    }

                    XposedBridge.log("JamesDSP: Processed audio data")
                    // 或者调用你的 JamesDspLocalEngine 进行处理：
                    // val processed = JamesDspLocalEngine().process(data.copyOfRange(offset, offset+size), 48000, 2)
                    // System.arraycopy(processed, 0, data, offset, size)
                }
            }
        )
    }
}
