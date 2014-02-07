'''
Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
'''
'''
Django url mapping for hfppnetwork project.

@author: TCSASSEMBLER
@version: 1.0
'''
from django.conf.urls import patterns, include, url

from django.contrib import admin
from hfppnetwork.sms.views import LoginView, ListStudyView, CreateStudyView,\
    search_partner, EditStudyView,\
    execute_studies, delete_studies, data_response, ViewStudyTransactionsView,\
    ViewStudyBusinessRuleView, complete_studies, archive_studies,\
    ViewStudyResultsView, generate_study_report, download_study_report,\
    CreateStudyChartView, delete_study_charts
from hfppnetwork import settings
from django.contrib.staticfiles.urls import staticfiles_urlpatterns
from django.conf.urls.static import static
from hfppnetwork.sms.tests import test_data_create, test_data_clear, test_parse
from hfppnetwork.sms.views import LoginView
from hfppnetwork.sms.views import LogoutView
from hfppnetwork.sms.views import ViewUserProfileView
from hfppnetwork.sms.views import CreatePartnerView
from hfppnetwork.sms.views import ListPartnerView
from hfppnetwork.sms.views import EditPartnerView
from hfppnetwork.sms.views import CreatePartnerTagView
from hfppnetwork.sms.views import ListPartnerTagView
from hfppnetwork.sms.views import EditPartnerTagView
from hfppnetwork.sms.views import CreateDARCEmailView
from hfppnetwork.sms.views import ListDARCEmailView

admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    #core begin
    url(r'^admin$', 'hfppnetwork.sms.views.redirect_to_admin_auth_user'),
    url(r'^admin/', include(admin.site.urls)),
     # We route root page to login view
    url(r'^$', LoginView.as_view(), name='login/next='),
    url(r'^login/', LoginView.as_view(), name='login'),
    url(r'^logout$', LogoutView.as_view(), name='logout'),
    url(r'^profile$', ViewUserProfileView.as_view(), name='view_user_profile'),

    url(r'^partners/create$', CreatePartnerView.as_view(), name='create_partner'),
    url(r'^partners$', ListPartnerView.as_view(), name='list_partners'),
    url(r'^partners/(?P<pk>\d+)/edit$', EditPartnerView.as_view(), name='edit_partner'),
    url(r'^delete_partners$', 'hfppnetwork.sms.views.delete_partners', name='delete_partners'),

    url(r'^partner_tags/create$', CreatePartnerTagView.as_view(), name='create_partner_tag'),
    url(r'^partner_tags$', ListPartnerTagView.as_view(), name='list_partner_tags'),
    url(r'^partner_tags/(?P<pk>\d+)/edit$', EditPartnerTagView.as_view(), name='edit_partner_tag'),
    url(r'^delete_partner_tags$', 'hfppnetwork.sms.views.delete_partner_tags', name='delete_partner_tags'),

    url(r'^darc_emails/create$', CreateDARCEmailView.as_view(), name='create_darc_email'),
    url(r'^darc_emails$', ListDARCEmailView.as_view(), name='list_darc_emails'),
    url(r'^delete_darc_emails$', 'hfppnetwork.sms.views.delete_darc_emails', name='delete_darc_emails'),
    #core end


    url(r'^studies/create$', CreateStudyView.as_view(), name='study_create'),
    url(r'^studies/(?P<pk>\d+)/business_rule$', ViewStudyBusinessRuleView.as_view(), name='view_study_business_rule'),
    url(r'^studies/(?P<pk>\d+)/transactions$', ViewStudyTransactionsView.as_view(), name='view_study_transactions'),
    url(r'^studies/(?P<pk>\d+)/edit$', EditStudyView.as_view(), name='edit_study'),
    url(r'^studies/(?P<pk>\d+)/results$', ViewStudyResultsView.as_view(), name='view_study_results'),
    url(r'^studies/(?P<study_id>\d+)/generate_report$', generate_study_report, name='generate_study_report'),
    url(r'^studies/(?P<study_id>\d+)/report$', download_study_report, name='download_study_report'),
    url(r'^studies/(?P<pk>\d+)/charts$', CreateStudyChartView.as_view(), name='create_chart_study'),
    url(r'^delete_study_charts$', delete_study_charts, name='delete_study_charts'),
    url(r'^studies/search_partner', search_partner, name='search_partner'),
    url(r'^execute_studies$', execute_studies, name='execute_studies'),
    url(r'^complete_studies$', complete_studies, name='complete_studies'),
    url(r'^delete_studies$', delete_studies, name='delete_studies'),
    url(r'^archive_studies$', archive_studies, name='archive_studies'),
    url(r'^data_response$', data_response, name='data_response'),
    url(r'^studies$', ListStudyView.as_view(), name='studies'),
    url(r'^test/create/', test_data_create, name='test_data_create'),
    url(r'^test/clear/', test_data_clear, name='test_data_clear'),
    url(r'^test/temp/', test_parse, name='tes_temp'),

    # Defines url mapping for resources.
    url(r'^js/(?P<path>.*)$', 'django.views.static.serve',
        { 'document_root': settings.STATIC_PATH + '/js/' }),
    url(r'^css/(?P<path>.*)$', 'django.views.static.serve',
      { 'document_root': settings.STATIC_PATH + '/css/' }),
    url(r'^i/(?P<path>.*)$', 'django.views.static.serve',
        { 'document_root': settings.STATIC_PATH + '/i/' }),
    url(r'^font/(?P<path>.*)$', 'django.views.static.serve',
        { 'document_root': settings.STATIC_PATH + '/font/' }),
) 