define([
    'jquery',
    'underscore',
    'backbone',
    'text!../templates/modalChangePassword.html',
    '../models/customer'
], function($, _, Backbone, formTemplate,Model){

    var ModalView = Backbone.View.extend({
        el: $('#modalContainer'),
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

            template=formTemplate;

            console.log(this.model.toJSON());
            var compiledTemplate = _.template( template, this.model.toJSON() );
            // Append our compiled template to this Views "el"
            this.$el.html( compiledTemplate );

            // this.$el.css('width',600);
            this.$el.find('#modalView').modal();

            if(cb) cb();
            
        }
    });
    // Our module now returns our view
    return  ModalView;
});