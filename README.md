# Fluid
Fluid is a scaffolding tool inspired by [Yeoman](https://yeoman.io/).

## Development
### Working with Jars
Depending on the module that you are working in, you may have to work with JARs. Here are some of the commands that can help with the workflow.

**1. List the table of contents of a jar file.**
```bash
$ jar tf my-jar.jar
```

**Output**
```
META-INF/
META-INF/MANIFEST.MF
com/
com/example/
com/example/generator/
com/example/generator/LibraryProjectConfig.class
com/example/generator/LibraryProjectGenerator.class
strawberry.png
```

**2. List the table of contents with additional information.**
```bash
$ jar tfv my-jar.jar
```

**Output**
```
     0 Sat Apr 11 11:08:38 IST 2020 META-INF/
   332 Sat Apr 11 11:08:38 IST 2020 META-INF/MANIFEST.MF
     0 Sat Apr 11 11:08:38 IST 2020 com/
     0 Sat Apr 11 11:08:38 IST 2020 com/example/
     0 Sat Apr 11 11:08:38 IST 2020 com/example/generator/
  2680 Sat Apr 11 11:08:38 IST 2020 com/example/generator/LibraryProjectConfig.class
  1411 Sat Apr 11 11:08:38 IST 2020 com/example/generator/LibraryProjectGenerator.class
 24246 Sat Apr 11 11:08:38 IST 2020 strawberry.png
```

**3. Extract one or more file(s) from the jar.**
```bash
$ jar xf my-jar.jar META-INF/MANIFEST.MF 
```

### Debugging
Working with snapshots during debugging could be tricky. You can make it easier by creating an IntelliJ Java Type Renderer. The screenshots below give you information on how to create and use your type renderer.

![alt text](docs/images/snapshot-type-renderer.png "type renderer")

Working with snapshots during debugging could be tricky. You can make it easier by creating an IntelliJ Java Type Renderer. The screenshots below give you information on how to create and use your type renderer. 

![alt text](docs/images/snapshot-debug-view.png "debug run")

### Attribution
[Strawberry](core/src/test/resources/strawberry.png) by Luis Prado from the Noun Project

```
© Copyright 2020 Red Green Engineering
```
