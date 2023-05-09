@file:Suppress("UnstableApiUsage")

include(":inputfield", ":sample")
rootProject.name = "Advanced InputField"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        maven("https://repo1.maven.org/maven2/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")

        maven(url = uri("https://clojars.org/repo/"))
        maven(url = uri("https://jitpack.io"))
        maven(url = uri("https://oss.sonatype.org/content/repositories/snapshots/"))
        maven(url = uri("https://oss.sonatype.org/content/repositories/releases/"))
        maven(url = uri("https://oss.jfrog.org/libs-snapshot/"))
        maven(url = uri("https://oss.jfrog.org/artifactory/oss-snapshot-local/"))
        maven(url = uri("https://repo1.maven.org/maven2/"))
    }
}
