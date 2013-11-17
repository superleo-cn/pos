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
            "click #searchReport"   : "searchReport",
            "click #exportTransactionDetail" :"exportTransactionDetail",
            "click #exportTransactionSummary" :"exportTransactionSummary",
            "click #exportLoginAudit" :"exportLoginAudit",
            "click #exportPL" :"exportPL"
        },
        exportTransactionDetail:function() {
          window.open("/reports/exportTransactionDetail");
        },
        exportTransactionSummary:function() {
            window.open("/reports/exportTransactionSummary");
        },
        exportTransactionSummary:function() {
            window.open("/reports/exportTransactionSummary");
        },
        exportLoginAudit:function() {
            window.open("/reports/exportLoginAudit");
        },
        exportPL:function() {
            window.open("/reports/exportPL");
        },
        searchReport:function() {
            var page =this.page;
            if(page=='reportTransaction') {
              var item = that.$el.find('#item').val();
                var outlet = that.$el.find('#outlet').val();
                var dateFrom = that.$el.find('#dateFrom').val();
                var timeFrom = that.$el.find('#timeFrom').val();
                if(timeFrom!='' && dateFrom!='')
                    dateFrom = dateFrom+' '+timeFrom;
                var dateTo = that.$el.find('#dateTo').val();
                var timeTo = that.$el.find('#timeTo').val();
                if(timeTo!='' && dateTo!='')
                    dateTo = dateTo+' '+timeTo;

                that.oTable.fnMultiFilter({"no":item,"item":outlet,"shopName":dateFrom,"totalQuantity":dateTo});

            }
            else  if(page=='reportLoginAudit') {

                var cashier = that.$el.find('#cashier').val();
                var outlet = that.$el.find('#outlet').val();
                var dateFrom = that.$el.find('#dateFrom').val();
                var dateTo = that.$el.find('#dateTo').val();

                that.oTable.fnMultiFilter({"no":cashier,"user.realname":outlet,"shop.name":dateFrom,"totalQuantity":dateTo});

            }
            else  if(page=='reportPL') {

                var outlet = that.$el.find('#outlet').val();

                var dateFrom = that.$el.find('#dateFrom').val();

                var dateTo = that.$el.find('#dateTo').val();


                that.oTable.fnMultiFilter({"shopName":outlet,"item":dateFrom,"amount":dateTo});

            }
        },
        initialize:function(options){
            console.log('initialize report view ');
            this.$el.undelegate();

            var that = this;
            this.model = new Model();

            this.listenTo(this.model,'error', function(model,e) {
                var err;


            });
            this.listenTo(this.model,'sync', function(e) {
                console.log('sukses');


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
            that.page=page;

            var accessType = userCredential.accessType;
            if(accessType=='ADMIN' || accessType=='OPERATOR'){
                that.$el.find('.adminFields').show();
            }
            else {
                that.$el.find('.adminFields').hide();
            }
            if(page=='reportTransaction') {
                    that.oTable = that.$el.find('#privia_grid').dataTable( {
                    "bProcessing": true,
                    "bServerSide": true,
                    "sDom": "<'row'<'span6'<'dt_actions'>l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                    "sPaginationType": "bootstrap_alt",
                    "sAjaxDataProp" : "recordList",
                 /*   "aoColumnDefs": [
                        {
                            "mRender": function ( data, type, row ) {
console.log(row);
                                return row;
                            },
                            "aTargets": [0 ]
                        }
                    ],*/
                    "aoColumns": [
                        { "mData": "no" },
                        { "mData": "item" },
                        { "mData": "shopName" },
                        { "mData": "totalQuantity" },
                        { "mData": "totalPrice" }
                    ],
                    "sAjaxSource": "/reports/transaction"
                } );

                $('#timeFrom,#timeTo').timepicker({'defaultTime':false,'template':'modal',showMeridian:false});

            }
            else if(page=='reportCashierClosing') {

                that.oTable = that.$el.find('#privia_grid').dataTable( {
                    "bProcessing": true,
                    "bServerSide": true,
                    "sDom": "<'row'<'span6'<'dt_actions'>l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                    "sPaginationType": "bootstrap_alt",
                    "sAjaxDataProp" : "recordList",

                    "aoColumns": [
                        { "mData": "no" },
                        { "mData": "item" },
                        { "mData": "no" },
                        { "mData": "totalQuantity" },
                        { "mData": "no" },{ "mData": "no" },{ "mData": "no" },
                        { "mData": "no" },{ "mData": "no" }
                    ],
                    "sAjaxSource": "/reports/transaction"
                } );
            }
            else if(page=='reportLoginAudit') {
                that.oTable = that.$el.find('#privia_grid').dataTable( {
                    "bProcessing": true,
                    "bServerSide": true,
                    "sDom": "<'row'<'span6'<'dt_actions'>l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                    "sPaginationType": "bootstrap_alt",
                    "sAjaxDataProp" : "recordList",

                    "aoColumns": [
                        { "mData": "no" },
                        { "mData": "user.realname" },
                        { "mData": "shop.name" },
                        { "mData": "createDate" },
                        { "mData": "action" }
                    ],
                    "sAjaxSource": "/reports/loginAudit"
                } );
            }
            else if(page=='reportPL') {
                that.oTable = that.$el.find('#privia_grid').dataTable( {
                    "bProcessing": true,
                    "bServerSide": true,
                    "sDom": "<'row'<'span6'<'dt_actions'>l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                    "sPaginationType": "bootstrap_alt",
                    "sAjaxDataProp" : "recordList",
                    "aoColumns": [
                        { "mData": "no" },
                        { "mData": "shopName" },
                        { "mData": "item" },
                        { "mData": "amount" }
                    ],
                    "sAjaxSource": "/reports/pl"
                } );
            }


            $("#dateFrom").datepicker({ dateFormat: 'yy-m-dd',changeYear :true,changeMonth: true  });
            $("#dateTo").datepicker({ dateFormat: 'yy-m-dd',changeYear :true,changeMonth: true  });

           // that.$el.find('#bungaPinjaman').select2({data:{results:[]}});


        }

    });
    // Our module now returns our view
    return  ReportView;
});