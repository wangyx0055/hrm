/* 
 * Copyright (C) 2016 davis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

var hrm = {
        /**
         * Manage data transfer between client and server.
         */
        DataTransfer: function() {

                /**
                 * Upload an array of files to the server via the url_call. 
                 * When the processing is finished, on_ready_callback will be called with the parameter info.
                 * @param {File[]} files the array of files to upload.
                 * @param {string} url_call request that will send to the server.
                 * @param {functoin} on_ready_callback callback that will be called when the files are uploaded.
                 * @param {var} info parameter that will pass into the no_ready_callback.
                 * @returns {void}
                 */
                this.upload_files = function(files, url_call, on_ready_callback, info) {
                        var xhr = new XMLHttpRequest();
                        xhr.addEventListener('progress', function(e) {
                                var done = e.loaded, total = e.total;
                                console.log('xhr progress: ' + (Math.floor(done / total * 1000) / 10) + '%');
                        }, false);
                        xhr.onreadystatechange = function(e) {
                                if (4 === this.readyState && this.status === 200) {
                                        console.log(['xhr upload complete', e]);
                                        on_ready_callback(info);
                                }
                        };
                        var formData = new FormData();
                        for (var i = 0; i < files.length; i++) {
                                formData.append("file", files[i], files[i].name);
                        }
                        xhr.open('post', url_call);
                        xhr.send(formData);
                };
                /**
                 * Similar to the upload_files function, this function allow the progress callback be specified.
                 * @param {File[]} files the array of files to upload.
                 * @param {string} url_call request that will send to the server.
                 * @param {function} on_progress_callback progress monitor callback.
                 * @param {functoin} on_ready_callback callback that will be called when the files are uploaded.
                 * @param {var} info parameter that will pass into the no_ready_callback.
                 * @returns {void}
                 */
                this.upload_files2 = function(files, url_call, on_progress_callback, on_ready_callback, info) {
                        var xhr = new XMLHttpRequest();
                        xhr.addEventListener('progress', function(evt) {
                                console.log('xhr progress' + math(evt.loaded / evt.total));
                                on_progress_callback(math(evt.loaded / evt.total));
                        }, false);
                        xhr.onreadystatechange = function(e) {
                                if (4 === this.readyState && this.status === 200) {
                                        console.log(['xhr upload complete', e]);
                                        on_ready_callback(info);
                                }
                        };
                        var formData = new FormData();
                        for (var i = 0; i < files.length; i++) {
                                formData.append("file", files[i], files[i].name);
                        }
                        xhr.open('post', url_call);
                        xhr.send(formData);
                },
                        /**
                         * Fetch any string content from the server asynchronously.
                         * @param {string} url_call request that will send to the server.
                         * @param {function} on_ready_callback callback that will be called 
                         * when the content is fetched to the client.
                         * @returns {void}
                         */
                        this.fetch_content = function(url_call, on_ready_callback) {
                                var xhr = new XMLHttpRequest();
                                xhr.onreadystatechange = function() {
                                        if (this.readyState === 4 && this.status === 200)
                                                on_ready_callback(this.responseText);
                                };
                                xhr.open("get", url_call, true);
                                xhr.send(null);
                        };
                /**
                 * Similar to the fetch_content function, this function allow the progress callback be specified.
                 * @param {string} url_call request that will send to the server.
                 * @param {function} on_progress_callback progress monitor callback.
                 * @param {function} on_ready_callback callback that will be called 
                 * when the content is fetched to the client.
                 * @returns {void}
                 */
                this.fetch_content2 = function(url_call, on_progress_callback, on_ready_callback) {
                        var xhr = new XMLHttpRequest();
                        xhr.addEventListener('progress', function(evt) {
                                console.log('xhr progress' + math(evt.loaded / evt.total));
                                on_progress_callback(math(evt.loaded / evt.total));
                        }, false);
                        xhr.onreadystatechange = function() {
                                if (this.readyState === 4 && this.status === 200)
                                        on_ready_callback(this.responseText);
                        };
                        xhr.open("get", url_call, true);
                        xhr.send(null);
                };
        },
        /**
         * Control page elements according to the info sent from the server.
         */
        ElementControl: function() {
                this.__elms = new Set();
                /**
                 * Internal facility shared among the server and the client 
                 * to transmit the events of elements under control.
                 * @param {Event} e
                 * @returns {void}
                 */
                this.__internal_callback = function(e) {
                };
                /**
                 * request for an element descriptor from the server.
                 * @returns {void}
                 */
                this.__request_element_descriptor = function() {
                };
                this.__update_element = function() {
                };
                /**
                 * Get url destination where the element's event will be sent.
                 * @param {HTMLElement} elm the element in question.
                 * @returns {string} the url destination.
                 */
                this.get_destination_for = function(elm) {
                };
                /**
                 * 
                 * @param {HTMLElement} elm
                 * @returns {undefined}
                 */
                this.put_control_for = function(elm) {
                        this.__elms.add(elm);
                };
                /**
                 * To bind the callback to the element specified in order for fine tuning 
                 * such as filtering, blocking and changing the events which will procede to the server.
                 * @param {HTMLElement} elm the element for which you want to filter.
                 * @param {function} filter_callback filtering function
                 * @returns {void}
                 */
                this.add_filter_for = function(elm, filter_callback) {
                };
                /**
                 * Fetch internal data from the server which will be used to update the element in control.
                 * @returns {void}
                 */
                this.update = function() {
                };
        },
        /**
         * To present data fetched from the server response onto the element specified.
         * @param {HTMLElement} elm the element where interpreted data will present onto.
         */
        DataPresent: function(elm) {
                this.__target = elm;
                /**
                 * Clear the data from the element.
                 * @returns {undefined}
                 */
                this.clear = function() {
                        this.__target.innerHTML = '';
                };
                this.__presentdata = function(data, format, to_append) {
                        if (!to_append) {
                                this.clear();
                        }
                        switch (format) {
                                case 'html':
                                {
                                        this.__target += data;
                                        break;
                                }
                                case 'bar-chart':
                                {
                                        break;
                                }
                                case 'line-chart':
                                {
                                        break;
                                }
                        }

                };
                /**
                 * Fetch the data to the local object asychronously.
                 * @param {string} url_call the request where presentable data can be fetched from.
                 * @param {string} format The format of the data to be interpreted.
                 * @param {bool} to_append Whether the new data will append to the element,
                 * or otherwise, truncate the old presentation.
                 * @returns {void}
                 */
                this.fetch = function(url_call, format, to_append) {
                };
                /**
                 * Use the local data specified as immediately available.
                 * @param {type} data
                 * @param {string} format The format of the data to be interpreted.
                 * @param {bool} to_append Whether the new data will append to the element,
                 * or otherwise, truncate the old presentation.
                 * @returns {undefined}
                 */
                this.fetch_local = function(data, format, to_append) {
                        this.__data = data;
                };
        },
        /**
         * Control the work flow associated with a set of pages
         */
        PageFlow: function() {
        }
};

var hrmui = {
        /**
         * Put text hint for an Html element.
         * @param {HTMLElement} elm Element which you would want to the hint to display.
         * @param {string} hint The hint to be displayed.
         * @returns {void}
         */
        InputHint: function(elm, hint) {
                this.__elm = elm;
                this.__hint = hint;

                elm.onfocus = (function(e) {
                        this.value = '';
                });
                elm.onblur = (function(e) {
                        this.value = hint;
                });
        }
};