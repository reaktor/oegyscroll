function OegyScroll(height, rowHeight, blockSize) {
	this.height = height;
	this.rowHeight = rowHeight;
	this.blockSize = blockSize;
	this.blockHeight = blockSize * rowHeight;
	this.main = $('<div id="scroller" style="height:'+height+'px;overflow:auto;border:1px solid #ccc;"></div>');
	this.scrollable = $('<div class="scrollable-area"></div>').appendTo(this.main);
	this.contentArea = $('<table id="content"></table>').appendTo(this.scrollable);
	this.createBlock = createBlock;
	this.createPlaceholder = createPlaceholder;
	this.createRow = createRow;
	this.createPlaceHolderBlock = createPlaceHolderBlock;						
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
