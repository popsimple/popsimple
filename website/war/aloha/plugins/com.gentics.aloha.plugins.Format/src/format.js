/*
* Aloha Editor
* Author & Copyright (c) 2010 Gentics Software GmbH
* aloha-sales@gentics.com
* Licensed unter the terms of http://www.aloha-editor.com/license.html
*/
(function(window,undefined){var jQuery=window.alohaQuery||window.jQuery,$=jQuery,GENTICS=window.GENTICS,Aloha=window.Aloha;Aloha.Format=new (Aloha.Plugin.extend({_constructor:function(){this._super("format")},languages:["en","de","fr","eo","fi","ru","it","pl"],config:["strong","b","i","del","sub","sup","p","h1","h2","h3","h4","h5","h6","pre","removeFormat"],init:function(){var me=this;this.initButtons();Aloha.bind("aloha-editable-activated",function(e,params){me.applyButtonConfig(params.editable.obj)})},applyButtonConfig:function(obj){var config=this.getEditableConfig(obj),button,i;for(button in this.buttons){if(jQuery.inArray(button,config)!=-1){this.buttons[button].button.show()}else{this.buttons[button].button.hide()}}for(i in this.multiSplitItems){if(jQuery.inArray(this.multiSplitItems[i].name,config)!=-1){this.multiSplitButton.showItem(this.multiSplitItems[i].name)}else{this.multiSplitButton.hideItem(this.multiSplitItems[i].name)}}},initButtons:function(){var scope="Aloha.continuoustext",that=this;this.buttons={};this.multiSplitItems=[];jQuery.each(Aloha.Format.config,function(j,button){switch(button){case"b":case"i":case"cite":case"q":case"code":case"abbr":case"del":case"strong":case"sub":case"sup":that.buttons[button]={button:new Aloha.ui.Button({iconClass:"aloha-button aloha-button-"+button,size:"small",onclick:function(){var markup=jQuery("<"+button+"></"+button+">"),rangeObject=Aloha.Selection.rangeObject,foundMarkup;if(Aloha.activeEditable){Aloha.activeEditable.obj[0].focus()}foundMarkup=rangeObject.findMarkup(function(){return this.nodeName.toLowerCase()==markup.get(0).nodeName.toLowerCase()},Aloha.activeEditable.obj);if(foundMarkup){if(rangeObject.isCollapsed()){GENTICS.Utils.Dom.removeFromDOM(foundMarkup,rangeObject,true)}else{GENTICS.Utils.Dom.removeMarkup(rangeObject,markup,Aloha.activeEditable.obj)}}else{if(rangeObject.isCollapsed()){GENTICS.Utils.Dom.extendToWord(rangeObject)}GENTICS.Utils.Dom.addMarkup(rangeObject,markup)}rangeObject.select();return false},tooltip:that.i18n("button."+button+".tooltip"),toggle:true}),markup:jQuery("<"+button+"></"+button+">")};Aloha.FloatingMenu.addButton(scope,that.buttons[button].button,Aloha.i18n(Aloha,"floatingmenu.tab.format"),1);break;case"p":case"h1":case"h2":case"h3":case"h4":case"h5":case"h6":case"pre":that.multiSplitItems.push({name:button,tooltip:that.i18n("button."+button+".tooltip"),iconClass:"aloha-button "+that.i18n("aloha-button-"+button),markup:jQuery("<"+button+"></"+button+">"),click:function(){if(Aloha.activeEditable){Aloha.activeEditable.obj[0].focus()}Aloha.Selection.changeMarkupOnSelection(jQuery("<"+button+"></"+button+">"))}});break;case"removeFormat":that.multiSplitItems.push({name:button,text:that.i18n("button."+button+".text"),tooltip:that.i18n("button."+button+".tooltip"),iconClass:"aloha-button aloha-button-"+button,wide:true,click:function(){Aloha.Format.removeFormat()}});break;default:Aloha.log("warn",this,'Button "'+button+'" is not defined');break}});if(this.multiSplitItems.length>0){this.multiSplitButton=new Aloha.ui.MultiSplitButton({items:this.multiSplitItems});Aloha.FloatingMenu.addButton(scope,this.multiSplitButton,Aloha.i18n(Aloha,"floatingmenu.tab.format"),3)}Aloha.bind("aloha-selection-changed",function(event,rangeObject){var statusWasSet=false,effectiveMarkup,foundMultiSplit,i,j,multiSplitItem;jQuery.each(that.buttons,function(index,button){statusWasSet=false;for(i=0;i<rangeObject.markupEffectiveAtStart.length;i++){effectiveMarkup=rangeObject.markupEffectiveAtStart[i];if(Aloha.Selection.standardTextLevelSemanticsComparator(effectiveMarkup,button.markup)){button.button.setPressed(true);statusWasSet=true}}if(!statusWasSet){button.button.setPressed(false)}});if(that.multiSplitItems.length>0){foundMultiSplit=false;for(i=0;i<rangeObject.markupEffectiveAtStart.length&&!foundMultiSplit;i++){effectiveMarkup=rangeObject.markupEffectiveAtStart[i];for(j=0;j<that.multiSplitItems.length&&!foundMultiSplit;j++){multiSplitItem=that.multiSplitItems[j];if(!multiSplitItem.markup){continue}if(Aloha.Selection.standardTextLevelSemanticsComparator(effectiveMarkup,multiSplitItem.markup)){that.multiSplitButton.setActiveItem(multiSplitItem.name);foundMultiSplit=true}}}if(!foundMultiSplit){that.multiSplitButton.setActiveItem(null)}}})},removeFormat:function(){var formats=["strong","b","i","cite","q","code","abbr","del","sub","sup"],rangeObject=Aloha.Selection.rangeObject,i;if(rangeObject.isCollapsed()){return}for(i=0;i<formats.length;i++){GENTICS.Utils.Dom.removeMarkup(rangeObject,jQuery("<"+formats[i]+"></"+formats[i]+">"),Aloha.activeEditable.obj)}rangeObject.select()},toString:function(){return"format"}}))()})(window);