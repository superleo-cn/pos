define([
  'jquery',
  'underscore',
  'backbone',
  './views/reportView',
  './views/commonView',
  './views/modalView'
  ], 

  function($, _, Backbone, ReportView,CommonView, ModalView) {

    var AppRouter = Backbone.Router.extend({
      initialize : function(option) {
        this.currentView = {};
          this.bind( "route", function(){
             if(hideSidebar) {
                 $('body').addClass('sidebar_hidden');
             }
          });
      },
      routes: {
      // Define some URL routes
      'reportTransaction': 'showReportTransaction',
      'reportCashierClosing': 'showReportCashierClosing',
      'reportLoginAudit': 'showReportLoginAudit',
      'reportPL': 'showReportPL',
      'cabang/edit/:id': 'editCabang',
      'cabang': 'showCabang',
      'modal/customer': 'addCustomerModal',
      'home': 'showHome',
      'analisis': 'showAnalisa',
      'login': 'showLogin',
      'success': 'showSuccess',
      // Default
      '': 'defaultAction',
      '*actions': 'defaultAction'
    },
    defaultAction: function(action) {
      console.log('nodonk' + action);
      new CommonView().render();
    },
    showHome: function() {
      console.log('showHome');
      new CommonView().render();
    },
   showReportTransaction: function() {
    console.log('showReportTransaction');
    new ReportView().render('reportTransaction');
  },
    showReportCashierClosing: function() {
        console.log('showReportCashierClosing');
        new ReportView().render('reportCashierClosing');
    },
    showReportLoginAudit: function() {
        console.log('showReportLoginAudit');
        new ReportView().render('reportLoginAudit');
    },
    showReportPL: function() {
        console.log('showReportPL');
        new ReportView().render('reportPL');
    },
showSuccess: function() {
  new SuccessView().render()
},
showLogin: function() {
  console.log('showLogin');
  new LoginView().render()
}
});

return AppRouter;
});