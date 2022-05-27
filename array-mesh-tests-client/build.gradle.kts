plugins {
    id("com.utopia-rise.godot-kotlin-jvm") version "0.3.3-3.4.2"
}

repositories {
    mavenCentral()
}

dependencies {
  implementation("org.apache.commons:commons-lang3:3.12.0")
}

godot {
    isAndroidExportEnabled.set(false)
    d8ToolPath.set(File("${System.getenv("ANDROID_SDK_ROOT")}/build-tools/31.0.0/d8"))
    androidCompileSdkDir.set(File("${System.getenv("ANDROID_SDK_ROOT")}/platforms/android-30"))
}

tasks.build {
  doLast {
    projectDir.resolve("build").resolve(".gdignore").createNewFile()
  }
}
