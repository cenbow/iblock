/*var express = require('express');
http = require('http');
var app = express();

app.configure(function () {
    app.use(express.methodOverride());
    app.use(express.bodyParser());
    app.use(function(req, res, next) {
      res.header("Access-Control-Allow-Origin", "*");
      res.header("Access-Control-Allow-Headers", "X-Requested-With");
      next();
    });
    app.use(app.router);
});

app.configure('development', function () {
    app.use(express.static(__dirname + '/'));
    app.use(express.errorHandler({ dumpExceptions: true, showStack: true }));
});

app.configure('production', function () {
    app.use(express.static(__dirname + '/'));
    app.use(express.errorHandler());
});*/

var express = require('express');

http = require('http');
var app = express();







app.use('/login.html', express.static(__dirname + '/login.html'));

app.use('/bower_components', express.static(__dirname + '/bower_components'));
app.use('/app', express.static(__dirname + '/app'));
app.use('/css', express.static(__dirname + '/css'));
app.use('/img', express.static(__dirname + '/img'));
app.use('/service', express.static(__dirname + '/service'));

//app.post('/service', express.static(__dirname + '/service'));

app.post(/\/service\/.*/, function(req, res, next) {
  res.sendFile (req.path, { root: __dirname});
  res.setHeader('content-type', 'application/json');
  
})

app.all('/signup/*', function(req, res, next) {
    // Just send the index.html for other files to support HTML5Mode
    res.sendFile('visitor.html', { root: __dirname });
});


app.all('/*', function(req, res, next) {
    // Just send the index.html for other files to support HTML5Mode
    res.sendFile('index.html', { root: __dirname });
});


http.createServer(app).listen(8000, function () {
  console.log('Express server listening on port ' + 8000);
});