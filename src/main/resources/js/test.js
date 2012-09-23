var fs = require('fs');

// file is included here:
eval(fs.readFileSync('gsclass.js')+'');

eval(fs.readFileSync('gscript.js')+'');

var a = gSlist([]);

console.log('Ready to Test!');

//////// Tests here




