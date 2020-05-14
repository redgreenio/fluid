# Fluid DSL Guide

## 1. Quick Start (3-minutes)
We are used to seeing and interacting with files and directories as trees. The Fluid DSL models this idea closely to take advantage of our familiarity.

Consider the following directory structure for a sample **hello-world** Gradle Kotlin Project.

```
hello-world
├── build.gradle
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── README.md
└── src
    ├── main
    │   └── kotlin
    └── test
        └── kotlin
            └── CanaryTest.kt
```

The project has a bunch of files, nested directories, and a file (**gradlew**) with execute permissions. Let's model such a project structure using the Fluid DSL.

```kotlin
scaffold { dslConfig ->
  file("build.gradle")
  copyDir("gradle")
  file("gradlew", EXECUTE)
  file("gradlew.bat")

  template("README.md", ReadmeModel(dslConfig.name, dslConfig.description))

  dir("src") {
    dir("main/kotlin")
    dir("test/kotlin") {
      file("CanaryTest.kt", Source("CanaryTest"))
    }
  }
}
```

The DSL always represents the *destination*. We can create new directories out of thin air, but we can't do the same with files and templates. Files and templates should come from a *source*. This source is the **resources** directory from your generator's main source set. For our example, the resources directory should look like the one shown below.

```
resources
├── build.gradle
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── README.md
└── CanaryTest
```

Most of the mappings from *source* → *DSL* → *destination* in this example are pretty straight-forward. However, two statements stand out.
- `file("gradlew", EXECUTE)` - This call copies the **gradlew** file to the destination with execute permission.

- `file("CanaryTest.kt", Source("CanaryTest"))` - `file`, `template`, `dir`, and `copyDir` directives are hierarchy-aware (i.e.) when nested they know their ancestry. In this case, merely calling `file("CanaryTest.kt")` would require the resources directory to have a file at **src/test/kotlin/CanaryTest.kt**. Instead of creating this hierarchy within the resources directory, we use an overloaded function that takes in a `Source("CanaryTest")` parameter.

**Note** IDEs will show error markers for source files placed inside the resources directory, but this does not affect the project's compilation. To avoid eyesore, drop the file's extension. Notice, the absence of **.kt** extension in **CanaryTest** in this example. This missing extension from the file name is intentional and not a mistake.

## 2. Complete Reference
The Fluid DSL begins with a `scaffold` block.

```kotlin
scaffold {
  // Things that matter go here…
}
```

### 2.1 Creating Directories
The `dir` function call creates one or more directories at the destination. It can accept a directory name, or a path segment.

```kotlin
scaffold {
  dir("src") {
    dir("main") {
      dir("kotlin")
    }
  }
}
```

The snippet above will create the directory path **src/main/kotlin** at the destination.

You can also collapse this into a single `dir` call by using a path segment.

```kotlin
scaffold {
  dir("src/main/kotlin")
}
```

### 2.2 Copying Files
The `file` function copies a file from your generator's **resources** directory to the destination.

```kotlin
scaffold {
  file("LICENSE.txt")
}
```

If the location of the file in your **resources** directory is different, you can use an overloaded function to specify the source path explicitly.

```kotlin
scaffold {
  file("LICENSE.txt", Source("licenses/LICENSE-MIT.txt"))
}
```

Notice that the destination file name can be different from the source.

You can also make a file executable by passing in the `EXECUTE` flag.

```kotlin
scaffold {
  file("deploy.sh", EXECUTE)
}
```

The above snippet will copy **deploy.sh** to the destination directory and make it executable.

### 2.3 Copying Templates
Static files are useful, but templates enable customization and dynamism by using information collected from the user or the environment.

For instance, this example shows how to add the project's name and description to the generated **README.md** file.

```kotlin
scaffold { dslConfig ->
  template("README.md", ReadmeModel(dslConfig.projectName, dslConfig.description))
}
```

The DSL has overloaded functions that allow you to specify the source template or add executable permissions when required.

```kotlin
scaffold { dslConfig ->
  template("greet.sh", dslConfig.maintainer, Source("scripts/greet.sh"), EXECUTE)
}
```

Fluid uses [Apache FreeMarker](https://freemarker.apache.org/) by default for templates.

### 2.4 Copying Directories
Dealing with *static files* one by one can be handy; however, there are times when you may want to copy an entire directory of static files and call it a day. The `copyDir` function comes in handy for those situations.

```kotlin
scaffold {
  copyDir("gradle")
}
```

In this example, the generator will copy the contents of the **gradle** directory from your generator's **resources** directory to the destination.

The `copyDir` function is useful; however, it cannot deal with templates or file permissions. Use the `template` and `permissions` functions to handle these cases appropriately.

### 2.5 Permissions
The `file` and `template` functions allow you to specify file permissions, in situations where you can't use either, use the `permissions` function.

```kotlin
scaffold {
  copyDir("scripts")
  permissions("scripts/run-server.sh", EXECUTE)
  permissions("scripts/stop-server.sh", EXECUTE)
}
```

### 2.6 Putting it together
```kotlin
scaffold { dslConfig ->
  file(".gitignore")
  file(".gitattributes")

  copyDir("gradle")

  dir("src/main/kotlin")
  dir("src/test/kotlin")

  template("README.md", ReadmeModel(dslConfig.name, dslConfig.description))

  file("gradlew", EXECUTE)
}
```

You can group and extract these statements into functions like the good programmer you are and make it look neat and clean.

```kotlin
scaffold { dslConfig ->
  setupGradle()
  addGitConfigurationFiles()
  createSourceDirectories()
  addReadme(dslConfig)
}
```

### 2.7 DSL Summary
Fluid DSL has five directives, and this table sums them up.

| Directive     | Purpose                                                                         | Specify<br />Source | Specify<br />Permissions |
|---------------|---------------------------------------------------------------------------------|---------------------|--------------------------|
| `dir`         | Creates a directory                                                             | -                   | -                        |
| `file`        | Copies a file                                                                   | Yes                 | Yes                      |
| `template`    | Renders and copies a template                                                   | Yes                 | Yes                      |
| `copyDir`     | Recursively copies a directory and its contents (includes empty subdirectories) | Yes                 | -                        |
| `permissions` | Sets permission on a file or template                                           | -                   | Yes                      |

That's all there is about the Fluid DSL.
