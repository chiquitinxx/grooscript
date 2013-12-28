//var fs = require('fs');
// file is included here:
//eval(fs.readFileSync('grooscript.js')+'');
var gs = require('./grooscript.js');
gs.consoleOutput = true;
console.log('Ready to Test!');

/////////////////////////////////////////////////////////// Tests here



////////////////////////////////////////--------------------End Test here -> Resume

console.log('\nFails = '+gs.fails);
