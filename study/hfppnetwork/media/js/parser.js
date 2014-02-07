/**
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 *
 * Query parser and editor builder
 * @author TCSASSEMBER
 * @version 1.0
 * @since Healthcare Fraud Prevention Release Assembly v1.0
 */

/**
 * Init query editor
 *
 * @stack the parsed stack
 * @context the context
 * @level the group level
 */
function initQueryEditor(stack, context, level) {
    if (stack.content.length > 0) {
        if (context.count > 0) {
            context.btnAddAttr.trigger('click');
        }
        stack.level = level;
        stack.count = 1;
        stack.sindex = context.count;
        context.count += 1;
        context.primitive.push(stack);
    } else {
        stack.count = 0;
        stack.level = level;
        for(var i = 0; i < stack.stack.length; i = i + 1) {
            var s = stack.stack[i];
            if (s == "AND" || s == "OR") {
                // do nothing
            } else {
                initQueryEditor(s, context, level + 1);
                stack.count += s.count;
            }
        }
    }
}

/**
 * Init query editor group
 * @param stack the parsed stack
 * @param context the context
 */
function initQueryEditorGroup(stack, context) {
    if (stack.content.length > 0) {
        // do nothing
    } else {
        for(var i = 0; i < stack.stack.length; i = i + 1) {
            var s = stack.stack[i];
            if (s == "AND" || s == "OR") {
                // do nothing
            } else {
                initQueryEditorGroup(s, context);
            }
        }
        for(var i = 0; i < stack.stack.length; i = i + 1) {
            var s = stack.stack[i];
            if (s == "AND" || s == "OR") {
                // do nothing
            } else {
                if (stack.level > 0) {
                    s.chkGr.trigger('click');
                }
            }
        }
        if (stack.level > 0) {
            context.btnGroup.trigger('click');
            context.queryConstructor.find('.chkGr').each(function() {
               if ($(this).hasClass('js-old') == false) {
                   stack.chkGr = $(this);
               }
            });
            stack.chkGr.addClass('js-old');
            if (stack.not) {
                stack.chkGr.parents('.qbGrAlt ').find('.typeIsNot').first().text('NOT');
            }
        }
    }
}

/**
 * Parse the query and rebuild the rule editor
 *
 * @param query the query
 */
function parseQueryAndRebuild(query) {
    query = query.replace(/\(\s+/g, '(');
    query = query.replace(/\s+\)/g, ')');
    query = query.replace(/\s+\(/g, '(');
    query = query.replace(/\)\s+/g, ')');
    query = query.replace(/\s+NOT\(/g, 'NOT(');
    var stack = [];
    stack.push({idx: 0, content: [], stack: [], level: 0});
    var len = query.length;
    var i = 0;
    var c = null;
    var s = null;
    var syntaxError = false;
    var andOr = [];
    for(i = 0; i < len; i += 1) {
        c = query[i];
        if (i + 4 < len && query[i] == 'N' && query[i + 1] == 'O' && query[i + 2] == 'T' && query[i + 3] == '(') {
            stack.push({idx: i, content: [], stack: [], sindex: 0, not:true});
            i += 3;
        } else if (c == '(') {
            stack.push({idx: i, content: [], stack: [], sindex: 0});
        } else if (c == ')') {
            s = stack.pop();
            stack[stack.length - 1].stack.push(s);
            if (i + 1 < len) {
                if (query[i + 1] == 'A') {
                    if (i + 3 < len && query[i + 2] == 'N' && query[i + 3] == 'D') {
                        stack[stack.length - 1].stack.push('AND');
                        i += 3;
                        andOr.push('AND');
                    } else {
                        syntaxError = true;
                        break;
                    }
                } else if (query[i + 1] == 'O') {
                    if (i + 2 < len && query[i + 2] == 'R') {
                        stack[stack.length - 1].stack.push('OR');
                        i += 2;
                        andOr.push('OR');
                    } else {
                        syntaxError = true;
                        break;
                    }
                } else if (query[i + 1] == ')') {
                    // go on
                } else {
                    syntaxError = true;
                    break;
                }
            }
        } else {
            stack[stack.length - 1].content.push(c);
        }
    }
    if (syntaxError) {
        alert('query syntax error');
        return;
    }
    var tabIndex = $('.switch-tab-container .switch-tab li').index($('.switch-tab-container .switch-tab li.active'));
    var queryConstructor = $('.sub-tab-content .queryConstructor').eq(tabIndex);
    var btnAddAttr = queryConstructor.find('.btnAddAttr');
    var btnGroup = $('.sub-tab-content .queryBuilder .btnGroup');
    var context = {count: 0, andOr: [], queryConstructor:queryConstructor, btnGroup:btnGroup, btnAddAttr:btnAddAttr, primitive:[]};
    initQueryEditor(stack[0], context, 0);
    for(i = 0; i < andOr.length; i = i + 1) {
        queryConstructor.find('.typeAndOr').eq(i).text(andOr[i]);
    }
    if (context.primitive.length) {
        for(i = 0; i <= andOr.length; i = i + 1) {
            context.primitive[i].chkGr = queryConstructor.find('.chkGr').eq(i);
            context.primitive[i].chkGr.addClass('js-old');
        }
    }
    initQueryEditorGroup(stack[0], context);
    if (context.primitive.length) {
        var j = 0;
        for(i = 0; i <= andOr.length; i = i + 1) {
            s = context.primitive[i];
            var wrap = s.chkGr.siblings('.selectWrap');
            var fieldName = null;
            var d = s.content.join('').split("'", 3);
            fieldName = d[1];
            var content = d[2] || '';
            content = $.trim(content);
            var op = null;
            if (content[0] == 'l') {
                if (content.indexOf('less than or equal to') == 0) {
                    op = 'less than or equal to';
                } else if (content.indexOf('less than') == 0) {
                    op = 'less than';
                } else {
                    syntaxError = true;
                    break;
                }
            } else if (content[0] == 'g') {
                if (content.indexOf('greater than or equal to') == 0) {
                    op = 'greater than or equal to';
                } else if (content.indexOf('greater than') == 0) {
                    op = 'greater than';
                } else {
                    syntaxError = true;
                    break;
                }
            } else if (content[0] == 'e') {
                if (content.indexOf('equal to') == 0) {
                    op = "equal to";
                } else {
                    syntaxError = true;
                    break;
                }
            } else if (content[0] == 'b') {
                if (content.indexOf('between') == 0) {
                    op = "between";
                } else {
                    syntaxError = true;
                    break;
                }
            } else if (content[0] == 'i') {
                if (content.indexOf('is') == 0) {
                    op = "is";
                } else {
                    syntaxError = true;
                    break;
                }
            } else if (content[0] == 'm') {
                if (content.indexOf('matches') == 0) {
                    op = "matches";
                } else {
                    syntaxError = true;
                    break;
                }
            } else {
                syntaxError = true;
                break;
            }
            content = $.trim(content.substr(op.length));
            var selListItem = null;
            wrap.find('.selList li a').each(function() {
                if (fieldName == $(this).text()) {
                    selListItem = $(this);
                }
            });
            if (!selListItem) {
                syntaxError = true;
                break;
            }
            selListItem.trigger('click');
            var extendedAttr = s.chkGr.siblings('.extendedAttr');
            if (s.not) {
                extendedAttr.find('.typeIsNot').text('NOT');
            }
            var opItem = null;
            extendedAttr.find('.selList li a').each(function() {
                if (op == $(this).text()) {
                    opItem = $(this);
                }
            });
            if (!opItem) {
                syntaxError = true;
                break;
            }
            opItem.trigger('click');
            if (op != 'between') {
                extendedAttr.find('input.txtNormal').first().val(content);
            } else {
                d = content.split(' to ');
                extendedAttr.find('input.txtNormal').eq(0).val(d[0]);
                extendedAttr.find('input.txtNormal').eq(1).val(d[1]);
            }
        }
        if (syntaxError) {
            alert('query syntax error');
        }
    }
}
