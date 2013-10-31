function showMessage(msg){
	$.mobile.showPageLoadingMsg($.mobile.pageLoadErrorMessageTheme, msg, true);
    setTimeout(function() {
    	$.mobile.hidePageLoadingMsg();
  	}, 2000);
}

function showPopup(msg){
	$.mobile.showPageLoadingMsg($.mobile.pageLoadErrorMessageTheme, msg, true);
}

function clearForm(form){
    $('input',form).not('[type="button"]').val(''); // clear inputs except buttons, setting value to blank
    $('select',form).val(''); // clear select
    $('textarea',form).val(''); // set text area value to blank
}

function enableScrolling(callbck){

    scrollOK = true;
    count    = 10;
    afterScrollCallback = callbck;


}

function deleteDialog2(callbackDelete){
	$("#confirm").popup("open");
    // Proceed when the user confirms
    $("#confirm #yes").one("click", function(){
    	callbackDelete();
        $("#confirm #cancel").off();
        
    });
    // Remove active state and unbind when the cancel button is clicked
    $("#confirm #cancel").one("click", function(){
        $("#confirm #yes").off();
    });
}

function initializeDefaultPage() {

}

function initializeFormPage(navBack,saveCallback,pageInit) {

    navigationBack = navBack;
    console.log(navBack);
    actionSave = saveCallback;
    console.log(pageInit);
	callbackPageInit=pageInit;
    scrollOK = false;
}

function initializeListPage(navAdd,navBack,pageInit) {

	currentPage = 0;
	isLastPage = true;
    scrollOK = true;
    navigationAdd = navAdd;
    navigationBack = navBack;
    callbackPageInit = pageInit;

}