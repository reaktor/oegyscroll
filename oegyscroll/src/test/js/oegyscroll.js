function OegyScroll(height, rowHeight, blockSize) {
	this.height = height;
	this.rowHeight = rowHeight;
	this.blockSize = blockSize;
	this.blockHeight = blockSize * rowHeight;
	this.main = $('<div style="height:'+height+'px;overflow:auto;border:1px solid #ccc;"></div>');
	this.main.attr("id", "scroller");	
	this.scrollable = $('<div class="scrollable-area"></div>').appendTo(this.main);
	this.contentArea = $('<table id="content"></table>').appendTo(this.scrollable);
	this.createBlock = createBlock;
	this.createPlaceholder = createPlaceholder;
	this.createRow = createRow;
	this.createPlaceHolderBlock = createPlaceHolderBlock;
	this.createAutomaticPlaceholder = createAutomaticPlaceholder;	
	this.scrollTo=scrollTo;	
	this.init = init;	
	this.refresh = refresh;	
	this.autorefresh = autorefresh;		
}	
function createBlock(contentCreator) {
	var block = $("<tbody></tbody>")
	contentCreator(block);
	block.appendTo(this.contentArea);
	return block;
};
function createPlaceholder(id, content) {
	return $('<tr id="' + id + '" class="loader-placeholder" style="height: ' + this.blockHeight + 'px">'+content+'</tr>');
};
function createRow(content) {
	return $('<tr class="loaded-row" style="height: ' + this.rowHeight + 'px">' + content + '</tr>');
};	
function createPlaceHolderBlock(id, content, onclickFunction) {
	var placeHolder = this.createPlaceholder(id, content);		
	var block = this.createBlock(function(block){
		placeHolder.appendTo(block)
	});
	placeHolder.get(0).onclick=onclickFunction;
	return block;
};
function init(rowCount, rowFetcher, placeHolderContent) {
	var oegy = this;
	this.remainder = rowCount % this.blockSize;
	if (this.remainder == 0) this.remainder = this.blockSize;
	this.createBlock(function(block) {
		appendRowsToBlock(oegy, block, 0, oegy.remainder, rowFetcher);
	});
	this.blockCount = (rowCount - this.remainder) / this.blockSize;
	for (i = 0; i < this.blockCount; i++) {
		this.createAutomaticPlaceholder(i, rowFetcher, placeHolderContent);
	}
}
function createAutomaticPlaceholder(blockNumber, rowFetcher, content) {
	var oegy = this;
	this.createPlaceHolderBlock('placeholder-' + blockNumber, content, function() {
		replacePlaceholderWithData(oegy, $(this), blockNumber, rowFetcher);
	});
}

function replacePlaceholderWithData(oegy, placeholder, blockNumber, rowFetcher) {
	block = placeholder.parent();
	offset = blockNumber * oegy.blockSize;
	start = offset + oegy.remainder;
	end = offset + oegy.blockSize + oegy.remainder;
	appendRowsToBlock(oegy, block, start, end, rowFetcher);
	placeholder.remove();
}

function appendRowsToBlock(oegy, block, start, end, rowFetcher) {
	for (row = start; row < end; row++) {
		oegy.createRow(rowFetcher(row)).attr("id", "row-" + (row+1)).appendTo(block);
	}
}
function scrollTo(yPos) {
	this.main.attr('scrollTop', yPos);
};		

function refresh() {
	refreshPlaceholders(this.main.get(0), this.contentArea.get(0));
}

function autorefresh() {
	updateRepeatedly(this.main.attr("id"));
}

function updateRepeatedly(id) {
	main = $("div#" + id);
	contentArea = $("table#content", main);
	refreshPlaceholders(main.get(0), contentArea.get(0));
	setTimeout('updateRepeatedly("' + id + '")', 1000);
}