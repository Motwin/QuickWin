(function(document, undefined ) {
    
function queryStringToObject( qstr ) {
    var result = {},
        nvPairs = ( ( qstr || "" ).replace( /^\?/, "" ).split( /&/ ) ),
        i, pair, n, v;

    for ( i = 0; i < nvPairs.length; i++ ) {
        var pstr = nvPairs[ i ];
        if ( pstr ) {
            pair = pstr.split( /=/ );
            n = pair[ 0 ];
            v = pair[ 1 ];
            if ( result[ n ] === undefined ) {
                result[ n ] = v;
            } else {
                if ( typeof result[ n ] !== "object" ) {
                    result[ n ] = [ result[ n ] ];
                }
                result[ n ].push( v );
            }
        }
    }

    return result;
}

// intercepts all the page changes and apply the routes
$(document).on("pagebeforechange", function(event, data) {
    
    // handle event only if toPage is a string
    if (typeof data.toPage === "string") {
        // parse the toPage
        var url = $.mobile.path.parseUrl(data.toPage);
        // use the hash to route
        if(url.hash && url.hash.length > 0) {
            var hash         = $.mobile.path.parseUrl(url.hash.replace( /^#/, "" )),
                hashNoSearch = hash.pathname;
                
            if(hash.search && hash.search.length > 0) {
                // load the page manually
                $.mobile.loadPage("#" + hashNoSearch, data.options )
                    .done(function( url, options, newPage, dupCachedPage ) {
                        var previousParams = newPage.data("params"), 
                            newParams      = queryStringToObject(hash.search);
                            
                        data.options.dataUrl = data.toPage;
                        data.options.duplicateCachedPage = dupCachedPage;
                        
                        if(JSON.stringify(newParams) != JSON.stringify(previousParams)) {
                            newPage.data("params", newParams)
                            newPage.trigger('pageparamschange', {
                                'params':         newParams, 
                                'previousParams': previousParams
                            });
                        }
                        
                        $.mobile.changePage(newPage, data.options);
                        
                    })
                    .fail(function( url, options ) {
                        data.options.pageContainer.trigger("pagechangefailed", data);
                        
                    });
                    
                event.preventDefault();
            }
        }
    }
});
    
})( document );