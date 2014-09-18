(function(motwin, window, undefined ) {
    
motwin.extendClientChannel("observableConnected", function() {
  var status = ko.observable(this.isConnected());
  
  this.onConnect(function() {
    status(true);
  });
  
  this.onDisconnect(function() {
    status(false);
  });
  
  return status;
});

motwin.extendClientChannel("observableContinuousQuery", function(query, args) {
  var args = args || {};
  
  // indicate if the data is available
  var isLoaded  = ko.observable(false);
  
  // indicate if the data is synchronized
  var isSynchronized = ko.observable(false);
  
  // create a first object observable
  var observableFirst = ko.observable();
  
  // create a observableArray
  var observableArray = ko.observableArray();
  
  // create the continuous query
  var continuousQuery = this.createContinuousQuery(query, args).
  
    // called when the data is synchronized with the server
    onSynchronize(function() {
      isSynchronized(true);
    }).
    
    // called when the data is unsynchronized from the server
    onUnsynchronize(function() {
      isSynchronized(false);
    }).
    
    // change loaded to true to indicate that the data is available
    onChange(function(metadata, rows) {
      if(!isLoaded()) {
          isLoaded(true);
      }
    }).
    
    // insert the row and update the first row if first
    onInsert(function(metadata, position, properties) {
      var observableProperties = {};
      for (var propertyName in properties) {
        var propertyValue = properties[propertyName];
        observableProperties[propertyName] = ko.observable(propertyValue);
      }
      observableArray.splice(position, 0, observableProperties);
      if(position == 0) {
          observableFirst(observableProperties);
      }
    }).
    
    // update the row and update the first row if first
    onUpdate(function(metadata, position, properties, updatedProperties) {
      var currentProperties = observableArray()[position];
      for (var propertyName in updatedProperties) {
        var propertyValue = updatedProperties[propertyName];
        currentProperties[propertyName](propertyValue);
      }
    }).
    
    // remove the row from the array and re insert the row at the index 
    onMove(function(metadata, oldPosition, newPosition) {
        var removedProperties = observableArray.splice(oldPosition, 1)[0];
        observableArray.splice(newPosition, 0, removedProperties);
        if(newPosition == 0) {
            observableFirst(removedProperties);
        }
    }).
    
    // remove from the array and nullify if the first row
    onDelete(function(metadata, position) {
        observableArray.splice(position, 1);
        if(position == 0) {
            observableFirst(null);
        }
    });

  continuousQuery.loaded       = isLoaded;
  continuousQuery.synchronized = isSynchronized;
  continuousQuery.all          = observableArray;
  continuousQuery.first        = observableFirst;
  
  return continuousQuery;
});

})( motwin, window );