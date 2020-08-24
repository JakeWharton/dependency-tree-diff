-dontobfuscate
-allowaccessmodification
-keepattributes SourceFile, LineNumberTable

-keep class com.jakewharton.gradle.dependencies.DependencyTreeDiff {
	public static void main(java.lang.String[]);
}
