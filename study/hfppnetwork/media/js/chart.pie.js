// Predefined chart colors and helper to get it
Raphael.CHART_COLORS = [0.6, 0, 0.15, 0.25, 0.1, 0.7, 0.5, 0.4, 0.8, 0.05];

Raphael.getChartColor = function(index, hover) {
    var hue = Raphael.CHART_COLORS[index % Raphael.CHART_COLORS.length];
    if (hover) {
        return Raphael.hsb(hue, 0.8, 0.6);
    } else {
        return Raphael.hsb(hue, 0.9, 0.8);
    }

};

/**
 * Draw pie sector
 * @param centerX
 * @param centerY
 * @param startAngle
 * @param endAngle
 * @param outerRadius
 * @param innerRadius
 * @param params {Raphael Element attributes}
 * @returns {*}
 */
Raphael.fn.sector = function(centerX, centerY, startAngle, endAngle, outerRadius, innerRadius, params) {
    function getArc(centerX, centerY, radius, startAngle, endAngle) {
        return {
            startX: centerX + radius*Math.cos(startAngle),
            startY: centerY + radius*Math.sin(startAngle),
            endX: centerX + radius*Math.cos(endAngle),
            endY: centerY + radius*Math.sin(endAngle)
        };
    }

    innerRadius = innerRadius || 0;

    // Fallback if sector is 360 grad - a whole circle
    if (Math.abs(startAngle - endAngle) % 360 < 0.01) {
        startAngle += 0.01;
    }

    var outerArc = getArc(centerX, centerY, outerRadius, startAngle*Math.PI/180, endAngle*Math.PI/180);

    // Flag to draw arc more than 180 grad
    var largeArc = +(endAngle - startAngle > 180 || endAngle - startAngle < 0);

    // Initial path - outer arc
    var path = ["M", outerArc.startX, outerArc.startY, "A", outerRadius, outerRadius, startAngle, largeArc, 1, outerArc.endX, outerArc.endY];

    if (innerRadius == 0) {
        // Extend path to circle center
        path.push("L", centerX, centerY);
    } else {
        // or to inner arc if inner radius non-zero
        var innerArc = getArc(centerX, centerY, innerRadius, startAngle*Math.PI/180, endAngle*Math.PI/180);
        path.push("L", innerArc.endX, innerArc.endY);
        path.push("A", innerRadius, innerRadius, endAngle, largeArc, 0, innerArc.startX, innerArc.startY);
    }

    // Close path
    path.push("L", outerArc.startX, outerArc.startY);

    return this.path(path.join(" ")).attr(params);
};

/**
 * Draw pie
 * @param centerX
 * @param centerY
 * @param radius
 * @param params {
 *      data: { Array of { value, title } },
 *      fancy: flag to draw fancy pie
 * }
 * @returns {*}
 */
Raphael.fn.pie = function(centerX, centerY, radius, params) {

    // Need to use inner function because of hover and click activity changes sector appearance
    function processSector(startAngle, endAngle, colorIndex, data) {
        var st = that.set();
        var color = Raphael.getChartColor(colorIndex);
        var hoverColor = Raphael.getChartColor(colorIndex, true);

        var sector = that.sector(centerX, centerY, startAngle, endAngle, radius, innerRadius, {fill: color});
        st.push(sector);

        var hoverSector = that.sector(centerX, centerY, startAngle, endAngle, radius + 1, innerRadius, {fill: "transparent", stroke: "#fff", "stroke-width": 2});
        st.push(hoverSector);

        var tooltipFunc = Raphael.getTooltipFunc(
            that,
            data.title + " : " + (data.value / sum * 100).toFixed(2) + "%",
            centerX + (radius + innerRadius) * Math.cos((startAngle + endAngle) * Math.PI / 360) / 2,
            centerY + (radius + innerRadius) * Math.sin((startAngle + endAngle) * Math.PI / 360) / 2
        );

        var tooltipHovered = false;
        var sectorHovered = false;

        // sector hover behavior
        hoverSector.hover(
            function(){
                sector.attr({
                    "fill": hoverColor
                });
                hoverSector.attr({stroke: "#fff", "stroke-width": 2, "opacity": 1});

                sectorHovered = true;

                if (!tooltipFunc.tooltip()) {
                    hoverSector.toFront();
                    tooltipFunc.over();
                    tooltipFunc.tooltip().hover(
                        function(){
                            tooltipHovered = true;
                        },
                        function(){
                            tooltipHovered = false;
                            checkTooltipVisible();
                        }
                    );
                }

            },
            function(){
                sectorHovered = false;
                checkTooltipVisible();
            }
        );

        function checkTooltipVisible(){
            setTimeout(function(){
                if (!(tooltipHovered || sectorHovered)) {
                    sector.attr({
                        "fill": color
                    });
                    hoverSector.attr({"opacity": 0});
                    tooltipFunc.out();
                }
            }, 0);
        }

        // sector click behavior used only for fancy pies
        if (params.fancy) {
            st.click(function(){
                if (activeSector) {
                    activeSector.remove();
                }
                activeSector = that.sector(centerX, centerY, startAngle, endAngle, radius * 1.1, innerRadius, {
                    fill: color,
                    stroke: "#fff", "stroke-width": 5
                });
                setCenterValue(data, color);
                activeSector.click(function(){
                    activeSector.remove();
                    activeSector = null;
                    setCenterValue(null);
                });
                tooltipFunc.out();
            });

            if (data.active) {
                st.click();
            }
        }
    }

    // Draw fancy pie core - center circle
    function setCenterValue(data, color) {
        if (fancyCenter) {
            fancyCenter.remove();
        }

        fancyCenter = that.add([
            {
                "type": "circle",
                "cx": centerX,
                "cy": centerY,
                "r": innerRadius,
                "fill": color == undefined ? Raphael.getChartColor(0) : color,
                "stroke-width": 5,
                "stroke": "#fff"
            },
            {
                "type": "text",
                "text":  data ? data.title : "All",
                "x": centerX,
                "y": centerY - radius / 6,
                "fill": "#fff",
                "font-size": Math.round(radius / 10)  + "px",
                "font-weight": "bold"
            },
            {
                "type": "text",
                "text": data ? data.value : sum,
                "x": centerX,
                "y": centerY + radius / 15,
                "fill": "#fff",
                "font-size": Math.round(radius / 5)  + "px",
                "font-weight": "bold"
            }
        ]);
    }

    var activeSector = null;
    var fancyCenter = null;

    var that = this;
    var data = params.data;
    if (!data.length) return;

    // Rotate pie by 90 grad CCW, then pie begins from 12 hours
    var angle = -90;

    var sum = 0;
    for (var i = 0; i < data.length; i++) {
        sum += data[i].value;
    }
    var innerRadius = params.fancy ? radius/ 2 : 0;

    if (params.fancy) {
        setCenterValue(null);
    }

    // Build whole pie Raphael set
    this.setStart();
    for (var i = 0; i < data.length; i++) {
        var part = data[i].value / sum;
        processSector(angle, angle + 360*part, i, data[i]);
        angle += 360*part;
    }
    var pie = this.setFinish();

    pie.attr({
        "stroke-width": 0,
        "stroke": "#fff"
    });

    return pie;
};