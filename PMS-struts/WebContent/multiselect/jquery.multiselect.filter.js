/* jshint forin:true, noarg:true, noempty:true, eqeqeq:true, boss:true, undef:true, curly:true, browser:true, jquery:true */
/*
 * jQuery MultiSelect UI Widget Filtering Plugin 3.0.0
 * Copyright (c) 2012 Eric Hynds
 *
 * http://www.erichynds.com/jquery/jquery-ui-multiselect-widget/
 *
 * Depends:
 *   - jQuery UI MultiSelect widget
 *
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 *
 */
(function($) {
  var rEscape = /[\-\[\]{}()*+?.,\\\^$|#\s]/g;

  // "{{term}}" is a placeholder below for where the search term
  // would be inserted in the resulting regular expression.
  var filterRules = {
      contains: '{{term}}',
      beginsWith:  '^{{term}}',
      endsWith:  '{{term}}$',
      exactMatch:  '^{{term}}$',
      containsNumber:  '\d',
      isNumeric:  '^\d+$',
      isNonNumeric:  '^\D+$'
  };

  var headerSelector = '.ui-multiselect-header';
  var hasFilterClass = 'ui-multiselect-hasfilter';
  var filterClass = 'ui-multiselect-filter';
  var optgroupClass = 'ui-multiselect-optgroup';
  var groupLabelClass = 'ui-multiselect-grouplabel';
  var hiddenClass = 'ui-multiselect-excluded';

  //Courtesy of underscore.js
  function debounce(func, wait, immediate) {
    var timeout;
    return function() {
      var context = this, args = arguments;
      var later = function() {
        timeout = null;
        if (!immediate) {
          func.apply(context, args);
        }
      };
      var callNow = immediate && !timeout;
      clearTimeout(timeout);
      timeout = setTimeout(later, wait);
      if (callNow) {
        func.apply(context, args);
      }
    };
  }

  $.widget('ech.multiselectfilter', {

    options: {
      label: 'Filter:',                // (string) The label to show with the input
      placeholder: 'Enter keywords',   // (string) The placeholder text to show in the input
      filterRule: 'contains',          // (string) Either a named filter rule from above or a regular expression containing {{term}} as a placeholder
      searchGroups: false,             // (true | false) If true, search option group labels and show an entire group on a match.
      autoReset: false,                // (true | false) If true, clear the filter each time the widget menu is closed.
      width: null,                     // (number) Override default width set in css file (px). null will inherit
      debounceMS: 250                  // (number) Number of milleseconds to wait between running the search handler.
    },

   /**
    * Performs widget creation
    * Widget API has already set this.element and this.options for us
    *   - Find the multiselect widget.
    *   - Create the filter input
    *   - Set up event handlers
    *   - Insert in header
    * - Create text cache
    *   - Override toggleState
    */
    _create: function() {
      var opts = this.options;
      var $element = this.element;

      // get the multiselect instance
      this.instance = $element.data('ech-multiselect');

      // 适配老版本开始
      this.instance.$menu = this.instance.$menu || this.instance.menu;
      this.instance.$checkboxes = this.instance.$checkboxes || this.instance.checkboxContainer;
      this.instance.$inputs = this.instance.$inputs || this.instance.inputs;
      this.instance._setMenuHeight = function(recalc) {
          var self = this;
          if (self._savedMenuHeight && !recalc) {
             return;
          }

          var maxHeight = $(window).height();
          var optionHeight = self.options.menuHeight || '';
          var useSelectSize = false;
          var elSelectSize = 4;

          if ( /\d/.test(optionHeight) ) {
             // Deduct height of header & border/padding to find height available for checkboxes.
             var $header = self.$header.filter(':visible');
             var headerHeight = $header.outerHeight(true);
             var menuBorderPaddingHt = this.$menu.outerHeight(false) - this.$menu.height();
             var cbBorderPaddingHt = this.$checkboxes.outerHeight(false) - this.$checkboxes.height();

             optionHeight = parse2px(optionHeight, self.element, true).px;
             maxHeight = Math.min(optionHeight, maxHeight) - headerHeight - menuBorderPaddingHt - cbBorderPaddingHt;
          }
          else if (optionHeight.toLowerCase() === 'size') {
             // Overall height based on native select 'size' attribute
             useSelectSize = true;
             // Retrieves native select's size attribute or defaults to 4 (like native select).
             elSelectSize = self.element[0].size || elSelectSize;
          }

          var overflowSetting = 'hidden';
          var itemCount = 0;
          var hoverAdjust = 4;  // Adjustment for hover height included here.
          var ulHeight = hoverAdjust;
          var ulTop = -1;

          // The following determines the how many items are visible per the menuHeight option.
          //   If the visible height calculation exceeds the calculated maximum height or if the number
          //   of item heights summed equal or exceed the native select size attribute, the loop is aborted.
          // If the loop is aborted, this means that the menu must be scrolled to see all the items.
          self.$checkboxes.find('li:not(.ui-multiselect-optgroup),a').filter(':visible').each( function() {
            if (ulTop < 0) {
               ulTop = this.offsetTop;
            }
            ulHeight =  this.offsetTop + this.offsetHeight - ulTop + hoverAdjust;
            if (useSelectSize && ++itemCount >= elSelectSize || ulHeight > maxHeight) {
              overflowSetting = 'auto';
              if (!useSelectSize) {
                ulHeight = maxHeight;
              }
              return false;
            }
          });

          // We actually only set the height of the checkboxes as the outer menu container is height:auto.
          // The _savedMenuHeight value below can be compared to optionHeight as an accuracy check.
          self.$checkboxes.css('overflow', overflowSetting).height(ulHeight);
          self._savedMenuHeight = this.$menu.outerHeight(false);
        };
        var _toggleChecked = this.instance._toggleChecked;
        this.instance._toggleChecked = function(flag, group, filteredInputs) {
        	var self = this;
            var $element = self.element;
            var $inputs = (group && group.length) ? group : self.$inputs;

            if (filteredInputs) {
               $inputs = self._isOpen
                           ? $inputs.closest('li').not('.ui-multiselect-excluded').find('input').not(':disabled')
                           : $inputs.not(':disabled');
            }

            // toggle state on inputs
            $inputs.each(self._toggleState('checked', flag));

            // Give the first input focus
            $inputs.eq(0).focus();

            // update button text
            self.update();

            // Create a plain object of the values that actually changed
            var inputValues = {};
            $inputs.each( function() {
              inputValues[ this.value ] = true;
            });

            // toggle state on original option tags
            $element.find('option')
                    .each( function() {
                      if (!this.disabled && inputValues[this.value]) {
                        self._toggleState('selected', flag).call(this);
                      }
                    });

            // trigger the change event on the select
            if ($inputs.length) {
              $element.trigger("change");
            }
        }
      // 适配老版本结束
      
      // store header; add filter class so the close/check all/uncheck all links can be positioned correctly
      this.$header = this.instance.$menu.find(headerSelector).addClass(hasFilterClass);

      // wrapper $element
      this.$input = $(document.createElement('input'))
        .attr({
          placeholder: opts.placeholder,
          type: "search"
        })
        .css({  width: (typeof opts.width === 'string')
                       ? this.instance._parse2px(opts.width, this.$header).px + 'px'
                       : (/\d/.test(opts.width) ? opts.width + 'px' : null)
             });
      this._bindInputEvents();
      // automatically reset the widget on close?
      if (this.options.autoReset) {
        $element.on('multiselectbeforeclose', $.proxy(this._reset, this));
      }        

      //var $label = $(document.createElement('label')).text(opts.label).append(this.$input).addClass('ui-multiselect-filter-label');
      var $label = $(document.createElement('label')).text(opts.label).addClass('ui-multiselect-filter-label');
      this.$wrapper = $(document.createElement('div'))
                                .addClass(filterClass)
                                .append($label)
                                .append(this.$input)
                                .prependTo(this.$header);

      // If menu already opened, have to reset menu height since
      // addition of the filter input changes the header height calc.
      if (!!this.instance._isOpen) {
         this.instance._setMenuHeight(true);
      }

      // cache input values for searching
      this.updateCache();

      // Change the normal _toggleChecked fxn behavior so that when checkAll/uncheckAll
      // is fired, only the currently displayed filtered inputs are checked if filter entered.
      var instance = this.instance;
      var filter = this.$input[0];
      instance._oldToggleChecked = instance._toggleChecked;
      instance._toggleChecked = function(flag, group) {
         instance._oldToggleChecked(flag, group, !!filter.value);
      };
    },

    /**
     * Binds keyboard events to the input
     * This is where special behavior like ALT-R for reset is bound
     */
    _bindInputEvents: function() {
      this.$input.on({
//        keydown: function(e) {
//          // prevent the enter key from submitting the form / closing the widget
//          if(e.which === 13)
//            e.preventDefault();
//          else if(e.which === 27) {
//            $element.multiselect('close');
//            e.preventDefault();
//          }
//          else if(e.which === 9 && e.shiftKey) {
//            $element.multiselect('close');
//            e.preventDefault();
//          }
//          else if(e.altKey) {
//            switch(e.which) {
//              case 82:
//                e.preventDefault();
//                $(this).val('').trigger('input', '');
//                break;
//              case 65:
//                $element.multiselect('checkAll');
//                break;
//              case 85:
//                $element.multiselect('uncheckAll');
//                break;
//              case 70:
//                $element.multiselect('flipAll');
//                break;
//              case 76:
//                $element.multiselect('instance').$labels.first().trigger("mouseenter");
//                break;
//            }
//          }
//        },
        input: $.proxy(debounce(this._handler, this.options.debounceMS), this),
        search: $.proxy(this._handler, this)
      });
    },

   /**
    * Handles searches as text is entered in the filter box.
    * Uses a text cache to speed up searching.
    * Debouncing is done to limit how often this is ran.
    * Alternate filter rules can be used.
    * Option group labels may be searched, also.
    * @param (object) event object from original event.
    */
    _handler: function(e) {
      var term = this.$input[0].value.toLowerCase().replace(/^\s+|\s+$/g,'');
      var filterRule = this.options.filterRule || 'contains';
      var regex = new RegExp( ( filterRules[filterRule] || filterRule ).replace('{{term}}', term.replace(rEscape, "\\$&")), 'i');
      var searchGroups = !!this.options.searchGroups;
      var $checkboxes = this.instance.$checkboxes;
      var cache = this.cache;   // Cached text() object

      this.$rows.toggleClass(hiddenClass, !!term);
      var filteredInputs = $checkboxes.children().map(function(x) {
        var elem = this;
        var $groupItems = $(elem);
        var groupShown = false;

         // Account for optgroups
         // If we are searching in option group labels and we match an optgroup label,
         // then show all its children and return all its inputs also.
        if (elem.classList.contains(optgroupClass)) {
          var $groupItems = $groupItems.find('li');
          if (searchGroups && regex.test( cache[x] ) ) {
             elem.classList.remove(hiddenClass);
             $groupItems.removeClass(hiddenClass);
             return $groupItems.find('input').get();
          }
        }

        return $groupItems.map(function(y) {
          if ( regex.test( cache[x + '.' + y] ) ) {
            // Show the opt group heading if needed
            if (!groupShown) {
               elem.classList.remove(hiddenClass);
               groupShown = true;
            }
            this.classList.remove(hiddenClass);
            return this.getElementsByTagName('input')[0];
          }
          return null;
        });

      });
      if (term) {
         this._trigger('filter', e, filteredInputs);
      }
      if (!this.instance.options.listbox && this.instance._isOpen) {
         this.instance._setMenuHeight(true);
         this.instance.position();
      }
      return;
    },

    _reset: function () {
      this.$input.val('')
      var event = document.createEvent('Event');
      event.initEvent('reset', true, true);
      this.$input.get(0).dispatchEvent(event)
      this._handler(event)
    },

   /**
    * Creates a text cache object from the widget options' text.
    * @param (boolean) alsoRefresh causes the displayed search results to refresh.
    */
    updateCache: function(alsoRefresh) {
      var cache = {};  // keys are like 0, 0.1, 1, 1.0, 1.1 etc.
      this.instance.$checkboxes.children().each(function(x) {
         var $element = $(this);
         // Account for optgroups
         if (this.classList.contains(optgroupClass)) {
            // Single number keys are the option labels
            cache[x] = this.getElementsByClassName(groupLabelClass)[0].textContent;
            $element = $element.find('li');
         }
         $element.each(function(y) {
            cache[x + '.' + y] = this.textContent;
         });
      });
      this.cache = cache;
      this.$rows = this.instance.$checkboxes.find('li');
      if (!!alsoRefresh) {
         this._handler();
      }
    },

   /**
    * Returns the input wrapper div
    */
    widget: function() {
      return this.$wrapper;
    },

   /**
    * Destroys this widget
    */
    destroy: function() {
      $.Widget.prototype.destroy.call(this);
      this.$input.val('').trigger("keyup").off('keydown input search');
      this.instance.$menu.find(headerSelector).removeClass(hasFilterClass);
      this.$wrapper.remove();
    }
  });

})(jQuery);
