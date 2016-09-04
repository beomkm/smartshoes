window.onload = init;

var canvas;
var ctx;

function init()
{
   canvas = document.getElementById("canvas");
   ctx = canvas.getContext("2d");

   drawBar(ctx, 100, 100);
}

function drawBar(ctx, cx, cy)
{

    for(var i=0; i<50; i++) {
        var sRGB = ToRGB(i/50).toString(16);
        var nRGB = "";
        for(j=sRGB.length; j<6; j++) {
            nRGB += "0";
        }
        nRGB += sRGB;
        ctx.fillStyle = "#"+nRGB;
        ctx.fillRect(i*4+cx, cy, 4, 40);
    }
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
