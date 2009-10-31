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
function createPlaceholder(id) {
	return $('<tr id="' + id + '" class="loader-placeholder" style="height: ' + this.blockHeight + 'px"><td>Placeholder ' + id + '</td></tr>');
};
function createRow(text) {
	return $('<tr class="loaded-row" style="height: ' + this.rowHeight + 'px"><td>' + text + '</td></tr>');
};	
function createPlaceHolderBlock(id, onclickFunction) {
	var placeHolder = this.createPlaceholder(id);		
	var block = this.createBlock(function(block){
		placeHolder.appendTo(block)
	});
	placeHolder.get(0).onclick=onclickFunction;
	return block;
};						
