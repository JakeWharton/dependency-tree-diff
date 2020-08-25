package com.jakewharton.gradle.dependencies

import org.junit.Test

class MainTest {
	@Test fun helpDoesNotExitProcess() {
		// Bare flag.
		main("-h")
		main("--help")

		// Flag with valid arguments.
		main("-h", "old.txt", "new.txt")
		main("--help", "old.txt", "new.txt")
		main("old.txt", "-h", "new.txt")
		main("old.txt", "--help", "new.txt")
		main("old.txt", "new.txt", "-h")
		main("old.txt", "new.txt", "--help")

		// Flag with too few arguments.
		main("-h", "old.txt")
		main("--help", "old.txt")
		main("old.txt", "-h")
		main("old.txt", "--help")

		// Flag with too many arguments.
		main("-h", "old.txt", "new.txt", "wat.txt")
		main("--help", "old.txt", "new.txt", "wat.txt")
		main("old.txt", "-h", "new.txt", "wat.txt")
		main("old.txt", "--help", "new.txt", "wat.txt")
		main("old.txt", "new.txt", "-h", "wat.txt")
		main("old.txt", "new.txt", "--help", "wat.txt")
		main("old.txt", "new.txt", "wat.txt", "-h")
		main("old.txt", "new.txt", "wat.txt", "--help")
	}
}
