@file:JvmName("DependencyTreeDiff")

package com.jakewharton.gradle.dependencies

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(vararg args: String) {
	val help = "-h" in args || "--help" in args
	if (help || args.size != 2) {
		System.err.println("Usage: dependency-tree-diff old.txt new.txt")
		if (!help) {
			exitProcess(1)
		}
		return
	}

	val old = args[0].let(Paths::get).readText()
	val new = args[1].let(Paths::get).readText()

	print(dependencyTreeDiff(old, new))
}

// Don't use Path.readText from stdlib because it introduces few classes, which will increase the binary size a bit.
@Suppress("NOTHING_TO_INLINE")
private inline fun Path.readText(charset: Charset = StandardCharsets.UTF_8): String {
	return Files.readAllBytes(this).toString(charset)
}
