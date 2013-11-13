define([
    'jquery',
  'underscore',
  'backbone',
  'text!../templates/karyawanList.html',
  'text!../templates/karyawanForm.html',
    '../models/karyawan'
], function($, _, Backbone, listTemplate,formTemplate,Model){

  var KaryawanView = Backbone.View.extend({
    el: $('#container'),
   events: {
          "click .save"   : "save",
          "click .cancel"   : "cancel"
      },
    initialize:function(options){
        console.log('initialize karyawan view ');
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
            that.karyawanForm.find('.save').button('reset');

        });
        this.listenTo(this.model,'sync', function(e) {
            console.log('sukses');
            if(!that.options.id)
                   this.model.set(new Model().toJSON());
            that.render('form',function(){
                var alertInfo = $('#alertInfo').clone();
                $('span',alertInfo).html('Sukses menyimpan karyawan');
                alertInfo.show().appendTo('.formMessage');
                that.karyawanForm.find('.save').button('reset');
            });

        });

    }, 
    save:function() {
            this.karyawanForm = this.$el.find('#karyawanForm');
            //console.log(this.userForm.serializeObject());
            if(this.karyawanForm.valid()) {
                this.model.set(this.karyawanForm.serializeObject());
                this.karyawanForm.find('.save').button('loading');
                this.model.save();
            }
        },
        cancel:function() {
            app.router.navigate('karyawan',true);
        },
    render: function(page,cb){
      console.log('render karyawan view');
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
                var karyawan = new Model({id:id});
                karyawan.fetch({
                    success: function (karyawan) {
                        var data = karyawan.toJSON();
                        that.model.set (data);
                        var compiledTemplate = _.template( template, data );
                        that.$el.html( compiledTemplate );
                        that.karyawanForm = that.$el.find('#karyawanForm');

                        if(cb) cb();

                    }
                });
            }
            else {
                console.log(this.model.toJSON());
                var compiledTemplate = _.template( template, this.model.toJSON() );
                // Append our compiled template to this Views "el"
                this.$el.html( compiledTemplate );

                this.karyawanForm = this.$el.find('#karyawanForm');

                this.karyawanForm.validate({
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
                        username: "required",
                        nama: "required",
                        jenisKelamin : "required",
                        noIndentitas : "required",
                        bank : "required",
                        noRekening :"required",
                        cabang :"required"
                    },
                    messages: {

                    }
                });

                if(cb) cb();
            }
    }
  });
  // Our module now returns our view
  return  KaryawanView;
});