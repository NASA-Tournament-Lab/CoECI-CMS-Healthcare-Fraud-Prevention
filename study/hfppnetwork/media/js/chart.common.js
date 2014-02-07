// generate chart tooltip
Raphael.fn.tooltip = function(tip, left, top){
    var PADDING = 5;
    var TAIL_SIZE = 8;
    this.setStart();

    // tooltip text generation and calculation bounds
    var text = this.text(left, top, tip).attr({
        "font-size": "11px",
        "fill": "#445157"
    });
    var bounds = text.getBBox();
    bounds.x -= PADDING;
    bounds.y -= PADDING;
    bounds.x2 += PADDING;
    bounds.y2 += PADDING;

    // tooltip bubble
    var path = [
        "M", bounds.x, bounds.y,
        "L", bounds.x2, bounds.y,
        "L", bounds.x2, bounds.y2,
        "L", (bounds.x + bounds.x2 + TAIL_SIZE) / 2, bounds.y2,
        "l", -TAIL_SIZE / 2, TAIL_SIZE, "l", -TAIL_SIZE / 2, -TAIL_SIZE,
        "L", bounds.x, bounds.y2,
        "L", bounds.x, bounds.y
    ];
    this.path(path.join(" ")).attr({
        "stroke": "#f2be9e",
        "fill": "#f6e1d8"
    }).insertBefore(text);

    var tooltip = this.setFinish();

    // move tooltip to right position
    tooltip.transform("t0," + ((bounds.y - bounds.y2) / 2  - TAIL_SIZE) );

    return tooltip;
};

// retrieve tooltip functions for hover and normal states
Raphael.getTooltipFunc = function(paper, tip, left, top){
    var tooltip;
    return {
        "over": function(){
            tooltip = paper.tooltip(tip, left, top);
        },
        "out": function(){
            if (tooltip) {
                tooltip.remove();
                tooltip = null;
            }
        },
        "tooltip": function(){
            return tooltip;
        }
    }
};

// draw axis values
Raphael.fn.axis = function(values) {
    var axisConfig = [];
    for (var i = 0; i < values.length; i++) {
        axisConfig.push({
            "type": "text",
            "text": values[i],
            "x": 0,
            "y": 0,
            "fill": "#33495f",
            "font-size": "11px"
        });
    }
    return this.add(axisConfig);
};

/**
 * Draw chart grid
 * @param params {
 *      xStart, yStart - zero point
 *      xAxis, yAxis - array of axis values
 *      xDiff, yDiff - difference between two neighboring axis marks
 *      xScale, yScale - ratio pixels per unit
 *      transpose - flag that dimension axis is yAxis
 * }
 */
Raphael.fn.chartGrid = function(params) {
    var MARK_LENGTH = 3;
    var BASE_WIDTH = 2;

    var xStart = params.xStart || 0;
    var yStart = params.yStart || 0;

    var xAxis = params.xAxis;
    var yAxis = params.yAxis;

    // draw baseline, marks and values axis
    var path;
    var boundsPrev, bounds;

    if (params.transpose) {
        // transposed case: x and y axis are swapped
        path = [
            "M", params.xStart, params.yStart, "l", 0, -params.ySize * params.yScale, "l", MARK_LENGTH, 0,
            "M", params.xStart, params.yStart, "l", MARK_LENGTH, 0
        ];

        var yPos = yStart - params.yDiff * params.yScale;
        for (var i = 1; i <= yAxis.length; i++) {
            path.push("M", xStart, yPos, "l", -MARK_LENGTH, 0);
            var text = this.text(xStart - 8, yPos, yAxis[i - 1]).attr({"fill": "#33495f", "font-size": "11px"});
            bounds = text.getBBox();
            if (boundsPrev && boundsPrev.y < bounds.y2) {
                text.remove();
            } else {
                text.transform("t-" + bounds.width / 2 +",0");
                boundsPrev = bounds;
            }

            yPos -= params.yDiff * params.yScale;
        }
    } else {
        // normal case
        path = [
            "M", params.xStart, params.yStart, "l", params.xSize * params.xScale, 0, "l", 0, MARK_LENGTH,
            "M", params.xStart, params.yStart, "l", 0, MARK_LENGTH
        ];

        var xPos = xStart + params.xDiff * params.xScale;
        for (var i = 1; i <= xAxis.length; i++) {
            path.push("M", xPos, yStart, "l", 0, MARK_LENGTH);
            var text = this.text(xPos, yStart + 15, xAxis[i - 1]).attr({"fill": "#33495f", "font-size": "11px"});
            bounds = text.getBBox();
            if (boundsPrev && boundsPrev.x2 > bounds.x) {
                text.remove();
            } else {
                boundsPrev = bounds;
            }
            xPos += params.xDiff * params.xScale;
        }
    }

    this.path(path.join(" ")).attr({
        "stroke-width": BASE_WIDTH,
        "stroke": "#888"
    }).toBack();

    // draw grid lines and dimension axis
    path = [];
    if (params.transpose) {
        // transposed case
        xPos = xStart;
        for (var i = 0; i < xAxis.length; i++) {
            if (i > 0) {
                path.push("M", Math.round(xPos) + 0.5, yStart, "l", 0, -params.ySize * params.yScale);
            }
            this.text(xPos, yStart + 10, xAxis[i]).attr({"fill": "#33495f", "font-size": "11px"});
            xPos += params.xDiff * params.xScale;
        }
    } else {
        // normal case
        yPos = yStart;
        for (var i = 0; i < yAxis.length; i++) {
            if (i > 0) {
                path.push("M", xStart, Math.round(yPos) - 0.5, "l", params.xSize * params.xScale, 0);
            }
            var text = this.text(xStart - 8, yPos, yAxis[i]).attr({"fill": "#33495f", "font-size": "11px"});
            text.transform("t-" + text.getBBox().width / 2 +",0");
            yPos -= params.yDiff * params.yScale;
        }
    }

    this.path(path.join(" ")).attr({
        "stroke-width": 1,
        "stroke": "#ccc"
    }).toBack();
};

/**
 * Draw single line
 * @param params {
 *      xStart, yStart - zero point
 *      xAxis, yAxis - array of axis values
 *      xDiff, yDiff - difference between two neighboring axis marks
 *      xScale, yScale - ratio pixels per unit
 *      colorIndex - shows which color use to draw line
 *      measureTitle - text to show in tooltip before the value
 *      data - array of number values
 * }
 */
Raphael.fn.line = function(params){
    var POINT_RADIUS = 6;
    var POINT_STROKE = 2;
    var LINE_STROKE = 3.5;

    // Need to use inner function because of hover activity changes point appearance
    function processPoint(index){
        // decrease point value by zero value
        var pointValue = data[index] - mMin;

        if (index > 0) {
            var line = that.path([
                "M", xStart + index * xScale, yStart - pointValue * yScale,
                "L", xStart + (index - 1) * xScale, yStart - (data[index - 1] - mMin) * yScale
            ].join(" "));
            line.attr({
                "stroke-width": LINE_STROKE,
                "stroke": color
            });
            lineSet.push(line);
        }

        var point = that.circle(xStart + index * xScale, yStart - pointValue * yScale, POINT_RADIUS);
        point.attr({
            "stroke-width": POINT_STROKE,
            "stroke": "#fff",
            "fill": color
        });

        var tooltipFunc = Raphael.getTooltipFunc(that, params.measureTitle + " : " + data[index], xStart + index * xScale, yStart - pointValue * yScale - POINT_RADIUS - POINT_STROKE);

        point.hover(
            function(){
                this.attr({
                    "fill": hoverColor,
                    "transform": "s1.2,1.2"
                });
                tooltipFunc.over();
            },
            function(){
                this.attr({
                    "fill": color,
                    "transform": ""
                });
                tooltipFunc.out();
            }
        );

        pointSet.push(point);
    }

    var that = this;
    var data = params.data;
    var xStart = params.xStart || 0;    // xAxis canvas zero point
    var yStart = params.yStart || 0;    // yAxis canvas zero point
    var xScale = params.xScale;
    var yScale = params.yScale;
    var mMin = params.mMin;             // yAxis zero-point data value
    var lineSet = this.set();
    var pointSet = this.set();
    var color = Raphael.getChartColor(params.colorIndex);
    var hoverColor = Raphael.getChartColor(params.colorIndex, true);

    for (var i = 0; i < data.length; i++) {
        processPoint(i);
    }
    pointSet.toFront();
};

/**
 * Draw multiply lines
 * @param params {
 *      xStart, yStart - zero point
 *      xAxis, yAxis - array of axis values
 *      xDiff, yDiff - difference between two neighboring axis marks
 *      xScale, yScale - ratio pixels per unit
 *      data - array of lines {
 *          data: array of numbers
 *      }
 * }
 */
Raphael.fn.lineChart = function(params){
    var data = params.data;
    for (var i = 0; i < data.lines.length; i++) {
        this.line({
            data: data.lines[i].data,
            colorIndex: i,
            xScale: params.xScale,
            yScale: params.yScale,
            xStart: params.xStart + params.xDiff * params.xScale,
            yStart: params.yStart,
            mMin: params.mMin,
            measureTitle: params.measureTitle
        });
    }
    this.chartGrid(params);
};

/**
 * Draw bar chart
 * @param params {
 *      xStart, yStart - zero point
 *      xAxis, yAxis - array of axis values
 *      xDiff, yDiff - difference between two neighboring axis marks
 *      xScale, yScale - ratio pixels per unit
 *      transpose - flag shows dimension axis is yAxis
 *      stacked - flag shows sequence bar build
 *      stacks - array of array of numbers
 * }
 */
Raphael.fn.barChart = function(params){
    function processBar(left, top, width, height, colorIndex, value){

        // clip bar by min and max canvas values
        if (top + height > params.yStart) {
            height = Math.max(0, params.yStart - top);
            top = params.yStart - height;
        }
        if (top < yEnd) {
            height = Math.max(0, height - yEnd + top);
            top = params.yStart - params.ySize * params.yScale;
        }
        if (left + width > xEnd) {
            width = Math.max(0, xEnd - left);
            left = xEnd - width;
        }
        if (left < params.xStart) {
            width = Math.max(0, width - params.xStart + left);
            left = params.xStart;
        }


        var rect = that.rect(Math.round(left), Math.round(top), Math.ceil(width), Math.ceil(height));
        var normalColor = Raphael.getChartColor(colorIndex);
        var hoverColor = Raphael.getChartColor(colorIndex, true);
        rect.attr({
            "stroke-width": 0,
            "fill": normalColor
        });
        var tooltipFunc = Raphael.getTooltipFunc(that, params.measureTitle + " : " + value, left + width / 2, top);
        rect.hover(
            function(){
                this.attr({
                    "fill": hoverColor
                });
                tooltipFunc.over();
            },
            function(){
                this.attr({
                    "fill": normalColor
                });
                tooltipFunc.out();
            }
        );
        return rect;
    }

    var that = this;
    var data = params.stacks;
    var stackLength = data[0].length;
    var mMin = params.mMin;                                     // yAxis zero point
    var xEnd = params.xStart + params.xSize * params.xScale;    // canvas max X value
    var yEnd = params.yStart - params.ySize * params.yScale;    // canvas min Y value

    var xPos, yPos, xBarPos, yBarPos;

    if (params.transpose) {
        // drawing horizontal bars

        yPos = params.yStart - params.yDiff * params.yScale;
        var barHeight = params.yScale;
        if (params.stacked) {
            barHeight = params.yScale / 2;
        } else {
            barHeight = params.yScale / (stackLength * 1.25 + 1);
        }

        for (var i = 0; i < data.length; i++) {
            xPos = params.xStart - mMin * params.xScale;

            for (var j = 0; j < stackLength; j++)  {
                if (params.stacked) {
                    yBarPos = yPos + barHeight / 2;
                } else {
                    yBarPos = yPos + (barHeight * 1.25) * (stackLength / 2 - j - 0.125);

                    // reset bar X position for grouped bars
                    xPos = params.xStart - mMin * params.xScale;
                }
                var colorIndex = (stackLength == 1) ? i : j;
                processBar(xPos, yBarPos - barHeight, data[i][j] * params.xScale, barHeight, colorIndex, data[i][j]);
                xPos += data[i][j] * params.xScale;
            }
            yPos -= params.yScale;
        }
    } else {
        // drawing vertical bars

        xPos = params.xStart + params.xDiff * params.xScale;
        var barWidth = params.xScale;
        if (params.stacked) {
            barWidth = params.xScale / 2;
        } else {
            barWidth = params.xScale / (stackLength * 1.25 + 1);
        }

        for (var i = 0; i < data.length; i++) {
            yPos = params.yStart + mMin * params.yScale;

            for (var j = 0; j < stackLength; j++)  {
                if (params.stacked) {
                    xBarPos = xPos - barWidth / 2;
                } else {
                    xBarPos = xPos - (barWidth * 1.25) * (stackLength / 2 - j - 0.125);

                    // reset bar Y position for grouped bars
                    yPos = params.yStart + mMin * params.yScale;
                }
                var colorIndex = (stackLength == 1) ? i : j;
                processBar(xBarPos, yPos - data[i][j] * params.yScale, barWidth, data[i][j] * params.yScale, colorIndex, data[i][j]);
                yPos -= data[i][j] * params.yScale;
            }
            xPos += params.xScale;
        }
    }
    this.chartGrid(params);
};

/**
 * Drawing pie charts
 * @param params
 */
Raphael.fn.pieCharts = function(params) {
    var LEGEND_GAP = 20;
    var pieCount = params.data.lines.length;

    // Legend drawing
    var legend = this.set();
    var yPos = LEGEND_GAP;
    for (var i = 0; i < params.xAxis.length; i++) {
        var item = this.legendItem(params.xAxis[i], Raphael.getChartColor(i));
        var bounds = item.getBBox();
        item.attr({
            "transform": Raphael.format("t{0},{1}", -bounds.x, -yPos - bounds.y)
        });
        yPos -= LEGEND_GAP;
        legend.push(item);
    }
    var legendBounds = legend.getBBox();
    for (i = 0; i < legend.length; i++) {
        legend[i].attr({
            "transform": Raphael.format("T{0},{1}", this.width - legendBounds.width - LEGEND_GAP + item.getBBox().width / 2 , (i + 2) * LEGEND_GAP)
        });
    }

    // Maximal possible chart size container calculation
    var rectWidth = this.width - legendBounds.width - LEGEND_GAP * 2;   // Whole diagram container width
    var rectHeight = this.height;                                       // Whole diagram container height
    var colCount = pieCount;
    var pieSize = Math.min(rectWidth / colCount, rectHeight);
    for (var i = 2; i < pieCount; i++) {
        if (rectHeight / i > pieSize) {
            colCount = Math.ceil(pieCount / i);
            pieSize = Math.min(rectWidth / colCount, rectHeight / i);
        } else {
            break;
        }
    }

    var pieW = rectWidth / colCount;                            // One pie container width
    var pieH = rectHeight / (Math.ceil(pieCount / colCount));   // One pie container height
    var pieR = pieSize / 2.4;                                   // Pie radius

    // Pies drawing
    for (var i = 0; i < pieCount; i++) {
        var line = params.data.lines[i];
        var data = [];
        for (var j = 0; j < line.data.length; j++) {
            data.push({
                value: line.data[j],
                title: params.xAxis[j]
            });
        }
        var cx = pieW * (0.5 + i % colCount);
        var cy = pieH * (Math.floor(i / colCount));

        this.text(cx, cy + pieSize / 20, line.title).attr({
            "font-weight": "bold",
            "font-size": pieSize / 15 + "px"
        });

        this.pie(cx, cy + pieR + pieSize / 8, pieR, {
            data: data,
            fancy: params.fancy
        });
    }
};

/**
 * Legend item draw
 * @param caption (string)
 * @param color (integer)
 * @returns {*}
 */
Raphael.fn.legendItem = function(caption, color) {
    var LEGEND_SIZE = 8;
    var that = this;
    that.setStart();
    var text = that.text(0, 0, caption).attr({
        "fill": "#33495f",
        "font-size": "11px"
    });
    var bounds = text.getBBox();
    var rect = that.rect(bounds.x - 14, (bounds.y2 + bounds.y - LEGEND_SIZE) / 2, LEGEND_SIZE, LEGEND_SIZE);
    rect.attr({
        "fill": color,
        "stroke-width": 0
    });
    return that.setFinish();
};


/**
 * Common chart draw
 * @param params
 */
Raphael.fn.chart = function(params) {
    var that = this;
    var data = params.data;
    var CHART_PADDING = 25;
    var TOP_CHART_PADDING = 35;
    var AXIS_PADDING = 50;
    var LEGEND_HEIGHT = 75;
    var CHART_RECT = {
        left: AXIS_PADDING,
        right: that.width - CHART_PADDING,
        top: TOP_CHART_PADDING,
        bottom: that.height - CHART_PADDING
    };

    // legend drawing
    function buildLegend(params) {
        if (params.data.lines.length <= 1) return;
        var LEGEND_GAP_X = 20;
        var LEGEND_GAP_Y = 20;
        var xPos = CHART_RECT.right;
        var yPos = CHART_RECT.top - CHART_PADDING;
        var legend = that.set();
        for (var i = 0; i < params.legend.length; i++) {
            var item = that.legendItem(params.legend[i], Raphael.getChartColor(i));
            var bounds = item.getBBox();
            xPos -= (bounds.x2 - bounds.x);

            if (xPos < CHART_PADDING) {
                xPos = CHART_RECT.right - (bounds.x2 - bounds.x);
                yPos += LEGEND_GAP_Y;
            }
            item.transform(["t", xPos - bounds.x, yPos - bounds.y].join(" "));
            legend.push(item);
            xPos -= LEGEND_GAP_X;
        }
        bounds = legend.getBBox();
        LEGEND_HEIGHT = bounds.height + LEGEND_GAP_Y / 2;
        CHART_RECT.top += LEGEND_HEIGHT - CHART_PADDING;
    }

    // X axis size calculation
    function fillXAxis(maxVal, params) {
        params.xStart = CHART_RECT.left;
        params.xDiff = 1;
        params.xSize = maxVal;
    }

    // Y axis size calculation
    function fillYAxis(minVal, maxVal, params) {
        var minLine = 0;
        var maxLine;
        if (params.autofit && !params.stacked) {
            // calculate autofit values
            minLine = Math.max(0, minVal - Math.ceil((maxVal - minVal) / 5));
            maxLine = maxVal + Math.ceil((maxVal - minLine) / 20);
        } else {
            // assign custom values
            minLine = params.measureMin || 0;
            maxLine = params.measureMax || maxVal + Math.ceil((maxVal - minLine) / 20);
        }

        var axisLength = Math.max(maxLine - minLine, 1);
        var diff;

        // calculate Y diff to draw from 6 to 12 grid lines
        if (axisLength > 20) {
            diff = Math.ceil(Math.ceil(axisLength / 5) / 12) * 5;
        } else {
            if (axisLength < 5) axisLength++;
            diff = Math.ceil(axisLength / 12);
        }
        var lineVal = minLine;
        var yAxis = [];
        while (lineVal <= maxLine) {
            yAxis.push(lineVal);
            lineVal += diff;
        }

        params.yAxis = yAxis;
        params.mMin = minLine;
        params.mMax = maxLine;
        params.ySize = axisLength;
        params.yStart = CHART_RECT.bottom;
        params.yDiff = diff;
    }

    function fillAxis(params){
        var xCount = data.lines[0].data.length;
        var stackSumMax = 0;
        var stackSumMin = +Infinity;
        var valueMax = 0;
        var valueMin = +Infinity;

        // calculate stack sum values and maximum and minimum values
        var stacks = [];
        for (var i = 0; i < xCount; i++) {
            var stackSum = 0;
            var stack = [];
            for (var j = 0; j < data.lines.length; j++) {
                var value = data.lines[j].data[i] || 0;
                if (value > valueMax) valueMax = value;
                if (value < valueMin) valueMin = value;
                stackSum += value;
                stack.push(value);
            }
            stacks.push(stack);
            if (stackSum > stackSumMax) stackSumMax = stackSum;
            if (stackSum < stackSumMin) stackSumMin = stackSum;
        }

        // set left chart padding accordingly with axis title width
        var xAxisSet = that.axis(params.xAxis);
        var yAxisSet = that.axis([stackSumMax]);
        if (params.transpose) {
            CHART_RECT.left = CHART_PADDING + xAxisSet.getBBox().width;
        } else {
            CHART_RECT.left = CHART_PADDING + yAxisSet.getBBox().width;
        }
        xAxisSet.remove();
        yAxisSet.remove();

        // draw legend if group/stack dimension present
        params.legend = [];
        for (i = 0; i < data.lines.length; i++) {
            params.legend.push(data.lines[i].title || "");
        }
        buildLegend(params);

        fillXAxis(xCount + 1, params);
        fillYAxis(params.stacked ? stackSumMin : valueMin, params.stacked ? stackSumMax : valueMax, params);

        // Transposing axises
        if (params.transpose) {
            var tmp = params.xAxis;
            params.xAxis = params.yAxis;
            params.yAxis = tmp;
            tmp = params.xDiff;
            params.xDiff = params.yDiff;
            params.yDiff = tmp;
            tmp = params.xSize;
            params.xSize = params.ySize;
            params.ySize = tmp;
        }

        // Data values to canvas scales calculation
        params.xScale = (CHART_RECT.right - CHART_RECT.left) / params.xSize;
        params.yScale = (CHART_RECT.bottom - CHART_RECT.top) / params.ySize;
        params.stacks = stacks;
    }

    // copy params hash to prevent original data modifying
    params = $.extend({}, params);

    // draw selected chart type
    switch (params.chartType) {
        case "lines":
            fillAxis(params);
            this.lineChart(params);
            break;
        case "bars":
            params.stacked = (params.barType == 'stacked');
            params.transpose = (params.barOrientation == 'horizontal');
            fillAxis(params);
            this.barChart(params);
            break;
        case "pie":
            this.pieCharts(params);
            break;
    }
};