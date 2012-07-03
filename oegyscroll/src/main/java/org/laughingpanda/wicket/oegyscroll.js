function OegyScroll(blockSize, height, rowHeight, id) {
	this.height = height;
	this.rowHeight = rowHeight;
	this.blockSize = blockSize;
	this.id = (id) ? id : "scroller";
	this.contentId = "content";
}	
OegyScroll.prototype.createBlock = function (contentCreator) {
	var block = (function($) { return $("<tbody></tbody>").addClass("block"); })(jQuery);
	contentCreator(block);
	block.appendTo(this.contentArea);
	return block;
}
OegyScroll.prototype.createPlaceholder = function (id, content) {
	return (function($) { 
		return $('<tr id="' + id + '" class="loader-placeholder" style="height: ' + (this.blockSize * this.rowHeight) + 'px">'+content+'</tr>'); 
		})(jQuery); 
};
OegyScroll.prototype.generateMarkup = function() {
	this.main = (function($) { 
		return $('<div style="height:'+this.height+'px;overflow:auto;border:1px solid #ccc;"></div>');
	})(jQuery);
	this.main.attr("id", this.id);	
	this.scrollable = (function($) { 
		return $('<div class="scrollable-area"></div>').appendTo(this.main); 
		})(jQuery);
	this.contentArea = (function($) { 
		return $('<table id="' + this.contentId + '"></table>').appendTo(this.scrollable);
	})(jQuery);
}	
OegyScroll.prototype.createRow = function (content) {
	return (function($) { 
		return $('<tr class="loaded-row" style="height: ' + this.rowHeight + 'px">' + content + '</tr>'); 
		})(jQuery);
}
OegyScroll.prototype.createPlaceHolderBlock = function(id, content, onclickFunction) {
	var placeHolder = this.createPlaceholder(id, content);		
	var block = this.createBlock(function(block){
		placeHolder.appendTo(block)
	});
	placeHolder.get(0).onclick=onclickFunction;
	return block;
}
OegyScroll.prototype.checkInit = function() {
	if (!this.main) {
		this.main=(function($) { 
			return $("#" + this.id);
		})(jQuery);
		if (this.main.size() > 0) {
			this.contentArea=(function($) { 
				return $("#" + this.contentId, this.main);
			})(jQuery);
		} else {
			this.generateMarkup();
		}
	}
}
OegyScroll.prototype.appendTo=function(body) {
	this.checkInit();
	this.main.appendTo(body);
}
OegyScroll.prototype.init = function(rowCount, rowFetcher, placeHolderContent) {
	this.initFetchByBlock(rowCount, function(oegy, block, start, end) {
		for (row = start; row < end; row++) {
			oegy.createRow(rowFetcher(row)).attr("id", "row-" + (row+1)).appendTo(block);
		}
	}, placeHolderContent);
}
OegyScroll.prototype.initFetchByBlock = function(rowCount, blockFetcher, placeHolderContent) {
	this.checkInit();
	this.contentArea.empty();
	var oegy = this;
	this.remainder = rowCount % this.blockSize;
	if (this.remainder == 0) this.remainder = this.blockSize;
	this.createBlock(function(block) {
		oegy.appendRowsToBlock(block, 0, oegy.remainder, blockFetcher);
	});
	this.blockCount = (rowCount - this.remainder) / this.blockSize;
	for (i = 0; i < this.blockCount; i++) {
		this.createAutomaticPlaceholder(i, blockFetcher, placeHolderContent);
	}
}
OegyScroll.prototype.createAutomaticPlaceholder = function(blockNumber, blockFetcher, content) {
	var oegy = this;
	this.createPlaceHolderBlock('placeholder-' + blockNumber, content, function() {
		oegy.replacePlaceholderWithData($(this), blockNumber, blockFetcher);
	});
}
OegyScroll.prototype.replacePlaceholderWithData = function(placeholder, blockNumber, blockFetcher) {
	block = placeholder.parent();
	offset = blockNumber * this.blockSize;
	start = offset + this.remainder;
	end = offset + this.blockSize + this.remainder;
	this.appendRowsToBlock(block, start, end, blockFetcher);
	placeholder.remove();
}
OegyScroll.prototype.appendRowsToBlock = function(block, start, end, blockFetcher) {
	blockFetcher(this, block, start, end);
}
OegyScroll.prototype.scrollTo = function(yPos) {
	this.main.attr('scrollTop', yPos);
};		
OegyScroll.prototype.refresh = function() {
	this.updater().checkScrollPosition();
}
OegyScroll.prototype.autorefresh = function() {
	this.updater().checkScrollPositionRepeatedly();
}
OegyScroll.prototype.updater = function() {
	return new OegyScrollUpdater(this.id, this.contentId);
}