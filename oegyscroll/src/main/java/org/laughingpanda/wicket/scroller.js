function initContentLoader(scrollerId, scrolledContentId) {
	scheduleScrollPositionUpdate(scrollerId, scrolledContentId);
}

function scheduleScrollPositionUpdate(scrollerId, scrolledContentId) {
	setTimeout('checkScrollPosition("' + scrollerId+ '", "' + scrolledContentId + '")', 1000);
}

function checkScrollPosition(scrollerId, scrolledContentId) {
	var scroller = elementById(scrollerId);
	var contentScrolled = elementById(scrolledContentId); 
	if (scroller && contentScrolled) {
		refreshPlaceholders(scroller, contentScrolled);
	}
	scheduleScrollPositionUpdate(scrollerId, scrolledContentId);
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
	forChildren(contentScrolled, function(element) {clickIfPlaceHolder(element, minPosition, maxPosition)});
}

function clickIfPlaceHolder(row, minPosition, maxPosition) {
	if (hasClass(row, 'loader-placeholder')) {
		var rowPosition = row.offsetTop;
		if (rowPosition > minPosition && rowPosition < maxPosition) {
			row.onclick();
		}
	}	
}

function forChildren(parentElement, f) {
	var parentElementChildren = parentElement.childNodes;
	for (var i = 0; i < parentElementChildren.length; i++) {
		var child = parentElementChildren[i];
		f(child);
		forChildren(child, f);
	}
}

function getPlaceHolderHeight(contentScrolled) {
	return getElementHeight(contentScrolled, 'loader-placeholder');
}

function getLoadedRowHeight(contentScrolled) {
	return getElementHeight(contentScrolled, 'loaded-row');
}

function getElementHeight(contentScrolled, className) {
	var contentScrolledChildren = contentScrolled.childNodes;
	for (var i = 0; i < contentScrolledChildren.length; i++) {
		var child = contentScrolledChildren[i];		if (hasClass(child, className)) {
			return child.offsetHeight;
		}
		var found = getElementHeight(child, className);
		if (!isNaN(found)) {
			return found;
		}
	}
	return NaN;
}

function error(text) {
	if (typeof(window["console"]) != "undefined") {
		console.log(text);
	}
}

function hasClass(element, className) {
	if (element.className) {
		var classes = element.className.split(' ');
		for (var i = 0; i < classes.length; i++) {
			if (classes[i] == className) {
				return true;
			}
		}
	}
	return false;
}

function elementById(id) {
	var element = document.getElementById(id);
	if (!element) {
		error('Element ' + id + ' not found from page.');
	}
	return element;
}