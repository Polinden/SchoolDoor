//modules
var nodemailer = require('nodemailer');
var fs = require('fs')


//how much Kiev time is bigger then time where server is located
var timeCorrHr=7;

//reportHorsettings
var reportHr=16;



//email settings
var transporter = nodemailer.createTransport('smtps://NAME%40gmail.com:PASSWORD@smtp.gmail.com');
 
 
// setup e-mail data with unicode symbols 
var mailOptions = {
    from: 'NAMEgmail.com', // sender address 
    to: 'MASTER@ukr.net', // list of receivers 
    subject: 'Attendance report', // Subject line 
    text: 'error reading file' // plaintext body 
    //html: '<b>Hello world</b>' // html body 
};
 


//constants
var dirNAME='RECORDS/';
var nameFIN='_in.txt';
var nameFOUT='_out.txt';
var nameFLATE='_late.txt';


  
//schedule actions for sendig emails
var date = new Date();
var hr = date.getHours();
var min = date.getMinutes();
if ((corrHr(hr)>reportHr) && (min<30)) {
	sendMeReport(getOUTfileName());
} 
 
 
// send mail with defined transport object
function sendMeReport(name){ 
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
