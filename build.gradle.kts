plugins {
    id("com.utopia-rise.godot-kotlin-jvm") version "0.3.3-3.4.2"
}

repositories {
    mavenCentral()
}

godot {
    isAndroidExportEnabled.set(false)
    d8ToolPath.set(File("${System.getenv("ANDROID_SDK_ROOT")}/build-tools/31.0.0/d8"))
    androidCompileSdkDir.set(File("${System.getenv("ANDROID_SDK_ROOT")}/platforms/android-30"))
}
