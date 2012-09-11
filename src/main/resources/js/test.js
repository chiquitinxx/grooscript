var fs = require('fs');

// file is included here:
eval(fs.readFileSync('gsclass.js')+'');

eval(fs.readFileSync('gscript.js')+'');

var a = gSlist([]);

console.log('Ready to Test!');

//////// Tests here

myFairStringy = "The rain in Spain stays mainly in the plain!";
//BOUNDS = "\b";
//rhyme = "" + BOUNDS + "\w*ain" + BOUNDS + "";
found = "";
gSregExp(myFairStringy,"\\b\\w*ain\\b").each(function(match) {

//gSregExp(myFairStringy,/\w*ain\b/g).each(function(match) {
  return found += (match + " ");
});
console.log('Found->'+found);