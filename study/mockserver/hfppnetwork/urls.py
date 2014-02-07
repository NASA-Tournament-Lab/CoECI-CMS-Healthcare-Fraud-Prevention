from django.conf.urls import patterns, include, url

from django.contrib import admin
from hfppnetwork.sms.views import data_request, data_response, general_service
admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'hfppnetwork.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

    url(r'^admin/', include(admin.site.urls)),
    url(r'^NetworkNode/services/data_request', data_request, name='data_request'),
    url(r'^NetworkNode/services/general_service', general_service, name='general_service'),
    url(r'^data_response', data_response, name='data_response'),
)
