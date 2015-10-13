# eaml

*WARNING:*
This project is still very alpha. There might be several syntax changes
and most features are not ready.

eaml (pronounced e-mel) is the extended android modeling language. It is
an XML preprocessor that will make your android resource definitions
simple, readable, understandable and will greatly facilitate supporting
several different configurations with a minimal code base.

## A short introduction

This simple guide will give you a feel of the eaml syntax. For a complete
understanding of the eaml syntax please see the [documentation](./doc/intro.md)
section. (TODO: docs not ready)

#### Multiple inheritance support
It is often the case when you want a style class inherit or "share" the
functionality of two previously created styles. Consider the case where
you have a `BigButton` and a `RedButton` and you want to create a
`BigRedButton` which inherits from both styles. You can write this using
eaml as follows:

```
# A style for big buttons. Notice that BigButton
# extends Button
style BigButton < Button {
  android:padding: 8dp
  android:textSize: 24sp
}

# A style for red buttons
style RedButton < Button {
  android:textColor: #F00
}

# A style for buttons that are both red and big
style BigRedButton < BigButton, RedButton {
  android:text: "I am a big red button"
}
```

#### Defining variables

You can easily define different colors, dimens, etc. as follows:

```
# defining colors
color primary_color: #ff0000

# defining dimens
dimen small_margins: 12dp

# Supporting multiple configurations
dimen normal_text {
  default: 12sp
  w820dp: 13sp # make text bigger on 7" devices
}
```

#### Support for multiple configurations

eaml also aids in the managing of multiple configurations with a nice
syntax:

```
style PrimaryButton < Button {
  android:textColor: #fff
  android:textSize: 12sp
  android:background: @drawable/btn_primary
  v21 {
    android:background: @drawable/btn_primary_ripple
  }
}
```

#### Operations

You can apply simple addition, multiplication, division, etc. between
some eaml types:

```
dimen small_margins: 12dp
dimen normal_margins: small_margins * 1.5
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
