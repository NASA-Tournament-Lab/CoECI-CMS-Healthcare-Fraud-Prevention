// JavaScript Document
function fix_select(selector) {
    var i=$(selector).parent().find('div,ul').remove().css('zIndex');
    $(selector).unwrap().removeClass('jqTransformHidden').jqTransSelect();
    $(selector).parent().css('zIndex', i);
}

$(document).ready(function(){
    $.ajaxSetup({
        beforeSend: function(xhr, settings) {
            if (settings.type.toUpperCase() === "POST") {
                // Only send the token to relative URLs i.e. locally.
                xhr.setRequestHeader("X-CSRFToken",
                                     $('input[name="csrfmiddlewaretoken"]').val());
            }
        }
    });

    function addHidden(theForm, key, value) {
        // Create a hidden input element, and append it to the form:
        var input = document.createElement('input');
        input.type = 'hidden';
        input.name = key;
        input.value = value;
        theForm.appendChild(input);
    }

    //Body function for login flyout
	$('body').on('click',function(){
		$('.flyout-login').hide();
	});

    /* Digit-only field handler */
    var cleanup = function () {
        var value = $(this).val();
        var newValue = value.replace(/[^0-9]/g, '');
        if (value != newValue) {
            $(this).val(newValue);
        }
   }
    $(document).on('keydown', "input[type='text'].digit-only", function (evt) {
        var charCode = (evt.which) ? evt.which : evt.keyCode;
        if (charCode <= 31 || (charCode >= 48 && charCode <= 57) || (charCode >= 96 && charCode <= 105) ||
            (charCode >= 33 && charCode <= 40) || charCode == 46 || evt.ctrlKey) {
            return true;
        }
        return false;
    });
    $(document).on('keyup', "input[type='text'].digit-only", cleanup);
    $(document).on('paste', "input[type='text'].digit-only", cleanup);

	function searchPartner(company_name, page, pageSize) {
        $.get('/studies/search_partner?company_name=' + company_name + '&page=' + page+ '&pageSize=' + pageSize, function(data) {
          page = data.page;
          totalPages = 0;
          pageSize = data.pageSize;
          var total = data.total,
          start = page * pageSize + 1,
          end = Math.min((page + 1) * pageSize, total) + 1,
          partners = data.partners,
          i;
          if (total === 0) {
              $(".pagination").hide();
              start = 0;
              end = 0;
          } else {
             $(".pagination").show();
              totalPages = Math.ceil(total / pageSize);
          }
          if (page > 0) {
            $(".btn-pagination.btn-previous").show();
          } else {
            $(".btn-pagination.btn-previous").hide();
          }
          if (page + 1 < totalPages) {
            $(".btn-pagination.btn-next").show();
          } else {
            $(".btn-pagination.btn-next").hide();
          }
          $('.js-pages').data('page', page + 1);
          $('.js-pages').data('total-pages', totalPages);
          $('#page-from-idx').text(start);
          $('#page-to-idx').text(Math.max(0, end - 1));
          $('#page-total-objs').text(total);
          $('.formset').empty();
          $('.partner-list-box ul').empty();
          $('#id_studydatarequest_set-TOTAL_FORMS').val(0);
          $('#id_studydatarequest_set-INITIAL_FORMS').val(0);
          for(i = 0; i < partners.length; i = i + 1) {
              $('.partner-list-box ul').append('<li><span class="js-action-partner-details" data-partner-id="' + partners[i].id + '">' + partners[i].name + '</span></li>');
          }
       });
	}

	$('.btn-search-partner').click(function() {
	    var page=$('.pagination').data('page');
        var pageSize=$('.js-page-size').val();
	    var company_name=$('#search-parter-tags-id').data('value');
		if(company_name=="Search partner/tags"){
			company_name="";
		}
        /*if ($.trim(company_name).length === 0) {
            return;
        }*/
        $("#js-choose-partner").find(".js-all").text("Select All");
	    searchPartner(company_name, page, pageSize);
	});

	function initPageSize(pageSizeStyle) {
        if ($(pageSizeStyle).length) {
            var extravars = $(pageSizeStyle).data('page-size');
            var pageSize = "10";
            var d = extravars.split('&');
            for(var i = 0; i < d.length; i += 1) {
               if (d[i].indexOf('page_size=') !== -1) {
                   pageSize = d[i].split('=')[1];
               }
            }
            if (pageSize === "65535") {
                pageSize = 'All';
            }
            $(pageSizeStyle).val(pageSize);
        }

        $(pageSizeStyle).change(function() {
            var pageSize = $(this).val();
            var href = window.location.href;
            href = href.replace(/page=[0-9]+/g, 'page=1');
            if (href.indexOf('page_size=') !== -1) {
                if (pageSize === 'All') {
                   window.location = href.replace(/page_size=[0-9]+/g, 'page_size=65535');
                } else {
                   window.location = href.replace(/page_size=[0-9]+/g, 'page_size=' + pageSize);
                }
            } else if (href.indexOf('?') !== -1) {
                if (pageSize === 'All') {
                   window.location = href + '&page_size=65535';
                } else {
                   window.location = href + '&page_size=' + pageSize;
                }
            } else {
                if (pageSize === 'All') {
                   window.location = href + '?page_size=65535';
                } else {
                   window.location = href + '?page_size=' + pageSize;
                }
            }
        });
	}

	initPageSize(".js-search-study-page-size");
    initPageSize(".js-transaction-partner-page-size");
    /*
	$('.js-search-study-page-size').change(function() {
	    var pageSize = $(this).val();
	    if (pageSize === 'All') {
	       window.location = "/studies?status=" + $('#id_status').val() + "&page_size=65535";
	    } else {
	       window.location = "/studies?status=" + $('#id_status').val() + "&page_size=" + pageSize;
	    }
	});
	*/

    $('.js-page-size').change(function() {
        var company_name=$('#search-parter-tags-id').data('value');
		if(company_name=="Search partner/tags"){
			company_name="";
		}
       /* if ($.trim(company_name).length === 0) {
            return;
        }*/
        searchPartner(company_name, 0, $(this).val());
    });

	$('.js-partner-prev').click(function() {
	    var company_name=$('#search-parter-tags-id').data('value');
		if(company_name=="Search partner/tags"){
			company_name="";
		}
		/*if ($.trim(company_name).length === 0) {
	        return;
	    }*/
	    var pageSize=$('.js-page-size').val();
	    if (pageSize === 'All') {
	        return;
	    }
	    var page = parseInt($('.js-pages').data('page'));
	    if (page > 1) {
	        searchPartner(company_name, (page - 1) - 1, pageSize);
	    }
	});

    $('.js-partner-next').click(function() {
        var company_name=$.trim($('#search-parter-tags-id').data('value'));
		if(company_name=="Search partner/tags"){
			company_name="";
		}
//        if ($.trim(company_name).length === 0) {
//            return;
//        }
        var pageSize=$('.js-page-size').val();
        if (pageSize === 'All') {
            return;
        }
        var page = parseInt($('.js-pages').data('page'));
        var totalPages = parseInt($('.js-pages').data('total-pages'));
        if (page < totalPages) {
            searchPartner(company_name, (page - 1) + 1, pageSize);
        }
    });

    $('.js-transaction-search-partner').click(function() {
         $(this).parent().parent().submit();
    });

	//Username text field function
	$('#id_username').on('focus',function(){
		//When username text field is default style
		if($(this).hasClass('default')){
			$(this).removeClass('default');
			$(this).val('');
		}
		//HighLight style
		$(this).parents('.input-wrapper').addClass('current');
	});
	$('#id_username').on('blur',function(){
		//When username text field is empey
		if($(this).val() == ''){
			$(this).addClass('default');
			$(this).val('Username');
		}
		//HighLight style
		$(this).parents('.input-wrapper').removeClass('current');
	});

	//Password text field function
	$('#password-holder').on('focus',function(){
		$(this).hide();
		$('#id_password').show();
		$('#id_password').focus();
		//HighLight style
		$(this).parents('.input-wrapper').addClass('current');
	});
	$('#id_password').on('focus',function(){
		$(this).parents('.input-wrapper').addClass('current');
	});
	$('#id_password').on('blur',function(){
		//When password text field is empry
		if($(this).val() == ''){
			$(this).hide();
			$('#password-holder').show();
		}
		//HighLight style
		$(this).parents('.input-wrapper').removeClass('current');
	});

	//Login flyout function
	$('.btn-login').on('click',function(event){
		$('#LoginForm').submit();
	});

	//Invalid credentials
	$('.invalid-credentials').on('click',function(){
		$('#login-form').addClass('error');
	});

	//JQUERY for transform
	$('.form').jqTransform();


    	/**
	 * Show detail of last edit partner.
	 *
	 * @author caoweiquan322
	 * @version 1.0
	 */
	setTimeout(function(){
		var currLocation = '' + window.location;
		if(currLocation.indexOf('?') < 0) {
			return;
		}
		if ($('#hidden-active-partner').text() == '') {
			$('.js-action-partner-details:last').trigger('click');
			return;
		}
		$('.partner-lists .partner-list-box .partnerId').each(function() {
			if ($('#hidden-active-partner').text() == $(this).text()) {
				$(this).closest('li').next().find('span').trigger('click');
				return false;
			}
			return true;
		});

	}, 50);

	/**
	 * Scroll the jqTransform library transformed select element.
	 *
	 * @author caoweiquan322
	 * @version 1.0
	 */
	var scrollToJqSelected = function(selectWrapper) {
		var ul_module = selectWrapper.find('ul');
		var ul_height = ul_module.height();
		var seleted_li = ul_module.find('a.selected').closest('li');
		if(seleted_li.length > 0) {
		    var height = seleted_li.height();
		    var prevCount = seleted_li.prevAll().length;
		    ul_module.scrollTop(height * prevCount - ul_height/2);
		}
	}

	/**
	 * Select the specified item of given index.
	 *
	 * @author caoweiquan322
	 * @version 1.0
	 */
	var selectItem = function(selectWrapper, selectedIndex) {
		selectWrapper.find('ul li a').removeClass('selected');
		selectWrapper.find('ul li a').eq(selectedIndex).addClass('selected');
		selectWrapper.find('div span').html(selectWrapper.find('ul li a').eq(selectedIndex).html());
		selectWrapper.find('select').val(selectWrapper.find('select option').eq(selectedIndex).val());
		//selectWrapper.find('select').val('' + (selectedIndex - 1));
	}

	/**
	 * Handles keydown event for jqTransform select elements.
	 *
	 * @author caoweiquan322
	 * @version 1.0
	 */
	$(document).keydown(function(event) {
		if (event.keyCode != 38 && event.keyCode != 40 && event.keyCode != 13) {
			return true;
		}
		var selectWrapper = null;
		$('.jqTransformSelectWrapper ul').each(function() {
			if ($(this).css('display') == 'block') {
				selectWrapper = $(this).closest('.jqTransformSelectWrapper');
				return false;
			}
			return true;
		});
		if (!selectWrapper) {
			return true;
		}
		if (event.keyCode == 13) { // Enter key
			selectWrapper.find('ul').css('display', 'none');
			return false;
		}

		var numberOfOptions= selectWrapper.find('li').length;
		var selectedIndex = selectWrapper.find('a.selected').closest('li').prevAll().length;
		if (event.keyCode == 38) { // Up key
			if (selectedIndex <= 0) {
				return true;
			}
			selectedIndex--;
		} else if (event.keyCode == 40) { // Down key
			if (selectedIndex >= numberOfOptions - 1) {
				return true;
			}
			selectedIndex++;
		}
		// select this index
		selectItem(selectWrapper, selectedIndex);
		// scroll to this index
		scrollToJqSelected(selectWrapper);
		return false;
	});


	//Date picker
	if($('.date-picker').length){
		$('.date-picker').datepicker({
			showOn: "both",
			buttonImage: "/i/calendar.png",
			buttonImageOnly: true,

            constrainInput: false
		}).attr('readonly','readonly');
	}

	//Hidden header elements in table
	$('.table-header').each(function(){
		$(this).find('tbody').remove();
	});

	//Column in body of table
	$('.table-tbody').each(function(){
		$(this).find('tr:odd').addClass('odd');
		$(this).find('tr:even').addClass('even');
		$(this).find('tr:last').addClass('last');
		//Remove last in draft section
		$('.draft-section .table-tbody tr').removeClass('last');
	});

    	//Column in .users-table-tbody of table
	$('.users-table-tbody').each(function(){
		$(this).find('tr:odd').addClass('odd');
		$(this).find('tr:even').addClass('even');
		$(this).find('tr:last').addClass('last');
		//Remove last in draft section
		$('.draft-section .table-tbody tr').removeClass('last');
	});

    $('.table-tbody').each(function(){
        $(this).find('.js-row').removeClass('odd');
        $(this).find('.js-row').removeClass('even');
        $(this).find('.js-row').removeClass('last');
        $(this).find('.js-row:odd').addClass('odd');
        $(this).find('.js-row:even').addClass('even');
        $(this).find('.js-row:last').addClass('last');
    });

	$('.details-table').each(function(){
		$(this).find('tr').removeClass('odd');
		$(this).find('tr').removeClass('even');
		$(this).find('tr:odd').addClass('odd');
		$(this).find('tr:even').addClass('even');
	});

	//Checkbox function in header of table
	$('.table-header').on('click','input:checkbox',function(){
		//When checkbox is on
		if($(this).attr('checked')){
			$(this).parents('.table-header').next().find('input:checkbox').attr('checked',false);
			$(this).parents('.table-header').next().find('.jqTransformCheckbox').removeClass('jqTransformChecked');
		}
		//When checkbox is off
		else{
			$(this).parents('.table-header').next().find('input:checkbox').attr('checked',true);
			$(this).parents('.table-header').next().find('.jqTransformCheckbox').addClass('jqTransformChecked');
		}
	});

	//Checkbox function in body of table
	$('.table-tbody').on('click','input:checkbox',function(){
		var flag = true; //All checkbox is on by default

		var i = $('.table-tbody input:checkbox').index($(this));
		//When checkbox is on
		if($(this).attr('checked')){
			$(this).parents('.table-tbody').prev().find('input:checkbox').attr('checked',false);
			$(this).parents('.table-tbody').prev().find('.jqTransformCheckbox').removeClass('jqTransformChecked');
		}
		//When checkbox os off
		else{
			$('.table-tbody input:checkbox').each(function(idx){
				if(!$(this).attr('checked') && idx!=i){
					flag = false;
				}
			});
			if(flag){
				$(this).parents('.table-tbody').prev().find('input:checkbox').attr('checked',true);
				$(this).parents('.table-tbody').prev().find('.jqTransformCheckbox').addClass('jqTransformChecked');
			}
		}
	});


    	//Checkbox function in header of .users-table-tbody table
	$('.users-table-tbody thead').on('click','input:checkbox',function(){
		//When checkbox is on
		if($(this).attr('checked')){
			$(this).parents('.users-table-tbody').find('tbody').find('input:checkbox').attr('checked',false);
			$(this).parents('.users-table-tbody').find('tbody').find('.jqTransformCheckbox').removeClass('jqTransformChecked');
		}
		//When chcekbox is off
		else{
			$(this).parents('.users-table-tbody').find('tbody').find('input:checkbox').attr('checked',true);
			$(this).parents('.users-table-tbody').find('tbody').find('.jqTransformCheckbox').addClass('jqTransformChecked');
		}
	});

	//Checkbox function in body of .users-table-tbody table
	$('.users-table-tbody tbody').on('click','input:checkbox',function(){
		var flag = true; //All checkbox is on by default

		var i = $('.table-tbody input:checkbox').index($(this));
		//When checkbox is on
		if($(this).attr('checked')){
			$(this).parents('.users-table-tbody').find('thead').find('input:checkbox').attr('checked',false);
			$(this).parents('.users-table-tbody').find('thead').find('.jqTransformCheckbox').removeClass('jqTransformChecked');
		}
		//When checkbox os off
		else{
			$('.users-table-tbody tbody input:checkbox').each(function(idx){
				if(!$(this).attr('checked') && idx!=i){
					flag = false;
				}
			});
			if(flag){
				$(this).parents('.users-table-tbody').find('thead').find('input:checkbox').attr('checked',true);
				$(this).parents('.users-table-tbody').find('thead').find('.jqTransformCheckbox').addClass('jqTransformChecked');
			}
		}
	});


	//Init progress function
	$('.progress').each(function(){
		//Get the precent of progress
		var width = $(this).find('span').text();
		//Set progress
		$(this).find('.progress-inner').css({
			'width': width
		});
	});

	if ($('.js-search-form-status').length) {
	    var status = $('#id_status').val();
	    if (status === "") {
	        status = "0";
	    }
        var i = parseInt(status);
        $('.tab-container .tab li').removeClass('active');
        $('.tab-container .tab li').eq(i).addClass('active');
        if (i != 0) {
            $('.tab-content').eq(0).hide();
            $('.tab-content').eq(i).show();
        }
	}

    /**
	 * Switch tab function. This function is modified so that web page will be redirected to another
	 * page, so that data would be refreshed.
	 *
	 * @author caoweiquan322
	 * @version 1.0
	 */
	//Switch tab function
	$('.tab-container .tab').on('click','li',function(){
		//Init number of current tab item
		var i = $('.tab-container .tab li').index($(this));
        if(window.location.href.indexOf('studies') < 0){
             if (i == 0) {
                if (window.location.href.indexOf('partners') < 0) {
                    window.location.href = "/partners";
                }
            } else {
                if (window.location.href.indexOf('partner_tags') < 0) {
                    window.location.href = "/partner_tags";
                }
            }
        }else{
           $('#id_status').val(i);
		    $('#search_form').submit();
        }
		//Set tab item
		//$('.tab-container .tab li').removeClass('active');
		//$(this).addClass('active');
		//Set tab content
		//$('.tab-content').hide();
		//$('.tab-content').eq(i).show();


	});

	$('.claim-data-tab-container .tab li').click(function() {
	    var claimDataIndex = $('.claim-data-tab-container .tab li').index($(this));
        //Set tab item
        $('.claim-data-tab-container .tab li').removeClass('active');
        $(this).addClass('active');

        //Set tab content
        $('.switch-claim-data-tab-wrapper .table-wrapper').hide();
        $('.switch-claim-data-tab-wrapper .table-wrapper').eq(claimDataIndex).show();


        var columnNames = [], fieldNames=[], i = 0;
        $('.switch-claim-data-tab-content .table-tbody').eq(claimDataIndex).find('.table-header .cell-claim-data').each(function() {
            columnNames.push($(this).find('span').text());
            fieldNames.push($(this).find('span').data('field'));
        });

        var fieldTypes = fieldTypesAll[claimDataIndex];

        $('.js-chart-x').empty();
        for(i = 0; i < columnNames.length; i += 1) {
            $('.js-chart-x').append('<option value="' + fieldNames[i] + '">'+ columnNames[i] + '</option>');
        }
        $('.js-chart-x option:first').attr('selected', 'selected');
        fix_select('.js-chart-x');

        $('.js-chart-y').empty();
        for(i = 0; i < columnNames.length; i += 1) {
            if (fieldTypes[fieldNames[i]] === "integer") {
                $('.js-chart-y').append('<option value="' + fieldNames[i] + '">'+ columnNames[i] + '</option>');
            }
        }
        $('.js-chart-y option:first').attr('selected', 'selected');
        fix_select('.js-chart-y');

        $('.js-chart-group').empty();
        $('.js-chart-group').append('<option selected="selected">--</option>');
        for(i = 0; i < columnNames.length; i += 1) {
            if (fieldTypes[fieldNames[i]] !== 'string') {
                $('.js-chart-group').append('<option value="' + fieldNames[i] + '">'+ columnNames[i] + '</option>');
            }
        }
        fix_select('.js-chart-group');
	});

	$('.claim-data-tab-container .tab li').first().trigger('click');

    $('.claim-data-column-tab-container .tab li').click(function() {
        var claimDataIndex = $('.claim-data-column-tab-container .tab li').index($(this));
        //Set tab item
        $('.claim-data-column-tab-container .tab li').removeClass('active');
        $(this).addClass('active');

        //Set tab content
        $('.claim-data-column-content-container').hide();
        $('.claim-data-column-content-container').eq(claimDataIndex).show();
    });

    $('.claim-data-column-tab-container .tab li').first().trigger('click');

    var claimDataColumns = [[],[],[],[],[]];
    if ($('.claim-data-column-content-container').length) {
        var i = 0, j = 0, len = 0, c=0;
        $('.claim-data-column-content-container').each(function () {
            len = $(this).find('.rows').length;
            for(j = 0; j < len; j = j + 1) {
                claimDataColumns[i].push(null);
            }
            c = 0;
            $(this).find('.column').each(function() {
                j = 0;
                $(this).find('.rows').each(function() {
                    claimDataColumns[i][j * 6 + c] = {'column': $(this).find('label').text(), 'field': $(this).find('input:checkbox').data('field'), 'checked': $(this).find('input:checkbox').is(':checked')};
                    j += 1;
                });
                c += 1;
            });
            i += 1;
        });

        $('.claim-data-column-content-container .rows input:checkbox').change(function() {
            var idx = $('.claim-data-column-content-container').index($(this).parents('.claim-data-column-content-container'));
            var columns = claimDataColumns[idx];
            for(idx = 0; idx < columns.length; idx += 1) {
                if ($(this).data('field') === columns[idx].field) {
                    columns[idx].checked = $(this).is(':checked');
                    break;
                }
            }
        });
    }

    $('.js-apply-columns').click(function() {
        $('.claim-data-column-content-container input[type="checkbox"]').each(function () {
         if ($(this).is(':checked')) {
             $(this).parent().append('<input type="hidden" name="' + $(this).data('name') + '" value="' + $(this).data('field') + '" />');
         }
        });
        $('#results-columns-form').submit();
    });

    $('.js-lookup-columns').click(function() {
        var text = $.trim($('.js-lookup-columns-text').val().toUpperCase());
        var i = $('.claim-data-column-tab-container .tab li').index($('.claim-data-column-tab-container .tab li.active')[0]), j = 0, c = 0, len = 0;
        var container = $('.claim-data-column-content-container').eq(i);
        var columns = claimDataColumns[i], matchedColumns = [], unmatchedColumns = [];
        if (text.length === 0) {
            matchedColumns = columns;
        } else {
            for(j = 0; j < columns.length; j += 1) {
                if (columns[j].column.toUpperCase().indexOf(text) !== -1) {
                    matchedColumns.push(columns[j]);
                } else {
                    unmatchedColumns.push(columns[j]);
                }
             }
        }
        j = 0;
        var allcells = [], allcellst = container.find('.rows');
        for(j = 0; j < columns.length; j += 1) {
            allcells.push(allcellst[j]);
        }
        c = 0;
        container.find('.column').each(function() {
            j = 0;
            $(this).find('.rows').each(function() {
                allcells[j * 6 + c] = $(this);
                j += 1;
            });
            c += 1;
        });
        for(j = 0; j < matchedColumns.length; j += 1) {
            allcells[j].removeClass('js-hidden');
            allcells[j].find('input:checkbox').attr("checked", matchedColumns[j].checked);
            if (matchedColumns[j].checked) {
                allcells[j].find('input:checkbox').siblings('a').addClass('jqTransformChecked');
            } else {
                allcells[j].find('input:checkbox').siblings('a').removeClass('jqTransformChecked');
            }
            allcells[j].find('input:checkbox').data('field', matchedColumns[j].field);
            allcells[j].find('label').text(matchedColumns[j].column);
        }

        for(j = matchedColumns.length; j < allcells.length; j += 1) {
            allcells[j].addClass('js-hidden');
            allcells[j].find('input:checkbox').attr("checked", unmatchedColumns[j - matchedColumns.length].checked);
            if (unmatchedColumns[j - matchedColumns.length].checked) {
                allcells[j].find('input:checkbox').siblings('a').addClass('jqTransformChecked');
            } else {
                allcells[j].find('input:checkbox').siblings('a').removeClass('jqTransformChecked');
            }
            allcells[j].find('input:checkbox').data('field', unmatchedColumns[j - matchedColumns.length].field);
            allcells[j].find('label').text(unmatchedColumns[j - matchedColumns.length].column);
        }
    });

    // switch the tabs with the anchor value in home page
    /*
    if($("#js-home-anchors").length>0 && location.hash.length>1){
        var hash = location.hash.substring(1),
            anchor = $("#js-home-anchors").find(".js-anchor-" + hash);
        if(anchor.length>0){
            anchor.trigger("click");
        }
    }
    */

    if ($('.switch-tab-container').length) {
        ($('.switch-tab-container .switch-tab ul li a')).each(function() {
           if (window.location.href.indexOf($(this).attr('href')) !== -1) {
               $(this).parent().addClass('active');
           }
        });
    }
    //Switch tab function
	$('.switch-tab-container .switch-tab').on('click','li',function(){

		var $parent = $(this).parents('.switch-tab-container');
		//Init number of current tab item
		var i = $('.switch-tab-container .switch-tab li').index($(this));
		//Set tab item
		$('.switch-tab-container .switch-tab li').removeClass('active');
		$(this).addClass('active');
		//Set tab content
		$parent.find('.switch-tab-content').hide();
		$parent.find('.switch-tab-content').eq(i).show();

        if ($('#edit-study-query-id').length) {
            switch(i) {
                case 0:
                    if ($('#beneficiary_builtQuery').hasClass('js-rebuild') == false) {
                        parseQueryAndRebuild($('#beneficiary_builtQuery').text());
                        $('#beneficiary_builtQuery').addClass('.js-rebuild');
                    }
                break;
                case 1:
                    if ($('#carrier_builtQuery').hasClass('js-rebuild') == false) {
                        parseQueryAndRebuild($('#carrier_builtQuery').text());
                        $('#carrier_builtQuery').addClass('.js-rebuild');
                    }
                break;
                case 2:
                    if ($('#inpatient_builtQuery').hasClass('js-rebuild') == false) {
                        parseQueryAndRebuild($('#inpatient_builtQuery').text());
                        $('#inpatient_builtQuery').addClass('.js-rebuild');
                    }
                break;
                case 3:
                    if ($('#outpatient_builtQuery').hasClass('js-rebuild') == false) {
                        parseQueryAndRebuild($('#outpatient_builtQuery').text());
                        $('#outpatient_builtQuery').addClass('.js-rebuild');
                    }
                break;
                case 4:
                    if ($('#prescription_builtQuery').hasClass('js-rebuild') == false) {
                        parseQueryAndRebuild($('#prescription_builtQuery').text());
                        $('#prescription_builtQuery').addClass('.js-rebuild');
                    }
                break;
            }
        }
	});

	$('.sub-tab-container .sub-tab').on('click', 'li', function(event){
		var $parent = $(this).parents('.sub-tab-container');
		//Init number of current tab item
		var i = $parent.find('.sub-tab li').index($(this));
		//Set tab item
		$parent.find('.sub-tab li').removeClass('active');
		$(this).addClass('active');
		//Set tab content
		$parent.find('.sub-tab-content').hide();
		$parent.find('.sub-tab-content').eq(i).show().trigger('show');
		event.stopPropagation();
	});

	//Rate
	$('.rate-bar').each(function(){
		//Get rate percent
		var l_num = parseInt($(this).find('.rate-l').text());
		var r_num = parseInt($(this).find('.rate-r').text());
		var width = $(this).width();
		total = l_num+r_num;
		//Set rate bar
		$(this).find('.rate-l').css({
			'width': l_num*width/total
		});
		$(this).find('.rate-r').css({
			'width': r_num*width/total+1
		});
	});

	//Modal position function
	positionModal = function(position){

		//Get X and Y of modal.
		var wWidth  = window.innerWidth;
		var wHeight = window.innerHeight;

		if (wWidth==undefined) {
			wWidth  = document.documentElement.clientWidth;
			wHeight = document.documentElement.clientHeight;
		}

		var boxLeft = parseInt((wWidth / 2) - ( $("#modal").width() / 2 ));
		var boxTop  = parseInt((wHeight / 2) - ( $("#modal").height() / 2 ));

		//Set the position of modal
		if(position){
			$("#modal").css({
				'position': position,
				'margin': '80px auto 0 ' + boxLeft + 'px'
			});
		}else{
			$("#modal").css({
				'position': 'fixed',
				'margin': boxTop + 'px auto 0 ' + boxLeft + 'px'
			});
		}

		$("#modal-background").css("opacity", "0.9");

		if ($("body").height() > $("#modal-background").height()){
            $("#modal-background").css("height", $("body").height() + "px");
		}
	}

	//Load modal function
	loadModal = function(itemId) {
        $('#modal-background').show();
		$(itemId).show();
		positionModal();
    }

	//Close modal function
	closeModal = function() {
        $('#modal-background').hide();
        $('.modal').hide();
    }

	//Close modal button
	$('.close-modal').on('click',function(){
		closeModal();
	});

	//Modal appear when click generate report button
	$('.btn-generate-report').on('click',function(){
		closeModal();
		loadModal('#modal-generate-report');
	});

    function archiveStudies(studyIds, form) {
        if (studyIds.length > 0) {
            $.ajax({
               type: "POST",
               url: '/archive_studies',
               data: form.serialize(),
               success: function(data)
               {
                   loadModal('#modal-archived');
               },
               error: function(data) {
                   alert('failed to archive studies: ' + data);
               }
             });
        }
    }

	//Modal appear when click archive study button
	$('.btn-archive-study').on('click',function(){
		closeModal();
        var studyIds = [$('#study_id').data('id')];
        var form = $('#study_op_form')
        archiveStudies(studyIds, form);
	});

    function executeStudies(studyIds, form) {
        if (studyIds.length > 0) {
            $.ajax({
               type: "POST",
               url: '/execute_studies',
               data: form.serialize(),
               success: function(data)
               {
                   var i = 0;
                   for(i = 0; i < studyIds.length; i += 1) {
                       studyIds[i] = '<strong>' + studyIds[i] + '</strong>';
                   }
                   $('#exec_trans_results').html(studyIds.join(' and '));
                   $('#study_op').text('executed');
                   loadModal('#modal-execute-transactions');
               },
               error: function(data) {
                   alert('failed to execute transactions: ' + data);
               }
             });
        }
    }

    //Modal appear when click archive study button
    $('.js-execute-study').on('click',function(){
        closeModal();
        var studyIds = [$('#study_id').data('id')];
        var form = $('#study_op_form')
        executeStudies(studyIds, form)
    });

	//Modal appear when click execute transactions button
	$('.js-execute-transactions').on('click',function(){
		closeModal();
		var studyIds = [];
        var form = $('#draft_form');
        //form.submit();
        $('#draft_form input[name="ids"]').remove();
		$('.js-draft-check-wrapper .jqTransformChecked').each(function() {
		    var studyId = $(this).parent().parent().siblings('.cell-study-id').find('a').text();
		    studyIds.push(studyId);
		    addHidden(form.get(0), 'ids', studyId);
		})

        executeStudies(studyIds, form)
	});

    function deleteStudies(studyIds, form) {
        if (studyIds.length > 0) {
            $.ajax({
               type: "POST",
               url: '/delete_studies',
               data: form.serialize(),
               success: function(data)
               {
                   var i = 0;
                   for(i = 0; i < studyIds.length; i += 1) {
                       studyIds[i] = '<strong>' + studyIds[i] + '</strong>';
                   }
                   $('#exec_trans_results').html(studyIds.join(' and '));
                   $('#study_op').text('deleted');
                   loadModal('#modal-execute-transactions');
               },
               error: function(data) {
                   alert('failed to delete transactions: ' + data);
               }
             });
        }
    }

    //Modal appear when click archive study button
    $('.js-delete-study').on('click',function(){
        closeModal();
        var studyIds = [$('#study_id').data('id')];
        var form = $('#study_op_form')
        deleteStudies(studyIds, form)
    });

    $('.js-delete-transactions').on('click',function(){
        closeModal();
        var studyIds = [];
        var form = $('#draft_form');
        //form.submit();
        $('#draft_form input[name="ids"]').remove();
        $('.js-draft-check-wrapper .jqTransformChecked').each(function() {
            var studyId = $(this).parent().parent().siblings('.cell-study-id').find('a').text();
            studyIds.push(studyId);
            addHidden(form.get(0), 'ids', studyId);
        })

        deleteStudies(studyIds, form);
    });

    //Modal appear when click transactions button
    $('.btn-transactions').on('click',function(){
        closeModal();
        var src = $('#modal-finalize-transactions').data('src');
        var studyIds = [];
        var form = null;
        if (src === "list") {
            form = $('#inprogress_form')
            $('#inprogress_form input[name="ids"]').remove();
            $('.js-inprogress-check-wrapper .jqTransformChecked').each(function() {
                var studyId = $(this).parent().parent().siblings('.cell-study-id').find('a').text();
                studyIds.push(studyId);
                addHidden(form.get(0), 'ids', studyId);
            });
        } else {
            form = $('#study_op_form')
            studyIds = [$('#study_id').data('id')];
        }
        if (studyIds.length === 0) {
            return;
        }
        loadModal('#modal-finalize-transactions');
    });

    //Modal appear when click finalize transactions button
    $('#modal-finalize-transactions .btn-yes').on('click', function(){
        closeModal();
        var src = $('#modal-finalize-transactions').data('src');
        var studyIds = [];
        var form = null;
        if (src === "list") {
            form = $('#inprogress_form')
            $('#inprogress_form input[name="ids"]').remove();
            $('.js-inprogress-check-wrapper .jqTransformChecked').each(function() {
                var studyId = $(this).parent().parent().siblings('.cell-study-id').find('a').text();
                studyIds.push(studyId);
                addHidden(form.get(0), 'ids', studyId);
            });
        } else {
            form = $('#study_op_form')
            studyIds = [$('#study_id').data('id')];
        }
        completeStudies(studyIds, form);
    });

    function completeStudies(studyIds, form) {
        if (studyIds.length > 0) {
            $.ajax({
               type: "POST",
               url: '/complete_studies',
               data: form.serialize(),
               success: function(data)
               {
                   loadModal('#modal-finalize-transactions-success');
               },
               error: function(data) {
                   alert('failed to finalize transactions: ' + data);
               }
             });
        }
    }

    $('.js-study-progress').each(function() {
        var p = $(this).parent().parent().parent();
        var a = parseInt(p.find('.js-study-cnt-satisfied').text());
        var b = parseInt(p.find('.js-study-cnt-sent').text());
        if (b > 0) {
            $(this).text((a * 100 / b) + '%');
        }
    });


	//$('#builtQuery').text($('#id_query').val());
        $('#beneficiary_builtQuery').text($('#id_beneficiary_query').val());
        $('#carrier_builtQuery').text($('#id_carrier_query').val());
        $('#inpatient_builtQuery').text($('#id_inpatient_query').val());
        $('#outpatient_builtQuery').text($('#id_outpatient_query').val());
        $('#prescription_builtQuery').text($('#id_prescription_query').val());
	//Modal appear when click save as draft button
	$('.btn-save-as-draft').on('click',function(){
                if (this.clicked) {
                    return;
                }
                this.clicked = true;
                // IMMJ ====>
                var existEmptyInput = false;
                $('.extendedAttr input.txtNormal').each(function() {
                    //alert($(this).val() + ', ' + $(this).html());
                    if ($(this).val() == '' && !$(this).parents('span').hasClass('priceGrMin')) {
                        existEmptyInput = true;
                        $('.rule-switch').find('.empty-error').removeClass('hide');
                        return false;
                    }
                    return true;
                });
                if (existEmptyInput) {
                    return;
                }

		closeModal();
		//loadModal('#modal-save-draft');
		var form = $('#study-form');
		var data = form.serialize();
		$('#id_beneficiary_query').val($('#beneficiary_builtQuery').text());
		$('#id_carrier_query').val($('#carrier_builtQuery').text());
		$('#id_inpatient_query').val($('#inpatient_builtQuery').text());
		$('#id_outpatient_query').val($('#outpatient_builtQuery').text());
		$('#id_prescription_query').val($('#prescription_builtQuery').text());
		form.submit();
		/*
		$.ajax({
           type: "POST",
           url: form.attr('action'),
           data: form.serialize(), // serializes the form's elements.
           success: function(data)
           {
               $('#modal-study-id').text('study id');
               loadModal('#modal-save-draft');
               //alert(data); // show response from the php script.
           },
           error: function(data) {
               alert(data);
           }
         });
         */
	});

	//Modal appear when click save as execute button
	$('.btn-save-and-execute').on('click',function(){
                // IMMJ ====>
                var existEmptyInput = false;
                $('.extendedAttr input.txtNormal').each(function() {
                    //alert($(this).val() + ', ' + $(this).html());
                    if ($(this).val() == '' && !$(this).parents('span').hasClass('priceGrMin')) {
                        existEmptyInput = true;
                        $('.rule-switch').find('.empty-error').removeClass('hide');
                        return false;
                    }
                    return true;
                });
                if (existEmptyInput) {
                    return;
                }

		closeModal();
		//loadModal('#modal-save-execute');
        var form = $('#study-form');
        //$('#id_query').val($('#builtQuery').text());
		$('#id_beneficiary_query').val($('#beneficiary_builtQuery').text());
		$('#id_carrier_query').val($('#carrier_builtQuery').text());
		$('#id_inpatient_query').val($('#inpatient_builtQuery').text());
		$('#id_outpatient_query').val($('#outpatient_builtQuery').text());
		$('#id_prescription_query').val($('#prescription_builtQuery').text());
        $('#id_status').val(1);
        var vfdata = form.serialize();
        form.submit();
	});

    $('.btn-send-report').click(function() {
        var i=0, dataIds = [[],[],[],[],[]];
        $('.switch-claim-data-tab-wrapper .table-wrapper').each(function() {
            $(this).find('.result-row-study-id').each(function() {
                if ($(this).is(':checked')) {
                    dataIds[i].push($(this).data('id'));
                }
            });
            i += 1;
        });
        var studyChartIds = [];
        $('.js-report-study-chart').each(function() {
            if ($(this).is(":checked")) {
                studyChartIds.push(parseInt($(this).data("id")));
            }
        });
        $.ajax({
           type: "POST",
           url: '/studies/' + $('#study_id').data('id') + '/generate_report',
           dataType: "json",
           contentType: "application/json",
           data: JSON.stringify({
               'summary': $('#summary-text').val(),
               'potential_fraudulent_claims': {
                   'beneficiary': dataIds[0],
                   'carrier': dataIds[1],
                   'inpatient': dataIds[2],
                   'outpatient': dataIds[3],
                   'prescription': dataIds[4]
               },
               'included_charts': studyChartIds,
               'is_sent_to_darc': $('#send-to-darc').is(':checked')
           }),
           success: function(data)
           {
               window.location.reload();
           },
           error: function(data) {
               alert('failed to send report:' + data);
           }
         });
    });

	//Tab of action function
	$('.action').on('click','li',function(){
		//Get the current item
		var i = $('.action li').index($(this));
		//When the current item is active
		if($(this).find('a').hasClass('active')){
			$(this).find('a').removeClass('active');
			$('.action-content').hide();
			$('.action').removeClass('action-expend');
		}
		//When the current item is not active
		else{
			$('.action li a').removeClass('active');
			$(this).find('a').addClass('active');

			$('.action-content').hide();
			$('.action-content').eq(i).show("slide");
			$('.action').addClass('action-expend');
		}
	});

	//ReInit the z-index of select
	$('.visual-analysis-content .jqTransformSelectWrapper').each(function(idx){
		var total = $('.visual-analysis-content .jqTransformSelectWrapper').length;
		$(this).css({
			'z-index': total - idx
		});
	});

	//Remove function in visual analysis tab
    // NOTE that function was changed from original prototype to use delegation
	$('.visual-analysis-content .sub-tab-container .sub-tab').on('click', '.remove', function(){
		//Get index of current button
		var i = $('.visual-analysis-content .sub-tab-container li').index($(this).parents('li'));
		var $parent = $(this).parents('.sub-tab-container');
        var form=$('#delete-study-chart-form');
        $('#id_study_chart').val($parent.find('.sub-tab li').eq(i).data('id'));
		//Remove start
        //Set flag if active tab closed
        var closeActive = $parent.find('.sub-tab li').eq(i).hasClass('active');
		$parent.find('.sub-tab-content').eq(i).remove();
		$(this).parents('li').remove();
        //Activate first tab, if active was closed
        if (closeActive) {
            $parent.find('.sub-tab li').eq(0).trigger('click');
        }
		//When only one item,the remove button is hidden
		if($('.visual-analysis-content .sub-tab-container .remove').length == 1){
			$('.visual-analysis-content .sub-tab-container .remove').hide();
		}

        $.ajax({
           type: "POST",
           url: '/delete_study_charts',
           data: form.serialize(),
           success: function(data)
           {
               //do nothing
           },
           error: function(data) {
               alert('failed to delete study chart:' + data);
           }
         });
	});

	$('.details-table').hide();



	//Init the expand section in table
	$('#study-result tbody td').on('click',function(){
		var $parents = $(this).parents('tr');
		var $grandeParents = $(this).parents('#study-result');
		if(!$(this).children().hasClass('jqTransformCheckboxWrapper') && !($parents.hasClass('details'))){
			//When the rows is active
			if($parents.hasClass('active')){
				$parents.next().find('.details-table').slideUp(function(){
					$parents.removeClass('active');
				});
			}
			//When the rows is not active
			else{
				//Hide the expend details
				$grandeParents.find('.details').each(function(){
					if(!$(this).find('.details-table').is(':hidden')){
						$(this).find('.details-table').slideUp(function(){
							$(this).parents('tr').prev().removeClass('active');
						});
					}
				});
				//Set the row that is clicked active
				$parents.addClass('active');
				$parents.next().find('.details-table').slideDown().css('max-width', $grandeParents.width());
			}
		}
	});

    //Insert the expand section in table
    if($('.default-expend').length){
        $('.default-expend').each(function() {
            $(this).find('.details-table:first').parent().parent().prev().find('td:last').trigger('click');
        });
    }

     	//Insert the expand section in table
	//if($('.default-expend').length){
		//$('.default-expend').find('.details-table:first').show();
	//}

	//Hide function of profile flyout in header of page
	var CT;
	function hideFlyout(){
		if(!CT){
			CT = null;
		}
		CT = setTimeout(function(){
			$('.profile-flyout').hide();
		},3000);
	}

	//When hover on current username in header of page
	$('#header .username').hover(function(){
		clearTimeout(CT);
		$('.profile-flyout').show();;
	},function(){
		hideFlyout();
	});

    	//When click current username in header of page
	$('#header .username').on('click', function(){
		window.location.href = "/profile";
	});

	//When hover on profile flyout
	$('.profile-flyout').hover(function(){
		clearTimeout(CT);
	},function(){
		hideFlyout();
	});

	//Click the item in profile flyout
	$('.profile-flyout li').on('click',function(){
		clearTimeout(CT);
		$('.profile-flyout').hide();
	});

	//Init corner div
	var tl = $('<div></div>').addClass('corner').addClass('tl');
	var tr = $('<div></div>').addClass('corner').addClass('tr');
	var bl = $('<div></div>').addClass('corner').addClass('bl');
	var br = $('<div></div>').addClass('corner').addClass('br');

	//Corner for username in login
	$('.login-form .input-wrapper:first').append(tl).append(tr);
	$('.login-form .input-wrapper:last').append(bl).append(br);

	//Corner for sub-tab
	$('.sub-tab-container .sub-tab li').each(function(){
        if($(this).find(".sub-tab-title").length==0){
            //Init corner div
            var tl = $('<div></div>').addClass('corner').addClass('tl');
            var tr = $('<div></div>').addClass('corner').addClass('tr');
            $(this)	.append(tl).append(tr);
        }
	});
	$('.sub-tab-container .sub-tab-title').each(function(){
		//Init corner div
		var tl = $('<div></div>').addClass('corner').addClass('tl');
		var tr = $('<div></div>').addClass('corner').addClass('tr');
		$(this)	.append(tl).append(tr);
	});

	//Corner for table
	$('.home-tab-wrapper .table-wrapper').append(tl).append(tr).append(bl).append(br);
	$('.study-result-content .table-wrapper').append(tl).append(tr).append(bl).append(br);

    // choose partners
    $("#js-choose-partner").on("click",".partner-list-box li",function(){
        $(this).find("span").toggleClass("active");
        if($("#js-choose-partner").find(".partner-list-box li span:not(.active)").length == 0){
            $("#js-choose-partner").find(".js-all").text("Unselect All");
        }else{
            $("#js-choose-partner").find(".js-all").text("Select All");
        }
        $('.formset').empty();
        var i = 0;
        $("#js-choose-partner .partner-list-box li span.active").each(function() {
            $('.formset').append('<input id="id_studydatarequest_set-' + i + '-id" name="studydatarequest_set-'+ i +'-id" type="hidden" />');
            $('.formset').append('<input id="id_studydatarequest_set-' + i + '-partner" name="studydatarequest_set-' + i + '-partner" type="hidden" value="' + $(this).data("partner-id") + '"/>')
            $('.formset').append('<input id="id_studydatarequest_set-' + i + '-status" name="studydatarequest_set-'+ i +'-status" type="hidden" value="0" />');
            $('.formset').append('<input id="id_studydatarequest_set-' + i + '-response_data" name="studydatarequest_set-'+ i +'-response_data" type="hidden" value="" />');
            i += 1;
        });
        $('#id_studydatarequest_set-TOTAL_FORMS').val(i);
        $('#id_studydatarequest_set-INITIAL_FORMS').val(0);
        return false;
    });
    $("#js-choose-partner").find(".js-all").click(function(){
        if($(this).text() == "Select All"){
            $("#js-choose-partner").find(".partner-list-box li span").addClass("active");
            $(this).text("Unselect All");
        }else{
            $("#js-choose-partner").find(".partner-list-box li span").removeClass("active");
            $(this).text("Select All");
        }
    })
    placeholder("#search-parter-tags-id", "Search partner or tags");

    if ($(".js-study-edit-page").length) {
        $('#js-choose-partner .partner-list-box li').trigger('click');
    }

    if ($('#operate-result').length) {
        operateResult = $('#operate-result').data('result');
        if (operateResult === 'saved') {
            closeModal();
            loadModal('#modal-save-draft');
        } else if (operateResult === 'executed') {
            closeModal();
            loadModal('#modal-save-execute');
        }
    }
});

/*  partner management functions */
(function(){
     $(function(){

         if($("#partnerPage").length==0) return;

        //init placeholder
        placeholder(".tags-search .search-tags-field", "Search tags");

        /**
    	 * Modal appear when click contacts list. This function is modified so that the detail
    	 * modal is filled with the expected partner information.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        //Modal appear when click contacts list
        $('.js-action-contact').live('click',function(){
            closeModal();
             // Copy data from formset to details modal
        	var full_name_id = $(this).closest('tr').find('.js-partner-contact-full_name_id').text();
        	contactsBackToDetail(full_name_id);
        	// load modal
            loadModal('#modal-contact-details');
        });

        //Modal appear when click add new contact button
        $('.js-new-contact').on('click',function(){
            closeModal();
            $('.js-contact-add-error-infor').hide();
            $('.js-contact-add-error-infor').val('');
            $('.js-contact-edit-error-infor').hide();
            $('.js-contact-edit-error-infor').val('');
            loadModal('#modal-contact-new');
        });


        /**
    	 * Modal appear when click edit contact button. This function is modified so that the
    	 * contact edit page will be filled with the expected partner information.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        //Modal appear when click edit contact button
        $('.js-contact-edit').live('click',function(){
           var full_name_id = $(this).closest('tr').find('.js-partner-contact-full_name_id').text();
        	// copy data from formset to the popup dialog
        	contactsBackToPopup(full_name_id);
            closeModal();
            loadModal('#modal-contact-edit');
        });

        /**
    	 * Modal appear when click delete partner button. This function is modified so that the
    	 * delete modal contains information of the partner id.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        //Modal appear when click delete partner button
        $('.js-action-partner-delete').live('click',function(){
            	// Set value of the modal dialog
            var partnerId = $('#partner-detail-box .form-body input.partnerId').val();
        	$('#modal-delete-partner form .partnerId').val(partnerId);
            closeModal();
            loadModal('#modal-delete-partner');
        });

         /**
    	 * Used to post delete partner request to user.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $('.js-action-save-delete-partner').on('click', function() {
        	// submit to server
        	$(this).parents('form').submit();
			closeModal();
		});

         	/**
    	 * Redirect to darc_emails/create page after clicked add button.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $('.js-action-email-add').live('click',function(){
        	window.location.href = "/darc_emails/create";
        });

    	/**
    	 * Delete one email.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $('.js-action-email-delete').live('click',function(){
        	// Set value of the modal dialog
        	var emailId = $(this).closest('form').find('input:last').val();
        	$('#modal-delete-partner form input:last').val(emailId);

            closeModal();
            loadModal('#modal-delete-partner');
        });

        $('.js-action-save-delete-email').live('click', function() {
        	$(this).closest('form').submit();
		});

    	/**
    	 * Redirect to create user page.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $('.js-action-user-add').live('click',function(){
        	window.location.href = "/admin/auth/user/add";
        });

    	/**
    	 * Redirect to delete user page.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $('.js-action-user-delete').live('click',function(){
        	// Set value of the modal dialog
            $('#user-delete-list').html('');
            var existDelete = false;
			var existDeleteSelf = false;
            $('.users-table-tbody tbody input:checkbox').each(function(idx){
                if (!$(this).attr('checked')) {
                    return true;
                }
                if ($('.welcome-widget a.username').text() === $(this).closest('tr').find('th a').text()) {
                    existDelete = false;
					existDeleteSelf = true;
                    alert('Cannot delete yourself');
                    return false;
                } else {
                    existDelete = true;
                    var editUrl = $(this).closest('tr').find('th a').attr('href');
                    userId = editUrl.split('/')[4].toString();
                    $('#user-delete-list').append('<input type="hidden" name="_selected_action" value="' + userId + '" />');
                    return true;
                }
            });

            if (existDelete) {
                closeModal();
                loadModal('#modal-delete-partner');
            } else if(!existDeleteSelf){
                alert('Please select user first.');
            }
        });

    	/**
    	 * Used to post delete users request to user.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $('.js-action-save-delete-user').on('click', function() {
        	// submit to server
        	$(this).parents('form').submit();
			closeModal();
			//window.location.href = "/admin/auth/user";
		});

    	/**
    	 * Save the added partner tag.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $(".js-action-save-new-tag").on("click", function(){
            // Send AJAX request to server
            onCreateSaveTag();
            // closeModal() will be called after AJAX returned.
        });

    	/**
    	 * Cancel adding partner tag.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $(".js-action-cancel-new-tag").on("click", function(){
            closeModal();
        });

    	/**
    	 * Send search request to server. This function responds to every search button.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $(".btn-search").on("click", function() {
			$(this).closest('form').submit();
		});

    	/**
    	 * Navigate to next page.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $(".btn-next").on("click", function() {
        	window.location.href = $(this).siblings('.nextHref').text();
		});

    	/**
    	 * Navigate to previous page.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $(".btn-previous").on("click", function() {
        	window.location.href = $(this).siblings('.previousHref').text();
		});

    	/**
    	 * Send request to change the number of partners shown in each page.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $("#tab-partners .pagination div.jqTransformSelectWrapper ul li a").click(function(){
        	var value = $(this).parents('.jqTransformSelectWrapper').find('span').text();
        	window.location.href = "/partners?page_size=" + value;
            return false; //prevent default browser action
        });

    	/**
    	 * Send request to change the number of partner tags shown in each page.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $("#tab-tags .pagination div.jqTransformSelectWrapper ul li a").click(function(){
        	var value = $(this).parents('.jqTransformSelectWrapper').find('span').text();
        	window.location.href = "/partner_tags?page_size=" + value;
            return false; //prevent default browser action
        });

    	/**
    	 * Send request to change the number of emails shown in each page.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $("#tab-emails .pagination div.jqTransformSelectWrapper ul li a").click(function(){
        	var value = $(this).parents('.jqTransformSelectWrapper').find('span').text();
        	window.location.href = "/darc_emails?page_size=" + value;
            return false; //prevent default browser action
        });

        //Modal appear when click delete partner button
        $('.js-action-tag-add').live('click',function(){
            closeModal();
            loadModal('#modal-tag-add');
        });

        /**
    	 * Modal appear when click edit tag button. Name/description/tagId will be trasmitted
    	 * to the modal.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        //Modal appear when click delete partner button
        $('.js-action-tag-edit').live('click',function(){
            var trParent = $(this).parents('tr');
        	var name = trParent.find('.cell-tag-name strong').html();
        	var description = trParent.find('.cell-tag-description p').html();
        	var id = trParent.find('.hide').html();
        	$("#modal-tag-edit input[name='name']").val(name);
        	$("#modal-tag-edit .textarea-section").html(description);
        	$("#modal-tag-edit .textarea-section").val(description);
        	$("#modal-tag-edit input[name='id']").val(id);
            closeModal();
            $('#modal-tag-edit .js-tag-edit-error-infor').hide();
            loadModal('#modal-tag-edit');
        });

         	/**
    	 * Save the modifed tag by AJAX request.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $(".js-action-save-edit-tag").on("click", function(){
        	var hasError = false;
        	var emptyRE = /^\s*$/;
        	// First hide all error information.
        	$('#modal-tag-edit .js-tag-edit-error-infor').hide();
        	// Check each input then.
        	if (emptyRE.test($('#modal-tag-edit form dd input').val())) {
        		$('#tag-edit-error-infor-name').show();
    			hasError = true;
    		}
        	if (emptyRE.test($('#modal-tag-edit form dd textarea').val())) {
        		$('#tag-edit-error-infor-description').show();
    			hasError = true;
    		}
        	if (hasError) {
				return;
			}

            // Send AJAX request to server
            onEditSaveTag();
            // closeModal() will be called after AJAX returned.
        });

    	/**
    	 * Cancel saving the modified tag.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $(".js-action-cancel-edit-tag").on("click", function(){
            closeModal();
        });

        //Modal appear when click delete partner button

        $('#modal-background').live('click',function(){
            closeModal();
        });

        /**
    	 * Add tag for a partner. We did checing here for duplicated tags.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        //add tag
        $(".js-action-add-tag").click(function(){
            var tagsControl = $(this).closest('.tags-add').find('select');
            var list = $(this).closest('.tags-add').siblings(".tags-lists");
            var tagId = tagsControl.find("option:selected").val();
            var tagName = tagsControl.find("option:selected").text();
            if(tagId == 0) return false;
            if (checkTagExist(list.find('.tagId'), tagId)) { // the tag already added
				return false;
			}
            var newTagsText = '';
            newTagsText = newTagsText + '<div class="hide tagId">' + tagId + '</div>';
            newTagsText = newTagsText + '<div class="mark-tag mark-tag-edit tagName"><span><span>'
        	+ tagName + '</span></span><a href="javascript:;" class="mark-delete"></a></div>';
            var html = $(newTagsText);
            list.show();
            list.find(".mark-tag-last-clear").before(html);
        });

        /**
    	 * Check if the tag has already been added in the jqTagIds.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        var checkTagExist = function(jqTagIds, tagId) {
        	var isExist = false;
        	jqTagIds.each(function(i, val) {
				if (val.textContent == tagId) {
					isExist = true;
				}
			});
        	return isExist;
        }

    	/**
    	 * Delete one tag from a partner. It is modified so that the elements recording its
    	 * tag id will also be removed.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        //delete tag mark
        $(".mark-tag-edit .mark-delete").live("click", function(){
            if($(this).closest(".tags-lists").find(".mark-tag").length ==1){
                $(this).closest(".tags-lists").addClass("hide").hide();
            }
            $(this).closest(".mark-tag").prev().remove();
            $(this).closest(".mark-tag").remove();
        });

        //delete tag
        $('.js-action-tag-delete').live('click',function(){
           	// Sub mit to 'delete_partner_tags' with ids setted.
        	//

        	// Set value of the modal dialog
        	var tagId = $(this).closest('form').find('input:last').val();
        	$('#modal-delete-tag form input:last').val(tagId);

            closeModal();
            loadModal('#modal-delete-tag');

            //$(this).closest('tr').remove();
            $("#tab-tags table tbody").find('tr').removeClass('odd even');
            $("#tab-tags table tbody").find('tr:odd').addClass('odd');
            $("#tab-tags table tbody").find('tr:even').addClass('even');
        });

          // finnally delete tag
        $('.js-action-save-delete-tag').live('click', function() {
        	$(this).closest('form').submit();
		});

    	/**
    	 * Delete row when click delete button. The modification will be copy to the background
    	 * formset, and then shown in partner details.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        //delete row when click delete button
        $(".form-contact-list .js-contact-delete").live('click',function(){
            	// Get the company_name_id and remove it from back
        	var full_name_id = $(this).closest('tr').find('.js-partner-contact-full_name_id').text();
        	deletBackContact(full_name_id);

            // Copy the value of django dynamic form-set
            contactsBackToList(true);
            /*var contactList = $(this).closest(".form-contact-list");
            if(contactList.find('table tbody tr').length == 1){
                contactList.addClass("hide");
            }
            $(this).closest('tr').remove();
            contactList.find('table tbody tr').removeClass('odd even');
            contactList.find('table tbody tr:odd').addClass('odd');
            contactList.find('table tbody tr:even').addClass('even');*/
        });

          	/**
    	 * Add contact row when click add button in add new contact modal.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $('#modal-contact-new .js-modal-add-contact').on('click',function(){
        	if(!checkContactAddValid()) {
			closeModal();
			loadModal('#modal-contact-new');
        		return;
        	}

            closeModal();
            // Update the django dynamic form-set
            $("#hidden-contact-edit .add-row").trigger("click");
            var lastTable = $('#hidden-contact-edit .dynamic-form:last');
            var lastId = lastTable.find('td:first input').attr('id');
            lastTable.find('.js-partnercontact-full_name').val($('#contact-add-full-name').val());
            lastTable.find('.js-partnercontact-job_title').val($('#contact-add-job-title').val());
            lastTable.find('.js-partnercontact-company_name').val($('#contact-add-company-name').val());
            lastTable.find('.js-partnercontact-office_address').val($('#contact-add-office-address').val());
            lastTable.find('.js-partnercontact-email').val($('#contact-add-email').val());
            lastTable.find('.js-partnercontact-phone').val($('#contact-add-phone').val());
            lastTable.find('.js-partnercontact-notes').html($('#contact-add-notes').val());
            lastTable.find('.js-partnercontact-will-delete').val('off');

            // Copy the value of django dynamic form-set
            contactsBackToList(false);
        });
        //add contact row when click add button in add new contact modal
        /*$('#modal-contact-new .js-modal-add').on('click',function(){
            closeModal();
            var contactList = $(".partner-main-content:visible").find(".form-contact-list");
            var html = new Array();
            html.push('<tr>');
            html.push('<td class="cell-contact-name">');
            html.push('<a href="javascript:;" class="color-orange js-action-contact">Firstname Latname</a>');
            html.push('<p>Job Title lorem ipsum</p>');
            html.push('</td>');
            html.push('<td class="cell-contact-infor">');
            html.push('<p>email@email.com</p>');
            html.push('<strong>123 456 7890</strong>');
            html.push('</td>');
            html.push('<td class="cell-contact-action">');
            html.push('<div class="button btn-blue-dark btn-blue-edit js-contact-edit"><span><span><span class="icon">EDIT</span></span></span></div>');
            html.push('<div class="button btn-blue-dark btn-blue-delete js-contact-delete"><span><span><span class="icon">DELETE</span></span></span></div>');
            html.push('</td>');
            html.push('</tr>');

            contactList.removeClass("hide");
            contactList.find("table tbody .empty-row").remove();
            contactList.find("table tbody").append(html.join(""));
            contactList.find('table tbody tr').removeClass('odd even');
            contactList.find('table tbody tr:odd').addClass('odd');
            contactList.find('table tbody tr:even').addClass('even');
        });*/


         	/**
    	 * Add contact row when click add button in add new contact modal.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $('#modal-contact-edit .js-modal-update-contact').on('click',function(){
        	if (!checkContactEditValid()) {
				closeModal();
				loadModal('#modal-contact-edit');
				return;
			}

            closeModal();
            var full_name_id = $('#contact-edit-full_name_id').html();
            $("#hidden-contact-edit .dynamic-form").each(function(){
            	if ($(this).find('.js-partnercontact-full_name').attr('id') == full_name_id) {
                    // Update the django dynamic form-set
                    var lastTable = $(this);
                    lastTable.find('.js-partnercontact-full_name').val($('#contact-edit-full-name').val());
                    lastTable.find('.js-partnercontact-job_title').val($('#contact-edit-job-title').val());
                    lastTable.find('.js-partnercontact-company_name').val($('#contact-edit-company-name').val());
                    lastTable.find('.js-partnercontact-office_address').val($('#contact-edit-office-address').val());
                    lastTable.find('.js-partnercontact-email').val($('#contact-edit-email').val());
                    lastTable.find('.js-partnercontact-phone').val($('#contact-edit-phone').val());
                    lastTable.find('.js-partnercontact-notes').html($('#contact-edit-notes').val());
                    lastTable.find('.js-partnercontact-will-delete').val('off');
                    // terminate each loop
                    return false;
    			}
            });

            // Copy the value of django dynamic form-set
            contactsBackToList(true);
        });


        //change tab to tags tab
        $(".js-link-tags").on("click", function(){
            $(".partner-tab-container .tab li:eq(1)").trigger("click");
        });

         /**
    	 * Swith to add partner box. This functon is modified so that it loads a
    	 * default empty formset from the background contact formset.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $(".js-action-add-partner").on("click", function(){
            $("#tab-partners .partner-main-content").hide();
            $("#partner-add-box").show();
            $('#partner-add-box').removeClass('error');
    		$('.js-partner-add-error-infor').hide();
            // refresh contact area with default formset
            refreshContactFormset(0);
            clearNavSide();
        });

    	/**
    	 * Send AJAX request to finally save the partner.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $(".js-action-save-add-partner").on("click", function(){
        	if (!checkPartnerAddValid()) {
				return;
			}
            clearNavSide();
            // Send AJAX request to server
            onCreateSavePartner();
        });

    	/**
    	 * Cancel saving the partner.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $(".js-action-no-selected").on("click", function(){
            $("#tab-partners .partner-main-content").hide();
            $("#partner-no-selected-placeholder").show();
            clearNavSide();
            $('#partner-add-box').removeClass('error');
        });

         	/**
    	 * Send AJAX request to finally save the email.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $(".js-action-save-add-email").on("click", function(){
        	$(this).closest('form').submit();
        });

    	/**
    	 * Cancel saving the newly created email.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $(".js-action-cancel-add-email").on("click", function(){
        	history.go(-1);
        });

    	/**
    	 * Send AJAX request to finally save the newly created user.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $(".js-action-save-add-user").on("click", function(){
        	$(this).closest('form').submit();
        });

    	/**
    	 * Cancel saving the newly created user.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $(".js-action-cancel-add-user").on("click", function(){
        	window.location.href = "/admin/auth/user";
        });

    	/**
    	 * Update user information.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $(".js-action-save-edit-user").on("click", function(){
        	$(this).closest('form').submit();
        });

    	/**
    	 * Cancel updating user information.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $(".js-action-cancel-edit-user").on("click", function(){
        	window.location.href = "/admin/auth/user";
        });

        /**
    	 * Show partner details page.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $(".js-action-partner-details").on("click", function(){
            $(".js-action-partner-details").removeClass("active");
            $(this).addClass("active");
            $("#tab-partners .partner-main-content").hide();

            // collect data from hide elements
            var details = $(this).parent().prev();
            var partnerId = details.find('.partnerId').html();
            var hfpp_network_id = details.find('.hfpp_network_id').html();
            var company_name = details.find('.company').html();
            var city = details.find('.city').html();
            var state = details.find('.state').html();
            var division = details.find('.division').html();
            var region = details.find('.region').html();
            var number_of_insured = details.find('.noi').html();
            var network_username = details.find('.username').html();
            var network_organization_name = details.find('.orgname').html();
            var network_role = details.find('.role').html();
            var network_auto_retrieve_cached_data = details.find('.cached').html();
            var tagsId = details.find('.tagId');
            var tagsName = details.find('.tagName');

            // assign values to the corresponding fields
            details = $('#partner-detail-box .form-body');
            details.find('input.partnerId').val(partnerId);
            details.find('.hfpp_network_id .label').html(hfpp_network_id);
            details.find('.company .label').html(company_name);
            details.find('.city .label').html(city);
            details.find('.state div:first').html(state);
            details.find('.state .label').html(
            		$('#partner-edit-state option[value="' + state + '"]').html());
            details.find('.division .label').html(division);
            details.find('.region .label').html(region);
            details.find('.noi .label').html(number_of_insured);
            details.find('.username .label').html(network_username);
            details.find('.orgname .label').html(network_organization_name);
            details.find('.role .label').html($('#partner-edit-network-role option[value="' + network_role + '"]').html());
            details.find('.cached .label').html(network_auto_retrieve_cached_data);
            var newTagsText = '';
            tagsId.each(function(idx, val) {
            	newTagsText = newTagsText + '<div class="hide tagId">' + val.textContent + '</div>';
			});
            tagsName.each(function(idx, val) {
            	newTagsText = newTagsText + '<div class="mark-tag"><span><span class="tagName">'
            	+ val.textContent + '</span></span></div>';
			});
            newTagsText = newTagsText + '<div class="clear"></div>';
            details.find('.tags-lists').html(newTagsText);

            $("#partner-detail-box").show();

            // Get Contact information
            refreshContactFormset(partnerId);
        });

    	/**
    	 * Show partner edit page.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $(".js-action-partner-edit").on("click", function(){
            $("#tab-partners .partner-main-content").hide();

            // collect data from hide elements
            var details = $('#partner-detail-box .form-body');
            var partnerId = details.find('input.partnerId').val();
            var hfpp_network_id = details.find('.hfpp_network_id .label').html();
            var company_name = details.find('.company .label').html();
            var city = details.find('.city .label').html();
            var state = details.find('.state div:first').html();
            var division = details.find('.division .label').html();
            var region = details.find('.region .label').html();
            var number_of_insured = details.find('.noi .label').html();
            var network_username = details.find('.username .label').html();
            var network_orgnization_name = details.find('.orgname .label').html();
            var network_role = details.find('.role .label').html();
            var network_auto_retrieve_cached_data = details.find('.cached .label').html();
            var tagsId = details.find('.tagId');
            var tagsName = details.find('.tagName');

            // assign values
            details = $('#partner-edit-box .form-body');
            details.find('#partner-edit-partner-id').val(partnerId);
            details.find("#partner-edit-hfpp_network_id").val(hfpp_network_id);
            details.find("#partner-edit-company-name").val(company_name);
            details.find('#partner-edit-city').val(city);
            details.find('#partner-edit-state').val(state);
            // We must set this because of jsTransform library bugs
//            details.find("#partner-edit-state").parents('.jqTransformSelectWrapper').find('span').html(
//            		details.find("#partner-edit-state").find("option:selected").text());
            var selectWrapper = details.find("#partner-edit-state").parents('.jqTransformSelectWrapper');
            var selectedIndex = parseInt(state);
    		selectWrapper.find('ul li a').removeClass('selected');
    		selectWrapper.find('ul li a').eq(selectedIndex).addClass('selected');
    		selectWrapper.find('div span').html(selectWrapper.find('ul li a').eq(selectedIndex).html());
    		selectWrapper.find('select').val('' + selectedIndex);

            details.find('#partner-edit-division').val(division);
            details.find('#partner-edit-region').val(region);
            details.find('#partner-edit-number-insured').val(number_of_insured);
            details.find('#partner-edit-network-username').val(network_username);
            details.find('#partner-edit-network-organization-name').val(network_orgnization_name);

            selectWrapper = details.find("#partner-edit-network-role").parents('.jqTransformSelectWrapper');
            var i = 0, network_role_id;
            selectWrapper.find('option').each(function() {
                if ($(this).text() == network_role) {
                    selectedIndex = i;
                    network_role_id = $(this).attr("value");
                }
                i += 1;
            });
            selectWrapper.find('ul li a').removeClass('selected');
            selectWrapper.find('ul li a').eq(selectedIndex).addClass('selected');
            selectWrapper.find('div span').html(selectWrapper.find('ul li a').eq(selectedIndex).html());
            selectWrapper.find('select').val(network_role_id);

            selectWrapper = details.find("#partner-edit-network-auto-retrieve-cached-data").parents('.jqTransformSelectWrapper');
            selectedIndex = network_auto_retrieve_cached_data.toLowerCase() === 'true' ? 0 : 1;
            selectWrapper.find('ul li a').removeClass('selected');
            selectWrapper.find('ul li a').eq(selectedIndex).addClass('selected');
            selectWrapper.find('div span').html(selectWrapper.find('ul li a').eq(selectedIndex).html());
            selectWrapper.find('select').val(network_auto_retrieve_cached_data.toLowerCase());


            var newTagsText = '';
            tagsId.each(function(idx, val) {
            	newTagsText = newTagsText + '<div class="hide tagId">' + val.textContent + '</div>';
			});
            tagsName.each(function(idx, val) {
            	newTagsText = newTagsText + '<div class="mark-tag mark-tag-edit tagName"><span><span>'
            	+ val.textContent + '</span></span><a href="javascript:;" class="mark-delete"></a></div>';
			});
            newTagsText = newTagsText + '<div class="clear mark-tag-last-clear"></div>';
            details.find('.tags-lists').html(newTagsText);

            $("#partner-edit-box").show();
            $("#partner-edit-box").removeClass('error');
            // refresh the contract data
            contactsBackToList(true);
        });

    	/**
    	 * Save the updated partner.
    	 *
    	 * @author caoweiquan322
    	 * @version 1.0
    	 */
        $(".js-action-save-edit-partner").on("click", function(){
        	// check if input is valid
        	if(!checkPartnerEditValid()) {
        		return;
        	}
            clearNavSide();
            // Send AJAX request to server
            onEditSavePartner();
        });
        //click add partners to change content
       /* $(".js-action-add-partner").on("click", function(){
            $("#tab-partners .partner-main-content").hide();
            $("#partner-add-box").show();
            clearNavSide();
        });*/

        //save new partner
        /*$(".js-action-save-detail").on("click", function(){
            $("#tab-partners .partner-main-content").hide();
            $("#partner-detail-box").show();
            clearNavSide();
        });*/

        //cancel adding new partner
        /*$(".js-action-no-selected").on("click", function(){
            $("#tab-partners .partner-main-content").hide();
            $("#partner-no-selected-placeholder").show();
            clearNavSide();
        });*/

        //show details page
        /*$(".js-action-partner-details").on("click", function(){
            $(".js-action-partner-details").removeClass("active");
            $(this).addClass("active");
            $("#tab-partners .partner-main-content").hide();
            $("#partner-detail-box").show();
        });*/

        //edit partner
       /* $(".js-action-partner-edit").on("click", function(){
            $("#tab-partners .partner-main-content").hide();
            $("#partner-edit-box").show();
        });*/
     });

    var clearNavSide = function(){
        $(".js-action-partner-details").removeClass("active");
    }
    /**
	 * Check if partner input box contains error.
	 *
	 * @author caoweiquan322
	 * @version 1.0
	 */
    var checkPartnerAddValid = function() {
    	var hasError = false;
    	var emptyRE = /^\s*$/;
    	var positiveNumber = /^\d+$/;
    	// First hide all partner error information.
    	$('#partner-add-box').removeClass('error');
		$('.js-partner-add-error-infor').hide();
		$('.js-partner-add-error-infor').html('');
    	// Check each input then.
    	if (emptyRE.test($('#partner-hfpp_network_id').val())) {
    		$('#partner-add-error-infor-hfpp_network_id').html('Hfpp Network ID should not be empty.');
    		$('#partner-add-error-infor-hfpp_network_id').show();
			hasError = true;
		}
    	if (emptyRE.test($('#partner-company-name').val())) {
    		$('#partner-add-error-infor-company_name').html('Company name should not be empty.');
    		$('#partner-add-error-infor-company_name').show();
			hasError = true;
		}
    	if (emptyRE.test($('#partner-city').val())) {
    		$('#partner-add-error-infor-city').html('City should not be empty');
    		$('#partner-add-error-infor-city').show();
			hasError = true;
		}
    	if (emptyRE.test($('#partner-division').val())) {
    		$('#partner-add-error-infor-division').html('Division should not be empty');
    		$('#partner-add-error-infor-division').show();
			hasError = true;
		}
    	if (emptyRE.test($('#partner-region').val())) {
    		$('#partner-add-error-infor-region').html('Region should not be empty');
    		$('#partner-add-error-infor-region').show();
			hasError = true;
		}
    	if (!positiveNumber.test($('#partner-number-insured').val())) {
    		$('#partner-add-error-infor-noi').html('Number of insured is invalid');
    		$('#partner-add-error-infor-noi').show();
			hasError = true;
		}
        if (emptyRE.test($('#partner-network-username').val())) {
            $('#partner-add-error-infor-username').html('Username should not be empty');
            $('#partner-add-error-infor-username').show();
            hasError = true;
        }
        if (emptyRE.test($('#partner-network-password').val())) {
            $('#partner-add-error-infor-password').html('Password should not be empty');
            $('#partner-add-error-infor-password').show();
            hasError = true;
        }
        if (emptyRE.test($('#partner-network-organization-name').val())) {
            $('#partner-add-error-infor-orgname').html('Organization Name should not be empty');
            $('#partner-add-error-infor-orgname').show();
            hasError = true;
        }
    	if (hasError) {
    		$('#partner-add-box').addClass('error');
    	}

    	return !hasError;
    }

	/**
	 * Check if contact input box contains error.
	 *
	 * @author caoweiquan322
	 * @version 1.0
	 */
    var checkContactAddValid = function() {
    	var hasError = false;
    	var emptyRE = /^\s*$/;
    	var emailRE = /^\w+@[\w\.]+$/;
    	var phoneRE = /^\d+$/;
    	// First hide all contacts error information.
    	$('.js-contact-add-error-infor').hide();
    	// Check each input then.
    	if (emptyRE.test($('#contact-add-full-name').val())) {
    		$('#contact-add-error-infor-full-name').show();
			hasError = true;
		}
    	if (emptyRE.test($('#contact-add-job-title').val())) {
    		$('#contact-add-error-infor-job-title').show();
			hasError = true;
		}
    	if (emptyRE.test($('#contact-add-company-name').val())) {
    		$('#contact-add-error-infor-company-name').show();
			hasError = true;
		}
    	if (emptyRE.test($('#contact-add-office-address').val())) {
    		$('#contact-add-error-infor-office-address').show();
			hasError = true;
		}
    	if (!emailRE.test($('#contact-add-email').val())) {
    		$('#contact-add-error-infor-email').show();
			hasError = true;
		}
    	if (!phoneRE.test($('#contact-add-phone').val())) {
    		$('#contact-add-error-infor-phone').show();
			hasError = true;
		}
    	if (emptyRE.test($('#contact-add-notes').val())) {
    		$('#contact-add-error-infor-notes').show();
			hasError = true;
		}

    	return !hasError;
    }

	/**
	 * Check if partner input box contains error.
	 *
	 * @author caoweiquan322
	 * @version 1.0
	 */
    var checkPartnerEditValid = function() {
    	var hasError = false;
    	var emptyRE = /^\s*$/;
    	var positiveNumber = /^\d+$/;
    	// First hide all partner error information.
    	$('#partner-edit-box').removeClass('error');
		$('.js-partner-edit-error-infor').hide();
		$('.js-partner-edit-error-infor').html('');
    	// Check each input then.
    	if (emptyRE.test($('#partner-edit-hfpp_network_id').val())) {
    		$('#partner-edit-error-infor-hfpp_network_id').html('Hfpp Network ID should not be empty.');
    		$('#partner-edit-error-infor-hfpp_network_id').show();
			hasError = true;
		}
    	if (emptyRE.test($('#partner-edit-company-name').val())) {
    		$('#partner-edit-error-infor-company_name').html('Company name should not be empty.');
    		$('#partner-edit-error-infor-company_name').show();
			hasError = true;
		}
    	if (emptyRE.test($('#partner-edit-city').val())) {
    		$('#partner-edit-error-infor-city').html('City should not be empty');
    		$('#partner-edit-error-infor-city').show();
			hasError = true;
		}
    	if (emptyRE.test($('#partner-edit-division').val())) {
    		$('#partner-edit-error-infor-division').html('Division should not be empty');
    		$('#partner-edit-error-infor-division').show();
			hasError = true;
		}
    	if (emptyRE.test($('#partner-edit-region').val())) {
    		$('#partner-edit-error-infor-region').html('Region should not be empty');
    		$('#partner-edit-error-infor-region').show();
			hasError = true;
		}
    	if (!positiveNumber.test($('#partner-edit-number-insured').val())) {
    		$('#partner-edit-error-infor-noi').html('Number of insured is invalid');
    		$('#partner-edit-error-infor-noi').show();
			hasError = true;
		}
        if (emptyRE.test($('#partner-edit-network-username').val())) {
            $('#partner-edit-error-infor-username').html('Username should not be empty');
            $('#partner-edit-error-infor-username').show();
            hasError = true;
        }
        if (emptyRE.test($('#partner-edit-network-organization-name').val())) {
            $('#partner-edit-error-infor-orgname').html('Organization Name should not be empty');
            $('#partner-edit-error-infor-orgname').show();
            hasError = true;
        }
    	if (hasError) {
    		$('#partner-edit-box').addClass('error');
    	}

    	return !hasError;
    }

	/**
	 * Check if contact input box contains error.
	 *
	 * @author caoweiquan322
	 * @version 1.0
	 */
    var checkContactEditValid = function() {
    	var hasError = false;
    	var emptyRE = /^\s*$/;
    	var emailRE = /^\w+@[\w\.]+$/;
    	var phoneRE = /^\d+$/;
    	// First hide all contacts error information.
    	$('.js-contact-edit-error-infor').hide();
    	// Check each input then.
    	if (emptyRE.test($('#contact-edit-full-name').val())) {
    		$('#contact-edit-error-infor-full-name').show();
			hasError = true;
		}
    	if (emptyRE.test($('#contact-edit-job-title').val())) {
    		$('#contact-edit-error-infor-job-title').show();
			hasError = true;
		}
    	if (emptyRE.test($('#contact-edit-company-name').val())) {
    		$('#contact-edit-error-infor-company-name').show();
			hasError = true;
		}
    	if (emptyRE.test($('#contact-edit-office-address').val())) {
    		$('#contact-edit-error-infor-office-address').show();
			hasError = true;
		}
    	if (!emailRE.test($('#contact-edit-email').val())) {
    		$('#contact-edit-error-infor-email').show();
			hasError = true;
		}
    	if (!phoneRE.test($('#contact-edit-phone').val())) {
    		$('#contact-edit-error-infor-phone').show();
			hasError = true;
		}
    	if (emptyRE.test($('#contact-edit-notes').val())) {
    		$('#contact-edit-error-infor-notes').show();
			hasError = true;
		}

    	return !hasError;
    }

	/**
	 * Send AJAX request to save the created partner.
	 *
	 * @author caoweiquan322
	 * @version 1.0
	 */
	var onCreateSavePartner = function() {
        if (onCreateSavePartner.isRunning) {
            return;
        }
        onCreateSavePartner.isRunning = true;
		var xmlhttp;
		if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
			xmlhttp = new XMLHttpRequest();
		} else {// code for IE6, IE5
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		xmlhttp.onreadystatechange = function() {
            onCreateSavePartner.isRunning = false;
			if (xmlhttp.responseText == '') {
				return;
			}
			var response = eval('(' + xmlhttp.responseText + ')');
			if (xmlhttp.readyState==4 && xmlhttp.status==200 && !response.errors) {
				// Disable error message.
				$('#partner-add-box').removeClass('error');
				$('.js-partner-add-error-infor').hide();
				// redirect url
				window.location.href = "/partners?page=last";
				// Hide this content and show detail of new added partner
	            $("#tab-partners .partner-main-content").hide();
	            $("#partner-detail-box").show();
	            $('.partner-lists-container .partner-list-box li:last').find('span').trigger('click');
			} else if (response.errors) {
				// Show error message.
				$('#partner-add-box').addClass('error');
				$('.js-partner-add-error-infor').hide();
				$('.js-partner-add-error-infor').html('');
				if (response.errors.hfpp_network_id) {
					$('#partner-add-error-infor-hfpp_network_id').html('hfpp_network_id: ' + response.errors.hfpp_network_id);
					$('#partner-add-error-infor-hfpp_network_id').show();
				}
				if (response.errors.company_name) {
					$('#partner-add-error-infor-company_name').html('company_name: ' + response.errors.company_name);
					$('#partner-add-error-infor-company_name').show();
				}
				if (response.errors.city) {
					$('#partner-add-error-infor-city').html('city: ' + response.errors.city);
					$('#partner-add-error-infor-city').show();
				}
				if (response.errors.state) {
					$('#partner-add-error-infor-state').html('state: ' + response.errors.state);
					$('#partner-add-error-infor-state').show();
				}
				if (response.errors.division) {
					$('#partner-add-error-infor-division').html('division: ' + response.errors.division);
					$('#partner-add-error-infor-division').show();
				}
				if (response.errors.region) {
					$('#partner-add-error-infor-region').html('region: ' + response.errors.region);
					$('#partner-add-error-infor-region').show();
				}
				if (response.errors.number_of_insured) {
					$('#partner-add-error-infor-noi').html('number_of_insured: ' + response.errors.number_of_insured);
					$('#partner-add-error-infor-noi').show();
				}
				if (response.errors.network_username) {
                    $('#partner-add-error-infor-username').html('username: ' + response.errors.network_username);
                    $('#partner-add-error-infor-username').show();
				}
                if (response.errors.network_password) {
                    $('#partner-add-error-infor-password').html('password: ' + response.errors.network_password);
                    $('#partner-add-error-infor-password').show();
                }
                if (response.errors.network_organization_name) {
                    $('#partner-add-error-infor-orgname').html('organization name: ' + response.errors.network_organization_name);
                    $('#partner-add-error-infor-orgname').show();
                }
                if (response.errors.network_role) {
                    $('#partner-add-error-infor-role').html('role: ' + response.errors.network_role);
                    $('#partner-add-error-infor-role').show();
                }
                if (response.errors.network_auto_retrieve_cached_data) {
                    $('#partner-add-error-infor-cached').html('network auto retrieve cached data: ' + response.errors.network_auto_retrieve_cached_data);
                    $('#partner-add-error-infor-cached').show();
                }
				if (response.errors.tags) {
					$('#partner-add-error-infor-tags').html('tags: ' + response.errors.tags);
					$('#partner-add-error-infor-tags').show();
				}
			}
		}
		xmlhttp.open("POST", "partners/create", true);
		xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xmlhttp.setRequestHeader("X_REQUESTED_WITH", "XMLHttpRequest");
		// Send serialized form to server
		var requestText = $("#partner-add-form").serialize();

		// Add tags
		var tagsText = '';
		var selectedTags = $('#partner-add-form .tags-lists .tagId');
		selectedTags.each(function(i, val) {
			tagsText = tagsText + '&tags=' + val.textContent;
		})
		if (tagsText.length > 1) {
			requestText = requestText + tagsText;
		}

		// Add contacts
		var contactsText = $('#hidden-contact-edit form').serialize();
		if (contactsText.length > 1) {
			requestText = requestText + '&' + contactsText;
		}
		xmlhttp.send(requestText);
	}

	/**
	 * Get contact contact information from server and fill in the hidden formset.
	 *
	 * @author caoweiquan322
	 * @version 1.0
	 */
	var refreshContactFormset = function(partnerId) {
		var xmlhttp;
		if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
			xmlhttp = new XMLHttpRequest();
		} else {// code for IE6, IE5
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.responseText == '') {
				return;
			}
			var response = eval('(' + xmlhttp.responseText + ')');
			if (xmlhttp.readyState == 4 && xmlhttp.status==200 && !response.errors) {
				managementForm = response.partner_contact_formset_managementForm;
				forms = response.partner_contact_formset_forms
				toInsert = '<div id = "hidden-contact-edit-management">' +  managementForm + '</div>';
				toInsert = toInsert + response.partner_contact_formset_forms;
				$('#hidden-contact-edit form').html(toInsert);
				// assign classes for formset elements
				assignFormsetClasses();
			    $('#hidden-contact-edit table').formset({
			        prefix: response.partner_contact_formset_prefix
			    });
			    // Bring back data to front
			    contactsBackToList(partnerId > 0);
			} else if (response.errors) {
			}
		}
		// Use GET to get the responde data
		if (partnerId > 0) {
			xmlhttp.open("GET", "partners/" + partnerId + "/edit", true);
		} else {
			xmlhttp.open("GET", "partners/create", true);
		}
		xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xmlhttp.setRequestHeader("X_REQUESTED_WITH", "XMLHttpRequest");
		xmlhttp.send('');
	}

	/**
	 * Assign classes for the hidden formset fields. This makes it easy for other
	 * part of this page to get the needed contact information.
	 *
	 * @author caoweiquan322
	 * @version 1.0
	 */
	var assignFormsetClasses = function() {
		$('#hidden-contact-edit input').each(function() {
			var inputName = $(this).attr('name');
			if (inputName.indexOf('full_name') >= 0) {
				$(this).addClass('js-partnercontact-full_name');
			} else if (inputName.indexOf('job_title') >= 0) {
				$(this).addClass('js-partnercontact-job_title');
			} else if (inputName.indexOf('company_name') >= 0) {
				$(this).addClass('js-partnercontact-company_name');
			} else if (inputName.indexOf('office_address') >= 0) {
				$(this).addClass('js-partnercontact-office_address');
			} else if (inputName.indexOf('email') >= 0) {
				$(this).addClass('js-partnercontact-email');
			} else if (inputName.indexOf('phone') >= 0) {
				$(this).addClass('js-partnercontact-phone');
			} else if (inputName.indexOf('DELETE') >= 0) {
				$(this).addClass('js-partnercontact-will-delete');
			}
			return true;
		});

		$('#hidden-contact-edit textarea').each(function() {
			var inputName = $(this).attr('name');
			if (inputName.indexOf('notes') >= 0) {
				$(this).addClass('js-partnercontact-notes');
			}
			return true;
		});
	}

	/**
	 * Send AJAX request to finally update the partner information.
	 *
	 * @author caoweiquan322
	 * @version 1.0
	 */
	var onEditSavePartner = function() {
		var xmlhttp;
		if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
			xmlhttp = new XMLHttpRequest();
		} else {// code for IE6, IE5
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.responseText == '') {
				return;
			}
			var response = eval('(' + xmlhttp.responseText + ')');
			if (xmlhttp.readyState==4 && xmlhttp.status==200 && !response.errors) {
				// Disable error message.
				$('#partner-edit-box').removeClass('error');
				$('.js-partner-edit-error-infor').hide();
				// redirect url
				var currLocation = '' + window.location;
	        	var newLocation = currLocation.replace(/active\=\w+&?/, '');
	        	var partnerId = $("#partner-edit-partner-id").val();
	        	var idx = newLocation.indexOf('?');
	        	if (idx < 0) {
	        		newLocation = newLocation + '?active=' + partnerId;
				} else if (idx < newLocation.length - 1 && newLocation.charAt(newLocation.length - 1) != '&') {
					newLocation = newLocation + '&active=' + partnerId;
				} else {
					newLocation = newLocation + 'active=' + partnerId;
				}
	        	window.location.href = newLocation;//"/partners?page=last";
				// Hide this content and show detail of new added partner
	            $("#tab-partners .partner-main-content").hide();
	            $("#partner-detail-box").show();
			} else if (response.errors) {
				// Show error message.
				$('#partner-edit-box').addClass('error');
				$('.js-partner-edit-error-infor').hide();
				$('.js-partner-edit-error-infor').html('');
				if (response.errors.hfpp_network_id) {
					$('#partner-edit-error-infor-hfpp_network_id').html('hfpp_network_id: ' + response.errors.hfpp_network_id);
					$('#partner-edit-error-infor-hfpp_network_id').show();
				}
				if (response.errors.company_name) {
					$('#partner-edit-error-infor-company_name').html('company_name: ' + response.errors.company_name);
					$('#partner-edit-error-infor-company_name').show();
				}
				if (response.errors.city) {
					$('#partner-edit-error-infor-city').html('city: ' + response.errors.city);
					$('#partner-edit-error-infor-city').show();
				}
				if (response.errors.state) {
					$('#partner-edit-error-infor-state').html('state: ' + response.errors.state);
					$('#partner-edit-error-infor-state').show();
				}
				if (response.errors.division) {
					$('#partner-edit-error-infor-division').html('division: ' + response.errors.division);
					$('#partner-edit-error-infor-division').show();
				}
				if (response.errors.region) {
					$('#partner-edit-error-infor-region').html('region: ' + response.errors.region);
					$('#partner-edit-error-infor-region').show();
				}
				if (response.errors.number_of_insured) {
					$('#partner-edit-error-infor-noi').html('number_of_insured: ' + response.errors.number_of_insured);
					$('#partner-edit-error-infor-noi').show();
				}
                if (response.errors.network_username) {
                    $('#partner-edit-error-infor-username').html('username: ' + response.errors.network_username);
                    $('#partner-edit-error-infor-username').show();
                }
                if (response.errors.network_password) {
                    $('#partner-edit-error-infor-password').html('password: ' + response.errors.network_password);
                    $('#partner-edit-error-infor-password').show();
                }
                if (response.errors.network_organization_name) {
                    $('#partner-edit-error-infor-orgname').html('organization name: ' + response.errors.network_organization_name);
                    $('#partner-edit-error-infor-orgname').show();
                }
                if (response.errors.network_role) {
                    $('#partner-edit-error-infor-role').html('role: ' + response.errors.network_role);
                    $('#partner-edit-error-infor-role').show();
                }
                if (response.errors.network_auto_retrieve_cached_data) {
                    $('#partner-edit-error-infor-cached').html('network auto retrieve cached data: ' + response.errors.network_auto_retrieve_cached_data);
                    $('#partner-edit-error-infor-cached').show();
                }
				if (response.errors.tags) {
					$('#partner-edit-error-infor-tags').html('tags: ' + response.errors.tags);
					$('#partner-edit-error-infor-tags').show();
				}z
			}
		}
		xmlhttp.open("POST", "partners/" + $("#partner-edit-partner-id").val() + "/edit", true);
		xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xmlhttp.setRequestHeader("X_REQUESTED_WITH", "XMLHttpRequest");
		// Send serialized form to server
		var requestText = $("#partner-edit-form").serialize();

		// Add tags
		var tagsText = '';
		var selectedTags = $('#partner-edit-form .tags-lists .tagId');
		selectedTags.each(function(i, val) {
			tagsText = tagsText + '&tags=' + val.textContent;
		});
		if (tagsText.length > 1) {
			requestText = requestText + tagsText;
		}

		// Add contacts
		var contactsText = $('#hidden-contact-edit form').serialize();
		if (contactsText.length > 1) {
			requestText = requestText + '&' + contactsText;
		}

		xmlhttp.send(requestText);
	}

	/**
	 * Assign delete to 'on' for the given contact, So that this item won't be sent to server.
	 *
	 * @author caoweiquan322
	 * @version 1.0
	 */
	var deletBackContact = function(full_name_id) {
		$("#hidden-contact-edit .dynamic-form").each(function(){
        	if ($(this).find('.js-partnercontact-full_name').attr('id') == full_name_id) {
        		if ($(this).find('.js-partnercontact-will-delete').size() > 0) {
            		// Set this delete value to 'on'
    				$(this).find('.js-partnercontact-will-delete').val('on');
				} else {
					// or just remove this row
					$(this).find('.delete-row').trigger('click');
				}
        		// Terminate the each loop
        		return false;
			} else {
				// Go on looking for matched contact
				return true;
			}
		});
	}

	/**
	 * This function triggers remove button of the django dynamic formsets. Except the first one
	 * recording structure of formset.
	 *
	 * @author caoweiquan322
	 * @version 1.0
	 */
	var removeAllBackContacts = function() {
		$("#hidden-contact-edit .dynamic-form").each(function(){
        	if ($(this).attr('style') == 'display: none;') {
        		return true;
        	}

        	// trigger delete-row button
        	$(this).find('.delete-row').trigger('click');
        	// Go on removing
        	return true;
		});
	}

	/**
	 * Copy the value of django dynamic form-set to the popup edit dialog.
	 *
	 * @author caoweiquan322
	 * @version 1.0
	 */
	var contactsBackToPopup = function(full_name_id) {
        $("#hidden-contact-edit .dynamic-form").each(function(){
        	if ($(this).find('.js-partnercontact-full_name').attr('id') == full_name_id) {
        		$('#contact-edit-full_name_id').html(full_name_id);
        		$('#contact-edit-full-name').val($(this).find('.js-partnercontact-full_name').val());
        		$('#contact-edit-job-title').val($(this).find('.js-partnercontact-job_title').val());
        		$('#contact-edit-company-name').val($(this).find('.js-partnercontact-company_name').val());
        		$('#contact-edit-office-address').val($(this).find('.js-partnercontact-office_address').val());
        		$('#contact-edit-email').val($(this).find('.js-partnercontact-email').val());
        		$('#contact-edit-phone').val($(this).find('.js-partnercontact-phone').val());
        		$('#contact-edit-notes').val($(this).find('.js-partnercontact-notes').val());
				return false;
			} else {
				// Go on looking for matched contact
				return true;
			}
        });
	}

	/**
	 * Copy the value of django dynamic form-set to the partner detail page.
	 *
	 * @author caoweiquan322
	 * @version 1.0
	 */
	var contactsBackToDetail = function(full_name_id) {
        $("#hidden-contact-edit .dynamic-form").each(function(){
        	if ($(this).find('.js-partnercontact-full_name').attr('id') == full_name_id) {
        		$('#contact-detail-full-name').html($(this).find('.js-partnercontact-full_name').val());
        		$('#contact-detail-job-title').html($(this).find('.js-partnercontact-job_title').val());
        		$('#contact-detail-company-name').html($(this).find('.js-partnercontact-company_name').val());
        		$('#contact-detail-office-address').html($(this).find('.js-partnercontact-office_address').val());
        		$('#contact-detail-email').html($(this).find('.js-partnercontact-email').val());
        		$('#contact-detail-phone').html($(this).find('.js-partnercontact-phone').val());
        		$('#contact-detail-notes').html($(this).find('.js-partnercontact-notes').val());
				return false;
			} else {
				// Go on looking for matched contact
				return true;
			}
        });
	}

	/**
	 * Copy the value of django dynamic form-set to the partner edit/create page.
	 *
	 * @author caoweiquan322
	 * @version 1.0
	 */
	var contactsBackToList = function(showEditDeleteBt) {
        var contactList = $(".partner-main-content:visible").find(".form-contact-list");
        // Determines if the front part is a detail box
        var isDetailBox = $(".partner-main-content:visible").attr("id") == "partner-detail-box";
        contactList.find("table tbody").html('');
        $("#hidden-contact-edit .dynamic-form").each(function(){
        	if ($(this).attr('style') == 'display: none;'
        			|| ($(this).find('.js-partnercontact-will-delete').size() > 0
        				&& $(this).find('.js-partnercontact-will-delete').val() == 'on')) {
				return true;
			}
        	// Remove those empty extra rows
        	if ($(this).find('.js-partnercontact-full_name').val() == '') {
				return true;
			}

        	// fill in the html
        	var html = new Array();
            html.push('<tr>');
            // record name of the "full_name" field.
            html.push('<td class="hide js-partner-contact-full_name_id">'
            		+ $(this).find('.js-partnercontact-full_name').attr('id') + '</td>');
            html.push('<td class="cell-contact-name">');
            html.push('<a href="javascript:;" class="color-orange js-action-contact">'
            		+ $(this).find('.js-partnercontact-full_name').val() + '</a>');
            html.push('<p>' + $(this).find('.js-partnercontact-job_title').val() + '</p>');
            html.push('</td>');
            html.push('<td class="cell-contact-infor">');
            html.push('<p>' + $(this).find('.js-partnercontact-email').val() + '</p>');
            html.push('<strong>' + $(this).find('.js-partnercontact-phone').val() + '</strong>');
            html.push('</td>');
            if (!isDetailBox && showEditDeleteBt) {
                html.push('<td class="cell-contact-action">');
                html.push('<div class="button btn-blue-dark btn-blue-edit js-contact-edit"><span><span><span class="icon">EDIT</span></span></span></div>');
                html.push('<div class="button btn-blue-dark btn-blue-delete js-contact-delete"><span><span><span class="icon">DELETE</span></span></span></div>');
                html.push('</td>');
			}
            html.push('</tr>');

        	// append the html
            contactList.find("table tbody").append(html.join(""));
        });

        // refresh the contact list
        contactList.removeClass("hide");
        contactList.find("table tbody .empty-row").remove();
        contactList.find('table tbody tr').removeClass('odd even');
        contactList.find('table tbody tr:odd').addClass('odd');
        contactList.find('table tbody tr:even').addClass('even');
	}

	/**
	 * Send AJAX request to finally save the created partner tag.
	 *
	 * @author caoweiquan322
	 * @version 1.0
	 */
	var onCreateSaveTag = function() {
		var xmlhttp;
		if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
			xmlhttp = new XMLHttpRequest();
		} else {// code for IE6, IE5
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.responseText == '') {
				return;
			}
			var response = eval('(' + xmlhttp.responseText + ')');
			if (xmlhttp.readyState==4 && xmlhttp.status==200 && !response.errors) {
				// Hide this content and show detail of new added partner
	            closeModal();
	            $('.js-tag-add-error-infor').hide();
				// redirect url
				window.location.href = "/partner_tags";
			} else if (response.errors) {
				// Show error message.
				//$('#partner-add-box').addClass('error');
				$('.js-tag-add-error-infor').hide();
				$('.js-tag-add-error-infor').html('');
				if (response.errors.name) {
					$('#tag-add-error-infor-name').html('Tag Name: ' + response.errors.name);
					$('#tag-add-error-infor-name').show();
				}
				if (response.errors.description) {
					$('#tag-add-error-infor-description').html('Tag Description: ' + response.errors.description);
					$('#tag-add-error-infor-description').show();
				}
			}
		}
		xmlhttp.open("POST", "partner_tags/create", true);
		xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xmlhttp.setRequestHeader("X_REQUESTED_WITH", "XMLHttpRequest");
		// Send serialized form to server
		xmlhttp.send($("#tag-add-form").serialize());
	}

	/**
	 * Send AJAX request to finally update the partner tag.
	 *
	 * @author caoweiquan322
	 * @version 1.0
	 */
	var onEditSaveTag = function() {
		var xmlhttp;
		if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
			xmlhttp = new XMLHttpRequest();
		} else {// code for IE6, IE5
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.responseText == '') {
				return;
			}
			var response = eval('(' + xmlhttp.responseText + ')');
			if (xmlhttp.readyState==4 && xmlhttp.status==200 && !response.errors) {
				// Hide this content and show detail of new added partner
	            closeModal();
				// redirect url
				window.location.href = "/partner_tags";
			} else {
				if (response && response.errors) {
					//alert(response.errors);
				}
			}
		}
		xmlhttp.open("POST", "partner_tags/" + $("#modal-tag-edit input[name='id']").val() + "/edit", true);
		xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xmlhttp.setRequestHeader("X_REQUESTED_WITH", "XMLHttpRequest");
		// Send serialized form to server
		xmlhttp.send($("#tag-edit-form").serialize());
    }
})();
var placeholder = function(selector, text) {
    $(selector).on('focus', function() {
        $(this).on('blur', function() {
            $(this).unbind('blur', arguments.callee);
            if ($(this).val() == '') {
                $(this).val(text).addClass("is-placeholder-hint");
            }
        });
        if ($(this).val() == text) {
            $(this).val('').removeClass("is-placeholder-hint");
        }
    });
};
/* end of partner management functions */

/* query build functions */
(function(){
     $(function(){
        // init query builder
        if($("#beneficiary_queryConstructor").length>0 && typeof(qb) != "undefined"){ qb.init(); }

        if ($('#edit-study-query-id').length) {
            parseQueryAndRebuild($('#beneficiary_builtQuery').text());
            $('#beneficiary_builtQuery').addClass('js-rebuild');
        }
     });

    // IMMJ ====> Add different expression types:
    var numerical_attributes = new Array(
                               "End stage renal disease Indicator",
                               "Date of Birth",
                               "Date of Death",
                               "Sex",
                               "Beneficiary Race Code",
                               "Total number of months of part A coverage for the beneficiary",
                               "Total number of months of part B coverage for the beneficiary",
                               "Total number of months of HMO coverage for the beneficiary",
                               "Total number of months of part D plan coverage for the beneficiary",
                               "Inpatient annual Medicare reimbursement amount",
                               "Inpatient annual beneficiary responsibility amount",
                               "Inpatient annual primary payer reimbursement amount",
                               "Outpatient annual Medicare reimbursement amount",
                               "Outpatient annual beneficiary responsibility amount",
                               "Outpatient annual primary payer reimbursement amount",
                               "Carrier annual Medicare reimbursement amount",
                               "Carrier annual beneficiary responsibility amount",
                               "Carrier annual primary payer reimbursement amount",
                               "Claim start date",
                               "Claim end date",
                               "Line NCH Payment Amount 1",
                               "Line NCH Payment Amount 2",
                               "Line NCH Payment Amount 3",
                               "Line NCH Payment Amount 4",
                               "Line NCH Payment Amount 5",
                               "Line NCH Payment Amount 6",
                               "Line NCH Payment Amount 7",
                               "Line NCH Payment Amount 8",
                               "Line NCH Payment Amount 9",
                               "Line NCH Payment Amount 10",
                               "Line NCH Payment Amount 11",
                               "Line NCH Payment Amount 12",
                               "Line NCH Payment Amount 13",
                               "Line Beneficiary Part B Deductible Amount 1",
                               "Line Beneficiary Part B Deductible Amount 2",
                               "Line Beneficiary Part B Deductible Amount 3",
                               "Line Beneficiary Part B Deductible Amount 4",
                               "Line Beneficiary Part B Deductible Amount 5",
                               "Line Beneficiary Part B Deductible Amount 6",
                               "Line Beneficiary Part B Deductible Amount 7",
                               "Line Beneficiary Part B Deductible Amount 8",
                               "Line Beneficiary Part B Deductible Amount 9",
                               "Line Beneficiary Part B Deductible Amount 10",
                               "Line Beneficiary Part B Deductible Amount 11",
                               "Line Beneficiary Part B Deductible Amount 12",
                               "Line Beneficiary Part B Deductible Amount 13",
                               "Line Beneficiary Primary Payer Paid Amount 1",
                               "Line Beneficiary Primary Payer Paid Amount 2",
                               "Line Beneficiary Primary Payer Paid Amount 3",
                               "Line Beneficiary Primary Payer Paid Amount 4",
                               "Line Beneficiary Primary Payer Paid Amount 5",
                               "Line Beneficiary Primary Payer Paid Amount 6",
                               "Line Beneficiary Primary Payer Paid Amount 7",
                               "Line Beneficiary Primary Payer Paid Amount 8",
                               "Line Beneficiary Primary Payer Paid Amount 9",
                               "Line Beneficiary Primary Payer Paid Amount 10",
                               "Line Beneficiary Primary Payer Paid Amount 11",
                               "Line Beneficiary Primary Payer Paid Amount 12",
                               "Line Beneficiary Primary Payer Paid Amount 13",
                               "Line Coinsurance Amount 1",
                               "Line Coinsurance Amount 2",
                               "Line Coinsurance Amount 3",
                               "Line Coinsurance Amount 4",
                               "Line Coinsurance Amount 5",
                               "Line Coinsurance Amount 6",
                               "Line Coinsurance Amount 7",
                               "Line Coinsurance Amount 8",
                               "Line Coinsurance Amount 9",
                               "Line Coinsurance Amount 10",
                               "Line Coinsurance Amount 11",
                               "Line Coinsurance Amount 12",
                               "Line Coinsurance Amount 13",
                               "Line Allowed Charge Amount 1",
                               "Line Allowed Charge Amount 2",
                               "Line Allowed Charge Amount 3",
                               "Line Allowed Charge Amount 4",
                               "Line Allowed Charge Amount 5",
                               "Line Allowed Charge Amount 6",
                               "Line Allowed Charge Amount 7",
                               "Line Allowed Charge Amount 8",
                               "Line Allowed Charge Amount 9",
                               "Line Allowed Charge Amount 10",
                               "Line Allowed Charge Amount 11",
                               "Line Allowed Charge Amount 12",
                               "Line Allowed Charge Amount 13",
                               "Claim Line Segment",
                               "Claims start date",
                               "Claims end date",
                               "Claim Payment Amount",
                               "NCH Primary Payer Claim Paid Amount",
                               "Inpatient admission date",
                               "Claim Pass Thru Per Diem Amount",
                               "NCH Beneficiary Inpatient Deductible Amount",
                               "NCH Beneficiary Part A Coinsurance Liability Amount",
                               "NCH Beneficiary Blood Deductible Liability Amount",
                               "Claim Utilization Day Count",
                               "Inpatient discharged date",
                               "Claim Line Segment",
                               "Claims start date",
                               "Claims end date",
                               "Claim Payment Amount",
                               "NCH Primary Payer Claim Paid Amount",
                               "NCH Beneficiary Blood Deductible Liability Amount",
                               "NCH Beneficiary Part B Deductible Amount",
                               "NCH Beneficiary Part B Coinsurance Amount",
                               "RX Service Date",
                               "Quantity Dispensed",
                               "Days Supply",
                               "Patient Pay Amount",
                               "Gross Drug Cost");

    var logical_attributes = new Array(
                               "Chronic Condition: Alzheimer or related disorders or senile",
                               "Chronic Condition: Heart Failure",
                               "Chronic Condition: Chronic Kidney Disease",
                               "Chronic Condition: Cancer",
                               "Chronic Condition: Chronic Obstructive Pulmonary Disease",
                               "Chronic Condition: Depression",
                               "Chronic Condition: Diabetes",
                               "Chronic Condition: Ischemic Heart Disease",
                               "Chronic Condition: Osteoporosis",
                               "Chronic Condition: Rheumatoid Arthritis or Osteoarthritis (RA/OA)",
                               "Chronic Condition: Stroke/transient Ischemic Attack");

    var textual_attributes = new Array(
                               "Beneficiary Code",
                               "State Code",
                               "County Code",
                               "Beneficiary Code",
                               "Claim ID",
                               "Claim Diagnosis Code 1",
                               "Claim Diagnosis Code 2",
                               "Claim Diagnosis Code 3",
                               "Claim Diagnosis Code 4",
                               "Claim Diagnosis Code 5",
                               "Claim Diagnosis Code 6",
                               "Claim Diagnosis Code 7",
                               "Claim Diagnosis Code 8",
                               "Provider Physician - National Provider Identifier Number 1",
                               "Provider Physician - National Provider Identifier Number 2",
                               "Provider Physician - National Provider Identifier Number 3",
                               "Provider Physician - National Provider Identifier Number 4",
                               "Provider Physician - National Provider Identifier Number 5",
                               "Provider Physician - National Provider Identifier Number 6",
                               "Provider Physician - National Provider Identifier Number 7",
                               "Provider Physician - National Provider Identifier Number 8",
                               "Provider Physician - National Provider Identifier Number 9",
                               "Provider Physician - National Provider Identifier Number 10",
                               "Provider Physician - National Provider Identifier Number 11",
                               "Provider Physician - National Provider Identifier Number 12",
                               "Provider Physician - National Provider Identifier Number 13",
                               "Provider Institution Tax Number 1",
                               "Provider Institution Tax Number 2",
                               "Provider Institution Tax Number 3",
                               "Provider Institution Tax Number 4",
                               "Provider Institution Tax Number 5",
                               "Provider Institution Tax Number 6",
                               "Provider Institution Tax Number 7",
                               "Provider Institution Tax Number 8",
                               "Provider Institution Tax Number 9",
                               "Provider Institution Tax Number 10",
                               "Provider Institution Tax Number 11",
                               "Provider Institution Tax Number 12",
                               "Provider Institution Tax Number 13",
                               "Line HCFA Common Procedure Coding System 1",
                               "Line HCFA Common Procedure Coding System 2",
                               "Line HCFA Common Procedure Coding System 3",
                               "Line HCFA Common Procedure Coding System 4",
                               "Line HCFA Common Procedure Coding System 5",
                               "Line HCFA Common Procedure Coding System 6",
                               "Line HCFA Common Procedure Coding System 7",
                               "Line HCFA Common Procedure Coding System 8",
                               "Line HCFA Common Procedure Coding System 9",
                               "Line HCFA Common Procedure Coding System 10",
                               "Line HCFA Common Procedure Coding System 11",
                               "Line HCFA Common Procedure Coding System 12",
                               "Line HCFA Common Procedure Coding System 13",
                               "Line Processing Indicator Code 1",
                               "Line Processing Indicator Code 2",
                               "Line Processing Indicator Code 3",
                               "Line Processing Indicator Code 4",
                               "Line Processing Indicator Code 5",
                               "Line Processing Indicator Code 6",
                               "Line Processing Indicator Code 7",
                               "Line Processing Indicator Code 8",
                               "Line Processing Indicator Code 9",
                               "Line Processing Indicator Code 10",
                               "Line Processing Indicator Code 11",
                               "Line Processing Indicator Code 12",
                               "Line Processing Indicator Code 13",
                               "Line Diagnosis Code 1",
                               "Line Diagnosis Code 2",
                               "Line Diagnosis Code 3",
                               "Line Diagnosis Code 4",
                               "Line Diagnosis Code 5",
                               "Line Diagnosis Code 6",
                               "Line Diagnosis Code 7",
                               "Line Diagnosis Code 8",
                               "Line Diagnosis Code 9",
                               "Line Diagnosis Code 10",
                               "Line Diagnosis Code 11",
                               "Line Diagnosis Code 12",
                               "Line Diagnosis Code 13",
                               "Beneficiary Code",
                               "Claim ID",
                               "Provider institution",
                               "Attending Physician - National Provider Identifier Number",
                               "Operating Physician - National Provider Identifier Number",
                               "Other Physician - National Provider Identifier Number",
                               "Claim Admitting Diagnosis Code",
                               "Claim Diagnosis Related Group Code",
                               "Claim Diagnosis Code 1",
                               "Claim Diagnosis Code 2",
                               "Claim Diagnosis Code 3",
                               "Claim Diagnosis Code 4",
                               "Claim Diagnosis Code 5",
                               "Claim Diagnosis Code 6",
                               "Claim Diagnosis Code 7",
                               "Claim Diagnosis Code 8",
                               "Claim Diagnosis Code 9",
                               "Claim Diagnosis Code 10",
                               "Claim Procedure Code 1",
                               "Claim Procedure Code 2",
                               "Claim Procedure Code 3",
                               "Claim Procedure Code 4",
                               "Claim Procedure Code 5",
                               "Claim Procedure Code 6",
                               "Revenue Center HCFA Common Procedure Coding System 1",
                               "Revenue Center HCFA Common Procedure Coding System 2",
                               "Revenue Center HCFA Common Procedure Coding System 3",
                               "Revenue Center HCFA Common Procedure Coding System 4",
                               "Revenue Center HCFA Common Procedure Coding System 5",
                               "Revenue Center HCFA Common Procedure Coding System 6",
                               "Revenue Center HCFA Common Procedure Coding System 7",
                               "Revenue Center HCFA Common Procedure Coding System 8",
                               "Revenue Center HCFA Common Procedure Coding System 9",
                               "Revenue Center HCFA Common Procedure Coding System 10",
                               "Revenue Center HCFA Common Procedure Coding System 11",
                               "Revenue Center HCFA Common Procedure Coding System 12",
                               "Revenue Center HCFA Common Procedure Coding System 13",
                               "Revenue Center HCFA Common Procedure Coding System 14",
                               "Revenue Center HCFA Common Procedure Coding System 15",
                               "Revenue Center HCFA Common Procedure Coding System 16",
                               "Revenue Center HCFA Common Procedure Coding System 17",
                               "Revenue Center HCFA Common Procedure Coding System 18",
                               "Revenue Center HCFA Common Procedure Coding System 19",
                               "Revenue Center HCFA Common Procedure Coding System 20",
                               "Revenue Center HCFA Common Procedure Coding System 21",
                               "Revenue Center HCFA Common Procedure Coding System 22",
                               "Revenue Center HCFA Common Procedure Coding System 23",
                               "Revenue Center HCFA Common Procedure Coding System 24",
                               "Revenue Center HCFA Common Procedure Coding System 25",
                               "Revenue Center HCFA Common Procedure Coding System 26",
                               "Revenue Center HCFA Common Procedure Coding System 27",
                               "Revenue Center HCFA Common Procedure Coding System 28",
                               "Revenue Center HCFA Common Procedure Coding System 29",
                               "Revenue Center HCFA Common Procedure Coding System 30",
                               "Revenue Center HCFA Common Procedure Coding System 31",
                               "Revenue Center HCFA Common Procedure Coding System 32",
                               "Revenue Center HCFA Common Procedure Coding System 33",
                               "Revenue Center HCFA Common Procedure Coding System 34",
                               "Revenue Center HCFA Common Procedure Coding System 35",
                               "Revenue Center HCFA Common Procedure Coding System 36",
                               "Revenue Center HCFA Common Procedure Coding System 37",
                               "Revenue Center HCFA Common Procedure Coding System 38",
                               "Revenue Center HCFA Common Procedure Coding System 39",
                               "Revenue Center HCFA Common Procedure Coding System 40",
                               "Revenue Center HCFA Common Procedure Coding System 41",
                               "Revenue Center HCFA Common Procedure Coding System 42",
                               "Revenue Center HCFA Common Procedure Coding System 43",
                               "Revenue Center HCFA Common Procedure Coding System 44",
                               "Revenue Center HCFA Common Procedure Coding System 45",
                               "Beneficiary Code",
                               "Claim ID",
                               "Provider institution",
                               "Attending Physician - National Provider Identifier Number",
                               "Operating Physician - National Provider Identifier Number",
                               "Other Physician - National Provider Identifier Number",
                               "Claim Diagnosis Code 1",
                               "Claim Diagnosis Code 2",
                               "Claim Diagnosis Code 3",
                               "Claim Diagnosis Code 4",
                               "Claim Diagnosis Code 5",
                               "Claim Diagnosis Code 6",
                               "Claim Diagnosis Code 7",
                               "Claim Diagnosis Code 8",
                               "Claim Diagnosis Code 9",
                               "Claim Diagnosis Code 10",
                               "Claim Procedure Code 1",
                               "Claim Procedure Code 2",
                               "Claim Procedure Code 3",
                               "Claim Procedure Code 4",
                               "Claim Procedure Code 5",
                               "Claim Procedure Code 6",
                               "Claim Admitting Diagnosis Code",
                               "Revenue Center HCFA Common Procedure Coding System 1",
                               "Revenue Center HCFA Common Procedure Coding System 2",
                               "Revenue Center HCFA Common Procedure Coding System 3",
                               "Revenue Center HCFA Common Procedure Coding System 4",
                               "Revenue Center HCFA Common Procedure Coding System 5",
                               "Revenue Center HCFA Common Procedure Coding System 6",
                               "Revenue Center HCFA Common Procedure Coding System 7",
                               "Revenue Center HCFA Common Procedure Coding System 8",
                               "Revenue Center HCFA Common Procedure Coding System 9",
                               "Revenue Center HCFA Common Procedure Coding System 10",
                               "Revenue Center HCFA Common Procedure Coding System 11",
                               "Revenue Center HCFA Common Procedure Coding System 12",
                               "Revenue Center HCFA Common Procedure Coding System 13",
                               "Revenue Center HCFA Common Procedure Coding System 14",
                               "Revenue Center HCFA Common Procedure Coding System 15",
                               "Revenue Center HCFA Common Procedure Coding System 16",
                               "Revenue Center HCFA Common Procedure Coding System 17",
                               "Revenue Center HCFA Common Procedure Coding System 18",
                               "Revenue Center HCFA Common Procedure Coding System 19",
                               "Revenue Center HCFA Common Procedure Coding System 20",
                               "Revenue Center HCFA Common Procedure Coding System 21",
                               "Revenue Center HCFA Common Procedure Coding System 22",
                               "Revenue Center HCFA Common Procedure Coding System 23",
                               "Revenue Center HCFA Common Procedure Coding System 24",
                               "Revenue Center HCFA Common Procedure Coding System 25",
                               "Revenue Center HCFA Common Procedure Coding System 26",
                               "Revenue Center HCFA Common Procedure Coding System 27",
                               "Revenue Center HCFA Common Procedure Coding System 28",
                               "Revenue Center HCFA Common Procedure Coding System 29",
                               "Revenue Center HCFA Common Procedure Coding System 30",
                               "Revenue Center HCFA Common Procedure Coding System 31",
                               "Revenue Center HCFA Common Procedure Coding System 32",
                               "Revenue Center HCFA Common Procedure Coding System 33",
                               "Revenue Center HCFA Common Procedure Coding System 34",
                               "Revenue Center HCFA Common Procedure Coding System 35",
                               "Revenue Center HCFA Common Procedure Coding System 36",
                               "Revenue Center HCFA Common Procedure Coding System 37",
                               "Revenue Center HCFA Common Procedure Coding System 38",
                               "Revenue Center HCFA Common Procedure Coding System 39",
                               "Revenue Center HCFA Common Procedure Coding System 40",
                               "Revenue Center HCFA Common Procedure Coding System 41",
                               "Revenue Center HCFA Common Procedure Coding System 42",
                               "Revenue Center HCFA Common Procedure Coding System 43",
                               "Revenue Center HCFA Common Procedure Coding System 44",
                               "Revenue Center HCFA Common Procedure Coding System 45",
                               "Beneficiary Code",
                               "CCW Part D Event Number",
                               "Product Service ID");

    var listContains = function(lst, ele) {
        for (var i = 0; i < lst.length; i++) {
            if (lst[i] == ele) {
                return true;
            }
        }
        return false;
    }

    var getAttributesType = function(txt) {
        if (listContains(numerical_attributes, txt)) {
            return "numerical";
        } else if (listContains(logical_attributes, txt)) {
            return "logical";
        } else if (listContains(textual_attributes, txt)) {
            return "textual";
        } else {
            return "unknown";
        }
    }

    var getDefaultExtraAttributes = function(attriType) {
        if (attriType == "numerical") {
            return 'less than';
        } else if (attriType == "logical") {
            return 'is';
        } else if (attriType == "textual") {
            return 'is';
        } else {
            return "";
        }
    }

    var getExtraAttributes = function(attriType) {
        if (attriType == "numerical") {
            return '<li><a href="javascript:;">less than</a></li>' +
                   '<li><a href="javascript:;">equal to</a></li>' +
                   '<li><a href="javascript:;">greater than</a></li>' +
                   '<li><a href="javascript:;">less than or equal to</a></li>' +
                   '<li><a href="javascript:;">greater than or equal to</a></li>' +
                   '<li><a href="javascript:;">between</a></li>';
        } else if (attriType == "logical") {
            return '<li><a href="javascript:;">is</a></li>';
        } else if (attriType == "textual") {
            return '<li><a href="javascript:;">is</a></li>' +
                   '<li><a href="javascript:;">matches</a></li>';
        } else {
            return "";
        }
    }


    // template stores the blueprint of row, operators... that are added dynamically.
    var beneficiary_template = {
        prntGroup : "",
        group : "",
        operator : "",
        lvl1Is : ""
    }
    var carrier_template = {
        prntGroup : "",
        group : "",
        operator : "",
        lvl1Is : ""
    }
    var inpatient_template = {
        prntGroup : "",
        group : "",
        operator : "",
        lvl1Is : ""
    }
    var outpatient_template = {
        prntGroup : "",
        group : "",
        operator : "",
        lvl1Is : ""
    }
    var prescription_template = {
        prntGroup : "",
        group : "",
        operator : "",
        lvl1Is : ""
    }

    var qb = {
        init : function() {
            //
            beneficiary_template.prntGroup = $('#beneficiary_queryConstructor .template .tempQbGrParent').clone().removeClass('tempQbGrParent');
            beneficiary_template.group = $('#beneficiary_queryConstructor .template .templvl1qbGr').clone().removeClass('templvl1qbGr');
            beneficiary_template.operator = $('#beneficiary_queryConstructor .template .tempLvl1qbOp').clone().removeClass('tempLvl1qbOp');
            beneficiary_template.lvl1Is = $('#beneficiary_queryConstructor .template .tempLvl1is').clone().removeClass('tempLvl1is');
            $('#beneficiary_queryConstructor .template').remove();
            //
            carrier_template.prntGroup = $('#carrier_queryConstructor .template .tempQbGrParent').clone().removeClass('tempQbGrParent');
            carrier_template.group = $('#carrier_queryConstructor .template .templvl1qbGr').clone().removeClass('templvl1qbGr');
            carrier_template.operator = $('#carrier_queryConstructor .template .tempLvl1qbOp').clone().removeClass('tempLvl1qbOp');
            carrier_template.lvl1Is = $('#carrier_queryConstructor .template .tempLvl1is').clone().removeClass('tempLvl1is');
            $('#carrier_queryConstructor .template').remove();
            //
            inpatient_template.prntGroup = $('#inpatient_queryConstructor .template .tempQbGrParent').clone().removeClass('tempQbGrParent');
            inpatient_template.group = $('#inpatient_queryConstructor .template .templvl1qbGr').clone().removeClass('templvl1qbGr');
            inpatient_template.operator = $('#inpatient_queryConstructor .template .tempLvl1qbOp').clone().removeClass('tempLvl1qbOp');
            inpatient_template.lvl1Is = $('#inpatient_queryConstructor .template .tempLvl1is').clone().removeClass('tempLvl1is');
            $('#inpatient_queryConstructor .template').remove();
            //
            outpatient_template.prntGroup = $('#outpatient_queryConstructor .template .tempQbGrParent').clone().removeClass('tempQbGrParent');
            outpatient_template.group = $('#outpatient_queryConstructor .template .templvl1qbGr').clone().removeClass('templvl1qbGr');
            outpatient_template.operator = $('#outpatient_queryConstructor .template .tempLvl1qbOp').clone().removeClass('tempLvl1qbOp');
            outpatient_template.lvl1Is = $('#outpatient_queryConstructor .template .tempLvl1is').clone().removeClass('tempLvl1is');
            $('#outpatient_queryConstructor .template').remove();
            //
            prescription_template.prntGroup = $('#prescription_queryConstructor .template .tempQbGrParent').clone().removeClass('tempQbGrParent');
            prescription_template.group = $('#prescription_queryConstructor .template .templvl1qbGr').clone().removeClass('templvl1qbGr');
            prescription_template.operator = $('#prescription_queryConstructor .template .tempLvl1qbOp').clone().removeClass('tempLvl1qbOp');
            prescription_template.lvl1Is = $('#prescription_queryConstructor .template .tempLvl1is').clone().removeClass('tempLvl1is');
            $('#prescription_queryConstructor .template').remove();
            // ie Fallback
            if (window.PIE) {
                $('.ieFB,  .btn').each(function() {
                    PIE.attach(this);
                });
            }

            // text box change function
            $('body').delegate('.qbGr .text ', 'change', qb.updateQuery);
            // select list show
            $('body').delegate('.selectWrap .select', 'click', function(e) {
                $('.selExpanded').removeClass('selExpanded');
                var _this = $(e.currentTarget).parents('.selectWrap:first');
                var list = $('.selListWrap', _this);
                if (list.is(':visible')) {
                    list.fadeOut('fast');
                } else {
                    list.fadeIn(100);
                    $(this).parents('.qbGr:first').addClass('selExpanded');
                }

            });
            $('body').delegate('.selectWrap', 'mouseleave', function(e) {
                var _this = $(e.currentTarget);
                var list = $('.selListWrap', _this);
                list.hide();
            });
            $('body').delegate('.selListWrap', 'mouseenter', function(e) {
                var _this = $(e.currentTarget);
                _this.clearQueue().stop().show();
            })

            // custom select
            $('body').delegate('.selectWrap .selList a', 'click', function(e) {
                // e.stopPropagation();
                var _this = $(e.currentTarget);

                var prnt = $(this).parents('.selectWrap');
                var txt = _this.text();
                $('.select .val', prnt).html(txt);
                $('.selList', prnt).addClass('hiding').hide();

                if (_this.hasClass('js-partner')) {
                    $('#search-parter-tags-id').data('value', _this.data('value'));
                    window.setTimeout(function() {
                        $('.hiding').removeAttr('style').removeClass('hiding');
                    }, 100);
                    return;
                }

                if (txt === "between") {
                    $('.priceGrMin', prnt.parents('.qbGr:first')).show();
                } else {
                    $('.priceGrMin', prnt.parents('.qbGr:first')).hide();
                }
				//https://apps.topcoder.com/bugs/browse/BUGR-10292
	            $(this).parents('.selectWrap').siblings('.extendedAttr').find('input[type=text]').val('');
                // IMMJ====> Check expression type:
                var attriType = getAttributesType(txt);

                if($($(this).parents('.selectWrap').find('span')[0]).text().indexOf("Date")!=-1 ||
                   $($(this).parents('.selectWrap').find('span')[0]).text().indexOf("date")!=-1){
                    $(this).parents('.selectWrap').siblings('.extendedAttr').find('input[type=text]').addClass('date-picker');

                    $('.date-picker').datepicker({
                        showOn: "both",
                        buttonImage: "/i/calendar.png",
                        buttonImageOnly: true,
                        constrainInput: false
                    }).attr('readonly','readonly');

                }
                else{
                    var datepickers=$(this).parents('.selectWrap').siblings('.extendedAttr').find('.date-picker');
                    if(datepickers.length>0){
                        datepickers.datepicker('destroy');
                    datepickers.removeClass('date-picker');
					datepickers.removeAttr('readonly');
                    }
                }
                window.setTimeout(function() {
                    $('.hiding').removeAttr('style').removeClass('hiding');
                }, 100);
                if (!prnt.hasClass('selectWrapAlt') && !$('.extendedAttr', prnt).is('visible')) {
                    var newPrnt = prnt.parents('.qbGr:first');
                    $('.extendedAttr', newPrnt).fadeIn();
                    // IMMJ ===>
                    $('.extendedAttr .select span', newPrnt).html(getDefaultExtraAttributes(attriType));
                    $('.extendedAttr .selectWrapAlt .selList ul', newPrnt).html(getExtraAttributes(attriType));
                }
                if (!prnt.hasClass('selectWrapAlt')) {
                    var newPrnt = prnt.parents('.qbGr:first');
                    if (_this.parents('.colAmount').length <= 0) {
                        $('.txtPrice', newPrnt).addClass('txtNormal');
                    } else {
                        $('.txtPrice', newPrnt).removeClass('txtNormal');
                    }
                }
                window.setTimeout(qb.updateQuery, 500);
            })

            $('body').delegate('.queryConstructor .operator', 'click', function(e) {
                var _this = $(e.currentTarget);
                var txt = $.trim($(e.currentTarget).text());
                if (_this.hasClass('typeIsNot')) {
                    if (txt === "IS") {
                        _this.text("NOT");
                    } else {
                        _this.text("IS");
                    }
                } else if (_this.hasClass('typeAndOr')) {
                    if (txt === "AND") {
                        _this.text("OR");
                    } else {
                        _this.text("AND");
                    }

                    // sync all operators in a group
                    var prntGr = $(this).parents('.qbGrAlt:first');
                    if (prntGr.length > 0) {
                        $('.qbOp', prntGr).not('.qbOpAlt').each(function() {
                            $('.operator', $(this)).text(_this.text());
                        })
                    } else {
                        var container = $(this).parents('.queryConstructor:first');
                        container.children('.qbOp').not('.qbOpAlt').each(function() {
                            $('.operator', $(this)).text(_this.text());
                        })
                    }
                }
                qb.updateQuery();
                // e.stopPropagation();
            })

            /* chk group sel. function */
            $('.queryConstructor').delegate('.qbGr .chkGr', 'click', function(e) {
                var _this = $(e.currentTarget);
                if (_this.hasClass('isChecked')) {
                    _this.removeClass('isChecked');
                    _this.parents('.qbGr:first').removeClass('groupingActivated');
                    if (_this.parents('.qbGr:first').prev('.qbOp').length > 0) {
                        _this.parents('.qbGr:first').prev('.qbOp').removeClass('groupingActivated');
                    }
                } else {
                    _this.addClass('isChecked')
                    _this.parents('.qbGr:first').addClass('groupingActivated');
                    if (_this.parents('.qbGr:first').prev('.qbOp').length > 0 && _this.parents('.qbGr:first').prev('.qbOpAlt').length <= 0) {
                        _this.parents('.qbGr:first').prev('.qbOp').addClass('groupingActivated');
                    }
                }
            });

            // add attribute
            $('#beneficiary_queryBuilder .btnAddAttr').click(function() {
                var newOp = beneficiary_template.operator.clone();
                var newGr = beneficiary_template.group.clone();
                if ($('.qbGr', $(this).parents('.queryBuilder:first')).length > 0) {
                    newOp.addClass('isMovable').fadeIn().insertBefore($(this).parents('.qbAction:first'));
                }
                newGr.addClass('isMovable').fadeIn().insertBefore($(this).parents('.qbAction:first'));
                qb.updateQuery();
            });
            $('#carrier_queryBuilder .btnAddAttr').click(function() {
                var newOp = carrier_template.operator.clone();
                var newGr = carrier_template.group.clone();
                if ($('.qbGr', $(this).parents('.queryBuilder:first')).length > 0) {
                    newOp.addClass('isMovable').fadeIn().insertBefore($(this).parents('.qbAction:first'));
                }
                newGr.addClass('isMovable').fadeIn().insertBefore($(this).parents('.qbAction:first'));
                qb.updateQuery();
            });
            $('#inpatient_queryBuilder .btnAddAttr').click(function() {
                var newOp = inpatient_template.operator.clone();
                var newGr = inpatient_template.group.clone();
                if ($('.qbGr', $(this).parents('.queryBuilder:first')).length > 0) {
                    newOp.addClass('isMovable').fadeIn().insertBefore($(this).parents('.qbAction:first'));
                }
                newGr.addClass('isMovable').fadeIn().insertBefore($(this).parents('.qbAction:first'));
                qb.updateQuery();
            });
            $('#outpatient_queryBuilder .btnAddAttr').click(function() {
                var newOp = outpatient_template.operator.clone();
                var newGr = outpatient_template.group.clone();
                if ($('.qbGr', $(this).parents('.queryBuilder:first')).length > 0) {
                    newOp.addClass('isMovable').fadeIn().insertBefore($(this).parents('.qbAction:first'));
                }
                newGr.addClass('isMovable').fadeIn().insertBefore($(this).parents('.qbAction:first'));
                qb.updateQuery();
            });
            $('#prescription_queryBuilder .btnAddAttr').click(function() {
                var newOp = prescription_template.operator.clone();
                var newGr = prescription_template.group.clone();
                if ($('.qbGr', $(this).parents('.queryBuilder:first')).length > 0) {
                    newOp.addClass('isMovable').fadeIn().insertBefore($(this).parents('.qbAction:first'));
                }
                newGr.addClass('isMovable').fadeIn().insertBefore($(this).parents('.qbAction:first'));
                qb.updateQuery();
            });

            // group selected expression
            $('#beneficiary_queryBuilder .btnGroup').click(function() {

                /*
                 * parent and child grouping can't be possible like for this
                 * expression (A and (B and (C and D))) we can't group (C and D)
                 * with group (A and (B and (C and D)))
                 */
                var isValid = true;
                $('.groupingActivated').each(function() {
                    if ($(this).parents('.groupingActivated').length > 0) {
                        isValid = false;
                        return false;
                    }
                });

                if (!isValid) {
                    return false;
                }

                $(this).parents('.rule-switch').find('.queryConstructor .qbGr.groupingActivated:first').prev('.qbOp').removeClass('groupingActivated');
                var groupingRows = $(this).parents('.rule-switch').find('.queryConstructor .groupingActivated');

                groupingRows.each(function() {
                    if ($(this).prev().hasClass('qbOpAlt') && $(this).next('.qbOp').length <= 0) {
                        $(this).addClass('autoAdd');
                    }
                });

                var parentGr = beneficiary_template.prntGroup.clone();
                parentGr.insertBefore(groupingRows.eq(0));
                // add selected groups
                parentGr.prepend(groupingRows);
                // add operator
                parentGr.prepend(beneficiary_template.lvl1Is.clone());
                $('.groupingActivated .isChecked').removeClass('isChecked');
                $('.groupingActivated').removeClass('groupingActivated');

                qb.fixGroup(parentGr);
                qb.updateQuery();
            });
            $('#carrier_queryBuilder .btnGroup').click(function() {

                /*
                 * parent and child grouping can't be possible like for this
                 * expression (A and (B and (C and D))) we can't group (C and D)
                 * with group (A and (B and (C and D)))
                 */
                var isValid = true;
                $('.groupingActivated').each(function() {
                    if ($(this).parents('.groupingActivated').length > 0) {
                        isValid = false;
                        return false;
                    }
                });

                if (!isValid) {
                    return false;
                }

                $(this).parents('.rule-switch').find('.queryConstructor .qbGr.groupingActivated:first').prev('.qbOp').removeClass('groupingActivated');
                var groupingRows = $(this).parents('.rule-switch').find('.queryConstructor .groupingActivated');

                groupingRows.each(function() {
                    if ($(this).prev().hasClass('qbOpAlt') && $(this).next('.qbOp').length <= 0) {

                        $(this).addClass('autoAdd');
                    }
                });

                var parentGr = carrier_template.prntGroup.clone();
                parentGr.insertBefore(groupingRows.eq(0));
                // add selected groups
                parentGr.prepend(groupingRows);
                // add operator
                parentGr.prepend(carrier_template.lvl1Is.clone());
                $('.groupingActivated .isChecked').removeClass('isChecked');
                $('.groupingActivated').removeClass('groupingActivated');

                qb.fixGroup(parentGr);
                qb.updateQuery();
            });
            $('#inpatient_queryBuilder .btnGroup').click(function() {

                /*
                 * parent and child grouping can't be possible like for this
                 * expression (A and (B and (C and D))) we can't group (C and D)
                 * with group (A and (B and (C and D)))
                 */
                var isValid = true;
                $('.groupingActivated').each(function() {
                    if ($(this).parents('.groupingActivated').length > 0) {
                        isValid = false;
                        return false;
                    }
                });

                if (!isValid) {
                    return false;
                }

                $(this).parents('.rule-switch').find('.queryConstructor .qbGr.groupingActivated:first').prev('.qbOp').removeClass('groupingActivated');
                var groupingRows = $(this).parents('.rule-switch').find('.queryConstructor .groupingActivated');

                groupingRows.each(function() {
                    if ($(this).prev().hasClass('qbOpAlt') && $(this).next('.qbOp').length <= 0) {

                        $(this).addClass('autoAdd');
                    }
                });

                var parentGr = inpatient_template.prntGroup.clone();
                parentGr.insertBefore(groupingRows.eq(0));
                // add selected groups
                parentGr.prepend(groupingRows);
                // add operator
                parentGr.prepend(inpatient_template.lvl1Is.clone());
                $('.groupingActivated .isChecked').removeClass('isChecked');
                $('.groupingActivated').removeClass('groupingActivated');

                qb.fixGroup(parentGr);
                qb.updateQuery();
            });
            $('#outpatient_queryBuilder .btnGroup').click(function() {

                /*
                 * parent and child grouping can't be possible like for this
                 * expression (A and (B and (C and D))) we can't group (C and D)
                 * with group (A and (B and (C and D)))
                 */
                var isValid = true;
                $('.groupingActivated').each(function() {
                    if ($(this).parents('.groupingActivated').length > 0) {
                        isValid = false;
                        return false;
                    }
                });

                if (!isValid) {
                    return false;
                }

                $(this).parents('.rule-switch').find('.queryConstructor .qbGr.groupingActivated:first').prev('.qbOp').removeClass('groupingActivated');
                var groupingRows = $(this).parents('.rule-switch').find('.queryConstructor .groupingActivated');

                groupingRows.each(function() {
                    if ($(this).prev().hasClass('qbOpAlt') && $(this).next('.qbOp').length <= 0) {

                        $(this).addClass('autoAdd');
                    }
                });

                var parentGr = outpatient_template.prntGroup.clone();
                parentGr.insertBefore(groupingRows.eq(0));
                // add selected groups
                parentGr.prepend(groupingRows);
                // add operator
                parentGr.prepend(outpatient_template.lvl1Is.clone());
                $('.groupingActivated .isChecked').removeClass('isChecked');
                $('.groupingActivated').removeClass('groupingActivated');

                qb.fixGroup(parentGr);
                qb.updateQuery();
            });
            $('#prescription_queryBuilder .btnGroup').click(function() {

                /*
                 * parent and child grouping can't be possible like for this
                 * expression (A and (B and (C and D))) we can't group (C and D)
                 * with group (A and (B and (C and D)))
                 */
                var isValid = true;
                $('.groupingActivated').each(function() {
                    if ($(this).parents('.groupingActivated').length > 0) {
                        isValid = false;
                        return false;
                    }
                });

                if (!isValid) {
                    return false;
                }

                $(this).parents('.rule-switch').find('.queryConstructor .qbGr.groupingActivated:first').prev('.qbOp').removeClass('groupingActivated');
                var groupingRows = $(this).parents('.rule-switch').find('.queryConstructor .groupingActivated');

                groupingRows.each(function() {
                    if ($(this).prev().hasClass('qbOpAlt') && $(this).next('.qbOp').length <= 0) {

                        $(this).addClass('autoAdd');
                    }
                });

                var parentGr = prescription_template.prntGroup.clone();
                parentGr.insertBefore(groupingRows.eq(0));
                // add selected groups
                parentGr.prepend(groupingRows);
                // add operator
                parentGr.prepend(prescription_template.lvl1Is.clone());
                $('.groupingActivated .isChecked').removeClass('isChecked');
                $('.groupingActivated').removeClass('groupingActivated');

                qb.fixGroup(parentGr);
                qb.updateQuery();
            });

            // un-grouping selected group
            $('.queryBuilder .btnUnGroup').click(function() {
                var parent = $('.groupingActivated').parent();
                $('.groupingActivated').each(function() {
                    if ($('.qbGr', $(this)).length > 0) {
                        $(this).children('.qbOpAlt').remove();
                        $(this).children('.chkGr').remove();
                        $(this).children('.removeGr').remove();
                        $(this).children('.mask').remove();
                        $('.qbGr:first', $(this)).unwrap();
                    }
                });
                qb.fixGroup(parent);
                qb.updateQuery();
            });

            // merge
            $('.btnMerge').click(function() {
                var fstEl = $('.qbGrAlt.groupingActivated:eq(0)');
                var prnt = fstEl.parent();
                var invalidGroup = false;
                // check selected expressions are groups (not independent
                // expression) and all selected groups are at same level
                $(this).parents('.rule-switch').find('.queryConstructor .qbGr.groupingActivated').each(function() {
                    if (!$(this).hasClass('qbGrAlt') || $(this).parent()[0] != prnt[0]) {
                        invalidGroup = true;
                        return false;
                    }
                });

                if (invalidGroup) {
                    return false;
                }

                $(this).parents('.rule-switch').find('.queryConstructor .qbGrAlt.groupingActivated').each(function(idx) {
                    if (idx > 0) {
                        fstEl.append($(this).prev('.groupingActivated'));
                        fstEl.append($(this).children('.qbOp, .qbGr').not('.qbOpAlt'));
                        $(this).remove();
                    }
                });
                fstEl.append(fstEl.children('.chkGr, .removeGr, .mask'));
                $('.groupingActivated .isChecked').removeClass('isChecked');
                $('.groupingActivated').removeClass('groupingActivated');
                qb.fixGroup(fstEl);
                qb.updateQuery();
            });

            // remove group
            $('.queryConstructor').delegate('.removeGr', 'click', function(e) {
                var _this = $(e.currentTarget);
                _this.parents('.qbGr:first').addClass('ready2Bremoved');
                var prev = _this.parents('.qbGr:first').prev();
                if (prev.hasClass('qbOp') && !prev.hasClass('qbOpAlt')) {
                    prev.addClass('ready2Bremoved');
                } else {
                    _this.parents('.qbGr:first').next('.qbOp').addClass('ready2Bremoved');
                }
                _this.parents('.qbGr:first').addClass('ready2Bremoved');
                $('.ready2Bremoved').remove();
                qb.updateQuery();
            });

            // remove all
            $('.btnClearAll').click(function() {
                closeModal();
                loadModal('#removeGroup');
            });

            qb.initSortable();

            // modal functions
            $('#removeGroup .btnNo').click(function() {
                $('.ready2Bremoved').removeClass('ready2Bremoved');
                closeModal();
            });
            $('#removeGroup .btnYes').click(function() {
                // IMMMMMMMJ, Need to be optimized.
                $('.queryConstructor').children().not('.qbAction').remove();
                closeModal();
                qb.updateQuery();
				//https://apps.topcoder.com/bugs/browse/BUGR-10293
				$('#beneficiary_builtQuery').html('');
                $('#carrier_builtQuery').html('');
                $('#inpatient_builtQuery').html('');
                $('#outpatient_builtQuery').html('');
                $('#prescription_builtQuery').html('');
				$('.rule-switch').find('.empty-error').addClass('hide');
                $("#js-choose-partner").find(".js-all").text("Unselect All").trigger("click");
            });
        },

        initSortable : function() {
            var idx = '';
            var prnt = '';
            $('.queryConstructor').sortable({
                cancel : ".chkGr, .removeGr, .selectWrap, .qbOp, input",
                items : ".qbGr.isMovable",
                placeholder : "qbLine",
                helper : 'clone',
                forceHelperSize : false,
                distance : 30,
                dropOnEmpty : false,
                start : function(e, ui) {
                    $(ui.item).removeClass('isMovable');
                    ui.item.prev().removeClass('isMovable');
    //				console.log(ui.item.prev());
                    $('.qbLine').append($('<span class="dot"></span>'));
                    if ($('.qbGr', ui.helper).length > 3) {
                        // $('.qbOp', ui.helper).css('visibility', 'hidden');
                        // $('.qbOp:eq(0), .qbOp:eq(1), .qbOp:eq(2)',
                        // ui.helper).css('visibility', 'visible');
                        ui.helper.addClass('extended');
                    }
                    ;
                    idx = ui.item.index();
                    prnt = ui.item.parent();
                    ui.item.show().addClass('qbGrOriginal');
                    var prev = ui.item.prev('.qbOp');
                    if (prev.not('.qbOpAlt').length > 0 && !prev.hasClass('qbOpAlt')) {
                        prev.addClass('currentQbGr');
                    } else if (prev.is('.qbOpAlt')) {
                        ui.item.next().next('.qbOp').addClass('currentQbGr');
                    } else if (prev.length === 0) {
                        ui.item.next().next('.qbOp').addClass('currentQbGr');
                    }

                    if (prev.is('.qbOpAlt') && ui.item.next().next('.qbOp').length <= 0) {
                        ui.item.addClass('autoAdd');
                    }

                },
                stop : function(e, ui) {
                    $(ui.item).addClass('isMovable');
                    $('.qbOp', $(ui.item.parent())).addClass('isMovable');
                //	console.log(ui.item.prev());
                    // if dorped at same postion then return
                    if (ui.item.parent()[0] === prnt[0] && idx === ui.item.index()) {
                        idx = -1;
                        prnt = "";
                        $('.currentQbGr').removeClass('currentQbGr');
                        $('.qbGrOriginal').removeClass('qbGrOriginal');
                        return false;
                    }
                    if (ui.item.prev().length > 0 && ui.item.prev().hasClass('qbOp')) {
                        $('.currentQbGr').insertAfter(ui.item);
                    } else if (ui.item.prev().length <= 0) {
                        $('.currentQbGr').insertAfter(ui.item);
                    } else {
                        $('.currentQbGr').insertBefore(ui.item);
                    }
                    var qbo = $('.qbGrOriginal');
                    $('.mask', qbo).fadeOut('slow', function() {
                        $('.mask', qbo).removeAttr('style');
                        qbo.removeClass('qbGrOriginal');
                        $('.currentQbGr').removeClass('currentQbGr');
                    });

                    // fix operators within a group
                    var prntGroup = ui.item.parent();
                    // if (prntGroup.hasClass('qbGrAlt')) {
                    qb.fixGroup(prntGroup);
                    // }
                    qb.updateQuery();
                }
            });
        },

        // fixes the AND & OR operators in a group
        fixGroup : function(gr) {
            // selecting leftmost as base operator
            var ops = gr.children('.qbOp').not('.qbOpAlt');
            var baseOp = ops.eq(0).find('.operator');
            var baseTxt = baseOp.text();
            ops.each(function() {
                $('.operator', $(this)).text(baseTxt);
            });

            baseTxt = 'AND' || "";
            // IMMMMJ need to be optimized.
            var clone = beneficiary_template.operator.clone();
            clone.find('.operator').text(baseTxt);
            if ($('.autoAdd').prev('.qbOp').length <= 0) {
                clone.insertBefore($('.autoAdd'));
            } else if ($('.autoAdd').next('.qbOp').length <= 0) {
                clone.insertAfter($('.autoAdd'));
            }
            $('.autoAdd').removeClass('autoAdd');
        },
        // readExpression
        readExpression : function(group) {
            var result = "";
            var th = group;
            var attrList = $('.selectWrap .val:visible, .operator:visible, input:visible', th);

            // attribute name
             var attributeName = "'" + $.trim(attrList.eq(0).text()) + "'";
            // IS (or) NOT
            var isNot = $.trim(attrList.eq(2).text());
            // less than, equal to...
            var comparision = $.trim(attrList.eq(1).text());

            if (isNot == "IS"){
                result += " <span class='keyword'>(</span> " + attributeName + " " + isNot + " " + comparision;
            }
            else{
                result += isNot + " <span class='keyword'>(</span> " + attributeName + " " + comparision;
            }

            // values
            if (attrList.length > 3) {
                if (attrList.eq(3).hasClass('txtNormal')) {
                    result += " " + $.trim(attrList.eq(3).val());
                } else {
                    result += " $" + $.trim(attrList.eq(3).val());
                }
            }
            if (attrList.length > 4) {
                if (attrList.eq(4).hasClass('txtNormal')) {
                    result += " to " + $.trim(attrList.eq(4).val());
                } else {
                    result += " to $" + $.trim(attrList.eq(4).val());
                }
            }
            result += " <span class='keyword'>)</span> "
            return result;
        },
        // update query
        updateQuery : function() {
            var currTab = $('.switch-tab-container .switch-tab li.active a').text();
            var query = null;
            if (currTab == "Beneficiary") {
		    query = qb.buidQuery($('#beneficiary_queryConstructor'));
		    $('#beneficiary_builtQuery').html(query);
            } else if (currTab == "Carrier") {
		    query = qb.buidQuery($('#carrier_queryConstructor'));
		    $('#carrier_builtQuery').html(query);
            } else if (currTab == "Inpatient") {
		    query = qb.buidQuery($('#inpatient_queryConstructor'));
		    $('#inpatient_builtQuery').html(query);
            } else if (currTab == "Outpatient") {
		    query = qb.buidQuery($('#outpatient_queryConstructor'));
		    $('#outpatient_builtQuery').html(query);
            } else {
		    query = qb.buidQuery($('#prescription_queryConstructor'));
		    $('#prescription_builtQuery').html(query);
            }

            $('.isTraversed').removeClass('isTraversed');
        },
          // return the query string
        getQuery: function(idx) {
            if (idx == 0)return $('#beneficiary_builtQuery').text();
            if (idx == 1)return $('#carrier_builtQuery').text();
            if (idx == 2)return $('#inpatient_builtQuery').text();
            if (idx == 3)return $('#outpatient_builtQuery').text();
            if (idx == 4)return $('#prescription_builtQuery').text();
            return "";
            //return $('#builtQuery').text();
        },
        // buildQuery that is displayed in test box
        buidQuery : function(el, res) {
            var result = res || "";

            $('.qbGr', el).each(function() {
                if (!$(this).hasClass('isTraversed')) {

                    if ($(this).hasClass('qbGrAlt')) {
                        result += $(this).children('.qbOpAlt:first').find('.operator').text() + " <span class='keyword'>(</span> " + qb.buidQuery($(this), "") + "<span class='keyword'>)</span>";
                    } else {
                        result += qb.readExpression($(this));
                    }

                    if ($(this).next('.qbOp').length > 0) {
                        result += " <span class='keyword'>" + $(this).next('.qbOp').find('.operator').text() + "</span> ";
                    }
                    $(this).addClass('isTraversed');
                }
            });
            result = result.replace(/IS/g,'')
            return result + "";
        }
    }
})();
/* end of query build functions */

/* visual analysis functions */
(function(){
    var FIRST_DIMENSION_LIMIT = 32;
    var SECOND_DIMENSION_LIMIT = 10;

    // Shrink date to months or years
    function shrinkDate(data, field, group) {
        if (!group) return;
        var date;
        var shrink;
        var fieldOrig = field + '_orig';
        if (group == 'day') shrink = undefined;
        if (group == 'month') shrink = 7;
        if (group == 'year') shrink = 4;
        for (var i = 0, l = data.length; i < l; i++) {
            if (data[i][fieldOrig]) {
                date = data[i][fieldOrig];
            } else {
                date = data[i][field];
                data[i][fieldOrig] = date;
            }
            if (!date) continue;
            data[i][field] = date.substr(0,shrink);
        }
    }

    // Aggregate data by dimensions and measure
    function aggregateData(data, params) {
        if (data.length == 0) {
            throw {
                fatal: 'Dataset is empty. Can not draw the graph.'
            }
        }

        var i, j, l;

        var dimHash = [];
        var keyHash = {};
        var dimValues = [];
        var rec, dim, key;
        var dl = params.dimensions.length;
        var dimensions = [];

        for (j = 0; j < dl; j++) {
            dimHash[j] = {};
            dimValues[j] = [];
            dimensions.push(params.dimensions[j].name);
        }
        var claimDataIndex = $('.claim-data-tab-container .tab li').index($('.claim-data-tab-container .tab li.active')[0]);
        for (i = 0, l = data.length; i < l; i++) {
            rec = data[i];
            key = [];
            for (j = 0; j < dl; j++) {
                dim = rec[dimensions[j]];
                key.push(dim);
                if (!dimHash[j][dim]) {
                    dimHash[j][dim] = 1;
                    dimValues[j].push(dim);
                }
            }
            key = key.join('|');

            if (!keyHash[key]) {
                keyHash[key] = (fieldTypesAll[claimDataIndex][params.measure] == 'integer' ? rec[params.measure] : 1);
            } else {
                keyHash[key] += (fieldTypesAll[claimDataIndex][params.measure] == 'integer' ? rec[params.measure] : 1);
            }
        }

        var res = {
            data: {
                lines: []
            }
        };

        // Available data is limited to prevent bad charts drawing
        if (dimValues[0].length > FIRST_DIMENSION_LIMIT) {
            dimValues[0] = dimValues[0].splice(-FIRST_DIMENSION_LIMIT);
        }
        if (dimValues[1] && dimValues[1].length > SECOND_DIMENSION_LIMIT) {
            dimValues[1] = dimValues[1].splice(-SECOND_DIMENSION_LIMIT);
            throw {
                fatal: 'Group dimension limit reached. Chart can not be displayed'
            }
        }

        for (i = 0; i < dimValues.length; i++) {
            dimValues[i].sort();
        }

        // Copy values to dimension axis
        res.xAxis = dimValues[0].slice();

        if (dimValues.length == 1) {
            res.data.lines[0] = {data: [], title: ""};
            for (i = 0; i < dimValues[0].length; i++){
                res.data.lines[0].data.push(keyHash[dimValues[0][i]]);
            }
        } else {
            for (j = 0; j < dimValues[1].length; j++) {
                var line = {
                    data: [],
                    title: dimValues[1][j]
                };
                res.data.lines.push(line);
                for (i = 0; i < dimValues[0].length; i++){
                    res.data.lines[j].data.push(keyHash[dimValues[0][i] + '|' + dimValues[1][j]]);
                }
            }
        }

        return res;
    }

    function ChartTab(container, data){
        container = $(container);

        var paper = Raphael(container[0], container.width(), container.height());
        paper.chart(data);
        container.on('show', function(){
            if (paper.width != container.width() || paper.height != container.height()) {
                paper.clear();
                paper.setSize(container.width(), container.height());
                paper.chart(data);
            }
        });
        return paper;
    }

    $(function(){
        // cache elements
        var vaContainer = $('.visual-analysis-content');
        var vaControls = $('.analysis-content', vaContainer);
        var vaTabs = $('.sub-tab-container', vaContainer);
        var vaTabWrapper = $('.tab-wrapper', vaTabs);
        var vaError = $('.error-message', vaControls);

        if(vaContainer.length==0 || vaContainer.hasClass("is-nonedit")) return;

        // set tab wrapper resize constraints
        function setTabWrapperConstraints(){
            setTimeout(function(){
                vaTabWrapper.resizable("option", {
                    minWidth: vaTabWrapper.width(),
                    maxWidth: vaTabWrapper.width()
                });
            }, 0);
        }

        // do tab wrapper resize
        var processTabResize = (function(){
            var timer = null;

            // redraw chart not often than once per 100ms
            return function(){
                if (timer) return;
                timer = setTimeout(function(){
                    $(".sub-tab-content:visible", vaTabs).trigger('show');
                    timer = null;
                }, 100);
            }
        })();

        // window resize handler
        $(window).on('resize', function(){
            processTabResize();
            setTabWrapperConstraints();
        });

        // set constraints when tab wrapper stays visible
        $('.visual-analysis').click(function(){
            setTabWrapperConstraints();
        });

        vaTabWrapper.resizable({
            handles: "se",
            minHeight: 250
        });

        // display appropriate dimension config controls when dimension was changed
        $('.dimension select', vaControls).each(function(){
            this.onchange = function(){
                var claimDataIndex = $('.claim-data-tab-container .tab li').index($('.claim-data-tab-container .tab li.active')[0]);
                var dataType = fieldTypesAll[claimDataIndex][this.value] || 'none';
                var configElem = $(this).closest('.dimension').next();
                configElem.attr('class', 'dimension-config type-' + dataType);
                $('input', configElem).each(function(){
                    this.value = "";
                });
            };
        });

        $('#chart-type')[0].onchange = function(){
            switch (this.value) {
                case 'pie':
                    $('.fancy', vaControls).slideDown();
                    $('.bar-config', vaControls).slideUp();
                    break;
                case 'bars':
                    $('.fancy', vaControls).slideUp();
                    $('.bar-config', vaControls).slideDown();
                    break;
                case 'lines':
                    $('.fancy', vaControls).slideUp();
                    $('.bar-config', vaControls).slideUp();
                    break;
            }
        };
        $('#chart-type')[0].onchange();


        function getDimensionConfig(claimDataIndex){
            var result = {
                dimensions: [],
                constraints: []
            };
            $('.dimension select', vaControls).each(function(){
                var configElem = $(this).closest('.dimension').next();
                var dimension = this.value;
                var fieldType = fieldTypesAll[claimDataIndex][dimension];
                var values, constraint;
                switch (fieldType) {
                    case 'string':
                        result.dimensions.push({
                            name: dimension,
                            dataType: fieldType
                        });
                        values = $('.discreteSet input', configElem).val();
                        result.constraints.push(values ? { values: values.split(',') } : null);
                        break;
                    case 'date':
                    // drop to integer case is valid
                    case 'float':
                    case 'integer':
                        result.dimensions.push({
                            name: dimension,
                            dataType: fieldType
                        });
                        if (fieldType == 'date') {
                            result.dimensions[result.dimensions.length - 1].group = $('.dateAggregation select', configElem).val();
                        }
                        values = $('.discreteSet input', configElem).val();
                        constraint = {};
                        if (values) {
                            constraint.values = values.split(',');
                        } else {
                            if ($('.minValue input', configElem).val()) {
                                constraint.min = $('.minValue input', configElem).val();
                            }
                            if ($('.maxValue input', configElem).val()) {
                                constraint.max = $('.maxValue input', configElem).val();
                            }
                        }
                        result.constraints.push($.isEmptyObject(constraint) ? null : constraint);
                        break;
                }
            });

            var measureBlock = $(".measure", vaControls);
            result.measure = $("select", measureBlock).val();
            result.measureTitle = $("select option:selected", measureBlock).text();
            result.measureMin = parseFloat($('.minValue input', measureBlock).val());
            result.measureMax = parseFloat($('.maxValue input', measureBlock).val());

            if (result.dimensions.length == 0) {
                throw {
                    fatal: 'No dimension chosen. Please choose dimension.'
                };

            }
            if (!result.measure) {
                throw {
                    fatal: 'No measure chosen. Please choose measure.'
                };
            }

            if (result.dimensions.length == 2 && result.dimensions[0].name == result.dimensions[1].name){
                throw {
                    fatal: 'The same field chosen for dimension and group. Please choose different fields there.'
                };
            }

            return result;
        }

        var chartName = ["A"];
        function genChartName(){
            var i = chartName.length - 1;
            while (i >= 0) {
                if (chartName[i] != "Z") {
                    chartName[i] = String.fromCharCode(chartName[i].charCodeAt(0) + 1);
                    break;
                }
                chartName[i] = "A";
                i--;
            }
            if (i < 0) {
                chartName.push("A");
            }
            return chartName.join("");
        }

        vaError.click(function(){
            var timer = vaError.data('timer');
            if (timer) {
                clearTimeout(timer);
                timer = null;
            }
            vaError.hide();
        });

        function showErrorMessage(error){
            $('.error-text', vaError).html(error);
            vaError.show();
            var timer = vaError.data('timer');
            if (timer) {
                clearTimeout(timer);
                timer = null;
            }
            if (error) {
                timer = setTimeout(function(){
                    vaError.fadeOut();
                    vaError.data('timer', null);
                }, 50000);
            }
            vaError.data('timer', timer);
        }

        // generate new chart
        $('.btn-generate').click(function(){
            try {
                var claimDataIndex = $('.claim-data-tab-container .tab li').index($('.claim-data-tab-container .tab li.active')[0]);
                var dimConfig = getDimensionConfig(claimDataIndex);
                var fieldNames = [];
                var data = [];
                $('.switch-claim-data-tab-content .table-tbody').eq(claimDataIndex).find('.table-header .cell-claim-data').each(function() {
                    fieldNames.push($(this).find('span').data('field'));
                });
                $('.switch-claim-data-tab-content .table-tbody').eq(claimDataIndex).find('.js-row').each(function() {
                    var rdata = $(this);
                    var tdata = {};
                    var j = 0;
                    rdata.find('.cell-claim-data').each(function() {
                        tdata[fieldNames[j]] = $(this).text();
                        if (fieldTypesAll[claimDataIndex][fieldNames[j]] === 'integer') {
                            tdata[fieldNames[j]] = parseFloat(tdata[fieldNames[j]]);
                        }
                        j += 1;
                    });
                    data.push(tdata);
                });

                //var data = vaTestData.slice();
                for (var i = 0; i < dimConfig.dimensions.length; i++) {
                    var dimName = dimConfig.dimensions[i].name;
                    if (dimConfig.dimensions[i].group) {
                        // shrinking date values to years or years and months
                        shrinkDate(data, dimName, dimConfig.dimensions[i].group);
                    }

                    // constraints handling
                    var constraint = dimConfig.constraints[i];
                    if (!$.isEmptyObject(constraint)) {
                        if (constraint.values) {
                            // filtering dimension by discrete list of values
                            var allowed = {};
                            for (var j = 0; j < constraint.values.length; j++) {
                                allowed[$.trim(constraint.values[j])] = true;
                            }
                            data = data.filter(function(rec){
                                return allowed[rec[dimName]];
                            });
                        } else {
                            // filtering dimension by min and max values
                            if (constraint.min || constraint.max) {
                                data = data.filter(function(rec){
                                    if (constraint.hasOwnProperty('min') && rec[dimName] < constraint.min) return false;
                                    if (constraint.hasOwnProperty('max') && rec[dimName] > constraint.max) return false;
                                    return true;
                                });
                            }
                        }
                    }
                }

                // building chart config with data by dimensions config
                var chartConfig = aggregateData(data, dimConfig);

                chartConfig.measureTitle = dimConfig.measureTitle;
                chartConfig.chartType = $('#chart-type').val();
                if (chartConfig.chartType == 'bars') {
                    chartConfig.barType = $('#bar-type').val();
                    chartConfig.barOrientation = $('#bar-orientation').val();
                }
                chartConfig.measureMin = dimConfig.measureMin;
                chartConfig.measureMax = dimConfig.measureMax;
                chartConfig.autofit = !!($('#autofit').attr('checked'));
                chartConfig.fancy = !!($('#fancy').attr('checked'));

            } catch (e) {
                showErrorMessage(e.fatal);
                return;
            }

            vaError.trigger('click');
            var chartName = $.trim($('#chart-name').val());
            if (chartName.length === 0) {
                   showErrorMessage('The chart name cannot be empty');
                   return;
            }
            var exists = false;
            vaTabs.find('.sub-tab-title').each(function() {
               if ($('a', $(this)).text() === chartName) {
                   exists = true;
               }
            });
            if (exists) {
               showErrorMessage('The chart name already exists');
               return;
            }
            var container = $('<div class="sub-tab-content" style="display: none;"><div class=""></div></div>');
            var tab = $('<li><div class="sub-tab-title"><a href="javascript:;">' + ($('#chart-name').val() || 'NO NAME') + '</a><div class="corner tl"></div><div class="corner tr"></div></div><a href="javascript:;" class="remove">REMOVE</a></li>');

            $('#chart-name').val(genChartName());

            $('ul', vaTabs).append(tab);
            $('.tab-wrapper', vaTabs).append(container);
            $('.remove', vaTabs).show();

            tab.trigger("click");

            var paper = ChartTab(container[0], chartConfig);
            //var svg = $('.sub-tab-content:last').find('svg')[0].outerHTML;
            var svg = paper.toSVG();
            var form = $('#study-chart-form');
            $('#id_chart_study').val($('#study_id').data('id'));
            $('#id_chart_name').val(chartName);
            $('#id_chart_svg').val(svg);
            var ttt = form.serialize();
            $.ajax({
               type: "POST",
               url: '/studies/' + $('#study_id').data('id') + '/charts',
               data: form.serialize(),
               success: function(data)
               {
                   $('.sub-tab-title:last').parent().data('id', data.pk);
               },
               error: function(data) {
                   alert('failed to save study chart:' + chartName);
               }
             });
        });

        // reset max and min inputs if autofit is checked
        $('#autofit').change(function(){
            if (this.checked) {
                $(this).closest('.dimension-config').find('.minValue, .maxValue').each(function(){
                    $("input", this).val("");
                });
            }
        });

    });
})();
/* end of visual analysis functions */

