		initContentLoader("${scrollerId}", "${scrolledContentId}");		
	 	
	 	function initContentLoader(scrollerId, scrolledContentId){
			refreshLoader(scrollerId, scrolledContentId);
		}
		
		function refreshLoader(scrollerId, scrolledContentId){
			setTimeout('checkScrollPosition("' + scrollerId+ '", "' + scrolledContentId + '")', 1000);
		}
		
		function checkScrollPosition(scrollerId, scrolledContentId){			
			var scroller = elementById(scrollerId); 
			var contentScrolled = elementById(scrolledContentId); 
			if (scroller && contentScrolled) {
				refreshPlaceholders(scroller, contentScrolled);
			}
			refreshLoader(scrollerId, scrolledContentId);
		}
		
		function refreshPlaceholders(scroller, contentScrolled) {
			var contentLoaderHeight = scroller.offsetHeight;
			var contentScrolledHeight = contentScrolled.offsetHeight; 
			
			var placeHolderHeight = getPlaceHolderHeight(contentScrolled); 
			var loadedRowHeight = getLoadedRowHeight(contentScrolled); 
			
			var minPosition = scroller.scrollTop - placeHolderHeight + loadedRowHeight; 
			var maxPosition = minPosition + placeHolderHeight + contentLoaderHeight - loadedRowHeight;
			
			clickPlaceholdersWithin(contentScrolled, minPosition, maxPosition);
		}
		
		function clickPlaceholdersWithin(contentScrolled, minPosition, maxPosition) {	
			var allcontentRows = getAllRows(contentScrolled);		
			for (var i = 0;allcontentRows.length > i; i++) {
				var row = allcontentRows[i];
				if (row.className == 'loader-placeholder') {
					var trPosition = row.offsetTop;
					if(trPosition > minPosition && trPosition < maxPosition){
						row.onclick();
					}
				}
		    }
		}
		
		function getPlaceHolderHeight(contentScrolled){
			return getElementHeight(contentScrolled, 'loader-placeholder')
		}
		
		function getLoadedRowHeight(contentScrolled){
			return getElementHeight(contentScrolled, 'loaded-row')
		}
		
		function getElementHeight(contentScrolled, className) {
			var allcontentRows = getAllRows(contentScrolled);
			for (var i = 0;allcontentRows.length > i; i++){
		        if(allcontentRows[i].className.indexOf(className) > -1){
		           return allcontentRows[i].offsetHeight;
		        }
		    }
		}
		
		function getAllRows(contentScrolled) {
			// TODO: reference to 'tr' is not generic enough
			return contentScrolled.getElementsByTagName('tr');
		}
		
		function error(text) {
			if (console) {
				console.log(text);
			}
		}

		function elementById(id) {
			var element = document.getElementById(id);
			if (!element) {
				error('Element ' + id + ' not found from page.');
			}
			return element;
		}