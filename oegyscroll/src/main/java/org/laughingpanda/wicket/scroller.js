		initContentLoader("${scrollerId}", "${scrolledContentId}");		
	 	
	 	function initContentLoader(scrollerId, scrolledContentId){
			refreshLoader(scrollerId, scrolledContentId);
		}
		
		function refreshLoader(scrollerId, scrolledContentId){
			setTimeout('checkScrollPosition("' + scrollerId+ '", "' + scrolledContentId + '")', 1000);
		}
		
		function checkScrollPosition(scrollerId, scrolledContentId){			
			var scroller = document.getElementById(scrollerId); 
			if (!scroller) {
				error(scrollerId + ' not found');
			}
			var contentLoaderHeight = scroller.offsetHeight;
			
			var contentScrolled = document.getElementById(scrolledContentId); 
			if (!contentScrolled) {
				error(scrolledContentId + ' not found');
			}
			var contentScrolledHeight = contentScrolled.offsetHeight; 
			
			var placeHolderHeight = getPlaceHolderHeight(contentScrolled); 
			var loadedRowHeight = getLoadedRowHeight(contentScrolled); 
			
			scrollPosition = scroller.scrollTop;
			
			var minPosition = scrollPosition - placeHolderHeight + loadedRowHeight; 
			var maxPosition = minPosition + placeHolderHeight + contentLoaderHeight - loadedRowHeight;
			
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
			refreshLoader(scrollerId, scrolledContentId);
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
		           var loadedRowHeight = allcontentRows[i].offsetHeight;
		           return loadedRowHeight;
		        }
		    }
		}
		
		function getAllRows(contentScrolled) {
			// TODO: reference to 'tr' is not generic enough
			return contentScrolled.getElementsByTagName('tr');
		}
		
		function error(text) {
			alert(text);
		}
