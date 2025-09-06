# Menu Lib By Xernas78

Hello, this is Menu Lib, a Java library to create Minecraft menus in a plugin using Inventory GUIs !
Menu Lib is a library that aims to be very simple to use, fast and powerful.

I recently made a wiki, [go check it out](https://github.com/Xernas78/menu-lib/wiki)

## Integration with Maven

Add this to your `pom.xml`:
```xml
<repositories>
  <!-- other repositories -->
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
  <!-- other repositories -->
</repositories>

<dependencies>
  <!-- other dependencies -->
  <dependency>
    <groupId>com.github.Xernas78</groupId>
    <artifactId>menu-lib</artifactId>
    <version>1.3.6</version>
  </dependency>
  <!-- other dependencies -->
</dependencies>
```
then reload your Maven project or run `mvn install` command.

## Integration with Gradle:

Add this to your `build.gradle`:

```groovy
repositories {
  maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Xernas78:menu-lib:1.3.6'
}
```
then reload your Gradle project.
