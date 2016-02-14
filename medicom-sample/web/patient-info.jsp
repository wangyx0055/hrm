<%-- 
    Document   : patient-info
    Created on : Feb 13, 2016, 11:39:04 PM
    Author     : davis
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Patient Info Management</title>
        <meta charset="UTF-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link rel="stylesheet" href="css/nature.css">
        <script src='js/d3/d3.js'></script>

    </head>
    <body class="nature">
        <h1 id="txt-logo" class="heading">
            <a class="no-decoration" href="index.jsp">MediCom</a>
        </h1>

        <div class="two-sided">

            <div class="forum-board left">
                <div class='content-board'>
                    <div><input type='search' value='Search'/></div>
                    <div style="margin-left: 20px; margin-top: 10px;
                         margin-bottom: 10px"><button class='classic'>Add Patient</button></div>
                    <div style="min-height: 400px;" class='content-board'>
                        <div><button class='content-box'>Patient 1</button></div>
                        <div><button class='content-box'>Patient 2</button></div>
                        <div><button class='content-box'>Patient 3</button></div>
                    </div>
                </div>
            </div>

            <div class="reminder-list right">
                <div class='content-board'>
                    <div>
                        <input  class="classic" type="file" id="file-select" name="photos[]"/>
                        <button class="classic" type="submit" id="btn-upload">Upload Data From File</button>
                    </div>
                    <div>Issues and Notes: </div>
                    <div><textarea style='width: 100%;' rows="4" cols="50" >High blood pressure...</textarea></div>
                    <div>
                        <select style='min-width: 50%'>
                            <option selected>Chart Selection</option>
                        </select>
                    </div>
                    <div id="charting-area" style='min-height: 300px;'>Chart Selected: </div>
                    <div>Comments</div>
                    <div><textarea style='width: 100%;' rows="4" cols="50" >Doctor's Comments</textarea></div>
                </div>
            </div>
        </div>

        <script>
            function file_uploader(file, file_name, url_call, on_ready_callback, info) {
                var xhr = new XMLHttpRequest();
                xhr.file = file; // not necessary if you create scopes like this
                xhr.addEventListener('progress', function (e) {
                    var done = e.position || e.loaded, total = e.totalSize || e.total;
                    console.log('xhr progress: ' + (Math.floor(done / total * 1000) / 10) + '%');
                }, false);
                if (xhr.upload) {
                    xhr.upload.onprogress = function (e) {
                        var done = e.position || e.loaded, total = e.totalSize || e.total;
                        console.log('xhr.upload progress: ' + done + ' / ' + total + ' = ' + (Math.floor(done / total * 1000) / 10) + '%');
                    };
                }
                xhr.onreadystatechange = function (e) {
                    if (4 === this.readyState) {
                        console.log(['xhr upload complete', e]);
                        on_ready_callback(info);
                    }
                };
                xhr.open('post', url_call, true);
                xhr.setRequestHeader("Content-Type", "multipart/form-data");
                var formData = new FormData();
                formData.append("thefile", file, file_name);
                xhr.send(file);
            }
            
            function d3_chart_drawer() {
            }
            
            function on_file_uploaded(info) {
                
            }
            
            var g_file = null;
            var g_file_path = null;
            
            function clk_set_file_path(e) {
                g_file = this.files[0];
                g_file_path = this.value;
            }
            
            function clk_upload_file(e) {
                file_uploader(g_file, g_file_path, "patient-info.jspx?call=CEPatientData&action=upload",
                              on_file_uploaded, null);
            }
            
            document.getElementById('btn-upload').onclick = clk_upload_file;
            document.getElementById('file-select').onchange = clk_set_file_path;
        </script>
    </body>
</html>