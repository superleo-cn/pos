define([
    'jquery',
    'underscore',
    'backbone',
    'text!../templates/customerList.html',
    'text!../templates/modalCustomer.html',
    'text!../templates/modalCustomer.html',
    '../models/customer'
], function($, _, Backbone, listTemplate,formTemplate,formEditTemplate,Model){

    var ModalView = Backbone.View.extend({
        el: $('#modalView'),
        events: {
            "click .save"   : "save",
            "click .cancel"   : "cancel"
        },
        initialize:function(options){
            console.log('initialize customer view ');
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
                that.customerForm.find('.save').button('reset');

            });
            this.listenTo(this.model,'sync', function(e) {
                console.log('sukses');
                that.$el.modal('hide');

            });

        },
        save:function() {
            this.customerForm = this.$el.find('#customerForm');
            //console.log(this.userForm.serializeObject());
            if(this.customerForm.valid()) {
                this.model.set(this.customerForm.serializeObject());
                this.customerForm.find('.save').button('loading');
                this.model.save();
            }
        },
        cancel:function() {
            this.$el.modal('hide');
        },
        render: function(page,cb){
            console.log('render customer view');
            var that = this;
            var id = this.options.id;
            var data = {};
            var template;

            if(page=='customer') {
                template=formTemplate;
            }
            else
                template=listTemplate;

                console.log(this.model.toJSON());
                var compiledTemplate = _.template( template, this.model.toJSON() );
                // Append our compiled template to this Views "el"
                this.$el.html( compiledTemplate );

                this.customerForm = this.$el.find('#customerForm');

                this.customerForm.validate({
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
                        namaModal: "required",
                        gender : "required",
                        "telepon[hp1]":"required"
                    },
                    messages: {

                    }
                });

                this.$el.css('width',600);
                this.$el.modal();

                if(cb) cb();
            
        }
    });
    // Our module now returns our view
    return  ModalView;
});