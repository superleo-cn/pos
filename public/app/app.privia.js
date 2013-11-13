define([ 'jquery',
  'underscore',
  'backbone',
  'text!./templates/sidebarsMenuSection.html','antiscroll','jquery.actual'], function($, _, Backbone,sidebarsTemplate) {

    console.log('Initialize App'); 
    
  // Provide a global location to place configuration settings and module
  // creation.
  var app = {
  // The root path to run the application.
  root: '/'
};
var compiledTemplate = _.template(sidebarsTemplate, {'akses':arrAkses} );

$('#sidebarsMenu').html(compiledTemplate);

    //* accordion change actions
    $('#side_accordion').on('hidden shown', function () {
      gebo_sidebar.make_active();
            gebo_sidebar.update_scroll();
    });
    //* resize elements on window resize
    var lastWindowHeight = $(window).height();
    var lastWindowWidth = $(window).width();
    $(window).on("debouncedresize",function() {
      if($(window).height()!=lastWindowHeight || $(window).width()!=lastWindowWidth){
        lastWindowHeight = $(window).height();
        lastWindowWidth = $(window).width();
        gebo_sidebar.update_scroll();
        if(!is_touch_device()){
                    $('.sidebar_switch').qtip('hide');
                }
      }
    });
    //* tooltips
    /*gebo_tips.init();
        if(!is_touch_device()){
        //* popovers
            gebo_popOver.init();
        }*/
        var gebo_sidebar = {
        init: function() {
      // sidebar onload state
      if($(window).width() > 979){
                if(!$('body').hasClass('sidebar_hidden')) {
                   
                } else {
                    $('.sidebar_switch').toggleClass('on_switch off_switch').attr('title','Show Sidebar');
                }
            } else {
                $('body').addClass('sidebar_hidden');
                $('.sidebar_switch').removeClass('on_switch').addClass('off_switch');
            }

      gebo_sidebar.info_box();
      //* sidebar visibility switch
            $('.sidebar_switch').click(function(){
                $('.sidebar_switch').removeClass('on_switch off_switch');
                if( $('body').hasClass('sidebar_hidden') ) {
                 
                    $('body').removeClass('sidebar_hidden');
                    $('.sidebar_switch').addClass('on_switch').show();
                    $('.sidebar_switch').attr( 'title', "Hide Sidebar" );
                } else {
                    $('body').addClass('sidebar_hidden');
                    $('.sidebar_switch').addClass('off_switch');
                    $('.sidebar_switch').attr( 'title', "Show Sidebar" );
                }
        gebo_sidebar.info_box();
        gebo_sidebar.update_scroll();
        $(window).resize();
            });
      //* prevent accordion link click
            $('.sidebar .accordion-toggle').click(function(e){e.preventDefault()});
        },
    info_box: function(){
      var s_box = $('.sidebar_info');
      var s_box_height = s_box.actual('height');
      s_box.css({
        'height'        : s_box_height
      });
      $('.push').height(s_box_height);
      $('.sidebar_inner').css({
        'margin-bottom' : '-'+s_box_height+'px',
        'min-height'    : '100%'
      });
        },
    make_active: function() {
      var thisAccordion = $('#side_accordion');
      thisAccordion.find('.accordion-heading').removeClass('sdb_h_active');
      var thisHeading = thisAccordion.find('.accordion-body.in').prev('.accordion-heading');
      if(thisHeading.length) {
        thisHeading.addClass('sdb_h_active');
      }
    },
        make_scroll: function() {
            antiScroll = $('.antiScroll').antiscroll().data('antiscroll');
        },
        update_scroll: function() {
      if($('.antiScroll').length) {
        if( $(window).width() > 979 ){
          $('.antiscroll-inner,.antiscroll-content').height($(window).height() - 40);
        } else {
          $('.antiscroll-inner,.antiscroll-content').height('400px');
        }
        antiScroll.refresh();
      }
        }
    };
    //* sidebar
        gebo_sidebar.init();
    gebo_sidebar.make_active();
    //* breadcrumbs
        /*gebo_crumbs.init();
    //* pre block prettify
    if(typeof prettyPrint == 'function') {
      prettyPrint();
    }
    //* external links
    gebo_external_links.init();
    //* accordion icons
    gebo_acc_icons.init();
    //* colorbox single
    gebo_colorbox_single.init();
    //* main menu mouseover
    gebo_nav_mouseover.init();
    //* top submenu
    gebo_submenu.init();
    */
    
    gebo_sidebar.make_scroll();
    gebo_sidebar.update_scroll();
    
    //* style switcher
    /*gebo_style_sw.init();
    */
    //* fix for dropdown menu (touch devices)
    $('body').on('touchstart.dropdown', '.dropdown-menu', function (e) { e.stopPropagation(); });


if (!Array.prototype.reduce)
{
  Array.prototype.reduce = function(fun /*, initial*/)
  {
    var len = this.length;
    if (typeof fun != "function")
      throw new TypeError();

    // no value to return if no initial value and an empty array
    if (len == 0 && arguments.length == 1)
      throw new TypeError();

    var i = 0;
    if (arguments.length >= 2)
    {
      var rv = arguments[1];
    }
    else
    {
      do
      {
        if (i in this)
        {
          rv = this[i++];
          break;
        }

        // if array contains no values, no initial value to return
        if (++i >= len)
          throw new TypeError();
      }
      while (true);
    }

    for (; i < len; i++)
    {
      if (i in this)
        rv = fun.call(null, rv, this[i], i, this);
    }

    return rv;
  };
}

Number.prototype.formatPrice = function() {
  var p = this.toFixed(2).split(".");
  return "Rp. " + p[0].split("").reverse().reduce(function(acc, num, i, orig) {
    return  num + (i && !(i % 3) ? "," : "") + acc;
  }, "") ;
     // return this+"".replace(/(\d)(?=(\d{3})+)/g, "$1,");
   };

   

var delay = (function(){
  var timer = 0;
  return function(callback, ms){
    clearTimeout (timer);
    timer = setTimeout(callback, ms);
  };
})();

app.delay = delay;

   return _.extend(app,{});

 });

