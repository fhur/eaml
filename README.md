# eaml

[![Circle CI](https://circleci.com/gh/fhur/eaml/tree/master.svg?style=svg)](https://circleci.com/gh/fhur/eaml/tree/master)

eaml (pronounced e-mel) is the extended android modeling language. It is
an XML preprocessor that will make your android resource definitions
simple, readable, understandable and will greatly facilitate supporting
several different configurations with a minimal code base.

Read the [release notes](./doc/release_notes.md) to see what features
are implemented in the current release.

Language QuickStart
===================

This short guide will give you a basic understanding of most eaml features.

## Introduction

Eaml is a simple language for defining styles in Android. Eaml is targeted at android
developers who already understand the Android styling framework but are having trouble
organizing and maintaining their styles as the project grows.

Eaml's most interesting features include:
- Support for nested directories. You can nest your `.eaml` files as deep as possible.
- Support for `mixin`s. See the mixin section for more information.
- Configuration selectors. Using the `&:` syntax you can specify how a style varies its
  behaviour depending on the device's configuration.

*NOTE:* A good understanding of how Android styles work is required in order to use Eaml
proficiently as Eaml transpiles to Android's XML styles.

## Defining simple resources

Eaml provides a simplified syntax for defining simple resources.
Simple resources include `color`, `dimen`, `bool`, `integer` and `string`

### Syntax
`<resource type> <identifier>: <value>;`

Example:
```
# Let's define some colors
color red: #f00;
color green: #0f0;
color blue: #00f;

# And some dimen
dimen small_margins: 4dp;
dimen medium_margins: 8dp;
dimen large_margins: 8dp;
```

### Resource Pointers

When a resource reference another resource, this reference is called a resource pointer.

Eaml provides two types of pointers:

- Native resource pointers e.g. `@color/red`. Use these type of pointers when referencing a resource
  defined in an xml file. This serves as a way of interop between existing xml styles and Eaml programs.
  All form of native resource pointers are supported including `@null`.
- Eaml resource pointers e.g. `color main_color: red;`. Use these pointers when referencing a resource
  defined in Eaml.

Example:
```
color a_cool_color: #f00;
color some_other_color: @android:color/a_color;
color my_favorite_color: a_cool_color;
```

### Supporting multiple configurations

Eaml simplifies supporting multiple configurations by providing a concise
syntax to describe how resources behave on different configurations.

#### Syntax:

```
<resource type> <identifier> {
  <configuration>: <value>;
  <configuration>: <value>;
  ...
}
```

Example:
```
# Paddings that behave differently when the device is in landscape
dimen button_paddings {
  default: 4dp;
  land: 8dp;
}
```

## Defining styles

Styles can be defined using the following syntax.
```
style <identifier> (< <optional parent>)? {
  <attribute>*
}
```

Example:
```
style Button {
  android:paddingLeft: 8dp;
  android:paddingRight: 8dp;
  android:paddingTop: 4dp;
  android:paddingBottom: 4dp;
  android:textSize: 12sp;
}
```

## Inheritance
Eaml supports single inheritance via the `<` keyword. This is simply syntax sugar
that piggy backs on android's `parent` attribute.

Example:
```
style BigButton < Button {
  android:paddingTop: 8dp;
  android:paddingBottom: 8dp;
}
```

## Multiple configuration support
Styles also come with built in syntax to support multiple configurations
as follows:

```
# A button that uses a ripple on v21 and a regular drawable by default
style RedButton < Button {
  android:textColor: red;
  android:background: @drawable/btn_red;
  &:v21 {
    android:background: @drawable/btn_red_ripple;
  }
}
```

## Mixins
In cases where you would like to reuse functionality from more than one source
you can use mixins. Mixins are for most purposes identical to styles except for
the fact that they cannot inherit from other `mixins` or `styles`.

```
# A simple mixin that just sets the text color to red.
mixin redText {
  android:textColor: red;
}

style RedButton < Button {
  redText()
}
```

Mixins can define behaviour for multiple configurations as follows:

```
mixin redText {
  android:textColor: red;
  &:v21 {
    android:textColor: fancy_red;
  }
}

style RedButton < Button {
  redText();
}
```

*NOTE*: you cannot invoke a mixin from inside a configuration block:
```
style RedButton < Button {
  &:v21 {
    redText(); # THIS WILL THROW AN ERROR!
  }
}
```

#### Android Studio Support

There is an Android Studio being developed in [fhur/eaml-idea](https://github.com/fhur/eaml-idea).
The plugin is still under construction but it will offer easy gradle integration, syntax highlighting,
auto completion and other nice features.

#### Feature requests

eaml is still in its infancy and we are very interested in understanding
what problems android devs encounter when writing styles.

Is there a feature that you really need but is not here?
Please create a new issue explaining the feature + use case and
it might end up on the next version :)

## License

Copyright © 2015 fhur

Distributed under the Eclipse Public License either version 1.0.
