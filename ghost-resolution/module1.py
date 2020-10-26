# utf-8

import re 
import requests
import json
import top.api

# 1.获得原始淘口令
# 2.解析出商品id
# 3.根据id转换成land_rul
# 4.生成新的淘口令


def creat_tkl(land_url:str,appkey:str,secret:str)->str:
    #url = 'taobao.tbk.tpwd.create'
    #port = 80
    #appkey='31690819'
    #secret='0e1dd55dd326bd40efa26d1c8f9e17d7'
    req=top.api.TbkTpwdCreateRequest()
    req.set_app_info(top.appinfo(appkey,secret))

    req.user_id="2416280559"
    req.text="长度大于5个字符"
    req.url=land_url
    resp= req.getResponse()
    return resp
    #try:
    #    resp= req.getResponse()
    #    return resp
    #except Exception as e:
    #    print(e)

def get_tao_land_url(good_id:str,apikey = 'DBopyqYPJz',pid_2='2114750177',pid_3='110957100054',uid='2416280559')->str:
    #apikey = 'DBopyqYPJz'
    #itemid=good_id
    #pid_2='2114750177'
    #pid_3='110957100054'
    #uid='2416280559'
    url = 'https://api.taokouling.com/tkl/TbkPrivilegeGet'
    data = {
        'apikey':apikey,
        'itemid':good_id,
        'siteid':pid_2,
        'adzoneid':pid_3,
        'uid':uid
    }
    requests.DEFAULT_RETRIES = 5
    s = requests.session()
    s.keep_alive = False
    response = requests.post(url='https://api.taokouling.com/tkl/TbkPrivilegeGet',data=json.dumps(data),verify=False)
    my_tkl = json.loads(response.text)
    return my_tkl['result']['data']['coupon_click_url']

def get_goods_id(tao_kou_ling:str)->str:
    try:
        header['Cookie'] = 'csrftoken=' + csrftoken + ";sessionid=" + session_id
        header['X-CSRFToken'] = csrftoken
        response = requests.post(url='http://m.mzsmn.com/tool/tkl_decrypt', data=json.dumps(data), headers=header,verify=False)
        good_info = json.loads(response.text)
        return good_info['data']['goods_id'],good_info
    except:
            data = {
                'mobile': '13104080302',
                'pwd': 'xuyang1001'
            }

            header = {
                'Cookie': 'csrftoken=aYiy12GqMojlfOia8ICzsemx1acrVDsuSC8E5kDtMXvMbfDgnh8TiW0oyMVjhV4g',
                'X-CSRFToken': 'aYiy12GqMojlfOia8ICzsemx1acrVDsuSC8E5kDtMXvMbfDgnh8TiW0oyMVjhV4g',
                'Content-Type': 'application/json',
                'Host': 'm.mzsmn.com',
                'Origin': 'http://m.mzsmn.com',
                'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36'
            }

            response = requests.post(url='http://m.mzsmn.com/login', data=json.dumps(data), headers=header,verify=False)
            cookie_map = response.cookies._cookies['m.mzsmn.com']['/']
            session_id = cookie_map['sessionid'].value
            csrftoken = cookie_map['csrftoken'].value

            data = {
                'tkl': tao_kou_ling
            }
            header['Cookie'] = 'csrftoken=' + csrftoken + ";sessionid=" + session_id
            header['X-CSRFToken'] = csrftoken

            response = requests.post(url='http://m.mzsmn.com/tool/tkl_decrypt', data=json.dumps(data), headers=header,verify=False)
            good_info = json.loads(response.text)
            return good_info['data']['goods_id'],good_info

def get_tao_kou_ling(text: str) -> str:
    symbol = "\₰|\/|\//|\¥|\\(|\\)|\《|\￥|\€|\\$|\₤|\₳|\¢|\¤|\฿|\฿|\₵|\₡|\₫|\₲|\₭|£|\₥|\₦|\₱|\〒|\₮|\₩|\₴|\₪|\៛|\﷼|\₢|\ℳ|\₯|\₠|\₣|\₧|\ƒ"
    pattern = '((' + symbol + ')([a-zA-Z0-9]{11})('+ symbol+'))'
    result = re.compile(pattern).findall(text)
    if len(result)>0:
        return True,result
    return False,None
 
def get_tao_url(text:str)->str:
    pattern = "((http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&:/~\+#]*[\w\-\@?^=%&/~\+#])?)"
    full_result = re.compile(pattern).findall(text)
    result = []
    for i in range(len(full_result)):
        if '.taobao.' in full_result[i][0]:
            result.append(full_result[i])
    if len(result)>0:
        return True,result
    return False,None

def full(text:str,#输入的qq信息
         apikey = 'DBopyqYPJz',#淘口令王apikey
         pid_2='2114750177',#阿里妈妈广告位id第二段
         pid_3='110957100054',#阿里妈妈广告位id第三段
         uid='2416280559',#淘宝uid
         appkey='31690819',#淘宝api的appkey
         secret='0e1dd55dd326bd40efa26d1c8f9e17d7'#淘宝api的secret
         )->str:
    #return -1  未识别到淘口令
    #return -2 识别到淘口令但在转化过程中出现异常
    #return 0 返回正常
    f,res = get_tao_kou_ling(text)
    if f:
        try:
            for tkl in res:
                print('0->解析淘口令:',tkl[0])
                good_id,good_info  = get_goods_id(tkl[0])
                print(good_info)
                print('1->解析商品ID:',good_id)
                land_url = get_tao_land_url(good_id,apikey ,pid_2,pid_3,uid)
                print('2->转成自己的商品land_url:',land_url)
                tkl_my = creat_tkl(land_url,appkey,secret)
                print('3->生成新的淘口令:',tkl_my['tbk_tpwd_create_response']['data']['model'])
                #text = text.replace(tkl[0],tkl_my['tbk_tpwd_create_response']['data']['password_simple'])
            print('4->返回新的文本:',text)
            return 0,tkl_my['tbk_tpwd_create_response']['data']['model'],good_info
        except Exception as e:
            print('异常！')
            print(e)
            return -2,-2,-2
    else:
        return -1,-1,-1

#############################################################
#Flask服务
from flask import Flask
from flask import request

def request_parse(req_data):
    if req_data.method == 'POST':
        data = req_data.json
    elif req_data.method == 'GET':
        data = req_data.args
    return data

app = Flask(__name__)

@app.route('/')
def hello_world():
    return '全体发财 都特么发财'

@app.route('/get_self_tkl', methods=['GET', 'POST'])
def get_self_tkl():
    data = request_parse(request)
    text=data.get("text")
    apikey = data.get("apikey")
    pid_2=data.get("pid_2")
    pid_3=data.get("pid_3")
    uid=data.get("uid")
    appkey=data.get("appkey")
    secret=data.get("secret")

    f,get_text,good_info = full(text,apikey,pid_2,pid_3,uid,appkey,secret)
    res={
        'flag':f,
        'text':get_text,
        'good_id':good_info
        }
    return json.dumps(res)


#############################################################

if __name__ == '__main__':
    data = {
        'mobile': '13104080302',
        'pwd': 'xuyang1001'
    }

    header = {
        'Cookie': 'csrftoken=aYiy12GqMojlfOia8ICzsemx1acrVDsuSC8E5kDtMXvMbfDgnh8TiW0oyMVjhV4g',
        'X-CSRFToken': 'aYiy12GqMojlfOia8ICzsemx1acrVDsuSC8E5kDtMXvMbfDgnh8TiW0oyMVjhV4g',
        'Content-Type': 'application/json',
        'Host': 'm.mzsmn.com',
        'Origin': 'http://m.mzsmn.com',
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36'
    }

    response = requests.post(url='http://m.mzsmn.com/login', data=json.dumps(data), headers=header,verify=False)
    cookie_map = response.cookies._cookies['m.mzsmn.com']['/']
    session_id = cookie_map['sessionid'].value
    csrftoken = cookie_map['csrftoken'].value

    app.run(host='0.0.0.0',port='5001')
    #test_str = '6.9   健美创研凡士林管状唇膏3g https://s.click.taobao.com/aNgS3vu ￥w9WocRo0VdJ￥/'
    #f,get_txt,good_info = full(test_str)
    #idd = get_goods_id('￥w9WocRo0VdJ￥/')
    #print('--------------------------------------------------------------')
    #print(get_txt)

 