var express = require('express');
var app = express();
var bodyParser = require("body-parser");


app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));


app.post('/reg', function (req, res) {
      console.log('reseived! '+req.body.code + ' '+req.body.inout+ ' '
      +(new Date(req.body.date)).toString());
      res.send('ok');
});



var server = app.listen(8082, function () {
 console.log("Listening on localhost  and port %s...", server.address().port);
});



