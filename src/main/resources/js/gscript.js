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
        destination[prop] = source[prop]
    }
}

//gSassert(true);
//console.log('gSfails->'+gSfails);
//gSassert(false);
//console.log('gSfails->'+gSfails);
