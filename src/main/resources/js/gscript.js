//Javascript code needed for conversions
var gSfails = false;

function gSassert(value) {
    if(value==false) {
          gSfails = true;
          var message = 'Assert Fails! - ';
          //gSprintln('tam-'+arguments.length);
          if (arguments.length == 2 && arguments[1]!=null) {
            message = arguments[1] + ' - ';
          }
          gSprintln(message+value);
    }
};

var gSconsole = "";

function gSprintln(value) {
    if (gSconsole != "") {
        gSconsole = gSconsole + "\n"
    }
    gSconsole = gSconsole + value
};



//gSassert(true);
//console.log('gSfails->'+gSfails);
//gSassert(false);
//console.log('gSfails->'+gSfails);
