from django.test import TestCase
from hfppnetwork.sms.models import Partner, Study, BeneficiaryClaimData,\
    CarrierClaimData, InpatientClaimData, OutpatientClaimData
from django.http.response import HttpResponseRedirect
from django.contrib.auth.models import User
from xml.etree import ElementTree
from hfppnetwork.sms import helper

# Create your tests here.
def test_data_create(request):
    if not isinstance(request.user, User):
        return HttpResponseRedirect('/login/')
    #helper.pull_hub_roles()
    #helper.pull_hub_partner('091f80d7-8ecb-429c-8f0b-caeaae18dcd8');
    #helper.add_hub_partner('user4', 'org4', '1', 'false', 'pass4')
    #helper.edit_hub_partner('349d9967-7bc1-4f0b-ba0f-150f8861fa98', 'user4e', 'org4e', '1', 'false', 'pass4e')
    #helper.delete_hub_partner('349d9967-7bc1-4f0b-ba0f-150f8861fa98')
    
    Partner.objects.all().delete()
    Partner.objects.create(hfpp_network_id = '1', company_name="partner 1 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        count_of_data_requests_pending = 0,
        reciprocity = 0).save()
    Partner.objects.create(hfpp_network_id = '2', company_name="partner 2 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        count_of_data_requests_pending = 0,
        reciprocity = 0).save()
    
    """
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_1', company_name="partner 1 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        count_of_data_requests_pending = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_2', company_name="partner 2 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        count_of_data_requests_pending = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_3', company_name="partner 3 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        count_of_data_requests_pending = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_4', company_name="partner 4 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        count_of_data_requests_pending = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_5', company_name="partner 5 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        count_of_data_requests_pending = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_6', company_name="partner 6 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        count_of_data_requests_pending = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_7', company_name="partner 7 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        count_of_data_requests_pending = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_8', company_name="partner 8 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        count_of_data_requests_pending = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_9', company_name="partner 9 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        count_of_data_requests_pending = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_10', company_name="partner 10 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        count_of_data_requests_pending = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_11', company_name="partner 11 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        count_of_data_requests_pending = 0,
        reciprocity = 10000.00).save()
    Partner.objects.create(hfpp_network_id = 'hfpp_partner_12', company_name="partner 12 company",city="C", state=1, \
        region='region', division='division', number_of_insured=0, owner = request.user,
        count_of_data_requests_received = 0,
        count_of_data_requests_sent = 0,
        count_of_data_requests_declined = 0,
        count_of_data_requests_responded = 0,
        count_of_data_requests_pending = 0,
        reciprocity = 10000.00).save()
    """
    return HttpResponseRedirect('/studies') 

def test_data_clear(request):
    if not isinstance(request.user, User):
        return HttpResponseRedirect('/login/')
    Partner.objects.all().delete()
    return HttpResponseRedirect('/studies/')

def test_parse(request):
    print ('!!!',BeneficiaryClaimData().bene_birth_dt);
    study = Study.objects.get(pk=16);
    root = ElementTree.parse('test_files/beneficiary_summary.xml')
    for beneficiary_summary in root.findall('//BeneficiarySummary'):
        print ('!!!code', beneficiary_summary.find('./BeneficiaryCode').text)
        properties = helper.parseBeneficiaryClaim({}, beneficiary_summary)
        print (properties)
        obj = BeneficiaryClaimData.objects.create(study=study, **properties)

    root = ElementTree.parse('test_files/carrier_claim.xml')
    for beneficiary_summary in root.findall('//CarrierClaim'):
        properties = helper.parseCarrierClaimData({}, beneficiary_summary)
        print (properties)
        obj = CarrierClaimData.objects.create(study=study, **properties)
        
    root = ElementTree.parse('test_files/inpatient_claim.xml')
    for beneficiary_summary in root.findall('//InpatientClaim'):
        properties = helper.parseInpatientClaimData({}, beneficiary_summary)
        print (properties)
        obj = InpatientClaimData.objects.create(study=study, **properties)
        
    root = ElementTree.parse('test_files/outpatient_claim.xml')
    for beneficiary_summary in root.findall('//OutpatientClaim'):
        properties = helper.parseOutpatientClaimData({}, beneficiary_summary)
        print (properties)
        obj = OutpatientClaimData.objects.create(study=study, **properties)
    return HttpResponseRedirect('/studies')