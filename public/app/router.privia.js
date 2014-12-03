define([ 'jquery', 'underscore', 'backbone', './views/reportView', './views/commonView', './views/modalView', './views/master/itemView' ],

function($, _, Backbone, ReportView, CommonView, ModalView, ItemView) {

	var AppRouter = Backbone.Router.extend({
		initialize : function(option) {
			this.currentView = {};
			this.bind("route", function() {
				if (hideSidebar) {
					$('body').addClass('sidebar_hidden');
				}
			});
		},
		routes : {
			// Define some URL routes
			'reportDashboard' : 'showReportDashboard',
			'reportTransaction' : 'showReportTransaction',
			'reportSummary' : 'showReportSummary',
			'reportCashierClosing' : 'showReportCashierClosing',
			'reportLoginAudit' : 'showReportLoginAudit',
			'reportPL' : 'showReportPL',
			'modal/customer': 'addCustomerModal',
			'home' : 'showHome',
			'login' : 'showLogin',
			'success' : 'showSuccess',
			'master/item/list' : 'itemList',
			'master/item/upload' : 'itemUpload',
			'master/item/edit/:id' : 'editItem',
			'master/item' : 'addItem',
			// Default
			'' : 'defaultAction',
			'*actions' : 'defaultAction'
		},
		defaultAction : function(action) {
			console.log('nodonk' + action);
			new CommonView().render();
		},
		showHome : function() {
			console.log('showHome');
			new CommonView().render();
		},
		itemList : function() {
			console.log('itemList');
			new ItemView().render('list');
		},
		itemUpload : function() {
			console.log('itemUpload');
			new ItemView().render('upload');
		},
		addItem : function() {
			console.log('addItem');
			new ItemView().render('form');
		},
		editItem : function(id) {
			console.log('editItem');
			new ItemView({
				id : id
			}).render('form');
		},
		showReportDashboard : function() {
			console.log('showReportDashboard');
			new ReportView().render('reportDashboard');
		},
		showReportTransaction : function() {
			console.log('showReportTransaction');
			new ReportView().render('reportTransaction');
		},
		showReportSummary : function() {
			console.log('showReportSummary');
			new ReportView().render('reportSummary');
		},
		showReportCashierClosing : function() {
			console.log('showReportCashierClosing');
			new ReportView().render('reportCashierClosing');
		},
		showReportLoginAudit : function() {
			console.log('showReportLoginAudit');
			new ReportView().render('reportLoginAudit');
		},
		showReportPL : function() {
			console.log('showReportPL');
			new ReportView().render('reportPL');
		},
		showSuccess : function() {
			new SuccessView().render()
		},
		showLogin : function() {
			console.log('showLogin');
			new LoginView().render()
		}
	});

	return AppRouter;
});