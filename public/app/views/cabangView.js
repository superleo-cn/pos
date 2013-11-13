define([
  'jquery',
  'underscore',
  'backbone',
  'text!../templates/reportTransaction.html',
  'text!../templates/cabangForm.html',
    '../models/cabang'
], function($, _, Backbone, listTemplate,formTemplate,Model){

  var CabangView = Backbone.View.extend({
    el: $('#container'),
     events: {
          "click .save"   : "save",
          "click .cancel"   : "cancel"
      },
      initialize:function(options){
            console.log('initialize cabang view ');
            this.$el.undelegate();

            var that = this;
            this.model = new Model();

            this.listenTo(this.model,'error', function(model,e) {
                var err;
                try {
                    var response = JSON.parse(e.responseText);
                    err = response.err;
                }catch(ex) {
                    err =  e.responseText;
                }
                var alertError = $('#alertError').clone();
                $('span',alertError).html('Terjadi masalah : '+err);
                alertError.show().appendTo('.formMessage');
                that.cabangForm.find('.save').button('reset');

            });
            this.listenTo(this.model,'sync', function(e) {
                console.log('sukses');
                if(!that.options.id)
                   this.model.set(new Model().toJSON());
                that.render('form',function(){
                    var alertInfo = $('#alertInfo').clone();
                    $('span',alertInfo).html('Sukses menyimpan cabang');
                    alertInfo.show().appendTo('.formMessage');
                    that.cabangForm.find('.save').button('reset');
                });

            });

        },
       save:function() {
            this.cabangForm = this.$el.find('#cabangForm');
            console.log(this.cabangForm.serializeObject());
            if(this.cabangForm.valid()) {
                this.model.set(this.cabangForm.serializeObject());
                this.cabangForm.find('.save').button('loading');
                this.model.save();
            }
        },
        cancel:function() {
            app.router.navigate('cabang',true);
        },
    render: function(page,cb){
      console.log('render cabang view');
            var that = this;
            var id = this.options.id;
            var data = {};
            var template;

            if(page=='form') {
                template=formTemplate;
            }
            else
                template=listTemplate;

            if(id) {
                template=formTemplate;

                console.log('load '+id);
                var cabang = new Model({id:id});
                cabang.fetch({
                    success: function (cabang) {
                        var data = cabang.toJSON();
                        that.model.set (data);
                        var compiledTemplate = _.template( template, data );
                        that.$el.html( compiledTemplate );
                        that.cabangForm = that.$el.find('#cabangForm');

                        if(cb) cb();

                    }
                });
            }
            else {
                console.log(this.model.toJSON());
                var compiledTemplate = _.template( template, this.model.toJSON() );
                // Append our compiled template to this Views "el"
                this.$el.html( compiledTemplate );

                this.cabangForm = this.$el.find('#cabangForm');

                this.cabangForm.validate({
                    onkeyup: false,
                    errorClass: 'error',
                    validClass: 'valid',
                    highlight: function(element) {
                        $(element).closest('div').addClass("f_error");
                    },
                    unhighlight: function(element) {
                        $(element).closest('div').removeClass("f_error");
                    },
                    errorPlacement: function(error, element) {
                        $(element).closest('div').append(error);
                    },
                    rules: {
                        kodeCabang: "required",
                        namaCabang: "required"
                    },
                    messages: {

                    }
                });

                if(cb) cb();
            }
    }
  });
  // Our module now returns our view
  return  CabangView;
});