# -*- coding: utf-8 -*-
import pymysql
pymysql.install_as_MySQLdb()
inited = False

def init():
    
    global inited
    if not inited:
        inited = True
        from hfppnetwork.sms import jobs
        #from hfppnetwork.sms import filters

init()


