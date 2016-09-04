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

    for(var i=0; i<200; i++) {
        var sRGB = ToRGB(i/200).toString(16);
        var nRGB = "";
        for(j=sRGB.length; j<6; j++) {
            nRGB += "0";
        }
        nRGB += sRGB;
        ctx.fillStyle = "#"+nRGB;
        console.log(ToRGB(i/200).toString(16));
        ctx.fillRect(i+cx, cy, 1, 40);
    }
}

function ToRGB(num)
{

    var R;
    var G;
    var B;

    if (num >= 0.0 && num < 0.25) {
        R = 0;
        G = num * 1023;
        B = 255;
    }
    else if (num >= 0.25 && num < 0.5) {
        R = 0;
        G = 255;
        B = (0.5-num) * 1023;
    }
    else if (num >= 0.5 && num < 0.75) {
        R = (num-0.5) * 1023;
        G = 255;
        B = 0;
    }
    else if (num >= 0.75 && num <= 1.0) {
        R = 255;
        G = (1-num)*1023;
        B = 0;
    }

    R = Math.floor(R);
    G = Math.floor(G);
    B = Math.floor(B);

    return ((R << 16) | (G << 8) | (B));
}
