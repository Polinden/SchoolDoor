//modules
var nodemailer = require('nodemailer');
var fs = require('fs')


//how much Kiev time is bigger then time where server is located
var timeCorrHr=7;

//reportHorsettings
var reportHr=14;


//email settings
var transporter = nodemailer.createTransport('smtps://formmasterhelper%40gmail.com:Krokozyabla2002@smtp.gmail.com');
 
 
//constants
var dirNAME='RECORDS/';
var nameFIN='_in.txt';
var nameFOUT='_out.txt';
var nameFLATE='_late.txt';


  
//schedule actions for sendig emails
var date = new Date();
var hr = date.getHours();
var min = date.getMinutes();
if ((corrHr(hr)==reportHr) && (min<15)) {
	sendMeReport(getINfileName(), "Attendance report CAME IN TIME 9-B");
	sendMeReport(getLATEfileName(), "Attendance report WERE LATE 9-B");
	sendMeReport(getOUTfileName(), "Attendance report GOT OUT 9-B");
} 
 
 
 
// send mail with defined transport object
function sendMeReport(name, tipics){

	// setup e-mail data with unicode symbols
	var mailOptions = {
    	from: 'formmasterhelper@gmail.com', // sender address
    	to: 'delishki@ukr.net', // list of receivers
    	subject: tipics, // Subject line
    	text: 'error reading file' // plaintext body
	};
 
 
    if (!fs.existsSync(name)) {console.log('file is absent '+name); return;}
    
    //if file of report exists then send it
    fs.readFile(name, function (err, data) {
	if (err) {console.log(err); return};
	mailOptions['text']=data;
        transporter.sendMail(mailOptions, function(error, info){
	    if(error){console.log(error); return;}
	    console.log('E-mail sent: ' + info.response);
	});
    });
}



//applying timeone for ReportHr
function corrHr(hr){
    var tHr=hr+timeCorrHr;
    if (tHr>24) tH=tHr-24;
    return tHr;
}


//getting date for new timeZone
function corrDate(){
    var date = new Date();
    var sec = date.getTime();
    sec = sec + timeCorrHr*3600000;
    return new Date(sec);
}


//taking the current date for forming the file name
function getNowDate(){
    var date = corrDate();
    return date.getDate()+"_"+date.getMonth()+"_"+date.getFullYear();
}


//getfileNames
function getOUTfileName(){
    return dirNAME+getNowDate()+nameFOUT;
}
function getLATEfileName(){
    return dirNAME+getNowDate()+nameFLATE;
}
function getINfileName(){
    return dirNAME+getNowDate()+nameFIN;
}

