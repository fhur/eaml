# eaml

[![Circle CI](https://circleci.com/gh/fhur/eaml/tree/master.svg?style=svg)](https://circleci.com/gh/fhur/eaml/tree/master)

eaml (pronounced e-mel) is the extended android modeling language. It is
an XML preprocessor that will make your android resource definitions
simple, readable, understandable and will greatly facilitate supporting
several different configurations with a minimal code base.

Read the [release notes](./doc/release_notes.md) to see what features
are implemented in the current release.

## A short introduction

This simple guide will give you a feel of the eaml syntax. For a complete
understanding of the eaml syntax please see the [documentation](./doc/intro.md)
section. (TODO: docs not ready)

Language QuickStart
===================

This short guide will give you a basic understanding of most eaml features.

## Defining simple resources

Simple resources include `color`, `dimen`, `bool`, `integer` and `string`

```
# Let's define some colors
color red: #f00;
color green: #0f0;
color blue: #00f;

# And some margins
dimen small_margins: 4dp;
dimen medium_margins: 8dp;
dimen large_margins: 8dp;

# color pointers
color main_color: red;
```

### Supporting multiple configurations

Eaml simplifies supporting multiple configurations by providing a concise
syntax to describe how resources behave on different configurations.

```
# Example: paddings that behave differently when the device is in landscape
dimen button_paddings {
  default: 4dp;
  land: 8dp;
}
```

## Defining styles

Styles can be defined using the following syntax.
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
```
style BigButton < Button {
  android:paddingTop: 8dp;
  android:paddingBottom: 8dp;
}
```

## Mixins
In cases where you would like to reuse functionality from more than one source
you can use mixins. Mixins are for most purposes identical to styles except for
the fact that they cannot inherit from other `mixins` or `styles`.

```
# A simple mixin that just sets the color to red.
mixin redText {
  android:textColor: red;
}

style RedButton < Button {
  redText()
}
```

## Multiple configuration support
Eaml styles also come with built in syntax to support multiple configurations
as follows:

```
# A button that uses a ripple on v21 and a regular drawable by default
style RedButton < Button {
  android:background {
    default: @drawable/btn_red
    v21: @drawable/btn_red_ripple
  }
}
```

#### Feature requests

eaml is still in its infancy and we are very interested in understanding
what problems android devs encounter when writing styles.

Is there a feature that you really need but is not here?
Please create a new issue explaining the feature + use case and
it might end up on the next version :)

## License

Copyright © 2015 fhur

Distributed under the Eclipse Public License either version 1.0.
