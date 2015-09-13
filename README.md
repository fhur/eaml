# eaml

eaml (pronounced e-mel) is the extended android modeling language. It is
an XML preprocessor that will make your android resource definitions
simple, readable, understandable and will greatly facilitate supporting
several different configurations with a minimal code base.

## Syntax and features

This simple guide will give you a feel of the eaml syntax. For a more
concise guide of the eaml syntax please see the
[documentation](./doc/intro.md)
section.

#### Multiple inheritance support
It is often the case when you want a style class inherit or "share" the
functionality of two previously created styles. Consider the case where
you have a `BigButton` and a `RedButton` and you want to create a
`BigRedButton` which inherits from both styles. You can write this using
eaml as follows:

```
# A style for big buttons
(defstyle BigButton
  android:padding=8dp
  android:textSize=24sp)

(defstyle RedButton
  android:textColor=#F00)

(defstyle BigRedButton
  (BigButton)
  (RedButton)
  android:text="I am a big red button")
```

#### Supporting multiple configurations
Android supports multiple configurations by creating different sets of
folders per configuration. eamls supports this from a language level as
follows:

```
# Let's define a layout with list of user's
(deflayout user_list
  (ListView android:id="@+id/usersList"))

# and a leyaout with the user's details
(deflayout user_detailed
  (LinearLayout
    android:layout_width=match_parent
    android:layout_height=match_parent
    (TextView android:id="@+id/userName")
    (TextView android:id="@+id/userLastName")
    (Button android:id="@+id/btnRefresh")))

# on a table we want to show both of them at the same time so
# we include both layouts as follows
(deflayout [land w600dp]
  (LinearLayout users_screen
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    (user_list)
    (user_detailed)))

```

This not only works for layouts, it works for any type of resource:
```
(defdrawable btn_background_red
  (selector (item android:state_pressed="true"
              (shape android:rectangular="true" (stroke android:color="#f22")))
            (item
              (shape android:rectangular="true" (stroke android:color="#f00")))))

(defdrawable [v21] btn_background_red
  (ripple android:color="#f22"
    (shape android:rectangular="true" (stroke (android:color="#f00")))))
```

#### Defining colors
Colors can easily be defined with the following syntax

```
(defcolor red="#f00"
          blue="#00f"
          green="#0f0")
```

#### Functions
There are some pre-defined functions that operate on colors and ohter
types:

```
(defcolor red_1="#f00"
          red_2=#(darken 20% red_1))
```

## License

Copyright © 2015 fhur

Distributed under the Eclipse Public License either version 1.0.
