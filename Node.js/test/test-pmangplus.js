'use strict';

var request = require('request');
var assert = require('assert');
var async = require('async');
var sha1 = require('sha1');

// application identifier
var APP_ID = 'pmang app id';
var APP_KEY = 'pmang app key';
var APP_SECRET = 'pmang app secret';
var LOCAL_CD = 'KOR';
var DEVICE_CD = 'ANDROID';
//var DEVICE_CD = 'IPHONE'

// user identifier
var ID = 'pmang id';
var PW = 'pmang pw';

// payment identifier
var DEVELOPER_PAYLOAD = 'payment developer payload';
var PURCHASE_TOKEN = 'payment purchase token';

// QA/TQ/Live URL
var URL = 'http://qa.pmangplus.com';
//var URL = 'http://tq.pmangplus.com';
//var URL = 'http://pmangplus.com';


describe('P+ TestCase', function() {

	var access_token = '';
	var member_id = '';
	var nickname = '';
	var push_allow = false;

	before(function(done) {
		// login
		async.series([
			function (callback) {
				var options = post('/accounts/2/login');
				options.form = {
						'local_cd': LOCAL_CD,
						'app_id': APP_ID,
						'app_key': APP_KEY,
						'app_secret': APP_SECRET,
						'udid': 'TestUDID_Node',
						'email': ID,
						'passwd': PW,
						'device_cd': DEVICE_CD
				};

				request(options, function (error, response, body) {
					var res = get_response(error, response, body);
					assert.ok(res.value.access_token);
					assert.ok(res.value.member.member_id);
					assert.ok(res.value.member.nickname);

					access_token = res.value.access_token;
					member_id = res.value.member.member_id;
					nickname = res.value.member.nickname;

					done();
				});
			}
		], done);
	});


	it('test members @self', function(done) {
		async.series([
			function (callback) {
				var options = post('/members/@self');
				options.form = {
						'local_cd': LOCAL_CD,
						'access_token': access_token
				};
		
				request(options, function (error, response, body) {
					var res = get_response(error, response, body);

					assert.ok(res.value.has('crt_dt') && res.value.crt_dt);
					assert.ok(res.value.has('upd_dt') && res.value.upd_dt);
					assert.ok(res.value.has('status_cd') && res.value.status_cd);
					assert.deepStrictEqual(res.value.member_id, member_id);
					assert.deepStrictEqual(res.value.nickname, nickname);
					assert.ok(res.value.has('profile_img_url'));
					assert.ok(res.value.has('feeling'));
					assert.ok(res.value.has('adult_auth_yn') && res.value.adult_auth_yn);
					assert.ok(res.value.has('adult_auth_dt'));
					assert.ok(res.value.has('recent_login_dt'));
					assert.ok(res.value.has('recent_app_id'));
					assert.ok(res.value.has('email'));
					assert.ok(res.value.has('anonymous_yn'));
					assert.ok(res.value.has('reg_path'));
					assert.ok(res.value.has('recent_app_title'));
					assert.ok(res.value.has('last_msg_dt'));
					assert.ok(res.value.has('new_msg_yn'));
					assert.ok(res.value.has('friend_accept_cd'));
					assert.ok(res.value.has('achieve_detail_info_list'));
					assert.ok(res.value.has('member_achievement_summary'));
					assert.ok(res.value.has('summary'));
					assert.ok(res.value.has('options'));
					assert.ok(res.value.has('contact_require_phone_auth'));
					assert.ok(res.value.has('contact_require_import'));
					assert.ok(res.value.has('contact_friend_mapping'));
					assert.ok(res.value.has('contact_require_phone_auth_confirm'));
					assert.ok(res.value.has('contact'));
					assert.ok(res.value.has('pmang_usn'));
					assert.ok(res.value.has('ci'));
					assert.ok(res.value.has('sanction'));
					assert.ok(res.value.has('profile_img_url_raw'));

					done();
				});
			}
		], done);
	});


	it('test market effectuate', function(done) {
		async.series([
			function (callback) {
				var options = post('/market/v3/effectuate');
				options.form = {
					'local_cd': LOCAL_CD,
					'developerPayload': DEVELOPER_PAYLOAD,
					'purchaseToken': PURCHASE_TOKEN
				};

				request(options, function (error, response, body) {
					var res = get_response(error, response, body);

					// if success
					//assert.deepStrictEqual(res.value.result, 'SUCCESS');
					//assert.ok(res.value.has('store_product_id') && res.value.store_product_id);
					//assert.ok(res.value.has('pay_id') && res.value.pay_id);

					// if no success(FAILURE / DUPLICATED / NOTEXISTS)
					assert.ok(res.value.has('result') && res.value.result);

					done();
				});
			}
		], done);
	});


	it('test gcm publish', function(done) {
		async.series([
			function (callback) {
				// member_id,member_id,member_id
				var member_ids = [member_id, member_id, member_id];
				var options = post('/gcm/' + APP_ID + '/publish');
				options.form = {
					'local_cd': LOCAL_CD,
					'member_ids': member_ids.join(),
					'message': '{"content":"내용", "title":"제목", "type":"ad", "url":"http://ppl.cm/abcdef"}'
				};

				request(options, function (error, response, body) {
					var res = get_response(error, response, body);

					assert.ok(res.value);

					done();
				});
			}
		], done);
	});


	it('test gcm get allow', function(done) {
		async.series([
			function (callback) {
				var options = post('/gcm/' + APP_ID + '/getAllow');
				options.form = {
					'local_cd': LOCAL_CD,
					'access_token': access_token
				};

				request(options, function (error, response, body) {
					var res = get_response(error, response, body);

					assert.ok(res.hasOwnProperty('value'));

					done();
				});
			}
		], done);
	});


	it('test gcm set allow', function(done) {
		async.series([
			// get allow
			function (callback) {
				var options = post('/gcm/' + APP_ID + '/getAllow');
				options.form = {
					'local_cd': LOCAL_CD,
					'access_token': access_token
				};

				request(options, function (error, response, body) {
					var res = get_response(error, response, body);

					assert.ok(res.hasOwnProperty('value'));
					push_allow = res.value;

					done();
				});
			},
			// toggle allow
			function (callback) {
				var options = post('/gcm/' + APP_ID + '/setAllow');
				options.form = {
					'local_cd': LOCAL_CD,
					'access_token': access_token,
					'allow': !push_allow
				};

				request(options, function (error, response, body) {
					var res = get_response(error, response, body);

					assert.ok(res.hasOwnProperty('value'));
					assert.equal(!push_allow, res.value);

					done();
				});
			},
			// confirm allow
			function (callback) {
				var options = post('/gcm/' + APP_ID + '/getAllow');
				options.form = {
					'local_cd': LOCAL_CD,
					'access_token': access_token
				};

				request(options, function (error, response, body) {
					var res = get_response(error, response, body);

					assert.ok(res.hasOwnProperty('value'));
					assert.equal(!push_allow, res.value);

					done();
				});
			}
		], done);
	});


	it('test apns publish', function(done) {
		async.series([
			function (callback) {
				// member_id,member_id,member_id
				var member_ids = [member_id, member_id, member_id];
				var options = post('/apns/' + APP_ID + '/publish');
				options.form = {
					'local_cd': LOCAL_CD,
					'member_ids': member_ids.join(),
					'message': 'my message'
				};

				request(options, function (error, response, body) {
					var res = get_response(error, response, body);

					assert.ok(res.value);

					done();
				});
			}
		], done);
	});


	it('test apns get allow', function(done) {
		async.series([
			function (callback) {
				var options = post('/apns/' + APP_ID + '/getAllow');
				options.form = {
					'local_cd': LOCAL_CD,
					'access_token': access_token
				};

				request(options, function (error, response, body) {
					var res = get_response(error, response, body);

					assert.ok(res.hasOwnProperty('value'));

					done();
				});
			}
		], done);
	});


	it('test apns set allow', function(done) {
		async.series([
			// get allow
			function (callback) {
				var options = post('/apns/' + APP_ID + '/getAllow');
				options.form = {
					'local_cd': LOCAL_CD,
					'access_token': access_token
				};

				request(options, function (error, response, body) {
					var res = get_response(error, response, body);

					assert.ok(res.hasOwnProperty('value'));
					push_allow = res.value;

					done();
				});
			},
			// toggle allow
			function (callback) {
				var options = post('/apns/' + APP_ID + '/setAllow');
				options.form = {
					'local_cd': LOCAL_CD,
					'access_token': access_token,
					'allow': !push_allow
				};

				request(options, function (error, response, body) {
					var res = get_response(error, response, body);

					assert.ok(res.hasOwnProperty('value'));
					assert.equal(!push_allow, res.value);

					done();
				});
			},
			// confirm allow
			function (callback) {
				var options = post('/apns/' + APP_ID + '/getAllow');
				options.form = {
					'local_cd': LOCAL_CD,
					'access_token': access_token
				};

				request(options, function (error, response, body) {
					var res = get_response(error, response, body);

					assert.ok(res.hasOwnProperty('value'));
					assert.equal(!push_allow, res.value);

					done();
				});
			}
		], done);
	});
	
});


function make_header() {
	var now = Date.now();
	return {
		'ver': 4,
		'ts': now,
		'fp': sha1(now + APP_SECRET),
		'User-Agent': 'PmangPlus for Server',
		'Content-Type': 'application/json;charset=UTF-8'};
}

function post(prefix) {
	return {
		url: URL + prefix,
		method: 'POST',
		headers: make_header()
	}
}

function get(prefix) {
	return {
		url: URL + prefix,
		method: 'GET',
		headers: make_header()
	}
}

function get_response(error, response, body) {
	if (error) assert.fail(error);
	if (response.statusCode != 200) assert.fail(response);

	var res = JSON.parse(body);
	if (typeof res.value == 'object') {
		res.value.has = function (name) {
			return this.hasOwnProperty(name);
		};
	}

	assert.deepStrictEqual(res.result_code, '000');
	assert.deepStrictEqual(res.result_msg, 'API_OK');

	return res;
}
