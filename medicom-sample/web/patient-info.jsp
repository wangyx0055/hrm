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
                        <div><button class='content-box' id='btn-01'>Patient 1</button></div>
                        <div><button class='content-box' id='btn-02'>Patient 2</button></div>
                        <div><button class='content-box' id='btn-03'>Patient 3</button></div>
                    </div>
                </div>
            </div>

            <div class="reminder-list right">
                <div class='content-board'>
                    <div>
                        <form enctype="multipart/form-data" method="post">
                            <input class="classic" id="ipt-file-select" type="file" name="file"/>
                            <button class="classic" id="btn-upload" type="button">Upload Data From File</button>
                        </form>
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
            function file_uploader(files, url_call, on_ready_callback, info) {
                var xhr = new XMLHttpRequest();
                xhr.addEventListener('progress', function (e) {
                    var done = e.position || e.loaded, total = e.totalSize || e.total;
                    console.log('xhr progress: ' + (Math.floor(done / total * 1000) / 10) + '%');
                }, false);
                if (xhr.upload) {
                    xhr.upload.onprogress = function (e) {
                        var done = e.position || e.loaded, total = e.total || e.total;
                        console.log('xhr.upload progress: ' + done + ' / ' + total + ' = ' + 
                                   (Math.floor(done / total * 1000) / 10) + '%');
                    };
                }
                xhr.onreadystatechange = function (e) {
                    if (4 === this.readyState) {
                        console.log(['xhr upload complete', e]);
                        on_ready_callback(info);
                    }
                };
                var formData = new FormData();
                for (var i = 0; i < files.length; i ++) {
                    formData.append("file", files[i], files[i].name);
                }
                xhr.open('post', url_call);
                xhr.send(formData);
            }

            function d3_chart_drawer() {
            }

            function on_file_uploaded(info) {
                alert('files has been uploaded');
            }

            var g_files = null;
            var g_curr_patient = null;

            function clk_patient_button(e) {
                alert(this.id);
            }

            function clk_set_file_path(e) {
                g_files = document.getElementById("ipt-file-select").files;
            }

            function clk_upload_file(e) {
                file_uploader(g_files,
                        "patient-info.jspx?call=CEPatientData&action=upload",
                        on_file_uploaded, null);
            }

            document.getElementById('btn-upload').onclick = clk_upload_file;
            document.getElementById('ipt-file-select').onchange = clk_set_file_path;

            document.getElementById('btn-01').onclick = clk_patient_button;
        </script>
    </body>
</html>