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
            "click #exportCashierClosing" :"exportCashierClosing",
            "click #exportPL" :"exportPL"
        },
        exportTransactionDetail:function() {
          window.open("/reports/exportTransactionDetail");
        },
        exportTransactionSummary:function() {
            window.open("/reports/exportTransactionSummary");
        },
        exportCashierClosing:function() {
            window.open("/reports/exportCashierClosing");
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

                var user = that.$el.find('#user').val();
                var outlet = that.$el.find('#outlet').val();
                var dateFrom = that.$el.find('#dateFrom').val();
                var dateTo = that.$el.find('#dateTo').val();

                that.oTable.fnMultiFilter({"no":user,"user.realname":outlet,"shop.name":dateFrom,"totalQuantity":dateTo});

            }
            else  if(page=='reportCashierClosing') {

                var cashier = that.$el.find('#cashier').val();

                var outlet = that.$el.find('#outlet').val();

                var dateFrom = that.$el.find('#dateFrom').val();

                var dateTo = that.$el.find('#dateTo').val();


                that.oTable.fnMultiFilter({"no":cashier,"realName":outlet,"shopName":dateFrom,"openBalance":dateTo});

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
            if(accessType=='ADMIN' || accessType=='SUPERADMIN'){
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
                   "aoColumnDefs": [
                        {
                            "mRender": function ( data, type, row ) {
                                return data.toFixed(2);
                            },
                            "aTargets": [4 ]
                        }
                    ],
                    "aoColumns": [
                        { "mData": "no",  "bSortable": false  },
                        { "mData": "item",  "bSortable": false  },
                        { "mData": "shopName",  "bSortable": false  },
                        { "mData": "totalQuantity" ,  "bSortable": false },
                        { "mData": "totalPrice",  "bSortable": false  }
                    ],
                    "sAjaxSource": "/reports/transaction"
                } );
                $('#timeFrom,#timeTo').timepicker({'defaultTime':false,'template':false,showMeridian:false});

            }
            else if(page=='reportCashierClosing') {

                that.oTable = that.$el.find('#privia_grid').dataTable( {
                    "bProcessing": true,
                    "bServerSide": true,
                    "sDom": "<'row'<'span6'<'dt_actions'>l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                    "sPaginationType": "bootstrap_alt",
                    "sAjaxDataProp" : "recordList",
                    "aoColumnDefs": [
                        {
                            "mRender": function ( data, type, row ) {
                                return data.toFixed(2);
                            },
                            "aTargets": [3,4,5,6,7,8 ]
                        }
                    ],
                    "aoColumns": [
                        { "mData": "no",  "bSortable": false  },
                        { "mData": "realName",  "bSortable": false  },
                        { "mData": "shopName",  "bSortable": false  },
                        { "mData": "openBalance",  "bSortable": false  },
                        { "mData": "cashInDrawer" ,  "bSortable": false },{ "mData": "expenses",  "bSortable": false  },{ "mData": "totalCollection",  "bSortable": false  },
                        { "mData": "dailyTurnover" ,  "bSortable": false },{ "mData": "total",  "bSortable": false  }
                    ],
                    "sAjaxSource": "/reports/cashierClosing"
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
                        { "mData": "no" ,  "bSortable": false },
                        { "mData": "user.realname",  "bSortable": false  },
                        { "mData": "shop.name",  "bSortable": false  },
                        { "mData": "createDate" ,  "bSortable": false },
                        { "mData": "action" ,  "bSortable": false }
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
                    "aoColumnDefs": [
                        {
                            "mRender": function ( data, type, row ) {
                                return data.toFixed(2);
                            },
                            "aTargets": [3]
                        }
                    ],
                    "aoColumns": [
                        { "mData": "no",  "bSortable": false  },
                        { "mData": "shopName",  "bSortable": false  },
                        { "mData": "item",  "bSortable": false  },
                        { "mData": "amount",  "bSortable": false  }
                    ],
                    "sAjaxSource": "/reports/pl"
                } );
            }

            $("#dateFrom").datepicker({ dateFormat: 'yy-mm-dd',changeYear :true,changeMonth: true});
            $("#dateTo").datepicker({ dateFormat: 'yy-m-dd',changeYear :true,changeMonth: true  });

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
                    outlet.replaceWith('<select name=outlet id=outlet>'+shopStr+'</select>');
                });
            }

            var cashier = that.$el.find('#cashier');
            if(cashier!=null) {
                //cashier.select2({data:{}});
                var format=function format(item) { return item.name; };

                $.get('/reports/cashiers',function(response){
                    response.recordList.unshift({id:'ALL',realname:'ALL'});

                    var record = $.map(response.recordList, function(obj){
                        return {id:obj.realname,name:obj.realname};
                    });
                    var cashierStr ='';
                    record.forEach(function(entry) {
                        cashierStr+='<option>'+entry.name+'</option>';
                    });
                    cashier.replaceWith('<select name=cashier id=cashier>'+cashierStr+'</select>');
                });
            }

            var user = that.$el.find('#user');
            if(user!=null) {
                //user.select2({data:{}});
                var format=function format(item) { return item.name; };

                $.get('/reports/users',function(response){
                    response.recordList.unshift({id:'ALL',realname:'ALL'});

                    var record = $.map(response.recordList, function(obj){
                        return {id:obj.realname,name:obj.realname};
                    });
                    var userStr ='';
                    record.forEach(function(entry) {
                        userStr+='<option>'+entry.name+'</option>';
                    });
                    user.replaceWith('<select name=user id=user>'+userStr+'</select>');
                });
            }
           // that.$el.find('#bungaPinjaman').select2({data:{results:[]}});


        }

    });
    // Our module now returns our view
    return  ReportView;
});