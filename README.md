# WYMIWYG Commons Core

Some utility classes. For example:

## ArgumentHandler

Allows to handle command line arguments by annotating an interface, e.g.

```java
import org.wymiwyg.commons.util.arguments.CommandLine;

public interface MyAppArgs {
    
    @CommandLine (
        longName ="token",
        shortName = "T", 
        required = true,
        description = "The API-Token to access Service"
    )
    public String token();

    @CommandLine (
        longName ="supressExtensions",
        shortName = "S",
        required = false,
        defaultValue = "true",
        description = "Supress the file extensions"
    )
}
```

And getting an instance implementing that interface with: 


```java
import org.wymiwyg.commons.util.arguments.ArgumentHandler;

[...]

public static void main(String[] args) throws Exception {
    MyAppArgs arguments = ArgumentHandler.readArguments(MyAppArgs.class, args);
    [...]
}
```