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

                "click #exportExpenses" :"exportExpenses",
                "click #exportCashCollection" :"exportCashCollection",
            "click #exportCashierClosing" :"exportCashierClosing",
            "click #exportPL" :"exportPL",
            "click #clearReport" :"clearReport"
        },
        clearReport:function() {
            that.$el.find('#item').val('');
            that.$el.find('#outlet').val('ALL');
            that.$el.find('#cashier').val('ALL');
            that.$el.find('#user').val('ALL');
            that.$el.find('#dateFrom').val('');
            that.$el.find('#timeFrom').val('');
            that.$el.find('#dateTo').val('');
            that.$el.find('#timeTo').val('');
        },
        exportExpenses:function() {
            window.open("/reports/exportExpensesDetails?_dc"+new Date());
        },
        exportCashCollection:function() {
            window.open("/reports/exportCollectionDetails?_dc"+new Date());
        },
        exportTransactionDetail:function() {
          window.open("/reports/exportTransactionDetail?_dc"+new Date());
        },
        exportTransactionSummary:function() {
            window.open("/reports/exportTransactionSummary?_dc"+new Date());
        },
        exportCashierClosing:function() {
            window.open("/reports/exportCashierClosing?_dc"+new Date());
        },
        exportLoginAudit:function() {
            window.open("/reports/exportLoginAudit?_dc"+new Date());
        },
        exportPL:function() {
            window.open("/reports/exportPL?_dc"+new Date());
        },
        searchReport:function() {
            var page =this.page;
            if(page=='reportTransaction') {
              var item = that.$el.find('#item').val();
                var outlet = that.$el.find('#outlet').val();
                var dateFrom = that.$el.find('#dateFrom').val();
                var timeFrom = that.$el.find('#timeFrom').val();
                if(timeFrom=='') {
                    timeFrom='00:00';
                }
                if(timeFrom!='' && dateFrom!='')
                    dateFrom = dateFrom+' '+timeFrom;
                var dateTo = that.$el.find('#dateTo').val();
                var timeTo = that.$el.find('#timeTo').val();
                if(timeTo=='') {
                    timeTo='23:59';
                }
                if(timeTo!='' && dateTo!='')
                    dateTo = dateTo+' '+timeTo;

                that.oTable.fnMultiFilter({"no":item,"item":outlet,"shopName":dateFrom,"totalQuantity":dateTo});

            }
            else  if(page=='reportLoginAudit') {

                var user = that.$el.find('#user').val();
                var outlet = that.$el.find('#outlet').val();
                var dateFrom = that.$el.find('#dateFrom').val();
                var dateTo = that.$el.find('#dateTo').val();

                that.oTable.fnMultiFilter({"no":user,"user.realname":outlet,"shop.name":dateFrom,"createDate":dateTo});

            }
            else  if(page=='reportCashierClosing') {

                var cashier = that.$el.find('#cashier').val();

                var outlet = that.$el.find('#outlet').val();

                var dateFrom = that.$el.find('#dateFrom').val();

                var dateTo = that.$el.find('#dateTo').val();


                that.oTable.fnMultiFilter({"no":cashier,"realName":outlet,"createDate":dateFrom,"shopName":dateTo});

            }
            else  if(page=='reportPL') {

                var outlet = that.$el.find('#outlet').val();

                var dateFrom = that.$el.find('#dateFrom').val();

                var dateTo = that.$el.find('#dateTo').val();


                that.oTable.fnMultiFilter({"shopName":outlet,"sales":dateFrom,"costOfSales":dateTo});

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
                    "iDisplayLength" : 100,
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
                        { "mData": "totalQuantity" ,  "bSortable": false,"sClass":"number_tac" },
                        { "mData": "totalPrice",  "bSortable": false,"sClass":"number_tar"  }
                    ],
                    "sAjaxSource": "/reports/transaction"
                } );

                that.$el.find('#dateFrom,#dateTo').val(moment().format('YYYY-MM-DD'));
                that.$el.find('#timeFrom,#timeTo').pickatime({ formatLabel:'HH:i',format:'HH:i'});

            }
            else if(page=='reportCashierClosing') {

                that.oTable = that.$el.find('#privia_grid').dataTable( {
                    "bProcessing": true,
                    "bServerSide": true,
                    "iDisplayLength" : 100,
                    "sDom": "<'row'<'span6'<'dt_actions'>l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                    "sPaginationType": "bootstrap_alt",
                    "sAjaxDataProp" : "recordList",

                    "aoColumnDefs": [
                        {
                            "mRender": function ( data, type, row ) {

                                return moment(data).format('YYYY-MM-DD H:mm');
                            },
                            "aTargets": [2 ]
                        },
                        {
                            "mRender": function ( data, type, row ) {
                                return data.toFixed(2);
                            },
                            "aTargets": [4,5,6,7,8,9,10 ]
                        }
                    ],
                    "aoColumns": [
                        { "mData": "no",  "bSortable": false  },
                        { "mData": "realName",  "bSortable": false  },
                        { "mData": "createDate",  "bSortable": false  },
                        { "mData": "shopName",  "bSortable": false  },
                        { "mData": "openBalance",  "bSortable": false ,"sClass":"number_tar" },
                        { "mData": "cashInDrawer" ,  "bSortable": false,"sClass":"number_tar" },
                        { "mData": "cardCollected" ,  "bSortable": false,"sClass":"number_tar" },
                        { "mData": "expenses",  "bSortable": false,"sClass":"number_tar"  },
                        { "mData": "nextOpenBalance",  "bSortable": false,"sClass":"number_tar"  },
                        { "mData": "totalCollection",  "bSortable": false,"sClass":"number_tar"  },
                        { "mData": "dailyTurnover" ,  "bSortable": false,"sClass":"number_tar" },
                        { "mData": "total",  "bSortable": false,"sClass":"number_tar"  }
                    ],
                    "sAjaxSource": "/reports/cashierClosing"
                } );
            }
            else if(page=='reportLoginAudit') {
                that.oTable = that.$el.find('#privia_grid').dataTable( {
                    "bProcessing": true,
                    "bServerSide": true,
                    "iDisplayLength" : 100,
                    "sDom": "<'row'<'span6'<'dt_actions'>l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                    "sPaginationType": "bootstrap_alt",
                    "sAjaxDataProp" : "recordList",

                    "aoColumnDefs": [
                        {
                            "mRender": function ( data, type, row ) {

                                return moment(data).format('YYYY-MM-DD H:mm');
                            },
                            "aTargets": [3 ]
                        }
                    ],
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
                    "iDisplayLength" : 100,
                    "sDom": "<'row'<'span6'<'dt_actions'>l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
                    "sPaginationType": "bootstrap_alt",
                    "sAjaxDataProp" : "recordList",
                    "aoColumnDefs": [
                        {
                            "mRender": function ( data, type, row ) {
                                return data.toFixed(2);
                            },
                            "aTargets": [2,3,4,5]
                        }
                    ],
                    "aoColumns": [
                        { "mData": "no",  "bSortable": false  },
                        { "mData": "shopName",  "bSortable": false  },
                        { "mData": "sales",  "bSortable": false ,"sClass":"number_tar" },
                        { "mData": "costOfSales",  "bSortable": false ,"sClass":"number_tar" },
                        { "mData": "expenses",  "bSortable": false,"sClass":"number_tar"  },
                        { "mData": "netProfit",  "bSortable": false,"sClass":"number_tar"  }
                    ],
                    "sAjaxSource": "/reports/pl"
                } );
            }

            $("#dateFrom").datepicker({ dateFormat: 'yy-mm-dd',changeYear :true,changeMonth: true});
            $("#dateTo").datepicker({ dateFormat: 'yy-mm-dd',changeYear :true,changeMonth: true  });

            var item = that.$el.find('#item');
            if(item!=null) {
                //  outlet.select2({data:{}});
                var format=function format(item) { return item.name; };

                $.get('/reports/items',function(response){
                    response.recordList.unshift({id:'ALL',name:'ALL'});

                    var record = $.map(response.recordList, function(obj){
                        return {id:obj.name,name:obj.name};
                    });
                    var itemStr ='';
                    record.forEach(function(entry) {
                        itemStr+='<option>'+entry.name+'</option>';
                    });
                    item.replaceWith('<select name=item id=item style="width:180px">'+itemStr+'</select>');
                });
            }

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