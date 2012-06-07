function OegyScrollUpdater(scrollerId, scrolledContentId) {
	this.scrollerId = scrollerId;
	this.scrolledContentId = scrolledContentId;
}

OegyScrollUpdater.prototype.scheduleScrollPositionUpdate = function() {
	setTimeout('new OegyScrollUpdater("' + this.scrollerId+ '", "' + this.scrolledContentId + '").checkScrollPositionRepeatedly()', 1000);
}

OegyScrollUpdater.prototype.checkScrollPositionRepeatedly = function() {
	try {
		this.checkScrollPosition();
	} catch (err) {
		this.error('Error occurred while updating scroller: ' + err);
	}
	this.scheduleScrollPositionUpdate();
}

OegyScrollUpdater.prototype.checkScrollPosition = function() {
	if (this.scroller() && this.contentScrolled()) {
		this.refreshPlaceholders();
	}
}

OegyScrollUpdater.prototype.scroller = function() {
	return this.elementById(document, this.scrollerId);
}

OegyScrollUpdater.prototype.contentScrolled = function() {
	return this.elementById(this.scroller(), this.scrolledContentId);
}

OegyScrollUpdater.prototype.refreshPlaceholders = function() {
	this.blokz = undefined;
	if (!this.getBlock(1)) return;
	for (blockIndex = this.getFirstVisibleBlock(); this.blockPosition(blockIndex) < this.scrollPos() + this.viewHeight() ; blockIndex++) {
		block = this.getBlock(blockIndex);
		placeholder = $(".loader-placeholder", $(block)).get(0);
		if (placeholder) {
			placeholder.onclick();
		}
	}
}

OegyScrollUpdater.prototype.viewHeight = function() {
	return this.scroller().offsetHeight;
}

OegyScrollUpdater.prototype.scrollPos = function() {
	return this.scroller().scrollTop;
}

OegyScrollUpdater.prototype.blockHeight = function() {
	return this.getBlock(1).offsetHeight;
}

OegyScrollUpdater.prototype.getBlock = function(index) {
	if (!this.blokz) {
		this.blokz = $(".block", this.contentScrolled());		
	}
	return this.blokz.get(index);
}

OegyScrollUpdater.prototype.blockPosition = function(index) {
	block = this.getBlock(index);
	if (block)
		return block.offsetTop;
}

OegyScrollUpdater.prototype.getFirstVisibleBlock = function() {
	return this.getFirstVisibleBlockStartingFrom(this.guessFirstVisibleBlock());
}

OegyScrollUpdater.prototype.getFirstVisibleBlockStartingFrom = function(from) {
	while (from > 0 && (!this.getBlock(from) || this.scrollPos() < this.blockPosition(from))) {
		from -= 1;
	}
	var addingActivated;
	while (this.scrollPos() > this.blockPosition(from) + this.blockHeight()) {
		from += 1;
		addingActivated = true;
	}
	if(addingActivated) {
		from -= 1;
	}
	return from;
}

OegyScrollUpdater.prototype.guessFirstVisibleBlock = function () {
	return Math.floor(Math.abs((this.scrollPos() - this.blockPosition(0)) / this.blockHeight()));
}

OegyScrollUpdater.prototype.error = function(text) {
	if (typeof(window["console"]) != "undefined") {
		console.log(text);
	}
}

OegyScrollUpdater.prototype.elementById = function(parent, id) {
	var element = $("#" + id, $(parent)).get(0);
	if (!element) {
		this.error('Element ' + id + ' not found from page.');
	}
	return element;
}