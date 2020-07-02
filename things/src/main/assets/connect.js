//页面打开自动连接服务器
var id = window.location.search;  //获取路径中的id
//alert(id);
if(id.length > 4){
	Vm.token = id.substr(4);
} else {
	Vm.token = '';
}

//页面打开自动连接服务器
setInterval(function(){
	if(!Vm.connected){
		Vm.autoWsConnect();
		console.log('连接服务器' + Vm.connected);
	}
	if(Vm.token === ''){
		window.location.href='login.html'
	}
}, 500);	

//验证Token
setTimeout(function(){
	if(Vm.connected){
		Vm.sendData("Token." + Vm.token)
	}
}, 800);
