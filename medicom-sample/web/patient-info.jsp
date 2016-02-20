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
        <link rel="stylesheet" href="css/simplicity.css">
        <script src='js/d3/d3.js'></script>
        <script src='js/hrm.js'></script>
    </head>
    <body class="nature">
        <h1 id="txt-logo" class="heading">
            <a class="no-decoration" href="index.jsp">MediCom</a>
        </h1>

        <div class="two-sided">

            <div class="forum-board left">
                <div class='content-board'>
                    <div><input id='txt-search' type='search' value='Search'/></div>
                    <div style="margin-top: 10px; margin-bottom: 10px">
                        <button class='classic-fill-width'>Add Patient</button>
                    </div>
                    <div style="margin-top: 20px; margin-bottom: 20px;">
                        <div><button class='classic-fill-width' id='btn-01'>Patient 1</button></div>
                        <div><button class='classic-fill-width' id='btn-02'>Patient 2</button></div>
                        <div><button class='classic-fill-width' id='btn-03'>Patient 3</button></div>
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
                    <div id="charting-area" width: 100%; style='min-height: 300px;'></div>
                    <div>Comments</div>
                    <div><textarea style='width: 100%;' rows="4" cols="50" >Doctor's Comments</textarea></div>
                </div>
            </div>
        </div>

        <script>
            function d3_chart_init(dst_elm) {
                dst_elm.style.backgroundColor = 'rgba(255,255,255,1)';
                var width = dst_elm.offsetWidth;
                var height = dst_elm.offsetHeight;
                var style = window.getComputedStyle(dst_elm);
                var x = 35;// style.getPropertyValue('padding-left');
                var y = 20; // style.getPropertyValue('padding-top');

                // Initialize with empty data
                d3_chart_draw_bar_chart(dst_elm, null, width, height, x, y);
            }

            function d3_chart_draw_bar_chart(dst_elm, xy_pairs, width, height, x, y) {
                var svg_container = d3.select('#' + dst_elm.id).append("svg:svg").
                        attr('width', width).attr('height', height);

                var x_scale = d3.scale.linear()
                        .domain([0, 10])
                        .range([0, width]);
                var y_scale = d3.scale.linear()
                        .domain([0, 120])
                        .range([height, 0]);
                var x_axis = d3.svg.axis()
                        .scale(x_scale).orient("top");
                var y_axis = d3.svg.axis()
                        .scale(y_scale).orient("left");
                svg_container.append("g")
                        .attr("transform", "translate(" + x + "," + (height - y) + ")")
                        .call(x_axis);
                svg_container.append("g")
                        .attr("transform", "translate(" + x + ",-" + y + ")")
                        .call(y_axis);
            }

            function on_file_uploaded(info) {
                alert('files has been uploaded');
            }

            function clk_patient_button(e) {
                alert(this.id);
            }

            function clk_upload_file(e) {
                var datatransfer = new hrm.DataTransfer();
                var file_select = document.getElementById("ipt-file-select");
                if (file_select.files.length === 0) {
                    alert('You have not selected any files');
                    return ;
                }
                datatransfer.upload_files(file_select.files,
                                          "patient-info.jspx?call=CEPatientData&action=upload",
                                          on_file_uploaded, null);
            }

            function page_load() {
                document.getElementById('btn-upload').onclick = clk_upload_file;
                document.getElementById('btn-01').onclick = clk_patient_button;
                
                hrmui.InputHint(document.getElementById('txt-search'), 'Search');
                d3_chart_init(document.getElementById('charting-area'));
            }

            window.onload = page_load;
        </script>
    </body>
</html>