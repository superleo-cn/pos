define([
    'jquery',
    'underscore',
    'backbone',
    'text!../templates/reportTransaction.html',
    'text!../templates/reportCashierClosing.html',
    'text!../templates/reportLoginAudit.html',
    'text!../templates/reportPL.html',
    '../models/report'
], function($, _, Backbone, reportTransaction,reportCashierClosing,reportLoginAudit,reportPL,Model){

    var ReportView = Backbone.View.extend({
        el: $('#container'),
        events: {
            "click .save"   : "save",
            "click .saveKontrak"   : "save",
            "click #exportTransaction" :"exportTransaction",
            "click #tambahCustomer"   : "tambahCustomer"
        },
        exportTransaction:function() {
          window.open("/reports/exportTransaction");
        },
        initialize:function(options){
            console.log('initialize quote view ');
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
                that.quoteForm.find('.save').button('reset');


            });
            this.listenTo(this.model,'sync', function(e) {
                console.log('sukses');
                if(!that.options.id)
                    this.model.set(new Model().toJSON());
                that.render('form',function(){
                    var alertInfo = $('#alertInfo').clone();
                    $('span',alertInfo).html('Sukses menyimpan Proposal');
                    alertInfo.show().appendTo('.formMessage');
                    that.quoteForm.find('.save').button('reset');
                });

            });

        },
        save:function(e) {
            this.quoteForm = this.$el.find('#quoteForm');
            console.log(this.quoteForm.serializeObject());

            if(this.quoteForm.valid()) {
                var status = e.target.textContent;

                this.model.set(this.quoteForm.serializeObject());
                this.model.set('perusahaanKredit',this.perusahaanKredit);
                this.model.set('customer',this.customer);
                this.model.set('status',status);
                this.quoteForm.find('.save').button('loading');
                this.model.save();
            }
        },
        tambahCustomer:function(e) {
            app.router.addCustomerModal();
        },
        cancel:function() {
            app.router.navigate('/quote',{trigger:true});
        },
        render: function(page,cb){
            console.log('render quote view');
            var that = this;
            var id = this.options.id;
            var data = {};
            var template;

            if(page=='reportTransaction')
                template=reportTransaction;
            else  if(page=='reportCashierClosing')
                template=reportCashierClosing;
            else  if(page=='reportLoginAudit')
                template=reportLoginAudit;
            else
                template=reportPL;

            console.log(this.model.toJSON());
            var compiledTemplate = _.template( template, this.model.toJSON() );
            // Append our compiled template to this Views "el"
            this.$el.html( compiledTemplate );

            this.quoteForm = this.$el.find('#reportForm');
            // console.log(this.$el.find('#quoteForm'));
            that.initializeForm(page);

            if(cb) cb();
        },
        initializeForm:function(page){
            that=this;
            if(page=='reportTransaction') {
                that.$el.find('#privia_grid').dataTable( {
                    "bProcessing": true,
                    "bServerSide": true,
                    "sDom": "<'row'<'span6'<'dt_actions'>l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                    "sPaginationType": "bootstrap_alt",
                    "sAjaxDataProp" : "recordList",
                    "aoColumnDefs": [
                        {
                            "mRender": function ( data, type, row ) {

                                return '<a href="#/cabang/edit/'+data+'">Ubah</a>';
                            },
                            "aTargets": [4 ]
                        }
                    ],
                    "aoColumns": [
                        { "mData": "S/N" },
                        { "mData": "Item" },
                        { "mData": "Shop" },
                        { "mData": "Quantity" },
                        { "mData": "Price" }
                    ],
                    "sAjaxSource": "/reports/transaction"
                } );
            }
            else if(page=='reportCashierClosing') {
                that.$el.find('#privia_grid').dataTable( {
                    "bProcessing": true,
                    "bServerSide": true,
                    "sDom": "<'row'<'span6'<'dt_actions'>l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                    "sPaginationType": "bootstrap_alt",
                    "sAjaxDataProp" : "recordList",
                    "aoColumnDefs": [
                        {
                            "mRender": function ( data, type, row ) {

                                return '<a href="#/cabang/edit/'+data+'">Ubah</a>';
                            },
                            "aTargets": [4 ]
                        }
                    ],
                    "aoColumns": [
                        { "mData": "S/N" },
                        { "mData": "Item" },
                        { "mData": "Shop" },
                        { "mData": "Quantity" },
                        { "mData": "Price" }
                    ],
                    "sAjaxSource": "/reports/transaction"
                } );
            }
            else if(page=='reportLoginAudit') {
                that.$el.find('#privia_grid').dataTable( {
                    "bProcessing": true,
                    "bServerSide": true,
                    "sDom": "<'row'<'span6'<'dt_actions'>l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                    "sPaginationType": "bootstrap_alt",
                    "sAjaxDataProp" : "recordList",
                    "aoColumnDefs": [
                        {
                            "mRender": function ( data, type, row ) {

                                return '<a href="#/cabang/edit/'+data+'">Ubah</a>';
                            },
                            "aTargets": [4 ]
                        }
                    ],
                    "aoColumns": [
                        { "mData": "S/N" },
                        { "mData": "Item" },
                        { "mData": "Shop" },
                        { "mData": "Quantity" },
                        { "mData": "Price" }
                    ],
                    "sAjaxSource": "/reports/transaction"
                } );
            }
            else if(page=='reportPL') {
                that.$el.find('#privia_grid').dataTable( {
                    "bProcessing": true,
                    "bServerSide": true,
                    "sDom": "<'row'<'span6'<'dt_actions'>l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                    "sPaginationType": "bootstrap_alt",
                    "sAjaxDataProp" : "recordList",
                    "aoColumnDefs": [
                        {
                            "mRender": function ( data, type, row ) {

                                return '<a href="#/cabang/edit/'+data+'">Ubah</a>';
                            },
                            "aTargets": [4 ]
                        }
                    ],
                    "aoColumns": [
                        { "mData": "S/N" },
                        { "mData": "Item" },
                        { "mData": "Shop" },
                        { "mData": "Quantity" },
                        { "mData": "Price" }
                    ],
                    "sAjaxSource": "/reports/transaction"
                } );
            }

           // that.$el.find('#bungaPinjaman').select2({data:{results:[]}});


        }

    });
    // Our module now returns our view
    return  ReportView;
});