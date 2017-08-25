var express = require('express');
var router = express.Router();
var multiparty = require('multiparty');
var path = require('path');
var fs = require('fs');
var mysql = require('mysql');
var pool = mysql.createPool({
  connectionLimit: 5,
  host: 'localhost',
  user: 'root',
  database: 'servertest',
  password: 'hk7531'
});

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.get('/load', function(req, res, next) {
  /*fs.readFile('./uploads/tmp_1501405320848.jpg', function(error, data) {
    var insert = {
      image: data
    };
    var insertString = JSON.stringify(insert);
    res.sen(insertString);
  });*/
  res.sendFile(path.resolve('./uploads/tmp_1501405320848.jpg'));
});

router.post('/image', function(req, res, next){
  //res.send('Uploaded : ' + req.file);
  var form = new multiparty.Form();

  form.on('field', function(name, value) {
    console.log('normal field / name = ' + name + ' , value = ' + value);
  });

  form.on('part', function(part) {
    var filename;
    var size;
    if(part.filename) {
      filename = part.filename;
      size = part.byteCount;
    } else {
      part.resume();
    }

    console.log("Write Streaming file :"+filename);

     var writeStream = fs.createWriteStream('./uploads/'+filename);
     writeStream.filename = filename;
     part.pipe(writeStream);

     part.on('data',function(chunk){
       console.log(filename+' read '+chunk.length + 'bytes');
     });

     part.on('end',function(){
         console.log(filename+' Part read complete');
         writeStream.end();
     });
  });

  form.on('close',function(){
     res.status(200).send('Upload complete');
   });

  form.on('progress',function(byteRead,byteExpected){
       console.log('Reading total '+byteRead+'/'+byteExpected);
  });

  form.parse(req);
});

router.post('/name', function(req, res, next){

  var imagename = req.body.imagename;
  var name = req.body.name;
  var post = [imagename, name];

  pool.getConnection(function(err, connection) {
    var sqlForInsert = "INSERT INTO test (imagename, name) VALUES (?, ?)";
    connection.query(sqlForInsert, post, function(err, result){
      if(err)
        console.error("err : ", err);

      console.log("result : " + JSON.stringify(result));
      connection.release();
    });
  });
});


router.get('/namelist', function(req, res, next){

  pool.getConnection(function(err, connection) {
    var sqlForInsert = "SELECT * FROM test ORDER BY id DESC";
    connection.query(sqlForInsert, post, function(err, data){
      if(err)
        console.error("err : ", err);
      console.log("result : " + JSON.stringify(data));
      res.json(data);
      connection.release();
    });
  });
});

module.exports = router;
