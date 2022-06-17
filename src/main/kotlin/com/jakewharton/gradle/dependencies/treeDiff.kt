@file:JvmName("DependencyTrees")

package com.jakewharton.gradle.dependencies

import java.util.ArrayDeque
import java.util.regex.Pattern

@JvmName("diff")
fun dependencyTreeDiff(old: String, new: String): String {
	val olds = dependencies(old)
	val news = dependencies(new)

	val addedConfigurations = news.keys - olds.keys
	val removedConfigurations = olds.keys - news.keys
	val matchingConfigurations = news.keys intersect olds.keys

	val added = addedConfigurations.associateWith { dependencyTreeDiff(emptyList(), news.getValue(it)) }
	val removed = removedConfigurations.associateWith { dependencyTreeDiff(olds.getValue(it), emptyList()) }
	val modified = matchingConfigurations.associateWith { dependencyTreeDiff(olds.getValue(it), news.getValue(it)) }

	val configurationDiffs = (added + modified + removed)
		.entries
		// Ignore configurations that didn't change at all.
		.filter { it.value != "" }

	return if (configurationDiffs.size == 1) {
		configurationDiffs.single().value
	} else {
		configurationDiffs
			.joinToString(separator = "\n") { (configuration, diff) ->
				"${configuration}\n${diff}"
			}
	}
}

private enum class DependencyScanState {
	LOOKING,
	SCANNING,
}

private val newlineRegex = Pattern.compile("(\\r\\n|\\n|\\r)")

private fun dependencies(text: String): Map<String, List<String>> {
	val configurations = mutableMapOf<String, List<String>>()
	var state = DependencyScanState.LOOKING
	var configuration = "String to Satisfy Kotlin Compiler"
	val dependencies = mutableListOf<String>()
	newlineRegex
		.split(text, -1)
		.fold("<unknown configuration>") { prev, curr ->
			when (state) {
				DependencyScanState.LOOKING -> {
					if (curr.startsWith("+--- ") || curr.startsWith("\\---")) {
						// Found first line of dependencies, save configuration and collect all of them.
						configuration = prev
						dependencies.add(curr)
						state = DependencyScanState.SCANNING
					} else {
						// Continue `reduce`, skipping over unknown lines.
					}
				}

				DependencyScanState.SCANNING -> {
					if (curr.isEmpty()) {
						if (configurations.putIfAbsent(configuration, dependencies.toList()) != null) {
							error("Unsupported input: multiple unknown configurations")
						}
						dependencies.clear()
						state = DependencyScanState.LOOKING
					} else {
						dependencies.add(curr)
					}
				}
			}
			curr
		}
	return configurations
}

private fun dependencyTreeDiff(oldLines: List<String>, newLines: List<String>): String {
	val oldPaths = findDependencyPaths(oldLines)
	val newPaths = findDependencyPaths(newLines)

	val removedTree = buildTree(oldPaths - newPaths)
	val addedTree = buildTree(newPaths - oldPaths)

	return buildString {
		appendDiff(removedTree, addedTree, "")
	}
}

private fun findDependencyPaths(dependencyLines: List<String>): Set<List<String>> {
	val dependencyPaths = mutableSetOf<List<String>>()
	val stack = ArrayDeque<String>()
	for (dependencyLine in dependencyLines) {
		val coordinateStart = dependencyLine.indexOf("--- ")
		check(coordinateStart > 0) {
			"Unable to find coordinate delimiter: $dependencyLine"
		}
		val coordinates = dependencyLine.substring(coordinateStart + 4)

		val coordinateDepth = coordinateStart / 5
		if (stack.size > coordinateDepth) {
			// The stack is too large. Save the current branch and pop off leaves to match depth.
			dependencyPaths += stack.toList()
			for (i in coordinateDepth until stack.size) {
				stack.removeLast()
			}
		}

		stack.addLast(coordinates)
	}

	// The loop only commits a path when it sees the following dependency. Don't forget the last one!
	dependencyPaths += stack.toList()

	return dependencyPaths
}

private data class Node(
	val coordinate: String,
	val versionInfo: String,
	val children: MutableList<Node>,
) {
	override fun toString() = "$coordinate:$versionInfo"
}

private fun buildTree(paths: Iterable<List<String>>): List<Node> {
	val rootNodes = mutableListOf<Node>()
	for (path in paths) {
		var nodes = rootNodes
		for (node in path) {
			val coordinate = node.substringBeforeLast(':')
			val versionInfo = node.substringAfterLast(':')

			val foundNode =
				nodes.singleOrNull { it.coordinate == coordinate && it.versionInfo == versionInfo }
			nodes = if (foundNode != null) {
				foundNode.children
			} else {
				val newNode = Node(coordinate, versionInfo, mutableListOf())
				nodes.add(newNode)
				newNode.children
			}
		}
	}
	return rootNodes
}

private fun StringBuilder.appendDiff(
	oldTree: List<Node>,
	newTree: List<Node>,
	indent: String,
) {
	var oldIndex = 0
	var newIndex = 0
	while (oldIndex < oldTree.size && newIndex < newTree.size) {
		val oldNode = oldTree[oldIndex]
		val newNode = newTree[newIndex]
		when {
			oldNode.coordinate == newNode.coordinate -> {
				if (oldNode.versionInfo == newNode.versionInfo) {
					val last = oldIndex == oldTree.lastIndex && newIndex == oldTree.lastIndex
					val nextIndent = appendNode(' ', indent, oldNode, last)
					appendDiff(oldNode.children, newNode.children, nextIndent)
				} else {
					// Optimization for when transitive dependencies have not changed. We only display
					// the subtree when it contains changes.
					val childrenChanged = oldNode.children != newNode.children

					val nextIndent = appendNode('-', indent, oldNode, oldIndex == oldTree.lastIndex)
					if (childrenChanged) {
						appendDiff(oldNode.children, emptyList(), nextIndent)
					}
					appendNode('+', indent, newNode, newIndex == newTree.lastIndex)
					if (childrenChanged) {
						appendDiff(emptyList(), newNode.children, nextIndent)
					}
				}
				oldIndex++
				newIndex++
			}
			oldNode.coordinate < newNode.coordinate -> {
				appendRemoved(oldNode, indent, oldIndex == oldTree.lastIndex)
				oldIndex++
			}
			oldNode.coordinate > newNode.coordinate -> {
				appendAdded(newNode, indent, newIndex == newTree.lastIndex)
				newIndex++
			}
		}
	}
	for (i in oldIndex until oldTree.size) {
		appendRemoved(oldTree[i], indent, i == oldTree.lastIndex)
	}
	for (i in newIndex until newTree.size) {
		appendAdded(newTree[i], indent, i == newTree.lastIndex)
	}
}

private fun StringBuilder.appendNode(
	diffChar: Char,
	indent: String,
	item: Any?,
	last: Boolean,
): String {
	append(diffChar)
	append(indent)
	append(if (last) '\\' else '+')
	append("--- ")
	append(item)
	append('\n')
	val carryChar = if (last) ' ' else '|'
	return "$indent$carryChar    "
}

private fun StringBuilder.appendAdded(
	node: Node,
	indent: String,
	last: Boolean,
) {
	val nextIndent = appendNode('+', indent, node, last)
	appendDiff(emptyList(), node.children, nextIndent)
}

private fun StringBuilder.appendRemoved(
	node: Node,
	indent: String,
	last: Boolean,
) {
	val nextIndent = appendNode('-', indent, node, last)
	appendDiff(node.children, emptyList(), nextIndent)
}
