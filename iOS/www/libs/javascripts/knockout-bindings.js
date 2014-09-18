// add custom knockout bindingHandlers
ko.bindingHandlers.highlight = {
    update: function(element, valueAccessor, allBindingsAccessor, viewModel) {
        var allBindings   = allBindingsAccessor();
        var duration      = allBindings.highlightDuration || 1000; 
        var color         = allBindings.highlightColor || '#FFFF32'; 
        
        $(element).effect('highlight', {'color':color}, duration);
        
    }
};

ko.bindingHandlers.date = {
    update: function(element, valueAccessor, allBindingsAccessor) {
        var value = valueAccessor(), allBindings = allBindingsAccessor();
        var format = allBindings.dateFormat || "yy-mm-dd";
        
        if(value) {
            $(element).text($.datepicker.formatDate(format, new Date(value * 1000)));
        } else {
            $(element).text("");
        }
    }
};