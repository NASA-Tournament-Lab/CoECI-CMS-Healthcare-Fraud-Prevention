'''
Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
'''
'''
Django url mapping for decision module.

@author: TCSASSEMBLER
@version: 1.0
'''
from django.conf.urls import patterns, include, url

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    url(r'^admin/', include(admin.site.urls)),
    
    url(r'^create$', 'decision_module.views.create_partner_request', name='create_request'),
    url(r'^$', 'decision_module.views.list_partner_requests', name='list_requests'),
    url(r'^deny_requests$', 'decision_module.views.deny_partner_request', name='deny_requests'),
    url(r'^approval_requests$', 'decision_module.views.approval_partner_request', name='approval_requests'),
)
