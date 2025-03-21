pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") } // OpenCV 의존성 해결을 위한 추가
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // OpenCV 관련 라이브러리 다운로드 문제 해결
    }
}

rootProject.name = "maeumgil"
include(":app")
include(":sdk")
