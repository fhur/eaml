(ns eaml.fixtures.simple-res)

(def fix-simple-colors
  "color red:        #f00;
   color green:      #0f0;
   color blue:       #00f;
   color main_color: red;")

(def fix-simple-dimen
  "dimen small_margins:   8dp;
   dimen medium_margins:  12dp;
   dimen large_margins:   24dp;
   dimen default_margins: medium_margins;")

(def fix-simple-strings
  "string hello_world: \"Hello World!\";
   string name:        'Pizza 123';")

(def fix-simple-bools
  "bool is_true:    true;
   bool aint_true:  false;
   bool a_boolean:  is_true;")

(def fix-simple-res-with-configs
  "dimen padding {
     default: 12dp;
     v21: 24dp;
     land: 30dp;
   }

   string supports_ripples {
     default: 'nope';
     v21: 'yes';
   }

   color main_color: #f00;
   color button_color {
     default: main_color;
     v21: @drawable/btn_ripple;
   }
  ")
