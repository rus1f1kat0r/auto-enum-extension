# auto-enum-extension

Enums is a rather powerful thing in java. It helps to implement polymorphic way of interaction with limited set of predefined elements, such as ones that describe object type etc.
Lets say we have some POJO - Advertisement. There are multiple predefined advertisement types : banner, popup, fullscreen.

This processor will take names and create extensible enum automatically for you.

## Integration guide

* Add annotation dependency to your project

```
dependencies {
    provided 'io.github.rus1f1kat0r:auto-enum-extension-annotation:0.0.2-SNAPSHOT'
}
```

* Add annotation to empty interface, and define enumeration of values with `@AutoEnum` annotation 

```
@AutoEnum(value = {"first", "second", "third", "fourth"}, name = "Count")
public interface Type {
}
    
```

**Note:** AutoEnum will generate enum and visitor interface in the same package.

* Apply apt plugin (Android project)

```
apply plugin: 'com.neenbedankt.android-apt'
...
buildscript {
    dependencies {
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.4'
    }
    ...
}

```

* Or java project (net.ltgt.apt is just what I have tried)

```
apply plugin: "net.ltgt.apt"

buildscript {
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath "net.ltgt.gradle:gradle-apt-plugin:0.9"
    }
}

```

* Provide annotation processor to your build

```
dependencies {
    apt 'io.github.rus1f1kat0r:auto-enum-extension:0.0.2-SNAPSHOT'
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

# License 

```
Copyright 2017 Kirill Kharkov

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
