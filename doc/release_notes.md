
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
  ./styles/buttons/my_buttons.eaml
                  /more_buttons.eaml
          /vars/colors.eaml
               /sizes.eaml
  ```
- One global scope.
  Currently there is only one scope: the global scope. This basically
  means that a `color foo` defined in one file is accesible everywhere.
- No support for comments: no comments are supported yet. This feature
  coming soon.
- No support for multiple configurations: Configuration support also
  coming soon.


