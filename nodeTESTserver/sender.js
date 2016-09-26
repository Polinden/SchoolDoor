//modules
var schedule = require('node-schedule');
var nodemailer = require('nodemailer');
var fs = require('fs')


//for specific server timeZoneSettings
var timeCorrHr=7;

//reportHorsettings
var reportHr=16;


//schedule actions for sendig emails
var rule = new schedule.RecurrenceRule();
rule.dayOfWeek = [0, new schedule.Range(1, 5)];
//rule.hour = reportHr+timeCorrHr;
rule.minute = [15, 30, 45, 00]; //0;
var j = schedule.scheduleJob(rule, function(){
  console.log('Sending e-mail to FM!...');
  sendMeReport();
});


//email settings
var transporter = nodemailer.createTransport('smtps://mir4vam%40gmail.com:DanLee1973@smtp.gmail.com');
 
 
 
// setup e-mail data with unicode symbols 
var mailOptions = {
    from: 'mir4vam@gmail.com', // sender address 
    to: 'delishki@ukr.net', // list of receivers 
    subject: 'Hello', // Subject line 
    text: 'error reading file' // plaintext body 
    //html: '<b>Hello world</b>' // html body 
};
 
 
// send mail with defined transport object
function sendMeReport(){ 
    readFile();
    transporter.sendMail(mailOptions, function(error, info){
	if(error){
    	    return console.log(error);
	}
	console.log('E-mail sent: ' + info.response);
    });
}


//read file to report via email
function readFile(){
    mailOptions['text']=fs.readFileSync('RECORDS/26_8_2016_out.txt');
}
