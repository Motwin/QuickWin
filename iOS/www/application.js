// Generic ViewModel inherited by AccountListViewModel and AccountViewModel
function GenericViewModel(page, channel) {
  var self = {};

  self.loaded = false;

  self.showError = function(error) {
    // show error message
    $.mobile.showPageLoadingMsg($.mobile.pageLoadErrorMessageTheme, "Please try again later.", true);
    // hide after delay
    setTimeout($.mobile.hidePageLoadingMsg, 1500);
  };

  self.showLoading = function() {
    if(!self.loaded) {
      $.mobile.showPageLoadingMsg("b", "Loading...");
    }
  };

  self.hideLoading = function() {
    if(!self.loaded) {
      self.loaded = true;
      $.mobile.hidePageLoadingMsg();
    }
  };

  self.resetLoading = function() {
    self.loaded = false;
  };
  
  return self;
}

// ViewModel of page id="accountListPage"
function AccountListViewModel(page, channel) {
  var self = new GenericViewModel(page, channel), 
      $page = $(page);

  // The continuous query that will be started in order to get all Accounts
  var accountsQuery = channel.observableContinuousQuery("select Id, Name, Site, BillingAddress, Phone, Type, CreatedDate from Account order by CreatedDate DESC")
    .onChange(function() {
      $('#table', $page).listview("refresh");
      self.hideLoading();
    }).onError(function(error) {
      self.showError(error);
    });

  self.accounts = accountsQuery.all;

  $page.on("pagebeforeshow", function() {
	  accountsQuery.start();
  });

  $page.on("pageshow", function() {
    self.showLoading();
  });

  $page.on("pagebeforehide", function() {
	  accountsQuery.stop();
  });
    
  // when clicking on an account, opens the account details page
  $page.on("tap", "#table a", function(event) {
    var data = ko.dataFor(this);
    $.mobile.changePage('#accountPage?id=' + encodeURIComponent(data.Id()));
    return false;
  });

  ko.applyBindings(self, page);

  return self;
}

// ViewModel of page id="accountPage"
function AccountViewModel(page, channel) {
  var self = new GenericViewModel(page, channel), 
      $page = $(page), 
      accountQuery = null;

  self.account = ko.observable();

  $page.on("pageparamschange", function(event, data) {
    self.resetLoading();
    
    // the continuous query that will be started
    accountQuery = channel.observableContinuousQuery('select * from Account where Id=? order by CreatedDate', [decodeURIComponent(data.params.id)])
      .onChange(function() {
        self.hideLoading();
      }).onError(function(error) {
        self.showError(error);
      });

    self.account(accountQuery.first);

  });

  $page.on("pagebeforeshow", function() {
	  accountQuery.start();
  });

  $page.on("pageshow", function() {
    self.showLoading();
  });

  $page.on("pagebeforehide", function() {
	  accountQuery.stop();
  });

  ko.applyBindings(self, page);

  return self;
}
  
// jQueryMobile defaults
$(document).on("mobileinit", function() {
  $.mobile.linkBindingEnabled = false;
  $.mobile.buttonMarkup.hoverDelay = 20;
  $.mobile.defaultPageTransition = "none";
  $.mobile.defaultDialogTransition = "none";
  $.mobile.pageLoadErrorMessage = "Unable to load the page";
  $.mobile.loadingMessage = "Loading...";
});

$(document).ready(function() {
  // set the SDK log level (use DEBUG in development environment and ERROR in production environment)
  motwin.Logger.setLevel(motwin.Logger.DEBUG);

  // create the ClientChannel
  var channel = motwin.createClientChannel("http://tests.motwin.net:1249", {
    appName : 'com.motwin.sample.salesforce-sample',
    appVersion : '0.0.2'
  });

  // connect or create the manager that manages the lifecycle (cordova only)
  if(typeof cordova === "undefined") {
    channel.connect();
  } else {
    channel.useCordovaLifeCycle();
  }

  // bind 'back' links to history.back
  $(":jqmData(rel=back)").live('click', function() {
    history.back(-1);
    return false;
  });
  
  // wait for pages initialization and create the associated ViewModel
  
  // apply AccountListViewModel to the page id="accountListPage" on pageinit
  $(document).on("pageinit", "#accountListPage", function() {
    new AccountListViewModel(this, channel);
  });
    
  // apply AccountViewModel to the page id="accountPage" on pageinit
  $(document).on("pageinit", "#accountPage", function() {
    new AccountViewModel(this, channel);
  });

});
