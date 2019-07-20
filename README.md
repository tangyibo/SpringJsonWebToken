# SpringBoot+JWT

## Document
> https://www.toutiao.com/a6712042805293744648/?tt_from=weixin&utm_campaign=client_share&wxshare_count=1&timestamp=1563023580&app=news_article&utm_source=weixin&utm_medium=toutiao_android&req_id=20190713211259010025066213746F55C&group_id=6712042805293744648

## Test

> curl -X POST -d '{ "username": "admin", "password": "admin123"}' -H 'Content-Type: application/json' -s http://127.0.0.1:8090/login

> curl -X GET -H 'Content-Type: application/json' -H 'Authorization: Bearer ${access_token}' http://127.0.0.1:8090/user/info
