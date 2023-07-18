cordova.define('cordova/plugin_list', function(require, exports, module) {
  module.exports = [
    {
      "id": "SimpleMath.MyMath",
      "file": "plugins/SimpleMath/www/MyMath.js",
      "pluginId": "SimpleMath",
      "clobbers": [
        "cordova.plugins.MyMath"
      ]
    },
    {
      "id": "cordova-plugin-splashscreen.SplashScreen",
      "file": "plugins/cordova-plugin-splashscreen/www/splashscreen.js",
      "pluginId": "cordova-plugin-splashscreen",
      "clobbers": [
        "navigator.splashscreen"
      ]
    }
  ];
  module.exports.metadata = {
    "SimpleMath": "1.0.0",
    "cordova-plugin-splashscreen": "6.0.1"
  };
});