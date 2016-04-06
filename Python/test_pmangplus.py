# -*- coding: utf-8 -*-

import unittest
import json
import requests
import time
import hashlib
import random
import socket

# application identifier
APP_ID = 'pmang app id'
APP_KEY = 'pmang app key'
APP_SECRET = 'pmang app secret'
LOCAL_CD = 'KOR'
DEVICE_CD = 'ANDROID'
#DEVICE_CD = 'IPHONE'

# user identifier
ID = 'pmang id'
PW = 'pmang pw'

# payment identifier
DEVELOPER_PAYLOAD = 'payment developer payload'
PURCHASE_TOKEN = 'payment purchase token'

# QA/TQ/Live URL
URL = 'http://qa.pmangplus.com'
#URL = 'http://tq.pmangplus.com'
#URL = 'http://pmangplus.com'


class TestClasses(unittest.TestCase):

	_access_token = None
	_member_id = None
	_nickname = None


	def setUp(self):
		def _params(email, passwd):
			udid = 'TestUDID_' + socket.gethostbyname(socket.gethostname())
			return {
				'local_cd': LOCAL_CD,
				'app_id': APP_ID,
				'app_key': APP_KEY,
				'app_secret': APP_SECRET,
				'udid': udid,
				'email': email,
				'passwd': passwd,
				'device_cd': DEVICE_CD
			}

		params = _params(ID, PW)
		res = self._post('/accounts/2/login', params)

		assert res['value']['access_token']
		assert res['value']['member']['member_id']
		assert res['value']['member']['nickname']

		self._access_token = res['value']['access_token']
		self._member_id = str(res['value']['member']['member_id'])
		self._nickname = res['value']['member']['nickname']

	@unittest.skip('')
	def test_self(self):
		def _params():
			return {
				'local_cd': LOCAL_CD,
				'access_token': self._access_token
			}
		params = _params()
		res = self._get('/members/@self', params)

		self.assertTrue(res['value'].has_key('crt_dt'))
		self.assertTrue(res['value'].has_key('upd_dt'))
		self.assertEqual(str(res['value']['member_id']), self._member_id)
		self.assertEqual(res['value']['nickname'], self._nickname)
		self.assertTrue(res['value'].has_key('profile_img_url'))
		self.assertTrue(res['value'].has_key('feeling'))
		self.assertTrue(res['value'].has_key('adult_auth_yn'))
		self.assertTrue(res['value'].has_key('adult_auth_dt'))
		self.assertTrue(res['value'].has_key('recent_login_dt'))
		self.assertTrue(res['value'].has_key('recent_app_id'))
		self.assertTrue(res['value'].has_key('email'))
		self.assertTrue(res['value'].has_key('anonymous_yn'))
		self.assertTrue(res['value'].has_key('reg_path'))
		self.assertTrue(res['value'].has_key('recent_app_title'))
		self.assertTrue(res['value'].has_key('last_msg_dt'))
		self.assertTrue(res['value'].has_key('new_msg_yn'))
		self.assertTrue(res['value'].has_key('friend_accept_cd'))
		self.assertTrue(res['value'].has_key('achieve_detail_info_list'))
		self.assertTrue(res['value'].has_key('member_achievement_summary'))
		self.assertTrue(res['value'].has_key('summary'))
		self.assertTrue(res['value'].has_key('options'))
		self.assertTrue(res['value'].has_key('contact_require_phone_auth'))
		self.assertTrue(res['value'].has_key('contact_require_import'))
		self.assertTrue(res['value'].has_key('contact_friend_mapping'))
		self.assertTrue(res['value'].has_key('contact_require_phone_auth_confirm'))
		self.assertTrue(res['value'].has_key('contact'))
		self.assertTrue(res['value'].has_key('pmang_usn'))
		self.assertTrue(res['value'].has_key('ci'))
		self.assertTrue(res['value'].has_key('sanction'))
		self.assertTrue(res['value'].has_key('profile_img_url_raw'))


	@unittest.skip('')
	def test_payment(self):
		def _params(developer_payload, purchase_token):
			return {
				'local_cd': LOCAL_CD,
				'developerPayload': developer_payload,
				'purchaseToken': purchase_token
			}
		params = _params(DEVELOPER_PAYLOAD, PURCHASE_TOKEN)
		res = self._post('/market/v3/effectuate', params)

		# if success
		#self.assertEqual(res['value']['result'], 'SUCCESS')
		#self.assertTrue(res['value'].has_key('store_product_id'))
		#self.assertTrue(res['value'].has_key('pay_id'))

		# if no success(FAILURE / DUPLICATED / NOTEXISTS)
		self.assertTrue(res['value'].has_key('result'))


	@unittest.skip('')
	def test_gcm_publish(self):
		def _params(member_ids):
			return {
				'local_cd': LOCAL_CD,
				'member_ids': ','.join(member_ids),
				'message': """{
					'content':'내용',
					'title':'제목',
					'type':'ad',
					'url':'http://ppl.cm/abcdef'
				}"""
			}
		member_ids = (str(self._member_id), str(self._member_id), str(self._member_id))
		params = _params(member_ids)
		res = self._post('/gcm/' + APP_ID + '/publish', params)

		self.assertTrue(res.has_key('value'))


	@unittest.skip('')
	def test_gcm_get_allow(self):
		def _params():
			return {
				'local_cd': LOCAL_CD,
				'access_token': self._access_token
			}
		params = _params()
		res = self._get('/gcm/' + APP_ID + '/getAllow', params)
		
		self.assertTrue(res.has_key('value'))


	@unittest.skip('')
	def test_gcm_set_allow(self):
		def _get_params():
			return {
				'local_cd': LOCAL_CD,
				'access_token': self._access_token
			}

		def _set_params(allow):
			return {
				'local_cd': LOCAL_CD,
				'access_token': self._access_token,
				'allow': allow
			}

		# get allow
		params = _get_params()
		res = self._get('/gcm/' + APP_ID + '/getAllow', params)
		
		self.assertTrue(res.has_key('value'))
		push_allow = res['value']

		# toggle allow
		params = _set_params(not push_allow)
		res = self._post('/gcm/' + APP_ID + '/setAllow', params)

		self.assertTrue(res.has_key('value'))
		self.assertEqual(not push_allow, res['value'])

		# confirm allow
		params = _get_params()
		res = self._get('/gcm/' + APP_ID + '/getAllow', params)
		
		self.assertTrue(res.has_key('value'))
		self.assertEqual(not push_allow, res['value'])


	def test_apns_publish(self):
		def _params(member_ids):
			return {
				'local_cd': LOCAL_CD,
				'member_ids': ','.join(member_ids),
				'message': '메세지'
			}
		member_ids = (str(self._member_id), str(self._member_id), str(self._member_id))
		params = _params(member_ids)
		res = self._post('/apns/' + APP_ID + '/publish', params)

		self.assertTrue(res.has_key('value'))


	def test_apns_get_allow(self):
		def _params():
			return {
				'local_cd': LOCAL_CD,
				'access_token': self._access_token
			}
		params = _params()
		res = self._get('/apns/' + APP_ID + '/getAllow', params)
		
		self.assertTrue(res.has_key('value'))


	def test_apns_set_allow(self):
		def _get_params():
			return {
				'local_cd': LOCAL_CD,
				'access_token': self._access_token
			}

		def _set_params(allow):
			return {
				'local_cd': LOCAL_CD,
				'access_token': self._access_token,
				'allow': allow
			}

		# get allow
		params = _get_params()
		res = self._get('/apns/' + APP_ID + '/getAllow', params)
		
		self.assertTrue(res.has_key('value'))
		push_allow = res['value']

		# toggle allow
		params = _set_params(not push_allow)
		res = self._post('/apns/' + APP_ID + '/setAllow', params)

		self.assertTrue(res.has_key('value'))
		self.assertEqual(not push_allow, res['value'])

		# confirm allow
		params = _get_params()
		res = self._get('/apns/' + APP_ID + '/getAllow', params)
		
		self.assertTrue(res.has_key('value'))
		self.assertEqual(not push_allow, res['value'])



	def _make_header(self):
		mills = int(time.time())

		h = hashlib.sha1()
		h.update(str(mills) + APP_SECRET)
		digest = h.hexdigest()

		return {
			'ver': '4',
			'ts': str(mills),
			'fp': digest,
			'User-Agent': 'PmangPlus for Server',
			'content-type': 'application/json;charset=UTF-8'
		}

	def _post(self, prefix, params):
		url = URL + prefix
		headers = self._make_header()
		response = requests.post(url, params=params, headers=headers)
		res = json.loads(response.content)
		self.assertEqual(res['result_code'], '000')
		self.assertEqual(res['result_msg'], 'API_OK')
		return res

	def _get(self, prefix, params):
		url = URL + prefix
		headers = self._make_header()
		r = requests.get(url, params=params, headers=headers)
		res = json.loads(r.content)
		self.assertEqual(res['result_code'], '000')
		self.assertEqual(res['result_msg'], 'API_OK')
		return res
