define([
  'jquery',
  'underscore',
  'backbone',
  'text!../templates/analisa.html'
], function($, _, Backbone, analisaTemplate){

  var AnalisaView = Backbone.View.extend({
    el: $('#container'),
    render: function(){
      // Using Underscore we can compile our template with data
      var data = {};
      var compiledTemplate = _.template( analisaTemplate, data );
      // Append our compiled template to this Views "el"
      this.$el.html( compiledTemplate );
    }
  });
  // Our module now returns our view
  return  AnalisaView;
});