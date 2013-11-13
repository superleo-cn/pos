define([
    'jquery',
    'underscore',
    'backbone',
    'text!../templates/customerList.html',
    'text!../templates/customerForm.html',
    'text!../templates/customerForm.html',
    '../models/customer'
], function($, _, Backbone, listTemplate,formTemplate,formEditTemplate,Model){

    var CustomerView = Backbone.View.extend({
        el: $('#container'),
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
                if(!that.options.id)
                   this.model.set(new Model().toJSON());
                that.render('form',function(){
                    var alertInfo = $('#alertInfo').clone();
                    $('span',alertInfo).html('Sukses menyimpan customer');
                    alertInfo.show().appendTo('.formMessage');
                    that.customerForm.find('.save').button('reset');
                });

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
            app.router.navigate('customer',true);
        },
        render: function(page,cb){
            console.log('render customer view');
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
                template=formEditTemplate;

                console.log('load '+id);
                var customer = new Model({id:id});
                customer.fetch({
                    success: function (customer) {
                        var data = customer.toJSON();
                        that.model.set (data);
                        var compiledTemplate = _.template( template, data );
                        that.$el.html( compiledTemplate );
                        that.customerForm = that.$el.find('#customerForm');

                        if(cb) cb();

                    }
                });
            }
            else {
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
                        namaCustomer: "required",
                        noIdentitas : "required",
                        gender : "required",
                        "telepon[hp1]":"required"
                    },
                    messages: {

                    }
                });

                if(cb) cb();
            }
        }
    });
    // Our module now returns our view
    return  CustomerView;
});