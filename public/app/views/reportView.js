define([
    'jquery',
    'underscore',
    'backbone',
    'text!../templates/reportDashboard.html',
    'text!../templates/reportTransaction.html',
    'text!../templates/reportSummary.html',
    'text!../templates/reportCashierClosing.html',
    'text!../templates/reportLoginAudit.html',
    'text!../templates/reportPL.html',
    '../models/report'
], function($, _, Backbone,reportDashboard,reportTransaction,reportSummary,reportCashierClosing,reportLoginAudit,reportPL,Model){

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
            "click #clearReport" :"clearReport",
            	
            "change #type" :"changeDashboardType"
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
            that.$el.find('#date').val('');
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
        changeDashboardType:function() {
        	var type = $("#type").val();
        	var format = 'yy-mm-dd';
        	if(type == "Daily"){
        		format = 'yy-mm-dd';
        	}else if(type == "Monthly"){
        		format = 'yy-mm';
        	}
        	$( "#date" ).datepicker( "option", "dateFormat", format );
        },
        searchReport:function() {
            var page =this.page;
            if(page=='reportDashboard') {
                var outlet = that.$el.find('#outlet').val();
                var type = that.$el.find('#type').val();
                var date = that.$el.find('#date').val();
               
                reportQuantity(outlet, type, date);
                pieChartMoney(outlet, type, date);
                lineChartQuantity(outlet, type, date);
                lineChartMoney(outlet, type, date);
            	
            }
            else if(page=='reportTransaction') {
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
            else if(page=='reportSummary') {
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
                  //that.oTable.fnMultiFilter({"shopName":outlet,"dateFrom":dateFrom,"dateTo":dateTo});
                  that.oTable.fnMultiFilter({"shopName":outlet,"totalQuantity":dateFrom,"retailPrice":dateTo});
            }
            else if(page=='reportLoginAudit') {

                var user = that.$el.find('#user').val();
                var outlet = that.$el.find('#outlet').val();
                var dateFrom = that.$el.find('#dateFrom').val();
                var dateTo = that.$el.find('#dateTo').val();
                that.oTable.fnMultiFilter({"no":user,"user.realname":outlet,"shop.name":dateFrom,"createDate":dateTo});

            }
            else if(page=='reportCashierClosing') {

                var cashier = that.$el.find('#cashier').val();
                var outlet = that.$el.find('#outlet').val();
                var dateFrom = that.$el.find('#dateFrom').val();
                var dateTo = that.$el.find('#dateTo').val();
                that.oTable.fnMultiFilter({"no":cashier,"realName":outlet,"createDate":dateFrom,"shopName":dateTo});

            }
            else if(page=='reportPL') {

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
            console.log('=>render quote view' + page);
            var that = this;
            var id = this.options.id;
            var data = {};
            var template;
            if(page=='reportDashboard')
                template=reportDashboard;
            else if(page=='reportTransaction')
                template=reportTransaction;
            else if(page=='reportSummary')
                template=reportSummary;
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
            if(page=='reportDashboard') {
              
            	 var outlet = that.$el.find('#outlet').val();
                 var type = that.$el.find('#type').val();
                 var date = that.$el.find('#date').val();
                
                 reportQuantity(outlet, type, date);
                 pieChartMoney(outlet, type, date);
                 lineChartQuantity(outlet, type, date);
                 lineChartMoney(outlet, type, date);
            }
            else if(page=='reportTransaction') {
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
                            "aTargets": [5 ]
                        }
                    ],
                    "aoColumns": [
                        { "mData": "no",  "bSortable": false  },
                        { "mData": "itemCategory",  "bSortable": false  },
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
            else if(page=='reportSummary') {
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
                            "aTargets": [3, 4, 5, 6]
                        }
                    ],
                    "aoColumns": [
                        { "mData": "no",  "bSortable": false  },
                        { "mData": "shopName",  "bSortable": false  },
                        { "mData": "totalQuantity" ,  "bSortable": false,"sClass":"number_tac" },
                        { "mData": "retailPrice" ,  "bSortable": false,"sClass":"number_tac" },
                        { "mData": "gst" ,  "bSortable": false,"sClass":"number_tac" },
                        { "mData": "sc" ,  "bSortable": false,"sClass":"number_tac" },
                        { "mData": "totalPrice",  "bSortable": false,"sClass":"number_tar"  }
                    ],
                    "sAjaxSource": "/reports/transactionSummary"
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
            $("#date").datepicker({ dateFormat: 'yy-mm-dd',changeYear :true,changeMonth: true});

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

function reportQuantity(outlet, type, date){
	 $.ajax({
		 url: "/reports/pieChartQuantity",
         dataType: "json",
         data:{"shopName" : outlet, "type": type, "date": date},
         async: true,
         type: "post",
         success: function (datas) {
      	  
         	FusionCharts.ready(function () {
         	    // Create a new instance of FusionCharts for rendering inside an HTML
         	    // `&lt;div&gt;` element with id `my-chart-container`.
         	    var myChart = new FusionCharts({
         	        type: 'Pie2D',
         	        renderAt: 'reportQuantity',
         	        width: "100%", 
                    height: "70%", 
         	        dataFormat: 'json',
         	        dataSource: {
         	            chart: {
         	            	"numberPrefix": "",
         	                "caption": "Sales Quantity Report",
         	                "subCaption": "",
         	                "bgcolor": "FFFFFF",
         	                "showvalues": "1",
         	                "showpercentvalues": "0",
         	                "showborder": "0",
         	                "showplotborder": "0",
         	                "showlegend": "1",
         	                "legendborder": "0",
         	                "legendposition": "bottom",
         	                "enablesmartlabels": "1",
         	                "use3dlighting": "0",
         	                "showshadow": "0",
         	                "legendbgcolor": "#CCCCCC",
         	                "legendbgalpha": "20",
         	                "legendborderalpha": "0",
         	                "legendshadow": "0",
         	                "legendnumcolumns": "3",
         	                "showBorder": "0",
         	                "pieRadius": "120",
         	                "palettecolors": "#f8bd19,#e44a00,#008ee4,#33bdda,#6baa01,#583e78"
         	            },
         	            data:datas
         	        }
         	    });
         	    // Render the chart.
         	    myChart.render();
         	});
         	
         	
         },
         error: function (request,error) {
             //alert('Network error has occurred please try again!');
         }
     });
}


function pieChartMoney(outlet, type, date){
	$.ajax({
		url: "/reports/pieChartMoney",
        dataType: "json",
        data:{"shopName" : outlet, "type": type, "date": date},
        async: true,
        type: "post",
        success: function (datas) {
     	  
        	FusionCharts.ready(function () {
        	    // Create a new instance of FusionCharts for rendering inside an HTML
        	    // `&lt;div&gt;` element with id `my-chart-container`.
        	    var myChart = new FusionCharts({
        	        type: 'Pie2D',
        	        renderAt: 'pieChartMoney',
        	        width: "100%", 
                    height: "70%", 
        	        dataFormat: 'json',
        	        dataSource: {
        	            chart: {
        	            	"numberPrefix": "S$",		
        	                "caption": "Sales Money Report",
        	                "subCaption": "",
        	                "bgcolor": "FFFFFF",
        	                "showvalues": "1",
        	                "showpercentvalues": "0",
        	                "showborder": "0",
        	                "showplotborder": "0",
        	                "showlegend": "1",
        	                "legendborder": "0",
        	                "legendposition": "bottom",
        	                "enablesmartlabels": "1",
        	                "use3dlighting": "0",
        	                "showshadow": "0",
        	                "legendbgcolor": "#CCCCCC",
        	                "legendbgalpha": "20",
        	                "legendborderalpha": "0",
        	                "legendshadow": "0",
        	                "legendnumcolumns": "3",
        	                "showBorder": "0",
        	                "pieRadius": "120",
        	                "palettecolors": "#f8bd19,#e44a00,#008ee4,#33bdda,#6baa01,#583e78"
        	            },
        	            data:datas
        	        }
        	    });
        	    // Render the chart.
        	    myChart.render();
        	});
        	
        	
        },
        error: function (request,error) {
            //alert('Network error has occurred please try again!');
        }
    });
}


function lineChartQuantity(outlet, type, date){	
	$.ajax({
	   url: "/reports/lineChartQuantity",
       dataType: "json",
       data:{"shopName" : outlet, "type": type, "date": date},
       async: true,
       type: "post",
       success: function (datas) {
    	   
       	FusionCharts.ready(function () {
       	    // Create a new instance of FusionCharts for rendering inside an HTML
       	    // `&lt;div&gt;` element with id `my-chart-container`.
       	    var myChart = new FusionCharts({
       	        type: 'ZoomLine',
       	        renderAt: 'lineChartQuantity',
       	        width: "100%", 
       	        height: "70%", 
       	        dataFormat: 'json',
       	        dataSource: {
       	        	chart: {
       	             "numberPrefix": "",
       	             "compactdatamode": "1",
       	             "dataseparator": "|",
       	             "caption": "Sales Quantity Report",
       	             "subcaption": "",
       	             "axis": "linear",
       	             "numberprefix": "",
       	             "formatnumberscale": "0",
       	             "allowpinmode": "0",
       	             "enableiconmousecursors": "0",
       	             "dynamicaxis": "1",
       	             "showlegend": "0",
       	             "slantlabels": "1",
       	             "rotatelabels": "0",
       	             "bgcolor": "FFFFFF",
       	             "showalternatehgridcolor": "0",
       	             "showplotborder": "0",
       	             "showvalues": "0",
       	             "divlinecolor": "CCCCCC",
       	             "showcanvasborder": "0",
       	             "linecolor": "008ee4",
       	             "showshadow": "0",
       	             "linethickness": "3",
       	             "captionpadding": "20",
       	             "canvasbottommargin": "30",
       	             "yaxisvaluespadding": "10",
       	             "scrollcolor": "CCCCCC",
       	             "canvasborderalpha": "0",
       	             "anchorradius": "3",
       	             "showborder": "0"
       	         },
       	         categories: [
       	             {
       	                 "category": datas.categories
       	             }
       	         ],
       	         dataset: [
       	             {
       	                 "seriesname": "Close",
       	                 "data": datas.dataset
       	             }
       	         ]
       	        }
       	    });
       	    // Render the chart.
       	    myChart.render();
       	});
       	
       	
       },
       error: function (request,error) {
           //alert('Network error has occurred please try again!');
       }
   });
}


function lineChartMoney(outlet, type, date){	
	$.ajax({
	   url: "/reports/lineChartMoney",
       dataType: "json",
       data:{"shopName" : outlet, "type": type, "date": date},
       async: true,
       type: "post",
       success: function (datas) {
       	FusionCharts.ready(function () {
       	    // Create a new instance of FusionCharts for rendering inside an HTML
       	    // `&lt;div&gt;` element with id `my-chart-container`.
       	    var myChart = new FusionCharts({
       	        type: 'ZoomLine',
       	        renderAt: 'lineChartMoney',
       	        width: "100%", 
       	        height: "70%", 
       	        dataFormat: 'json',
       	        dataSource: {
       	        	chart: {
       	             "numberPrefix": "S$",		
       	             "compactdatamode": "1",
       	             "dataseparator": "|",
       	             "caption": "Sale Money Report",
       	             "subcaption": "",
       	             "axis": "linear",
       	             "numberprefix": "$",
       	             "formatnumberscale": "0",
       	             "allowpinmode": "0",
       	             "enableiconmousecursors": "0",
       	             "dynamicaxis": "1",
       	             "showlegend": "0",
       	             "slantlabels": "1",
       	             "rotatelabels": "0",
       	             "bgcolor": "FFFFFF",
       	             "showalternatehgridcolor": "0",
       	             "showplotborder": "0",
       	             "showvalues": "0",
       	             "divlinecolor": "CCCCCC",
       	             "showcanvasborder": "0",
       	             "linecolor": "008ee4",
       	             "showshadow": "0",
       	             "linethickness": "3",
       	             "captionpadding": "20",
       	             "canvasbottommargin": "30",
       	             "yaxisvaluespadding": "10",
       	             "scrollcolor": "CCCCCC",
       	             "canvasborderalpha": "0",
       	             "anchorradius": "3",
       	             "showborder": "0"
       	         },
       	         categories: [
       	             {
       	                 "category": datas.categories
       	             }
       	         ],
       	         dataset: [
       	             {
       	                 "seriesname": "Close",
       	                 "data": datas.dataset
       	             }
       	         ]
       	        }
       	    });
       	    // Render the chart.
       	    myChart.render();
       	});
       	
       	
       },
       error: function (request,error) {
           //alert('Network error has occurred please try again!');
       }
   });
}