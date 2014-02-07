from django.test import TestCase
from hfppnetwork.sms.models import Partner
from django.http.response import HttpResponseRedirect
from django.contrib.auth.models import User

# Create your tests here.
def test_data_create(request):
    if not isinstance(request.user, User):
        return HttpResponseRedirect('/login/')
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_1', company_name="partner 1 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_1', company_name="partner 2 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_2', company_name="partner 3 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_3', company_name="partner 4 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_4', company_name="partner 5 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_5', company_name="partner 6 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_6', company_name="partner 7 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_7', company_name="partner 8 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_8', company_name="partner 9 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_9', company_name="partner 10 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_10', company_name="partner 11 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_11', company_name="partner 12 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        reciprocity = 10000.00).save()
    return HttpResponseRedirect('/studies/') 

def test_data_clear(request):
    if not isinstance(request.user, User):
        return HttpResponseRedirect('/login/')
    Partner.objects.all().delete()
    return HttpResponseRedirect('/studies/')