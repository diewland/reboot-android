package com.diewland.rebootandroid

import android.util.Log
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

object SuUtil {

    private const val TAG = "OTA_SU_UTIL"

    fun exec (cmd: String): ArrayList<String?> {
        return exec(arrayListOf(cmd))
    }

    // https://stackoverflow.com/a/11311955/466693
    fun exec (cmds: List<String>): ArrayList<String?> {

        // make sure su is ready
        initAdb()

        // do su process
        val proc = Runtime.getRuntime().exec("su") ?: return arrayListOf(null, null)
        val os = DataOutputStream(proc.outputStream)
        for (cmd in cmds) {
            os.writeBytes(cmd + "\n")
            os.flush()
        }
        os.writeBytes("exit\n")
        os.flush()
        os.close()

        // grab output
        val output = extractOutput(proc)

        // wait until process done
        proc.waitFor()

        // return output
        return output
    }

    //fun exec2 (cmd: String): ArrayList<String?> {
    //    val proc = Runtime.getRuntime().exec(arrayOf("su", "-c", cmd))
    //    proc.waitFor()
    //    return extractOutput(proc)
    //}

    private fun extractOutput (proc: Process): ArrayList<String?> {
        // gather resp
        val stdInput = BufferedReader(InputStreamReader(proc.inputStream))
        val stdError = BufferedReader(InputStreamReader(proc.errorStream))
        var s: String? // null included
        var o: String? = ""
        var e: String? = ""

        // read counter
        var numX = 10 // max
        var numO = 0
        var numE = 0

        while ((stdInput.readLine().also { s = it } != null) && (numO < numX)) { o += s; numO += 1 }
        while ((stdError.readLine().also { s = it } != null) && (numE < numX)) { e += s; numE += 1 }

        // if blank, cast to null
        if (o.isNullOrBlank()) o = null
        if (e.isNullOrBlank()) e = null

        // debug
        Log.d(TAG, "[success $numO] $o")
        Log.d(TAG, "[error   $numE] $e")

        // return output, error
        return arrayListOf(o, e)
    }

    // first time of exec su, adb require initialize
    private fun initAdb () {
        val s = arrayOf("sh", "-c", "echo exit | su")
        val su = Runtime.getRuntime().exec(s)
        extractOutput(su)
        su.waitFor()
        Log.d(TAG, "[INIT_ADB_] su is ready")
    }
}