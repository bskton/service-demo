/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
    // Application Constructor
    initialize: function() {
        this.bindEvents();
    },
    // Bind Event Listeners
    //
    // Bind any events that are required on startup. Common events are:
    // 'load', 'deviceready', 'offline', and 'online'.
    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },
    // deviceready Event Handler
    //
    // The scope of 'this' is the event. In order to call the 'receivedEvent'
    // function, we must explicitly call 'app.receivedEvent(...);'
    onDeviceReady: function() {
        console.log("Service Demo started.");
        app.service = cordova.require('com.red_folder.phonegap.plugin.backgroundservice.BackgroundService');
        app.getStatus();
        document.getElementById("updateConfigBtn").onclick = function() {
            app.updateConfig();
        }
    },

    handleSuccess: function(data) {
        console.log('handleSuccess');
        console.log(JSON.stringify(data));
        app.updateInfo(data);
    },

    handleError: function(data) {
        console.log('handleError');
        console.log(JSON.stringify(data));
        app.updateInfo(data);
    },

    getStatus: function() {
        this.service.getStatus(this.handleSuccess, this.handleError);
    },

    updateInfo: function(data) {
        console.log("ServiceRunning: " + data.ServiceRunning);

        // TODO: move to separate method
        var interval = document.getElementById("interval");
        var host = document.getElementById("host");
        if (data.Configuration) {
            try {
                app.interval = data.Configuration.interval;
                interval.value = app.interval;
                host.value = data.Configuration.host;
            } catch(e) {
                console.log(e.message);
            }
        } else {
            app.interval = "1000";
            interval.value = app.interval;
            host.value = "localhost";
        }

        // TODO: move to separate method
        if (data.ServiceRunning) {
            document.getElementById("serviceRunning").innerHTML = "True";

            var serviceRunningToggle = document.getElementById("serviceRunningToggle");
            serviceRunningToggle.value = "Stop";
            serviceRunningToggle.onclick = function() {
                app.stopService();
            }
        } else {
            document.getElementById("serviceRunning").innerHTML = "False";

            var serviceRunningToggle = document.getElementById("serviceRunningToggle");
            serviceRunningToggle.value = "Run";
            serviceRunningToggle.onclick = function() {
                app.startService();
            }
        }

        // TODO: move to separate method
        if (data.TimerEnabled) {
            document.getElementById("timerEnabled").innerHTML = "True";

            var timerEnableToggle = document.getElementById("timerEnableToggle");
            timerEnableToggle.value = "Disable";
            timerEnableToggle.onclick = function() {
                app.disableTimer();
            }
        } else {
            document.getElementById("timerEnabled").innerHTML = "False";

            var timerEnableToggle = document.getElementById("timerEnableToggle");
            timerEnableToggle.value = "Enable";
            timerEnableToggle.onclick = function() {
                app.enableTimer(app.interval);
            }
        }
    },

    startService: function() {
        this.service.startService(this.handleSuccess, this.handleError);
    },

    stopService: function() {
        this.service.stopService(this.handleSuccess, this.handleError);
    },

    enableTimer: function(interval) {
        // TODO: When updating interval, need disable timer then enable it. It calls updateInfo.
        // Need update interval without calling updateInfo.
        this.service.enableTimer(interval, this.handleSuccess, this.handleError);
    },

    disableTimer: function() {
        this.service.disableTimer(this.handleSuccess, this.handleError);
    },

    //TODO: To update config we need run service first
    updateConfig: function() {
        console.log("Set config");
        var interval = document.getElementById("interval").value;
        var host = document.getElementById("host").value;
        // TODO: add validation

        var config = {
            interval: interval,
            host: host
        }

        this.service.setConfiguration(config, this.handleSuccess, this.handleError);
    }
};

app.initialize();
