define([
  'jquery',
  'underscore',
  'backbone',
  'text!/app/templates/master/itemForm.html',
  'text!/app/templates/master/itemUploadForm.html',
  'text!/app/templates/master/itemList.html',
  '../../models/item'
], function($, _, Backbone, formTemplate,uploadFormTemplate,listTemplate,Model){

  var ItemView = Backbone.View.extend({
    el: $('#container'),
     events: {
          "click .save"   : "save",
          "click .cancel"   : "cancel",
          "click #searchItem"   : "searchItem",
          "click #clearItem"   : "clearItem"
      },
      clearItem:function() {
            this.$el.find('#outlet').val('ALL');
        }, 
        searchItem:function() {
            var page =this.page;

            if(page=='list') {
                var outlet = this.$el.find('#outlet').val();

                this.oTable.fnMultiFilter({"sn":outlet});

            }
        },
      initialize:function(options){
            console.log('initialize master item view ');
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
                that.itemForm.find('.save').button('reset');

            });
            this.listenTo(this.model,'sync', function(e) {
                console.log('sukses');
                if(!that.options.id)
                   this.model.set(new Model().toJSON());
                that.render('form',function(){
                    var alertInfo = $('#alertInfo').clone();
                    $('span',alertInfo).html('Sukses menyimpan item');
                    alertInfo.show().appendTo('.formMessage');
                    that.itemForm.find('.save').button('reset');
                });

            });

        },
       save:function(e) {
            that=this;
            this.itemForm = this.$el.find('#itemForm');
            console.log(this.itemForm.serializeObject());
            if(this.itemForm.valid()) {

                this.model.readFile(this.$('form :file')[0].files[0], function(){
                    that.itemForm.find('.save').button('reset');
                }, function(){
                    that.render('list');
                });
                this.itemForm.find('.save').button('loading');
            
            }
        },
        cancel:function() {
            app.router.navigate('master/item/list',true);
        },
    render: function(page,cb){
      console.log('render item view');
            var that = this;
            that.page=page;
            var id = this.options.id;
            var data = {};
            var template;


            if(page=='form') {
                template=formTemplate;
            }
            else if(page=='upload') {
                template=uploadFormTemplate;
            }
            else if(page=='list')
                template=listTemplate;

            if(id) {
                template=formTemplate;

                console.log('load '+id);
                var item = new Model({id:id});
                item.fetch({
                    success: function (item) {
                        var data = item.toJSON();
                        that.model.set (data);
                        var compiledTemplate = _.template( template, data );
                        that.$el.html( compiledTemplate );
                        that.itemForm = that.$el.find('#itemForm');

                        if(cb) cb();

                    }
                });
            }
            else {
                console.log(this.model.toJSON());
                var compiledTemplate = _.template( template, this.model.toJSON() );
                // Append our compiled template to this Views "el"
                this.$el.html( compiledTemplate );

               if(page=='list') {
                    that.oTable = that.$el.find('#privia_grid').dataTable( {
                    "bProcessing": true,
                    "bServerSide": true,
                    "iDisplayLength" : 25,
                    "sDom": "<'row'<'span6'<'dt_actions'>l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                    "sPaginationType": "bootstrap_alt",
                    "sAjaxDataProp" : "recordList",
                    "aoColumnDefs": [
                        {
                            "mRender": function ( data, type, row ) {
                                
                                return row.name+' / '+row.nameZh;
                            },
                            "aTargets": [1 ]
                        },
                        {
                            "mRender": function ( data, type, row ) {
                                return (typeof row.barCode!='undefined')?row.barCode:'';
                            },
                            "aTargets": [2 ]
                        }
                    ],
                    "aoColumns": [
                        { "mData": "sn",  "bSortable": false  },
                        { "mData": "name",  "bSortable": false  },
                        { "mData": "nameZh",  "bSortable": false  },
                        { "mData": "costPrice",  "bSortable": false  },
                        { "mData": "retailPrice",  "bSortable": false  },
                        { "mData": "shop.name",  "bSortable": false  }
                    ],
                    "sAjaxSource": "/masterItem/search"
                } );

                var outlet = that.$el.find('#outlet');
                if(outlet!=null) {
                //  outlet.select2({data:{}});
                var format=function format(item) { return item.name; };

                $.get('/reports/shops',function(response){
                    response.recordList.unshift({id:'ALL',name:'ALL'});

                    var record = $.map(response.recordList, function(obj){
                        return {id:obj.name,name:obj.name};
                    });
                    var shopStr ='';
                    record.forEach(function(entry) {
                       shopStr+='<option>'+entry.name+'</option>';
                    });
                    outlet.replaceWith('<select name=outlet id=outlet style="width:180px">'+shopStr+'</select>');
                });
            }


            }

                if(cb) cb();
            }
    }
  });
  // Our module now returns our view
  return  ItemView;
});