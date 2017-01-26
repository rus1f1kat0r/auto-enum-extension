# auto-enum-extension

Enums is a rather powerful thing in java. It helps to implement polymorphic way of interaction with limited set of predefined elements, such as ones that describe object type etc.
Lets say we have some POJO - Advertisement. There are multiple predefined advertisement types : banner, popup, fullscreen.

This processor will take names and create extensible enum automatically for you.

## Integration guide

* Add annotation dependency to your project

```
provided 'ru.rus1f1kat0r:auto-enum-extension-annotation:0.0.1-SNAPSHOT'
```

* Add annotation to empty interface, and define enumeration of values with `@AutoEnum` annotation 

```
@AutoEnum(value = {"first", "second", "third", "fourth"}, name = "Count")
public interface Type {
}
    
```

**Note:** AutoEnum will generate enum and visitor interface in the same package.

* Provide AutoEnum processor for apt (Android project)

```
apply plugin: 'com.neenbedankt.android-apt'

... 

dependencies {
    apt 'ru.rus1f1kat0r:auto-enum-extension:0.0.1-SNAPSHOT'
}

```

* Clean and rebuild your project in order to generate all the stuff, then use generated enum and visitor
* Implement generated visitor

```
    private static class CounterVisitor implements Count.CountVisitor<String, Integer> {
        @Override
        public String first(Integer param) {
            return "" + 1 * param;
        }

        @Override
        public String second(Integer param) {
            return "" + 2 * param;
        }

        @Override
        public String third(Integer param) {
            return "" + 3 * param;
        }

        @Override
        public String fourth(Integer param) {
            return "" + 4 * param;
        }
    }
```

* Use polymorphic accept for extending your generated enum without coupling it with details and without boilerplate code

```

    List<Count> list = getCounts();
    Count.CountVisitor<String, Integer> visitor = new CounterVisitor();
    for (Count each : list) {
        Log.d("VISITOR", each.accept(visitor, 2));
    }
```