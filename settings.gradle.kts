pluginManagement {
    repositories {
        // 依赖使用阿里云 maven 源
        maven {
            setUrl("https://maven.aliyun.com/repository/public/")
        }
        maven {
            setUrl("https://maven.aliyun.com/repository/spring/")
        }
        maven {
            setUrl("https://maven.aliyun.com/repository/google/")
        }
        maven {
            setUrl("https://maven.aliyun.com/repository/gradle-plugin/")
        }
        maven {
            setUrl("https://maven.aliyun.com/repository/grails-core/")
        }
        maven {
            setUrl("https://maven.aliyun.com/repository/apache-snapshots/")
        }
        gradlePluginPortal() // 依然保留，但优先使用阿里云
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        // 依赖使用阿里云 maven 源
//        maven {
//            setUrl("https://maven.aliyun.com/repository/public/")
//        }
//        maven {
//            setUrl("https://maven.aliyun.com/repository/spring/")
//        }
//        maven {
//            setUrl("https://maven.aliyun.com/repository/google/")
//        }
//        maven {
//            setUrl("https://maven.aliyun.com/repository/gradle-plugin/")
//        }
//        maven {
//            setUrl("https://maven.aliyun.com/repository/grails-core/")
//        }
//        maven {
//            setUrl("https://maven.aliyun.com/repository/apache-snapshots/")
//        }
        mavenCentral()
    }
}
rootProject.name = "easy-yapi"
include(":common-api", ":idea-plugin", ":plugin-adapter:plugin-adapter-markdown")

