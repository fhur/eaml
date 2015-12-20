# Planned features
- Gradle integration [ ]
- Android studio plugin [ ]
  - Syntax highlighting
  - Compile from IDE
  - Go to definition
- Mixins with arguments e.g. [ ]
```
mixin coloredText(textColor, backgroundColor) {
  android:textColor: textColor;
  android:backgroundColor: backgroundColor;
}
```
Which could then be invoked using `coloredText(#f00,#fff)` inside a Style.
- Reverse compiler: cleanup your existing styles. [ ]
- Detailed documentation [ ]
- Type checking at compile time [ ]

# eaml 0.4 - 1450616768

- Added support for `@null`
- Added mixins with no arguments:
```
mixin redText {
  android:textColor: #f00;
}

style RedButton < Button {
  redText();
}
```
- Mixins with arguments will come in the next version
- Fixed several issues with native resource pointers (e.g. @foo/bar)

# eaml 0.3.0 - 1448745045

Main features:
- Removed multiple inheritance. Implementation proved to be way to complicated.
  Added support for single inheritance via piggy-backing on the style's native
  `parent` attribute.
- Added support for `bool`, `string` and `integer`.
- Added support for multiple configurations for simple resources:
```
dimen margins {
  default: 8dp;
  land: 12dp;
}
```
- Added support for multiple configurations for styles:
```
style Button < BaseButton {
  android:background: @drawable/btn_bkg;
  &:v21, v22 {
    android:background: @drawable/btn_ripple;
  }
}
```

# eaml 0.2.0 - 1445287611

First minimal working version:
- Support for `color` and `dimens` e.g. `color primary_color: #f00;`
- Multiple inheritance between styles
```
style BigRedButton < RedButton, BigButton {
  # your code here
}
```
- Nested directories
  You can organize your files with nested directores:
```
./styles/
├── variables.eaml
├── common_styles.eaml
└── buttons
  ├── buttons.eaml
  └── more_buttons.eaml
```
- One global scope.
  Currently there is only one scope: the global scope. This basically
  means that a `color foo` defined in one file is accesible everywhere.
- No support for comments: no comments are supported yet. This feature
  coming soon.
- No support for multiple configurations: Configuration support also
  coming soon.


