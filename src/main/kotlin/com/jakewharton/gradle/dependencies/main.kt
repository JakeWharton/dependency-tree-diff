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
	val diffCommand = "diff"
	val flattenCommand = "flatten"

	when {
		help -> printHelp(isWrongInput = false)
		args.size < 2 -> printHelp(isWrongInput = true)
		args.firstOrNull() == diffCommand -> {
			val flatten = args[1] in setOf("-f", "--flatten")
			val argsOffset = if (flatten) 2 else 1
			if (argsOffset + 2 != args.size) {
				printHelp(isWrongInput = true)
			}
			val old = args[argsOffset].let(Paths::get).readText()
			val new = args[argsOffset + 1].let(Paths::get).readText()
			print(
				if (flatten) {
					dependencyFlatChanges(old, new)
				} else {
					dependencyTreeDiff(old, new)
				}
			)
		}
		args.firstOrNull() == flattenCommand -> {
			if (args.size != 2) {
				printHelp(isWrongInput = true)
			}
			val dependencies = args[1].let(Paths::get).readText()
			print(flatDependencies(dependencies))
		}
	}
}

private inline fun printHelp(isWrongInput: Boolean) {
	val usage = "Usage: dependency-tree-diff <command> [<args>]\n" +
		"Commands:\n" +
		"    diff [-f | --flatten] old.txt new.txt - print diff for 2 dependency files.\n" +
		"        -f flag generates list of changed, removed, and added dependencies.\n" +
		"\n" +
		"    flatten deps.txt - transform dependency tree to flat list of libraries."
	System.err.println(usage)
	if (isWrongInput) {
		exitProcess(1)
	}
	return
}

// TODO replace with https://youtrack.jetbrains.com/issue/KT-19192
@Suppress("NOTHING_TO_INLINE")
private inline fun Path.readText(charset: Charset = StandardCharsets.UTF_8): String {
	return Files.readAllBytes(this).toString(charset)
}
