function tc(obj)
{
	if(null != obj)
	{
		var id = obj.id;
		var arr = id.split("_");
		this.type = arr[0];
		this.posx = arr[2];
		this.posy = arr[1];
		if(obj.className == "select")
		{
			this.selected = true;
		}
		this.obj = obj;
		obj.t_obj=this;
	}
}
new tc(null);
tc.prototype.type= null;
tc.prototype.posx=-1;
tc.prototype.posy=-1;
tc.prototype.selected = false;
tc.prototype.obj = null;
tc.prototype.select = tc_select;
tc.prototype.unselect = tc_unselect;
tc.prototype.change = tc_change;
tc.prototype.rangecell=tc_rangecell;
tc.prototype.rangetopleft=tc_rangetopleft;
tc.prototype.rangetopright=tc_rangetopright;
tc.prototype.rangebottomleft=tc_rangebottomleft;
tc.prototype.rangebottomright=tc_rangebottomright;
tc.prototype.rangetop=tc_rangetop;
tc.prototype.rangebottom=tc_rangebottom;
tc.prototype.rangeleft=tc_rangeleft;
tc.prototype.rangeright=tc_rangeright;
tc.prototype.rangeinner=tc_rangeinner;
tc.prototype.rangeouter=tc_rangeouter;
tc.prototype.rangetopleftbottom=tc_rangetopleftbottom;
tc.prototype.rangetopbottomright=tc_rangetopbottomright;
tc.prototype.rangetopbottom=tc_rangetopbottom;
tc.prototype.rangetopleftright=tc_rangetopleftright;
tc.prototype.rangeleftbottomright=tc_rangeleftbottomright;
tc.prototype.rangeleftright=tc_rangeleftright;
tc.prototype.setDisable=tc_setDisable;
tc.prototype.setEnable=tc_setEnable;

function tc_select()
{
	this.selected = true;
	//this.obj.className="select";
	this.obj.style.background="#5d9cff";
}
function tc_unselect()
{
	this.selected = false;
	//this.obj.className="";
	this.obj.style.background="#FFFFFF";
}
function tc_change()
{
	if(this.selected)
	{
		this.unselect();
	}
	else
	{
		this.select();
	}
}

function tc_rangecell()
{
	this.obj.style.backgroundImage="url(images/ts/selcell.gif)";
}
function tc_rangetopleft()
{
	this.obj.style.backgroundImage="url(images/ts/seltopleft.gif)";
}
function tc_rangetopright()
{
	this.obj.style.backgroundImage="url(images/ts/seltopright.gif)";
}
function tc_rangebottomleft()
{
	this.obj.style.backgroundImage="url(images/ts/selbottomleft.gif)";
}
function tc_rangebottomright()
{
	this.obj.style.backgroundImage="url(images/ts/selbottomright.gif)";
}
function tc_rangetop()
{
	this.obj.style.backgroundImage="url(images/ts/seltop.gif)";
}
function tc_rangebottom()
{
	this.obj.style.backgroundImage="url(images/ts/selbottom.gif)";
}
function tc_rangeleft()
{
	this.obj.style.backgroundImage="url(images/ts/selleft.gif)";
}
function tc_rangeright()
{
	this.obj.style.backgroundImage="url(images/ts/selright.gif)";
}
function tc_rangetopleftbottom()
{
	this.obj.style.backgroundImage="url(images/ts/seltopleftbottom.gif)";
}
function tc_rangetopbottomright()
{
	this.obj.style.backgroundImage="url(images/ts/seltopbottomright.gif)";
}
function tc_rangetopbottom()
{
	this.obj.style.backgroundImage="url(images/ts/seltopbottom.gif)";
}
function tc_rangetopleftright()
{
	this.obj.style.backgroundImage="url(images/ts/seltopleftright.gif)";
}
function tc_rangeleftbottomright()
{
	this.obj.style.backgroundImage="url(images/ts/selleftbottomright.gif)";
}
function tc_rangeleftright()
{
	this.obj.style.backgroundImage="url(images/ts/selleftright.gif)";
}

function tc_rangeinner()
{
	this.obj.style.backgroundImage="url(images/ts/selnull.gif)";
}
function tc_rangeouter()
{
	this.obj.style.backgroundImage="url(images/ts/selnull.gif)";
}

/******************************************************************************/
function range(start, end)
{
	if(null == start || null == end)
	{
		return;
	}
	
	if(parseInt(start.posx) == -1)
	{
		this.left = 0;
		this.right = 47;
	}
	else if(parseInt(start.posx) < parseInt(end.posx))
	{
		this.left = parseInt(start.posx);
		this.right = parseInt(end.posx);
	}
	else
	{
		this.left = parseInt(end.posx);
		this.right = parseInt(start.posx);
	}

	if(parseInt(start.posy) == -1)
	{
		this.top = 0;
		this.bottom = 6;

		if(parseInt(start.posx) != -1)
		{
			if(parseInt(start.posx) < parseInt(end.posx))
			{
				this.left = parseInt(start.posx)*2;
				this.right = parseInt(end.posx)*2+1;
			}
			else
			{
				this.left = parseInt(end.posx)*2;
				this.right = parseInt(start.posx)*2+1;
			}
		}
	}
	else if(parseInt(start.posy) < parseInt(end.posy))
	{
		this.top = parseInt(start.posy);
		this.bottom = parseInt(end.posy);
	}
	else
	{
		this.top = parseInt(end.posy);
		this.bottom = parseInt(start.posy);
	}
	
	/*document.title="top: "+this.top+" "+
					"left: "+this.left+" "+
					"bottom: "+this.bottom+" "+
					"right: "+this.right
					;*/
}

function tc_setDisable()
{
	if(parseInt(this.posx)==-1)
	{
		this.obj.disabled=true;
	}
	else if(parseInt(this.posy)==-1)
	{
		this.obj.disabled=true;
	}
	else if(this.selected)
	{
		this.obj.style.background="d7d5ce";  //"#aca899";
	}
}

function tc_setEnable()
{
	if(parseInt(this.posx)==-1)
	{
		this.obj.disabled=false;
	}
	else if(parseInt(this.posy)==-1)
	{
		this.obj.disabled=false;
	}
	else if(this.selected)
	{
		this.select();
	}
}
var rangecount=0;
new range(null, null);
range.prototype.top = 0;
range.prototype.left = 0;
range.prototype.bottom = 0;
range.prototype.right = 0;

/******************************************************************************/

function sheet_gettc(obj)
{
	return obj.t_obj;
}
function sheet_down()
{
	if(t_disabled)
	{
		return;
	}
	
	if(t_mouse_down==true)
	{
		sheet_select_showrange_clear();
		sheet_select_showrange_stop();
		t_mouse_down = false;
		return;
	}
	
	t_mouse_down=true;
	
	var tc = sheet_gettc(this);
	t_select_type=tc.type;
	
	sheet_select_start(tc);
}
function sheet_move()
{
	var tc = sheet_gettc(this);
	if(t_mouse_down==true && t_select_type == tc.type)
	{
		sheet_select_move(tc);
	}
}
function sheet_out()
{
}
function sheet_over()
{
}
function sheet_up()
{
	var tc = null;
	if(this.tagName == "TABLE")
	{
		tc = t_select_end;
	}
	else
	{
		tc = sheet_gettc(this);
	}
	
	if(t_mouse_down==true && t_select_type==tc.type)
	{
		sheet_select_end(tc);
		t_mouse_down=false;
		t_select_type = null;
	}
}

function sheet_initobj(obj)
{
	obj.onmousedown=sheet_down;
	obj.onmousemove=sheet_move;
	//obj.onmouseout=sheet_out;
	//obj.onmouseover=sheet_over;
	obj.onmouseup=sheet_up;
	
	var cell = new tc(obj);
	
	return cell;
}
var t_oTopLeft = null;
var t_oTops = [];
var t_oLefts = [];
var t_oCells = [];

var t_oTimeSheet = null;
function sheet_init()
{
	t_oTimeSheet = getObj(t_oTimeSheetName);
	t_oTimeSheet.onmouseup=sheet_up;
	
	var oTd = getObj("topleft_-1_-1");
	var oTc = sheet_initobj(oTd);
	t_oTopLeft = oTc;
	
	for(var y=0;y<7;y++)
	{
		var oTd = getObj("left_"+y+"_-1");
		oTc = sheet_initobj(oTd);
		t_oLefts[y] = oTc;
	}
	for(var x=0;x<24;x++)
	{
		var oTd = getObj("top_-1_"+x);
		oTc = sheet_initobj(oTd);
		t_oTops[x] = oTc;
	}
	for(var y=0;y<7;y++)
	{
		t_oCells[y] = [];
		for(var x=0;x<48;x++)
		{
			var oTd = getObj("cell_"+y+"_"+x);
			oTc = sheet_initobj(oTd);
			t_oCells[y][x] = oTc;
		}
	}
	
	sheet_initvalue();
}

var t_mouse_down = false;
var t_select_type = null;
var t_select_start = null;
var t_select_end = null;
var t_select_oldend = null;

function sheet_select_start(tc)
{
	t_select_start = tc;
	sheet_select_showrange(tc);
}
function sheet_select_move(tc)
{
	if(tc != t_select_end)
	{
		sheet_select_showrange(tc);
	}
}
function sheet_select_end(tc)
{
	sheet_select_showrange(null);
}

var nTimer = null;
function timer_run(str)
{
	if(null != nTimer)
	{
		clearTimeout(nTimer);
	}
	nTimer = setTimeout(str+"nTimer=null;", 10);
}
function timer_clear()
{
	clearTimeout(nTimer);
	nTimer = null;
}
function sheet_select_showrange(newend)
{
	if(null == newend)
	{
		timer_clear();
		sheet_select_showrange_clear();
		sheet_select_showrange_stop();
	}
	else
	{
		t_select_end = newend;
		timer_run("sheet_select_showrange_show();");
	}
}

function sheet_select_showrange_clear()
{
	if(t_select_oldend == null)
	{
		return;
	}
	var newrange = new range(t_select_start, t_select_oldend);
	t_select_oldend = null;
		
	if(newrange.top == newrange.bottom)
	{
		if(newrange.left == newrange.right)
		{
			t_oCells[newrange.top][newrange.left].rangeouter();
		}
		else
		{
			t_oCells[newrange.top][newrange.left].rangeouter();
			t_oCells[newrange.top][newrange.right].rangeouter();
			
			for(var x= newrange.left+1; x<= newrange.right-1; x++)
			{
				t_oCells[newrange.top][x].rangeouter();
			}
		}
	}
	else
	{
		if(newrange.left == newrange.right)
		{
			t_oCells[newrange.top][newrange.left].rangeouter();
			t_oCells[newrange.bottom][newrange.left].rangeouter();
			
			for(var y= newrange.top+1; y<= newrange.bottom-1; y++)
			{
				t_oCells[y][newrange.left].rangeouter();
			}
		}
		else
		{
			t_oCells[newrange.top][newrange.left].rangeouter();
			t_oCells[newrange.top][newrange.right].rangeouter();
			t_oCells[newrange.bottom][newrange.left].rangeouter();
			t_oCells[newrange.bottom][newrange.right].rangeouter();
			
			for(var x=newrange.left+1; x<= newrange.right-1; x++)
			{
				t_oCells[newrange.top][x].rangeouter();
				t_oCells[newrange.bottom][x].rangeouter();
			}

			for(var y=newrange.top+1; y<= newrange.bottom-1; y++)
			{
				t_oCells[y][newrange.left].rangeouter();
				t_oCells[y][newrange.right].rangeouter();
			}
		}
	}		
}
function sheet_select_showrange_show()
{
	if(t_select_oldend == t_select_end)
	{
		return;
	}
	
	sheet_select_showrange_clear();
	
	t_select_oldend = t_select_end;
	
	var newrange = new range(t_select_start, t_select_end);
	
	if(newrange.top == newrange.bottom)
	{
		if(newrange.left == newrange.right)
		{
			t_oCells[newrange.top][newrange.left].rangecell();
		}
		else
		{
			t_oCells[newrange.top][newrange.left].rangetopleftbottom();
			t_oCells[newrange.top][newrange.right].rangetopbottomright();
			
			for(var x= newrange.left+1; x<= newrange.right-1; x++)
			{
				t_oCells[newrange.top][x].rangetopbottom();
			}
		}
	}
	else
	{
		if(newrange.left == newrange.right)
		{
			t_oCells[newrange.top][newrange.left].rangetopleftright();
			t_oCells[newrange.bottom][newrange.left].rangeleftbottomright();
			
			for(var y= newrange.top+1; y<= newrange.bottom-1; y++)
			{
				t_oCells[y][newrange.left].rangeleftright();
			}
		}
		else
		{
			t_oCells[newrange.top][newrange.left].rangetopleft();
			t_oCells[newrange.top][newrange.right].rangetopright();
			t_oCells[newrange.bottom][newrange.left].rangebottomleft();
			t_oCells[newrange.bottom][newrange.right].rangebottomright();
			
			for(var x=newrange.left+1; x<= newrange.right-1; x++)
			{
				t_oCells[newrange.top][x].rangetop();
				t_oCells[newrange.bottom][x].rangebottom();
			}

			for(var y=newrange.top+1; y<= newrange.bottom-1; y++)
			{
				t_oCells[y][newrange.left].rangeleft();
				t_oCells[y][newrange.right].rangeright();
			}
		}
	}		
}
function sheet_select_showrange_stop()
{
	var newrange = new range(t_select_start, t_select_end);
	for(var x=newrange.left; x<=newrange.right; x++)
	{
		for(var y=newrange.top; y<=newrange.bottom; y++)
		{
			t_oCells[y][x].change();
		}
	}
}
function sheet_initvalue()
{
	var value = getObj(t_sName).value;
	for(var i=0;i<value.length;i++)
	{
		if("1" != value.charAt(i))
		{
			t_oCells[(i-i%48)/48][i%48].select();
		}
		else if(t_oCells[(i-i%48)/48][i%48].selected)
		{
			t_oCells[(i-i%48)/48][i%48].unselect();
		}
	}
}
function sheet_submit()
{
	var value = "";
	var oValue = getObj(t_sName);
	for(var y=0; y<=6; y++)
	{
		for(var x=0; x<=47; x++)
		{
			value += t_oCells[y][x].selected? "2":"1";
		}
	}
	oValue.value = value;
	return true;
}

function sheet_dialog_show(value, align)
{
	getObj(t_sName).value = value;
	sheet_initvalue();
	
	var oDiv = getObj("hidetimerange");
	return overlib(oDiv.innerHTML, WIDTH, 580, align);
}

function sheet_dialog_hide()
{
	return nd();
}

var t_disabled=false;
function sheet_setDisable()
{
	if(t_disabled)
	{
		return;
	}
	
	t_disabled = true;

	t_oTopLeft.setDisable();
	
	for(var top=0; top<=23; top++)
	{
		t_oTops[top].setDisable();
	}
	for(var left=0; left<=6; left++)
	{
		t_oLefts[left].setDisable();
	}
	
	for(var y=0; y<=6; y++)
	{
		for(var x=0; x<=47; x++)
		{
			t_oCells[y][x].setDisable();
		}
	}
}

function sheet_setEnable()
{
	if(!t_disabled)
	{
		return;
	}

	t_disabled = false;

	t_oTopLeft.setEnable();
	
	for(var top=0; top<=23; top++)
	{
		t_oTops[top].setEnable();
	}
	for(var left=0; left<=6; left++)
	{
		t_oLefts[left].setEnable();
	}

	for(var y=0; y<=6; y++)
	{
		for(var x=0; x<=47; x++)
		{
			t_oCells[y][x].setEnable();
		}
	}
}
