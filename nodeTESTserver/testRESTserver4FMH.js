//modules
var express = require('express');
var app = express();
var bodyParser = require("body-parser");
var fs = require("fs");
var basicAuth = require('basic-auth-connect');


//constants
var dirNAME='RECORDS/';
var nameFIN='_in.txt';
var nameFOUT='_out.txt';
var nameFLATE='_late.txt';
var lateHr=8;
var lateMn=40;
var password='misha';
var user='FMH';



app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(basicAuth(user, password));


//creating REST api
app.post('/reg', function (req, res) {
	//writing result on the screen
      	console.log('reseived! '+req.body.code + ' '+req.body.inout+ ' '
      	+(new Date(req.body.date)).toString());
	//writing result to the file
      	writeFile(req.body.code, req.body.inout, (new Date(req.body.date)).getHours(), (new Date(req.body.date)).getMinutes());
      	res.send('ok');
});



//starting server
var server = app.listen(8082, function () {
 console.log("Listening on localhost  and port %s...", server.address().port);
});





//writing to file
function writeFile(code, inout, hr, min){
	//check for error or missing data
	if (!code || !inout) return;
	if (hr>24 || min >60) return;
	if (hr<0 || min <0) return;
	if ((!inout.startsWith('in'))&&(!inout.startsWith('out'))) return;

	//creating text line of formated text
	function concatMe(code, inout, hr, min){
		var smin=min<10?'0':"";
		smin+=min;
		return nameF, code+" "+inout+" "+hr+":"+smin+"\n";
	}
	
	//creating a title for a table in the file
	function writeTitle(inout, hr, min){
		var sinout;
		if (inout.startsWith('in'))
			if (isLate(hr, min)) sinout='been LATE';
				else sinout='ckecking IN';
		else sinout='checking OUT';
		return 'The list of '+sinout+ ' students of '+getNowDate()+
			    "\n\n -------------------------------------------------------\n";
	}
	
	
	//creating the file name
	var nameF;
	if (inout.startsWith('out')) nameF=dirNAME+getNowDate()+nameFOUT;
	    else if (isLate(hr, min)) nameF=dirNAME+getNowDate()+nameFLATE; 
		else nameF=dirNAME+getNowDate()+nameFIN;
	
	
	//checking for existence of the file    
	fs.exists(nameF, (exists) => {
  		if (exists) {
    			fs.appendFile(nameF, concatMe(code, inout, hr, min), (err, fd) => {if (err) throw err;});
  		} else {
			fs.writeFile(nameF, writeTitle(inout, hr, min)+concatMe(code, inout, hr, min), (err, fd) => {if (err) throw err;});
		}
		console.log('written to file '+nameF);
	});
}


//taking the current date for forming the file name
function getNowDate(){
	var date = new Date();
	return date.getDate()+"_"+date.getMonth()+"_"+date.getFullYear();
}


//checking if one is late
function isLate(hr, min){
    return ((hr>lateHr) || ((hr==lateHr) && (min>lateMn)));
}

