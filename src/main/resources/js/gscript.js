//Javascript code needed for conversions
var gSfails = false;

function gSassert(value) {
    if(value==false) {
          gSfails = true;
          gSprintln('Assert Fails!-'+value)
    }
}

var gSconsole = ""

function gSprintln(value) {
    if (gSconsole != "") {
        gSconsole = gSconsole + "\n"
    }
    gSconsole = gSconsole + value
}

function gSpassMapToObject(source,destination) {
    for (prop in source) {
        if (typeof source[prop] === "function") continue;
        destination[prop] = source[prop]
    }
}

//gSassert(true);
//console.log('gSfails->'+gSfails);
//gSassert(false);
//console.log('gSfails->'+gSfails);
