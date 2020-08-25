@file:JvmName("DependencyTrees")

package com.jakewharton.gradle.dependencies

import java.util.ArrayDeque

@JvmName("diff")
fun dependencyTreeDiff(old: String, new: String): String {
	val oldPaths = findDependencyPaths(old)
	val newPaths = findDependencyPaths(new)

	val removedTree = buildTree(oldPaths - newPaths)
	val addedTree = buildTree(newPaths - oldPaths)

	return buildString {
		appendDiff(removedTree, addedTree, "")
	}
}

private fun findDependencyPaths(text: String): Set<List<String>> {
	val dependencyLines = text.split('\n')
		.dropWhile { !it.startsWith("+--- ") }
		.takeWhile { it.isNotEmpty() }

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
