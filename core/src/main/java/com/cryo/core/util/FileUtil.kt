package com.cryo.core.util

import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object FileUtil {
    const val TAG = "FileUtil"
    fun unzipFile(zipPath: File, outputDirectory: File) {
        Log.i(TAG, "开始解压的文件： $zipPath\n解压的目标路径：$outputDirectory")
        var cloneFile = outputDirectory
        if (!cloneFile.exists()) {
            cloneFile.mkdirs()
        }
        val inputStream: InputStream = FileInputStream(zipPath)
        val zipInputStream = ZipInputStream(inputStream)
        var zipEntry: ZipEntry? = zipInputStream.nextEntry
        val buffer = ByteArray(1024 * 1024)
        var count: Int
        while (zipEntry != null) {
            Log.i(TAG, "解压文件 入口 1： $zipEntry")
            var fileName: String = zipEntry.getName()
            Log.i(TAG, "解压文件 原来 文件的位置： $fileName")
            Log.i(TAG, "解压文件 的名字： $fileName")
            cloneFile = File("$outputDirectory${File.separator.toString()}$fileName")
            if (!zipEntry.isDirectory) {
                cloneFile.createNewFile()
                val fileOutputStream = FileOutputStream(cloneFile)
                while (zipInputStream.read(buffer).also { count = it } > 0) {
                    fileOutputStream.write(buffer, 0, count)
                }
                fileOutputStream.close()
            }
            else {
                cloneFile.mkdirs()
            }
            zipEntry = zipInputStream.nextEntry
            Log.i(TAG, "解压文件 入口 2： $zipEntry")
        }
        zipInputStream.close()
        Log.i(TAG, "解压完成")
    }

    /**
     * 删除文件
     * @param file
     */
    fun deleteFile(file: File) {
        if(file.isDirectory) {
            file.listFiles()?.forEach {
                deleteFile(it)
            }
        }
        file.delete()
    }

    private fun copyFileTo(from: File, to: File) {
        val inputStream = from.inputStream()
        val outputStream = to.outputStream()
        inputStream.channel.also {
            it.transferTo(0, it.size(), outputStream.channel)
        }
        inputStream.close()
        outputStream.close()
    }

    fun copyFileTo(from: InputStream, to: File) {
        val outputStream = to.outputStream()
        val bytes = ByteArray(1024)
        var len: Int
        while (from.read(bytes).also { len = it } != -1) {
            outputStream.write(bytes, 0, len)
        }
        from.close()
        outputStream.close()
    }

    fun copy(from: File, to: File) {
        if(from.isFile) {
            copyFileTo(from, to)
        }
        else {
            if(!to.exists()) {
                to.mkdirs()
            }
            from.listFiles()?.forEach { file ->
                val dstFile = File(to, file.name)
                copy(file, dstFile)
            }
        }
    }
}