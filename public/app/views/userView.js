define([
  'jquery',
  'underscore',
  'backbone',
  'text!../templates/userList.html',
  'text!../templates/userForm.html',
  'text!../templates/userEditForm.html',
  '../models/user',
  '../models/changePassword'
  ], function($, _, Backbone, listTemplate,formTemplate,formEditTemplate,Model,ChangePasswordModel){

    var UserView = Backbone.View.extend({
      el: $('#container'),
      events: {
        "click .save"   : "save",
        "click .cancel"   : "cancel",
        "click #changePassword"   : "changePassword"
      },
      initialize:function(options){
        console.log('initialize user view ');
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
        that.userForm.find('.save').button('reset');

      });
        this.listenTo(this.model,'sync', function(e) {
          console.log('sukses');
          that.render('form',function(){
            var alertInfo = $('#alertInfo').clone();
            $('span',alertInfo).html('Sukses menyimpan user');
            alertInfo.show().appendTo('.formMessage');
            that.userForm.find('.save').button('reset');
          });
          
        });

      },
      save:function() {
        this.userForm = this.$el.find('#userForm');
       
        if(this.userForm.valid()) {   
          var updatedModel = this.userForm.serializeObject();
          
          this.model.set(updatedModel);
          console.log(!updatedModel.akses);
          if(!updatedModel.akses)
            this.model.set('akses',[]);

          this.userForm.find('.save').button('loading');
          this.model.save();
        }
      },
      cancel:function() {
        app.router.navigate('/users',{trigger:true});
      },
      changePassword:function() {

        this.changePasswordForm = this.$el.find('#changePasswordForm');
        if(this.changePasswordForm.valid()) {
          this.changePasswordModel.set('password',this.$el.find('#password').val());
          this.changePasswordModel.save();
          $('#changePasswordModal').modal('hide');
      }
    },

    render: function(page,cb){
      console.log('render user view');
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
        var user = new Model({id:id});
        this.changePasswordModel = new ChangePasswordModel({id:id});
        user.fetch({
          success: function (user) {
            var data = user.toJSON();
            that.model.set (data);
            var compiledTemplate = _.template( template, data );
            that.$el.html( compiledTemplate );
            that.userForm = that.$el.find('#userForm');
        
        //console.log(that.userForm.serializeObject());
        that.userForm.validate({
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
            username: "required"
          },
          messages: {
            username: "Please enter your username"
          }
        });

        that.changePasswordForm = that.$el.find('#changePasswordForm');
        
        that.changePasswordForm.validate({
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
            password : {
              required :true,
              minlength : 1
            },
            confirmPassword : {
              required :true,
              minlength : 1,
              equalTo : "input[name=password]"
            }
          }
        });

        if(cb) cb();

      }
    });
}
else {
 var compiledTemplate = _.template( template, this.model.toJSON() );
        // Append our compiled template to this Views "el"
        this.$el.html( compiledTemplate );

        this.userForm = this.$el.find('#userForm');
       
        this.userForm.validate({
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
            password : {
              required :true,
              minlength : 3
            },
            confirmPassword : {
              required :true,
              minlength : 3,
              equalTo : "input[name=password]"
            }
          },
          messages: {
            username: "Please enter your username"
          }
        });

        if(cb) cb();
      }
    }
  });
  // Our module now returns our view
  return  UserView;
});