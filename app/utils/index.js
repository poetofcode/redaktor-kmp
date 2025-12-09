const winston = require('winston');
const expressWinston = require('express-winston');
const fs = require('fs');
const util = require('util');
const { dirname } = require('path');
const crypto = require('crypto');

const { createLogger, format, transports } = winston;
const { combine, timestamp, label, printf } = format;


const appDir = dirname(require.main.filename);

const ignoreList = [
  '/api/v1/sessions/',
  '/style/',
  '/lib/',
  '/script/',
  '/front',
];

function logger() {
	const myFormat = printf(({ level, message, timestamp }) => {
		const parsedDate = new Date(timestamp);
		const dateFormatted = parsedDate.toISOString().
				replace(/T/, ' ').
				replace(/\..+/, '');
	  return `${dateFormatted} ${level}: ${message}`;
	});
	return expressWinston.logger({
      transports: [
        new winston.transports.Console()
      ],
      format: winston.format.combine(
      	winston.format.timestamp(),
        winston.format.colorize(),
        myFormat
      ),
      meta: false,
      msg: "{{req.method}} {{req.url}} {{res.responseTime}}ms",
      expressFormat: true,
      colorize: true,
      ignoreRoute: function (req, res) { 
        let result = false;      
        ignoreList.forEach((item) => {
          if (req.url.includes(item)) {
            result = true;
          };
        });
        return result; 
      }
    })
}

function wrapResult(data) {
  return {
    result: data
  }
}

function wrapError(error) {
  return {
    error: error.message || "unknown",
    code: error.code || -1,
  }
}

function buildError(status, description, code) {
  const err = new Error(description);
  if (code) {
    err.code = code;
  }
  err.status = status;
  return err;
}

function escapeHtml(unsafe) {
    return unsafe
         .replace(/&/g, "&amp;")
         .replace(/</g, "&lt;")
         .replace(/>/g, "&gt;")
         .replace(/"/g, "&quot;")
         .replace(/'/g, "&#039;");
}

async function requireAll(path) {
  const readdir = util.promisify(fs.readdir);
  const files = (await readdir(`${appDir}/${path}`)).filter((file) => file != 'index.js');

  const loaded = { all: [] };
  files.forEach((file) => {
    const cleanFileName = file.replace('.js', '');
    const required = require(`${appDir}/${path}/${file}`);
    loaded[cleanFileName] = required;
    loaded.all.push( { name: cleanFileName, value: required} );
  });

  loaded.forEach = function(cb) {
    loaded.all.forEach((pair) => {
      cb(pair.name, pair.value);
    })
  }

  loaded.map = function(cb) {
    return loaded.all.map((pair) => {
      return cb(pair.name, pair.value);
    })
  }

  return loaded;
}


function sha1(arg) {
  var shasum = crypto.createHash('sha1');
  shasum.update(arg);
  return shasum.digest('hex').toString();
}


var emailRegex = /^[-!#$%&'*+\/0-9=?A-Z^_a-z{|}~](\.?[-!#$%&'*+\/0-9=?A-Z^_a-z`{|}~])*@[a-zA-Z0-9](-*\.?[a-zA-Z0-9])*\.[a-zA-Z](-?[a-zA-Z0-9])+$/;

function isEmailValid(email) {
    if (!email)
        return false;

    if(email.length>254)
        return false;

    var valid = emailRegex.test(email);
    if(!valid)
        return false;

    // Further checking of some things regex can't handle
    var parts = email.split("@");
    if(parts[0].length>64)
        return false;

    var domainParts = parts[1].split(".");
    if(domainParts.some(function(part) { return part.length>63; }))
        return false;

    return true;
}

function setIntervalImmediately(cb, intervalMs) {
  cb();
  return setInterval(cb, intervalMs);
}

function deepCopy(stuff) {
  return JSON.parse(JSON.stringify(stuff));
}


exports.utils = {
  appDir: appDir,
	logger: logger,
  wrapResult: wrapResult,
  wrapError: wrapError,
  buildError: buildError,
  escapeHtml: escapeHtml,
  requireAll: requireAll,
  sha1: sha1,
  isEmailValid: isEmailValid,
  setIntervalImmediately: setIntervalImmediately,
  deepCopy: deepCopy
}
