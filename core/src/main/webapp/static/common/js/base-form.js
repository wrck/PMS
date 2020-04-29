/**
 *  add by billJiang 2016/10/9
 *  form 表单数据回填/获取/重置
 */
(function($, window, document, undefined) {
	'use strict';

	var pluginName = 'form';

	$.fn[pluginName] = function (options) {
        if (this == null)
            return null;
        return new BaseForm(this, $.extend(true, {}, options));
    };
    
    var BaseForm = function (element, options) {
        this.$element = $(element);
        this.options = options;
        //icheck
        this.icheckElement = "[data-flag='icheck']";
        //datepicker
        this.datepickerElement = "[data-flag='datepicker']";
        //datetimepicker
        this.datetimepickerElement = "[data-flag='datetimepicker']";
        //selector
        this.dictSelectorElement = "[data-flag='dictSelector']";
        this.urlSelectorElement = "[data-flag='urlSelector']";
        this.select2Element = ".select2";
        // autocomplete
        this.autocompleteElement = "[data-flag='autocomplete']";
        // summernote 富文本
        this.summernoteElement = "[data-flag='summernote']";
        
        this.tagsinputElement = "[data-flag='tagsinput']";
        
        this.autosizeElement = "[data-flag='autosize']";
        this.init();
    }

    //初始化
    BaseForm.prototype.init = function () {
        //baseEntity
        this.initBaseEntity();
        // datepicker
        this.initDatePicker();
        //datetiempicker
        this.initDateTimePicker();
        // icheck 、select2
        this.initComponent();
        //dictSelector
        this.initDictSelector();
        //urlSelector
        this.initUrlSelector();
        // summernote 富文本编辑器
        this.initSummernote();
        // autocomplete
        this.initAutoComplete();
        //tagsinput
        this.initTagsinput();
        //autosize
        this.initAutosize();
    }
    /**
     * autosize的textarea高度自适应
     */
    BaseForm.prototype.initAutosize = function(){
    	var form = this.$element;
        if (form.find(this.autosizeElement).length > 0) {
            autosize(form.find(this.autosizeElement));
        }
    }
    /**
     * 初始化tagsinput
     */
    BaseForm.prototype.initTagsinput = function(){
    	 var form = this.$element;
         if (form.find(this.tagsinputElement).length > 0) {
             form.find(this.tagsinputElement).tagsinput();
             //form.find(this.tagsinputElement).eq(0).addTag('A'); 
             form.find('.bootstrap-tagsinput input').focus(function(){jQuery(this).attr('placeholder', '')});
         }
    }
    
    //主要解决icheck 校验对勾错位的问题
    BaseForm.prototype.initComponent = function () {
        // icheck
        this.initICheck();
        //select2
        this.initSelect2();
    }

    BaseForm.prototype.initSelect2 = function () {
    	if ($.fn.select2) {
    		var defaultConfig = {
                minimumResultsForSearch: Infinity
            };
    		$(this.select2Element, this.$element).each(function() {
    			var config = $(this).data("select2Config") || "";
    			if (config) {
    				try {
    					config = JSON.parse(config);
    				} catch(e){
    					try {
    						config = eval(config);
    					} catch(e){
    						config = defaultConfig;
    					}
    				}
    			} else {
    				config = defaultConfig;
    			}
    			$(this).select2(config);
    			if (config.events) {
    				try {
    					var events = config.events;
    					for ( var key in events) {
							var event = events[key];
							$(this).on(key, eval("("+ event + ")"));
						}
    				} catch(e) {}
    			}
    		});
//    		$(this.select2Element, this.$element).select2({
//                minimumResultsForSearch: Infinity
//            });
    		// 定义数据缓存适配器，在需要的地方$.fn.select2.require(select2/data/dataCacheAdapter),放到参数中
    		$.fn.select2.amd.define('select2/data/dataCacheAdapter', [
    			'select2/data/array',
    			'select2/utils'
    			], function (ArrayData, Utils) {
    				function DataCacheAdapter ($element, options) {
    					DataCacheAdapter.__super__.constructor.call(this, $element, options);
    			    }
    			    Utils.Extend(DataCacheAdapter, ArrayData);
    			    DataCacheAdapter.prototype.query = function (params, callback) {
    			    	var _this = this;
    			    	var key = params.term;
    			    	var page = params.page || 0;
    			    	var options = _this.options.options;
		    			if (!key || key.length < options.minimumInputLength) {
		    				this.trigger('results:message', {
		    			        message: 'inputTooShort',
		    			        args: {
		    			          minimum: options.minimumInputLength,
		    			          input: params.term || "",
		    			          params: params
		    			        }
		    			    });
		                    return;
		                }
		    			this.dataCaches = _this.dataCaches || {};
		    			_this.dataCaches[key] = _this.dataCaches[key] || {};
		                var dataCache = _this.dataCaches[key][page];
		                if (dataCache) {
		                    callback(dataCache);
		                } else {
	                		var ajaxOption = options.ajax;
		                	var request = function() {
	                			ajaxGet(ajaxOption.url, ajaxOption.data(params), function(data) {
	                				var results = data;
	                				if (ajaxOption.processResults) {
	                					results = ajaxOption.processResults.call(_this, data, params);
	                				}
	                				/* var oldReuslts = _this.dataCaches[key];
	                				if((oldReuslts.pagination || {}).more) {
	                					oldReuslts.results.concat(results.results);
	                				} */
	                				_this.dataCaches[key][page] = results;
	                                callback(results);
		    			    	})
		                	};
		                	if (ajaxOption.delay) {
		                		_this._queryTimeout && window.clearTimeout(_this._queryTimeout);
		                		_this._queryTimeout = window.setTimeout(request, ajaxOption.delay);
		                	} else {
		                		request();
		                	}
	                		/* ajaxOption.delay ? (_this._queryTimeout && window.clearTimeout(_this._queryTimeout),
            				_this._queryTimeout = window.setTimeout(request, ajaxOption.delay)) : request(); */
		                }
    			    };
    			    return DataCacheAdapter;
    		});
    	}
    }


    /**
     * 页面增加BaseEntity中的属性
     * 通过是否baseentity配置，baseentity 不配置或为true
     */
    BaseForm.prototype.initBaseEntity = function () {
        if (this.options.baseEntity === false)
            return;
        var form = this.$element;
        /*if (form.find('[name="deleted"]').length == 0) {
            form.prepend("<input type='hidden' name='deleted' value='0'>");
        }
        if (form.find(':hidden[name="createDateTime"]').length == 0) {
            form.prepend('<input type="hidden" name="createDateTime" data-flag="date" data-format="yyyy-mm-dd hh:ii:ss">');
        }
        if (form.find(':hidden[name="version"]').length == 0) {
            form.prepend("<input type='hidden' name='version'>");
        }*/
        if (form.find(':hidden[name="id"]').length == 0) {
            form.prepend("<input type='hidden' id='id' name='id'>");
        }
    }

    /**
     */
    BaseForm.prototype.initICheck = function () {
        var form = this.$element;
        if (form.find('[data-flag="icheck"]').length > 0) {
            form.find('[data-flag="icheck"]').each(function () {
                var cls = $(this).attr("class") ? $(this).attr("class") : "square-green";
                $(this).iCheck(
                    {
                        checkboxClass: 'icheckbox_' + cls,
                        radioClass: 'iradio_' + cls
                    }
                ).on('ifChanged', function (e) {
                    var field = $(this).attr('name');
                    var validator = form.data('bootstrapValidator');
                    if (validator && validator.options.fields[field])
                        validator.updateStatus(field, 'NOT_VALIDATED', null).validateField(field);
                });
            });
        }
    }

    /**
     * 初始化datepicker
     */
    BaseForm.prototype.initDatePicker = function () {
        var form = this.$element;
        if (form.find(this.datepickerElement).length > 0) {
            form.find(this.datepickerElement).datepicker({
                autoclose: true,
                format: $(this).data("format") ? $(this).data("format") : "yyyy-mm-dd",
                language: 'zh-CN',
                clearBtn: true,
                todayHighlight: true
            }).on('change', function (e) {
                var field = $(this).attr('name');
                var validator = form.data('bootstrapValidator');
                if (validator && validator.options.fields[field])
                    validator.updateStatus(field, 'NOT_VALIDATED', null).validateField(field);
            }).parent().css("padding-left", "15px").css("padding-right", "15px");
        }
    }
    /**
     * 初始化datetimepicker
     */
    BaseForm.prototype.initDateTimePicker = function () {
        var form = this.$element;
//        if (form.find(this.datetimepickerElement).length > 0) {
//            form.find(this.datetimepickerElement).datetimepicker({
//                format: $(this).data("format") ? $(this).data("format") : "yyyy-mm-dd hh:ii",
//                autoclose: true,
//                clearBtn: true,
//                language: 'zh-CN'
//            }).on('change', function (e) {
//                var field = $(this).attr('name');
//                var validator = form.data('bootstrapValidator');
//                if (validator && validator.options.fields[field])
//                    validator.updateStatus(field, 'NOT_VALIDATED', null).validateField(field);
//            }).parent().css("padding-left", "15px").css("padding-right", "15px");
//        }
     // datetimepicker
		if (form.find(this.datetimepickerElement).length > 0) {
			form.find(this.datetimepickerElement).each(function(){
				var formatPattern = $(this).data("format").trim();
				if (formatPattern === "yyyy-MM-dd HH:mm:ss") {
					formatPattern = "yyyy-mm-dd hh:ii:ss";
				} else if (formatPattern === "yyyy-MM-dd HH:mm") {
					formatPattern = "yyyy-mm-dd hh:ii";
				} else {
					formatPattern = "yyyy-mm-dd hh:ii";
				}
				$(this).datetimepicker({
					autoclose: true,
					format: formatPattern,
					todayBtn: true,
					clearBtn: true,
					language: 'zh-CN',
					place: function() {
						if (this.isInline) return;

					      if (!this.zIndex) {
					        var index_highest = 0;
					        $('div').each(function () {
					          var index_current = parseInt($(this).css('zIndex'), 10);
					          if (index_current > index_highest) {
					            index_highest = index_current;
					          }
					        });
					        this.zIndex = index_highest + 10;
					      }
					        this.picker.removeClass("datetimepicker-dropdown-bottom-right").removeClass("datetimepicker-dropdown-top-right");
					      var offset, top, left, containerOffset;
					      var calendarWidth = this.picker.outerWidth(),
									calendarHeight = this.picker.outerHeight(),
									visualPadding = 10,
									
									windowWidth = $(window).width(),
									windowHeight = $(window).height(),
									scrollTop = $(window).scrollTop();
									var height = this.component ? this.component.outerHeight(true) : this.element.outerHeight(false);
								var width = this.component ? this.component.outerWidth(true) : this.element.outerWidth(false);
					      if (this.container instanceof $) {
					        containerOffset = this.container.offset();
					      } else {
					        containerOffset = $(this.container).offset();
					      }

					      if (this.component) {
					        offset = this.component.offset();
					        left = offset.left;
					        if (this.pickerPosition === 'bottom-left' || this.pickerPosition === 'top-left') {
					          left += this.component.outerWidth() - this.picker.outerWidth();
					        }
					      } else {
					        offset = this.element.offset();
					        left = offset.left;
					        if (offset.left < 0)
										left -= offset.left - visualPadding;
									else if (offset.left + calendarWidth > windowWidth)
										left = windowWidth - calendarWidth - visualPadding;
					        if (this.pickerPosition === 'bottom-left' || this.pickerPosition === 'top-left') {
					          left += this.element.outerWidth() - this.picker.outerWidth();
					        }
					      }
					      
					   
									top = offset.top;
					      var bodyWidth = document.body.clientWidth || window.innerWidth;
					      if (left + 220 > bodyWidth) {
					        left = bodyWidth - 220;
					      }
						  var yorient, top_overflow, bottom_overflow;
						  top_overflow = -scrollTop + offset.top - calendarHeight;
									bottom_overflow = scrollTop + windowHeight - (offset.top + height + calendarHeight);
									if (bottom_overflow < 0)
										yorient = 'top';
									else
										yorient = 'bottom';
										if (yorient === 'bottom')
									top += height;
								else
									top -= calendarHeight + parseInt(this.picker.css('padding-top'));
//					       if (this.pickerPosition === 'top-left' || this.pickerPosition === 'top-right') {
//					         top = offset.top - this.picker.outerHeight();
//					       } else {
//					         top = offset.top + this.height;
//					       }
					        this.picker.addClass("datetimepicker-dropdown-"+yorient+"-right");
					      top = top - containerOffset.top;
					      left = left - containerOffset.left;

					      this.picker.css({
					        top:    top,
					        left:   left,
					        zIndex: this.zIndex
					      });
					}
				}).on('change', function (e) {
					// Validate the date when user change it
					var field = $(this).attr('name');
					// Get the bootstrapValidator instance
					form.data('bootstrapValidator')
					// Mark the field as not validated, so it'll be re-validated when the
					// user change date
						.updateStatus(field, 'NOT_VALIDATED', null)
						// Validate the field
						.validateField(field);
				}).parent().css("padding-left", "15px").css("padding-right", "15px")
			});
		}
    }

    BaseForm.prototype.initSummernote = function () {
    	var form = this.$element;
    	// summernote 富文本编辑器
		if (form.find(this.summernoteElement).length > 0) {
			form.find(this.summernoteElement).each(function(){
				var placeholder = $(this).attr("placeholder").trim();
				if (!placeholder) {
					placeholder = $(this).data("placeholder").trim();
				}
				placeholder = placeholder ? placeholder : "请输入内容";
				$(this).summernote({     
		            focus: true,   
		            lang:'zh-CN',
		            placeholder:placeholder,
		            minHeight:'100px',
		            // 重写图片上传
		            callbacks: {
		                onImageUpload: function(files) {  
		                    saveImageUpload(files,this);
		                }
		            }
		        });
			});
		}
    }
    BaseForm.prototype.initAutoComplete = function () {
    	var _this = this;
    	if ($.fn.autocomplete) {
    		var defaultConfig = {
				//lookup:[],
				transformResult: function(response, originalQuery) {
					var sel = $(this);
					var src_data = sel.data("src-data") || "data";
					var data = response[src_data];
		            if (typeof data == "string") {
		            	try {
		            		data = JSON.parse(data);
		            	} catch(e) {
		            		data = [];
		            	}
		            }
		            var results = [];
		            if (data && data.length > 0) {
		                var value = sel.data("value") ? sel.data("value") : "id";
		                var text = sel.data("text") ? sel.data("text") : "name";
		                var separator = sel.data("separator") || "-";
		                var storeSource = sel.data("store-source") || false;
		                storeSource = storeSource == "false" || storeSource == false ? false : true;
		                for (var i = 0; i < data.length; i++) {
		                	var textName = [];
		                	var texts = text.split(" ");
		                	for (var t = 0; t < texts.length; t++) {
								var tempText = $.trim(texts[t]);
								if (tempText) {
									textName.push(data[i][tempText]);
								}
							}
		                    var suggestion = {value: textName.join(separator), data:data[i][value]};
		                    if (storeSource) {
		                    	suggestion["source"] = data[i];
		                    }
		                    results.push(suggestion);
		                }
		            }
		            return {suggestions:results};
		            //_this.fillElemValue(sel[0], values);
				}
            };
    		$(this.autocompleteElement, this.$element).each(function(index, item) {
    			var url = $(item).data("src");
                var autoload = $(item).data("autoload");
                autoload = autoload == "false" || autoload == false ? false : true;
    			var config = $(this).data("autocompleteConfig") || "";
    			if (config) {
    				try {
    					config = JSON.parse(config);
    				} catch(e){
    					try {
    						config = eval(config);
    					} catch(e){
    						config = defaultConfig;
    					}
    				}
    				if (config.events) {
        				try {
        					var events = config.events;
        					for ( var key in events) {
    							var event = events[key];
    							config[key] = eval("("+ event + ")");
    						}
        				} catch(e) {}
        			}
    			} else {
    				config = defaultConfig;
    			}
    			config = $.extend(true, {}, defaultConfig, config);
//    			var builder = function(response, originalQuery) {
//					var sel = $(item);
//					var src_data = sel.data("src-data") || "data";
//					var data = response[src_data];
//		            if (typeof data == "string") {
//		            	try {
//		            		data = JSON.parse(data);
//		            	} catch(e) {
//		            		data = [];
//		            	}
//		            }
//		            var results = [];
//		            if (data && data.length > 0) {
//		                var value = sel.data("value") ? sel.data("value") : "id";
//		                var text = sel.data("text") ? sel.data("text") : "name";
//		                var separator = sel.data("separator") || "-";
//		                var storeSource = sel.data("store-source") || false;
//		                storeSource = storeSource == "false" || storeSource == false ? false : true;
//		                for (var i = 0; i < data.length; i++) {
//		                	var textName = [];
//		                	var texts = text.split(" ");
//		                	for (var t = 0; t < texts.length; t++) {
//								var tempText = $.trim(texts[t]);
//								if (tempText) {
//									textName.push(data[i][tempText]);
//								}
//							}
//		                    var suggestion = {value: textName.join(separator), data:data[i][value]};
//		                    if (storeSource) {
//		                    	suggestion["source"] = data[i];
//		                    }
//		                    results.append(suggestion);
//		                }
//		            }
//		            //_this.fillElemValue(sel[0], values);
//				}
    			if (url) {
                    if (autoload) {
                    	_this.getDataByUrl(url, function(data) {
                    		var results = config.transformResult.call(item, data);
                    		config.lookup = results.suggestions;
                    		$(item).autocomplete('setOptions', config);
                    	})
                    } else {
                    	if (config.lazy) {
                    		$(item).one("click", function() {
                    			_this.getDataByUrl(url, function(data) {
                            		var results = config.transformResult.call(item, data);
                            		config.lookup = results.suggestions;
                            		$(item).autocomplete('setOptions', config);
                            	})
                    		})
                    	} else {
                    		config.serviceUrl = config.serviceUrl || url;
                        	if (!config.serviceUrl.startsWith(basePath)) {
                        		config.serviceUrl = basePath + config.serviceUrl;
                        	}
                        	config.dataType = config.dataType || "json";
                        	config.deferRequestBy = config.deferRequestBy || 250;
                    	}
                    	
//                    	config.lookup = function(query, done) {
//                    		_this.getDataByUrl(config.serviceUrl, function(data) {
//                    			done(data);
//                    		});
//                    	}
            			
//                        var that = this;
//                        $(this).click(function () {
//                            _this.buildAjaxUrlSelect(that, url);
//                        })
                    }
                }
    			$(item).autocomplete(config);
    		});
    	}
    }
    
    
    /**
     * 字典类型的控件
     */
    BaseForm.prototype.initDictSelector = function (dictSelectorElement) {
        var _this = this;
        var element = dictSelectorElement ? dictSelectorElement : this.dictSelectorElement;
        var elements = this.$element.find(element);
        $(elements).each(function (index, item) {
            var code = $(item).data("code");
            var autoload = $(item).data("autoload");
            autoload = autoload == "false" || autoload == false ? false : true;
            if (code) {
                if (autoload) {
                    if ($(item).is("input"))
                        _this.buildAjaxDictBox(this, code);
                    else if ($(item).is("select"))
                        _this.buildAjaxDictSelect(this, code);
                } else {
                    var that = this;
                    $(this).click(function () {
                        _this.buildAjaxDictSelect(that, code);
                    })
                }
            }
        });
    }

    /**
     * url外部数据控件
     */
    BaseForm.prototype.initUrlSelector = function (urlSelectElement) {
        var _this = this;
        var element = urlSelectElement ? urlSelectElement : this.urlSelectorElement;
        var elements = this.$element.find(element);
        $(elements).each(function (index, item) {
            var url = $(item).data("src");
            var autoload = $(item).data("autoload");
            var lazy = $(item).data("lazy");
            autoload = autoload == "false" || autoload == false ? false : true;
            if (url) {
                if (autoload) {
                    if ($(item).is("input"))
                        _this.buildAjaxUrlBox(this, url);
                    else if ($(item).is("select"))
                        _this.buildAjaxUrlSelect(this, url);
                } else {
                    var that = this;
                    var isSelect2 = $(item).hasClass("select2");
                    var $target = isSelect2 ? $(item).siblings(".select2.select2-container") : $(item);
                    if (lazy) {
                    	$target.one("click", function() {
                    		_this.buildAjaxUrlSelect(that, url);
                		})
                	} else {
                		$target.click(function () {
	                        _this.buildAjaxUrlSelect(that, url);
	                    })
                	}
                }
            }
        });
    }

    //数据来源为字典的radio checkbox
    BaseForm.prototype.buildAjaxDictBox = function (selector, dictCode) {
        var builder = this.buildAjaxBox(selector);
        $dataSource.getDict(dictCode, builder);
    }
    //数据来源为url的 radio checkbox
    BaseForm.prototype.buildAjaxUrlBox = function (selector, url) {
        var builder = this.buildAjaxBox(selector);
        $dataSource.getDataByUrl(url, builder);
    }
    //数据来源为字典的下拉框
    BaseForm.prototype.buildAjaxDictSelect = function (selector, dictCode) {
        var builder = this.buildAjaxSelector(selector);
        $dataSource.getDict(dictCode, builder);
    }
    //数据来源为url的下拉框
    BaseForm.prototype.buildAjaxUrlSelect = function (selector, url) {
        var builder = this.buildAjaxSelector(selector);
        this.getDataByUrl(url, builder);
    }
    //radio checkbox 渲染并生成
    BaseForm.prototype.buildAjaxBox = function (selector) {
        var type = $(selector).attr("type");
        var name = $(selector).attr("name");
        var value = $(selector).data("value") ? $(selector).data("value") : "id";
        var text = $(selector).data("text") ? $(selector).data("text") : "name";
        var boxtype = type.replace("icheck-", "");
        var builder = function (data) {
            for (var i = 0; i < data.length; i++) {
                var obj = $("<label class='control-label'> " +
                    "<input type='" + boxtype + "' name='" + name + "' value='" + data[i][value] + "'> " + data[i][text] + "</label>&nbsp;");
                if (type.startWith("icheck"))
                    obj = $("<label class='control-label'> " +
                        "<input type='" + boxtype + "' name='" + name + "' class='square-blue' data-flag='icheck' class='flat-red' value='" + data[i][value] + "'> " + data[i][text] + "</label>");
                $(selector).after(obj);
                $(selector).after("&nbsp;&nbsp;")
            }
            $(selector).remove();
        }
        return builder;
    }
    //下拉框组件生成
    BaseForm.prototype.buildAjaxSelector = function (selector) {
        var sel = $(selector);
        if (sel.children().length > 0) {
            return false;
        }
        var _this = this;
        var blank_value = sel.data("blank-value");
        var blank_text = sel.data("blank-text");
        var is_blank = sel.data("blank") ? true : false;
        var src_data = sel.data("src-data") || "data";
        var values = sel.data("selected");
        var builder = function (data) {
            if (is_blank) {
                if (blank_value === undefined && !blank_text)
                    sel.append($('<option></option>'));
                else if (!blank_text)
                    sel.append($("<option value='" + blank_value + "'></option>"));
                else if (blank_value === undefined)
                    sel.append($("<option>" + blank_text + "</option>"));
                else
                    sel.append($("<option value='" + blank_value + "'>" + blank_text + "</option>"));
            }
            data = data[src_data];
            if (typeof data == "string") {
            	try {
            		data = JSON.parse(data);
            	} catch(e) {
            		data = [];
            	}
            }
            if (data && data.length > 0) {
                var value = sel.data("value") ? sel.data("value") : "id";
                var text = sel.data("text") ? sel.data("text") : "name";
                var separator = sel.data("separator") || "-";
                var storeSource = sel.data("store-source") || false;
                storeSource = storeSource == "false" || storeSource == false ? false : true;
                for (var i = 0; i < data.length; i++) {
                	//var option = $("<option value='" + data[i][value] + "'>" + data[i][text] + "</option>");
                	var textName = [];
                	var texts = text.split(" ");
                	for (var t = 0; t < texts.length; t++) {
						var tempText = $.trim(texts[t]);
						if (tempText) {
							textName.push(data[i][tempText]);
						}
					}
                    var option = $("<option value='" + data[i][value] + "'>" + textName.join(separator)+ "</option>");
                    if (storeSource) {
                    	option.data("source", data[i]);
                    }
                    sel.append(option);
                }
            }
            _this.fillElemValue(sel[0], values);
        }
        return builder;
    }
    
    BaseForm.prototype.getDataByUrl = function(url, callback) {
    	if(!url.startsWith(basePath)) {
    		url = basePath + url;
    	}
    	ajaxPost(url, {}, function(data) {
    		//if (data.data && data.data.length > 0 && callback) {
    		if (data && callback) {
                callback(data);
            } 
    	});
    }
//	var BaseForm=function(element,options){
//		this.$element=$(element);
//		this.initFormComponent();
//	}

	//初始化
	BaseForm.prototype.initFormComponent=function() {
		var form = this.$element;
		form = form.length == 0 ? $('form[name="' + form_flag + '"]') : form;
		//给form表单增加BaseEntity中的属性
		if (form.find(':hidden[name="createTime"]').length == 0) {
			form.prepend('<input type="hidden" name="createTime" data-flag="date" data-format="yyyy-MM-dd HH:mm:ss">');
		}
		// icheck
		if (form.find('[data-flag="icheck"]').length > 0) {
			form.find('[data-flag="icheck"]').iCheck({
				checkboxClass: 'icheckbox_square-green',
				radioClass: 'iradio_square-green'
			}).on('ifChanged', function (e) {
				// Get the field name
				var field = $(this).attr('name');
				form
				// Mark the field as not validated
					.bootstrapValidator('updateStatus', field, 'NOT_VALIDATED')
					// Validate field
					.bootstrapValidator('validateField', field);
			});
		}
		// datepicker
		if (form.find('[data-flag="datepicker"]').length > 0) {
			form.find('[data-flag="datepicker"]').datepicker({
				autoclose: true,
				format: 'yyyy-mm-dd',
				language: 'cn'
			}).on('change', function (e) {
				// Validate the date when user change it
				var field = $(this).attr('name');
				// Get the bootstrapValidator instance
				form.data('bootstrapValidator')
				// Mark the field as not validated, so it'll be re-validated when the
				// user change date
					.updateStatus(field, 'NOT_VALIDATED', null)
					// Validate the field
					.validateField(field);
			}).parent().css("padding-left", "15px").css("padding-right", "15px");
		}
		// datetimepicker
		if (form.find('[data-flag="datetimepicker"]').length > 0) {
			form.find('[data-flag="datetimepicker"]').each(function(){
				var formatPattern = $(this).data("format").trim();
				if (formatPattern === "yyyy-MM-dd HH:mm:ss") {
					formatPattern = "yyyy-mm-dd hh:ii:ss";
				} else if (formatPattern === "yyyy-MM-dd HH:mm") {
					formatPattern = "yyyy-mm-dd hh:ii";
				} else {
					formatPattern = "yyyy-mm-dd hh:ii";
				}
				$(this).datetimepicker({
					autoclose: true,
					format: formatPattern,
					todayBtn: true,
					language: 'cn',
					place: function() {
						if (this.isInline) return;

					      if (!this.zIndex) {
					        var index_highest = 0;
					        $('div').each(function () {
					          var index_current = parseInt($(this).css('zIndex'), 10);
					          if (index_current > index_highest) {
					            index_highest = index_current;
					          }
					        });
					        this.zIndex = index_highest + 10;
					      }
					        this.picker.removeClass("datetimepicker-dropdown-bottom-right").removeClass("datetimepicker-dropdown-top-right");
					      var offset, top, left, containerOffset;
					      var calendarWidth = this.picker.outerWidth(),
									calendarHeight = this.picker.outerHeight(),
									visualPadding = 10,
									
									windowWidth = $(window).width(),
									windowHeight = $(window).height(),
									scrollTop = $(window).scrollTop();
									var height = this.component ? this.component.outerHeight(true) : this.element.outerHeight(false);
								var width = this.component ? this.component.outerWidth(true) : this.element.outerWidth(false);
					      if (this.container instanceof $) {
					        containerOffset = this.container.offset();
					      } else {
					        containerOffset = $(this.container).offset();
					      }

					      if (this.component) {
					        offset = this.component.offset();
					        left = offset.left;
					        if (this.pickerPosition === 'bottom-left' || this.pickerPosition === 'top-left') {
					          left += this.component.outerWidth() - this.picker.outerWidth();
					        }
					      } else {
					        offset = this.element.offset();
					        left = offset.left;
					        if (offset.left < 0)
										left -= offset.left - visualPadding;
									else if (offset.left + calendarWidth > windowWidth)
										left = windowWidth - calendarWidth - visualPadding;
					        if (this.pickerPosition === 'bottom-left' || this.pickerPosition === 'top-left') {
					          left += this.element.outerWidth() - this.picker.outerWidth();
					        }
					      }
					      
					   
									top = offset.top;
					      var bodyWidth = document.body.clientWidth || window.innerWidth;
					      if (left + 220 > bodyWidth) {
					        left = bodyWidth - 220;
					      }
						  var yorient, top_overflow, bottom_overflow;
						  top_overflow = -scrollTop + offset.top - calendarHeight;
									bottom_overflow = scrollTop + windowHeight - (offset.top + height + calendarHeight);
									if (bottom_overflow < 0)
										yorient = 'top';
									else
										yorient = 'bottom';
										if (yorient === 'bottom')
									top += height;
								else
									top -= calendarHeight + parseInt(this.picker.css('padding-top'));
//					       if (this.pickerPosition === 'top-left' || this.pickerPosition === 'top-right') {
//					         top = offset.top - this.picker.outerHeight();
//					       } else {
//					         top = offset.top + this.height;
//					       }
					        this.picker.addClass("datetimepicker-dropdown-"+yorient+"-right");
					      top = top - containerOffset.top;
					      left = left - containerOffset.left;

					      this.picker.css({
					        top:    top,
					        left:   left,
					        zIndex: this.zIndex
					      });
					}
				}).on('change', function (e) {
					// Validate the date when user change it
					var field = $(this).attr('name');
					// Get the bootstrapValidator instance
					form.data('bootstrapValidator')
					// Mark the field as not validated, so it'll be re-validated when the
					// user change date
						.updateStatus(field, 'NOT_VALIDATED', null)
						// Validate the field
						.validateField(field);
				}).parent().css("padding-left", "15px").css("padding-right", "15px")
			});
		}
		// summernote 富文本编辑器
		if (form.find('[data-flag="summernote"]').length > 0) {
			form.find('[data-flag="summernote"]').each(function(){
				var placeholder = $(this).attr("placeholder").trim();
				if (!placeholder) {
					placeholder = $(this).data("placeholder").trim();
				}
				placeholder = placeholder ? placeholder : "请输入内容";
				$(this).summernote({     
		            focus: true,   
		            lang:'zh-CN',
		            placeholder:placeholder,
		            minHeight:'100px',
		            // 重写图片上传
		            callbacks: {
		                onImageUpload: function(files) {  
		                    saveImageUpload(files,this);
		                }
		            }
		        });
			});
		}
	}

	/**
	 * 获取表单数据
	 */
	BaseForm.prototype.getFormSimpleData=function (filter) {
		var datas = {};
		var form = this.$element;
		if (form.length == 0)
			return datas;
		var elems = form.find('input[name], select[name], textarea[name]').not(filter);

		// 设置datas属性
		elems.each(function (ind, elem) {
			var el_name = elem.name, is_radio = elem.type == 'radio', is_ckbox = elem.type == 'checkbox';
			if (!el_name || ((is_radio || is_ckbox) && !elem.checked) || !elem.value)
				return;
			var assembly = function (name) {
				var res = {}, sind = name.indexOf('.');
				res[sind > -1 ? name.substring(0, sind) : name] = sind > -1 ? assembly(name.substring(sind + 1)) : '';
				return res;
			};
			var ind = el_name.indexOf('.');
			datas[ind > -1 ? el_name.substring(0, ind) : el_name] = ind > -1 ? assembly(el_name.substring(ind + 1)) : '';
		});

		// 设置datas属性值
		elems.each(function (ind, elem) {
			var el_name = elem.name, is_radio = elem.type == 'radio', is_ckbox = elem.type == 'checkbox';
			if (!el_name || ((is_radio || is_ckbox) && !elem.checked) || !elem.value)
				return;
			var old_val = eval('datas.' + el_name); // checkbox值用逗号分割
			try {
				eval('datas.' + el_name + '="' + (is_ckbox ? (old_val ? (old_val + ',') : '') : '') + $(elem).val() + '"');
			} catch (e) {
				datas[el_name] = (is_ckbox ? (old_val ? (old_val + ',') : '') : '') + $(elem).val();
			}
		});
		return datas;
	}

	/**
	 * 	表单数据回填
	 * @param json_data 回填的数据
	 */
	BaseForm.prototype.initFormData=function(json_data) {
		if (!json_data)
			return;
		var form =this.$element;
		if (form.length == 0)
			return;
		var that = this;
		form.find('input[name], select[name], textarea[name], label[name]').each(function(ind, elem) {
			var obj = $(elem), el_name = obj.attr('name'), value;
			try {
				value = eval('json_data.' + el_name);
			} catch (e) {
				value = null;
			}
			if (value != undefined && value != null && $.trim(value) != '') {
				var is_radio = elem.type == 'radio', is_ckbox = elem.type == 'checkbox';
				var is_date=$(elem).data("flag")=="datepicker"||$(elem).data("flag")=="datetimepicker"||$(elem).data("flag")=="date";
				var date_format=$(elem).data("format")||"yyyy-MM-dd";
				var is_summernote = $(elem).data("flag") == "summernote";
				var is_tagsinput = $(elem).data("flag") == "tagsinput";
				var is_autosize = $(elem).data("flag") == "autosize";
				var is_selector = elem.type.indexOf("select") > -1;
				if(is_date) {
					value=formatDate(value,date_format);
				}
				//新增bootstrap-tagsinput byx02561
				if(is_tagsinput){
					var valueArr = value.split(',');
					if(valueArr.length>0){
						for(var i=0;i<valueArr.length;i++){
							obj.tagsinput('add',valueArr[i]);
							//tagsinput的placeholder都清空 by x02561
							obj.parent().find('.bootstrap-tagsinput').children('input').attr('placeholder', '');
						}
					}
				}else if (is_radio) {
					//icheck
					if($(elem).data("flag")=="icheck"){
						$(elem).iCheck( elem.value == value?'check':'uncheck');
						form.data('bootstrapValidator').updateStatus(el_name, 'NOT_VALIDATED', null);
					}else{
						//原生radio
						elem.checked = elem.value == value;
					}
				} else if (is_ckbox) {
					//icheck
					if($(elem).data("flag")=="icheck"){
						$(elem).iCheck($.inArray(elem.value, value.split(',')) > -1?'check':'uncheck');
						form.data('bootstrapValidator').updateStatus(el_name, 'NOT_VALIDATED', null);
					}else{
						//原生checkbox
						elem.checked = $.inArray(elem.value, value.split(',')) > -1 ? true : false;
					}
				} else if (elem.tagName.toUpperCase() == 'LABEL') {
					elem.innerText = value;
				} else if (is_summernote) {
					elem.value = value;
					$(elem).summernote('code',value);
				}else if(is_autosize){ //textarea 高度自适应
					elem.value = value;
					autosize.update(obj);
				} else if (is_selector) {
					var multiple = elem.type.indexOf("multiple") > -1;
					if (multiple) {
						$(elem).val(value.split(","));
					} else {
						elem.value = value;
					}
					$(elem).trigger('change');
				} else {
					elem.value = value;
				}
			}
		});
	}

	/**
	 * 表单重置
	 */
	BaseForm.prototype.clearForm=function() {
		if (this.$element.length>0) {
			var form =this.$element;
			form.find(':input[name]:not(:radio)').val('').trigger('change');
			form.find(':radio').attr('checked', false);
			form.find(':checkbox').attr('checked', false);
			try{
				if ($.fn.iCheck) {
					form.find(':radio[data-flag]').iCheck('update');
					form.find(':checkbox[data-flag]').iCheck('update');
				}
			}catch(e){console.log(e)}
			form.find('label[name]').text('');
			if(form.data('bootstrapValidator'))
				form.data('bootstrapValidator').resetForm(true);
		} else {
			$(':input[name]:not(:radio)').val('').trigger('change');
			$(':radio').removeAttr('checked');
			$(':checkbox').removeAttr('checked');
			if ($.fn.iCheck) {
				$(':radio[data-flag]').iCheck('update');
				$(':checkbox[data-flag]').iCheck('update');
			}
			$('label[name]').text('');
		}
	}
	
	BaseForm.prototype.fillElemValue = function(elem, value) {
		if (value != undefined && value != null && $.trim(value) != '') {
			var is_radio = elem.type == 'radio', is_ckbox = elem.type == 'checkbox';
			var is_date=$(elem).data("flag")=="datepicker"||$(elem).data("flag")=="datetimepicker"||$(elem).data("flag")=="date";
			var date_format=$(elem).data("format")||"yyyy-MM-dd";
			var is_summernote = $(elem).data("flag") == "summernote";
			var is_tagsinput = $(elem).data("flag") == "tagsinput";
			var is_autosize = $(elem).data("flag") == "autosize";
			var is_selector = elem.type.indexOf("select") > -1;
			if(is_date) {
				value=formatDate(value,date_format);
			}
			//新增bootstrap-tagsinput byx02561
			if(is_tagsinput){
				var valueArr = value.split(',');
				if(valueArr.length>0){
					for(var i=0;i<valueArr.length;i++){
						obj.tagsinput('add',valueArr[i]);
						//tagsinput的placeholder都清空 by x02561
						obj.parent().find('.bootstrap-tagsinput').children('input').attr('placeholder', '');
					}
				}
			}else if (is_radio) {
				//icheck
				if($(elem).data("flag")=="icheck"){
					$(elem).iCheck( elem.value == value?'check':'uncheck');
					form.data('bootstrapValidator').updateStatus(el_name, 'NOT_VALIDATED', null);
				}else{
					//原生radio
					elem.checked = elem.value == value;
				}
			} else if (is_ckbox) {
				//icheck
				if($(elem).data("flag")=="icheck"){
					$(elem).iCheck($.inArray(elem.value, value.split(',')) > -1?'check':'uncheck');
					form.data('bootstrapValidator').updateStatus(el_name, 'NOT_VALIDATED', null);
				}else{
					//原生checkbox
					elem.checked = $.inArray(elem.value, value.split(',')) > -1 ? true : false;
				}
			} else if (elem.tagName.toUpperCase() == 'LABEL') {
				elem.innerText = value;
			} else if (is_summernote) {
				elem.value = value;
				$(elem).summernote('code',value);
			}else if(is_autosize){ //textarea 高度自适应
				elem.value = value;
				autosize.update(obj);
			} else if (is_selector) {
				var multiple = elem.type.indexOf("multiple") > -1;
				if (multiple) {
					$(elem).val(value.split(","));
				} else {
					elem.value = value;
				}
				$(elem).trigger('change');
			} else {
				elem.value = value;
			}
		}
	}
})(jQuery, window, document);


