function zXmlWork()
{
	this._zXmlHttp=zXmlHttp.createRequest();
}
zXmlWork.prototype.method="get";
zXmlWork.prototype.asyn=true;   //not support asyn=false
zXmlWork.prototype.data=null;
zXmlWork.prototype.userdata=null;

zXmlWork.prototype._zXmlHttp=null; //private

zXmlWork.prototype.onwork=false;
zXmlWork.prototype.cbfunc = null;

var _g_qarray = [];

function zXmlQueue(active)
{
	this.active = active;
	this.queue = [];
	
	_g_qarray[_g_qarray.length] = this;
}
var g_queue = new zXmlQueue(1); //will visit by zXmlQueue_cbfunc

zXmlQueue.prototype.active=1; //max active work
zXmlQueue.prototype.queue = [];
zXmlQueue.prototype._active = 0; //curent active work
zXmlQueue.prototype.add = zXmlQueue_add;
zXmlQueue.prototype.dowork = zXmlQueue_dowork;

function zXmlQueue_add(url, cbfunc, userdata)
{
	var work = new zXmlWork();
	work.url = url;
	work.cbfunc = cbfunc;
	work.userdata = userdata;
	
	this.queue[this.queue.length] = work;
	
	this.dowork();
}

function zXmlQueue_cbfunc()
{
	for(var i=0;i<_g_qarray.length;i++)
	{
		_g_qarray[i].dowork();
	}
}

function zXmlQueue_dowork()
{
	//check if some work finish
	for(var i=0 ; i < this.queue.length ;)
	{
		var work = this.queue[i];
		if(work.onwork)
		{
			if(work._zXmlHttp.readyState == 4) //ok
			{
				work.cbfunc(work._zXmlHttp, work.userdata);
				this._active-=1;
				this.queue.splice(i, 1); //delete the work
				continue;
			}
		}
		i+=1;
	}
	
	if(this._active >= this.active)
	{
		return;
	}
	
	//check if some work can start
	for(var i=0 ; i < this.queue.length ;i+=1)
	{
		var work = this.queue[i];
		if(work.onwork == false)
		{
			work.onwork = true;
			this._active+=1;
			
			work._zXmlHttp.open(work.method,
				work.url,
				work.asyn);
			work._zXmlHttp.onreadystatechange=zXmlQueue_cbfunc;
			work._zXmlHttp.send(work.data);
			
			if(this._active >= this.active)
			{
				break;
			}
		}
	}
}