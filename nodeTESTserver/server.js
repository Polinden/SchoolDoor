#!/bin/env node

//modules
var express = require('express');
var app = express();
var bodyParser = require("body-parser");
var fs = require("fs");
var basicAuth = require('basic-auth-connect');
var nameparser = require('./nameparser.js');


//constants
var dirNAME='RECORDS/';
var nameFIN='_in.txt';
var nameFOUT='_out.txt';
var nameFLATE='_late.txt';
//settings for been late and reporting
var lateHr=8;
var lateMn=40;
var reportHr=16;
//security options
var password='misha';
var user='FMH';

//openShiftConst
var server_port = process.env.OPENSHIFT_NODEJS_PORT || 8082
var server_ip_address = process.env.OPENSHIFT_NODEJS_IP || '127.0.0.1'
//time zone corrector
var timeCorrHr=7;
var timeCorr=(3600000*timeCorrHr);

//statistics
//people counter
var count=0;
//array of checked in
var checkedIn=[];


app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(basicAuth(user, password));


//creating REST api
app.post('/reg', function (req, res) {
    //writing result on the screen
        console.log('reseived! '+req.body.code + ' '+req.body.inout+ ' '
        +(new Date(req.body.date+timeCorr)).toString());
    //writing result to the file
        writeFile(req.body.code, req.body.inout, (new Date(req.body.date+timeCorr)).getHours(), (new Date(req.body.date)).getMinutes());
        res.send('ok');
});


app.get('/count', function(req, res){
        res.send(''+count);
});


app.get('/test', function(req, res){
        res.send(checkedIn.toString());
});


//starting server
var server = app.listen(server_port, server_ip_address, function () {
 console.log("Listening on localhost  and port %s...", server.address().port);
});





//writing to file
function writeFile(code, inout, hr, min){
    //check for error or missing data
    if (!code || !inout) return;
    if (hr>24 || min >60) return;
    if (hr<0 || min <0) return;
    if ((strcmp(inout,'in')!=0)&&(strcmp(inout,'out')!=0)) return;

    //creating text line of formated text
    function concatMe(code, inout, hr, min){
	var smin=min<10?'0':"";
	smin+=min;
	//find student name by code
	var scode=nameparser.findByname(code);
	return scode+" "+inout+" "+hr+":"+smin+"\n";
    }
    
    //creating a title for a table in the file
    function writeTitle(inout, hr, min){
	var sinout;
	//initiate new day counter
	initCount(inout);
	if (strcmp(inout,'in')==0)
	    if (isLate(hr, min)) sinout='been LATE';
		else sinout='ckecked IN';
	else sinout='checked OUT';
	return 'The list of '+sinout+ ' students of '+getNowDate()+
	        "\n\n -------------------------------------------------------\n";
    }
    
    
    //return if we already checked in
    if (ifCheckedIn(code, inout)) return;
    
    
    //creating the file name
    var nameF;
    if (strcmp(inout,'out')==0) nameF=dirNAME+getNowDate()+nameFOUT;
        else if (isLate(hr, min)) nameF=dirNAME+getNowDate()+nameFLATE; 
	else nameF=dirNAME+getNowDate()+nameFIN;
    
    
    //checking for existence of the file    
    fs.exists(nameF, function (exists) {
	if (exists) {
		fs.appendFile(nameF, concatMe(code, inout, hr, min), function (err, fd) {if (err) console.log(err);});
	} else {
	    fs.writeFile(nameF, writeTitle(inout, hr, min)+concatMe(code, inout, hr, min), function (err, fd) {if (err) console.log(err);});
	}
	//do count students
	countStudents(inout);
	addtoChecked(code, inout);
	console.log('written to file '+nameF);
    });
}


//taking the current date for forming the file name
function getNowDate(){
    var date = new Date(new Date().getTime()+timeCorr);
    return date.getDate()+"_"+date.getMonth()+"_"+date.getFullYear();
}


//checking if one is late
function isLate(hr, min){
    return ((hr>lateHr) || ((hr==lateHr) && (min>lateMn)));
}


//helper for compering strings
function strcmp(a, b) {
    if (a.toString() < b.toString()) return -1;
    if (a.toString() > b.toString()) return 1;
    return 0;
}


//for counting students
function countStudents(inout){	
    if (strcmp(inout,'in')==0) count++;
	else count--;
    if (count<0) count=0;
}

//set counter to null
function initCount(inout){
    if(strcmp(inout, 'in')!=0) return; 
    count=0;
    checkedIn=[];
}

//add/remove to/form checked in array
function addtoChecked(code, inout){
    if (strcmp(inout, 'in')==0) {checkedIn.push(code); return;}
    var ind = checkedIn.indexOf(code);
    if (ind<0) return;
    checkedIn.splice(ind, 1);
}

//check if is already cheched in/out
function ifCheckedIn(code, inout){
    if (strcmp(inout, 'in')===0) return (checkedIn.indexOf(code)>=0);
    return (checkedIn.indexOf(code)<0);
}

