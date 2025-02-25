package com.cryo.compiler

import com.cryo.annotation.Component
import com.google.auto.service.AutoService
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.util.Collections
import java.util.Scanner
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.StandardLocation


@AutoService(Processor::class)
class ComponentCompiler : AbstractProcessor() {
    private var processed = false
    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return Collections.singleton(Component::class.java.canonicalName)
    }

    override fun getSupportedSourceVersion(): SourceVersion? {
        return SourceVersion.latestSupported()
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        if(processed) return false
        processed = true
        val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
        var file = File(kaptKotlinGeneratedDir)
        while(true) {
            if(file.endsWith("build")) {
                file = file.parentFile
                break
            }
            file = file.parentFile
        }
        val jsonFile = File(file, "src/main/assets/plugin.conf")
        if(!jsonFile.parentFile.exists()) {
            jsonFile.parentFile.mkdirs()
        }
        if(!jsonFile.exists()) {
            jsonFile.createNewFile()
        }

        // 开始写入
        val pluginId = file.name
        println("###pluginId=$pluginId")
        val out = PrintWriter(FileOutputStream(jsonFile))
        val componentClasses = roundEnv?.getElementsAnnotatedWith(Component::class.java)
        if(componentClasses != null) {
            val str = StringBuffer("{\n").append("   pluginId:\"$pluginId\"").append("\n").append("   mappings: {")
            val iterator = componentClasses.iterator()
            while(iterator.hasNext()) {
                val item = iterator.next()
                val component = item.getAnnotation(Component::class.java)
                str.append("\n      ").append(component.name).append(":")
                .append(" {").append("\n")
                    .append("         class:").append("\"").append(item).append("\"")
                    .also {
                        if(component.path != "#") {
                            it.append("\n         path:").append("\"").append(component.path).append("\"")
                        }
                    }
                    .also {
                        if(component.isMain) {
                            it.append("\n         isMain:").append(true)
                        }
                    }
                    .append(
                        when(component.extras.isEmpty()) {
                            true -> ""
                            false -> "\n         args: {".plus(
                                component.extras.joinToString {
                                    "\n            ".plus(it)
                                }
                            ).plus("\n         }")
                        }
                    )
                    .append(
                        when(component.theme != "#") {
                            true -> "\n         theme:".plus("\"").plus(component.theme).plus("\"")
                            false -> ""
                        }
                    )
                .append("\n").append("      }")
            }
            str.append("\n   }").append("\n}")
            println("##### ${str}")
            out.write(str.toString())
        }
        out.flush()
        out.close()
        return true
    }
}