cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/com.red_folder.phonegap.plugin.backgroundservice/www/backgroundservice.js",
        "id": "com.red_folder.phonegap.plugin.backgroundservice.BackgroundService",
        "clobbers": [
            "window.plugins.BackgroundServices"
        ]
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "com.red_folder.phonegap.plugin.backgroundservice": "3.3"
}
// BOTTOM OF METADATA
});