
//file with students
var FILENAME = 'kinderlist.json';
var parserlist;

//modules
fs=require('fs');


(function(){
fs.readFile(FILENAME, function (err, data){
   if(err) {console.log(err); return;}
   parserlist=JSON.parse(data);
})


console.log('Start parsing module ...');
})()




//function to translate numbers to last names
exports.findByname = function (code){
   var pcode = parserlist[code];
   if (pcode ==null) return code;    
   return pcode;
}

