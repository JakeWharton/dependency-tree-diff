@file:JvmName("DependencyTreeDiff")

package com.jakewharton.gradle.dependencies

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(vararg args: String) {
	if (args.size != 2) {
		System.err.println("Usage: dependency-tree-diff old.txt new.txt")
		exitProcess(1)
	}

	val old = args[0].let(Paths::get).readText()
	val new = args[1].let(Paths::get).readText()

	print(dependencyTreeDiff(old, new))
}

// TODO replace with https://youtrack.jetbrains.com/issue/KT-19192
@Suppress("NOTHING_TO_INLINE")
private inline fun Path.readText(charset: Charset = StandardCharsets.UTF_8): String {
	return Files.readAllBytes(this).toString(charset)
}
