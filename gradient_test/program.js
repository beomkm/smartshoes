window.onload = init;

var canvas;
var ctx;

function init()
{
   canvas = document.getElementById("canvas");
   ctx = canvas.getContext("2d");

   drawBar(ctx, 100, 100);
   drawMap(ctx);
}

function drawMap(ctx)
{
    var p1x = 150;
    var p1y = 120;
    var p2x = 150;
    var p2y = 300;

    for(var i=50; i<350; i++) {
        for(var j=50; j<250; j++) {
            var d1 = (j-p1x)*(j-p1x) + (i-p1y)*(i-p1y);
            var d2 = (j-p2x)*(j-p2x) + (i-p2y)*(i-p2y);
            var sRGB = intToColorstr(ToRGB(1));
            ctx.fillStyle = sRGB;
            ctx.fillRect(j, i, 1, 1);
        }
    }
}

function drawBar(ctx, cx, cy)
{

    for(var i=0; i<50; i++) {
        var sRGB = intToColorstr(ToRGB(i/50));
        ctx.fillStyle = sRGB;
        ctx.fillRect(i*4+cx, cy, 4, 40);
    }
}

function intToColorstr(num)
{
    var num16 = num.toString(16);
    var str = "#";
    for(j=num16.length; j<6; j++) {
        str += "0";
    }
    str += num16;
    return str;
}

function ToRGB(num)
{

    var R;
    var G;
    var B;

    if (num >= 0.0 && num < 0.30) {
        R = 0;
        G = num * 853;
        B = 255;
    }
    else if (num >= 0.30 && num < 0.5) {
        R = 0;
        G = 255;
        B = (0.5-num) * 1279;
    }
    else if (num >= 0.5 && num < 0.70) {
        R = (num-0.5) * 1279;
        G = 255;
        B = 0;
    }
    else if (num >= 0.70 && num <= 1.0) {
        R = 255;
        G = (1-num)*853;
        B = 0;
    }

    R = Math.floor(R);
    G = Math.floor(G);
    B = Math.floor(B);

    return ((R << 16) | (G << 8) | (B));
}
