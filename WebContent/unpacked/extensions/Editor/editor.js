/*************************************************************
 *
 *  MathJax/extensions/Editor/editor.js
 *
 *  Allows editing of equations in MatJax output
 *  
 *  ---------------------------------------------------------------------
 *  
 *  Copyright (c) 2012 - 2013 Evgeny Savel'ev.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

(function (HUB,HTML,AJAX,CALLBACK) 
{
	var VERSION = "0.1";

	var EXTENSION = MathJax.Extension;
	var ED = EXTENSION.Editor = 
		{
			version: VERSION,
			directory : "[MathJax]/extensions/Editor/",
			
			config : 
				{
					OutputJax : "HTML-CSS",
					blinkDelay : 550
				},
			
			Config : function ()
				{
					document.addEventListener('click',ED.Event.documentClick,true);
					
					ED.Event.focusObj.blinkerSpan = HTML.Element("span", {style : {display:"inline-block", visibility : "hidden", width: "1px", position:"absolute", backgroundColor : "black"}});
				}
		};

	var SETTINGS = HUB.config.EditorSettings;

	var EVENT = ED.Event = {
		LEFTBUTTON: 0,           // the event.button value for left button
		RIGHTBUTTON: 2,          // the event.button value for right button
		MENUKEY: "altKey",       // the event value for alternate context menu
		
		MOUSEX : "clientX",
		MOUSEY : "clientY",

		Keydown: 	function (event) {return EVENT.Handler(event,"Keydown",this)},
		Keyup:		function (event) {return EVENT.Handler(event,"Keyup",this)},
		Keypress:	function (event) {return EVENT.Handler(event,"Keypress",this)},
		
		Mousedown:	function (event) {return EVENT.Handler(event,"Mousedown",this)},
		Mouseup:	function (event) {return EVENT.Handler(event,"Mouseup",this)},
		Mousemove:	function (event) {return EVENT.Handler(event,"Mousemove",this)},
		Mouseover:	function (event) {return EVENT.Handler(event,"Mouseover",this)},
		Mouseout:	function (event) {return EVENT.Handler(event,"Mouseout",this)},
		Click:		function (event) {return EVENT.Handler(event,"Click",this)},
		DblClick:	function (event) {return EVENT.Handler(event,"DblClick",this)},
		Menu:		function (event) {return EVENT.Handler(event,"ContextMenu",this)},

		Handler : function (event,type,math) {
		  if (!event) {event = window.event};
		  if (ED.Event["Process"+type]) {return ED.Event["Process"+type].call(ED.Event,event,math)};
		},
		
		False: function (event) 
		{
			if (!event) {event = window.event}
			if (event) 
			{
				if (event.preventDefault) {event.preventDefault()}
				if (event.stopPropagation) {event.stopPropagation()}
				event.cancelBubble = true;
				event.returnValue = false;
			}
			return false;
		},
		
		ProcessClick : function (evt,math) 
			{
				if ((evt.altKey)||(evt.ctrlKey)||(evt.metaKey))
				{//Process clicks with modifiers
				}
				else
				{//Just a click. Mark an mrow as selected and show the blinker
					this.focusObj.blur();
					
					var span = evt.target, spanMRow = (evt.currentTarget)?(evt.currentTarget):(math), side = -1,
						jax = this.focusObj.jax = HUB.getJaxFor(span), def,
						spanRect = span.getBoundingClientRect();
					
					span = this.checkSpan(span);
					
					if (((evt[this.MOUSEX] - spanRect.left)/spanRect.width)>1/2)
					{
						side = 1;
					}
					
					def = this.findMMLelements(span,spanMRow,jax,side);
					this.focusObj.setData(def);
					
					if(this.focusObj.inputField)
					{
						this.focusObj.startBlinking(span, spanMRow, side);
					}
					
					return this.False(evt);
				}
			},
		
		ProcessKeydown : function (evt,math)
			{
				if (this.focusObj.inputField)//Should be always true as we add and remove listeners when changing the focus.
				{
					var def = {}, code=evt.keyCode;//which button was pressed
				
					if (code==39) //Right arrow. TODO: process arrows with modifiers, like [shift] + [->] or [Ctrl] + [->]
					{
						if (this.focusObj.toLeft)
						{
							def = this.focusObj.toLeft.parent.focusOutToRightFromChild(this.focusObj.toLeft,def)
						}
						else if (this.focusObj.toRight)
						{
							def = this.focusObj.toRight.focusLeftToRight(this.focusObj.toRight,def);
						}
						else {def = this.focusObj.mRow.focusOutToRight(this.focusObj.mRow,def)}

						this.focusObj.setData (def);
						this.focusObj.repositionBlinker();
						return this.False(evt);
					}
					else if (code==37) //Left arrow. TODO: process arrows with modifiers, like [shift] + [<-] or [Ctrl] + [<-]
					{
						if (this.focusObj.toRight)
						{
							def = this.focusObj.toRight.parent.focusOutToLeftFromChild(this.focusObj.toRight,def)
						}
						else if (this.focusObj.toLeft)
						{
							def = this.focusObj.toLeft.focusRightToLeft(this.focusObj.toLeft,def);
						}
						else {def = this.focusObj.mRow.focusOutToLeft(this.focusObj.mRow,def)}

						this.focusObj.setData (def);
						this.focusObj.repositionBlinker();
						return this.False(evt);
					}
				}
			},
		
		documentClick : function (evt)
			{
				if((evt)&&(evt.target))
				{
					if(!evt.target.isMathJax)
					{
						if(ED.Event.focusObj.mRow)
						{
							ED.Event.focusObj.blur();
							return ED.Event.False(evt);
						}
					}
					else
					{
						var iterator = evt.target;
						while (iterator)
						{
							if(iterator.className == "math") {iterator = null;}
							else if((iterator.EDisInputField)&&(iterator.EDisInputField==true)) {return;}
							else {iterator = iterator.parentNode;}
						}
						if(ED.Event.focusObj.mRow)
						{
							ED.Event.focusObj.blur();
							return ED.Event.False(evt);
						}
					}
				}
			},
		highlightingObj :
			{// This object holds and proceses events that simulate highlighting and drag and drop activities.
				highlightedNodes	:[],
				inProgress			:false,
				timer				:null,
				
				start				:null,//Where did the selection start
				startside			:0,//On what side of the object it did 
				startTree			:[],//complete ancestry line to the top \inputfield
				end					:null,//Current object receiving mouseover events
				endside				:0,//on which side mouse hovers
				endTree				:[],//complete ancestry line to the top \inputfield
				
				topleft				:null,//leftmost unique parent element
				topleftIndex		:-1,
				topright			:null,//rightmost unique parent element
				toprightIndex		:-1,
				topparent			:null,//least common ancestor
				
				draggingOn			:false,
				draggingTimer		:null,
				startScreenX		:0,
				startScreenY		:0,
				source				:null,
	
				insertedObj			:null,
				
				setData : function (data)
					{
						for (var id in data)
						{
							if (data.hasOwnProperty(id)) 
							{
								if(this[id])
								{
									if (this[id]!=data[id])
									{
										this["clear"+id]();
										this["set"+id](data[id]);
									}
								}
								else this["set"+id](data[id]);
							}
						}
					},
				
				findLCA :	function () //Finds least common ancestor of highlighted MML elements. Returns null if elements are not in a common \inputfield
					{
						var def = null;
						if (this.start == this.end) 
						{
							if(this.startside!=this.endside)
							{
								var item = this.start;
								def=
									{
										startTree 	: [item], 
										endTree 	: [item], 
										topleft 	: item,
										topleftIndex: 0,
										topright 	: item,
										toprightIndex: 0,
										topparent 	: item.parent
									};
							}	
							return def; //just to speed things up in the beginning of selection
						}
						
						var parent1list=[],parent2list=[];
	
						var parentnode=this.start, mathRoot = null, l1,l2,i=1;
	
						while (!parentnode.EDisInputField)
						{// loop through all ancestors of start
							parentnode=parentnode.parent;
							parent1list.push(parentnode);
						}
						parentnode=this.end;
						while (!parentnode.EDisInputField)
						{// loop through all ancestors of end
							parentnode=parentnode.parent;
							parent2list.push(parentnode);
						}
	
						l1=parent1list.length;
						l2=parent2list.length;
	
						while ((parent1list[l1-i]==parent2list[l2-i])&&(i<=l1)&&(i<=l2))
						{
							mathRoot=parent1list[l1-i];
							i++;
						}
						
						def = 
							{
								startTree 	: parent1list, 
								endTree 	: parent2list, 
								topleft 	: parent1list[l1-i],
								topleftIndex: l1-i,
								topright 	: parent2list[l2-i],
								toprightIndex: l2-i,
								topparent 	: item
							};
	
						return def;
					},
					
				highlightNodes : function ()
					{
						if(this.topparent)
						{
							var indexes = this.topparent.getChildIndexes(this.topleft,this.topright),
								l,r,direction = 1;
							
							l=indexes[0];
							r=indexes[1];
							
							if((l>-1)&&(r>-1))
							{
								if(l>r)
								{//switch nodes
									direction =-1;
									
								}
							}
						} 
					}
			},
			
		focusObj :
			{
				Init : function ()
					{
						this.suspendBlinking();
						
						
						this.clearjax();
						this.cleartoLeft();
						
						this.cleartoRight();
						
						this.clearmRow();
						
						this.clearinputField();
						
						this.clearblinkTimer();
						this.clearinsertAt();
				
						this.clearclickSpan();
						this.clearclickSide();
					},
					
				clearjax		: function (){	this.jax			= null},
				setjax			: function (jax){this.jax			= jax},
				clearinsertAt	: function (){	this.insertAt		= null},
				setinsertAt		: function (insertAt){this.insertAt= insertAt},
				clearclickSpan	: function (){	this.clickSpan		= null},
				setclickSpan	: function (clickSpan){this.clickSpan= clickSpan},
				clearclickSide	: function (){	this.clickSide		= 0},
				setclickSide	: function (clickSide){this.clickSide= clickSide},
				clearblinkTimer	: function (){	this.blinkTimer		= null},
				cleartoLeft		: function (){	this.toLeft			= null; if (this.toLeftSpan) {this.toLeftSpan.removeAttribute("lefthand")};this.toLeftSpan	= null;},
				settoLeft		: function (toLeft){this.toLeft	= toLeft; if (toLeft && toLeft.spanID) 
						{this.toLeftSpan 	= document.getElementById("MathJax-Span-"+this.toLeft.spanID);if (this.toLeftSpan) {this.toLeftSpan.setAttribute("lefthand","true")}}},
				cleartoRight	: function (){	this.toRight		= null; if (this.toRightSpan) {this.toRightSpan.removeAttribute("righthand")};this.toRightSpan	= null;},
				settoRight		: function (toRight){this.toRight	= toRight; if (toRight && toRight.spanID) 
						{this.toRightSpan 	= document.getElementById("MathJax-Span-"+this.toRight.spanID);if (this.toRightSpan) {this.toRightSpan.setAttribute("righthand","true")}}},
				clearmRow		: function ()
					{
						if (this.mRow) {delete(this.mRow.extraAttributes.focused)};
						this.mRow = null;
						if (this.mRowSpan) {this.mRowSpan.removeAttribute("focused")};
						try {this.mRowSpan.removeChild(this.blinkerSpan);} catch(e){};
						this.mRowSpan = null;
					},
				setmRow			: function (mRow)
					{
						this.mRow = mRow;
						if (mRow && mRow.spanID) 				
						{
							this.mRowSpan 		= document.getElementById("MathJax-Span-"+this.mRow.spanID);
							if (this.mRowSpan) {this.mRowSpan.setAttribute("focused","true");}
							if (this.mRow.extraAttributes) {this.mRow.extraAttributes["focused"]=true;}
							else {this.mRow.extraAttributes = {focused:true};}
						}
					},
				clearinputField : function ()
					{
						if ( this.inputField) {delete(this.inputField.extraAttributes.selected)};
						this.inputField		= null;
						if (this.fieldSpan) 
						{
							try{this.fieldSpan.removeAttribute("selected");} catch(e){};
							try{document.removeEventListener('keydown',ED.Event.Keydown,true);} catch(e){};
						}
						this.fieldSpan		= null;
					},
				setinputField : function (inputField)
					{
						this.inputField = inputField;
						if (inputField && inputField.spanID) 	
						{
							this.fieldSpan 	= document.getElementById("MathJax-Span-"+this.inputField.spanID);
							if(this.fieldSpan)
							{
								this.fieldSpan.setAttribute("selected","true");
								document.addEventListener('keydown',ED.Event.Keydown,true);
							}
							if (this.inputField.extraAttributes) {this.inputField.extraAttributes["selected"]=true;}
							else {this.inputField.extraAttributes = {selected:true};};
						}
					},
				
				setData : function (data)
					{
						this.suspendBlinking();
						
						for (var id in data)
						{
							if (data.hasOwnProperty(id)) 
							{
								if(this[id])
								{
									if (this[id]!=data[id])
									{
										this["clear"+id]();
										this["set"+id](data[id]);
									}
								}
								else this["set"+id](data[id]);
							}
						}
					},
				
				blinkerSpan :null,
				
				revealBlinker : function ()
					{
						if (this.blinkerSpan) {this.blinkerSpan.style.visibility = "visible";}
						//span.style.display = "inline-block";
						this.blinkTimer=setTimeout(CALLBACK([this,this.hideBlinker]),ED.config.blinkDelay);
					},
					
				hideBlinker	: function ()
					{
						if (this.blinkerSpan) {this.blinkerSpan.style.visibility = "hidden";}
						//span.style.display = "none";
						this.blinkTimer=setTimeout(CALLBACK([this,this.revealBlinker]),ED.config.blinkDelay);
					},
				
				repositionBlinker : function ()
					{
						if (this.blinkerSpan)
						{
							if (this.mRowSpan != this.blinkerSpan.parentNode)
							{
								if (this.blinkerSpan.parentNode) {try {this.blinkerSpan.parentNode.removeChild(this.blinkerSpan);} catch(e){};}
							
								this.blinkerSpan.style.top = "0px";
								this.mRowSpan.appendChild(this.blinkerSpan);
							
								var mRowSpanRect = this.mRowSpan.getBoundingClientRect(),
									blinkerSpanRect = this.blinkerSpan.getBoundingClientRect();
							
								this.blinkerSpan.style.top = Math.round(mRowSpanRect.top - blinkerSpanRect.top).toString() + "px";
								this.blinkerSpan.style.height = Math.round(mRowSpanRect.height).toString() + "px";
							}
							
							this.blinkerSpan.style.left = "0px";
							var blinkerSpanRect = this.blinkerSpan.getBoundingClientRect(),
								mathElementRect = this.clickSpan.getBoundingClientRect();
							
							if(this.clickSide == 1)
							{
								this.blinkerSpan.style.left = String(Math.round(mathElementRect.right - blinkerSpanRect.right) +.5) + "px";
							}
							else if(this.clickSide == -1)
							{
								this.blinkerSpan.style.left = String(Math.round(mathElementRect.left - blinkerSpanRect.left) -.5) + "px";
							}
							else
							{
								var mRowSpanRect = this.mRowSpan.getBoundingClientRect();
								this.blinkerSpan.style.left = String(Math.round(mRowSpanRect.left - blinkerSpanRect.left+0.5*mRowSpanRect.width) -.5) + "px";
							}
							
							this.resumeBlinking();
							return true
						}
						return false
					},
				
				startBlinking: function ()
					{
						if (this.blinkerSpan)
						{
							return this.repositionBlinker();
						}
						return false
					},
				
				resumeBlinking : function ()
					{
						if (this.blinkTimer) {try {clearTimeout( this.blinkTimer );} catch(e){};}
						if (this.blinkerSpan) {this.blinkerSpan.style.visibility = "visible";}
						this.blinkTimer=setTimeout(CALLBACK([this,this.hideBlinker]),ED.config.blinkDelay);
					},
					
				suspendBlinking : function ()
					{
						if(this.blinkTimer)
						{
							try {clearTimeout( this.blinkTimer );} catch(e){};
							if (this.blinkerSpan) {this.blinkerSpan.style.visibility = "hidden";}
							this.blinkTimer = null;
						}
					},
				
				blur : function ()
					{
						this.Init();
					}
			},
			
		checkSpan	: function (span)
			{
				var iterator = span;
				while (iterator.id.indexOf("MathJax-Span-") == -1) {iterator = iterator.parentNode}
				return iterator;
			},
			
		findMMLelements : function (span, spanMRow, jax, side)
			{
				var iterator = jax.root, stack=[iterator],item, def = {clickSide : side, clickSpan : span},
					spanID = Number(span.id.replace("MathJax-Span-","")),
					spanMRowID = Number(spanMRow.id.replace("MathJax-Span-",""));
				
				if ((iterator)&&(iterator.spanID) && (iterator.spanID==spanMRowID))
				{// We have found the containing \inputfield
					def.inputField = iterator;
					def.mRow = iterator;
					def.insertAt = 0;
				}
				else
				{// iterate through the mml tree to find the \inputfield element
					while (stack.length>0)
					{
						iterator = stack.pop();
					
						if(iterator&&iterator.data)
						{
							for (var i=0, m=iterator.data.length;i<m;i++)
							{
								item = iterator.data[i];
							
								if (item && (item.spanID) && (item.spanID==spanMRowID))
								{
									def.inputField = item;
									def.mRow = item;
									def.insertAt = 0;
									iterator = item;
									stack = [];
									break;
								}
								else
								{
									stack.push(item);
								}
							}
						}
					}
				}
				
				if ((iterator.spanID) && (iterator.spanID==spanID))
				{// the click was on the \inputfield, try to figure out if we can focus on children
					if ((iterator.data)&&(iterator.data.length>0)) 
					{
						if(side == 1)
						{
							item = iterator.data[iterator.data.length-1];
							item.focusInFromRight(iterator,def);
						}
						else
						{
							item = iterator.data[0];
							item.focusInFromLeft(iterator,def);
						}
					}
					else 
					{// this is an empty mrow, blink in the center
						def.toRight = null;
						def.toLeft = null;
						def.clickSide = 0;
					}
				}
				else
				{
					stack=[iterator];
					while (stack.length>0)
					{
						iterator = stack.pop();
						
						if(iterator&&iterator.data)
						{
							for (var i=0, m=iterator.data.length;i<m;i++)
							{
								item = iterator.data[i];
							
								if (item&&(item.spanID) && (item.spanID==spanID))
								{
									if (side == 1)
									{
										item.focusInFromRight(iterator,def);
										if (i<m-1) {def.toRight = iterator.data[i+1];};
									}
									else
									{
										item.focusInFromLeft(iterator,def);
										if (i>0) {def.toLeft = iterator.data[i-1];};
									}
									def.mRow = item.findParentMRow();
									stack = [];
									break;
								}
								else
								{
									stack.push(item);
								}
							}
						}
					}
				}
			
				return def;
			}
	};
	
	MathJax.Hub.Register.StartupHook("onLoad",CALLBACK(ED.Config));
	
	AJAX.Require(ED.directory+"editor.css");
})(MathJax.Hub,MathJax.HTML,MathJax.Ajax,MathJax.Callback);

MathJax.Hub.Register.StartupHook("TeX Jax Ready",function () {
  
  var MML = MathJax.ElementJax.mml,
      TEX = MathJax.InputJax.TeX,
      HUB = MathJax.Hub;

  var TEXDEF = TEX.Definitions,
      STACKITEM = TEX.Stack.Item;
  
  TEXDEF.Add({
    macros: {
    	inputfield:	'InputField'
    }
  },null,true);

	var oldMmlData = STACKITEM.prototype.mmlData;//Overriden so that all TeX commands that take arguments produce mml elements with their children wrapped in mrow while inside \inputfield
	STACKITEM.Augment({
		mmlData : function (inferred,forceRow) 
		{
			if(this.env.inInputField)
			{
				return MML.mrow.apply(MML,this.data);
			}
			else
			{
				return oldMmlData.apply(this,arguments);
			}
		}
	});
						
	var oldOpenCheckItem = STACKITEM.open.prototype.checkItem;//Override so that TeXAtom is replaced with mrow while inside the \inputfield
	STACKITEM.open.Augment({
		checkItem: function (item) 
		{
			if(this.env.inInputField)
			{
				if (item.type === "close") 
				{
					var mml = this.mmlData(); // mmlData is ovverriden to always return an mrow
					return STACKITEM.mml(mml); // remove TeXAtom as it is not necessary and screws things up.
				}
				return this.SUPER(arguments).checkItem.call(this,item);
			}
			else
			{
				return oldOpenCheckItem.apply(this,arguments);
			}
		}
	});

	var oldSubsupCheckItem = STACKITEM.subsup.prototype.checkItem;//Override so that children are added wrapped in mrow while inside \inputfield
	STACKITEM.subsup.Augment({
		checkItem: function (item) 
		{
			if(this.env.inInputField)
			{
				var script = ["","subscript","superscript"][this.position];
				if (item.type === "open" || item.type === "left") {return true}
				if (item.type === "mml") 
				{
					if((item.data[0].isa(MML.mrow))&&(!item.data[0].EDisContainer))
					{
						this.data[0].SetData(this.position,item.data[0]);
					}
					else
					{
						this.data[0].SetData(this.position,MML.mrow(item.data[0]));
					}
					return STACKITEM.mml(this.data[0]);
				}
				if (this.SUPER(arguments).checkItem.call(this,item))
				{TEX.Error("Missing open brace for "+script)}
			}
			else
			{
				return oldSubsupCheckItem.apply(this,arguments);
			}
		}
	});

	var oldFnCheckItem = STACKITEM.fn.prototype.checkItem; //Override so that no "function application" mentity item is not added while inside \inputfield
	STACKITEM.fn.Augment({
		checkItem: function (item) 
		{
			if(this.env.inInputField)
			{
				if (this.data[0]) 
				{
					if (item.type !== "mml" || !item.data[0]) {return [this.data[0],item]}
					if (item.data[0].isa(MML.mspace)) {return [this.data[0],item]}
					var mml = item.data[0]; if (mml.isEmbellished()) {mml = mml.CoreMO()}
					if ([0,0,1,1,0,1,1,0,0,0][mml.Get("texClass")]) {return [this.data[0],item]}
					return [this.data[0],item];
				}
				return this.SUPER(arguments).checkItem.apply(this,arguments);
			}
			else
			{
				return oldFnCheckItem.apply(this,arguments);
			}
		}
	});
	
	TEX.Parse.Augment({
/*	mmlToken : function () {return arguments},*/
    InputField:	function(name)
				{
					//We want every complex element to create an mrow and numbers and function names be sequences of symbols
					//It is easier to augment TeX.Parse object and revert the changes at the end of processing the arguments of \inputfield
					//than to implement the same via stack because mml elemtnts don't get to see the stack.env
					var ParseAugmentData = ["Superscript","Subscript","Number","NamedFn"],ParseOld = {},
						fieldList, fieldEntry;
					
					if (!this.fieldList) {this.fieldList = fieldList = []} else {fieldList = this.fieldList}
					
					if (this.stack.env.inInputField){TEX.Error("Nested \\inputfield invocation.")}
					else {fieldEntry = {processing : true}; fieldList.push(fieldEntry)}
					
					
					for(var i=0, m=ParseAugmentData.length;i<m;i++)//Save the old definitions of TeX.Parse object
					{
						ParseOld[ParseAugmentData[i]] = TEX.Parse.prototype[ParseAugmentData[i]];
					}
					
					TEX.Parse.Augment({
							Superscript: function (c) 
								{
									if (this.GetNext().match(/\d/)) // don't treat numbers as a unit
									{
										this.string = this.string.substr(0,this.i+1)+" "+this.string.substr(this.i+1)
									}
									var position, base = this.stack.Prev();
									if (!base) {base = MML.mrow(MML.mi(""))}
									if (base.isEmbellishedWrapper) {base = base.data[0].data[0]}
									if (base.type === "msubsup") 
									{
										if (base.data[base.sup]) 
										{
											if (!base.data[base.sup].isPrime) {TEX.Error("Double exponent: use braces to clarify")}
											base = MML.msubsup(MML.mrow(base),null,null);
										}
										position = base.sup;
									} 
									else if (base.movesupsub) 
									{
										if (base.type !== "munderover" || base.data[base.over]) 
										{
											if (base.movablelimits && base.isa(MML.mi)) {base = MML.mrow(this.mi2mo(base))}
											if((base.isa(MML.mrow))&&(!base.EDisContainer))
											{
												base = MML.munderover(base,null,null).With({movesupsub:true})
											}
											else
											{
												base = MML.munderover(MML.mrow(base),null,null).With({movesupsub:true})
											}
										}
										position = base.over;
									} 
									else 
									{
										if((base.isa(MML.mrow))&&(!base.EDisContainer))
										{
											base = MML.msubsup(base,null,null);
										}
										else
										{
											base = MML.msubsup(MML.mrow(base),null,null);
										}
										position = base.sup;
										
									}
									this.Push(STACKITEM.subsup(base).With({position: position}));
								},
							Subscript: function (c) 
								{
									if (this.GetNext().match(/\d/)) // don't treat numbers as a unit
									{this.string = this.string.substr(0,this.i+1)+" "+this.string.substr(this.i+1)}
									var position, base = this.stack.Prev(); if (!base) {base = MML.mi("")}
									if (base.isEmbellishedWrapper) {base = base.data[0].data[0]}
									if (base.type === "msubsup") 
									{
										if (base.data[base.sub]) {TEX.Error("Double subscripts: use braces to clarify")}
										position = base.sub;
									} 
									else if (base.movesupsub) 
									{
										if (base.type !== "munderover" || base.data[base.under]) 
										{
											if (base.movablelimits && base.isa(MML.mi)) {base = MML.mrow(this.mi2mo(base))}
											
											if((base.isa(MML.mrow))&&(!base.EDisContainer))
											{
												base = MML.munderover(base,null,null).With({movesupsub:true})
											}
											else
											{
												base = MML.munderover(MML.mrow(base),null,null).With({movesupsub:true})
											}
										}
										position = base.under;
									} 
									else 
									{
										if((base.isa(MML.mrow))&&(!base.EDisContainer))
										{
											base = MML.msubsup(base,null,null);
										}
										else
										{
											base = MML.msubsup(MML.mrow(base),null,null);
										}
										position = base.sub;
									}
									this.Push(STACKITEM.subsup(base).With({position: position}));
								},
							NamedFn: function (name,id) 
								{
									if (!id) {id = name.substr(1)};
									var mml = MML.functionrow().With({fName:id});
									for (i=0,m=id.length;i<m;i++) {mml.Append(this.mmlToken(MML.mi(id.charAt(i))));}
									this.Push(STACKITEM.fn(mml));
								},
							Number: function (c) 
								{
									var mnrow = MML.numberrow(), mml, n = this.string.slice(this.i-1).match(TEXDEF.number);
									if (n) 
									{
										mml = [];
										n=n[0].replace(/[{}]/g,"");
										for (var i=0, m=n.length;i<m;i++)
										{
											mml.push(MML.mn(n[i]));
										}
										this.i += n.length - 1
									}
									else {mml = MML.mo(MML.chars(c))}
									if (this.stack.env.font) {mml.mathvariant = this.stack.env.font}
									
									if(mml instanceof Array)
									{
										for (var i=0, m= mml.length;i<m;i++) {mnrow.Append(this.mmlToken(mml[i]));}
										
									}
									else mnrow.Append(this.mmlToken(mml));
									
									this.Push(mnrow);
								}
						});
					
					this.stack.env.inInputField = true;
					var params = this.GetBrackets(name,""),
						math = this.ParseArg(name,{inInputField : true});
					// return to original mmlData function and TEXDEF
					TEX.Parse.Augment(ParseOld);
					
					var i=0,j ,parens=0, styleStrings = params.split('style');
					
					params = styleStrings.shift(); /* We process everything before the first appearance of 'style' later*/
/*					for (var string in styleStrings)
					{
						i=0;
						
						while (i < string.length) 
						{
							switch (params.charAt(this.i++)) 
							{
								case '{':   parens++; if (j == undefined) {j=i} break;
								case '\\':  i++; break;
								case '}':
								if (parens-- <= 0) {TEX.Error("Extra close brace while looking for ']' inside \\inputfield parameters")}
								if (parens == 0) {}
								break;   
								case ']':
								if (parens == 0) {return string.slice(j,i-1)}
								break;
							}
						}
					}
*/
						
					var parts = params.split(/,/), def = {EDisInputField : true, extraAttributes : {EDisInputField : 1}};
					
					for (var i = 0, m = parts.length, part = parts[0]; i < m; i++, part=parts[i])
					{
						var keys = part.split(/=/);
						switch(keys[0])
						{
							case 'id' : 
								if (!def.extraAttributes) {def.extraAttributes = {};}
								HUB.Insert(def.extraAttributes, {fieldId : keys[1]});
						}
					}
					
					if (!def.extraAttributes) {def.extraAttributes = {};} def.extraAttributes.EDisInputField=1;
					if((math.isa(MML.mrow))&&(!math.EDisContainer))
					{
						delete (math.inferred);
						math=math.With(def);
					}
					else
					{
						math = MML.mrow(math).With(def);
					}
					
					fieldList.splice(-1,1,def);
					delete (this.stack.env.inInputField);
					
					this.Push(math);
				}
  });
  
  MathJax.Hub.Startup.signal.Post("MathJax Editor Ready");
  
});

MathJax.Hub.Register.StartupHook(MathJax.Extension.Editor.config.OutputJax + " Jax Startup",function (){
	var MML = MathJax.ElementJax.mml,
		ED  = MathJax.Extension.Editor,
		JAX = MathJax.OutputJax[ED.config.OutputJax],
		HUB = MathJax.Hub;
		
	MML.mbase.Augment({
		EDisAtomic : true,
			
		EDcheckHighlighted : function ()
			{
				var parent = this;
				
				while (parent)
				{
					if(parent.EDisInputField)
					{
						return false
					}
					else
					{
						if(parent.EDisHighlighted) return true;
					}
					parent = parent.parent;
				}
				return false;
			},
		
		/********* MML tree traversal code ********************************************************/
		
		findParentMRow : function ()
			{
				var parent = this.parent;
				
				while (parent)
				{
					if ((!parent.EDisContainer)&&(parent.type == "mrow"))
					{
						return parent;
					}
					if (parent.type == "math") return null;
					
					parent = parent.parent;
				}
				return null;
			},
		
		getChildIndexes : function () //Returns array of indexes of children, if it can locate ones.
			{
				var indexes = null;
				
				if (arguments.length>0)
				{
					var item;
					
					indexes = [];
					
					if (this.data&&(this.data.length>0))
					{
						for(var i = 0, n=arguments.length;i<n;i++)
						{
							item = arguments[i];
							if(item)
							{
								for (var j=0,m=this.data.length;j<m;j++)
								{
									if (item == this.data[j])
									{
										indexes.push(j);
										break;
									}
									indexes.push(-1);
								}
							}
							else indexes.push(-1);
						}
					}
				}
				return indexes;
			},
			
		getIndex : function ()
			{
				var parent = this.parent;
				
				if (parent)
				{
					for(var i =0, m = parent.data.length; i<m;i++)
					{
						if (parent.data[i]==this)
						{
							return i;
						}
					}
				}
				
				return null;
			},
			
		getNext : function ()
			{
				var parent = this.parent;
				
				if (parent)
				{
					for(var i =0, m = parent.data.length; i<m;i++)
					{
						if (parent.data[i]==this)
						{
							if (i<m-1) return parent.data[i+1];
							break;
						}
					}
				}
				
				return null;
			},
		
		getPrevious : function ()
			{
				var parent = this.parent;
				
				if (parent)
				{
					for(var i =0, m = parent.data.length; i<m;i++)
					{
						if (parent.data[i]==this)
						{
							if (i>0) return parent.data[i-1];
							break;
						}
					}
				}
				
				return null;
			},
		
		/********* Focusing Code for navigation and clicks ****************************************/
		
		focusOutToLeftFromChild : function (child,def)
			{
				var prev = child.getPrevious();
				
				if (prev)
				{
					return prev.focusRightToLeft(this,def)
				}
				else
				{
					prev = this.getPrevious();
					if(prev)
					{
						def.toRight = this;
						return prev.focusInFromRight(this,def);
					}
					else
					{
						def.toLeft = null;
						return this.focusInFromLeft(this,def);
					}
/*					prev = this.parent;
					if (prev) {def = prev.focusOutToLeftFromChild(this,def);prev = (def.toLeft)?def.toLeft:def.toRight;def.mRow = prev.findParentMRow();}*/
				}
				
				return def
			},
		
		focusOutToRightFromChild : function (child,def)
			{
				var next = child.getNext();
				
				if (next)
				{
					return next.focusLeftToRight(this,def);
				}
				else
				{
					next = this.getNext();
					if(next)
					{
						def.toLeft = this;
						return next.focusInFromLeft(this,def);
					}
					else
					{
						def.toRight = null;
						return this.focusInFromRight(this,def);
					}
/*					next = this.parent;
					if (next) {def = next.focusOutToRightFromChild(this,def);next = (def.toLeft)?def.toLeft:def.toRight;def.mRow = next.findParentMRow();}*/
				}
				
				return def
			},
		
		focusInFromLeft : function (item, def)
			{
				var add = {
								toRight	: this,
								insertAt : this.getIndex(),
								clickSpan : document.getElementById("MathJax-Span-"+this.spanID),
								clickSide : -1
							};
				for (var id in add) {if (add.hasOwnProperty(id)) {def[id] = add[id]}}
				return def
			},
		
		focusInFromRight : function (item, def)
			{
				var add = {
								toLeft	: this,
								insertAt : this.getIndex()+1,
								clickSpan : document.getElementById("MathJax-Span-"+this.spanID),
								clickSide : 1
							};
				for (var id in add) {if (add.hasOwnProperty(id)) {def[id] = add[id]}}
				return def
			},
		
		focusOutToRight : function (item, def)
			{
				var next = this.getNext();
				
				if (next)
				{
					return next.focusLeftToRight(this,def);
				}
				else
				{
					next = this.parent;
					
					if (next) return next.focusOutToRightFromChild(this,def);
					else return def;
				}
			},
		
		focusOutToLeft : function (item, def)
			{
				var prev = this.getPrevious();
				
				if (prev)
				{
					return prev.focusRightToLeft(this,def)
				}
				else
				{
					prev = this.parent;
					
					if (prev) return prev.focusOutToLeftFromChild(this,def);
					else return def;
				}
			},
		
		focusLeftToRight : function (item, def)
			{
				var next = this.getNext();
				
				if (next)
				{
					def.toLeft = this;
					return next.focusInFromLeft(this,def);
				}
				else
				{
					var add = {
									toRight	: null,
									toLeft	: this,
									insertAt : this.getIndex()+1,
									clickSpan : document.getElementById("MathJax-Span-"+this.spanID),
									clickSide : 1
								};
					for (var id in add) {if (add.hasOwnProperty(id)) {def[id] = add[id]}}
					return def
				}
			},
		focusRightToLeft : function (item, def)
			{
				var prev = this.getPrevious();
				
				if (prev)
				{
					def.toRight = this;
					return prev.focusInFromRight(this,def)
				}
				else
				{
					var add = {
									toRight	: this,
									toLeft	: null,
									insertAt : this.getIndex(),
									clickSpan : document.getElementById("MathJax-Span-"+this.spanID),
									clickSide : -1
								};
					for (var id in add) {if (add.hasOwnProperty(id)) {def[id] = add[id]}}
					return def
				}
			},
		
		/********* Editing Code *******************************************************************/
		InsertAt : function (at,math,def)
			{
				if(math!=null)
				{
					if(!(math instanceof MML.mbase))
					{
						math = ((this.isToken)?(MML.chars(math)):(MML.mtext(math)));
					}
					math.parent = this;
					math.setInherit(this.inheritFromMe ? this : this.inherit);
				}
				if(this.data&&this.data.length)
				{
					this.data.splice(at,0,math);
				}
				else
				{
					this.data = [math];
				}
			},
			
		RemoveAt : function (at,def)
			{
				if(this.data&&this.data.length)
				{
					this.data.splice(at,0,math);
				}
				else
				{
					return
				}
			}
	});
	
	MML.mrow.Augment({
		EDisAtomic : false,
		
		/********* Focusing Code for navigation and clicks ****************************************/
		
		focusOutToLeftFromChild : function (child,def)
			{
				var prev = child.getPrevious();
				
				if (prev)
				{
					return prev.focusRightToLeft(this,def)
				}
				else
				{
					if (this.EDisInputField)
					{
					}
					else
					{
						prev = this.parent;
						if (prev) {def = prev.focusOutToLeftFromChild(this,def);prev = (def.toLeft)?def.toLeft:def.toRight;def.mRow = prev.findParentMRow();}
					}
				}
				
				return def
			},
		
		focusOutToRightFromChild : function (child,def)
			{
				var next = child.getNext();
				
				if (next)
				{
					return next.focusLeftToRight(this,def);
				}
				else
				{
					if (this.EDisInputField)
					{
					}
					else
					{
						next = this.parent;
						if (next) {def = next.focusOutToRightFromChild(this,def);next = (def.toLeft)?def.toLeft:def.toRight;def.mRow = next.findParentMRow();}
					}
				}
				
				return def
			},
		
		focusInFromLeft : function (item, def)
			{
				if((this.data)&&(this.data.length>0))
				{
					def.toLeft=null;
					return this.data[0].focusInFromLeft(this,def);;
				}
				else return def
			},
		
		focusInFromRight : function (item, def)
			{
				if((this.data)&&(this.data.length>0))
				{
					def.toRight=null;
					return this.data[this.data.length-1].focusInFromRight(this,def);;
				}
				else return def
			},
		
		focusLeftToRight : function (item, def)
			{
				if((this.data)&&(this.data.length>0))
				{
					def.toLeft=null;
					return this.data[0].focusInFromLeft(this,def);
				}
				else 
				{
					var next = this.getNext();
				
					if (next)
					{
						def.toLeft = this;
						return next.focusInFromLeft(this,def);
					}
					else
					{
						var add = {
										toRight	: null,
										toLeft	: this,
										insertAt : this.getIndex()+1,
										clickSpan : document.getElementById("MathJax-Span-"+this.spanID),
										clickSide : 1
									};
						for (var id in add) {if (add.hasOwnProperty(id)) {def[id] = add[id]}}
						return def
					}
				}
			},
			
		focusRightToLeft : function (item, def)
			{
				if((this.data)&&(this.data.length>0))
				{
					def.toRight=null;
					return this.data[this.data.length - 1].focusInFromRight(this,def);
				}
				else 
				{
					var prev = this.getPrevious();
				
					if (prev)
					{
						def.toRight = this;
						return prev.focusInFromRight(this,def)
					}
					else
					{
						var add = {
										toRight	: this,
										toLeft	: null,
										insertAt : this.getIndex(),
										clickSpan : document.getElementById("MathJax-Span-"+this.spanID),
										clickSide : -1
									};
						for (var id in add) {if (add.hasOwnProperty(id)) {def[id] = add[id]}}
						return def
					}
				}
			},
		
		/********* HTML Generation Code *******************************************************************/

		toHTML: function (span) {
				span = this.HTMLcreateSpan(span); 
				for (var i = 0, m = this.data.length; i < m; i++)
				  {if (this.data[i]) {this.data[i].toHTML(span)}}
				var stretchy = this.HTMLcomputeBBox(span);
				var h = span.bbox.h, d = span.bbox.d;
				for (i = 0, m = stretchy.length; i < m; i++) {stretchy[i].HTMLstretchV(span,h,d)}
				if (stretchy.length) {this.HTMLcomputeBBox(span,true)}
					if (this.HTMLlineBreaks(span)) {span = this.HTMLmultiline(span)}
				this.HTMLhandleSpace(span);
				this.HTMLhandleColor(span);
				if (this.extraAttributes)
				{
					this.HTMLAppendAttributes(span);
				}
				if (this.EDisInputField)
				{
					this.HTMLAttachEventListeners(span);
					span.EDisInputField=true;
				}
				return span;
			},
		HTMLAppendAttributes : function(span)
			{
			/*	var node;*/
				for (var id in this.extraAttributes)
				{
					if (this.extraAttributes.hasOwnProperty(id))
					{
/*						node = document.createAttribute(String(id));
						
						node.value = String(this.extraAttributes[id]);
						span.setAttributeNode(node);*/
						span.setAttribute(String(id),String(this.extraAttributes[id]));
					}
				}
			},
		HTMLAttachEventListeners : function(span)
			{
				span.addEventListener('click',ED.Event.Click,true);
				
				if ((this.extraAttributes)&&(this.extraAttributes.selected==true))
				{
					document.addEventListener('keydown',ED.Event.Keydown,true);
				}
			}
      });

	MML.numberrow = MML.mrow.Subclass(
		{
			focusOutToLeftFromChild : function (child,def)
				{
					var prev = child.getPrevious();
				
					if (prev)
					{
						return prev.focusRightToLeft(this,def)
					}
					else
					{
						prev = this.getPrevious();
						if(prev)
						{
							def.toRight = this;
							return prev.focusRightToLeft(this,def);
						}
						else
						{
							prev = this.parent;
							if (prev) {def = prev.focusOutToLeftFromChild(this,def);prev = (def.toLeft)?def.toLeft:def.toRight;def.mRow = prev.findParentMRow();}
						}
					}
				
					return def
				},
		
			focusOutToRightFromChild : function (child,def)
				{
					var next = child.getNext();
				
					if (next)
					{
						return next.focusLeftToRight(this,def);
					}
					else
					{
						next = this.getNext();
						if(next)
						{
							def.toLeft = this;
							return next.focusLeftToRight(this,def);
						}
						else
						{
							next = this.parent;
							if (next) {def = next.focusOutToRightFromChild(this,def);next = (def.toLeft)?def.toLeft:def.toRight;def.mRow = next.findParentMRow();}
						}
					}
				
					return def
				},
			
			focusLeftToRight : function (item, def)
				{
					if((this.data)&&(this.data.length>0))
					{
						return this.data[0].focusLeftToRight(this,def);
					}
					else 
					{
						var next = this.getNext();
				
						if (next)
						{
							def.toLeft = this;
							return next.focusInFromLeft(this,def);
						}
						else
						{
							var add = {
											toRight	: null,
											toLeft	: this,
											insertAt : this.getIndex()+1,
											clickSpan : document.getElementById("MathJax-Span-"+this.spanID),
											clickSide : 1
										};
							for (var id in add) {if (add.hasOwnProperty(id)) {def[id] = add[id]}}
							return def
						}
					}
				},
			
			focusRightToLeft : function (item, def)
				{
					if((this.data)&&(this.data.length>0))
					{
						return this.data[this.data.length - 1].focusRightToLeft(this,def);
					}
					else 
					{
						var prev = this.getPrevious();
				
						if (prev)
						{
							def.toRight = this;
							return prev.focusInFromRight(this,def)
						}
						else
						{
							var add = {
											toRight	: this,
											toLeft	: null,
											insertAt : this.getIndex(),
											clickSpan : document.getElementById("MathJax-Span-"+this.spanID),
											clickSide : -1
										};
							for (var id in add) {if (add.hasOwnProperty(id)) {def[id] = add[id]}}
							return def
						}
					}
				},
			EDisContainer	: "mn",
			numberString	: "",
			extraAttributes	: {container : "mn"}
		});
	
	MML.functionrow = MML.mrow.Subclass(
		{
			focusOutToLeftFromChild : function (child,def)
				{
					var prev = child.getPrevious();
				
					if (prev)
					{
						return prev.focusRightToLeft(this,def)
					}
					else
					{
						prev = this.getPrevious();
						if(prev)
						{
							def.toRight = this;
							return prev.focusRightToLeft(this,def);
						}
						else
						{
							prev = this.parent;
							if (prev) {def = prev.focusOutToLeftFromChild(this,def);prev = (def.toLeft)?def.toLeft:def.toRight;def.mRow = prev.findParentMRow();}
						}
					}
				
					return def
				},
		
			focusOutToRightFromChild : function (child,def)
				{
					var next = child.getNext();
				
					if (next)
					{
						return next.focusLeftToRight(this,def);
					}
					else
					{
						next = this.getNext();
						if(next)
						{
							def.toLeft = this;
							return next.focusLeftToRight(this,def);
						}
						else
						{
							next = this.parent;
							if (next) {def = next.focusOutToRightFromChild(this,def);next = (def.toLeft)?def.toLeft:def.toRight;def.mRow = next.findParentMRow();}
						}
					}
				
					return def
				},
		
			focusLeftToRight : function (item, def)
				{
					if((this.data)&&(this.data.length>0))
					{
						return this.data[0].focusLeftToRight(this,def);
					}
					else 
					{
						var next = this.getNext();
				
						if (next)
						{
							def.toLeft = this;
							return next.focusInFromLeft(this,def);
						}
						else
						{
							var add = {
											toRight	: null,
											toLeft	: this,
											insertAt : this.getIndex()+1,
											clickSpan : document.getElementById("MathJax-Span-"+this.spanID),
											clickSide : 1
										};
							for (var id in add) {if (add.hasOwnProperty(id)) {def[id] = add[id]}}
							return def
						}
					}
				},
			
			focusRightToLeft : function (item, def)
				{
					if((this.data)&&(this.data.length>0))
					{
						return this.data[this.data.length - 1].focusRightToLeft(this,def);
					}
					else 
					{
						var prev = this.getPrevious();
				
						if (prev)
						{
							def.toRight = this;
							return prev.focusInFromRight(this,def)
						}
						else
						{
							var add = {
											toRight	: this,
											toLeft	: null,
											insertAt : this.getIndex(),
											clickSpan : document.getElementById("MathJax-Span-"+this.spanID),
											clickSide : -1
										};
							for (var id in add) {if (add.hasOwnProperty(id)) {def[id] = add[id]}}
							return def
						}
					}
				},
			Append: function () {//this function is overriden so that the letters will be automatically added with the MML.VARIANT.NORMAL style
				for (var i = 0, m = arguments.length; i < m; i++)
				  {this.SetData(this.data.length,arguments[i].With(this.defaults))}
			},
			texClass: MML.TEXCLASS.ORD,
			fName	: "",
			defaults: 
				{
					mathvariant: MML.VARIANT.NORMAL,
				},
			EDisContainer : "fn",
			extraAttributes : {container : "fn"}
		});
	
	MML.mfrac.Augment(
		{
			focusOutToLeftFromChild : function (child,def)
				{
					if (child == this.data[this.den])
					{
						return this.data[this.num].focusRightToLeft(this,def)
					}
					else
					{
						var prev = this.getPrevious();
						if(prev)
						{
							def.toRight = this;
							return prev.focusInFromRight(this,def);
						}
						else
						{
							def.toLeft = null;
							return this.focusInFromLeft(this,def);
						}
					}
				
					return def
				},
		
			focusOutToRightFromChild : function (child,def)
				{
					if (child == this.data[this.num])
					{
						return this.data[this.den].focusLeftToRight(this,def)
					}
					else
					{
						var next = this.getNext();
						if(next)
						{
							def.toLeft = this;
							return next.focusInFromLeft(this,def);
						}
						else
						{
							def.toRight = null;
							return this.focusInFromRight(this,def);
						}
	/*					next = this.parent;
						if (next) {def = next.focusOutToRightFromChild(this,def);next = (def.toLeft)?def.toLeft:def.toRight;def.mRow = next.findParentMRow();}*/
					}
				
					return def
				},
		
			focusLeftToRight : function (item, def)
				{
					var next;
					def = this.data[this.num].focusLeftToRight(this,def);
					next = (def.toRight)?def.toRight:def.toLeft;def.mRow = next.findParentMRow();
					return def;
				},
			focusRightToLeft : function (item, def)
				{
					var prev;
					def = this.data[this.den].focusRightToLeft(this,def);
					prev = (def.toLeft)?def.toLeft:def.toRight;def.mRow = prev.findParentMRow();
					return def;
				}
		});
	
	MML.msubsup.Augment(
		{
			focusOutToLeftFromChild : function (child,def)
				{
					if (child == this.data[this.sup])
					{
						if(this.data[this.sub])
						{
							return this.data[this.sub].focusRightToLeft(this,def)
						}
						else
						{
							return this.data[this.base].focusRightToLeft(this,def)
						}
					}
					else if (child == this.data[this.sub])
					{
						return this.data[this.base].focusRightToLeft(this,def)
					}
					else
					{
						var prev = this.getPrevious();
						if(prev)
						{
							def.toRight = this;
							return prev.focusInFromRight(this,def);
						}
						else
						{
							def.toLeft = null;
							return this.focusInFromLeft(this,def);
						}
					}
				
					return def
				},
		
			focusOutToRightFromChild : function (child,def)
				{
					if (child == this.data[this.base])
					{
						if(this.data[this.sub])
						{
							return this.data[this.sub].focusLeftToRight(this,def)
						}
						else if(this.data[this.sup])
						{
							return this.data[this.sup].focusLeftToRight(this,def)
						}
					}
					else if (child == this.data[this.sub])
					{
						if(this.data[this.sup])
						{
							return this.data[this.sup].focusLeftToRight(this,def)
						}
					}
					var next = this.getNext();
					if(next)
					{
						def.toLeft = this;
						return next.focusInFromLeft(this,def);
					}
					else
					{
						def.toRight = null;
						return this.focusInFromRight(this,def);
					}
/*					next = this.parent;
					if (next) {def = next.focusOutToRightFromChild(this,def);next = (def.toLeft)?def.toLeft:def.toRight;def.mRow = next.findParentMRow();}*/
					return def
				},
		
			focusLeftToRight : function (item, def)
				{
					var next;
					def = this.data[this.base].focusLeftToRight(this,def);
					next = (def.toRight)?def.toRight:def.toLeft;def.mRow = next.findParentMRow();
					return def;
				},
			focusRightToLeft : function (item, def)
				{
					var prev;
					if(this.data[this.sup]) prev = this.data[this.sup];
					else if(this.data[this.sub]) prev = this.data[this.sub];
					else prev = this.data[this.base];
					def =prev.focusRightToLeft(this,def);
					prev = (def.toLeft)?def.toLeft:def.toRight;def.mRow = prev.findParentMRow();
					return def;
				}
		});
	
});

MathJax.Ajax.loadComplete("[MathJax]/extensions/Editor/editor.js");
