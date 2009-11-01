function OegyScrollUpdater(scrollerId, scrolledContentId) {
	this.scrollerId = scrollerId;
	this.scrolledContentId = scrolledContentId;
}

OegyScrollUpdater.prototype.scheduleScrollPositionUpdate = function() {
	setTimeout('new OegyScrollUpdater("' + this.scrollerId+ '", "' + this.scrolledContentId + '").checkScrollPositionRepeatedly()', 1000);
}

OegyScrollUpdater.prototype.checkScrollPositionRepeatedly = function() {
	this.checkScrollPosition();
	this.scheduleScrollPositionUpdate();
}

OegyScrollUpdater.prototype.checkScrollPosition = function() {
	this.scroller = this.elementById(document, this.scrollerId);
	this.contentScrolled = this.elementById(this.scroller, this.scrolledContentId); 
	if (this.scroller && this.contentScrolled) {
		this.refreshPlaceholders();
	}
}

OegyScrollUpdater.prototype.refreshPlaceholders = function() {
	var contentLoaderHeight = this.scroller.offsetHeight;
	var contentScrolledHeight = this.contentScrolled.offsetHeight;
	
	var placeHolderHeight = this.getPlaceHolderHeight(); 
	var loadedRowHeight = this.getLoadedRowHeight();
	
	var minPosition = this.scroller.scrollTop - placeHolderHeight + loadedRowHeight;
	var maxPosition = minPosition + placeHolderHeight + contentLoaderHeight - loadedRowHeight;
	
	this.clickPlaceholdersWithin(minPosition, maxPosition);
}

OegyScrollUpdater.prototype.clickPlaceholdersWithin = function(minPosition, maxPosition) {
	updater = this;
	this.forChildren(this.contentScrolled, function(element) {updater.clickIfPlaceHolder(element, minPosition, maxPosition)});
}

OegyScrollUpdater.prototype.clickIfPlaceHolder = function(row, minPosition, maxPosition) {
	if (this.hasClass(row, 'loader-placeholder')) {
		var rowPosition = row.offsetTop;
		if (rowPosition > minPosition && rowPosition < maxPosition) {
			row.onclick();
		}
	}	
}

OegyScrollUpdater.prototype.forChildren = function(parentElement, f) {
	var parentElementChildren = parentElement.childNodes;
	for (var i = 0; i < parentElementChildren.length; i++) {
		var child = parentElementChildren[i];
		f(child);
		this.forChildren(child, f);
	}
}

OegyScrollUpdater.prototype.getPlaceHolderHeight = function() {
	return this.getElementHeight(this.contentScrolled, 'loader-placeholder');
}

OegyScrollUpdater.prototype.getLoadedRowHeight = function() {
	return this.getElementHeight(this.contentScrolled, 'loaded-row');
}

OegyScrollUpdater.prototype.getElementHeight = function(container, className) {
	var contentScrolledChildren = container.childNodes;
	for (var i = 0; i < contentScrolledChildren.length; i++) {
		var child = contentScrolledChildren[i];		
		if (this.hasClass(child, className)) {
			return child.offsetHeight;
		}
		var found = this.getElementHeight(child, className);
		if (!isNaN(found)) {
			return found;
		}
	}
	return NaN;
}

OegyScrollUpdater.prototype.error = function(text) {
	if (typeof(window["console"]) != "undefined") {
		console.log(text);
	}
}

OegyScrollUpdater.prototype.hasClass = function(element, className) {
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

OegyScrollUpdater.prototype.elementById = function(parent, id) {
	var element = $("#" + id, $(parent)).get(0);
	if (!element) {
		this.error('Element ' + id + ' not found from page.');
	}
	return element;
}