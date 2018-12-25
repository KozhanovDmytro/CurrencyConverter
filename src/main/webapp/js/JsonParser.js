/**
 * Function for parsing JSON document which was got by {@param url}.
 *
 * @param url JSON url
 * @param callback function for make response whether was read json or not.
 *
 * @author Dmytro K.
 * @version 25.12.2018 18:00
 * */
var getJSON = function(url, callback) {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', url, true);
    xhr.responseType = 'json';
    xhr.onload = function() {
        var status = xhr.status;
        if (status === 200) {
            callback(null, xhr.response);
        } else {
            callback(status);
        }
    };
    xhr.send();
};