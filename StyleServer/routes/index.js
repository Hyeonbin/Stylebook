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
  database: 'styledatabase',
  password: 'xpfksWkd'
});

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.get('/stylelist', function(req, res, next) {

  pool.getConnection(function (err, connection) {
    var sqlForSelect = "SELECT * FROM stylelistdata ORDER BY id DESC";
    var sqlForSelect1 = "SELECT * FROM likedata ORDER BY listid DESC";
    var sqlForSelect2 = "SELECT * FROM commentdata";
    var sqlForSelect3 = "SELECT * FROM profiledata";

    connection.query(sqlForSelect, function(err, stylelistdata) {
      if(err)
        console.error("err : ", err);

      connection.query(sqlForSelect1, function(err, likedata) {
        if(err)
          console.error("err : ", err);

        connection.query(sqlForSelect2, function(err, commentdata) {
          if(err)
            console.error("err : ", err);

          connection.query(sqlForSelect3, function(err, profiledata) {
            if(err)
              console.error("err : ", err);

            var sendData = [{stylelistdata, likedata, commentdata, profiledata}];
            res.json(sendData);
            console.log(JSON.stringify(sendData));
            connection.release();
          });
        });
      });
    });
  });
});

router.get('/stylelist/:imagename', function(req, res, next){
  var imagename = req.params.imagename;

  res.sendFile(path.resolve('./image/stylelistimg/' + imagename));
});

router.post('/stylelist/addstyle', function(req, res, next) {
  var facebookid = req.body.facebookid;
  var imagename = req.body.imagename;
  var text = req.body.text;
  var time = req.body.time;

  pool.getConnection(function(err, connection) {
    var sqlForInsert = "INSERT INTO stylelistdata (facebookid, imagename, text, time) VALUES (?, ?, ?, ?)";
    var postData = [facebookid, imagename, text, time];

    connection.query(sqlForInsert, postData, function(err, result) {
      if(err)
        console.error("err : ", err);

      console.log("result : " + JSON.stringify(result));
      connection.release();
    });
  });
});

router.post('/stylelist/addimage', function(req, res, next) {
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

     var writeStream = fs.createWriteStream('./image/stylelistimg/'+filename);
     writeStream.filename = filename;
     part.pipe(writeStream);

     part.on('data',function(chunk){
       console.log(filename + ' read ' + chunk.length + 'bytes');
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

router.post('/stylelist/likebtnclicked', function(req, res, next) {
  var listid = req.body.listid;
  var facebookid = req.body.facebookid;
  var time = req.body.time;

  pool.getConnection(function(err, connection) {
    var sqlForInsert = "INSERT INTO likedata (listid, facebookid, time) VALUES (?, ?, ?)";
    var postData = [listid, facebookid, time];

    connection.query(sqlForInsert, postData, function(err, result) {
      if(err)
        console.error("err : ", err);

      console.log("result : " + JSON.stringify(result));
      connection.release();
    });
  });
});

router.post('/stylelist/likebtnunclicked', function(req, res, next) {
  var listid = req.body.listid;
  var facebookid = req.body.facebookid;

  pool.getConnection(function(err, connection) {
    var sqlForDelete = "DELETE FROM likedata WHERE listid = '" + listid + "' AND facebookid = '" + facebookid + "'";

    connection.query(sqlForDelete, function(err, result) {
      if(err)
        console.error("err : ", err);

      console.log("result : " + JSON.stringify(result));
      connection.release();
    });
  });
});

router.get('/stylelist/likenum', function(req, res, next) {
  var listid = req.body.listid;

  pool.getConnection(function(err, connection){
    var sqlForSelect = "SELECT * FROM likedata WHERE listid = '" + listid + "'";

    connection.query(sqlForSelect, function(err, data) {
      if(err)
        console.error("err : ", err);

      console.log("result : " + JSON.stringify(data));
      res.json(data);
      connection.release();
    });
  });
});

router.post('/stylelist/comment', function(req, res, next) {
  var listid = req.body.listid;

  pool.getConnection(function(err, connection){
    var sqlForSelect = "SELECT * FROM commentdata WHERE listid = '" + listid + "' ORDER BY id DESC";
    var sqlForSelect1 = "SELECT * FROM profiledata";

    connection.query(sqlForSelect, function(err, commentdata) {
      if(err)
        console.error("err : ", err);

      connection.query(sqlForSelect1, function(err, profiledata) {
        if(err)
          console.error("err : ", err);

        var postData = [{commentdata, profiledata}];
        console.log("result : " + JSON.stringify(postData));
        res.json(postData);
        connection.release();
      });
    });
  });
});

router.post('/stylelist/addcomment', function(req, res, next) {
  var listid = req.body.listid;
  var facebookid = req.body.facebookid;
  var text = req.body.text;
  var time = req.body.time;

  var postData = [listid, facebookid, text, time];

  pool.getConnection(function(err, connection) {
    var sqlForInsert = "INSERT INTO commentdata (listid, facebookid, text, time) VALUES (?, ?, ?, ?)";

    connection.query(sqlForInsert, postData, function(err, result) {
      if(err)
        console.error("err : ", err);

      console.log(JSON.stringify(result));
      connection.release();
    });
  });
});

router.get('/stylelist/modifystyle', function(req, res, next) {
  var listid = req.body.listid;

  pool.getConnection(function(err, connection) {
    var sqlForSelect = "SELECT * FROM stylelistdata WHERE id = '" + listid + "'";

    connection.query(sqlForSelect, function(err, data) {
      if(err)
        console.error("err : ", err);

      res.json(data);
      console.log("result : " + JSON.stringify(data));
      connection.release();
    });
  });
});

router.post('/stylelist/modifystyle', function(req, res, next) {
  var listid = req.body.listid;
  var imagename = req.body.imagename;
  var text = req.body.text;
  var delimgsig = req.body.delimgsig;

  pool.getConnection(function(err, connection) {
    var sqlForUpdate = "UPDATE stylelistdata SET imagename = '" + imagename + "', text = '" + text + "' WHERE id = '" + listid + "'";
    var sqlForSelect = "SELECT * FROM stylelistdata WHERE id = '" + listid + "'";

    if(delimgsig == 0){
      connection.query(sqlForUpdate, function(err, result) {
        if(err)
          console.error("err : ", err);

        console.log(JSON.stringify(result));
        connection.release();
      });
    } else {
      connection.query(sqlForSelect, function(err, data) {
        if(err)
          console.error("err : ", err);

        console.log(JSON.stringify(data));
        var oldimagename = data[0].imagename;
        fs.unlink("./image/stylelistimg/" + oldimagename, function(err) {
          if(err)
            console.error("err : ", err);

          console.log("./image/stylelistimg/" + oldimagename + "is modified!");
        });
          connection.query(sqlForDelete, function(err, result) {
            if(err)
              console.error("err : ", err);

            console.log(JSON.stringify(result));
            connection.release();
          });
      });
    }
  });
});

router.post('/stylelist/deletestyle', function(req, res, next) {
  var listid = req.body.listid;

  pool.getConnection(function(err, connection) {
    var sqlForSelect = "SELECT * FROM stylelistdata WHERE id = '" + listid + "'";
    var sqlForDelete = "DELETE FROM stylelistdata WHERE id = '" + listid + "'";
    var sqlForDelete1 = "DELETE FROM commentdata WHERE listid = '" + listid + "'";

    connection.query(sqlForSelect, function(err, data) {
      if(err)
        console.error("err : ", err);

      console.log(JSON.stringify(data));

      var imagename = data[0].imagename;
      fs.unlink("./image/stylelistimg/" + imagename, function(err) {
        if(err)
          console.error("err : ", err);

        console.log("./image/stylelistimg/" + imagename + "is deleted!");
      });
      connection.query(sqlForDelete, function(err, result) {
        if(err)
          console.error("err : ", err);

        console.log(JSON.stringify(result));

        connection.query(sqlForDelete1, function(err, result) {
          if(err)
            console.error("err : ", err);

          console.log(JSON.stringify(result));
          connection.release();
        });
      });
    });
  });
});

router.post('/profile', function(req, res, next) {
  var facebookid = req.body.facebookid;

  pool.getConnection(function(err, connection) {
    var sqlForSelect = "SELECT * FROM profiledata WHERE facebookid = '" + facebookid + "'";
    var sqlForSelect1 = "SELECT * FROM stylelistdata WHERE facebookid = '" + facebookid + "' ORDER BY id DESC";
    var sqlForSelect2 = "SELECT * FROM likedata";
    var sqlForSelect3 = "SELECT * FROM commentdata";

    connection.query(sqlForSelect, function(err, profiledata) {
      if(err)
        console.error("err : ", err);

      connection.query(sqlForSelect1, function(err, stylelistdata) {
        if(err)
          console.error("err : ", err);

        connection.query(sqlForSelect2, function(err, likedata) {
          if(err)
            console.error("err : ", err);

            connection.query(sqlForSelect3, function(err, commentdata) {
              if(err)
                console.error("err : ", err);

              var sendData =[{profiledata, stylelistdata, likedata, commentdata}];
              res.json(sendData);
              console.log(JSON.stringify(sendData));
              connection.release();
          });
        });
      });
    });
  });
});

router.post('/profile/addprofile', function(req, res, next) {
  var facebookid = req.body.facebookid;
  var name = req.body.name;
  var profileimage = req.body.profileimage;
  var location = req.body.location;
  var style = req.body.style;
  var text = req.body.text;

  var postData = [facebookid, name, profileimage, location, style, text];

  pool.getConnection(function(err, connection) {
    var sqlForSelect = "SELECT * FROM profiledata WHERE facebookid = '" + facebookid + "'";
    var sqlForInsert = "INSERT INTO profiledata (facebookid, name, profileimage, location, style, text) VALUES (?, ?, ?, ?, ?, ?)";

    connection.query(sqlForSelect, function(err, data) {
      if(err)
        console.error("err : ", err);

      console.log(JSON.stringify(data));
      if(data == null){
        connection.query(sqlForInsert, postData, function(err, result) {
          if(err)
            console.error("err : ", err);

          console.log(JSON.stringify(result));
          connection.release();
        });
      } else {
        connection.release();
      }
    });
  });
});

router.post('/profile/modifyprofile', function(req, res, next) {
  var facebookid = req.body.facebookid;
  var name = req.body.name;
  var profileimage = req.body.profileimage;
  var location = req.body.location;
  var style = req.body.style;
  var text = req.body.text;
  var delimgsig = req.body.delimgsig;

  pool.getConnection(function(err, connection) {
    var sqlForUpdate = "UPDATE profiledata SET name = '" + name + "', profileimage = '" + profileimage + "', location = '" + location + "', style = '" + style + "', text = '" + text + "' WHERE facebookid = '" + facebookid + "'";
    var sqlForSelect = "SELECT * FROM profiledata WHERE facebookid = '" + facebookid + "'";

    if(delimgsig == 0) {
      connection.query(sqlForUpdate, function(err, result) {
        if(err)
          console.error("err : ", err);

        console.log(JSON.stringify(result));
        connection.release();
      });
    } else {
      connection.query(sqlForSelect, function(err, data) {
        if(err)
          console.error("err : ", err);

        var oldprofileimagename = data[0].profileimagename;
        fs.unlink("./image/profileimg/" + oldprofileimagename, function(err) {
          if(err)
            console.error("err : ", err);

          console.log("./image/profileimg/" + oldprofileimagename + "is modified!");
        });

        connection.query(sqlForUpdate, function(err, result) {
          if(err)
            console.error("err : ", err);

          console.log(JSON.stringify(result));
          connection.release();
        });
      });
    }
  });
});

router.post('/profile/addimage', function(req, res, next) {
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

     var writeStream = fs.createWriteStream('./image/profileimg/'+filename);
     writeStream.filename = filename;
     part.pipe(writeStream);

     part.on('data',function(chunk){
       console.log(filename + ' read ' + chunk.length + 'bytes');
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

router.get('/profile/:facebookid', function(req, res, next) {
  var facebookid = req.params.facebookid;

  res.sendFile(path.resolve('./image/profileimg/' + facebookid + '.jpg'));
});

router.post('/search', function(req, res, next) {
  var keyword = req.body.keyword;

  pool.getConnection(function(err, connection) {
    var sqlForSelect = "SELECT * FROM stylelistdata WHERE text LIKE '%" + keyword + "%' ORDER BY id DESC";
    var sqlForSelect1 = "SELECT * FROM likedata";
    var sqlForSelect2 = "SELECT * FROM commentdata";
    var sqlForSelect3 = "SELECT * FROM profiledata";

    connection.query(sqlForSelect, function(err, stylelistdata) {
      if(err)
        console.error("err : ", err);

      connection.query(sqlForSelect1, function(err, likedata) {
        if(err)
          console.error("err : ", err);

        connection.query(sqlForSelect2, function(err, commentdata) {
          if(err)
            console.error("err : ", err);

          connection.query(sqlForSelect3, function(err, profiledata) {
            if(err)
              console.error("err : ", err);

            var postData = [{stylelistdata, likedata, commentdata, profiledata}];
            res.json(postData);
            console.log("result: " + JSON.stringify(postData));
            connection.release();
          });
        });
      });
    });
  });
});

router.post('/like', function(req, res, next) {
  var facebookid = req.body.facebookid;

  pool.getConnection(function(err, connection) {
    var sqlForSelect = "SELECT * FROM likedata WHERE facebookid = '" + facebookid + "' ORDER BY id DESC";
    var sqlForSelect1 = "SELECT * FROM likedata";
    var sqlForSelect2 = "SELECT * FROM commentdata";
    var sqlForSelect3 = "SELECT * FROM stylelistdata ORDER BY id DESC";
    var sqlForSelect4 = "SELECT * FROM profiledata";

    connection.query(sqlForSelect, function(err, likedata) {
      if(err)
        console.error("err : ", err);

      connection.query(sqlForSelect1, function(err, likedataforstylelist) {
        if(err)
          console.error("err : ", err);

        connection.query(sqlForSelect2, function(err, commentdata) {
          if(err)
            console.error("err : ", err);

          connection.query(sqlForSelect3, function(err, stylelistdata) {
            if(err)
              console.error("err : ", err);

            connection.query(sqlForSelect4, function(err, profiledata) {
              if(err)
                console.error("err : ", err);

              var postData = [{likedata, likedataforstylelist, commentdata, stylelistdata, profiledata}];
              res.json(postData);
              console.log(JSON.stringify(postData));
              connection.release();
            });
          });
        });
      });
    });
  });
});

router.post('/like/stylelist', function(req, res, next) {
  var listid = req.body.listid;

  pool.getConnection(function(err, connection) {
    var sqlForSelect = "SELECT * FROM stylelistdata WHERE id = '" + listid + "'";

    connection.query(sqlForSelect, function(err, stylelistdata) {
      if(err)
        console.error("err : ", err);

      res.json(stylelistdata);
      console.log(JSON.stringify(stylelistdata));
      connection.release();
    });
  });
});

module.exports = router;
